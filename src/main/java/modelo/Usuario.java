package modelo;

/**
 * Classe que representa um usuario (operador) do sistema.
 * Responsavel por autenticar e controlar o acesso ao sistema.
 */
public class Usuario {
    
    private int codigo;
    private String nome;
    private String login;
    private String senha;

    /**
     * Construtor padrao.
     */
    public Usuario() {
        this.codigo = 0;
        this.nome = "";
        this.login = "";
        this.senha = "";
    }

    /**
     * Construtor com parametros.
     * 
     * @param codigo Codigo unico do usuario
     * @param nome Nome completo do usuario
     * @param login Login para acesso ao sistema
     * @param senha Senha para autenticacao
     */
    public Usuario(int codigo, String nome, String login, String senha) {
        this.codigo = codigo;
        this.nome = nome;
        this.login = login;
        this.senha = senha;
    }

    // Getters e Setters
    
    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                ", login='" + login + '\'' +
                ", senha='***'" + // Nao exibir senha por seguranca
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Usuario usuario = (Usuario) obj;
        return codigo == usuario.codigo;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(codigo);
    }
    
    /**
     * Valida se o login e senha estao corretos.
     * 
     * @param login Login informado
     * @param senha Senha informada
     * @return true se as credenciais estao corretas
     */
    public boolean autenticar(String login, String senha) {
        return this.login.equals(login) && this.senha.equals(senha);
    }
}