package br.ufal.ic.p2.wepayu.services;



import br.ufal.ic.p2.wepayu.models.*;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.WeekFields;
import java.util.*;
import java.time.DayOfWeek;

public class CalculoFolha {
    public  String puxaFolha(LocalDate data, Map<String, Empregado> empregados, Map<LocalDate, FolhaDePonto> folhaDePontos){
        List<Horista> horistaList = new LinkedList<>();
        List<Assalariado> assalariadoList = new LinkedList<>();
        List<Comissionado> comissionadoList = new LinkedList<>();
        Double salario_bruto = 0.0, salarioBrutoHorista = 0.0, salarioBrutoAssalariado = 0.0, salarioBrutoComissionado = 0.0;
        String salario_bruto_string;

        FolhaDePonto folhaDePonto = new FolhaDePonto();
        folhaDePontos.put(data, folhaDePonto);

        for (Map.Entry<String, Empregado> entry : empregados.entrySet()) {
            Empregado empregado = entry.getValue();
            if(empregado.getTipo().equals("horista")){
                salarioBrutoHorista += puxaFolhaEmpregadoHorista(data,empregado,folhaDePontos,salarioBrutoHorista, horistaList);
            } else if(empregado.getTipo().equals("assalariado")){
                salarioBrutoAssalariado += puxaFolhaEmpregadoAssalariado(data,empregado, assalariadoList,LocalDate.of(2005, 1, 1));
            } else if(empregado.getTipo().equals("comissionado")){
                salarioBrutoComissionado += puxaFolhaEmpregadoComissionado(data, empregado, comissionadoList);
            }
        }

        salario_bruto_string = transformaSalarioBruto(salario_bruto + salarioBrutoHorista + salarioBrutoAssalariado + salarioBrutoComissionado);
        folhaDePonto.setSalarioBrutoTotal(salario_bruto_string);

        folhaDePontos.get(data).setSalarioBrutoHorista(salarioBrutoHorista);
        folhaDePontos.get(data).setSalarioBrutoAssalariado(salarioBrutoAssalariado);
        folhaDePontos.get(data).setSalarioBrutoComissinado(salarioBrutoComissionado);

        folhaDePontos.get(data).setHoristaList(horistaList);
        folhaDePontos.get(data).setAssalariadoList(assalariadoList);
        folhaDePontos.get(data).setComissionadoList(comissionadoList);

        return salario_bruto_string;
    }

    private String transformaSalarioBruto(Double salarioBruto) {
        String salario_bruto_string = String.format("%.2f", salarioBruto);


        salario_bruto_string = salario_bruto_string.replaceAll("\\.",",");

        if (salario_bruto_string.matches(".*,[0-9]$")) {
            salario_bruto_string += "0";
        }

        return salario_bruto_string;
    }

