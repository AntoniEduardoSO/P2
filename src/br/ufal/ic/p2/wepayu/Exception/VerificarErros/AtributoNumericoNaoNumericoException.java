package br.ufal.ic.p2.wepayu.Exception.VerificarErros;

public class AtributoNumericoNaoNumericoException extends  Exception{

    public AtributoNumericoNaoNumericoException(String numerico){
        super(numerico.equals("Comissao") ? numerico + " deve ser numerica." : numerico + " deve ser numerico.");
    }
}
