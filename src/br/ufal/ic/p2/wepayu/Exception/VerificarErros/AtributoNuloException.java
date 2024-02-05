package br.ufal.ic.p2.wepayu.Exception.VerificarErros;

public class AtributoNuloException extends Exception{
    public AtributoNuloException(String atributo){
        super(atributo.equals("Comissao") ? atributo + " nao pode ser nula." : atributo + " nao pode ser nulo.");
    }
}
