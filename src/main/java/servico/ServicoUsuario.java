package servico;

import dao.Dao;
import excecoes.UsuarioDuplicadoException;
import excecoes.AutenticacaoException;
import interfaces.IServicoAutenticacao;
import modelo.Usuario;
import java.util.List;

/**
 * Servico responsavel por gerenciar usuarios (operadores) do sistema.
 * Implementa validacoes de negocio e tratamento de excecoes.
 */
public class ServicoUsuario implements IServicoAutenticacao {
    
    private final Dao<Usuario> dao;
    
    public ServicoUsuario() {
        this.dao = new Dao<>(Usuario.class);
    }
    
    /**
     * Cadastra um novo usuario no sistema.
     * 
     * @param usuario Usuario a ser cadastrado
     * @throws UsuarioDuplicadoException Se ja existe usuario com o mesmo login
     * @throws IllegalArgumentException Se dados forem invalidos
     */
    public void cadastrar(Usuario usuario) throws UsuarioDuplicadoException {
        // Validacoes
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario nao pode ser nulo");
        }
        
        if (usuario.getLogin() == null || usuario.getLogin().trim().isEmpty()) {
            throw new IllegalArgumentException("Login e obrigatorio");
        }
        
        if (usuario.getSenha() == null || usuario.getSenha().length() < 4) {
            throw new IllegalArgumentException("Senha deve ter no minimo 4 caracteres");
        }
        
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome e obrigatorio");
        }
        
        // Verifica se ja existe usuario com o mesmo login
        Usuario existente = dao.buscarPorChave("login", usuario.getLogin());
        if (existente != null) {
            throw new UsuarioDuplicadoException(usuario.getLogin());
        }
        
        // Se codigo nao foi definido, gerar automaticamente
        if (usuario.getCodigo() == 0) {
            usuario.setCodigo(gerarProximoCodigo());
        }
        
        dao.inserir(usuario);
    }
    
    /**
     * Atualiza dados de um usuario.
     * 
     * @param usuario Usuario com dados atualizados
     * @throws UsuarioDuplicadoException Se o novo login ja existe
     */
    public void atualizar(Usuario usuario) throws UsuarioDuplicadoException {
        if (usuario == null || usuario.getCodigo() == 0) {
            throw new IllegalArgumentException("Usuario invalido para atualizacao");
        }
        
        // Buscar usuario original
        Usuario original = dao.buscarPorChave("codigo", usuario.getCodigo());
        if (original == null) {
            throw new IllegalArgumentException("Usuario nao encontrado");
        }
        
        // Se login foi alterado, verificar se novo login ja existe
        if (!original.getLogin().equals(usuario.getLogin())) {
            Usuario comMesmoLogin = dao.buscarPorChave("login", usuario.getLogin());
            if (comMesmoLogin != null) {
                throw new UsuarioDuplicadoException(usuario.getLogin());
            }
        }
        
        dao.alterar("codigo", String.valueOf(usuario.getCodigo()), usuario);
    }
    
    /**
     * Remove um usuario do sistema.
     * 
     * @param codigo Codigo do usuario
     * @return true se foi removido
     */
    public boolean remover(int codigo) {
        return dao.excluir("codigo", String.valueOf(codigo));
    }
    
    /**
     * Busca um usuario por codigo.
     * 
     * @param codigo Codigo do usuario
     * @return Usuario encontrado ou null
     */
    public Usuario buscarPorCodigo(int codigo) {
        return dao.buscarPorChave("codigo", codigo);
    }
    
    /**
     * Busca um usuario por login.
     * 
     * @param login Login do usuario
     * @return Usuario encontrado ou null
     */
    public Usuario buscarPorLogin(String login) {
        return dao.buscarPorChave("login", login);
    }
    
    /**
     * Lista todos os usuarios cadastrados.
     * 
     * @return Lista de usuarios
     */
    public List<Usuario> listarTodos() {
        return dao.listarTodos();
    }
    
    @Override
    public Usuario autenticar(String login, String senha) throws AutenticacaoException {
        if (login == null || login.trim().isEmpty()) {
            throw new AutenticacaoException("Login nao informado");
        }
        
        if (senha == null || senha.isEmpty()) {
            throw new AutenticacaoException("Senha nao informada");
        }
        
        Usuario usuario = dao.buscarPorChave("login", login);
        
        if (usuario == null) {
            throw new AutenticacaoException("Usuario nao encontrado");
        }
        
        if (!usuario.getSenha().equals(senha)) {
            throw new AutenticacaoException("Senha incorreta");
        }
        
        return usuario;
    }
    
    @Override
    public boolean validarCredenciais(Usuario usuario, String senha) {
        if (usuario == null || senha == null) {
            return false;
        }
        return usuario.getSenha().equals(senha);
    }
    
    /**
     * Gera o proximo codigo disponivel para usuario.
     * 
     * @return Proximo codigo
     */
    private int gerarProximoCodigo() {
        List<Usuario> usuarios = dao.listarTodos();
        if (usuarios.isEmpty()) {
            return 1;
        }
        
        int maxCodigo = 0;
        for (Usuario u : usuarios) {
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
