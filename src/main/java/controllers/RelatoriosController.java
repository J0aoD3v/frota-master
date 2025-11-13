package controllers;

import com.App;
import modelo.Utilizacao;
import servico.ServicoUtilizacao;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

/**
 * Controller para visualizar relatorios
 */
public class RelatoriosController implements Initializable {

    @FXML
    private TextField txtPlacaRelatorio;
    @FXML
    private TextField txtPlacaData;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextArea txtResultado;

    private ServicoUtilizacao servicoUtilizacao;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        servicoUtilizacao = new ServicoUtilizacao();
        txtResultado.setStyle("-fx-font-family: 'Courier New', monospace;");
    }

    @FXML
    private void buscarPorPlaca(ActionEvent event) {
        String placa = txtPlacaRelatorio.getText().trim().toUpperCase();
        
        if (placa.isEmpty()) {
            exibirAviso("Informe a placa do veiculo");
            return;
        }

        try {
            List<Utilizacao> utilizacoes = servicoUtilizacao.listarUtilizacoesPorPlaca(placa);
            
            StringBuilder resultado = new StringBuilder();
            resultado.append("=".repeat(80)).append("\n");
            resultado.append("RELATORIO DE UTILIZACOES - PLACA: ").append(placa).append("\n");
            resultado.append("=".repeat(80)).append("\n\n");
            
            if (utilizacoes.isEmpty()) {
                resultado.append("Nenhuma utilizacao encontrada para esta placa.\n");
            } else {
                resultado.append("Total de utilizacoes: ").append(utilizacoes.size()).append("\n\n");
                
                for (Utilizacao u : utilizacoes) {
                    resultado.append("-".repeat(80)).append("\n");
                    resultado.append("Codigo: ").append(u.getCodigo()).append("\n");
                    resultado.append("Motorista: ").append(u.getMotorista().getNome()).append("\n");
                    resultado.append("Retirada: ").append(u.getDataRetirada().format(formatter)).append("\n");
                    resultado.append("Devolucao: ");
                    if (u.getDataDevolucao() != null) {
                        resultado.append(u.getDataDevolucao().format(formatter));
                    } else {
                        resultado.append("EM USO");
                    }
                    resultado.append("\n");
                    resultado.append("Status: ").append(u.isEmUso() ? "EM USO" : "DEVOLVIDO").append("\n");
                }
            }
            
            resultado.append("=".repeat(80)).append("\n");
            txtResultado.setText(resultado.toString());
            
        } catch (Exception e) {
            exibirErro("Erro ao buscar utilizacoes", e.getMessage());
        }
    }

    @FXML
    private void buscarPorData(ActionEvent event) {
        String placa = txtPlacaData.getText().trim().toUpperCase();
        LocalDate data = datePicker.getValue();
        
        if (placa.isEmpty() || data == null) {
            exibirAviso("Informe a placa e a data");
            return;
        }

        try {
            List<Utilizacao> utilizacoes = servicoUtilizacao.buscarUtilizacaoPorPlacaEData(placa, data);
            
            StringBuilder resultado = new StringBuilder();
            resultado.append("=".repeat(80)).append("\n");
            resultado.append("RELATORIO DE UTILIZACOES - PLACA: ").append(placa);
            resultado.append(" - DATA: ").append(data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
            resultado.append("=".repeat(80)).append("\n\n");
            
            if (utilizacoes.isEmpty()) {
                resultado.append("Nenhuma utilizacao encontrada para esta placa nesta data.\n");
            } else {
                resultado.append("Total de utilizacoes: ").append(utilizacoes.size()).append("\n\n");
                
                for (Utilizacao u : utilizacoes) {
                    resultado.append("-".repeat(80)).append("\n");
                    resultado.append("Codigo: ").append(u.getCodigo()).append("\n");
                    resultado.append("Motorista: ").append(u.getMotorista().getNome()).append("\n");
                    resultado.append("CNH: ").append(u.getMotorista().getCnh()).append("\n");
                    resultado.append("Setor: ").append(u.getMotorista().getSetor()).append("\n");
                    resultado.append("Retirada: ").append(u.getDataRetirada().format(formatter)).append("\n");
                    resultado.append("Devolucao: ");
                    if (u.getDataDevolucao() != null) {
                        resultado.append(u.getDataDevolucao().format(formatter));
                    } else {
                        resultado.append("EM USO");
                    }
                    resultado.append("\n");
                    resultado.append("Status: ").append(u.isEmUso() ? "EM USO" : "DEVOLVIDO").append("\n");
                }
            }
            
            resultado.append("=".repeat(80)).append("\n");
            txtResultado.setText(resultado.toString());
            
        } catch (Exception e) {
            exibirErro("Erro ao buscar utilizacoes", e.getMessage());
        }
    }

    @FXML
    private void listarEmAberto(ActionEvent event) {
        try {
            List<Utilizacao> utilizacoes = servicoUtilizacao.listarEmAberto();
            
            StringBuilder resultado = new StringBuilder();
            resultado.append("=".repeat(80)).append("\n");
            resultado.append("RELATORIO DE VEICULOS EM USO\n");
            resultado.append("=".repeat(80)).append("\n\n");
            
            if (utilizacoes.isEmpty()) {
                resultado.append("Nenhum veiculo em uso no momento.\n");
            } else {
                resultado.append("Total de veiculos em uso: ").append(utilizacoes.size()).append("\n\n");
                
                for (Utilizacao u : utilizacoes) {
                    resultado.append("-".repeat(80)).append("\n");
                    resultado.append("Codigo: ").append(u.getCodigo()).append("\n");
                    resultado.append("Veiculo: ").append(u.getVeiculo().getPlaca());
                    resultado.append(" - ").append(u.getVeiculo().getMarca());
                    resultado.append(" ").append(u.getVeiculo().getModelo()).append("\n");
                    resultado.append("Motorista: ").append(u.getMotorista().getNome()).append("\n");
                    resultado.append("CNH: ").append(u.getMotorista().getCnh()).append("\n");
                    resultado.append("Setor: ").append(u.getMotorista().getSetor()).append("\n");
                    resultado.append("Retirada: ").append(u.getDataRetirada().format(formatter)).append("\n");
                }
            }
            
            resultado.append("=".repeat(80)).append("\n");
            txtResultado.setText(resultado.toString());
            
        } catch (Exception e) {
            exibirErro("Erro ao listar veiculos em uso", e.getMessage());
        }
    }

    @FXML
    private void listarTodos(ActionEvent event) {
        try {
            List<Utilizacao> utilizacoes = servicoUtilizacao.listarTodas();
            
            StringBuilder resultado = new StringBuilder();
            resultado.append("=".repeat(80)).append("\n");
            resultado.append("RELATORIO DE TODAS AS UTILIZACOES\n");
            resultado.append("=".repeat(80)).append("\n\n");
            
            if (utilizacoes.isEmpty()) {
                resultado.append("Nenhuma utilizacao registrada.\n");
            } else {
                resultado.append("Total de utilizacoes: ").append(utilizacoes.size()).append("\n\n");
                
                long emUso = utilizacoes.stream().filter(Utilizacao::isEmUso).count();
                long devolvidos = utilizacoes.size() - emUso;
                
                resultado.append("Em uso: ").append(emUso).append("\n");
                resultado.append("Devolvidos: ").append(devolvidos).append("\n\n");
                
                for (Utilizacao u : utilizacoes) {
                    resultado.append("-".repeat(80)).append("\n");
                    resultado.append("Codigo: ").append(u.getCodigo()).append("\n");
                    resultado.append("Veiculo: ").append(u.getVeiculo().getPlaca());
                    resultado.append(" - ").append(u.getVeiculo().getMarca());
                    resultado.append(" ").append(u.getVeiculo().getModelo()).append("\n");
                    resultado.append("Motorista: ").append(u.getMotorista().getNome()).append("\n");
                    resultado.append("Retirada: ").append(u.getDataRetirada().format(formatter)).append("\n");
                    resultado.append("Devolucao: ");
                    if (u.getDataDevolucao() != null) {
                        resultado.append(u.getDataDevolucao().format(formatter));
                    } else {
                        resultado.append("EM USO");
                    }
                    resultado.append("\n");
                    resultado.append("Status: ").append(u.isEmUso() ? "EM USO" : "DEVOLVIDO").append("\n");
                }
            }
            
            resultado.append("=".repeat(80)).append("\n");
            txtResultado.setText(resultado.toString());
            
        } catch (Exception e) {
            exibirErro("Erro ao listar utilizacoes", e.getMessage());
        }
    }

    @FXML
    private void voltarMenu(ActionEvent event) {
        try {
            servicoUtilizacao.fecharConexao();
            App.setRoot("Menu");
        } catch (IOException e) {
            exibirErro("Erro ao voltar ao menu", e.getMessage());
        }
    }

    private void exibirAviso(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    private void exibirErro(String titulo, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}
