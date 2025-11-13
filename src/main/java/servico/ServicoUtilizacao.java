package servico;

import dao.Dao;
import excecoes.UtilizacaoException;
import modelo.Motorista;
import modelo.Usuario;
import modelo.Utilizacao;
import modelo.Veiculo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Servico responsavel por gerenciar utilizacoes de veiculos.
 * Implementa as regras de negocio principais do sistema:
 * - Retirada de veiculos por operadores autenticados
 * - Devolucao de veiculos
 * - Consultas por data e placa
 */
public class ServicoUtilizacao {
    
    private final Dao<Utilizacao> dao;
    private final Dao<Veiculo> daoVeiculo;
    private final Dao<Motorista> daoMotorista;
    
    public ServicoUtilizacao() {
        this.dao = new Dao<>(Utilizacao.class);
        this.daoVeiculo = new Dao<>(Veiculo.class);
        this.daoMotorista = new Dao<>(Motorista.class);
    }
    
    /**
     * Registra a retirada de um veiculo.
     * REQUER: Operador autenticado (conforme especificacao)
     * 
     * @param placa Placa do veiculo
     * @param codigoMotorista Codigo do motorista
     * @param operador Usuario operador autenticado que esta fazendo o registro
     * @return Utilizacao criada
     * @throws UtilizacaoException Se houver problema na retirada
     */
    public Utilizacao registrarRetirada(String placa, int codigoMotorista, Usuario operador) 
            throws UtilizacaoException {
        
        // Validar operador (autenticacao ja foi feita, mas precisa do objeto)
        if (operador == null) {
            throw new UtilizacaoException("Operacao requer autenticacao de operador");
        }
        
        // Validar veiculo
        Veiculo veiculo = daoVeiculo.buscarPorChave("placa", placa);
        if (veiculo == null) {
            throw new UtilizacaoException("Veiculo nao encontrado: " + placa);
        }
        
        // Validar motorista
        Motorista motorista = daoMotorista.buscarPorChave("codigo", codigoMotorista);
        if (motorista == null) {
            throw new UtilizacaoException("Motorista nao encontrado: " + codigoMotorista);
        }
        
        // Verificar se o veiculo ja esta em uso
        if (veiculoEmUso(placa)) {
            throw new UtilizacaoException("Veiculo ja esta em uso: " + placa);
        }
        
        // Criar nova utilizacao
        int codigo = gerarProximoCodigo();
        LocalDateTime agora = LocalDateTime.now();
        
        Utilizacao utilizacao = new Utilizacao(codigo, veiculo, motorista, agora, operador);
        dao.inserir(utilizacao);
        
        return utilizacao;
    }
    
    /**
     * Registra a devolucao de um veiculo.
     * REQUER: Operador autenticado (conforme especificacao)
     * 
     * @param placa Placa do veiculo
     * @param operador Usuario operador autenticado que esta fazendo o registro
     * @throws UtilizacaoException Se houver problema na devolucao
     */
    public void registrarDevolucao(String placa, Usuario operador) throws UtilizacaoException {
        
        // Validar operador
        if (operador == null) {
            throw new UtilizacaoException("Operacao requer autenticacao de operador");
        }
        
        // Buscar utilizacao em aberto para este veiculo
        List<Utilizacao> utilizacoes = dao.listarTodos();
        Utilizacao utilizacaoAtual = null;
        
        for (Utilizacao u : utilizacoes) {
            if (u.getVeiculo() != null && 
                u.getVeiculo().getPlaca().equals(placa) && 
                u.isEmUso()) {
                utilizacaoAtual = u;
                break;
            }
        }
        
        if (utilizacaoAtual == null) {
            throw new UtilizacaoException("Nao ha utilizacao em aberto para o veiculo: " + placa);
        }
        
        // Registrar devolucao
        LocalDateTime agora = LocalDateTime.now();
        utilizacaoAtual.devolverVeiculo(agora, operador);
        
        // Atualizar no banco
        dao.alterar("codigo", String.valueOf(utilizacaoAtual.getCodigo()), utilizacaoAtual);
    }
    
