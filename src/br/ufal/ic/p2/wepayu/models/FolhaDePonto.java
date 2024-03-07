package br.ufal.ic.p2.wepayu.models;

import java.util.LinkedList;
import java.util.List;

public class FolhaDePonto {
    List<Assalariado> assalariadoList = new LinkedList<>();
    List<Horista> horistaList = new LinkedList<>();

    List<Comissionado> comissionadoList = new LinkedList<>();

    String salarioBrutoTotal;

    Double salarioBrutoHorista;

    Double salarioBrutoAssalariado;

    Double salarioBrutoComissinado;

    public Double getSalarioBrutoComissinado() {
        return salarioBrutoComissinado;
    }

    public Double getSalarioBrutoAssalariado() {
        return salarioBrutoAssalariado;
    }

    public Double getSalarioBrutoHorista() {
        return salarioBrutoHorista;
    }

    public List<Assalariado> getAssalariadoList() {
        return assalariadoList;
    }

    public List<Horista> getHoristaList() {
        return horistaList;
    }

    public List<Comissionado> getComissionadoList() {
        return comissionadoList;
    }

    public String getSalarioBrutoTotal() {return salarioBrutoTotal;}

    public void setSalarioBrutoTotal(String salarioBrutoTotal) {
        this.salarioBrutoTotal = salarioBrutoTotal;
    }

    public void setAssalariadoList(List<Assalariado> assalariadoList) {
        this.assalariadoList = assalariadoList;
    }

    public void setHoristaList(List<Horista> horistaList) {
        this.horistaList = horistaList;
    }

    public void setComissionadoList(List<Comissionado> comissionadoList) {
        this.comissionadoList = comissionadoList;
    }

    public void setSalarioBrutoHorista(Double salarioBrutoHorista) {
        this.salarioBrutoHorista = salarioBrutoHorista;
    }

    public void setSalarioBrutoAssalariado(Double salarioBrutoAssalariado) {
        this.salarioBrutoAssalariado = salarioBrutoAssalariado;
    }

    public void setSalarioBrutoComissinado(Double salarioBrutoComissinado) {
        this.salarioBrutoComissinado = salarioBrutoComissinado;
    }
}
