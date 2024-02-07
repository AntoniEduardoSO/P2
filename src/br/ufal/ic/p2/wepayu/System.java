package br.ufal.ic.p2.wepayu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;



import br.ufal.ic.p2.wepayu.Exception.*;


import br.ufal.ic.p2.wepayu.Exception.VerificarErros.*;
import br.ufal.ic.p2.wepayu.Exception.VerificarErroCartaoDePonto.*;
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

    public void removerEmpregado(String id) throws EmpregadoNaoExisteException, IdEmpregadoNuloException{
        Empregado empregado = getEmpregado(id);

        empregados.remove(id);
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

    public String getHorasNormaisTrabalhadasCartaoDePonto(String id, String dataInicialString, String dataFinalString) throws DataInvalidaException, EmpregadoNaoExisteException, IdEmpregadoNuloException, TipoInvalidoCartaoDePontoException, DataInicialMaiorException {
        double horasTotais = 0;
        LocalDate dataInicial = verifica_data_valida("data_inicial", dataInicialString);
        LocalDate dataFinal  = verifica_data_valida("data_final", dataFinalString);
        Printar print = new Printar();

        Empregado empregado = getEmpregado(id);

        if(!empregado.getTipo().equals("horista")){
            throw new TipoInvalidoCartaoDePontoException();
        }

        else if (dataInicial.compareTo(dataFinal) > 0) {
            throw new DataInicialMaiorException();
        }

//        print.printarTeste("Data incial: " + dataInicial.toString());
//        print.printarTeste("Data Final: " + dataFinal.toString());

        for (LocalDate data = dataInicial; !data.isAfter(dataFinal); data = data.plusDays(1)) {
//            print.printarTeste(data.toString());
            if (empregado.getCartoesPonto().containsKey(data)) {
                if(empregado.getCartoesPonto().get(data) >= 8){
                    print.printarTeste("HORAS NORMAIS: data atual: " + data.toString() + " e data que tem o cartao de Ponto: " + empregado.getCartoesPonto().toString());
                    horasTotais += 8;
                }


                else{
                    print.printarTeste("HORAS NORMAIS: data atual: " + data.toString() + " e data que tem o cartao de Ponto: " + empregado.getCartoesPonto().toString());
                    horasTotais += empregado.getCartoesPonto().get(data);
                }
            }

        }



//        print.printarTeste(String.valueOf(horasTotais));




        return String.valueOf(horasTotais);
    }

    public String getHorasExtrasTrabalhadasCartaoDePonto(String id, String dataInicialString, String dataFinalString) throws DataInvalidaException, EmpregadoNaoExisteException, IdEmpregadoNuloException, TipoInvalidoCartaoDePontoException, DataInicialMaiorException {
        double horasTotais = 0;
        LocalDate dataInicial = verifica_data_valida("data_inicial", dataInicialString);
        LocalDate dataFinal  = verifica_data_valida("data_final", dataFinalString);
        Printar print = new Printar();


        Empregado empregado = getEmpregado(id);

        if(!empregado.getTipo().equals("horista")){
            throw new TipoInvalidoCartaoDePontoException();
        }

        else if (dataInicial.compareTo(dataFinal) > 0) {
            throw new DataInicialMaiorException();
        }

        for (LocalDate data = dataInicial; !data.isAfter(dataFinal); data = data.plusDays(1)) {
//            print.printarTeste(data.toString());
            if (empregado.getCartoesPonto().containsKey(data)) {

                if(empregado.getCartoesPonto().get(data) > 8.0){
                    print.printarTeste("HORAS EXTRAS: data atual: " + data.toString() + " e data que tem o cartao de Ponto: " + empregado.getCartoesPonto().toString());
                    horasTotais += empregado.getCartoesPonto().get(data) - 8;
                }
            }

        }

        String horasTotaisFormatadas = (horasTotais == (int) horasTotais) ? String.format("%.0f", horasTotais) : String.valueOf(horasTotais);

//        print.printarTeste(String.valueOf(horasTotais));


        return String.valueOf(horasTotaisFormatadas);
    }

    public void lancaCartao(String id, String dataString, Double horas) throws EmpregadoNaoExisteException, IdEmpregadoNuloException, DataInvalidaException, TipoInvalidoCartaoDePontoException, HorasNulasException {
        Empregado empregado = getEmpregado(id);

        if(!empregado.getTipo().equals("horista")){
            throw new TipoInvalidoCartaoDePontoException();
        }

        if(horas <= 0){
            throw new HorasNulasException("Horas devem ser positivas.");
        }

        LocalDate data = verifica_data_valida("data_cartao", dataString);

        empregado.adicionarCartaoPonto(data, horas);
    }

    public LocalDate verifica_data_valida(String identificacao, String dataString) throws DataInvalidaException {
        Printar print = new Printar();
        String formato = "";

        int contadorBarras = 0;

        for (int i = 0; i < dataString.length(); i++) {
            char caractere = dataString.charAt(i);
            if (caractere == '/') {
                contadorBarras++;
                if (contadorBarras == 1) {
                    formato += (i == 1) ? "d/" : "dd/";
                } else if (contadorBarras == 2) {
                    formato += (i == 3) ? "M/" : "MM/";
                }
            }
        }

        formato+="yyyy";

        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formato);

            LocalDate data = LocalDate.parse(dataString, formatter);

            return data;
        }catch (DateTimeParseException e) {
            String mensagemErro;
            switch (identificacao) {
                case "data_inicial":
                    mensagemErro = "Data inicial invalida.";
                    break;
                case "data_final":
                    mensagemErro = "Data final invalida.";
                    break;
                case "data_cartao":
                    mensagemErro = "Data invalida.";
                    break;
                default:
                    mensagemErro = "Data invalida.";
                    break;
            }
            throw new DataInvalidaException(mensagemErro);
        }
    }

}
