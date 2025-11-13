package controllers;

import com.App;
import excecoes.AutenticacaoException;
import excecoes.UtilizacaoException;
import modelo.Motorista;
import modelo.Usuario;
import modelo.Utilizacao;
import modelo.Veiculo;
import servico.ServicoMotorista;
import servico.ServicoUsuario;
import servico.ServicoUtilizacao;
import servico.ServicoVeiculo;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

/**
 * Controller para gerenciar utilizacoes
 */
public class UtilizacoesController implements Initializable {

    @FXML
    private ComboBox<Veiculo> comboVeiculo;
    @FXML
    private ComboBox<Motorista> comboMotorista;
    @FXML
    private TableView<Utilizacao> tabelaUtilizacoes;
    @FXML
    private TableColumn<Utilizacao, Integer> colCodigo;
    @FXML
    private TableColumn<Utilizacao, String> colVeiculo;
    @FXML
    private TableColumn<Utilizacao, String> colMotorista;
    @FXML
    private TableColumn<Utilizacao, String> colDataRetirada;
    @FXML
    private TableColumn<Utilizacao, String> colDataDevolucao;
    @FXML
    private TableColumn<Utilizacao, String> colStatus;

    private ServicoUtilizacao servicoUtilizacao;
    private ServicoVeiculo servicoVeiculo;
    private ServicoMotorista servicoMotorista;
    private ServicoUsuario servicoUsuario;
    private ObservableList<Utilizacao> listaUtilizacoes;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        servicoUtilizacao = new ServicoUtilizacao();
        servicoVeiculo = new ServicoVeiculo();
        servicoMotorista = new ServicoMotorista();
        servicoUsuario = new ServicoUsuario();
        listaUtilizacoes = FXCollections.observableArrayList();

