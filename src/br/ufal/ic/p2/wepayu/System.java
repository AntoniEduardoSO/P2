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
            verificarErrosNumericos(salario);

            String id = UUID.randomUUID().toString();
            Empregado empregado =  new Empregado(nome, endereco, tipo, salario);

            this.empregados.put(id, empregado);
            return id;
        } catch (AtributoNumericoNaoNumericoException | AtributoNuloException | AtributoTipoNaoValido |  AtributoNumericoNegativoException | AtributoTipoNaoAplicavelException e) {
            throw new ValidacaoException(e.getMessage());
        }
    }

    public String setEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws ValidacaoException {
        try {
            verificarErrosEmpregado(nome, endereco, tipo, salario, comissao);
            verificarErrosNumericos(salario, comissao);

            String id = UUID.randomUUID().toString();
            Empregado empregado =  new Empregado(nome, endereco, tipo, salario);
            empregado.setComissao(comissao);

            this.empregados.put(id, empregado);
            return id;
        } catch (AtributoNumericoNaoNumericoException | AtributoNumericoNegativoException | AtributoNuloException | AtributoTipoNaoValido |  AtributoTipoNaoAplicavelException e) {
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

    public String getAtributoEmpregado(String id, String atributo) throws EmpregadoNaoExisteException, AtributoNaoExisteException, IdEmpregadoNuloException{
        Empregado empregado = getEmpregado(id);
        return empregado.getAtributo(atributo);
    }


    public void verificarErrosEmpregado(String nome, String endereco, String tipo, String salario) throws AtributoNuloException, AtributoTipoNaoValido, AtributoTipoNaoAplicavelException {

        if(nome.isEmpty()){
            throw new AtributoNuloException("Nome");
        }

        else if(endereco.isEmpty()){
            throw new AtributoNuloException("Endereco");
        }

        else if(salario.isEmpty()){
            throw new AtributoNuloException("Salario");
        }

        else if(tipo.equals("abc")){
            throw new AtributoTipoNaoValido();
        }

        else if(tipo.equals("comissionado")){
            throw new AtributoTipoNaoAplicavelException();
        }



    }

    public void verificarErrosEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws AtributoNuloException, AtributoTipoNaoValido,  AtributoTipoNaoAplicavelException {

        if(nome.isEmpty()){
            throw new AtributoNuloException("Nome");
        }

        else if(endereco.isEmpty()){
            throw new AtributoNuloException("Endereco");
        }

        else if(salario.isEmpty()){
            throw new AtributoNuloException("Salario");
        }


        else if(comissao.isEmpty()){
            throw new AtributoNuloException("Comissao");
        }

        else if(!tipo.equals("comissionado")){
            throw new AtributoTipoNaoAplicavelException();
        }


    }

    public void verificarErrosNumericos(String salario) throws  AtributoNumericoNegativoException, AtributoNumericoNaoNumericoException{
        Printar print = new Printar();


        if(salario.contains("-")){
            throw new AtributoNumericoNegativoException("Salario");
        }

        else if (salario.matches(".*[a-zA-Z].*")) {
            throw new AtributoNumericoNaoNumericoException("Salario");
        }

        else if(!salario.contains(",")){
            salario += ",00";
        }


    }


    public void verificarErrosNumericos(String salario, String comissao) throws  AtributoNumericoNegativoException, AtributoNumericoNaoNumericoException{
        Printar print = new Printar();

        if(comissao.contains("-")){
            throw new AtributoNumericoNegativoException("Comissao");
        }

        else if(salario.contains("-")){
            throw new AtributoNumericoNegativoException("Salario");
        }

        else if(comissao.matches(".*[a-zA-Z].*")){
            throw new AtributoNumericoNaoNumericoException("Comissao");
        }

        else if(!salario.contains(",")){
            salario += ",00";
        }
    }

}
