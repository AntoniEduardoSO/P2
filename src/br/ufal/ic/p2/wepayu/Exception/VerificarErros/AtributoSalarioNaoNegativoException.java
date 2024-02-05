package br.ufal.ic.p2.wepayu.Exception.VerificarErros;

public class AtributoSalarioNaoNegativoException extends Exception{
    public AtributoSalarioNaoNegativoException(){
        super("Salario deve ser nao-negativo.");
    }
}
