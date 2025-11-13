package interfaces;

import java.util.List;

/**
 * Interface que define operacoes basicas de um repositorio generico.
 * Implementa o principio de abstracao e permite polimorfismo.
 * 
 * @param <T> Tipo da entidade gerenciada pelo repositorio
 */
public interface IRepositorio<T> {
    
    /**
     * Insere um novo objeto no repositorio.
     * 
     * @param objeto Objeto a ser inserido
     * @throws Exception Se houver erro na insercao
     */
    void inserir(T objeto) throws Exception;
    
    /**
     * Altera um objeto existente no repositorio.
     * 
     * @param chave Nome do campo identificador
     * @param valor Valor do campo identificador
     * @param novo Objeto com os novos valores
     * @throws Exception Se houver erro na alteracao
     */
    void alterar(String chave, String valor, T novo) throws Exception;
    
    /**
     * Exclui um objeto do repositorio.
     * 
     * @param chave Nome do campo identificador
     * @param valor Valor do campo identificador
     * @return true se o objeto foi excluido, false caso contrario
     * @throws Exception Se houver erro na exclusao
     */
    boolean excluir(String chave, String valor) throws Exception;
    
    /**
     * Busca um objeto pela chave.
     * 
     * @param chave Nome do campo identificador
     * @param valor Valor do campo identificador
     * @return Objeto encontrado ou null
     * @throws Exception Se houver erro na busca
     */
    T buscarPorChave(String chave, Object valor) throws Exception;
    
    /**
     * Lista todos os objetos do repositorio.
     * 
     * @return Lista com todos os objetos
     * @throws Exception Se houver erro na listagem
     */
    List<T> listarTodos() throws Exception;
    
    /**
     * Filtra objetos por um criterio especifico.
     * 
     * @param campo Nome do campo para filtrar
     * @param criterio Valor do criterio
     * @return Lista de objetos que atendem ao criterio
     * @throws Exception Se houver erro na filtragem
     */
    List<T> filtrar(String campo, String criterio) throws Exception;
}
