package br.ufal.ic.p2.wepayu;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;


import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.services.*;


import br.ufal.ic.p2.wepayu.Exception.VerificarErros.*;
import br.ufal.ic.p2.wepayu.Exception.VerificarErroCartaoDePonto.*;
import br.ufal.ic.p2.wepayu.Exception.VerificarErrosLancaVendas.*;


public class System {
    private Map<String, Empregado> empregados = new HashMap<>();
    private Map<String, Sindicato> sindicatos = new HashMap<>();

    public System() {


    }


    public void zerarSistema() {
        this.empregados = new HashMap<>();
    }

    public String setEmpregado(String nome, String endereco, String tipo, String salario) throws ValidacaoException {
        Printar print = new Printar();
        try {
            EmpregadoServices.verificarErrosEmpregado(nome, endereco, tipo, salario);
            verificarErrosNumericos(salario);

            String id = UUID.randomUUID().toString();
            Empregado empregado = new Empregado(nome, endereco, tipo, salario, id);

            this.empregados.put(id, empregado);
            EmpregadoServices.verificarIndicesEmpregado(empregado, this.empregados, empregado.getId());

            return id;

        } catch (AtributoNumericoNaoNumericoException | AtributoNuloException | AtributoTipoNaoValido |
                 AtributoNumericoNegativoException | AtributoTipoNaoAplicavelException e) {
            throw new ValidacaoException(e.getMessage());
        }
    }

    public String setEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws ValidacaoException {
        try {
            Printar print = new Printar();
            EmpregadoServices.verificarErrosEmpregado(nome, endereco, tipo, salario, comissao);
            verificarErrosNumericos(salario, comissao);

            String id = UUID.randomUUID().toString();


            Empregado empregado = new Empregado(nome, endereco, tipo, salario, id);
            empregado.setComissao(comissao);

            this.empregados.put(id, empregado);
            EmpregadoServices.verificarIndicesEmpregado(empregado, this.empregados, empregado.getId());

            print.printarTeste("Dentro do setempregado: " + id.toString());


            return id;
        } catch (AtributoNumericoNaoNumericoException | AtributoNumericoNegativoException | AtributoNuloException |
                 AtributoTipoNaoValido | AtributoTipoNaoAplicavelException e) {
            throw new ValidacaoException(e.getMessage());
        }
    }


    public Empregado getEmpregado(String id) throws EmpregadoNaoExisteException, IdEmpregadoNuloException, EmpregadoNaoExisteNomeException {
        Printar print = new Printar();
        if (id.isEmpty()) {
            throw new IdEmpregadoNuloException();
        } else if (!this.empregados.containsKey(id)) {
            throw new EmpregadoNaoExisteException();
        }

        return this.empregados.get(id);
    }

    public String getEmpregadoPorNome(String nome, Integer indice) throws EmpregadoNaoExisteNomeException{
        return EmpregadoServices.verificarPorNome(nome, this.empregados, indice);
    }

    public String getAtributoEmpregado(String id, String atributo) throws EmpregadoNaoExisteException, AtributoNaoExisteException, IdEmpregadoNuloException, EmpregadoNaoExisteNomeException {
        Empregado empregado = getEmpregado(id);
        return empregado.getAtributo(atributo);
    }

    public void removerEmpregado(String id) throws EmpregadoNaoExisteException, IdEmpregadoNuloException, EmpregadoNaoExisteNomeException {
        Empregado empregado = getEmpregado(id);

        empregados.remove(id);
    }

    public void verificarErrosNumericos(String salario) throws AtributoNumericoNegativoException, AtributoNumericoNaoNumericoException {
        Printar print = new Printar();

        if (salario.contains("-")) {
            throw new AtributoNumericoNegativoException("Salario");
        } else if (salario.matches(".*[a-zA-Z].*")) {
            throw new AtributoNumericoNaoNumericoException("Salario");
        }
    }


