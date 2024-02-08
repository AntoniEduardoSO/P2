package br.ufal.ic.p2.wepayu.models;

import  br.ufal.ic.p2.wepayu.models.Servico;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Sindicato {
    String id;
    boolean valor;
    private final List<Servico> listaServico = new ArrayList<>();


    public List<Servico> getLancaServico(){
        return this.listaServico;
    }

    public String lancaServico(LocalDate dataInicial, LocalDate dataFinal) {
        double totalVendasDia = 0;
        for (LocalDate data = dataInicial; !data.isEqual(dataFinal); data = data.plusDays(1)) {
            for (Servico servico : listaServico) {
                if (servico.getData().isEqual(data)) {
                    totalVendasDia += servico.getValor();
                }
            }
        }

        String totalVendasDiaFormatadas = (totalVendasDia == (int) totalVendasDia) ? String.format("%.0f", totalVendasDia) : String.valueOf(totalVendasDia);

        if (totalVendasDiaFormatadas.contains(".")) {
            if(totalVendasDiaFormatadas.matches(".*\\d.*")){
                System.out.println("existi aqui");
                totalVendasDiaFormatadas += "0";
            }
        }

        else{
            totalVendasDiaFormatadas+=",00";
        }


        totalVendasDiaFormatadas = totalVendasDiaFormatadas.replace('.', ',');

        return totalVendasDiaFormatadas;
    }

    public boolean getValor(){
        return this.valor;
    }

    public void setValor(boolean valor) {
        this.valor = valor;
    }

    public String getId(){
        return this.id;
    }

    public void setId(String id){
        this.id = id;
    }


}
