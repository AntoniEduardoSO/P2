package br.ufal.ic.p2.wepayu.Exception;

public class EmpregadoNaoExisteNomeException extends Exception{
    public EmpregadoNaoExisteNomeException(){
        super("Nao ha empregado com esse nome.");
    }
}
