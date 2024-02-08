package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;

public class Servico {

        LocalDate data;
        Double valor;
        public Servico(LocalDate data, Double valor){
            this.data = data;
            this.valor = valor;
        }

        public Double getValor() {
            return this.valor;
        }

        public LocalDate getData() {
            return this.data;
        }

}