    /**
     * Busca quem utilizou um veiculo em determinada data.
     * Especificacao item 7: "Deve ser possivel saber, a qualquer momento, 
     * quem utilizou o veiculo em determinada data"
     * 
     * @param placa Placa do veiculo
     * @param data Data para buscar
     * @return Lista de utilizacoes na data especificada
     */
    public List<Utilizacao> buscarUtilizacaoPorPlacaEData(String placa, LocalDate data) {
        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.atTime(LocalTime.MAX);
        
        // Buscar todas utilizacoes no periodo
        List<Utilizacao> todasDoPeriodo = dao.buscarPorPeriodo("dataRetirada", inicio, fim);
        
        // Filtrar por placa
        List<Utilizacao> resultado = new java.util.ArrayList<>();
        for (Utilizacao u : todasDoPeriodo) {
            if (u.getVeiculo() != null && u.getVeiculo().getPlaca().equals(placa)) {
                resultado.add(u);
            }
        }
        
        return resultado;
    }
    
    /**
     * Lista todas as utilizacoes de um veiculo por ordem crescente de data.
     * Especificacao item 8: "Deve ser possivel, dada uma placa, listar todas 
     * as utilizacoes, por ordem crescente"
     * 
     * @param placa Placa do veiculo
     * @return Lista ordenada de utilizacoes
     */
    public List<Utilizacao> listarUtilizacoesPorPlaca(String placa) {
        // Buscar todas utilizacoes
        List<Utilizacao> todas = dao.listarOrdenado("dataRetirada", true);
        
        // Filtrar por placa
        List<Utilizacao> resultado = new java.util.ArrayList<>();
        for (Utilizacao u : todas) {
            if (u.getVeiculo() != null && u.getVeiculo().getPlaca().equals(placa)) {
                resultado.add(u);
            }
        }
        
        return resultado;
    }
    
    /**
     * Lista todas as utilizacoes ordenadas por data.
     * 
     * @param crescente true para ordem crescente
     * @return Lista ordenada
     */
    public List<Utilizacao> listarTodasOrdenadas(boolean crescente) {
        return dao.listarOrdenado("dataRetirada", crescente);
    }
    
    /**
     * Lista todas as utilizacoes.
     * 
     * @return Lista de todas utilizacoes
     */
    public List<Utilizacao> listarTodas() {
        return dao.listarTodos();
    }
    
    /**
     * Lista utilizacoes em aberto (veiculos ainda em uso).
     * 
     * @return Lista de utilizacoes em aberto
     */
    public List<Utilizacao> listarEmAberto() {
        List<Utilizacao> todas = dao.listarTodos();
        List<Utilizacao> emAberto = new java.util.ArrayList<>();
        
        for (Utilizacao u : todas) {
            if (u.isEmUso()) {
                emAberto.add(u);
            }
        }
        
        return emAberto;
    }
    
    /**
     * Busca uma utilizacao especifica por codigo.
     * 
     * @param codigo Codigo da utilizacao
     * @return Utilizacao encontrada ou null
     */
    public Utilizacao buscarPorCodigo(int codigo) {
        return dao.buscarPorChave("codigo", codigo);
    }
    
    /**
     * Verifica se um veiculo esta em uso.
     * 
     * @param placa Placa do veiculo
     * @return true se esta em uso
     */
    public boolean veiculoEmUso(String placa) {
        List<Utilizacao> todas = dao.listarTodos();
        
        for (Utilizacao u : todas) {
            if (u.getVeiculo() != null && 
                u.getVeiculo().getPlaca().equals(placa) && 
                u.isEmUso()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Busca utilizacoes em um periodo de datas.
     * 
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Lista de utilizacoes no periodo
     */
    public List<Utilizacao> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(LocalTime.MAX);
        
        return dao.buscarPorPeriodo("dataRetirada", inicio, fim);
    }
    
    /**
     * Remove uma utilizacao do sistema.
     * 
     * @param codigo Codigo da utilizacao
     * @return true se foi removida
     */
    public boolean remover(int codigo) {
        return dao.excluir("codigo", String.valueOf(codigo));
    }
    
    /**
     * Gera o proximo codigo disponivel para utilizacao.
     * 
     * @return Proximo codigo
     */
    private int gerarProximoCodigo() {
        List<Utilizacao> utilizacoes = dao.listarTodos();
        if (utilizacoes.isEmpty()) {
            return 1;
        }
        
        int maxCodigo = 0;
        for (Utilizacao u : utilizacoes) {
            if (u.getCodigo() > maxCodigo) {
                maxCodigo = u.getCodigo();
            }
        }
        return maxCodigo + 1;
    }
    
    /**
     * Fecha a conexao com o banco.
     */
    public void fecharConexao() {
        dao.fecharConexao();
    }
}
