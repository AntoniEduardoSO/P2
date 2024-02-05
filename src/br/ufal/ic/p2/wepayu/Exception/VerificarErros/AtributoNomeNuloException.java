package br.ufal.ic.p2.wepayu.Exception.VerificarErros;

public class AtributoNomeNuloException  extends Exception{
    public AtributoNomeNuloException(){
        super("Nome nao pode ser nulo.");
    }
}
