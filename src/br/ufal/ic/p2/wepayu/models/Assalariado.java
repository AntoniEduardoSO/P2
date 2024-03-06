package br.ufal.ic.p2.wepayu.models;

public class Assalariado {
    String nome;
    String salarioBruto;
    String descontos;
    String salarioLiquido;
    String metodo;


    public Assalariado(String nome, String salarioBruto, String descontos, String salarioLiquido, String metodo){
        this.nome = nome;
        this.salarioBruto = salarioBruto;
        this.descontos = descontos;
        this.salarioLiquido = salarioLiquido;
        this.metodo = metodo;
    }

    public String getSalarioBruto() {
        return salarioBruto;
    }

    public String getSalarioLiquido() {
        return salarioLiquido;
    }

    public String getNome() {
        return nome;
    }

    public String getDescontos() {
        return descontos;
    }

    public String getMetodo() {
        return metodo;
    }
}



/*
===============================================================================================================================
===================== ASSALARIADOS ============================================================================================
===============================================================================================================================
Nome                                             Salario Bruto Descontos Salario Liquido Metodo
================================================ ============= ========= =============== ======================================

TOTAL ASSALARIADOS                                        0,00      0,00            0,00
 */