    public void verificarErrosNumericos(String salario, String comissao) throws AtributoNumericoNegativoException, AtributoNumericoNaoNumericoException {
        Printar print = new Printar();

        if (comissao.contains("-")) {
            throw new AtributoNumericoNegativoException("Comissao");
        } else if (salario.contains("-")) {
            throw new AtributoNumericoNegativoException("Salario");
        } else if (comissao.matches(".*[a-zA-Z].*")) {
            throw new AtributoNumericoNaoNumericoException("Comissao");
        } else if (!salario.contains(",")) {
            salario += ",00";
        }
    }

    public String getHorasNormaisTrabalhadasCartaoDePonto(String id, String dataInicialString, String dataFinalString) throws DataInvalidaException, EmpregadoNaoExisteException, IdEmpregadoNuloException, TipoInvalidoCartaoDePontoException, DataInicialMaiorException, EmpregadoNaoExisteNomeException {
        double horasTotais = 0;
        LocalDate dataInicial = verifica_data_valida("data_inicial", dataInicialString);
        LocalDate dataFinal = verifica_data_valida("data_final", dataFinalString);
        Printar print = new Printar();

        Empregado empregado = getEmpregado(id);

        if (!empregado.getTipo().equals("horista")) {
            throw new TipoInvalidoCartaoDePontoException();
        } else if (dataInicial.compareTo(dataFinal) > 0) {
            throw new DataInicialMaiorException();
        }


        for (LocalDate data = dataInicial; !data.isEqual(dataFinal); data = data.plusDays(1)) {
            if (empregado.getCartoesPonto().containsKey(data)) {
                if (empregado.getCartoesPonto().get(data) >= 8) {
                    horasTotais += 8;
                } else {
                    horasTotais += empregado.getCartoesPonto().get(data);
                }
            }

        }


        String horasTotaisFormatadas = (horasTotais == (int) horasTotais) ? String.format("%.0f", horasTotais) : String.valueOf(horasTotais);

        if (horasTotaisFormatadas.contains(".")) {
            horasTotaisFormatadas = horasTotaisFormatadas.replace(',', '.');
        }

        return String.valueOf(horasTotaisFormatadas);
    }

    public String getHorasExtrasTrabalhadasCartaoDePonto(String id, String dataInicialString, String dataFinalString) throws DataInvalidaException, EmpregadoNaoExisteException, IdEmpregadoNuloException, TipoInvalidoCartaoDePontoException, DataInicialMaiorException, EmpregadoNaoExisteNomeException {
        double horasTotais = 0;
        LocalDate dataInicial = verifica_data_valida("data_inicial", dataInicialString);
        LocalDate dataFinal = verifica_data_valida("data_final", dataFinalString);
        Printar print = new Printar();


        Empregado empregado = getEmpregado(id);

        if (!empregado.getTipo().equals("horista")) {
            throw new TipoInvalidoCartaoDePontoException();
        } else if (dataInicial.compareTo(dataFinal) > 0) {
            throw new DataInicialMaiorException();
        }

        for (LocalDate data = dataInicial; !data.isEqual(dataFinal); data = data.plusDays(1)) {
            if (empregado.getCartoesPonto().containsKey(data)) {

                if (empregado.getCartoesPonto().get(data) > 8.0) {
                    horasTotais += empregado.getCartoesPonto().get(data) - 8;
                }
            }

        }

        String horasTotaisFormatadas = (horasTotais == (int) horasTotais) ? String.format("%.0f", horasTotais) : String.valueOf(horasTotais);

        if (horasTotaisFormatadas.contains(".")) {
            horasTotaisFormatadas = horasTotaisFormatadas.replace('.', ',');
        }


        return String.valueOf(horasTotaisFormatadas);
    }

