package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.VerificarErros.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;

public class Empregado {
    private String id;
    private String nome;
    private String endereco;
    private String tipo;
    private String salario;

    private String comissao;

    private Boolean sindicalizado;
    // Data e Horas
    private Map<LocalDate, Double> cartoesPonto = new HashMap<>();

    private Integer indice;

    public Empregado(String nome, String endereco, String tipo, String salario, String id) {
        this.nome = nome;
        this.endereco = endereco;
        this.tipo = tipo;
        this.salario = salario;
        this.sindicalizado = Boolean.FALSE;
        this.id = id;
    }

    public void setIndice(Integer indice){
        this.indice = indice;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public Integer getIndice(){
        return this.indice;
    }


    public void printarTeste(String id){
        System.out.println(id);
    }

    public void adicionarCartaoPonto(LocalDate data, Double horas) {
        Printar print = new Printar();



        if(!cartoesPonto.containsKey(data)){
            this.cartoesPonto.put(data, horas);
        } else{
            Double horasAtuais = this.cartoesPonto.get(data);
            this.cartoesPonto.put(data, horasAtuais + horas);
        }

    }

    public Map<LocalDate, Double> getCartoesPonto() {
        return cartoesPonto;
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
        if(!this.salario.contains(",")){
            this.salario += ",00";
        }
        return this.salario;
    }

    public String getComissao() {
        return comissao;
    }


}
