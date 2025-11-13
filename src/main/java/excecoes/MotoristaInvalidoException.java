package excecoes;

/**
 * Excecao lancada quando se tenta cadastrar um motorista com dados invalidos.
 */
public class MotoristaInvalidoException extends Exception {
    
    public MotoristaInvalidoException(String mensagem) {
        super(mensagem);
    }
    
    public MotoristaInvalidoException(String mensagem, Throwable cause) {
        super(mensagem, cause);
    }
}
