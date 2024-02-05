package br.ufal.ic.p2.wepayu.Exception.VerificarErros;

public class AtributoNumericoNegativoException extends Exception {
    public AtributoNumericoNegativoException(String numerico){
        super(numerico.equals("Comissao") ? numerico + " deve ser nao-negativa." : numerico + " deve ser nao-negativo.");
    }
}
