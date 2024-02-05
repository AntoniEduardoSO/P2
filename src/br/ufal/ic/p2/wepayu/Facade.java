package br.ufal.ic.p2.wepayu;



import java.util.*;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.Exception.VerificarErros.*;
import br.ufal.ic.p2.wepayu.models.*;

//import java.util.UUID;

public class Facade {
    private br.ufal.ic.p2.wepayu.System system = new System();


    public void zerarSistema(){
        this.system.zerarSistema();
    }

    public void encerrarSistema(){
        this.system = new System();
    }


    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws ValidacaoException{
        return this.system.setEmpregado(nome, endereco, tipo, salario);
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws ValidacaoException {
        return this.system.setEmpregado(nome, endereco, tipo, salario, comissao);
    }

    public String getAtributoEmpregado(String id, String atributo) throws EmpregadoNaoExisteException, AtributoNaoExisteException, IdEmpregadoNuloException{
        return this.system.getAtributoEmpregado(id, atributo);
    }
}
