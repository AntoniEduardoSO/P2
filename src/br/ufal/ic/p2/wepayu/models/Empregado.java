package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.VerificarErros.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Empregado {
    private String id;
    private String nome;
    private String endereco;
    private String tipo;
    private String salario;

    private String comissao;

    private Sindicato sindicato = new Sindicato();
    // Data e Horas
    private Map<LocalDate, Double> cartoesPonto = new HashMap<>();

    private Integer indice;

    private final List<Vendas> listaVendas = new ArrayList<>();

    private Pagamento pagamento = new Pagamento();


    public Empregado(String nome, String endereco, String tipo, String salario, String id) {
        this.nome = nome;
        this.endereco = endereco;
        this.tipo = tipo;
        this.salario = salario;
        this.sindicato.setValor(Boolean.FALSE);
        this.id = id;
        this.getPagamento().setMetodoDePagamento("emMaos");
    }

    public String getId() {
        return this.id;
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
                return String.valueOf(this.sindicato.getValor());

            case "comissao":
                if(!this.getTipo().equals("comissionado")){
                    throw new NullPointerException("Empregado nao eh comissionado.");
                }
                return this.getComissao();

            case "idSindicato":
                if(!this.sindicato.getValor() == Boolean.TRUE){
                    throw new NullPointerException("Empregado nao eh sindicalizado.");
                }
                return this.sindicato.getId();

            case "taxaSindical":
                if(!this.sindicato.getValor() == Boolean.TRUE){
                    throw new NullPointerException("Empregado nao eh sindicalizado.");
                }
                return this.sindicato.getTaxaSindical();

            case "banco":
                if(!this.getPagamento().getMetodoDePagamento().equals("banco")){
                    throw new NullPointerException("Empregado nao recebe em banco.");
                }
                return this.pagamento.getBanco();

            case "agencia":
                if(!this.getPagamento().getMetodoDePagamento().equals("banco")){
                    throw new NullPointerException("Empregado nao recebe em banco.");
                }
                return this.pagamento.getAgencia();

            case "contaCorrente":
                if(!this.getPagamento().getMetodoDePagamento().equals("banco")){
                    throw new NullPointerException("Empregado nao recebe em banco.");
                }
                return this.pagamento.getContaCorrente();


            case "metodoPagamento":
                return this.pagamento.getMetodoDePagamento();

            default:
                throw new AtributoNaoExisteException();
        }
    }

    public Sindicato getSindicalizado() {
        return this.sindicato;
    }

    public Map<LocalDate, Double> getCartoesPonto() {
        return cartoesPonto;
    }

    public List<Vendas> getLancaVendas(){
        return this.listaVendas;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento)  {
        this.pagamento = pagamento;
    }

    public void setSindicato(Sindicato sindicato){
        this.sindicato = sindicato;
    }

    public void setLancaVendas(Vendas vendas){
        listaVendas.add(vendas);
    }


    public String lancaVendas(LocalDate dataInicial, LocalDate dataFinal) {
        double totalVendasDia = 0;
        for (LocalDate data = dataInicial; !data.isEqual(dataFinal); data = data.plusDays(1)) {
            for (Vendas venda : listaVendas) {
                if (venda.getData().isEqual(data)) {
                    totalVendasDia += venda.getValor();
                }
            }
        }

        String totalVendasDiaFormatadas = (totalVendasDia == (int) totalVendasDia) ? String.format("%.0f", totalVendasDia) : String.valueOf(totalVendasDia);

        if (totalVendasDiaFormatadas.contains(".")) {
            if(totalVendasDiaFormatadas.matches(".*\\d.*")){
                totalVendasDiaFormatadas += "0";
            }
        }

        else{
            totalVendasDiaFormatadas+=",00";
        }


        totalVendasDiaFormatadas = totalVendasDiaFormatadas.replace('.', ',');

        return totalVendasDiaFormatadas;
    }

    public void setIndice(Integer indice){
        this.indice = indice;
    }

    public void setId(String id){
        this.id = id;
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


    public void setComissao(String comissao) throws AtributoNumericoNegativoException, AtributoNumericoNaoNumericoException {
        if (comissao.contains("-")) {
            throw new AtributoNumericoNegativoException("Comissao");
        }  else if (comissao.matches(".*[a-zA-Z].*")) {
            throw new AtributoNumericoNaoNumericoException("Comissao");
        }
        this.comissao = comissao;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setSalario(String salario) {
        this.salario = salario;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setMetodo(Pagamento banco) {
        this.pagamento = banco;
    }

}
