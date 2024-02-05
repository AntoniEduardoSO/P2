package br.ufal.ic.p2.wepayu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


import br.ufal.ic.p2.wepayu.Exception.*;


import br.ufal.ic.p2.wepayu.Exception.VerificarErros.*;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.Printar;

public class System {
    private Map<String, Empregado> empregados = new HashMap<>();
    public System() {


    }


    public void zerarSistema() {
        this.empregados = new HashMap<>();
    }

    public String setEmpregado(String nome, String endereco, String tipo, String salario) throws ValidacaoException {
        try {
            verificarErrosEmpregado(nome, endereco, tipo, salario);

            String id = UUID.randomUUID().toString();
            Empregado empregado =  new Empregado(nome, endereco, tipo, salario);

            this.empregados.put(id, empregado);
            return id;
        } catch (AtributoTipoNaoValido | AtributoNomeNuloException | AtributoEnderecoNuloException | AtributoSalarioNuloException | AtributoSalarioNaoNegativoException e) {
            throw new ValidacaoException(e.getMessage());
        }
    }

    public String setEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws ValidacaoException {
        try {
            verificarErrosEmpregado(nome, endereco, tipo, salario, comissao);
            verificarErrosSalario(salario);

            String id = UUID.randomUUID().toString();
            Empregado empregado =  new Empregado(nome, endereco, tipo, salario);
            empregado.setComissao(comissao);

            this.empregados.put(id, empregado);
            return id;
        } catch (AtributoTipoNaoAplicavel | AtributoTipoNaoValido | AtributoNomeNuloException | AtributoComissaoNulaException | AtributoEnderecoNuloException | AtributoSalarioNuloException | AtributoSalarioNaoNegativoException | AtributoComissaoNaoNegativo e) {
            throw new ValidacaoException(e.getMessage());
        }
    }


    public Empregado getEmpregado(String id) throws EmpregadoNaoExisteException, IdEmpregadoNuloException {

        if(id.isEmpty()){
            throw new IdEmpregadoNuloException();
        }

        else if (!this.empregados.containsKey(id)) {
            throw new EmpregadoNaoExisteException();
        }

        return this.empregados.get(id);
    }

    public String getAtributoEmpregado(String id, String atributo) throws EmpregadoNaoExisteException, AtributoNaoExisteException, AtributoNomeNuloException,IdEmpregadoNuloException{
        Empregado empregado = getEmpregado(id);
        return empregado.getAtributo(atributo);
    }


    public void verificarErrosEmpregado(String nome, String endereco, String tipo, String salario) throws AtributoTipoNaoValido, AtributoNomeNuloException, AtributoEnderecoNuloException, AtributoSalarioNuloException, AtributoSalarioNaoNegativoException {

        if(nome.isEmpty()){
            throw new AtributoNomeNuloException();
        }

//        else if(tipo != "horista)

        else if(endereco.isEmpty()){
            throw new AtributoEnderecoNuloException();
        }

        else if(salario.isEmpty()){
            throw new AtributoSalarioNuloException();
        }

        else if(salario.contains("-")){
            throw new AtributoSalarioNaoNegativoException();
        }


    }

    public void verificarErrosEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws AtributoTipoNaoAplicavel, AtributoTipoNaoValido, AtributoNomeNuloException, AtributoEnderecoNuloException, AtributoSalarioNuloException, AtributoSalarioNaoNegativoException, AtributoComissaoNulaException, AtributoComissaoNaoNegativo {

        if(nome.isEmpty()){
            throw new AtributoNomeNuloException();
        }

        else if(endereco.isEmpty()){
            throw new AtributoEnderecoNuloException();
        }

        else if(salario.isEmpty()){
            throw new AtributoSalarioNuloException();
        }

        else if(salario.contains("-")){
            throw new AtributoSalarioNaoNegativoException();
        }

        else if(comissao.contains(("-"))){
            throw new AtributoComissaoNaoNegativo();
        }

        else if(comissao.isEmpty()){
            throw new AtributoComissaoNulaException();
        }

        else if(tipo != "comissionado" ){
            throw new AtributoTipoNaoAplicavel();
        }

        else if(!salario.contains(",")){
            salario += ",00";
        }


    }

    public void verificarErrosSalario(String salario){
        if(!salario.contains(",")){
            salario += ",00";
        }
    }

}
