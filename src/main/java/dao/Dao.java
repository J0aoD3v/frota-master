package dao;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.DeleteResult;
import interfaces.IRepositorio;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

/**
 * Classe responsavel pela persistencia de objetos. 
 * Implementa IRepositorio para permitir polimorfismo.
 * 
 * @param <T> Parametro de tipo: classe do objeto a ser persistido. 
 */
public class Dao<T> implements IRepositorio<T> {

    private final String URI = "mongodb://admin:senha123@localhost:27017/";
    private final String DATABASE = "veiculos";   // Nome do banco conforme especificacao do projeto 
    private final MongoClient mongoClient;
    private final MongoDatabase database; 
    private final String colecao;  // nome da colecao 
    private final MongoCollection<T> collection; 
    
    public Dao(Class<T> classe){
        this.colecao = classe.getSimpleName().toLowerCase(); 
        mongoClient = MongoClients.create(URI);
        CodecRegistry pojoCodecRegistry = org.bson.codecs.configuration.
                CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), 
                        org.bson.codecs.configuration.CodecRegistries.
                                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        database = mongoClient.getDatabase(DATABASE).withCodecRegistry(pojoCodecRegistry);  
        collection = database.getCollection(colecao, classe); 
    }

    public void fecharConexao() {
        mongoClient.close();
    }

    /**
     * 
     * @param chave O nome do atributo pelo qual o objeto vai ser buscado, ex: codigo. 
     * @param valor O valor do atributo identificador do objeto a ser alterado, exemplo: 20 (vai buscar o objeto cujo codigo seja 20).
     * @param novo O objeto com os novos valores que devem substituir os antigos. 
     */
    public void alterar(String chave, String valor, T novo){
        // Converter valor para tipo correto se for numero
        Object valorConvertido = valor;
        try {
            // Tenta converter para inteiro se for campo codigo
            if (chave.equals("codigo")) {
                valorConvertido = Integer.parseInt(valor);
            }
        } catch (NumberFormatException e) {
            // Se falhar, mantem como String
            valorConvertido = valor;
        }
        
        // Executar a atualizacao
        Document filtro = new Document(chave, valorConvertido);
        com.mongodb.client.result.UpdateResult resultado = collection.replaceOne(filtro, novo);
        
        // Debug: verificar se a atualizacao funcionou
        if (resultado.getMatchedCount() == 0) {
            System.err.println("AVISO: Nenhum documento encontrado para alterar com " + chave + " = " + valorConvertido);
        }
    }
    
    
    /**
     * Apaga um objeto no banco. 
     * @param chave O nome do atributo pelo qual o objeto vai ser buscado, ex: codigo.
     * @param valor O valor do atributo identificador do objeto a ser alterado, exemplo: 20 (vai excluir o objeto cujo codigo seja 20).
     * @return True se um objeto foi excluido ou false caso contrario. 
     */
    public boolean excluir(String chave, String valor){
        // Converter valor para tipo correto se for numero
        Object valorConvertido = valor;
        try {
            // Tenta converter para inteiro se for campo codigo
            if (chave.equals("codigo")) {
                valorConvertido = Integer.parseInt(valor);
            }
        } catch (NumberFormatException e) {
            // Se falhar, mantem como String
            valorConvertido = valor;
        }
        Document filter = new Document(chave, valorConvertido);
        DeleteResult result = collection.deleteOne(filter);
        return result.getDeletedCount() > 0;
    }
    
    /**
     * Retorna o objeto cuja chave for igual ao valor passado. 
     * @param chave o campo pelo qual o objeto vai ser buscado
     * @param valor o valor da chave
     * @return O objeto correspondente a chave ou null caso nao exista. 
     */
    public T buscarPorChave(String chave, Object valor){ 
         return collection.find(new Document(chave, valor)).first();
    }
    
    public void inserir(T objeto){       
        collection.insertOne(objeto); 
    }
    
    
    /**
     * Retorna todos os objetos de uma colecao do tipo T. 
     * @return 
     */
    public List<T> listarTodos(){
        ArrayList<T> todos = new ArrayList();          
        try (MongoCursor<T> cursor = collection.find().iterator()) {
            while(cursor.hasNext()){
                todos.add(cursor.next());
            }
        }
        return todos; 
    }
    
    
    /**
     * Retorna os objetos que atendam um deteminado criterio, por exemplo, veiculos com determinada placa. 
     * @param campoDaColecao: o nome do atributo do objeto. Exemplo: "nome"
     * @param criterio: o valor do atributo. Exemplo: "Gasparzinho".
     * @return uma lista de todos os objetos que atendam ao criterio. 
     */
    public List<T> filtrar(String campoDaColecao, String criterio) {
        Bson filtro = Filters.eq(campoDaColecao, criterio);
        FindIterable<T> resultados = collection.find(filtro);
        // converte em List/ArrayList        
        List<T>  retorno = new ArrayList();
        resultados.into(retorno);
        return retorno;
    }
    
    /**
     * Busca utilizacoes por data especifica.
     * 
     * @param campo Nome do campo de data ("dataRetirada" ou "dataDevolucao")
     * @param dataInicio Data/hora de inicio do periodo
     * @param dataFim Data/hora de fim do periodo
     * @return Lista de objetos no periodo especificado
     */
    public List<T> buscarPorPeriodo(String campo, LocalDateTime dataInicio, LocalDateTime dataFim) {
        Bson filtro = Filters.and(
            Filters.gte(campo, dataInicio),
            Filters.lte(campo, dataFim)
        );
        FindIterable<T> resultados = collection.find(filtro);
        List<T> retorno = new ArrayList<>();
        resultados.into(retorno);
        return retorno;
    }
    
    /**
     * Lista objetos ordenados por um campo especifico.
     * 
     * @param campoOrdenacao Campo para ordenar
     * @param crescente true para ordem crescente, false para decrescente
     * @return Lista ordenada
     */
    public List<T> listarOrdenado(String campoOrdenacao, boolean crescente) {
        ArrayList<T> todos = new ArrayList<>();
        Bson ordem = crescente ? Sorts.ascending(campoOrdenacao) : Sorts.descending(campoOrdenacao);
        
        try (MongoCursor<T> cursor = collection.find().sort(ordem).iterator()) {
            while(cursor.hasNext()){
                todos.add(cursor.next());
            }
        }
        return todos;
    }
    
    /**
     * Filtra e ordena objetos.
     * 
     * @param campoFiltro Campo para filtrar
     * @param valorFiltro Valor do filtro
     * @param campoOrdenacao Campo para ordenar
     * @param crescente true para ordem crescente
     * @return Lista filtrada e ordenada
     */
    public List<T> filtrarOrdenado(String campoFiltro, String valorFiltro, 
                                    String campoOrdenacao, boolean crescente) {
        Bson filtro = Filters.eq(campoFiltro, valorFiltro);
        Bson ordem = crescente ? Sorts.ascending(campoOrdenacao) : Sorts.descending(campoOrdenacao);
        
        FindIterable<T> resultados = collection.find(filtro).sort(ordem);
        List<T> retorno = new ArrayList<>();
        resultados.into(retorno);
        return retorno;
    }
    
    /**
     * Conta quantos documentos existem na colecao.
     * 
     * @return Numero de documentos
     */
    public long contar() {
        return collection.countDocuments();
    }
    
    /**
     * Conta quantos documentos atendem a um criterio.
     * 
     * @param campo Campo para filtrar
     * @param valor Valor do filtro
     * @return Numero de documentos que atendem ao criterio
     */
    public long contar(String campo, Object valor) {
        Bson filtro = Filters.eq(campo, valor);
        return collection.countDocuments(filtro);
    }
}
