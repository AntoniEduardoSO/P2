package br.ufal.ic.p2.wepayu.Exception.VerificarErrosLancaVendas;

public class TipoInvalidoLancaVendasException extends Exception {
    public TipoInvalidoLancaVendasException(){
        super("Empregado nao eh comissionado.");
    }
}
