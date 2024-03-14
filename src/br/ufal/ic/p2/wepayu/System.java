package br.ufal.ic.p2.wepayu;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

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

    private Map<LocalDate, FolhaDePonto> folhaDePontos = new LinkedHashMap<>();


    public void zerarSistema() {
        this.file.delete();
        this.empregados = new LinkedHashMap<>();
    }

    public void encerrarSistema() {
        Printar print = new Printar();
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
                if (empregado.getSindicalizado().getValor() == Boolean.TRUE) {
                    sindicatoElement.setAttribute("sindicato_id", empregado.getSindicalizado().getId());
                    sindicatoElement.setAttribute("valor", String.valueOf(empregado.getSindicalizado().getValor()));
                    sindicatoElement.setAttribute("taxaSindical", String.valueOf(empregado.getSindicalizado().getTaxaSindical()));
                } else {
                    sindicatoElement.setAttribute("valor", String.valueOf(empregado.getSindicalizado().getValor()));
                }


                empregadoElement.appendChild(sindicatoElement);


                Element pagamentoElemnt = doc.createElement("pagamento");
                pagamentoElemnt.setAttribute("metodo", empregado.getPagamento().getMetodoDePagamento());
                pagamentoElemnt.setAttribute("valor1", String.valueOf(empregado.getPagamento().getValor1()));
                pagamentoElemnt.setAttribute("banco", String.valueOf(empregado.getPagamento().getBanco()));
                pagamentoElemnt.setAttribute("agencia", String.valueOf(empregado.getPagamento().getAgencia()));
                pagamentoElemnt.setAttribute("contaCorrente", String.valueOf(empregado.getPagamento().getContaCorrente()));
                empregadoElement.appendChild(pagamentoElemnt);


                for (Map.Entry<LocalDate, Double> cartao : empregado.getCartoesPonto().entrySet()) {
                    LocalDate data = cartao.getKey();
                    Double horas = cartao.getValue();
                    Element cartaoElement = doc.createElement("cartaoPonto");
                    cartaoElement.setAttribute("data", data.toString());
                    cartaoElement.setAttribute("horas", horas.toString());
                    empregadoElement.appendChild(cartaoElement);
                }

                for (Vendas venda : empregado.getLancaVendas()) {
                    LocalDate data = venda.getData();
                    Double valor = venda.getValor();
                    Element vendaElement = doc.createElement("venda");
                    vendaElement.setAttribute("data", data.toString());
                    vendaElement.setAttribute("valor", valor.toString());
                    empregadoElement.appendChild(vendaElement);
                }
//
                for (Servico servico : empregado.getSindicalizado().getLancaServico()) {
                    LocalDate data = servico.getData();
                    String valor = servico.getValor();
                    Element servicoElement = doc.createElement("servico");
                    servicoElement.setAttribute("data", data.toString());
                    servicoElement.setAttribute("valor", valor);
                    empregadoElement.appendChild(servicoElement);
                }


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

    public void recuperarDadosEmpregado() throws HorasNulasException, EmpregadoNaoExisteNomeException, EmpregadoNaoExisteException, IdEmpregadoNuloException, TipoInvalidoCartaoDePontoException, DataInvalidaException, TipoInvalidoLancaVendasException, AtributoNumericoNegativoException, AtributoNumericoNaoNumericoException {
        if (this.funcao.equals(Boolean.FALSE)) {
            try {
                Printar print = new Printar();
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new File("./database/empregados.xml"));

                doc.getDocumentElement().normalize();

                NodeList empregadoNodes = doc.getElementsByTagName("empregado");

                for (int i = 0; i < empregadoNodes.getLength(); i++) {
                    Node empregadoNode = empregadoNodes.item(i);

                    if (empregadoNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element empregadoElement = (Element) empregadoNode;

                        String id = empregadoElement.getAttribute("id");
                        String nome = empregadoElement.getAttribute("nome");
                        String endereco = empregadoElement.getAttribute("endereco");
                        String tipo = empregadoElement.getAttribute("tipo");
                        String salario = empregadoElement.getAttribute("salario");
                        String comissao = empregadoElement.getAttribute("comissao");
                        String indice = empregadoElement.getAttribute("indice");

                        Empregado empregado = new Empregado(nome, endereco, tipo, salario, id);
                        if (!comissao.equals("")) empregado.setComissao(comissao);
                        empregado.setIndice(Integer.parseInt(indice));


                        NodeList children = empregadoElement.getChildNodes();
                        for (int j = 0; j < children.getLength(); j++) {
                            Node child = children.item(j);
                            if (child.getNodeType() == Node.ELEMENT_NODE) {
                                Element childElement = (Element) child;
                                String tagName = childElement.getTagName();
                                if (tagName.equals("sindicato")) {
                                    String sindicatoId = childElement.getAttribute("sindicato_id");
                                    String valor = childElement.getAttribute("valor");
                                    String taxaSindical = childElement.getAttribute("taxaSindical");

                                    empregado.getSindicalizado().setValor(Boolean.parseBoolean(valor));
                                    empregado.getSindicalizado().setId(sindicatoId);
                                    empregado.getSindicalizado().setTaxaSindical(taxaSindical);


                                } else if (tagName.equals("pagamento")) {
                                    String valor1 = childElement.getAttribute("valor1");
                                    String banco = childElement.getAttribute("banco");
                                    String agencia = childElement.getAttribute("agencia");
                                    String contaCorrente = childElement.getAttribute("contaCorrente");
                                    String metodo = childElement.getAttribute("metodo");

                                    empregado.getPagamento().setValor1(valor1);
                                    empregado.getPagamento().setBanco(banco);
                                    empregado.getPagamento().setAgencia(agencia);
                                    empregado.getPagamento().setContaCorrente(contaCorrente);
                                    empregado.getPagamento().setMetodoDePagamento(metodo);


                                } else if (tagName.equals("cartaoPonto")) {
                                    String dataString = childElement.getAttribute("data");
                                    String horas = childElement.getAttribute("horas");


                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                                    LocalDate data = LocalDate.parse(dataString, formatter);

                                    empregado.adicionarCartaoPonto(data, Double.parseDouble(horas));
//


                                } else if (tagName.equals("venda")) {
                                    String dataString = childElement.getAttribute("data");
                                    String valor = childElement.getAttribute("valor");
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                                    LocalDate data = LocalDate.parse(dataString, formatter);

                                    Vendas vendas = new Vendas(data, Double.parseDouble(valor));
                                    empregado.setLancaVendas(vendas);

                                } else if (tagName.equals("servico")) {
                                    String dataString = childElement.getAttribute("data");
                                    String valor = childElement.getAttribute("valor");
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                                    LocalDate data = LocalDate.parse(dataString, formatter);

                                    Servico servico = new Servico(data, valor);
                                    empregado.getSindicalizado().setListaServico(servico);
                                }
                            }
                        }

                        this.empregados.put(id, empregado);
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
            if(tipo.equals("horista")){
                empregado.getPagamento().setAgendaDePagamento("semanal 5");
            } else if(tipo.equals("assalariado")){
                empregado.getPagamento().setAgendaDePagamento("mensal $");
            }

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
            empregado.getPagamento().setAgendaDePagamento("semanal 2 5");


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

    public String getEmpregadoPorNome(String nome, Integer indice) throws EmpregadoNaoExisteNomeException, AtributoNumericoNegativoException, AtributoNumericoNaoNumericoException {
        Printar print = new Printar();
        if (file.exists()) {
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

    public void verificarErrosNumericosServico(String valor) throws AtributoValorException {
        Printar print = new Printar();
        if (valor.isEmpty()) {
            throw new NullPointerException("Valor pode ser nulo.");
        } else if (valor.equals("0")) {
            throw new AtributoValorException("Valor deve ser positivo.");
        } else if (valor.contains("-")) {
            throw new AtributoValorException("Valor deve ser positivo.");
        } else if (valor.matches(".*[a-zA-Z].*")) {
            throw new AtributoValorException("Valor deve ser numerico.");
        } else if (valor.contains(",")) {
            valor += "0";
        }
    }

    public void verificarErrosNumericosSindicato(String valor) throws AtributoNumericoNegativoException, AtributoNumericoNaoNumericoException, AtributoValorException {
        Printar print = new Printar();
        if (valor.isEmpty()) {
            throw new NullPointerException("Taxa sindical nao pode ser nula.");
        } else if (valor.equals("0")) {
            throw new AtributoValorException("Valor deve ser positivo.");
        } else if (valor.contains("-")) {
            throw new AtributoValorException("Taxa sindical deve ser nao-negativa.");
        } else if (valor.matches(".*[a-zA-Z].*")) {
            throw new AtributoValorException("Taxa sindical deve ser numerica.");
        } else if (valor.contains(",")) {
            valor += "0";
        }
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

    public String getVendasRealizadas(String id, String dataInicialString, String dataFinalString) throws DataInvalidaException, EmpregadoNaoExisteException, IdEmpregadoNuloException, TipoInvalidoLancaVendasException, DataInicialMaiorException, EmpregadoNaoExisteNomeException {
        double valor = 0;
        Empregado empregado = getEmpregado(id);
        LocalDate dataInicial = verifica_data_valida("data_inicial", dataInicialString);
        LocalDate dataFinal = verifica_data_valida("data_final", dataFinalString);

        if (!empregado.getTipo().equals("comissionado")) {
            throw new TipoInvalidoLancaVendasException();
        } else if (dataInicial.compareTo(dataFinal) > 0) {
            throw new DataInicialMaiorException();
        }

        return empregado.lancaVendas(dataInicial, dataFinal);
    }

    public String getTaxasServico(String id, String dataInicialString, String dataFinalString) throws DataInvalidaException, EmpregadoNaoExisteNomeException, EmpregadoNaoExisteException, IdEmpregadoNuloException, DataInicialMaiorException {
        double valor = 0;
        Empregado empregado = getEmpregado(id);

        if (empregado.getSindicalizado().getValor() == Boolean.FALSE) {
            throw new NullPointerException("Empregado nao eh sindicalizado.");
        }

        Sindicato sindicato = empregado.getSindicalizado();

        LocalDate dataInicial = verifica_data_valida("data_inicial", dataInicialString);
        LocalDate dataFinal = verifica_data_valida("data_final", dataFinalString);

        if (dataInicial.compareTo(dataFinal) > 0) {
            throw new DataInicialMaiorException();
        }

        return sindicato.lancaServico(dataInicial, dataFinal);
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

    public void lancaVenda(String id, String dataString, String valorString) throws EmpregadoNaoExisteException, IdEmpregadoNuloException, DataInvalidaException, TipoInvalidoLancaVendasException, HorasNulasException, EmpregadoNaoExisteNomeException {
        Empregado empregado = getEmpregado(id);
        valorString = valorString.replace(',', '.');
        double valor = Double.parseDouble(valorString);

        if (!empregado.getTipo().equals("comissionado")) {
            throw new TipoInvalidoLancaVendasException();
        }

        if (valor <= 0) {
            throw new HorasNulasException("Valor deve ser positivo.");
        }

        LocalDate data = verifica_data_valida("data_cartao", dataString);
        Vendas vendas = new Vendas(data, valor);

        empregado.setLancaVendas(vendas);
    }

    public LocalDate verifica_data_valida(String identificacao, String dataString) throws DataInvalidaException {
        Printar print = new Printar();
        String mensagemErro;
        String formato = "";
        String[] dataVetor = dataString.split("/");
        Integer dia = Integer.parseInt(dataVetor[0]);


        
        if(dia > 31 && dataVetor[1].equals("1")){
            mensagemErro = mensagemErrorData(identificacao);
            throw new DataInvalidaException(mensagemErro);
        } else if(dia > 28 &&  dataVetor[1].equals("2")){
            mensagemErro = mensagemErrorData(identificacao);
            throw new DataInvalidaException(mensagemErro);
        }
        

        int contadorBarras = 0;

        for (int i = 0; i < dataString.length(); i++) {
            char caractere = dataString.charAt(i);
            if (caractere == '/') {
                contadorBarras++;
                if (contadorBarras == 1) {
                    if (i == 1) {
                        formato += "d/";
                    } else {
                        formato += "dd/";
                    }

                } else if (contadorBarras == 2) {
                    if (formato.equals("d/")) {
                        if (i == 3) {
                            formato += "M/";
                        } else {
                            formato += "MM/";
                        }
                    } else {
                        if (i == 4) {
                            formato += "M/";
                        } else {
                            formato += "MM/";
                        }
                    }

                }
            }
        }

        formato += "yyyy";

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formato);

            LocalDate data = LocalDate.parse(dataString, formatter);

            return data;
        } catch (DateTimeParseException e) {
            mensagemErro = mensagemErrorData(identificacao);
            throw new DataInvalidaException(mensagemErro);
        }
    }

    public String mensagemErrorData(String identificacao) throws DataInvalidaException {
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
        return mensagemErro;
    }

    public void alteraEmpregado(String id, String atributo, String valor, String comissao) throws EmpregadoNaoExisteException, IdEmpregadoNuloException, EmpregadoNaoExisteNomeException, AtributoNumericoNegativoException, AtributoNumericoNaoNumericoException {
        Empregado empregado = getEmpregado(id);

        if (valor.equals("abc")) {
            throw new NullPointerException("Tipo nao aplicavel");
        }


        switch (atributo) {

            case "tipo":
                empregado.setTipo(valor);


                if (valor.equals("comissionado")) {
                    empregado.setComissao(comissao);
                } else if (valor.equals("horista")) {
                    empregado.setSalario(comissao);
                } else if (valor.equals("assalariado")) {
                    empregado.setSalario(valor);
                }

                break;

            default:
                throw new NullPointerException("Tipo invalido.");
        }
    }

    public void alteraEmpregado(String id, String atributo, String valor) throws EmpregadoNaoExisteException, IdEmpregadoNuloException, EmpregadoNaoExisteNomeException, AtributoNumericoNegativoException, AtributoNumericoNaoNumericoException {
        Empregado empregado = getEmpregado(id);
        if(atributo.equals("agendaPagamento")){
            if(!valor.equals("mensal $") && !valor.equals("semanal 2 5") && !valor.equals("semanal 5")){
                throw new NullPointerException("Agenda de pagamento nao esta disponivel");
            }

            empregado.getPagamento().setAgendaDePagamento(valor);
            return;
        }


        switch (atributo) {
            case "sindicalizado":
                if (!valor.equals("false") || valor.equals("true")) {
                    throw new NullPointerException("Valor deve ser true ou false.");
                }
                empregado.getSindicalizado().setValor(Boolean.parseBoolean(valor));
                break;

            case "nome":
                if (valor.isEmpty()) {
                    throw new NullPointerException("Nome nao pode ser nulo.");
                }
                empregado.setNome(valor);
                break;

            case "salario":
                if (valor.isEmpty()) {
                    throw new NullPointerException("Salario nao pode ser nulo.");
                }

                verificarErrosNumericos(valor + ",00");
                empregado.setSalario(valor);
                break;

            case "tipo":
                if (valor.equals("abc")) {
                    throw new NullPointerException("Tipo invalido.");
                }
                empregado.setTipo(valor);
                break;

            case "comissao":
                if (valor.isEmpty()) {
                    throw new NullPointerException("Comissao nao pode ser nula.");
                }
                if (!empregado.getTipo().equals("comissionado")) {
                    throw new NullPointerException("Empregado nao eh comissionado.");
                }

                empregado.setComissao(valor);
                break;

            case "endereco":
                if (valor.isEmpty()) {
                    throw new NullPointerException("Endereco nao pode ser nulo.");
                }
                empregado.setEndereco(valor);
                break;

            case "metodoPagamento":
                if (valor.equals("abc")) {
                    throw new NullPointerException("Metodo de pagamento invalido.");
                }
                empregado.getPagamento().setMetodoDePagamento(valor);
                break;

            case "contaCorrente":
                if (valor.isEmpty()) {
                    throw new NullPointerException("Conta corrente nao pode ser nulo.");
                }
                empregado.getPagamento().setContaCorrente(valor);
                break;



            default:
                throw new NullPointerException("Atributo nao existe.");
        }
    }

    public void alteraEmpregado(String id, String atributo, boolean valor, String idSindicato, String taxaSindicalString) throws EmpregadoNaoExisteException, IdEmpregadoNuloException, EmpregadoNaoExisteNomeException, AtributoNumericoNegativoException, AtributoNumericoNaoNumericoException, AtributoValorException {
        Printar print = new Printar();
        Empregado empregado = getEmpregado(id);

        verificarErrosNumericosSindicato(taxaSindicalString);

        for (Map.Entry<String, Empregado> entry : empregados.entrySet()) {
            if (entry.getValue().getSindicalizado().getValor() == Boolean.TRUE) {
                if (entry.getValue().getSindicalizado().getId().equals(idSindicato)) {
                    throw new NullPointerException("Ha outro empregado com esta identificacao de sindicato");
                }
            }
        }

        if (idSindicato.isEmpty()) {
            throw new NullPointerException("Identificacao do sindicato nao pode ser nula.");
        }

        verificarErrosNumericosSindicato(taxaSindicalString);

        Sindicato sindicato = new Sindicato(idSindicato, taxaSindicalString);
        empregado.setSindicato(sindicato);

    }


    public void alteraEmpregado(String id, String atributo, String valor1, String banco, String agencia, String contaCorrente) throws EmpregadoNaoExisteNomeException, EmpregadoNaoExisteException, IdEmpregadoNuloException {
        Empregado empregado = getEmpregado(id);
        if (atributo.equals("metodoPagamento")) {
            if (valor1.equals("banco")) {
                vericaErrosMetodoDePagamento(banco, agencia, contaCorrente);
                Pagamento pagamento = new Pagamento(banco);

                empregado.getPagamento().setMetodoDePagamento("banco");
                empregado.getPagamento().setBanco(banco);
                empregado.getPagamento().setAgencia(agencia);
                empregado.getPagamento().setContaCorrente(contaCorrente);
            }
        }
    }

    public void vericaErrosMetodoDePagamento(String banco, String agencia, String contaCorrente) {
        if (banco.isEmpty()) {
            throw new NullPointerException("Banco nao pode ser nulo.");
        } else if (agencia.isEmpty()) {
            throw new NullPointerException("Agencia nao pode ser nulo.");
        } else if (contaCorrente.isEmpty()) {
            throw new NullPointerException("Conta corrente nao pode ser nulo.");
        }
    }

    public Sindicato getSindicato(String membro) {

        for (Map.Entry<String, Empregado> empregado : empregados.entrySet()) {
            if (empregado.getValue().getSindicalizado().getValor() == Boolean.TRUE) {
                if (empregado.getValue().getSindicalizado().getId().equals(membro)) {
                    return empregado.getValue().getSindicalizado();
                }
            }
        }


        throw new NullPointerException("Membro nao existe.");
    }

    public void lancaServico(String membro, String dataString, String valorString) throws HorasNulasException, DataInvalidaException, AtributoNumericoNegativoException, AtributoValorException, AtributoNumericoNaoNumericoException {
        if (membro == null || membro.equals("")) {
            throw new NullPointerException("Identificacao do membro nao pode ser nula.");
        }


        Sindicato sindicato = getSindicato(membro);
        verificarErrosNumericosServico(valorString);
        LocalDate data = verifica_data_valida("data_cartao", dataString);


        Servico servico = new Servico(data, valorString);
        sindicato.setListaServico(servico);
    }

    public String totalFolha(String dataString) throws DataInvalidaException {
        LocalDate data = verifica_data_valida("data_cartao", dataString);
        CalculoFolha calculoFolha = new CalculoFolha();
        return calculoFolha.puxaFolha(data, this.empregados, this.folhaDePontos);
    }

    public void rodaFolha(String dataString, String saida) throws DataInvalidaException {
        Printar print = new Printar();

        LocalDate data = verifica_data_valida("data_cartao", dataString);
        if(!this.folhaDePontos.isEmpty()){
            FolhaDePonto folhaDePonto = this.folhaDePontos.get(data);
            criaArquivoFolha(data,  saida, folhaDePonto);
        }
    }


    public void criaArquivoFolha(LocalDate data, String saida, FolhaDePonto folhaDePonto){

        String nomeArquivo = saida;


        try {
            // Cria o diretório se ele não existir
            File diretorio = new File("P2");
            if (!diretorio.exists()) {
                diretorio.mkdirs();
            }

            // Cria o arquivo se ele não existir
            File arquivo = new File(nomeArquivo);
            if (!arquivo.exists()) {
                arquivo.createNewFile();

            } else {
                arquivo.delete();
                arquivo.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(arquivo);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);


            escreveArquivoFolha(bufferedWriter, folhaDePonto, data);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void escreveArquivoFolha(BufferedWriter bufferedWriter, FolhaDePonto folhaDePonto, LocalDate data) throws IOException {
        bufferedWriter.write("FOLHA DE PAGAMENTO DO DIA " + data.toString() + "\n");
        bufferedWriter.write("====================================\n\n");

        escreveArquivoHorista(data, bufferedWriter, folhaDePonto);
        escreveArquivoAssalariado(data,bufferedWriter,folhaDePonto);
        escreveArquivoComissinado(data, bufferedWriter, folhaDePonto);

        bufferedWriter.write("TOTAL FOLHA: " + folhaDePonto.getSalarioBrutoTotal() + "\n");


        bufferedWriter.close();
    }

    private void escreveArquivoComissinado(LocalDate data, BufferedWriter bufferedWriter, FolhaDePonto folhaDePonto) throws IOException {
        List<Comissionado> comissionadoList = folhaDePonto.getComissionadoList();

        bufferedWriter.write("===============================================================================================================================\n");
        bufferedWriter.write("===================== COMISSIONADOS ===========================================================================================\n");
        bufferedWriter.write("===============================================================================================================================\n");
        bufferedWriter.write("Nome                  Fixo     Vendas   Comissao Salario Bruto Descontos Salario Liquido Metodo\n");
        bufferedWriter.write("===================== ======== ======== ======== ============= ========= =============== ======================================\n");


        if(folhaDePonto.getSalarioBrutoComissinado() <= 0 ){
            bufferedWriter.write("\nTOTAL COMISSIONADOS       0,00     0,00     0,00          0,00      0,00            0,00\n\n");
        } else{
            Double fixo_total = 0.0, vendas_total = 0.0, comissao_total = 0.0, salario_bruto_total = 0.0, salario_liquido_total = 0.0, descontos_total = 0.0;
            Comparator<Comissionado> comparador = new Comparator<Comissionado>() {
                @Override
                public int compare(Comissionado comissionado1, Comissionado comissionado2) {
                    return comissionado1.getNome().compareTo(comissionado2.getNome());
                }
            };

            Collections.sort(comissionadoList, comparador);

            for(int i = 0; i < comissionadoList.size();i++){
                Comissionado comissionado = comissionadoList.get(i);
                bufferedWriter.write(comissionado.getNome());
                encherDeEspaco(bufferedWriter, 22 - comissionado.getNome().length());
                encherDeEspaco(bufferedWriter, 8 - comissionado.getFixo().length());
                bufferedWriter.write(comissionado.getFixo());
                encherDeEspaco(bufferedWriter, 9 - comissionado.getVendas().length());
                bufferedWriter.write(comissionado.getVendas());
                encherDeEspaco(bufferedWriter,9 - comissionado.getComissao().length());
                bufferedWriter.write(comissionado.getComissao());
                encherDeEspaco(bufferedWriter, 14 - comissionado.getSalarioBruto().length());
                bufferedWriter.write(comissionado.getSalarioBruto());
                encherDeEspaco(bufferedWriter, 10 - comissionado.getDescontos().length());
                bufferedWriter.write(comissionado.getDescontos());
                encherDeEspaco(bufferedWriter, 16 - comissionado.getSalarioLiquido().length());
                bufferedWriter.write(comissionado.getSalarioLiquido());
                bufferedWriter.write(" ");
                bufferedWriter.write(comissionado.getMetodo());

                fixo_total += Double.parseDouble(comissionado.getFixo().replaceAll(",","\\."));
                vendas_total += Double.parseDouble(comissionado.getVendas().replaceAll(",","\\."));
                comissao_total += Double.parseDouble(comissionado.getComissao().replaceAll(",","\\."));
                salario_bruto_total += Double.parseDouble(comissionado.getSalarioBruto().replaceAll(",","\\."));
                salario_liquido_total += Double.parseDouble(comissionado.getSalarioLiquido().replaceAll(",","\\."));
                descontos_total += Double.parseDouble(comissionado.getDescontos().replaceAll(",","\\."));

                bufferedWriter.write("\n");
            }

            java.text.DecimalFormat df = new java.text.DecimalFormat("#.##");
            String numeroFormatadoBruto = df.format(salario_bruto_total).replaceAll("\\.", ",");
            String numeroFormatadoLiquido = df.format(salario_liquido_total).replaceAll("\\.", ",");
            String numeroFormatadoVendas = df.format(vendas_total).replaceAll("\\.", ",");


            String fixo_total_str = fixo_total.toString().replaceAll("\\.", ",");
            String comissao_total_str = comissao_total.toString().replaceAll("\\.", ",");
            String descontos_total_str = descontos_total.toString().replaceAll("\\.", ",");

            numeroFormatadoBruto = getErrosComissaoTotalValor(numeroFormatadoBruto);
            numeroFormatadoLiquido = getErrosComissaoTotalValor(numeroFormatadoLiquido);
            numeroFormatadoVendas = getErrosComissaoTotalValor(numeroFormatadoVendas);
            fixo_total_str =  getErrosComissaoTotal(fixo_total_str);
            comissao_total_str =  getErrosComissaoTotal(comissao_total_str);
            descontos_total_str =  getErrosComissaoTotal(descontos_total_str);


            bufferedWriter.write("\n");
            bufferedWriter.write("TOTAL COMISSIONADOS");
            encherDeEspaco(bufferedWriter, 22 - 19);
            encherDeEspaco(bufferedWriter, 8 - fixo_total_str.length());
            bufferedWriter.write(fixo_total_str);
            encherDeEspaco(bufferedWriter, 9 - numeroFormatadoVendas.length());
            bufferedWriter.write(numeroFormatadoVendas);
            encherDeEspaco(bufferedWriter,9 - comissao_total_str.length());
            bufferedWriter.write(comissao_total_str);
            encherDeEspaco(bufferedWriter, 14 - numeroFormatadoBruto.length());
            bufferedWriter.write(numeroFormatadoBruto);
            encherDeEspaco(bufferedWriter, 10 - descontos_total_str.length());
            bufferedWriter.write(descontos_total_str);
            encherDeEspaco(bufferedWriter, 16 - numeroFormatadoLiquido.length());
            bufferedWriter.write(numeroFormatadoLiquido);
            bufferedWriter.write("\n\n");
        }
    }


    private String getErrosComissaoTotalValor(String valor) {
        String[] partes = valor.split(",");
        if(!valor.contains(",")){
            valor+=",00";
        } else if(partes.length == 2 && partes[1].length() == 1){
            valor +="0";
        }

        return valor;
    }
    private String getErrosComissaoTotal(String valor) {
        String[] partes = valor.split(",");
        if (partes.length == 2 && partes[1].length() == 1) {
            valor +=0;
        }

        return valor;
    }


    private void escreveArquivoHorista(LocalDate data, BufferedWriter bufferedWriter, FolhaDePonto folhaDePonto) throws IOException {
        List<Horista> horistaList = folhaDePonto.getHoristaList();
        String horas_totais, horas_extras;


        bufferedWriter.write("===============================================================================================================================\n");
        bufferedWriter.write("===================== HORISTAS ================================================================================================\n");
        bufferedWriter.write("===============================================================================================================================\n");
        bufferedWriter.write("Nome                                 Horas Extra Salario Bruto Descontos Salario Liquido Metodo\n");
        bufferedWriter.write("==================================== ===== ===== ============= ========= =============== ======================================\n");

        Comparator<Horista> comparador = new Comparator<Horista>() {
            @Override
            public int compare(Horista horista1, Horista horista2) {
                return horista1.getNome().compareTo(horista2.getNome());
            }
        };

        Collections.sort(horistaList, comparador);
        if(data.plusDays(1).getDayOfMonth() == 1){
            bufferedWriter.write("\nTOTAL HORISTAS                           0     0          0,00      0,00            0,00\n");
            bufferedWriter.write("\n");
        }

        else{
            for(int i = 0; i < horistaList.size();i++){
                Horista horista = horistaList.get(i);
                bufferedWriter.write(horista.getNome());
                encherDeEspaco(bufferedWriter, 37 - horista.getNome().length());
                encherDeEspaco(bufferedWriter, 5 - horista.getHoras().length());
                bufferedWriter.write(horista.getHoras());
                encherDeEspaco(bufferedWriter, 6 - horista.getExtra().length());
                bufferedWriter.write(horista.getExtra());
                encherDeEspaco(bufferedWriter, 14 - horista.getSalarioBruto().length());
                bufferedWriter.write(horista.getSalarioBruto());
                encherDeEspaco(bufferedWriter, 10 - horista.getDescontos().length());
                bufferedWriter.write(horista.getDescontos());
                encherDeEspaco(bufferedWriter, 16 - horista.getSalarioLiquido().length());
                bufferedWriter.write(horista.getSalarioLiquido());
                bufferedWriter.write(" ");
                bufferedWriter.write(horista.getMetodo());
                bufferedWriter.write("\n");
            }

            horas_totais = getHorasTotais(horistaList);
            horas_extras = getHorasExtras(horistaList);
            String salario_bruto_total = getSalarioBrutoHorista(horistaList);
            String desconto_total = getDescontoHorista(horistaList);
            Double salario_liquido_total = Double.parseDouble(salario_bruto_total.replaceAll(",", "\\.")) - Double.parseDouble(desconto_total.replaceAll(",","\\."));
            String salario_liquido_total_str = salario_liquido_total.toString().replaceAll("\\.", ",");
            if(salario_liquido_total_str.matches(".*,[0-9]$")){
                salario_liquido_total_str+="0";
            }

            bufferedWriter.write("\n");
            bufferedWriter.write("TOTAL HORISTAS");
            encherDeEspaco(bufferedWriter, 37-14);
            encherDeEspaco(bufferedWriter,  5 - horas_totais.length());
            bufferedWriter.write(horas_totais);
            encherDeEspaco(bufferedWriter, 6 - horas_extras.length());
            bufferedWriter.write(horas_extras);
            encherDeEspaco(bufferedWriter, 14 - salario_bruto_total.length());
            bufferedWriter.write(salario_bruto_total);
            encherDeEspaco(bufferedWriter, 10 - desconto_total.length());
            bufferedWriter.write(desconto_total);
            encherDeEspaco(bufferedWriter, 16 - salario_liquido_total_str.length());
            bufferedWriter.write(salario_liquido_total_str);
            bufferedWriter.write("\n\n");
        }

    }

    private String getDescontoHorista(List<Horista> horistaList){
        Double desconto_total = 0.0;
        for(int i = 0; i < horistaList.size();i++){
            desconto_total+= Double.parseDouble(horistaList.get(i).getDescontos().replaceAll(",", "\\."));
        }

        String desconto_total_str = desconto_total.toString().replaceAll("\\.", ",");

        if(desconto_total_str.matches(".*,[0-9]$")){
            desconto_total_str += 0;
        }

        return desconto_total_str;
    }

    private String getSalarioBrutoHorista(List<Horista> horistaList) {
        Double salario_bruto_total = 0.0;
        for(int i = 0; i < horistaList.size();i++){
            salario_bruto_total += Double.parseDouble(horistaList.get(i).getSalarioBruto().replaceAll(",","\\."));
        }

        String salario_bruto_total_str = salario_bruto_total.toString().replaceAll("\\.",",");

        if (salario_bruto_total_str.matches(".*,[0-9]$")) {
            salario_bruto_total_str += "0";
        }

        return salario_bruto_total_str;
    }

    private String getHorasTotais(List<Horista> horistaList) {
        Double horas_normais = 0.0;

        for( int i = 0; i < horistaList.size(); i++){
            horas_normais += Double.parseDouble(horistaList.get(i).getHoras());
        }


        String horas_normais_str = horas_normais.toString();

        if(horas_normais_str.contains(".0")){
            horas_normais_str =  horas_normais_str.replace(".0","");
        } else{
            horas_normais_str =  horas_normais_str.replaceAll("\\.", ",");
        }


        return horas_normais_str;
    }

    private String getHorasExtras(List<Horista> horistaList) {
        Double horas_extras = 0.0;

        for( int i = 0; i < horistaList.size(); i++){
            horas_extras += Double.parseDouble(horistaList.get(i).getExtra());
        }


        String horas_extras_str = horas_extras.toString();

        if(horas_extras_str.contains(".0")){
            horas_extras_str =  horas_extras_str.replace(".0","");
        } else{
            horas_extras_str =  horas_extras_str.replaceAll("\\.", ",");
        }


        return horas_extras_str;
    }

    private void escreveArquivoAssalariado(LocalDate data, BufferedWriter bufferedWriter, FolhaDePonto folhaDePonto) throws IOException {
        List<Assalariado> assalariadoList = folhaDePonto.getAssalariadoList();
        bufferedWriter.write("===============================================================================================================================\n");
        bufferedWriter.write("===================== ASSALARIADOS ============================================================================================\n");
        bufferedWriter.write("===============================================================================================================================\n");
        bufferedWriter.write("Nome                                             Salario Bruto Descontos Salario Liquido Metodo\n");
        bufferedWriter.write("================================================ ============= ========= =============== ======================================\n");

        if(data.plusDays(1).getDayOfMonth() == 1){
            Comparator<Assalariado> comparador = new Comparator<Assalariado>() {
                @Override
                public int compare(Assalariado assalariado1, Assalariado assalariado2) {
                    return assalariado1.getNome().compareTo(assalariado2.getNome());
                }
            };

            Collections.sort(assalariadoList, comparador);

            Double salario_bruto_total = 0.0, descontos_total = 0.0, salario_liquido_total = 0.0;
            for(int i = 0; i < assalariadoList.size();i++){
                Assalariado assalariado = assalariadoList.get(i);
                bufferedWriter.write(assalariado.getNome());
                encherDeEspaco(bufferedWriter, 49 - assalariado.getNome().length());
                encherDeEspaco(bufferedWriter, 13 - assalariado.getSalarioBruto().length());
                bufferedWriter.write(assalariado.getSalarioBruto());
                encherDeEspaco(bufferedWriter, 10 - assalariado.getDescontos().length());
                bufferedWriter.write(assalariado.getDescontos());
                encherDeEspaco(bufferedWriter, 16 - assalariado.getSalarioLiquido().length());
                bufferedWriter.write(assalariado.getSalarioLiquido());
                bufferedWriter.write(" " + assalariado.getMetodo());

                bufferedWriter.write("\n");

                salario_bruto_total += Double.parseDouble(assalariado.getSalarioBruto().replaceAll(",","\\."));
                descontos_total += Double.parseDouble(assalariado.getDescontos().replaceAll(",","\\."));
                salario_liquido_total += Double.parseDouble(assalariado.getSalarioLiquido().replaceAll(",","\\."));
            }

            String salario_bruto_total_str = salario_bruto_total.toString().replaceAll("\\.",",");
            String descontos_total_str = descontos_total.toString().replaceAll("\\.",",");
            String salario_liquido_total_str = salario_liquido_total.toString().replaceAll("\\.",",");

            if(salario_bruto_total_str.matches(".*,[0-9]$")){
                salario_bruto_total_str+="0";
            }

            if(descontos_total_str.matches(".*,[0-9]$")){
                descontos_total_str+="0";
            }

            if(salario_liquido_total_str.matches(".*,[0-9]$")){
                salario_liquido_total_str+="0";
            }

            bufferedWriter.write("\n");
            bufferedWriter.write("TOTAL ASSALARIADOS");
            encherDeEspaco(bufferedWriter, 49 - 18);
            encherDeEspaco(bufferedWriter, 13 - salario_bruto_total_str.length());
            bufferedWriter.write(salario_bruto_total_str);
            encherDeEspaco(bufferedWriter, 10 - descontos_total_str.length());
            bufferedWriter.write(descontos_total_str);
            encherDeEspaco(bufferedWriter, 16 - salario_liquido_total_str.length());
            bufferedWriter.write(salario_liquido_total_str);
            bufferedWriter.write("\n\n");

        } else {
            bufferedWriter.write("\n");
            bufferedWriter.write("TOTAL ASSALARIADOS                                        0,00      0,00            0,00");
            bufferedWriter.write("\n\n");
        }
    }

    private void encherDeEspaco(BufferedWriter bufferedWriter, int length) throws IOException {
        for(int i =0; i < length; i ++){
            bufferedWriter.write(" ");
        }
    }


}
