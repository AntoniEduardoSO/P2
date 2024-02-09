package br.ufal.ic.p2.wepayu.models;

public class Pagamento {

    String valor1;
    String banco;
    String agencia;
    String contaCorrente;

    String metodoDePagamento;

    public Pagamento(String valor1){
        this.valor1 = valor1;
    }

    public Pagamento() {

    }

    public String getBanco() {
        return banco;
    }

    public String getAgencia() {
        return agencia;
    }

    public String getContaCorrente() {
        return contaCorrente;
    }


    public String getMetodoDePagamento() {
        return metodoDePagamento;
    }

    public String getValor1() {
        return valor1;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public void setContaCorrente(String contaCorrente) {
        this.contaCorrente = contaCorrente;
    }

    public void setMetodoDePagamento(String metodoDePagamento) {
        this.metodoDePagamento = metodoDePagamento;
    }

    public void setValor1(String valor1) {
        this.valor1 = valor1;
    }
}
