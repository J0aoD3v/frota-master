package controllers;

import com.App;
import dao.Dao;
import excecoes.UsuarioDuplicadoException;
import modelo.Usuario;
import servico.ServicoUsuario;

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
 * Controller para gerenciar usuarios
 */
public class UsuariosController implements Initializable {

    @FXML
    private TextField txtCodigo;
    @FXML
    private TextField txtNome;
    @FXML
    private TextField txtLogin;
    @FXML
    private PasswordField txtSenha;
    @FXML
    private TableView<Usuario> tabelaUsuarios;
    @FXML
    private TableColumn<Usuario, Integer> colCodigo;
    @FXML
    private TableColumn<Usuario, String> colNome;
    @FXML
    private TableColumn<Usuario, String> colLogin;

    private ServicoUsuario servicoUsuario;
    private ObservableList<Usuario> listaUsuarios;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        servicoUsuario = new ServicoUsuario();
        listaUsuarios = FXCollections.observableArrayList();

        // Configurar colunas da tabela
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colLogin.setCellValueFactory(new PropertyValueFactory<>("login"));

        // Campo codigo desabilitado para edicao (sera gerado automaticamente)
        txtCodigo.setEditable(false);
        txtCodigo.setStyle("-fx-background-color: #f0f0f0;");

        // Carregar dados
        carregarDados();

        // Listener para selecao na tabela
        tabelaUsuarios.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selecionarUsuario(newValue)
        );
    }

    private void carregarDados() {
        try {
            listaUsuarios.clear();
            listaUsuarios.addAll(servicoUsuario.listarTodos());
            tabelaUsuarios.setItems(listaUsuarios);
        } catch (Exception e) {
            exibirErro("Erro ao carregar usuarios", e.getMessage());
        }
    }

    private void selecionarUsuario(Usuario usuario) {
        if (usuario != null) {
            txtCodigo.setText(String.valueOf(usuario.getCodigo()));
            txtNome.setText(usuario.getNome());
            txtLogin.setText(usuario.getLogin());
            txtSenha.clear();
        }
    }

    @FXML
    private void adicionar(ActionEvent event) {
        try {
            if (validarCampos()) {
                String nome = txtNome.getText();
                String login = txtLogin.getText();
                String senha = txtSenha.getText();

                // Codigo sera gerado automaticamente pelo servico
                Usuario usuario = new Usuario(0, nome, login, senha);
                servicoUsuario.cadastrar(usuario);

                exibirSucesso("Usuario adicionado com sucesso!");
                limpar(null);
                carregarDados();
            }
        } catch (UsuarioDuplicadoException e) {
            exibirErro("Erro", "Ja existe um usuario com este login!");
        } catch (IllegalArgumentException e) {
            exibirErro("Erro de Validacao", e.getMessage());
        } catch (Exception e) {
            exibirErro("Erro ao adicionar usuario", e.getMessage());
        }
    }

    @FXML
    private void atualizar(ActionEvent event) {
        try {
            Usuario selecionado = tabelaUsuarios.getSelectionModel().getSelectedItem();
            if (selecionado == null) {
                exibirAviso("Selecione um usuario para atualizar");
                return;
            }

            if (validarCamposParaAtualizar()) {
                int codigo = selecionado.getCodigo();
                String nome = txtNome.getText();
                String login = txtLogin.getText();
                String senha = txtSenha.getText().isEmpty() ? selecionado.getSenha() : txtSenha.getText();

                Usuario usuarioAtualizado = new Usuario(codigo, nome, login, senha);
                servicoUsuario.atualizar(usuarioAtualizado);
                exibirSucesso("Usuario atualizado com sucesso!");
                limpar(null);
                carregarDados();
            }
        } catch (UsuarioDuplicadoException e) {
            exibirErro("Erro", "Ja existe um usuario com este login!");
        } catch (IllegalArgumentException e) {
            exibirErro("Erro de Validacao", e.getMessage());
        } catch (Exception e) {
            exibirErro("Erro ao atualizar usuario", e.getMessage());
        }
    }

    @FXML
    private void excluir(ActionEvent event) {
        try {
            Usuario selecionado = tabelaUsuarios.getSelectionModel().getSelectedItem();
            if (selecionado == null) {
                exibirAviso("Selecione um usuario para excluir");
                return;
            }

            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Confirmar Exclusao");
            confirmacao.setHeaderText(null);
            confirmacao.setContentText("Deseja realmente excluir o usuario " + selecionado.getNome() + "?");
            
            if (confirmacao.showAndWait().get() == ButtonType.OK) {
                servicoUsuario.remover(selecionado.getCodigo());
                exibirSucesso("Usuario excluido com sucesso!");
                limpar(null);
                carregarDados();
            }
        } catch (Exception e) {
            exibirErro("Erro ao excluir usuario", e.getMessage());
        }
    }

    @FXML
    private void limpar(ActionEvent event) {
        txtCodigo.clear();
        txtNome.clear();
        txtLogin.clear();
        txtSenha.clear();
        tabelaUsuarios.getSelectionModel().clearSelection();
    }

    @FXML
    private void voltarMenu(ActionEvent event) {
        try {
            servicoUsuario.fecharConexao();
            App.setRoot("Menu");
        } catch (IOException e) {
            exibirErro("Erro ao voltar ao menu", e.getMessage());
        }
    }

    private boolean validarCampos() {
        if (txtNome.getText().isEmpty()) {
            exibirAviso("Informe o nome do usuario");
            return false;
        }
        if (txtLogin.getText().isEmpty()) {
            exibirAviso("Informe o login do usuario");
            return false;
        }
        if (txtSenha.getText().isEmpty()) {
            exibirAviso("Informe a senha do usuario");
            return false;
        }
        return true;
    }

    private boolean validarCamposParaAtualizar() {
        if (txtNome.getText().isEmpty()) {
            exibirAviso("Informe o nome do usuario");
            return false;
        }
        if (txtLogin.getText().isEmpty()) {
            exibirAviso("Informe o login do usuario");
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
