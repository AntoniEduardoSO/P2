package br.ufal.ic.p2.wepayu.services;


//import br.ufal.ic.p2.wepayu.System;
import br.ufal.ic.p2.wepayu.models.*;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.time.DayOfWeek;

public class CalculoFolha {
    public  String puxaFolha(LocalDate data, Map<String, Empregado> empregados, Map<LocalDate, FolhaDePonto> folhaDePontos){
        List<Horista> horistaList = new LinkedList<>();
        Double salario_bruto = 0.0, salarioBrutoHorista = 0.0, salarioBrutoAssalariado = 0.0, salarioBrutoComissionado = 0.0;
        String salario_bruto_string;

        FolhaDePonto folhaDePonto = new FolhaDePonto();
        folhaDePontos.put(data, folhaDePonto);

        for (Map.Entry<String, Empregado> entry : empregados.entrySet()) {
            Empregado empregado = entry.getValue();
            if(empregado.getTipo().equals("horista")){
                salarioBrutoHorista += puxaFolhaEmpregadoHorista(data,empregado,folhaDePontos,salarioBrutoHorista, horistaList);
            } else if(empregado.getTipo().equals("assalariado")){
                salarioBrutoAssalariado += puxaFolhaEmpregadoAssalariado(data,empregado);
            } else if(empregado.getTipo().equals("comissionado")){
                salarioBrutoComissionado += puxaFolhaEmpregadoComissionado(data, empregado);
            }
//            salario_bruto += puxaFolhaEmpregado(data, empregado, folhaDePontos, salarioBrutoHorista);
        }

        salario_bruto_string = transformaSalarioBruto(salario_bruto + salarioBrutoHorista + salarioBrutoAssalariado + salarioBrutoComissionado);


        folhaDePonto.setSalarioBrutoTotal(salario_bruto_string);

        folhaDePontos.get(data).setSalarioBrutoHorista(salarioBrutoHorista);
        folhaDePontos.get(data).setSalarioBrutoAssalariado(salarioBrutoAssalariado);
        folhaDePontos.get(data).setSalarioBrutoComissinado(salarioBrutoComissionado);

        folhaDePontos.get(data).setHoristaList(horistaList);





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


    public  Double puxaFolhaEmpregado(LocalDate data, Empregado empregado, Map<LocalDate, FolhaDePonto> folhaDePontos, Double salarioBrutoHorista){
        Double salario_bruto = 0.0;



        if(empregado.getTipo().equals("comissionado")){
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

    public  Double puxaFolhaEmpregadoHorista(LocalDate data, Empregado empregado, Map<LocalDate, FolhaDePonto> folhaDePontos, Double salarioTotalHorista, List<Horista> horistaList){

        Double salario_bruto = 0.0;
        Map<LocalDate, Double> cartoesPonto = empregado.getCartoesPonto();
        Double horas_normais = 0.0, horas_extras = 0.0, horas_totais, salario, horas_atuais;
        Double descontos = 0.0;

        LocalDate primeiro_contrato = puxaPrimeiroContratoHorista(cartoesPonto, data);

        if(data.getDayOfWeek() == DayOfWeek.FRIDAY){
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

            salario = Double.parseDouble(empregado.getSalario().replaceAll(",","."));
            salario_bruto += (horas_normais  * salario) + (horas_extras * (salario * 1.5));


            if(empregado.getSindicalizado().getValor() ==  Boolean.TRUE){
                descontos += getDescontosSindicato(salario_bruto, empregado, data, folhaDePontos);
            }
        }

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
                if(entry.getValue().getSalarioBrutoHorista() != null){
                    if(entry.getValue().getSalarioBrutoHorista() <= 0){
                        j+=1;
                    }
                }
            }

            System.out.println(j);

            if(j == 1){
                j*=7;
            }

            descontos += ((Double.parseDouble(sindicato.getTaxaSindical().replaceAll(",", ".")) * (j) ) );
            descontos += Double.parseDouble(sindicato.lancaServico(data.minusDays(6), data).replaceAll(",", "."));
        }

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
