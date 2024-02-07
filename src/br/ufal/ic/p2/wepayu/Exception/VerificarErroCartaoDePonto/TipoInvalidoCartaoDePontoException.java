package br.ufal.ic.p2.wepayu.Exception.VerificarErroCartaoDePonto;

public class TipoInvalidoCartaoDePontoException extends Exception{
    public TipoInvalidoCartaoDePontoException(){
        super("Empregado nao eh horista.");
    }
}
