package controllers;

import com.App;
import excecoes.MotoristaInvalidoException;
import modelo.Motorista;
import servico.ServicoMotorista;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller para gerenciar motoristas
 */
public class MotoristasController implements Initializable {

    @FXML
    private TextField txtCodigo;
    @FXML
    private TextField txtNome;
    @FXML
    private TextField txtCnh;
    @FXML
    private TextField txtSetor;
    @FXML
    private TableView<Motorista> tabelaMotoristas;
    @FXML
    private TableColumn<Motorista, Integer> colCodigo;
    @FXML
    private TableColumn<Motorista, String> colNome;
    @FXML
    private TableColumn<Motorista, String> colCnh;
    @FXML
    private TableColumn<Motorista, String> colSetor;

    private ServicoMotorista servicoMotorista;
    private ObservableList<Motorista> listaMotoristas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        servicoMotorista = new ServicoMotorista();
        listaMotoristas = FXCollections.observableArrayList();

        // Configurar colunas da tabela
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCnh.setCellValueFactory(new PropertyValueFactory<>("cnh"));
        colSetor.setCellValueFactory(new PropertyValueFactory<>("setor"));

        // Campo codigo desabilitado para edicao (sera gerado automaticamente)
        txtCodigo.setEditable(false);
        txtCodigo.setStyle("-fx-background-color: #f0f0f0;");

        // Carregar dados
        carregarDados();

        // Listener para selecao na tabela
        tabelaMotoristas.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selecionarMotorista(newValue)
        );
    }

    private void carregarDados() {
        try {
            listaMotoristas.clear();
            listaMotoristas.addAll(servicoMotorista.listarTodos());
            tabelaMotoristas.setItems(listaMotoristas);
        } catch (Exception e) {
            exibirErro("Erro ao carregar motoristas", e.getMessage());
        }
    }

    private void selecionarMotorista(Motorista motorista) {
        if (motorista != null) {
            txtCodigo.setText(String.valueOf(motorista.getCodigo()));
            txtNome.setText(motorista.getNome());
            txtCnh.setText(motorista.getCnh());
            txtSetor.setText(motorista.getSetor());
        }
    }

    @FXML
    private void adicionar(ActionEvent event) {
        try {
            if (validarCampos()) {
                String nome = txtNome.getText();
                String cnh = txtCnh.getText();
                String setor = txtSetor.getText();

                // Codigo sera gerado automaticamente pelo servico
                Motorista motorista = new Motorista(0, nome, cnh, setor);
                servicoMotorista.cadastrar(motorista);

                exibirSucesso("Motorista adicionado com sucesso!");
                limpar(null);
                carregarDados();
            }
        } catch (MotoristaInvalidoException e) {
            exibirErro("Erro", e.getMessage());
        } catch (IllegalArgumentException e) {
            exibirErro("Erro de Validacao", e.getMessage());
        } catch (Exception e) {
            exibirErro("Erro ao adicionar motorista", e.getMessage());
        }
    }

    @FXML
    private void atualizar(ActionEvent event) {
        try {
            Motorista selecionado = tabelaMotoristas.getSelectionModel().getSelectedItem();
            if (selecionado == null) {
                exibirAviso("Selecione um motorista para atualizar");
                return;
            }

            if (validarCampos()) {
                int codigo = selecionado.getCodigo();
                String nome = txtNome.getText();
                String cnh = txtCnh.getText();
                String setor = txtSetor.getText();

                Motorista motoristaAtualizado = new Motorista(codigo, nome, cnh, setor);
                servicoMotorista.atualizar(motoristaAtualizado);
                exibirSucesso("Motorista atualizado com sucesso!");
                limpar(null);
                carregarDados();
            }
        } catch (MotoristaInvalidoException e) {
            exibirErro("Erro", e.getMessage());
        } catch (IllegalArgumentException e) {
            exibirErro("Erro de Validacao", e.getMessage());
        } catch (Exception e) {
            exibirErro("Erro ao atualizar motorista", e.getMessage());
        }
    }

    @FXML
    private void excluir(ActionEvent event) {
        try {
            Motorista selecionado = tabelaMotoristas.getSelectionModel().getSelectedItem();
            if (selecionado == null) {
                exibirAviso("Selecione um motorista para excluir");
                return;
            }

            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Confirmar Exclusao");
            confirmacao.setHeaderText(null);
            confirmacao.setContentText("Deseja realmente excluir o motorista " + selecionado.getNome() + "?");
            
            if (confirmacao.showAndWait().get() == ButtonType.OK) {
                servicoMotorista.remover(selecionado.getCodigo());
                exibirSucesso("Motorista excluido com sucesso!");
                limpar(null);
                carregarDados();
            }
        } catch (Exception e) {
            exibirErro("Erro ao excluir motorista", e.getMessage());
        }
    }

    @FXML
    private void limpar(ActionEvent event) {
        txtCodigo.clear();
        txtNome.clear();
        txtCnh.clear();
        txtSetor.clear();
        tabelaMotoristas.getSelectionModel().clearSelection();
    }

    @FXML
    private void voltarMenu(ActionEvent event) {
        try {
            servicoMotorista.fecharConexao();
            App.setRoot("Menu");
        } catch (IOException e) {
            exibirErro("Erro ao voltar ao menu", e.getMessage());
        }
    }

    private boolean validarCampos() {
        if (txtNome.getText().isEmpty()) {
            exibirAviso("Informe o nome do motorista");
            return false;
        }
        if (txtCnh.getText().isEmpty()) {
            exibirAviso("Informe a CNH do motorista");
            return false;
        }
        if (txtSetor.getText().isEmpty()) {
            exibirAviso("Informe o setor do motorista");
            return false;
        }
        return true;
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
