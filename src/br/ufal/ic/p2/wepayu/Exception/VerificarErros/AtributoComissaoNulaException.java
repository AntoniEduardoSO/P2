package br.ufal.ic.p2.wepayu.Exception.VerificarErros;

import java.util.concurrent.ExecutionException;

public class AtributoComissaoNulaException extends Exception {
    public  AtributoComissaoNulaException(){
        super("Comissao nao pode ser nula.");
    }
}
