package br.ufal.ic.p2.wepayu.models;

import  br.ufal.ic.p2.wepayu.models.Servico;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Sindicato {
    String id;
    boolean valor;
    String taxaSindical;
    private final List<Servico> listaServico = new ArrayList<>();

    public Sindicato(String id,  String taxaSindical){
        this.valor = Boolean.TRUE;
        this.id = id;
        this.taxaSindical = taxaSindical;
    }

    public Sindicato() {

    }


    public List<Servico> getLancaServico(){
        return this.listaServico;
    }

    public String lancaServico(LocalDate dataInicial, LocalDate dataFinal) {
        double totalVendasDia = 0;
        for (LocalDate data = dataInicial; !data.isEqual(dataFinal); data = data.plusDays(1)) {
            for (Servico servico : this.listaServico) {
                if (servico.getData().isEqual(data)) {
                    totalVendasDia += Double.parseDouble(servico.getValor());
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

    public void setListaServico(Servico servico){
        this.listaServico.add(servico);
    }
    public String getId(){
        return this.id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getTaxaSindical() {
        return taxaSindical;
    }
}
