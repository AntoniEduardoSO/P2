package br.ufal.ic.p2.wepayu.models;

import java.util.LinkedList;
import java.util.List;

public class FolhaDePonto {
    List<Assalariado> assalariadoList = new LinkedList<>();
    List<Horista> horistaList = new LinkedList<>();

    List<Comissionado> comissionadoList = new LinkedList<>();

    String salarioBrutoTotal;

//    public FolhaDePonto(String salarioBrutoTotal){
//        this.salarioBrutoTotal = salarioBrutoTotal;
//    }


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

    public void setHoristaList(Horista horista) {
        this.horistaList.add(horista) ;
    }

    public void setComissionadoList(List<Comissionado> comissionadoList) {
        this.comissionadoList = comissionadoList;
    }
}
