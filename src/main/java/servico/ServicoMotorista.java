package servico;

import dao.Dao;
import excecoes.MotoristaInvalidoException;
import modelo.Motorista;
import java.util.List;

/**
 * Servico responsavel por gerenciar motoristas.
 * Implementa validacoes de negocio e tratamento de excecoes.
 */
public class ServicoMotorista {
    
    private final Dao<Motorista> dao;
    
    public ServicoMotorista() {
        this.dao = new Dao<>(Motorista.class);
    }
    
    /**
     * Cadastra um novo motorista no sistema.
     * 
     * @param motorista Motorista a ser cadastrado
     * @throws MotoristaInvalidoException Se dados forem invalidos ou CNH duplicada
     */
    public void cadastrar(Motorista motorista) throws MotoristaInvalidoException {
        // Validacoes
        if (motorista == null) {
            throw new IllegalArgumentException("Motorista nao pode ser nulo");
        }
        
        if (motorista.getNome() == null || motorista.getNome().trim().isEmpty()) {
            throw new MotoristaInvalidoException("Nome e obrigatorio");
        }
        
        if (motorista.getCnh() == null || motorista.getCnh().trim().isEmpty()) {
            throw new MotoristaInvalidoException("CNH e obrigatoria");
        }
        
        if (!motorista.isCnhValida()) {
            throw new MotoristaInvalidoException("CNH deve conter 11 digitos numericos");
        }
        
        if (motorista.getSetor() == null || motorista.getSetor().trim().isEmpty()) {
            throw new MotoristaInvalidoException("Setor e obrigatorio");
        }
        
        // Verifica se ja existe motorista com a mesma CNH
        Motorista existente = dao.buscarPorChave("cnh", motorista.getCnh());
        if (existente != null) {
            throw new MotoristaInvalidoException("Ja existe um motorista cadastrado com a CNH: " + motorista.getCnh());
        }
        
        // Se codigo nao foi definido, gerar automaticamente
        if (motorista.getCodigo() == 0) {
            motorista.setCodigo(gerarProximoCodigo());
        }
        
        dao.inserir(motorista);
    }
    
    /**
     * Atualiza dados de um motorista.
     * 
     * @param motorista Motorista com dados atualizados
     * @throws MotoristaInvalidoException Se dados invalidos ou CNH duplicada
     */
    public void atualizar(Motorista motorista) throws MotoristaInvalidoException {
        if (motorista == null || motorista.getCodigo() == 0) {
            throw new IllegalArgumentException("Motorista invalido para atualizacao");
        }
        
        // Buscar motorista original
        Motorista original = dao.buscarPorChave("codigo", motorista.getCodigo());
        if (original == null) {
            throw new MotoristaInvalidoException("Motorista nao encontrado");
        }
        
        // Validar CNH
        if (!motorista.isCnhValida()) {
            throw new MotoristaInvalidoException("CNH deve conter 11 digitos numericos");
        }
        
        // Se CNH foi alterada, verificar se nova CNH ja existe
        if (!original.getCnh().equals(motorista.getCnh())) {
            Motorista comMesmaCnh = dao.buscarPorChave("cnh", motorista.getCnh());
            if (comMesmaCnh != null) {
                throw new MotoristaInvalidoException("Ja existe um motorista com a CNH: " + motorista.getCnh());
            }
        }
        
        dao.alterar("codigo", String.valueOf(motorista.getCodigo()), motorista);
    }
    
    /**
     * Remove um motorista do sistema.
     * 
     * @param codigo Codigo do motorista
     * @return true se foi removido
     */
    public boolean remover(int codigo) {
        return dao.excluir("codigo", String.valueOf(codigo));
    }
    
    /**
     * Busca um motorista por codigo.
     * 
     * @param codigo Codigo do motorista
     * @return Motorista encontrado ou null
     */
    public Motorista buscarPorCodigo(int codigo) {
        return dao.buscarPorChave("codigo", codigo);
    }
    
    /**
     * Busca um motorista por CNH.
     * 
     * @param cnh CNH do motorista
     * @return Motorista encontrado ou null
     */
    public Motorista buscarPorCnh(String cnh) {
        return dao.buscarPorChave("cnh", cnh);
    }
    
    /**
     * Lista todos os motoristas cadastrados.
     * 
     * @return Lista de motoristas
     */
    public List<Motorista> listarTodos() {
        return dao.listarTodos();
    }
    
    /**
     * Lista motoristas de um setor especifico.
     * 
     * @param setor Setor para filtrar
     * @return Lista de motoristas do setor
     */
    public List<Motorista> listarPorSetor(String setor) {
        return dao.filtrar("setor", setor);
    }
    
    /**
     * Gera o proximo codigo disponivel para motorista.
     * 
     * @return Proximo codigo
     */
    private int gerarProximoCodigo() {
        List<Motorista> motoristas = dao.listarTodos();
        if (motoristas.isEmpty()) {
            return 1;
        }
        
        int maxCodigo = 0;
        for (Motorista m : motoristas) {
            if (m.getCodigo() > maxCodigo) {
                maxCodigo = m.getCodigo();
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
