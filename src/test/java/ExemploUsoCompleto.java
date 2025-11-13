import excecoes.*;
import modelo.*;
import servico.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Demonstracao pratica de uso do sistema de gerenciamento de frota.
 * Este arquivo mostra como usar todos os servicos implementados.
 */
public class ExemploUsoCompleto {
    
    public static void main(String[] args) {
        System.out.println("=== DEMONSTRACAO DO SISTEMA DE FROTA ===\n");
        
        try {
            // Inicializar servicos
            ServicoUsuario servicoUsuario = new ServicoUsuario();
            ServicoMotorista servicoMotorista = new ServicoMotorista();
            ServicoVeiculo servicoVeiculo = new ServicoVeiculo();
            ServicoUtilizacao servicoUtilizacao = new ServicoUtilizacao();
            
            // ========== PASSO 1: CADASTRAR OPERADOR DO SISTEMA ==========
            System.out.println("📋 PASSO 1: Cadastrando operador do sistema...");
            
            Usuario operador = new Usuario(1, "Maria Silva", "maria.silva", "senha123");
            try {
                servicoUsuario.cadastrar(operador);
                System.out.println("   ✅ Operador cadastrado: " + operador.getNome());
            } catch (UsuarioDuplicadoException e) {
                System.out.println("   ⚠️ Operador ja existe, usando existente");
                operador = servicoUsuario.buscarPorCodigo(1);
            }
            
            // ========== PASSO 2: AUTENTICAR OPERADOR ==========
            System.out.println("\n🔐 PASSO 2: Autenticando operador...");
            
            try {
                Usuario autenticado = servicoUsuario.autenticar("maria.silva", "senha123");
                System.out.println("   ✅ Autenticado: " + autenticado.getNome());
                operador = autenticado; // Usar operador autenticado
            } catch (AutenticacaoException e) {
                System.err.println("   ❌ Falha na autenticacao: " + e.getMessage());
                return;
            }
            
            // ========== PASSO 3: CADASTRAR MOTORISTAS ==========
            System.out.println("\n🚗 PASSO 3: Cadastrando motoristas...");
            Motorista motorista1 = new Motorista(1, "Joao Santos", "12345678901", "Direcao");
            Motorista motorista2 = new Motorista(2, "Ana Costa", "98765432109", "Hospital");
            
            try {
                servicoMotorista.cadastrar(motorista1);
                System.out.println("   ✅ Motorista cadastrado: " + motorista1.getDisplayName());
            } catch (MotoristaInvalidoException e) {
                System.out.println("   ⚠️ Motorista 1 ja existe");
            }
            
            try {
                servicoMotorista.cadastrar(motorista2);
                System.out.println("   ✅ Motorista cadastrado: " + motorista2.getDisplayName());
            } catch (MotoristaInvalidoException e) {
                System.out.println("   ⚠️ Motorista 2 ja existe");
            }
            
            // ========== PASSO 4: CADASTRAR VEICULOS ==========
            System.out.println("\n🚙 PASSO 4: Cadastrando veiculos...");
            
            Veiculo veiculo1 = new Veiculo("ABC-1234", "Toyota", "Corolla");
            Veiculo veiculo2 = new Veiculo("XYZ-5678", "Honda", "Civic");
            
            try {
                servicoVeiculo.cadastrar(veiculo1);
                System.out.println("   ✅ Veiculo cadastrado: " + veiculo1.getDisplayInfo());
            } catch (VeiculoDuplicadoException e) {
                System.out.println("   ⚠️ Veiculo 1 ja existe");
            }
            
            try {
                servicoVeiculo.cadastrar(veiculo2);
                System.out.println("   ✅ Veiculo cadastrado: " + veiculo2.getDisplayInfo());
            } catch (VeiculoDuplicadoException e) {
                System.out.println("   ⚠️ Veiculo 2 ja existe");
            }
            
            // ========== PASSO 5: REGISTRAR RETIRADA DE VEICULO ==========
            System.out.println("\n🔑 PASSO 5: Registrando retirada de veiculo...");
            System.out.println("   Operador: " + operador.getNome());
            System.out.println("   Veiculo: " + veiculo1.getPlaca());
            System.out.println("   Motorista: " + motorista1.getNome());
            
            try {
                // Verificar se ja esta em uso
                if (servicoUtilizacao.veiculoEmUso("ABC-1234")) {
                    System.out.println("   ⚠️ Veiculo ja esta em uso, devolvendo primeiro...");
                    servicoUtilizacao.registrarDevolucao("ABC-1234", operador);
                }
                
                Utilizacao utilizacao1 = servicoUtilizacao.registrarRetirada(
                    "ABC-1234",      // placa
                    1,               // codigo do motorista
                    operador         // operador autenticado
                );
                
                System.out.println("   ✅ Retirada registrada!");
                System.out.println("      Codigo utilizacao: " + utilizacao1.getCodigo());
                System.out.println("      Data/hora: " + utilizacao1.getDataRetirada());
                System.out.println("      Status: " + (utilizacao1.isEmUso() ? "EM USO" : "DEVOLVIDO"));
                
            } catch (UtilizacaoException e) {
                System.err.println("   ❌ Erro: " + e.getMessage());
            }
            
            // ========== PASSO 6: LISTAR VEICULOS EM USO ==========
            System.out.println("\n📊 PASSO 6: Listando veiculos em uso...");
            
            List<Utilizacao> emAberto = servicoUtilizacao.listarEmAberto();
            System.out.println("   Total em uso: " + emAberto.size());
            
            for (Utilizacao u : emAberto) {
                System.out.println("   - " + u.getDisplayInfo());
            }
            
            // ========== PASSO 7: REGISTRAR DEVOLUCAO ==========
            System.out.println("\n🔙 PASSO 7: Registrando devolucao de veiculo...");
            
            // Aguardar 2 segundos para simular tempo de uso
            System.out.println("   ⏳ Aguardando 2 segundos (simulando uso do veiculo)...");
            Thread.sleep(2000);
            
            try {
                servicoUtilizacao.registrarDevolucao("ABC-1234", operador);
                System.out.println("   ✅ Devolucao registrada!");
                
                // Verificar se foi devolvido
                boolean emUso = servicoUtilizacao.veiculoEmUso("ABC-1234");
                System.out.println("   Status do veiculo: " + (emUso ? "EM USO" : "DISPONIVEL"));
                
            } catch (UtilizacaoException e) {
                System.err.println("   ❌ Erro: " + e.getMessage());
            }
            
            // ========== PASSO 8: CONSULTAR UTILIZACOES POR PLACA ==========
            System.out.println("\n🔍 PASSO 8: Consultando utilizacoes por placa (ordenado crescente)...");
            
            List<Utilizacao> utilizacoesPlaca = servicoUtilizacao.listarUtilizacoesPorPlaca("ABC-1234");
            System.out.println("   Total de utilizacoes: " + utilizacoesPlaca.size());
            
            for (int i = 0; i < Math.min(5, utilizacoesPlaca.size()); i++) {
                Utilizacao u = utilizacoesPlaca.get(i);
                System.out.println("   " + (i + 1) + ". " + u.getDisplayInfo());
            }
            
            // ========== PASSO 9: CONSULTAR QUEM USOU EM DETERMINADA DATA ==========
            System.out.println("\n📅 PASSO 9: Consultando quem usou veiculo hoje...");
            
            LocalDate hoje = LocalDate.now();
            List<Utilizacao> utilizacoesHoje = servicoUtilizacao
                .buscarUtilizacaoPorPlacaEData("ABC-1234", hoje);
            
            System.out.println("   Utilizacoes da placa ABC-1234 em " + hoje + ":");
            
            if (utilizacoesHoje.isEmpty()) {
                System.out.println("   Nenhuma utilizacao encontrada");
            } else {
                for (Utilizacao u : utilizacoesHoje) {
                    System.out.println("   - Motorista: " + u.getMotorista().getNome());
                    System.out.println("     Setor: " + u.getMotorista().getSetor());
                    System.out.println("     Operador retirada: " + u.getOperadorRetirada().getNome());
                    if (u.getOperadorDevolucao() != null) {
                        System.out.println("     Operador devolucao: " + u.getOperadorDevolucao().getNome());
                    }
                    System.out.println("     Horario: " + u.getDataRetirada() + 
                        " ate " + (u.getDataDevolucao() != null ? u.getDataDevolucao() : "EM USO"));
                }
            }
            
            // ========== PASSO 10: ESTATISTICAS ==========
            System.out.println("\n📈 PASSO 10: Estatisticas do sistema...");
            
            System.out.println("   Total de usuarios: " + servicoUsuario.listarTodos().size());
            System.out.println("   Total de motoristas: " + servicoMotorista.listarTodos().size());
            System.out.println("   Total de veiculos: " + servicoVeiculo.listarTodos().size());
            System.out.println("   Total de utilizacoes: " + servicoUtilizacao.listarTodas().size());
            System.out.println("   Veiculos em uso: " + servicoUtilizacao.listarEmAberto().size());
            
            // ========== VALIDACOES E SEGURANCA ==========
            System.out.println("\n🔒 DEMONSTRACAO DE VALIDACOES:");
            
            // Tentar cadastrar placa invalida
            System.out.println("\n   Tentando cadastrar veiculo com placa invalida...");
            try {
                Veiculo veiculoInvalido = new Veiculo("INVALIDO", "Teste", "Teste");
                servicoVeiculo.cadastrar(veiculoInvalido);
            } catch (Exception e) {
                System.out.println("   ✅ Validacao funcionou: " + e.getMessage());
            }
            
            // Tentar cadastrar CNH invalida
            System.out.println("\n   Tentando cadastrar motorista com CNH invalida...");
            try {
                Motorista motoristaInvalido = new Motorista(999, "Teste", "123", "Setor");
                servicoMotorista.cadastrar(motoristaInvalido);
            } catch (Exception e) {
                System.out.println("   ✅ Validacao funcionou: " + e.getMessage());
            }
            
            // Tentar autenticar com senha errada
            System.out.println("\n   Tentando autenticar com senha incorreta...");
            try {
                servicoUsuario.autenticar("maria.silva", "senhaErrada");
            } catch (Exception e) {
                System.out.println("   ✅ Validacao funcionou: " + e.getMessage());
            }
            
            // Tentar retirar veiculo sem autenticacao
            System.out.println("\n   Tentando registrar retirada sem operador autenticado...");
            try {
                servicoUtilizacao.registrarRetirada("ABC-1234", 1, null);
            } catch (Exception e) {
                System.out.println("   ✅ Validacao funcionou: " + e.getMessage());
            }
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("✅ DEMONSTRACAO CONCLUIDA COM SUCESSO!");
            System.out.println("=".repeat(60));
            
            // Fechar conexoes
            servicoUsuario.fecharConexao();
            servicoMotorista.fecharConexao();
            servicoVeiculo.fecharConexao();
            servicoUtilizacao.fecharConexao();
            
        } catch (Exception e) {
            System.err.println("\n❌ ERRO INESPERADO:");
            e.printStackTrace();
        }
    }
}
