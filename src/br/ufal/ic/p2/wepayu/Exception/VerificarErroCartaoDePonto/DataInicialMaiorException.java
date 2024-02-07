package br.ufal.ic.p2.wepayu.Exception.VerificarErroCartaoDePonto;

public class DataInicialMaiorException extends Exception{
    public  DataInicialMaiorException(){
        super("Data inicial nao pode ser posterior aa data final.");
    }
}
