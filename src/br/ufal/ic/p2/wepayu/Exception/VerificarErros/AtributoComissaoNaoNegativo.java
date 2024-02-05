package br.ufal.ic.p2.wepayu.Exception.VerificarErros;


public class AtributoComissaoNaoNegativo  extends  Exception{
    public  AtributoComissaoNaoNegativo(){
        super("Comissao deve ser nao-negativa.");
    }
}
