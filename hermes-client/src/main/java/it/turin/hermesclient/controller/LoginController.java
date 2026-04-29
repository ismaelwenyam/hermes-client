package it.turin.hermesclient.controller;

import it.turin.hermesclient.model.ClientModel;
import it.turin.hermesclient.tasks.Login;
import it.turin.hermesclient.utils.EmailValidator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class LoginController {
    private ClientModel clientModel;

    @FXML
    public VBox loginView;
    @FXML
    public ProgressBar progessBar;
    @FXML
    public TextField emailField;
    @FXML
    public Label errorLabel;
    @FXML
    public Button loginButton;

    public boolean emailValid = false;

    public void init (ClientModel clientModel) {
        this.clientModel = clientModel;
        clientModel.emailProperty().bind(emailField.textProperty());
        loginView.visibleProperty().bind(clientModel.userNotLoggedInProperty());
        errorLabel.visibleProperty().bind(clientModel.showErrorProperty());
        errorLabel.textProperty().bind(clientModel.errorMessageProperty());
    }

    public void onLoginButton(MouseEvent mouseEvent) {
        loginButton.setVisible(false);
        if (!EmailValidator.isValid(emailField.getText().trim())) {
            clientModel.setErrorMessage("Email not valid");
            clientModel.setShowError(true);
            loginButton.setVisible(true);
        }
        else
            logUser();
    }

    private void logUser() {
        Thread t = new Thread(new Login(clientModel, 8080), "login-thread");
        t.start();
    }

    public void hideErrorLabel(MouseEvent mouseEvent) {
        loginButton.setVisible(true);
        clientModel.setShowError(false);
    }

    /*
    public void onEnterPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)){
            loginButton.setVisible(false);
            if (!EmailValidator.isValid(emailField.getText().trim())) {
                clientModel.setErrorMessage("Email not valid");
                clientModel.setShowError(true);
                loginButton.setVisible(true);
            }
            else
                logUser();
        }
    }
    */
}
