package br.ufal.ic.p2.wepayu.services;


//import br.ufal.ic.p2.wepayu.System;
import br.ufal.ic.p2.wepayu.models.*;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.time.DayOfWeek;

public class CalculoFolha {
    public  String puxaFolha(LocalDate data, Map<String, Empregado> empregados, Map<LocalDate, FolhaDePonto> folhaDePontos){
        Double salario_bruto = 0.0;
        String salario_bruto_string;

        FolhaDePonto folhaDePonto = new FolhaDePonto();

        for (Map.Entry<String, Empregado> entry : empregados.entrySet()) {
            Empregado empregado = entry.getValue();
            salario_bruto += puxaFolhaEmpregado(data, empregado);
        }

        salario_bruto_string = transformaSalarioBruto(salario_bruto);


        folhaDePonto.setSalarioBrutoTotal(salario_bruto_string);
        folhaDePontos.put(data, folhaDePonto);

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


    public  Double puxaFolhaEmpregado(LocalDate data, Empregado empregado){
        Double salario_bruto = 0.0;


        if(empregado.getTipo().equals("horista")){
            salario_bruto += puxaFolhaEmpregadoHorista(data,empregado);
        } else if(empregado.getTipo().equals("comissionado")){
            salario_bruto += puxaFolhaEmpregadoComissionado(data,empregado);
        } else if(empregado.getTipo().equals("assalariado")){
            salario_bruto += puxaFolhaEmpregadoAssalariado(data, empregado);
        }

        return salario_bruto;
    }

    private Double puxaFolhaEmpregadoAssalariado(LocalDate data, Empregado empregado) {
        Double salario_bruto = 0.0;

        if(data.plusDays(1).getDayOfMonth() == 1) {
            salario_bruto += Double.parseDouble(empregado.getSalario().replaceAll(",", "."));
        }

        return salario_bruto;
    }

    public  Double puxaFolhaEmpregadoHorista(LocalDate data, Empregado empregado){
        Double salario_bruto = 0.0;
        Map<LocalDate, Double> cartoesPonto = empregado.getCartoesPonto();
        Double horas_normais = 0.0, horas_extras = 0.0, horas_totais, salario, horas_atuais, descontos = 0.0;

        LocalDate primeiro_contrato = puxaPrimeiroContratoHorista(cartoesPonto, data);

        System.out.println("sindicato do omi: " + empregado.getNome() + " " + empregado.getSindicalizado().getValor());


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

//                System.out.println("data: " + dataAtual + " teste: " + empregado.getCartoesPonto().get(dataAtual) + "horas_normais = " + horas_normais + " horas_extras = " + horas_extras);
            }

//            if(empregado.getSindicalizado().getValor() == Boolean.TRUE){
//                descontos += Double.parseDouble(empregado.getSindicalizado().getTaxaSindical());
//
////                for(int i = 0; i < empregado.getSindicalizado().getLancaServico().size(); i++){
////                    descontos +=  Double.parseDouble(empregado.getSindicalizado().getLancaServico().get(i).getValor());
////                }
//
//            }

        }

        Sindicato sindicato = empregado.getSindicalizado();

//        if(data.getDayOfWeek() == DayOfWeek.FRIDAY){
//            descontos += Double.parseDouble(sindicato.lancaServico(data.minusDays(7), data));
//        }




        String horas_normais_str =   horas_normais.toString().replaceAll("\\.0","");
        String horas_extras_str = horas_extras.toString().replaceAll("\\.0","");


        salario = Double.parseDouble(empregado.getSalario().replaceAll(",","."));
        salario_bruto += (horas_normais  * salario) + (horas_extras * (salario * 1.5));

        String salario_bruto_str = transformaSalarioBruto(salario_bruto);


//        Horista horista = new Horista(empregado.getNome(), horas_normais_str , horas_extras_str, salario_bruto_str, , (salario_bruto), empregado.getPagamento().getMetodoDePagamento() );


        System.out.println("empregado: " + empregado.getNome() + " horas: " + (horas_normais + horas_extras) +  " descontos: " + descontos + " da data: " + data.toString());

        return salario_bruto;
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

    private Double puxaFolhaEmpregadoComissionado(LocalDate data, Empregado empregado){
        Double salario_bruto = 0.0, salario;

        if(verificaDataPagamentoComissionado(data) == Boolean.TRUE){
            Double fixo = ((Double.parseDouble(empregado.getSalario().replaceAll(",", ".")) * 12) / 52 ) * 2;
            Double vendas = 0.0;

            vendas = getPagamentoVendas(data,empregado);

            Double comissao = Double.parseDouble(empregado.getComissao().replaceAll(",",".")) * vendas;


            fixo = transformaValores(fixo);
            comissao = transformaValores(comissao);

            salario_bruto += comissao + fixo;

//            System.out.println("empregado: " + empregado.getNome() + " fixo: " + fixo + " salario: " + empregado.getSalario() + " vendas;  " + vendas + " Comissao: " + comissao );
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
