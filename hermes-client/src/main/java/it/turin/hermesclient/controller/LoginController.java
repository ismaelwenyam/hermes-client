package it.turin.hermesclient.controller;

import it.turin.hermesclient.dto.Response;
import it.turin.hermesclient.model.LoginModel;
import it.turin.hermesclient.tasks.LoginTask;
import it.turin.hermesclient.utils.EmailValidator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class LoginController {
    private LoginModel loginModel;

    @FXML
    public ProgressBar progessBar;
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
        if (!EmailValidator.isValid(emailField.getText().trim()))
            showErrorLabel("Email not valid");
        else
            logUser();
    }

    private void logUser() {
        try {
            FutureTask<Response> futureResponse = new FutureTask<Response>(new LoginTask(InetAddress.getLocalHost(), 8080, loginModel));
            Thread t = new Thread(futureResponse);
            t.start();
            progessBar.setVisible(true);
            Response response = futureResponse.get();
            System.out.println("response: " + response);
        } catch (UnknownHostException e) {
            System.err.println("exception in get local host: " + e.getMessage());
        } catch (ExecutionException | InterruptedException e) {
            System.err.println("exeception caught in execution tuture task: " + e.getMessage());
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
