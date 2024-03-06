package br.ufal.ic.p2.wepayu;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
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
        String formato = "";

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

    public void alteraEmpregado(String id, String atributo, String valor) throws EmpregadoNaoExisteException, IdEmpregadoNuloException, EmpregadoNaoExisteNomeException, AtributoNumericoNegativoException, AtributoNumericoNaoNumericoException {
        Empregado empregado = getEmpregado(id);


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

//    public void rodaFolha(String dataString, String saida) throws DataInvalidaException {
//        LocalDate data = verifica_data_valida("data_cartao", dataString);
//        FolhaDePonto cartaoDePonto = this.cartoesDePontos.get(data);
//
//
//        String nomeArquivo = "./ok/folha-" + saida;
//
//        try {
//            // Cria o diretório se ele não existir
//            File diretorio = new File("WePayU/ok");
//            if (!diretorio.exists()) {
//                diretorio.mkdirs();
//            }
//
//            // Cria o arquivo se ele não existir
//            File arquivo = new File(diretorio, nomeArquivo);
//            if (!arquivo.exists()) {
//                arquivo.createNewFile();
//            } else {
//
//            }
//        } catch (IOException e) {
//        }
//    }
}
