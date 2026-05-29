package it.turin.hermesclient.controller;

import it.turin.hermesclient.model.ClientModel;
import it.turin.hermesclient.tasks.Login;
import it.turin.hermesclient.utils.EmailValidator;
import it.turin.hermesclient.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import java.io.IOException;

/**
 * Controller della vista di login.
 * <p>
 * Valida l'indirizzo email inserito dall'utente e avvia l'attivita' di login che
 * verifica l'account sul server.
 */
public class LoginController extends ClientController {
    private ClientModel clientModel;
    private Login login;

    @FXML
    public TextField emailField;
    @FXML
    public Label errorLabel;
    @FXML
    public Button loginButton;

    /**
     * Inizializza il controller e collega il campo email al modello condiviso.
     *
     * @param clientModel stato condiviso dell'applicazione
     */
    public void init (ClientModel clientModel) {
        this.clientModel = clientModel;
        clientModel.emailProperty().bind(emailField.textProperty());
    }

    /**
     * Arresta le attivita' in background gestite dal modello del client.
     */
    @Override
    public void shutdown() {
        clientModel.getTasksExecutor().shutdown();
    }

    /**
     * Gestisce il click sul pulsante di login
     *
     * @param mouseEvent evento di click che ha attivato il tentativo di login
     */
    public void onLoginButton(MouseEvent mouseEvent) {
        loginButton.setVisible(false);
        validateAndLogUser();
    }

    /**
     * Gestisce l'evento in cui viene premuto il pulsante enter.
     * @param keyEvent evento di press che ha attivato il tentativo di login
     * */
    public void onEnterPressed(KeyEvent keyEvent) {
        if (!keyEvent.getCode().getName().equalsIgnoreCase("enter")) return;
        loginButton.setVisible(false);
        validateAndLogUser();
    }

    private void validateAndLogUser() {
        if (!EmailValidator.isValid(emailField.getText().trim())) {
            errorLabel.setText("Email not valid");
            errorLabel.setVisible(true);
            loginButton.setVisible(true);
        }
        else
            logUser();
    }


    /**
     * Crea e avvia l'attivita' di login, poi passa alla vista principale in caso di
     * autenticazione riuscita.
     */
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

    /**
     * Ripristina il pulsante di login e nasconde il messaggio di errore.
     *
     * @param mouseEvent evento di click che ha attivato il reset
     */
    public void hideErrorLabel(MouseEvent mouseEvent) {
        loginButton.setVisible(true);
        errorLabel.setVisible(false);
    }
}
