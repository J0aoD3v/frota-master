package excecoes;

/**
 * Excecao lancada quando ha problemas com autenticacao.
 */
public class AutenticacaoException extends Exception {
    
    public AutenticacaoException(String mensagem) {
        super(mensagem);
    }
    
    public AutenticacaoException(String mensagem, Throwable cause) {
        super(mensagem, cause);
    }
}
