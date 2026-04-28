package it.turin.hermesclient.controller;

import it.turin.hermesclient.model.LoginModel;
import it.turin.hermesclient.utils.EmailValidator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class LoginController {
    private LoginModel loginModel;

    @FXML
    public TextField emailField;
    @FXML
    public Label errorLabel;

    public boolean emailValid = false;

    public void init (LoginModel loginModel) {
        this.loginModel = loginModel;
        loginModel.emailProperty().bind(emailField.textProperty());
    }

    public void login(MouseEvent mouseEvent) {
        if (!EmailValidator.isValid(emailField.getText().trim())){
            showErrorLabel("Email not valid");
            return;
        }
    }

    private void showErrorLabel(String error) {
        errorLabel.setText(error);
        errorLabel.setVisible(true);
    }

    public void hideErrorLabel(MouseEvent mouseEvent) {
        errorLabel.setVisible(false);
    }
}