    public void lancaCartao(String id, String dataString, String horasString) throws EmpregadoNaoExisteException, IdEmpregadoNuloException, DataInvalidaException, TipoInvalidoCartaoDePontoException, HorasNulasException, EmpregadoNaoExisteNomeException {
        Empregado empregado = getEmpregado(id);
        horasString = horasString.replace(',', '.');
        double horas = Double.parseDouble(horasString);

        if (!empregado.getTipo().equals("horista")) {
            throw new TipoInvalidoCartaoDePontoException();
        }

        if (horas <= 0) {
            throw new HorasNulasException("Horas devem ser positivas.");
        }

        LocalDate data = verifica_data_valida("data_cartao", dataString);

        empregado.adicionarCartaoPonto(data, horas);
    }

    public void lancaVenda(String id, String dataString, String valorString)  throws EmpregadoNaoExisteException, IdEmpregadoNuloException, DataInvalidaException, TipoInvalidoLancaVendasException, HorasNulasException, EmpregadoNaoExisteNomeException{
        Empregado empregado = getEmpregado(id);
        valorString = valorString.replace(',', '.');
        double valor = Double.parseDouble(valorString);

        if (!empregado.getTipo().equals("comissionado")) {
            throw new TipoInvalidoLancaVendasException();
        }

        if(valor <= 0 ){
            throw new HorasNulasException("Valor deve ser positivo.");
        }

        LocalDate data = verifica_data_valida("data_cartao", dataString);
        Vendas vendas = new Vendas(data,valor);

        empregado.setLancaVendas(vendas);
    }

    public String getVendasRealizadas(String id, String dataInicialString, String dataFinalString) throws DataInvalidaException, EmpregadoNaoExisteException, IdEmpregadoNuloException, TipoInvalidoLancaVendasException, DataInicialMaiorException, EmpregadoNaoExisteNomeException{
        double valor = 0;
        Empregado empregado = getEmpregado(id);
        LocalDate dataInicial = verifica_data_valida("data_inicial", dataInicialString);
        LocalDate dataFinal = verifica_data_valida("data_final", dataFinalString);

        if (!empregado.getTipo().equals("comissionado")) {
            throw new TipoInvalidoLancaVendasException();
        } else if (dataInicial.compareTo(dataFinal) > 0) {
            throw new DataInicialMaiorException();
        }

        return empregado.lancaVendas(dataInicial,dataFinal);
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

        formato += "yyyy";

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formato);

            LocalDate data = LocalDate.parse(dataString, formatter);

            return data;
        } catch (DateTimeParseException e) {
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

    public void alteraEmpregado(String id, String atributo, String valor) throws EmpregadoNaoExisteException, IdEmpregadoNuloException, EmpregadoNaoExisteNomeException{
        Empregado empregado = getEmpregado(id);

        switch (atributo){
            case "sindicalizado":
                empregado.getSindicalizado().setValor(Boolean.FALSE);
        }
    }

    public void alteraEmpregado(String id, String atributo, boolean valor, String idSindicato, String taxaSindical) throws EmpregadoNaoExisteException, IdEmpregadoNuloException, EmpregadoNaoExisteNomeException{
        Printar print = new Printar();
        Empregado empregado = getEmpregado(id);



        switch (atributo){
            case "sindicalizado":
                Sindicato sindicato = new Sindicato();

        }

        // alteraEmpregado emp=${id3} atributo=sindicalizado valor=true idSindicato=s130 taxaSindical=1,00

//        if(this.sindicatos.containsKey(sindicato.getId())){
//
//        }


    }

    public Sindicato getSindicato(String membro){
        if(!this.sindicatos.containsKey(membro)){
            throw new RuntimeException("FODA-SEEE");
        } else if(membro == null){
            throw new RuntimeException("caguei");
        }


        return this.sindicatos.get(membro);
    }

    public void lancaServico(String membro, String dataString, String valorString) throws HorasNulasException, DataInvalidaException {


        valorString = valorString.replace(',', '.');
        double valor = Double.parseDouble(valorString);


        if(valor <= 0 ){
            throw new HorasNulasException("Servico deve ser positivo.");
        }

        LocalDate data = verifica_data_valida("data_cartao", dataString);
        Servico vendas = new Servico(data,valor);


    }

}
