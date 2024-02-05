package br.ufal.ic.p2.wepayu.Exception;

public class EmpregadoDadosExisteException extends Exception{
    public EmpregadoDadosExisteException(String tipo){

        super(tipo + "nao pode ser nulo");
    }
}
