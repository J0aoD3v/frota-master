import modelo.Usuario;
import servico.ServicoUsuario;
import excecoes.UsuarioDuplicadoException;

/**
 * Teste para verificar se as alteracoes estao funcionando corretamente no BD
 */
public class TesteAlteracoesBD {
    
    public static void main(String[] args) {
        System.out.println("=== TESTE DE ALTERACOES NO BANCO DE DADOS ===\n");
        
        ServicoUsuario servicoUsuario = new ServicoUsuario();
        
        try {
            // 1. INSERIR um usuario de teste
            System.out.println("1. Inserindo usuario de teste...");
            Usuario usuarioTeste = new Usuario(999, "Teste Alteracao", "teste.alt", "senha123");
            
            // Remover se ja existir
            try {
                servicoUsuario.remover(999);
                System.out.println("   - Usuario anterior removido");
            } catch (Exception e) {
                // Ignora se nao existir
            }
            
            servicoUsuario.cadastrar(usuarioTeste);
            System.out.println("   ✓ Usuario inserido com sucesso!");
            
            // 2. BUSCAR o usuario inserido
            System.out.println("\n2. Buscando usuario inserido...");
            Usuario usuarioBuscado = servicoUsuario.buscarPorCodigo(999);
            if (usuarioBuscado != null) {
                System.out.println("   ✓ Usuario encontrado: " + usuarioBuscado.getNome());
            } else {
                System.out.println("   ✗ ERRO: Usuario nao encontrado!");
                return;
            }
            
            // 3. ALTERAR o usuario
            System.out.println("\n3. Alterando dados do usuario...");
            usuarioBuscado.setNome("Teste Alteracao MODIFICADO");
            usuarioBuscado.setLogin("teste.modificado");
            servicoUsuario.atualizar(usuarioBuscado);
            System.out.println("   ✓ Usuario atualizado!");
            
            // 4. VERIFICAR se a alteracao foi salva
            System.out.println("\n4. Verificando se alteracao foi salva no BD...");
            Usuario usuarioVerificacao = servicoUsuario.buscarPorCodigo(999);
            if (usuarioVerificacao != null) {
                System.out.println("   - Nome no BD: " + usuarioVerificacao.getNome());
                System.out.println("   - Login no BD: " + usuarioVerificacao.getLogin());
                
                if (usuarioVerificacao.getNome().equals("Teste Alteracao MODIFICADO")) {
                    System.out.println("   ✓ ALTERACAO FUNCIONOU! Dados foram salvos no BD!");
                } else {
                    System.out.println("   ✗ ERRO: Alteracao NAO foi salva no BD!");
                }
            } else {
                System.out.println("   ✗ ERRO: Usuario nao encontrado apos alteracao!");
            }
            
            // 5. EXCLUIR o usuario de teste
            System.out.println("\n5. Removendo usuario de teste...");
            boolean removido = servicoUsuario.remover(999);
            if (removido) {
                System.out.println("   ✓ Usuario removido com sucesso!");
            } else {
                System.out.println("   ✗ ERRO: Nao foi possivel remover o usuario!");
            }
            
            // 6. VERIFICAR se foi realmente excluido
            System.out.println("\n6. Verificando exclusao...");
            Usuario usuarioExcluido = servicoUsuario.buscarPorCodigo(999);
            if (usuarioExcluido == null) {
                System.out.println("   ✓ EXCLUSAO FUNCIONOU! Usuario nao esta mais no BD!");
            } else {
                System.out.println("   ✗ ERRO: Usuario ainda existe no BD!");
            }
            
            System.out.println("\n=== TESTE CONCLUIDO ===");
            
        } catch (UsuarioDuplicadoException e) {
            System.out.println("ERRO: Usuario duplicado - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
            e.printStackTrace();
        } finally {
            servicoUsuario.fecharConexao();
        }
    }
}
