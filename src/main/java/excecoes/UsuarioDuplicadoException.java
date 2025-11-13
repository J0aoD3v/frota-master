package excecoes;

/**
 * Excecao lancada quando se tenta cadastrar um usuario com login ja existente.
 */
public class UsuarioDuplicadoException extends Exception {
    
    public UsuarioDuplicadoException(String login) {
        super("Ja existe um usuario cadastrado com o login: " + login);
    }
    
    public UsuarioDuplicadoException(String login, Throwable cause) {
        super("Ja existe um usuario cadastrado com o login: " + login, cause);
    }
}
