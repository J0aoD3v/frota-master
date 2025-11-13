package excecoes;

/**
 * Excecao lancada quando ha problemas com operacoes de utilizacao de veiculos.
 */
public class UtilizacaoException extends Exception {
    
    public UtilizacaoException(String mensagem) {
        super(mensagem);
    }
    
    public UtilizacaoException(String mensagem, Throwable cause) {
        super(mensagem, cause);
    }
}
