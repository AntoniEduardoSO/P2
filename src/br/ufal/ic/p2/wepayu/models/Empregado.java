package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.VerificarErros.*;

public class Empregado {
    private String id;
    private String nome;
    private String endereco;
    private String tipo;
    private String salario;

    private String comissao;

    private Boolean sindicalizado;

    public Empregado(String nome, String endereco, String tipo, String salario) {
        this.nome = nome;
        this.endereco = endereco;
        this.tipo = tipo;
        this.salario = salario;
        this.sindicalizado = Boolean.FALSE;
    }

    public void printarTeste(String id){
        System.out.println(id);
    }

    public String getAtributo(String atributo) throws  AtributoNaoExisteException {
        switch (atributo){
            case "nome":
                return this.getNome();

            case "tipo":
                return this.getTipo();

            case "endereco":
                return this.getEndereco();

            case "salario":
                return this.getSalario();

            case "sindicalizado":
                return this.getSindicalizado().toString();

            case "comissao":
                return this.getComissao();


            default:
                throw new AtributoNaoExisteException();
        }
    }

    public Boolean getSindicalizado() {
        return sindicalizado;
    }

    public void setComissao(String comissao){
        this.comissao = comissao;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getTipo() {
        return tipo;
    }

    public String getSalario() {
        return salario;
    }

    public String getComissao() {
        return comissao;
    }
}