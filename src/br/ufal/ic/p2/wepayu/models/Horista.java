package br.ufal.ic.p2.wepayu.models;

public class Horista {
    String nome;
    String horasNormais;
    String horasExtras;
    String salarioBruto;
    String descontos;
    String salarioLiquido;
    String metodo;

    public Horista(String nome, String horasNormais, String horasExtras, String salarioBruto, String descontos, String salarioLiquido, String metodo){
        this.nome = nome;
        this.horasNormais = horasNormais;
        this.horasExtras = horasExtras;
        this.salarioBruto = salarioBruto;
        this.descontos = descontos;
        this.salarioLiquido = salarioLiquido;
        this.metodo = metodo;
    }

    public String getNome() {
        return nome;
    }

    public String getHoras() {
        return horasNormais;
    }

    public String getExtra() {
        return horasExtras;
    }

    public String getSalarioBruto() {
        return salarioBruto;
    }

    public String getDescontos() {
        return descontos;
    }

    public String getSalarioLiquido() {
        return salarioLiquido;
    }

    public String getMetodo() {
        return metodo;
    }
}

/*
===================== HORISTAS ================================================================================================
===============================================================================================================================
Nome                                 Horas Extra Salario Bruto Descontos Salario Liquido Metodo
==================================== ===== ===== ============= ========= =============== ======================================
Claudia Abreu                           15     2        198,00      7,00          191,00 Em maos
Claudia Raia                            15     2        180,00     88,40           91,60 Banco do Brasil, Ag. 1591-1 CC 51002-2
Fernanda Montenegro                      0     0          0,00      0,00            0,00 Em maos
Lavinia Vlasak                          15     2        201,78      0,00          201,78 Banco do Brasil, Ag. 1591-1 CC 51001-1
Paloma Duarte                           15     0        168,75      0,00          168,75 Banco do Brasil, Ag. 1591-1 CC 51000-0

TOTAL HORISTAS                          60     6        748,53     95,40          653,13
 */
