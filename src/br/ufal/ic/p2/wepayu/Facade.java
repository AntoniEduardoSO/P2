package br.ufal.ic.p2.wepayu;



import java.util.*;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.Exception.VerificarErroCartaoDePonto.*;
import br.ufal.ic.p2.wepayu.Exception.VerificarErros.*;
import br.ufal.ic.p2.wepayu.Exception.VerificarErrosLancaVendas.TipoInvalidoLancaVendasException;
import br.ufal.ic.p2.wepayu.models.*;

//import java.util.UUID;

public class Facade {
    private br.ufal.ic.p2.wepayu.System system = new System();


    public void zerarSistema(){
        this.system.zerarSistema();
    }

    public void encerrarSistema(){

    }


    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws ValidacaoException{
        return this.system.setEmpregado(nome, endereco, tipo, salario);
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws ValidacaoException {
        return this.system.setEmpregado(nome, endereco, tipo, salario, comissao);
    }

    public String getAtributoEmpregado(String id, String atributo) throws EmpregadoNaoExisteException, AtributoNaoExisteException, IdEmpregadoNuloException, EmpregadoNaoExisteNomeException{
        return this.system.getAtributoEmpregado(id, atributo);
    }

    public String getEmpregadoPorNome(String nome, Integer indice) throws EmpregadoNaoExisteNomeException{
        return this.system.getEmpregadoPorNome(nome, indice);
    }

    public void removerEmpregado(String id) throws EmpregadoNaoExisteException, IdEmpregadoNuloException, EmpregadoNaoExisteNomeException{
        this.system.removerEmpregado(id);
    }

    public String getHorasNormaisTrabalhadas(String id, String dataInicial, String dataFinal) throws DataInvalidaException, EmpregadoNaoExisteException, IdEmpregadoNuloException, TipoInvalidoCartaoDePontoException, DataInicialMaiorException, EmpregadoNaoExisteNomeException {
        return this.system.getHorasNormaisTrabalhadasCartaoDePonto(id, dataInicial, dataFinal);
    }

    public String getHorasExtrasTrabalhadas(String id, String dataInicial, String dataFinal) throws DataInvalidaException, EmpregadoNaoExisteException, IdEmpregadoNuloException, TipoInvalidoCartaoDePontoException, DataInicialMaiorException, EmpregadoNaoExisteNomeException{
        return this.system.getHorasExtrasTrabalhadasCartaoDePonto(id, dataInicial, dataFinal);
    }

    public void lancaCartao(String id, String data, String horas) throws EmpregadoNaoExisteException, IdEmpregadoNuloException, DataInvalidaException, TipoInvalidoCartaoDePontoException, HorasNulasException, EmpregadoNaoExisteNomeException {
        this.system.lancaCartao(id, data, horas);
    }

    public void lancaVenda(String id, String data, String valor) throws EmpregadoNaoExisteException, IdEmpregadoNuloException, DataInvalidaException, TipoInvalidoLancaVendasException, HorasNulasException, EmpregadoNaoExisteNomeException{
        this.system.lancaVenda(id,data,valor);
    }

    public String getVendasRealizadas(String id, String dataInicial, String dataFinal) throws DataInvalidaException, EmpregadoNaoExisteException, IdEmpregadoNuloException, TipoInvalidoLancaVendasException, DataInicialMaiorException, EmpregadoNaoExisteNomeException {
        return this.system.getVendasRealizadas(id,dataInicial,dataFinal);
    }

    public void alteraEmpregado(String id, String atributo, String valor) throws EmpregadoNaoExisteException, IdEmpregadoNuloException, EmpregadoNaoExisteNomeException{
        this.system.alteraEmpregado(id,atributo,valor);
    }
    public void alteraEmpregado(String id, String atributo, boolean valor, String idSindicato, String taxaSindical) throws EmpregadoNaoExisteException, IdEmpregadoNuloException, EmpregadoNaoExisteNomeException{
        this.system.alteraEmpregado(id,atributo,valor, idSindicato,taxaSindical);
    }
}
