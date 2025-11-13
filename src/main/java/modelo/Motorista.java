package modelo;

/**
 * Classe que representa um motorista autorizado a utilizar os veiculos da frota.
 */
public class Motorista {
    
    private int codigo;
    private String nome;
    private String cnh;
    private String setor;

    /**
     * Construtor padrao.
     */
    public Motorista() {
        this.codigo = 0;
        this.nome = "";
        this.cnh = "";
        this.setor = "";
    }

    /**
     * Construtor com parametros.
     * 
     * @param codigo Codigo unico do motorista
     * @param nome Nome completo do motorista
     * @param cnh Numero da Carteira Nacional de Habilitacao
     * @param setor Setor de trabalho (ex: direcao, hospital, fazenda escola)
     */
    public Motorista(int codigo, String nome, String cnh, String setor) {
        this.codigo = codigo;
        this.nome = nome;
        this.cnh = cnh;
        this.setor = setor;
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

    public String getCnh() {
        return cnh;
    }

    public void setCnh(String cnh) {
        this.cnh = cnh;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    @Override
    public String toString() {
        return "Motorista{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                ", cnh='" + cnh + '\'' +
                ", setor='" + setor + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Motorista motorista = (Motorista) obj;
        return codigo == motorista.codigo;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(codigo);
    }
    
    /**
     * Valida se a CNH e valida (formato basico).
     * 
     * @return true se a CNH tem formato valido
     */
    public boolean isCnhValida() {
        return cnh != null && cnh.length() == 11 && cnh.matches("\\d{11}");
    }
    
    /**
     * Retorna uma representacao amigavel para exibicao em listas.
     * 
     * @return String formatada com codigo e nome
     */
    public String getDisplayName() {
        return codigo + " - " + nome + " (" + setor + ")";
    }
}