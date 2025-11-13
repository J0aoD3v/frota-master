package servico;

import dao.Dao;
import excecoes.VeiculoDuplicadoException;
import modelo.Veiculo;
import java.util.List;

/**
 * Servico responsavel por gerenciar veiculos.
 * Implementa validacoes de negocio e tratamento de excecoes.
 */
public class ServicoVeiculo {
    
    private final Dao<Veiculo> dao;
    
    public ServicoVeiculo() {
        this.dao = new Dao<>(Veiculo.class);
    }
    
    /**
     * Cadastra um novo veiculo no sistema.
     * 
     * @param veiculo Veiculo a ser cadastrado
     * @throws VeiculoDuplicadoException Se ja existe veiculo com a mesma placa
     */
    public void cadastrar(Veiculo veiculo) throws VeiculoDuplicadoException {
        // Validacoes
        if (veiculo == null) {
            throw new IllegalArgumentException("Veiculo nao pode ser nulo");
        }
        
        if (veiculo.getPlaca() == null || veiculo.getPlaca().trim().isEmpty()) {
            throw new IllegalArgumentException("Placa e obrigatoria");
        }
        
        if (veiculo.getMarca() == null || veiculo.getMarca().trim().isEmpty()) {
            throw new IllegalArgumentException("Marca e obrigatoria");
        }
        
        if (veiculo.getModelo() == null || veiculo.getModelo().trim().isEmpty()) {
            throw new IllegalArgumentException("Modelo e obrigatorio");
        }
        
        // Normalizar placa (uppercase e sem espacos)
        String placa = veiculo.getPlaca().trim().toUpperCase();
        veiculo.setPlaca(placa);
        
        if (!veiculo.isPlacaValida()) {
            throw new IllegalArgumentException("Placa invalida. Use formato ABC-1234 ou ABC1D23");
        }
        
        // Verifica se ja existe veiculo com a mesma placa
        Veiculo existente = dao.buscarPorChave("placa", veiculo.getPlaca());
        if (existente != null) {
            throw new VeiculoDuplicadoException(veiculo.getPlaca());
        }
        
        dao.inserir(veiculo);
    }
    
    /**
     * Atualiza dados de um veiculo.
     * 
     * @param veiculo Veiculo com dados atualizados
     * @throws VeiculoDuplicadoException Se a nova placa ja existe
     */
    public void atualizar(Veiculo veiculo) throws VeiculoDuplicadoException {
        if (veiculo == null || veiculo.getPlaca() == null || veiculo.getPlaca().isEmpty()) {
            throw new IllegalArgumentException("Veiculo invalido para atualizacao");
        }
        
        // Buscar veiculo original
        Veiculo original = dao.buscarPorChave("placa", veiculo.getPlaca());
        if (original == null) {
            throw new IllegalArgumentException("Veiculo nao encontrado");
        }
        
        if (!veiculo.isPlacaValida()) {
            throw new IllegalArgumentException("Placa invalida");
        }
        
        dao.alterar("placa", veiculo.getPlaca(), veiculo);
    }
    
    /**
     * Remove um veiculo do sistema.
     * 
     * @param placa Placa do veiculo
     * @return true se foi removido
     */
    public boolean remover(String placa) {
        return dao.excluir("placa", placa);
    }
    
    /**
     * Busca um veiculo por placa.
     * 
     * @param placa Placa do veiculo
     * @return Veiculo encontrado ou null
     */
    public Veiculo buscarPorPlaca(String placa) {
        return dao.buscarPorChave("placa", placa);
    }
    
    /**
     * Lista todos os veiculos cadastrados.
     * 
     * @return Lista de veiculos
     */
    public List<Veiculo> listarTodos() {
        return dao.listarTodos();
    }
    
    /**
     * Lista veiculos ordenados por placa.
     * 
     * @param crescente true para ordem crescente
     * @return Lista ordenada de veiculos
     */
    public List<Veiculo> listarOrdenadoPorPlaca(boolean crescente) {
        return dao.listarOrdenado("placa", crescente);
    }
    
    /**
     * Lista veiculos de uma marca especifica.
     * 
     * @param marca Marca para filtrar
     * @return Lista de veiculos da marca
     */
    public List<Veiculo> listarPorMarca(String marca) {
        return dao.filtrar("marca", marca);
    }
    
    /**
     * Fecha a conexao com o banco.
     */
    public void fecharConexao() {
        dao.fecharConexao();
    }
}
