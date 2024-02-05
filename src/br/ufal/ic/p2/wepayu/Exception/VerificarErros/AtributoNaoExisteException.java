package br.ufal.ic.p2.wepayu.Exception.VerificarErros;

public class AtributoNaoExisteException extends Exception{
    public AtributoNaoExisteException(){
        super("Atributo nao existe.");
    }
}
