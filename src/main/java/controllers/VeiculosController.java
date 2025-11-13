package controllers;

import com.App;
import excecoes.VeiculoDuplicadoException;
import modelo.Veiculo;
import servico.ServicoVeiculo;
import servico.ServicoUtilizacao;

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
 * Controller para gerenciar veiculos
 */
public class VeiculosController implements Initializable {

    @FXML
    private TextField txtPlaca;
    @FXML
    private TextField txtMarca;
    @FXML
    private TextField txtModelo;
    @FXML
    private TableView<Veiculo> tabelaVeiculos;
    @FXML
    private TableColumn<Veiculo, String> colPlaca;
    @FXML
    private TableColumn<Veiculo, String> colMarca;
    @FXML
    private TableColumn<Veiculo, String> colModelo;
    @FXML
    private TableColumn<Veiculo, String> colStatus;

    private ServicoVeiculo servicoVeiculo;
    private ServicoUtilizacao servicoUtilizacao;
    private ObservableList<Veiculo> listaVeiculos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        servicoVeiculo = new ServicoVeiculo();
        servicoUtilizacao = new ServicoUtilizacao();
        listaVeiculos = FXCollections.observableArrayList();

        // Configurar colunas da tabela
        colPlaca.setCellValueFactory(new PropertyValueFactory<>("placa"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colStatus.setCellValueFactory(cellData -> {
            String placa = cellData.getValue().getPlaca();
            boolean emUso = servicoUtilizacao.veiculoEmUso(placa);
            return new javafx.beans.property.SimpleStringProperty(emUso ? "EM USO" : "DISPONIVEL");
        });

        // Carregar dados
        carregarDados();

        // Listener para selecao na tabela
        tabelaVeiculos.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selecionarVeiculo(newValue)
        );
    }

    private void carregarDados() {
        try {
            listaVeiculos.clear();
            listaVeiculos.addAll(servicoVeiculo.listarTodos());
            tabelaVeiculos.setItems(listaVeiculos);
        } catch (Exception e) {
            exibirErro("Erro ao carregar veiculos", e.getMessage());
        }
    }

    private void selecionarVeiculo(Veiculo veiculo) {
        if (veiculo != null) {
            txtPlaca.setText(veiculo.getPlaca());
            txtMarca.setText(veiculo.getMarca());
            txtModelo.setText(veiculo.getModelo());
        }
    }

    @FXML
    private void adicionar(ActionEvent event) {
        try {
            if (validarCampos()) {
                String placa = txtPlaca.getText().toUpperCase();
                String marca = txtMarca.getText();
                String modelo = txtModelo.getText();

                Veiculo veiculo = new Veiculo(placa, marca, modelo);
                servicoVeiculo.cadastrar(veiculo);

                exibirSucesso("Veiculo adicionado com sucesso!");
                limpar(null);
                carregarDados();
            }
        } catch (VeiculoDuplicadoException e) {
            exibirErro("Erro", "Veiculo ja cadastrado com esta placa!");
        } catch (IllegalArgumentException e) {
            exibirErro("Erro de Validacao", e.getMessage());
        } catch (Exception e) {
            exibirErro("Erro ao adicionar veiculo", e.getMessage());
        }
    }

    @FXML
    private void atualizar(ActionEvent event) {
        try {
            Veiculo selecionado = tabelaVeiculos.getSelectionModel().getSelectedItem();
            if (selecionado == null) {
                exibirAviso("Selecione um veiculo para atualizar");
                return;
            }

            if (validarCamposParaAtualizar()) {
                String placa = selecionado.getPlaca();
                String marca = txtMarca.getText();
                String modelo = txtModelo.getText();

                Veiculo veiculoAtualizado = new Veiculo(placa, marca, modelo);
                servicoVeiculo.atualizar(veiculoAtualizado);
                exibirSucesso("Veiculo atualizado com sucesso!");
                limpar(null);
                carregarDados();
            }
        } catch (VeiculoDuplicadoException e) {
            exibirErro("Erro", "Ja existe um veiculo com esta placa!");
        } catch (IllegalArgumentException e) {
            exibirErro("Erro de Validacao", e.getMessage());
        } catch (Exception e) {
            exibirErro("Erro ao atualizar veiculo", e.getMessage());
        }
    }

    @FXML
    private void excluir(ActionEvent event) {
        try {
            Veiculo selecionado = tabelaVeiculos.getSelectionModel().getSelectedItem();
            if (selecionado == null) {
                exibirAviso("Selecione um veiculo para excluir");
                return;
            }

            // Verificar se o veiculo esta em uso
            if (servicoUtilizacao.veiculoEmUso(selecionado.getPlaca())) {
                exibirAviso("Nao e possivel excluir um veiculo em uso!");
                return;
            }

            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Confirmar Exclusao");
            confirmacao.setHeaderText(null);
            confirmacao.setContentText("Deseja realmente excluir o veiculo " + selecionado.getPlaca() + "?");
            
            if (confirmacao.showAndWait().get() == ButtonType.OK) {
                servicoVeiculo.remover(selecionado.getPlaca());
                exibirSucesso("Veiculo excluido com sucesso!");
                limpar(null);
                carregarDados();
            }
        } catch (Exception e) {
            exibirErro("Erro ao excluir veiculo", e.getMessage());
        }
    }

    @FXML
    private void limpar(ActionEvent event) {
        txtPlaca.clear();
        txtMarca.clear();
        txtModelo.clear();
        tabelaVeiculos.getSelectionModel().clearSelection();
    }

    @FXML
    private void voltarMenu(ActionEvent event) {
        try {
            servicoVeiculo.fecharConexao();
            servicoUtilizacao.fecharConexao();
            App.setRoot("Menu");
        } catch (IOException e) {
            exibirErro("Erro ao voltar ao menu", e.getMessage());
        }
    }

    private boolean validarCampos() {
        if (txtPlaca.getText().isEmpty()) {
            exibirAviso("Informe a placa do veiculo");
            return false;
        }
        if (txtMarca.getText().isEmpty()) {
            exibirAviso("Informe a marca do veiculo");
            return false;
        }
        if (txtModelo.getText().isEmpty()) {
            exibirAviso("Informe o modelo do veiculo");
            return false;
        }
        return true;
    }

    private boolean validarCamposParaAtualizar() {
        if (txtMarca.getText().isEmpty()) {
            exibirAviso("Informe a marca do veiculo");
            return false;
        }
        if (txtModelo.getText().isEmpty()) {
            exibirAviso("Informe o modelo do veiculo");
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
