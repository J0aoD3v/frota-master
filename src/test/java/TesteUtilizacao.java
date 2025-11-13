import modelo.*;
import servico.*;
import excecoes.*;

import java.time.LocalDateTime;

/**
 * Teste para verificar problema na devolucao de veiculos
 */
public class TesteUtilizacao {
    
    public static void main(String[] args) {
        System.out.println("=== TESTE DE UTILIZACOES - RETIRADA E DEVOLUCAO ===\n");
        
        ServicoVeiculo servicoVeiculo = new ServicoVeiculo();
        ServicoMotorista servicoMotorista = new ServicoMotorista();
        ServicoUsuario servicoUsuario = new ServicoUsuario();
        ServicoUtilizacao servicoUtilizacao = new ServicoUtilizacao();
        
        try {
            // 1. Criar dados de teste
            System.out.println("1. Preparando dados de teste...");
            
            // Criar veiculo
            Veiculo veiculo = new Veiculo("TST-9999", "TesteMarca", "TesteModelo");
            try {
                servicoVeiculo.remover("TST-9999");
            } catch (Exception e) {}
            servicoVeiculo.cadastrar(veiculo);
            System.out.println("   ✓ Veiculo criado: " + veiculo.getPlaca());
            
            // Criar motorista
            Motorista motorista = new Motorista(888, "Motorista Teste", "88888888888", "Setor Teste");
            try {
                servicoMotorista.remover(888);
            } catch (Exception e) {}
            servicoMotorista.cadastrar(motorista);
            System.out.println("   ✓ Motorista criado: " + motorista.getNome());
            
            // Criar usuario operador
            Usuario operador = new Usuario(888, "Operador Teste", "op.teste", "senha123");
            try {
                servicoUsuario.remover(888);
            } catch (Exception e) {}
            servicoUsuario.cadastrar(operador);
            System.out.println("   ✓ Operador criado: " + operador.getNome());
            
            // 2. Registrar RETIRADA
            System.out.println("\n2. Registrando retirada do veiculo...");
            Utilizacao utilizacao = servicoUtilizacao.registrarRetirada("TST-9999", 888, operador);
            System.out.println("   ✓ Retirada registrada! Codigo: " + utilizacao.getCodigo());
            System.out.println("   - Veiculo: " + utilizacao.getVeiculo().getPlaca());
            System.out.println("   - Motorista: " + utilizacao.getMotorista().getNome());
            System.out.println("   - Data: " + utilizacao.getDataRetirada());
            System.out.println("   - Em uso: " + utilizacao.isEmUso());
            
            // 3. Verificar se esta em uso
            System.out.println("\n3. Verificando se veiculo esta em uso...");
            boolean emUso = servicoUtilizacao.veiculoEmUso("TST-9999");
            System.out.println("   - Veiculo em uso: " + emUso);
            if (emUso) {
                System.out.println("   ✓ OK - Veiculo marcado como em uso");
            } else {
                System.out.println("   ✗ ERRO - Veiculo deveria estar em uso!");
            }
            
            // 4. Registrar DEVOLUCAO
            System.out.println("\n4. Registrando devolucao do veiculo...");
            servicoUtilizacao.registrarDevolucao("TST-9999", operador);
            System.out.println("   ✓ Devolucao registrada!");
            
            // 5. Verificar se devolucao foi salva
            System.out.println("\n5. Verificando se devolucao foi salva no BD...");
            Utilizacao utilizacaoVerificacao = servicoUtilizacao.buscarPorCodigo(utilizacao.getCodigo());
            
            if (utilizacaoVerificacao != null) {
                System.out.println("   - Codigo: " + utilizacaoVerificacao.getCodigo());
                System.out.println("   - Data Retirada: " + utilizacaoVerificacao.getDataRetirada());
                System.out.println("   - Data Devolucao: " + utilizacaoVerificacao.getDataDevolucao());
                System.out.println("   - Em uso: " + utilizacaoVerificacao.isEmUso());
                
                if (!utilizacaoVerificacao.isEmUso() && utilizacaoVerificacao.getDataDevolucao() != null) {
                    System.out.println("   ✓ DEVOLUCAO FUNCIONOU! Dados salvos no BD!");
                } else {
                    System.out.println("   ✗ ERRO! Devolucao NAO foi salva no BD!");
                    System.out.println("   - isEmUso: " + utilizacaoVerificacao.isEmUso());
                    System.out.println("   - dataDevolucao: " + utilizacaoVerificacao.getDataDevolucao());
                }
            } else {
                System.out.println("   ✗ ERRO - Utilizacao nao encontrada!");
            }
            
            // 6. Verificar novamente se esta em uso
            System.out.println("\n6. Verificando se veiculo ainda esta em uso...");
            emUso = servicoUtilizacao.veiculoEmUso("TST-9999");
            System.out.println("   - Veiculo em uso: " + emUso);
            if (!emUso) {
                System.out.println("   ✓ OK - Veiculo nao esta mais em uso");
            } else {
                System.out.println("   ✗ ERRO - Veiculo ainda esta marcado como em uso!");
            }
            
            // 7. Limpeza
            System.out.println("\n7. Limpando dados de teste...");
            servicoVeiculo.remover("TST-9999");
            servicoMotorista.remover(888);
            servicoUsuario.remover(888);
            System.out.println("   ✓ Dados removidos");
            
            System.out.println("\n=== TESTE CONCLUIDO ===");
            
        } catch (Exception e) {
            System.out.println("\n✗ ERRO: " + e.getMessage());
            e.printStackTrace();
        } finally {
            servicoVeiculo.fecharConexao();
            servicoMotorista.fecharConexao();
            servicoUsuario.fecharConexao();
            servicoUtilizacao.fecharConexao();
        }
    }
}
