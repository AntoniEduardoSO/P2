package br.ufal.ic.p2.wepayu.Exception.VerificarErros;

public class IdEmpregadoNuloException extends Exception {
    public  IdEmpregadoNuloException(){
        super("Identificacao do empregado nao pode ser nula.");
    }
}
