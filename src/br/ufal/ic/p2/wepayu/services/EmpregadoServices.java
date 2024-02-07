package br.ufal.ic.p2.wepayu.services;


import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteNomeException;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.Printar;

import java.util.Map;

public class EmpregadoServices {
    public static void verificarIndicesEmpregado(Empregado empregado , Map<String, Empregado> empregados, String id){
        empregado.setIndice(verificarPorNome(empregado.getNome(),empregados, id));
    }

    public static Integer verificarPorNome(String nome, Map<String, Empregado> empregados, String id){
        Integer indice = 0;
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
        Printar print = new Printar();

        for (Map.Entry<String, Empregado> entry : empregados.entrySet()) {
            if (entry.getValue().getNome().equals(nome) && entry.getValue().getIndice().equals(indice)) {
                return entry.getValue().getId();
            }
        }

        throw new EmpregadoNaoExisteNomeException();
    }
}
