package br.ufal.ic.p2.wepayu.models;

public class Comissionado {
    String nome;
    String fixo;
    String vendas;
    String comissao;
    String salarioBruto;
    String descontos;
    String salarioLiquido;
    String metodo;

    public Comissionado(String nome, String fixo, String vendas, String comissao, String salarioBruto, String descontos, String salarioLiquido, String metodo){
        this.nome = nome;
        this.fixo = fixo;
        this.vendas = vendas;
        this.comissao = comissao;
        this.salarioBruto = salarioBruto;
        this.descontos = descontos;
        this.salarioLiquido = salarioLiquido;
        this.metodo = metodo;
    }

    public String getNome() {
        return nome;
    }

    public String getFixo() {
        return fixo;
    }

    public String getVendas() {
        return vendas;
    }

    public String getComissao() {
        return comissao;
    }

    public String getSalarioBruto() {
        return salarioBruto;
    }

    public String getMetodo() {
        return metodo;
    }

    public String getDescontos() {
        return descontos;
    }

    public String getSalarioLiquido() {
        return salarioLiquido;
    }
}

/*
===============================================================================================================================
===================== COMISSIONADOS ===========================================================================================
===============================================================================================================================
Nome                  Fixo     Vendas   Comissao Salario Bruto Descontos Salario Liquido Metodo
===================== ======== ======== ======== ============= ========= =============== ======================================

TOTAL COMISSIONADOS       0,00     0,00     0,00          0,00      0,00            0,00

 */