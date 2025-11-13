package excecoes;

/**
 * Excecao lancada quando se tenta cadastrar um veiculo com placa ja existente.
 */
public class VeiculoDuplicadoException extends Exception {
    
    public VeiculoDuplicadoException(String placa) {
        super("Ja existe um veiculo cadastrado com a placa: " + placa);
    }
    
    public VeiculoDuplicadoException(String placa, Throwable cause) {
        super("Ja existe um veiculo cadastrado com a placa: " + placa, cause);
    }
}