    public  Boolean verificaDataPagamento(LocalDate data){
        if(data.getDayOfWeek() != DayOfWeek.FRIDAY){
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    private Double puxaFolhaEmpregadoAssalariado(LocalDate data, Empregado empregado, List<Assalariado> assalariadoList, LocalDate dataPrimeira) {
        Double salario_bruto = 0.0, descontos = 0.0;



        if(empregado.getPagamento().getAgendaDePagamento().contains("mensal")){
            String[] valores = empregado.getPagamento().getAgendaDePagamento().split(" ");
            if(valores[1].equals("$")){
                if(data.plusDays(1).getDayOfMonth() == 1) {
                    LocalDate ultimoDiaMesAnterior = data.minusMonths(1).withDayOfMonth(31);
                    Sindicato sindicato = empregado.getSindicalizado();

                    salario_bruto += Double.parseDouble(empregado.getSalario().replaceAll(",", "."));


                    if(empregado.getSindicalizado().getValor() == Boolean.TRUE){
                        Double taxa_sindical = Double.parseDouble(empregado.getSindicalizado().getTaxaSindical().replaceAll(",","."));

                        for(LocalDate dataAtual = data; dataAtual.get(ChronoField.MONTH_OF_YEAR) != ultimoDiaMesAnterior.get(ChronoField.MONTH_OF_YEAR) ; dataAtual = dataAtual.minusDays(1) ){
                            descontos += taxa_sindical;
                        }

                        Double servico = Double.parseDouble(sindicato.lancaServico(ultimoDiaMesAnterior, data).replaceAll(",", "."));
                        descontos+=servico;

                    }


                }

            } else {
                Integer dia = Integer.parseInt(valores[1]);

                if(data.getDayOfMonth() == dia){
                    salario_bruto += Double.parseDouble(empregado.getSalario().replaceAll(",", "."));
                }
            }
        }

        if(empregado.getPagamento().getAgendaDePagamento().contains("semanal")){
            String valores[] = empregado.getPagamento().getAgendaDePagamento().split(" ");

            if(valores.length > 2){
                Integer repeticao =  Integer.parseInt(valores[1]);
                Integer dia = Integer.parseInt(valores[2]);


                int numeroSemana = data.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());

                LocalDate dataInicio = dataPrimeira.plusWeeks(repeticao-1);
                LocalDate dataFinal = dataPrimeira.plusWeeks(repeticao);


                if( (numeroSemana - 1) % repeticao == 0 && data.getDayOfWeek().getValue() == dia){
                    salario_bruto += ( Double.parseDouble(empregado.getSalario().replaceAll(",","\\."))  * 12 * repeticao) / 52;
                }




            } else{
                Integer dia = Integer.parseInt(valores[1]);
                if(data.getDayOfWeek().getValue() == dia ){
                    salario_bruto += (Double.parseDouble( empregado.getSalario().replaceAll(",","\\.")) * 12) / 52;
                }
            }
        }

        String[] valores = salario_bruto.toString().split("\\.");
        if(valores[1].length() >=2){
            salario_bruto = Double.parseDouble(valores[0] + "." + valores[1].substring(0,2));
        } else{
            salario_bruto = Double.parseDouble(valores[0] + "." + valores[1] + "0");
        }

        Double salario_liquido = (salario_bruto - descontos);


        String salario_liquido_str = salario_liquido.toString().replaceAll("\\.",",");
        String descontos_str = descontos.toString().replaceAll("\\.", ",");
        String salario_bruto_str = salario_bruto.toString().replaceAll("\\.",",");


        if(salario_liquido_str.matches(".*,[0-9]$")){
            salario_liquido_str+=0;
        }

        if(descontos_str.matches(".*,[0-9]$")){
            descontos_str +=0;
        }

        if(salario_bruto_str.matches(".*,[0-9]$")){
            salario_bruto_str +=0;
        }

        String metodo = getMetodoPagamento(empregado);



        Assalariado assalariado = new Assalariado(empregado.getNome(), salario_bruto_str, descontos_str, salario_liquido_str, metodo);
        assalariadoList.add(assalariado);



        return salario_bruto;
    }

    public  Double puxaFolhaEmpregadoHorista(LocalDate data, Empregado empregado, Map<LocalDate, FolhaDePonto> folhaDePontos, Double salarioTotalHorista, List<Horista> horistaList){

        Double salario_bruto = 0.0;
        Map<LocalDate, Double> cartoesPonto = empregado.getCartoesPonto();
        Double horas_normais = 0.0, horas_extras = 0.0, horas_totais, salario, horas_atuais;
        Double descontos = 0.0;
        LocalDate primeiro_contrato = puxaPrimeiroContratoHorista(cartoesPonto, data);

        if(empregado.getPagamento().getAgendaDePagamento().equals("mensal $")){
            System.out.println("TO NO IF MENSAL");
            if(data.plusDays(1).getDayOfMonth() == 1){
                LocalDate data_inicial = data.minusMonths(1);
                System.out.println("data inicial: " + data_inicial + " data final: " + data);
                horas_normais += calculoDeHorasFolhaEmpregadoHoristaHorasNormais(empregado, data_inicial, data);
                horas_extras += calculoDeHorasFolhaEmpregadoHoristaHorasExtras(empregado, data_inicial, data);
            }


            System.out.println("horas normais = " + horas_normais);

        }

        else if(empregado.getPagamento().getAgendaDePagamento().equals("semanal 2 5")){
            System.out.println("TO NO IF semanal 2 5");
            if(verificaDataPagamentoComissionado(data)){
                LocalDate data_inicial = data.minusDays(14);
                horas_normais += calculoDeHorasFolhaEmpregadoHoristaHorasNormais(empregado, data_inicial, data);
                horas_extras += calculoDeHorasFolhaEmpregadoHoristaHorasExtras(empregado, data_inicial, data);
            }

            if(empregado.getSindicalizado().getValor() ==  Boolean.TRUE){
                descontos += getDescontosSindicato(salario_bruto, empregado, data, folhaDePontos);
            }
        }

        else if(data.getDayOfWeek() == DayOfWeek.FRIDAY){
            for(LocalDate dataAtual = data; dataAtual.getDayOfMonth() != 0;  dataAtual = dataAtual.minusDays(1)){
                if(dataAtual.getDayOfWeek() == DayOfWeek.FRIDAY && dataAtual != data){
                    break;
                }

                if (empregado.getCartoesPonto().containsKey(dataAtual)) {
                    horas_atuais = empregado.getCartoesPonto().get(dataAtual);

                    if(  horas_atuais > 8 ){
                        horas_extras += horas_atuais - 8;
                        horas_atuais -= horas_extras;
                    }

                    horas_normais += horas_atuais;
                }
            }

            if(empregado.getSindicalizado().getValor() ==  Boolean.TRUE){
                descontos += getDescontosSindicato(salario_bruto, empregado, data, folhaDePontos);
            }

            System.out.println("data: " + data + " descontos: " + descontos);

        }


        salario = Double.parseDouble(empregado.getSalario().replaceAll(",","."));
        salario_bruto += (horas_normais  * salario) + (horas_extras * (salario * 1.5));
        String horas_normais_str =   horas_normais.toString().replaceAll("\\.0","");
        String horas_extras_str = horas_extras.toString().replaceAll("\\.0","");
        String salario_bruto_str = transformaSalarioBruto(salario_bruto);
        String descontos_str = transformaSalarioBruto(descontos);
        String salario_liquido_str = transformaSalarioBruto(salario_bruto - descontos);
        String metodo = getMetodoPagamento(empregado);




        Horista horista = new Horista(empregado.getNome(), horas_normais_str , horas_extras_str, salario_bruto_str, descontos_str , salario_liquido_str, metodo );

        horistaList.add(horista);



        return salario_bruto;
    }

    private Double calculoDeHorasFolhaEmpregadoHoristaHorasExtras(Empregado empregado, LocalDate data_inicial, LocalDate data_final) {
        Double horas_extras = 0.0;

        for(; data_final.isAfter(data_inicial);data_final =  data_final.minusDays(1)){
            if(empregado.getCartoesPonto().containsKey(data_final)){
                Double horas_atuais = empregado.getCartoesPonto().get(data_final);

                if(horas_atuais > 8){
                    horas_extras += (horas_atuais - 8);
                }
            }
        }

        return horas_extras;
    }

    private Double calculoDeHorasFolhaEmpregadoHoristaHorasNormais(Empregado empregado, LocalDate data_inicial, LocalDate data_final){
        Double horas_normais = 0.0;


        for(; data_final.isAfter(data_inicial);data_final =  data_final.minusDays(1)){
            if(empregado.getCartoesPonto().containsKey(data_final)){
                Double horas_atuais = empregado.getCartoesPonto().get(data_final);

                if(horas_atuais > 8){
                     horas_normais += 8;
                } else{
                   horas_normais +=horas_atuais;
                }
            }
        }


        return horas_normais;
    }

    private String getMetodoPagamento(Empregado empregado) {

        String metodo = empregado.getPagamento().getMetodoDePagamento();

        if(metodo.equals("emMaos")){
            metodo = "Em maos";
        } else if(metodo.equals("banco")){
            metodo = empregado.getPagamento().getBanco() + ", Ag. " + empregado.getPagamento().getAgencia() + " CC " + empregado.getPagamento().getContaCorrente();
        } else if(metodo.equals("correios")){
            metodo = "Correios, " + empregado.getEndereco();
        }


        return metodo;
    }

    private Double getDescontosSindicato(Double salario_bruto,Empregado empregado, LocalDate data, Map<LocalDate, FolhaDePonto> folhaDePontos ) {
        Double descontos = 0.0;

        if(salario_bruto > 0) {
            Integer j = -5;
            Sindicato sindicato = empregado.getSindicalizado();




            for (Map.Entry<LocalDate, FolhaDePonto> entry : folhaDePontos.entrySet()) {
                System.out.println("entry: " + entry.getValue().getSalarioBrutoTotal());
                if(entry.getValue().getSalarioBrutoHorista() != null){
                    System.out.println("DENTRO DO IF DO FOR");
                    if(entry.getValue().getSalarioBrutoHorista() <= 0){
                        j+=1;
                    }
                }
            }

            if(j == 1){
                j*=7;
            }

            descontos += ((Double.parseDouble(sindicato.getTaxaSindical().replaceAll(",", ".")) * (j) ) );
            descontos += Double.parseDouble(sindicato.lancaServico(data.minusDays(6), data).replaceAll(",", "."));
        }

        System.out.println(descontos);

        return descontos;
    }

    private  LocalDate puxaPrimeiroContratoHorista(Map<LocalDate, Double> cartoesPonto, LocalDate filtro) {
        LocalDate dataMaisAntiga = filtro;
        LocalDate data;


        for (Map.Entry<LocalDate, Double> entry : cartoesPonto.entrySet()) {
            data = entry.getKey();
            if(data.isBefore(dataMaisAntiga)) {
                dataMaisAntiga = data;
            }
        }


        return dataMaisAntiga;
    }

    private Double puxaFolhaEmpregadoComissionado(LocalDate data, Empregado empregado, List<Comissionado> comissionadoList){
        Double salario_bruto = 0.0, salario, descontos = 0.0, taxa_servico = 0.0;

        if(empregado.getPagamento().getAgendaDePagamento().equals("mensal $")){
            if(data.plusDays(1).getDayOfMonth() == 1){
                Double fixo = Double.parseDouble(empregado.getSalario().replaceAll(",", "."));

                Double vendas = 0.0;

                List<Vendas> listaVendas = empregado.getLancaVendas();

                for(LocalDate dataAtual = data.minusMonths(1); dataAtual.isBefore(data); dataAtual = dataAtual.plusDays(1)){
                    for (Vendas venda : listaVendas) {
                        if (venda.getData().isEqual(dataAtual)) {
                            vendas += venda.getValor();
                        }
                    }
                }

                System.out.println("vendas: " + vendas);

                Double comissao = Double.parseDouble(empregado.getComissao().replaceAll(",","\\.")) * vendas;

                salario_bruto += comissao + fixo;






            }

        }

        else if(empregado.getPagamento().getAgendaDePagamento().equals("semanal 5")){
            if(data.getDayOfWeek() == DayOfWeek.FRIDAY){
                Double fixo_nao_formatado = (Double.parseDouble(empregado.getSalario().replaceAll(",","\\.")) * 12) / 52;
                String valores[] = fixo_nao_formatado.toString().replaceAll("\\.",",").split(",");

                Double fixo = Double.parseDouble(valores[0] + "." + valores[1].substring(0,2));

                List<Vendas> listaVendas = empregado.getLancaVendas();
                Double vendas = 0.0;

                for(LocalDate dataAtual = data.minusDays(7); dataAtual.isBefore(data); dataAtual = dataAtual.plusDays(1)){
                    for (Vendas venda : listaVendas) {
                        if (venda.getData().isEqual(dataAtual)) {
                            vendas += venda.getValor();
                        }
                    }
                }


                Double comissao = Double.parseDouble(empregado.getComissao().replaceAll(",","\\.")) * vendas;

                salario_bruto += comissao + fixo;
            }
        }

        else if(verificaDataPagamentoComissionado(data) == Boolean.TRUE){
            Double fixo = ((Double.parseDouble(empregado.getSalario().replaceAll(",", ".")) * 12) / 52 ) * 2;
            Double vendas = 0.0;

            vendas = getPagamentoVendas(data,empregado);

            Double comissao = Double.parseDouble(empregado.getComissao().replaceAll(",",".")) * vendas;


            fixo = transformaValores(fixo);
            comissao = transformaValores(comissao);


            salario_bruto += comissao + fixo;


            if(empregado.getSindicalizado().getValor() == Boolean.TRUE){
                LocalDate dataLimite = data.minusDays(14);
                Sindicato sindicato = empregado.getSindicalizado();
                for(LocalDate dataAtual = data; dataAtual.isAfter(dataLimite);dataAtual = dataAtual.minusDays(1) ){
                    descontos += Double.parseDouble(sindicato.getTaxaSindical().replaceAll(",","\\."));
                }

                descontos += Double.parseDouble(sindicato.lancaServico(dataLimite, data).replaceAll(",","\\."));
            }

            Double salario_liquido = salario_bruto - descontos;

            java.text.DecimalFormat df = new java.text.DecimalFormat("#.##");
            String numeroFormatadoBruto = df.format(salario_bruto).replaceAll("\\.", ",");
            String numeroFormatadoLiquido = df.format(salario_liquido).replaceAll("\\.", ",");


            String descontos_str = descontos.toString().replaceAll("\\.",",");
            String fixo_str = fixo.toString().replaceAll("\\.",",");
            String comissao_str = comissao.toString().replaceAll("\\.",",");
            String vendas_str = vendas.toString().replaceAll("\\.",",");
            String metodo = getMetodoPagamento(empregado);

            if(!numeroFormatadoBruto.contains(",")){
                numeroFormatadoBruto+=",00";
            } if(numeroFormatadoBruto.matches(".*,[0-9]$")){
                numeroFormatadoBruto+="0";
            }if(!numeroFormatadoLiquido.contains(",")){
                numeroFormatadoLiquido+=",00";
            } if(numeroFormatadoLiquido.matches(".*,[0-9]$")){
              numeroFormatadoLiquido +="0";
            } if(descontos_str.matches(".*,[0-9]$")){
                descontos_str+="0";
            } if(fixo_str.matches(".*,[0-9]$")){
                fixo_str+="0";
            } if(comissao_str.matches(".*,[0-9]$")){
                comissao_str+="0";
            } if(vendas_str.matches(".*,[0-9]$")){
                vendas_str+="0";
            }


            Comissionado comissionado = new Comissionado(empregado.getNome(), fixo_str, vendas_str, comissao_str, numeroFormatadoBruto, descontos_str, numeroFormatadoLiquido, metodo);
            comissionadoList.add(comissionado);
        }



        return salario_bruto;
    }

    private Double transformaValores(Double valor) {

        BigDecimal bd = new BigDecimal(valor);
        BigDecimal resultadoArredondado = bd.setScale(2, BigDecimal.ROUND_DOWN);

        double resultado = resultadoArredondado.doubleValue();

        return resultado;
    }

    private Double getPagamentoVendas(LocalDate data, Empregado empregado) {
        List<Vendas> listaVendas = empregado.getLancaVendas();
        Double vendas = 0.0;
        if(data.getDayOfMonth() <= 15){
            for (LocalDate dataAtual = data; dataAtual.getMonthValue() == data.getMonthValue();  dataAtual = dataAtual.minusDays(1)) {
                for (Vendas venda : listaVendas) {
                    if (venda.getData().isEqual(dataAtual)) {
                        vendas += venda.getValor();
                    }
                }
            }
        }

        else{
            for (LocalDate dataAtual = data; dataAtual.getDayOfWeek() == DayOfWeek.FRIDAY;  dataAtual = dataAtual.minusDays(1)) {
                for (Vendas venda : listaVendas) {
                    if (venda.getData().isEqual(dataAtual)) {
                        vendas += venda.getValor();
                    }
                }
            }
        }

        return vendas;
    }

    private Boolean verificaDataPagamentoComissionado(LocalDate data) {
        LocalDate primeiroDiaMes = data.withDayOfMonth(1);

        if(data.getDayOfWeek() == DayOfWeek.FRIDAY){
            int sexta_feiras = 1;
            LocalDate entry = data.minusDays(7);

            while(entry.getMonthValue() == data.getMonthValue()){
                sexta_feiras++;
                entry = entry.minusDays(7);
            }

            if(sexta_feiras % 2 == 0){
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }
}
