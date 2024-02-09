package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;

public class Servico {

        LocalDate data;
        String valor;
        public Servico(LocalDate data, String valor){
            this.data = data;
            this.valor = valor;
        }

        public String getValor() {
            if(this.valor.contains(",")){

                this.valor = valor.replace(",", ".");
            }

            return this.valor;
        }

        public LocalDate getData() {
            return this.data;
        }

}
