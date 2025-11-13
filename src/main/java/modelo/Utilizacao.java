package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe que representa um registro de utilizacao de veiculo.
 * Controla quando um veiculo foi retirado e devolvido, e por qual motorista.
 */
public class Utilizacao {
    
    private int codigo;
    private Veiculo veiculo;
    private Motorista motorista;
    private LocalDateTime dataRetirada;
    private LocalDateTime dataDevolucao;
    private Usuario operadorRetirada;  // Operador que registrou a retirada
    private Usuario operadorDevolucao; // Operador que registrou a devolucao

    /**
     * Construtor padrao.
     */
    public Utilizacao() {
        this.codigo = 0;
        this.veiculo = null;
        this.motorista = null;
        this.dataRetirada = null;
        this.dataDevolucao = null;
        this.operadorRetirada = null;
        this.operadorDevolucao = null;
    }

    /**
     * Construtor para nova utilizacao (apenas retirada).
     * 
     * @param codigo Codigo unico da utilizacao
     * @param veiculo Veiculo que esta sendo utilizado
     * @param motorista Motorista responsavel
     * @param dataRetirada Data e hora da retirada
     * @param operadorRetirada Operador que registrou a retirada
     */
    public Utilizacao(int codigo, Veiculo veiculo, Motorista motorista, 
                     LocalDateTime dataRetirada, Usuario operadorRetirada) {
        this.codigo = codigo;
        this.veiculo = veiculo;
        this.motorista = motorista;
        this.dataRetirada = dataRetirada;
        this.dataDevolucao = null; // Sera preenchida na devolucao
        this.operadorRetirada = operadorRetirada;
        this.operadorDevolucao = null;
    }

    /**
     * Construtor completo (com devolucao).
     * 
     * @param codigo Codigo unico da utilizacao
     * @param veiculo Veiculo que foi utilizado
     * @param motorista Motorista responsavel
     * @param dataRetirada Data e hora da retirada
     * @param dataDevolucao Data e hora da devolucao
     * @param operadorRetirada Operador que registrou a retirada
     * @param operadorDevolucao Operador que registrou a devolucao
     */
    public Utilizacao(int codigo, Veiculo veiculo, Motorista motorista, 
                     LocalDateTime dataRetirada, LocalDateTime dataDevolucao,
                     Usuario operadorRetirada, Usuario operadorDevolucao) {
        this.codigo = codigo;
        this.veiculo = veiculo;
        this.motorista = motorista;
        this.dataRetirada = dataRetirada;
        this.dataDevolucao = dataDevolucao;
        this.operadorRetirada = operadorRetirada;
        this.operadorDevolucao = operadorDevolucao;
    }

    // Getters e Setters
    
    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public Motorista getMotorista() {
        return motorista;
    }

    public void setMotorista(Motorista motorista) {
        this.motorista = motorista;
    }

    public LocalDateTime getDataRetirada() {
        return dataRetirada;
    }

    public void setDataRetirada(LocalDateTime dataRetirada) {
        this.dataRetirada = dataRetirada;
    }

    public LocalDateTime getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(LocalDateTime dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public Usuario getOperadorRetirada() {
        return operadorRetirada;
    }

    public void setOperadorRetirada(Usuario operadorRetirada) {
        this.operadorRetirada = operadorRetirada;
    }

    public Usuario getOperadorDevolucao() {
        return operadorDevolucao;
    }

    public void setOperadorDevolucao(Usuario operadorDevolucao) {
        this.operadorDevolucao = operadorDevolucao;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        return "Utilizacao{" +
                "codigo=" + codigo +
                ", veiculo=" + (veiculo != null ? veiculo.getPlaca() : "null") +
                ", motorista=" + (motorista != null ? motorista.getNome() : "null") +
                ", dataRetirada=" + (dataRetirada != null ? dataRetirada.format(formatter) : "null") +
                ", dataDevolucao=" + (dataDevolucao != null ? dataDevolucao.format(formatter) : "Em uso") +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Utilizacao that = (Utilizacao) obj;
        return codigo == that.codigo;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(codigo);
    }
    
    /**
     * Verifica se o veiculo ainda esta em uso (nao foi devolvido).
     * 
     * @return true se o veiculo ainda esta em uso
     */
    public boolean isEmUso() {
        return dataDevolucao == null;
    }
    
    /**
     * Registra a devolucao do veiculo.
     * 
     * @param dataDevolucao Data e hora da devolucao
     * @param operadorDevolucao Operador que registrou a devolucao
     */
    public void devolverVeiculo(LocalDateTime dataDevolucao, Usuario operadorDevolucao) {
        this.dataDevolucao = dataDevolucao;
        this.operadorDevolucao = operadorDevolucao;
    }
    
    /**
     * Calcula o tempo de utilizacao em horas.
     * 
     * @return Horas de utilizacao, ou -1 se ainda em uso
     */
    public long getTempoUtilizacaoHoras() {
        if (dataRetirada == null) return -1;
        
        LocalDateTime fim = dataDevolucao != null ? dataDevolucao : LocalDateTime.now();
        return java.time.Duration.between(dataRetirada, fim).toHours();
    }
    
    /**
     * Retorna informacoes formatadas para exibicao.
     * 
     * @return String formatada para interface
     */
    public String getDisplayInfo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        String info = String.format("Cod: %d | Veiculo: %s | Motorista: %s | Retirada: %s",
                codigo,
                veiculo != null ? veiculo.getPlaca() : "N/A",
                motorista != null ? motorista.getNome() : "N/A",
                dataRetirada != null ? dataRetirada.format(formatter) : "N/A");
        
        if (dataDevolucao != null) {
            info += " | Devolucao: " + dataDevolucao.format(formatter);
        } else {
            info += " | Status: EM USO";
        }
        
        return info;
    }
}