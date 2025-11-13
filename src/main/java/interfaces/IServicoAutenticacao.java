package interfaces;

import modelo.Usuario;

/**
 * Interface para servicos de autenticacao.
 * Define o contrato para autenticacao de usuarios.
 */
public interface IServicoAutenticacao {
    
    /**
     * Autentica um usuario com login e senha.
     * 
     * @param login Login do usuario
     * @param senha Senha do usuario
     * @return Usuario autenticado ou null se credenciais invalidas
     * @throws Exception Se houver erro na autenticacao
     */
    Usuario autenticar(String login, String senha) throws Exception;
    
    /**
     * Verifica se um usuario esta autenticado.
     * 
     * @param usuario Usuario a verificar
     * @param senha Senha para validar
     * @return true se as credenciais sao validas
     */
    boolean validarCredenciais(Usuario usuario, String senha);
}
