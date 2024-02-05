package br.ufal.ic.p2.wepayu.Exception.VerificarErros;

public class AtributoSalarioNuloException extends Exception{
    public AtributoSalarioNuloException(){
        super("Salario nao pode ser nulo.");
    }
}
