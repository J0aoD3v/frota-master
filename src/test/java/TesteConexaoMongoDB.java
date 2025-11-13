import dao.Dao;
import modelo.Usuario;
import modelo.Motorista;
import modelo.Veiculo;
import modelo.Utilizacao;
import java.time.LocalDateTime;

/**
 * Classe para testar a conexao e operacoes basicas com MongoDB.
 */
public class TesteConexaoMongoDB {
    
    public static void main(String[] args) {
        System.out.println("=== TESTE DE CONEXAO E OPERACOES COM MONGODB ===");
        System.out.println();
        
        try {
            // Teste 1: Conexao e operacoes com Usuarios
            testeUsuarios();
            
            // Teste 2: Conexao e operacoes com Motoristas
            testeMotoristas();
            
            // Teste 3: Conexao e operacoes com Veiculos
            testeVeiculos();
            
            // Teste 4: Conexao e operacoes com Utilizacoes
            testeUtilizacoes();
            
            System.out.println("✅ TODOS OS TESTES EXECUTADOS COM SUCESSO!");
            
        } catch (Exception e) {
            System.err.println("❌ ERRO NOS TESTES:");
            e.printStackTrace();
        }
    }
    
    private static void testeUsuarios() {
        System.out.println("🔵 Testando operacoes com Usuarios...");
        
        Dao<Usuario> daoUsuario = new Dao<>(Usuario.class);
        
        // Criar usuario de teste
        Usuario usuario = new Usuario(1, "Administrador", "admin", "123456");
        
        // Inserir
        daoUsuario.inserir(usuario);
        System.out.println("   ✅ Usuario inserido: " + usuario.getNome());
        
        // Listar
        var usuarios = daoUsuario.listarTodos();
        System.out.println("   ✅ Total de usuarios: " + usuarios.size());
        
        // Buscar
        var usuarioEncontrado = daoUsuario.buscarPorChave("codigo", 1);
        if (usuarioEncontrado != null) {
            System.out.println("   ✅ Usuario encontrado: " + usuarioEncontrado.getNome());
        }
        
        System.out.println();
    }
    
    private static void testeMotoristas() {
        System.out.println("🟢 Testando operacoes com Motoristas...");
        
        Dao<Motorista> daoMotorista = new Dao<>(Motorista.class);
        
        // Criar motorista de teste
        Motorista motorista = new Motorista(1, "Joao Silva", "12345678901", "Direcao");
        
        // Inserir
        daoMotorista.inserir(motorista);
        System.out.println("   ✅ Motorista inserido: " + motorista.getNome());
        
        // Listar
        var motoristas = daoMotorista.listarTodos();
        System.out.println("   ✅ Total de motoristas: " + motoristas.size());
        
        // Buscar
        var motoristaEncontrado = daoMotorista.buscarPorChave("codigo", 1);
        if (motoristaEncontrado != null) {
            System.out.println("   ✅ Motorista encontrado: " + motoristaEncontrado.getNome());
        }
        
        System.out.println();
    }
    
    private static void testeVeiculos() {
        System.out.println("🟡 Testando operacoes com Veiculos...");
        
        Dao<Veiculo> daoVeiculo = new Dao<>(Veiculo.class);
        
        // Criar veiculo de teste
        Veiculo veiculo = new Veiculo("ABC-1234", "Toyota", "Corolla");
        
        // Inserir
        daoVeiculo.inserir(veiculo);
        System.out.println("   ✅ Veiculo inserido: " + veiculo.getPlaca());
        
        // Listar
        var veiculos = daoVeiculo.listarTodos();
        System.out.println("   ✅ Total de veiculos: " + veiculos.size());
        
        // Buscar
        var veiculoEncontrado = daoVeiculo.buscarPorChave("placa", "ABC-1234");
        if (veiculoEncontrado != null) {
            System.out.println("   ✅ Veiculo encontrado: " + veiculoEncontrado.getPlaca());
        }
        
        System.out.println();
    }
    
    private static void testeUtilizacoes() {
        System.out.println("🟠 Testando operacoes com Utilizacoes...");
        
    Dao<Utilizacao> daoUtilizacao = new Dao<>(Utilizacao.class);
    Dao<Veiculo> daoVeiculo = new Dao<>(Veiculo.class);
    Dao<Motorista> daoMotorista = new Dao<>(Motorista.class);
    Dao<Usuario> daoUsuario = new Dao<>(Usuario.class);
        
        // Buscar veiculo e motorista existentes
        Veiculo veiculo = daoVeiculo.buscarPorChave("placa", "ABC-1234");
        Motorista motorista = daoMotorista.buscarPorChave("codigo", 1);
        
        Usuario operador = daoUsuario.buscarPorChave("codigo", 1);
        if (operador == null) {
            operador = new Usuario(1, "Administrador", "admin", "123456");
            daoUsuario.inserir(operador);
        }

        if (veiculo != null && motorista != null) {
            // Criar utilizacao de teste
            Utilizacao utilizacao = new Utilizacao(1, veiculo, motorista, LocalDateTime.now(), operador);
            
            // Inserir
            daoUtilizacao.inserir(utilizacao);
            System.out.println("   ✅ Utilizacao inserida - Codigo: " + utilizacao.getCodigo());
            
            // Listar
            var utilizacoes = daoUtilizacao.listarTodos();
            System.out.println("   ✅ Total de utilizacoes: " + utilizacoes.size());
            
            // Buscar
            var utilizacaoEncontrada = daoUtilizacao.buscarPorChave("codigo", 1);
            if (utilizacaoEncontrada != null) {
                System.out.println("   ✅ Utilizacao encontrada - Status: " + 
                    (utilizacaoEncontrada.isEmUso() ? "EM USO" : "DEVOLVIDO"));
            }
        } else {
            System.out.println("   ⚠️ Nao foi possivel criar utilizacao - veiculo ou motorista nao encontrado");
        }
        
        System.out.println();
    }
}