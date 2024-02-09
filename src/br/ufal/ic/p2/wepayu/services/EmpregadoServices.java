package br.ufal.ic.p2.wepayu.services;


import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteNomeException;
import br.ufal.ic.p2.wepayu.Exception.VerificarErros.AtributoNuloException;
import br.ufal.ic.p2.wepayu.Exception.VerificarErros.AtributoTipoNaoAplicavelException;
import br.ufal.ic.p2.wepayu.Exception.VerificarErros.AtributoTipoNaoValido;
import br.ufal.ic.p2.wepayu.System;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.Printar;

import java.util.Map;

public class EmpregadoServices {

    public static void verificarIndicesEmpregado(Empregado empregado , Map<String, Empregado> empregados, String id){
        empregado.setIndice(verificarPorNome(empregado.getNome(),empregados, id));
    }

    public static Integer verificarPorNome(String nome, Map<String, Empregado> empregados, String id){
        Printar print  = new Printar();
        int indice = 0;
        for (Map.Entry<String, Empregado> entry : empregados.entrySet()) {
            if (entry.getValue().getNome().equals(nome)) {
                indice+=1;
                if(entry.getValue().getId().equals(id)){
                    return indice;
                }
            }
        }

        return indice;
    }


    public static String verificarPorNome(String nome, Map<String, Empregado> empregados, Integer indice) throws EmpregadoNaoExisteNomeException {
        Printar print  = new Printar();

        print.printarTeste("NOME: ");

        for (Map.Entry<String, Empregado> entry : empregados.entrySet()) {
            if (entry.getValue().getNome().equals(nome) && entry.getValue().getIndice().equals(indice)) {
                print.printarTeste("NOME: " + entry.getValue().getNome());
                return entry.getValue().getId();
            }
        }

        throw new EmpregadoNaoExisteNomeException();
    }


    // SERVICES ERROS
    public static void verificarErrosEmpregado(String nome, String endereco, String tipo, String salario) throws AtributoNuloException, AtributoTipoNaoValido, AtributoTipoNaoAplicavelException{
        if (nome.isEmpty()) {
            throw new AtributoNuloException("Nome");
        } else if (endereco.isEmpty()) {
            throw new AtributoNuloException("Endereco");
        } else if (salario.isEmpty()) {
            throw new AtributoNuloException("Salario");
        } else if (tipo.equals("abc")) {
            throw new AtributoTipoNaoValido();
        } else if (tipo.equals("comissionado")) {
            throw new AtributoTipoNaoAplicavelException();
        }
    }

    public static void verificarErrosEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws AtributoNuloException, AtributoTipoNaoValido, AtributoTipoNaoAplicavelException {

        if (nome.isEmpty()) {
            throw new AtributoNuloException("Nome");
        } else if (endereco.isEmpty()) {
            throw new AtributoNuloException("Endereco");
        } else if (salario.isEmpty()) {
            throw new AtributoNuloException("Salario");
        } else if (comissao.isEmpty()) {
            throw new AtributoNuloException("Comissao");
        } else if (!tipo.equals("comissionado")) {
            throw new AtributoTipoNaoAplicavelException();
        }
    }
}
