package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.Exception.AgendaInvalidaException;

import java.util.List;

public class AgendaDePagamentoCalculo {
    public static void main(String descricao, List<String> agendaPagamentos) throws AgendaInvalidaException {
        String valores[] = descricao.split(" ");
        String tipo  = valores[0];

        if(agendaPagamentos.isEmpty()){
            agendaPagamentos.add("mensal $");
            agendaPagamentos.add("semanal 5");
            agendaPagamentos.add("semanal 2 5");
        }

        for(int i = 0; i < agendaPagamentos.size();i++){
            if(agendaPagamentos.get(i).equals(descricao)){
                throw new AgendaInvalidaException("Agenda de pagamentos ja existe");
            }
        }

        if(tipo.contains("semanal")){
            verificarErrosTipoSemanal(descricao,agendaPagamentos, valores);
        } else if(tipo.contains("mensal")){
            verificarErrosTipoMensal(descricao,agendaPagamentos, valores);
        } else{
            throw new AgendaInvalidaException("Descricao de agenda invalida");
        }
    }

    public static void verificarErrosTipoSemanal(String descricao, List<String> agendaDePagamentos, String[] valores) throws AgendaInvalidaException {
        if(valores.length > 2){
            Integer repeticoes = Integer.parseInt(valores[1]);
            Integer dia = Integer.parseInt(valores[2]);

            if(dia < 1 || dia > 7){
                throw new AgendaInvalidaException("Descricao de agenda invalida");
            } else if (repeticoes < 0 || repeticoes > 52) {
                throw new AgendaInvalidaException("Descricao de agenda invalida");
            }

            agendaDePagamentos.add(descricao);


        } else{
            Integer dia = Integer.parseInt(valores[1]);

            if(dia < 1 || dia > 7){
                throw new AgendaInvalidaException("Descricao de agenda invalida");
            }

            agendaDePagamentos.add(descricao);
        }

    }

    public static void verificarErrosTipoMensal(String descricao, List<String> agendaDePagamentos, String[] valores) throws AgendaInvalidaException {
        Integer dia = Integer.parseInt(valores[1]);

        if(dia > 28 || dia < 1){
            throw new AgendaInvalidaException("Descricao de agenda invalida");
        }

        agendaDePagamentos.add(descricao);
    }
}
