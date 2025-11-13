package controllers;

import com.App;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

/**
 * Menu Controller class
 *
 * @author Joao C
 */
public class MenuController implements Initializable {

    @FXML
    private Label statusOperador;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (statusOperador != null) {
            statusOperador.setText("Operador: nao autenticado");
        }
    }    
    
    @FXML
    private void abrirUsuarios(ActionEvent event) {
        try {
            App.setRoot("Usuarios");
        } catch (IOException e) {
            exibirErro("Erro ao abrir tela de Usuarios", e.getMessage());
        }
    }

    @FXML
    private void abrirMotoristas(ActionEvent event) {
        try {
            App.setRoot("Motoristas");
        } catch (IOException e) {
            exibirErro("Erro ao abrir tela de Motoristas", e.getMessage());
        }
    }

    @FXML
    private void abrirVeiculos(ActionEvent event) {
        try {
            App.setRoot("Veiculos");
        } catch (IOException e) {
            exibirErro("Erro ao abrir tela de Veiculos", e.getMessage());
        }
    }

    @FXML
    private void abrirUtilizacoes(ActionEvent event) {
        try {
            App.setRoot("Utilizacoes");
        } catch (IOException e) {
            exibirErro("Erro ao abrir tela de Utilizacoes", e.getMessage());
        }
    }

    @FXML
    private void abrirRelatorios(ActionEvent event) {
        try {
            App.setRoot("Relatorios");
        } catch (IOException e) {
            exibirErro("Erro ao abrir tela de Relatorios", e.getMessage());
        }
    }

    @FXML
    private void sair(ActionEvent event) {
        System.exit(0);
    }

    private void exibirErro(String titulo, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}
