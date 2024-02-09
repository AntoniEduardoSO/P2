package br.ufal.ic.p2.wepayu;

import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.services.*;


import br.ufal.ic.p2.wepayu.Exception.VerificarErros.*;
import br.ufal.ic.p2.wepayu.Exception.VerificarErroCartaoDePonto.*;
import br.ufal.ic.p2.wepayu.Exception.VerificarErrosLancaVendas.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class System {

    private Map<String, Empregado> empregados = new LinkedHashMap<>();
    File file = new File("./database/empregados.xml");
    private Boolean funcao = Boolean.FALSE;


    public void zerarSistema() {
        this.file.delete();
        this.empregados = new LinkedHashMap<>();
    }

    public void encerrarSistema(){
        Printar print =  new Printar();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc = builder.newDocument();

            // Elemento raiz
            Element rootElement = doc.createElement("empregados");
            doc.appendChild(rootElement);

            // Adiciona cada empregado como um elemento filho do elemento raiz
            for (Map.Entry<String, Empregado> entry : empregados.entrySet()) {
                Empregado empregado = entry.getValue();
                Element empregadoElement = doc.createElement("empregado");
                empregadoElement.setAttribute("id", entry.getKey());
                empregadoElement.setAttribute("nome", empregado.getNome());
                empregadoElement.setAttribute("endereco", empregado.getEndereco());
                empregadoElement.setAttribute("tipo", empregado.getTipo());
                empregadoElement.setAttribute("salario", empregado.getSalario());
                empregadoElement.setAttribute("comissao", empregado.getComissao());
                empregadoElement.setAttribute("indice", empregado.getIndice().toString());


                Element sindicatoElement = doc.createElement("sindicato");
                sindicatoElement.setAttribute("sindicato_id", empregado.getSindicalizado().getId());
                sindicatoElement.setAttribute("valor", String.valueOf(empregado.getSindicalizado().getValor()));
                empregadoElement.appendChild(sindicatoElement);



                for(Servico servico : empregado.getSindicalizado().getLancaServico()){
                    Element servicoElement = doc.createElement("servico");
                    sindicatoElement.setAttribute("data",servico.getData().toString());
                    sindicatoElement.setAttribute("valor",servico.getValor().toString());
                    sindicatoElement.appendChild(servicoElement);
                }


                for (Map.Entry<LocalDate, Double> cartao : empregado.getCartoesPonto().entrySet()) {
                    LocalDate data = cartao.getKey();
                    Double horas = cartao.getValue();
                    Element cartaoElement = doc.createElement("cartaoPonto");
                    cartaoElement.setAttribute("data", data.toString());
                    cartaoElement.setAttribute("horas", horas.toString());
                    empregadoElement.appendChild(cartaoElement);
                }

                for(Vendas venda : empregado.getLancaVendas()){
                    LocalDate data = venda.getData();
                    Double valor = venda.getValor();
                    Element vendaElement = doc.createElement("venda");
                    vendaElement.setAttribute("data", data.toString());
                    vendaElement.setAttribute("valor", valor.toString());
                    empregadoElement.appendChild(vendaElement);
                }
//                print.printarTeste(this.empregados.toString());
                rootElement.appendChild(empregadoElement);
            }

            // Transforma o documento XML em arquivo
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            File file = new File("./database/empregados.xml");
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    public void recuperarDadosEmpregado() throws HorasNulasException, EmpregadoNaoExisteNomeException, EmpregadoNaoExisteException, IdEmpregadoNuloException, TipoInvalidoCartaoDePontoException, DataInvalidaException, TipoInvalidoLancaVendasException {
        if(this.funcao.equals(Boolean.FALSE)){
            try {
                Printar print = new Printar();
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new File("./database/empregados.xml"));

                doc.getDocumentElement().normalize();

                NodeList empregadoNodes = doc.getElementsByTagName("empregado");

                for (int i = 0; i < empregadoNodes.getLength(); i++) {
                    Node empregadoNode = empregadoNodes.item(i);

                    if ( empregadoNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element empregadoElement = (Element) empregadoNode;

                        String id = empregadoElement.getAttribute("id");
                        String nome = empregadoElement.getAttribute("nome");
                        String endereco = empregadoElement.getAttribute("endereco");
                        String tipo = empregadoElement.getAttribute("tipo");
                        String salario = empregadoElement.getAttribute("salario");
                        String comissao = empregadoElement.getAttribute("comissao");
                        String indice = empregadoElement.getAttribute("indice");

                        Empregado empregado = new Empregado(nome, endereco, tipo, salario, id);
                        if(!comissao.equals("")){
                            empregado.setComissao(comissao);
                        }
                        empregado.setIndice( Integer.parseInt(indice));


                        NodeList children = empregadoElement.getChildNodes();
                        for (int j = 0; j < children.getLength(); j++) {
                            Node child = children.item(j);
                            if (child.getNodeType() == Node.ELEMENT_NODE) {
                                Element childElement = (Element) child;
                                String tagName = childElement.getTagName();
                                if (tagName.equals("sindicato")) {
                                    String sindicatoId = childElement.getAttribute("sindicato_id");
                                    String valor = childElement.getAttribute("valor");

                                    // Adicione o código para lidar com os dados do sindicato aqui
                                } else if (tagName.equals("cartaoPonto")) {
                                    String data = childElement.getAttribute("data");
                                    String horas = childElement.getAttribute("horas");


                                } else if (tagName.equals("venda")) {
                                    LocalDate data = verifica_data_valida("cartao_ponto", childElement.getAttribute("data")) ;
                                    String valor = childElement.getAttribute("valor");
                                    Vendas vendas = new Vendas( data, Double.parseDouble(valor));
                                    empregado.setLancaVendas(vendas);
                                } else if (tagName.equals("servico")) {
                                    String data = childElement.getAttribute("data");
                                    String valor = childElement.getAttribute("valor");
                                    // Adicione o código para lidar com os dados do serviço aqui
                                }
                            }
                        }

                        this.empregados.put(id,empregado);
                    }

                    this.funcao = Boolean.TRUE;
                }
            } catch (ParserConfigurationException | IOException | SAXException e) {
                e.printStackTrace();
            }
        }

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
        Printar print = new Printar();
        if(file.exists()){
            try {
                recuperarDadosEmpregado();
            } catch (HorasNulasException e) {
                throw new RuntimeException(e);
            } catch (EmpregadoNaoExisteException e) {
                throw new RuntimeException(e);
            } catch (IdEmpregadoNuloException e) {
                throw new RuntimeException(e);
            } catch (TipoInvalidoCartaoDePontoException e) {
                throw new RuntimeException(e);
            } catch (DataInvalidaException e) {
                throw new RuntimeException(e);
            } catch (TipoInvalidoLancaVendasException e) {
                throw new RuntimeException(e);
            }
        }
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

        for(Map.Entry<String, Empregado> empregado : empregados.entrySet()){

        }




        return null;
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
