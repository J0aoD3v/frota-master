import dao.Dao;
import modelo.Usuario;
import modelo.Motorista;
import modelo.Veiculo;
import modelo.Utilizacao;

/**
 * Script para verificar dados salvos no MongoDB.
 */
public class VerificarDados {
    
    public static void main(String[] args) {
        System.out.println("=== VERIFICACAO DOS DADOS NO MONGODB ===");
        System.out.println();
        
        try {
            // Verificar usuarios
            Dao<Usuario> daoUsuario = new Dao<>(Usuario.class);
            var usuarios = daoUsuario.listarTodos();
            System.out.println("👥 USUARIOS CADASTRADOS (" + usuarios.size() + "):");
            usuarios.forEach(u -> System.out.println("   - " + u.toString()));
            System.out.println();
            
            // Verificar motoristas
            Dao<Motorista> daoMotorista = new Dao<>(Motorista.class);
            var motoristas = daoMotorista.listarTodos();
            System.out.println("🚗 MOTORISTAS CADASTRADOS (" + motoristas.size() + "):");
            motoristas.forEach(m -> System.out.println("   - " + m.toString()));
            System.out.println();
            
            // Verificar veiculos
            Dao<Veiculo> daoVeiculo = new Dao<>(Veiculo.class);
            var veiculos = daoVeiculo.listarTodos();
            System.out.println("🚙 VEICULOS CADASTRADOS (" + veiculos.size() + "):");
            veiculos.forEach(v -> System.out.println("   - " + v.toString()));
            System.out.println();
            
            // Verificar utilizacoes
            Dao<Utilizacao> daoUtilizacao = new Dao<>(Utilizacao.class);
            var utilizacoes = daoUtilizacao.listarTodos();
            System.out.println("📋 UTILIZAcoES REGISTRADAS (" + utilizacoes.size() + "):");
            utilizacoes.forEach(u -> System.out.println("   - " + u.getDisplayInfo()));
            System.out.println();
            
            System.out.println("✅ DADOS VERIFICADOS COM SUCESSO!");
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao verificar dados:");
            e.printStackTrace();
        }
    }
}