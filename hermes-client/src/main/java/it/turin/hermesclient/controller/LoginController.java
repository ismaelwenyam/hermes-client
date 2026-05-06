package it.turin.hermesclient.controller;

import it.turin.hermesclient.model.ClientModel;
import it.turin.hermesclient.tasks.Login;
import it.turin.hermesclient.utils.EmailValidator;
import it.turin.hermesclient.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import java.io.IOException;

public class LoginController extends ClientController {
    private ClientModel clientModel;
    private Login login;

    @FXML
    public TextField emailField;
    @FXML
    public Label errorLabel;
    @FXML
    public Button loginButton;

    public void init (ClientModel clientModel) {
        this.clientModel = clientModel;
        clientModel.emailProperty().bind(emailField.textProperty());
    }

    public void onLoginButton(MouseEvent mouseEvent) {
        loginButton.setVisible(false);
        if (!EmailValidator.isValid(emailField.getText().trim())) {
            errorLabel.setText("Email not valid");
            errorLabel.setVisible(true);
            loginButton.setVisible(true);
        }
        else
            logUser();
    }

    private void logUser() {
        login = new Login(clientModel, 8080);
        login.setOnSucceeded(e -> {
            if (login.getValue()) {
                try {
                    SceneManager.switchScene("home-view.fxml", clientModel);
                } catch (IOException ex) {
                    System.err.println("exception switching scene");
                }
            } else {
                errorLabel.setText(login.getMessage());
                errorLabel.setVisible(true);
            }
        });
        login.setOnFailed(e -> {
            errorLabel.setText("client error");
            errorLabel.setVisible(true);
        });

        Thread t = new Thread(login, "login-thread");
        t.start();
    }

    public void hideErrorLabel(MouseEvent mouseEvent) {
        loginButton.setVisible(true);
        errorLabel.setVisible(false);
    }
}
