package modelo;

/**
 * Classe que representa um veiculo da frota.
 */
public class Veiculo {
    
    private String placa;        
    private String marca;  
    private String modelo; 

    public Veiculo() {
        this.placa = "";
        this.marca = "";
        this.modelo = "";
    }
   
    public Veiculo(String placa, String marca, String modelo) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    @Override
    public String toString() {
        return "Veiculo{" +
                "placa='" + placa + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Veiculo veiculo = (Veiculo) obj;
        return placa != null && placa.equals(veiculo.placa);
    }

    @Override
    public int hashCode() {
        return placa != null ? placa.hashCode() : 0;
    }
    
    /**
     * Valida se a placa tem formato valido (formato antigo ou Mercosul).
     * 
     * @return true se a placa e valida
     */
    public boolean isPlacaValida() {
        if (placa == null || placa.isEmpty()) return false;
        
        // Formato antigo: ABC-1234 ou ABC1234
        // Formato Mercosul: ABC1D23 ou ABC-1D23
        String placaSemHifen = placa.replace("-", "");
        
        if (placaSemHifen.length() != 7) return false;
        
        // Formato antigo: 3 letras + 4 numeros
        boolean formatoAntigo = placaSemHifen.matches("[A-Z]{3}[0-9]{4}");
        
        // Formato Mercosul: 3 letras + 1 numero + 1 letra + 2 numeros
        boolean formatoMercosul = placaSemHifen.matches("[A-Z]{3}[0-9][A-Z][0-9]{2}");
        
        return formatoAntigo || formatoMercosul;
    }
    
    /**
     * Retorna uma representacao amigavel para exibicao.
     * 
     * @return String formatada
     */
    public String getDisplayInfo() {
        return placa + " - " + marca + " " + modelo;
    }
}
