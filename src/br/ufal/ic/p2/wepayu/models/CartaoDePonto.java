package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;

public class CartaoDePonto {

    String salario_bruto;

    public CartaoDePonto(String sala_bruto){
        this.salario_bruto = sala_bruto;
    }


    public String getSalario_bruto() {
        return salario_bruto;
    }


    public void setSalario_bruto(String salario_bruto) {
        this.salario_bruto = salario_bruto;
    }
}
