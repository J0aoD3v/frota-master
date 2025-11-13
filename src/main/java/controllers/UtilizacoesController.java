package controllers;

import com.App;
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
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

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
    private Usuario operadorAtual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        servicoUtilizacao = new ServicoUtilizacao();
        servicoVeiculo = new ServicoVeiculo();
        servicoMotorista = new ServicoMotorista();
        servicoUsuario = new ServicoUsuario();
        listaUtilizacoes = FXCollections.observableArrayList();

        // Usuario padrao para testes
        operadorAtual = servicoUsuario.buscarPorCodigo(1);
        if (operadorAtual == null) {
            operadorAtual = new Usuario(1, "Administrador", "admin", "123456");
        }

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

            Utilizacao utilizacao = servicoUtilizacao.registrarRetirada(
                veiculo.getPlaca(),
                motorista.getCodigo(),
                operadorAtual
            );

            exibirSucesso("Retirada registrada com sucesso!\nCodigo: " + utilizacao.getCodigo());
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

            servicoUtilizacao.registrarDevolucao(selecionada.getVeiculo().getPlaca(), operadorAtual);
            exibirSucesso("Devolucao registrada com sucesso!");
            limparCombos();
            carregarCombos();
            carregarDados();

        } catch (UtilizacaoException e) {
            exibirErro("Erro", e.getMessage());
        } catch (Exception e) {
            exibirErro("Erro ao registrar devolucao", e.getMessage());
        }
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