        // Configurar colunas da tabela
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colVeiculo.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getVeiculo().getPlaca()));
        colMotorista.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMotorista().getNome()));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        colDataRetirada.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDataRetirada().format(formatter)));
        colDataDevolucao.setCellValueFactory(cellData -> {
            var devolucao = cellData.getValue().getDataDevolucao();
            return new javafx.beans.property.SimpleStringProperty(
                devolucao != null ? devolucao.format(formatter) : "-");
        });
        colStatus.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().isEmUso() ? "EM USO" : "DEVOLVIDO"));

        // Carregar dados
        carregarCombos();
        carregarDados();
    }

    private void carregarCombos() {
        try {
            // Carregar veiculos disponiveis
            var veiculos = servicoVeiculo.listarTodos();
            comboVeiculo.setItems(FXCollections.observableArrayList(veiculos));
            comboVeiculo.setConverter(new javafx.util.StringConverter<Veiculo>() {
                @Override
                public String toString(Veiculo veiculo) {
                    return veiculo != null ? veiculo.getPlaca() + " - " + veiculo.getMarca() + " " + veiculo.getModelo() : "";
                }
                @Override
                public Veiculo fromString(String string) {
                    return null;
                }
            });

            // Carregar motoristas
            var motoristas = servicoMotorista.listarTodos();
            comboMotorista.setItems(FXCollections.observableArrayList(motoristas));
            comboMotorista.setConverter(new javafx.util.StringConverter<Motorista>() {
                @Override
                public String toString(Motorista motorista) {
                    return motorista != null ? motorista.getNome() : "";
                }
                @Override
                public Motorista fromString(String string) {
                    return null;
                }
            });
        } catch (Exception e) {
            exibirErro("Erro ao carregar dados", e.getMessage());
        }
    }

    private void carregarDados() {
        try {
            listaUtilizacoes.clear();
            listaUtilizacoes.addAll(servicoUtilizacao.listarTodas());
            tabelaUtilizacoes.setItems(listaUtilizacoes);
        } catch (Exception e) {
            exibirErro("Erro ao carregar utilizacoes", e.getMessage());
        }
    }

    @FXML
    private void registrarRetirada(ActionEvent event) {
        try {
            Veiculo veiculo = comboVeiculo.getValue();
            Motorista motorista = comboMotorista.getValue();

            if (veiculo == null || motorista == null) {
                exibirAviso("Selecione o veiculo e o motorista");
                return;
            }

            // AUTENTICACAO OBRIGATORIA
            Usuario operador = autenticarOperador();
            if (operador == null) {
                exibirAviso("Operacao cancelada. Autenticacao necessaria.");
                return;
            }

            Utilizacao utilizacao = servicoUtilizacao.registrarRetirada(
                veiculo.getPlaca(),
                motorista.getCodigo(),
                operador
            );

            exibirSucesso("Retirada registrada com sucesso!\n" +
                         "Codigo: " + utilizacao.getCodigo() + "\n" +
                         "Operador: " + operador.getNome());
            limparCombos();
            carregarCombos();
            carregarDados();

        } catch (UtilizacaoException e) {
            exibirErro("Erro", e.getMessage());
        } catch (Exception e) {
            exibirErro("Erro ao registrar retirada", e.getMessage());
        }
    }

    @FXML
    private void registrarDevolucao(ActionEvent event) {
        try {
            Utilizacao selecionada = tabelaUtilizacoes.getSelectionModel().getSelectedItem();
            
            if (selecionada == null) {
                exibirAviso("Selecione uma utilizacao em aberto");
                return;
            }

            if (!selecionada.isEmUso()) {
                exibirAviso("Esta utilizacao ja foi devolvida!");
                return;
            }

            // AUTENTICACAO OBRIGATORIA
            Usuario operador = autenticarOperador();
            if (operador == null) {
                exibirAviso("Operacao cancelada. Autenticacao necessaria.");
                return;
            }

            servicoUtilizacao.registrarDevolucao(selecionada.getVeiculo().getPlaca(), operador);
            exibirSucesso("Devolucao registrada com sucesso!\n" +
                         "Operador: " + operador.getNome());
            limparCombos();
            carregarCombos();
            carregarDados();

        } catch (UtilizacaoException e) {
            exibirErro("Erro", e.getMessage());
        } catch (Exception e) {
            exibirErro("Erro ao registrar devolucao", e.getMessage());
        }
    }

    /**
     * Exibe dialog de autenticacao e retorna o usuario autenticado.
     * Retorna null se a autenticacao falhar ou for cancelada.
     */
    private Usuario autenticarOperador() {
        // Criar dialog customizado
        Dialog<Usuario> dialog = new Dialog<>();
        dialog.setTitle("Autenticacao Requerida");
        dialog.setHeaderText("Informe suas credenciais de operador\npara autorizar esta operacao:");

        // Definir botoes
        ButtonType loginButtonType = new ButtonType("Autenticar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Criar campos do formulario
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtLogin = new TextField();
        txtLogin.setPromptText("Login");
        PasswordField txtSenha = new PasswordField();
        txtSenha.setPromptText("Senha");

        grid.add(new Label("Login:"), 0, 0);
        grid.add(txtLogin, 1, 0);
        grid.add(new Label("Senha:"), 0, 1);
        grid.add(txtSenha, 1, 1);

        // Adicionar icone de aviso
        Label avisoLabel = new Label("⚠️ Autenticacao obrigatoria conforme especificacao");
        avisoLabel.setStyle("-fx-text-fill: #e67e22; -fx-font-size: 10px;");
        grid.add(avisoLabel, 0, 2, 2, 1);

        dialog.getDialogPane().setContent(grid);

        // Focar no campo de login ao abrir
        Platform.runLater(() -> txtLogin.requestFocus());

        // Desabilitar botao de login se campos vazios
        javafx.scene.Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        txtLogin.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty() || txtSenha.getText().trim().isEmpty());
        });
        
        txtSenha.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty() || txtLogin.getText().trim().isEmpty());
        });

        // Converter resultado quando OK for pressionado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                try {
                    return servicoUsuario.autenticar(txtLogin.getText(), txtSenha.getText());
                } catch (AutenticacaoException e) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Falha na Autenticacao");
                        alert.setHeaderText(null);
                        alert.setContentText("❌ " + e.getMessage() + "\n\nTente novamente.");
                        alert.showAndWait();
                    });
                    return null;
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Erro");
                        alert.setHeaderText(null);
                        alert.setContentText("Erro ao autenticar: " + e.getMessage());
                        alert.showAndWait();
                    });
                    return null;
                }
            }
            return null;
        });

        Optional<Usuario> resultado = dialog.showAndWait();
        return resultado.orElse(null);
    }

    @FXML
    private void excluir(ActionEvent event) {
        try {
            Utilizacao selecionada = tabelaUtilizacoes.getSelectionModel().getSelectedItem();
            
            if (selecionada == null) {
                exibirAviso("Selecione uma utilizacao para excluir");
                return;
            }

            // Confirmacao de exclusao
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Confirmar Exclusao");
            confirmacao.setHeaderText(null);
            confirmacao.setContentText(
                "Deseja realmente excluir a utilizacao?\n\n" +
                "Codigo: " + selecionada.getCodigo() + "\n" +
                "Veiculo: " + selecionada.getVeiculo().getPlaca() + "\n" +
                "Motorista: " + selecionada.getMotorista().getNome() + "\n" +
                "Status: " + (selecionada.isEmUso() ? "EM USO" : "DEVOLVIDO")
            );
            
            if (confirmacao.showAndWait().get() == ButtonType.OK) {
                // Buscar o servico e excluir
                boolean removido = servicoUtilizacao.remover(selecionada.getCodigo());
                
                if (removido) {
                    exibirSucesso("Utilizacao excluida com sucesso!");
                    limparCombos();
                    carregarCombos();
                    carregarDados();
                } else {
                    exibirErro("Erro", "Nao foi possivel excluir a utilizacao");
                }
            }
        } catch (Exception e) {
            exibirErro("Erro ao excluir utilizacao", e.getMessage());
        }
    }

    @FXML
    private void atualizarLista(ActionEvent event) {
        carregarCombos();
        carregarDados();
    }

    private void limparCombos() {
        comboVeiculo.setValue(null);
        comboMotorista.setValue(null);
        tabelaUtilizacoes.getSelectionModel().clearSelection();
    }

    @FXML
    private void voltarMenu(ActionEvent event) {
        try {
            servicoUtilizacao.fecharConexao();
            servicoVeiculo.fecharConexao();
            servicoMotorista.fecharConexao();
            servicoUsuario.fecharConexao();
            App.setRoot("Menu");
        } catch (IOException e) {
            exibirErro("Erro ao voltar ao menu", e.getMessage());
        }
    }

    private void exibirSucesso(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Sucesso");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
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
