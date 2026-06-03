package it.turin.hermesclient.controller;

import it.turin.hermesclient.model.ClientModel;
import it.turin.hermesclient.model.ComposeModel;
import it.turin.hermesclient.model.Email;
import it.turin.hermesclient.tasks.Forwarding;
import it.turin.hermesclient.utils.EmailValidator;
import it.turin.hermesclient.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Controller della vista di composizione delle email.
 * <p>
 * Collega il form di composizione al {@link ClientModel} condiviso, valida i
 * destinatari e avvia l'attivita' di inoltro quando l'utente invia un'email.
 */
public class ComposeController extends ClientController {
    private ClientModel clientModel;
    private ComposeModel composeModel;

    @FXML public TextField argumentTxtField;
    @FXML public TextField recipientsTxtField;
    @FXML public TextArea mailTxtArea;
    @FXML public Label errorLabel;
    @FXML public Circle serverStatus;

    /**
     * Collega i controlli della vista di composizione al modello condiviso.
     *
     * @param clientModel stato condiviso dell'applicazione
     */
    public void init (ClientModel clientModel) {
        this.clientModel = clientModel;
        this.composeModel = clientModel.getComposeModel();
        argumentTxtField.textProperty().bindBidirectional(composeModel.argumentProperty());
        recipientsTxtField.textProperty().bindBidirectional(composeModel.recipientsProperty());
        mailTxtArea.textProperty().bindBidirectional(composeModel.textBodyProperty());
        serverStatus.fillProperty().bind(clientModel.serverStatusColorProperty());
        errorLabel.visibleProperty().bind(clientModel.showErrorProperty());
        errorLabel.textProperty().bind(clientModel.errorMessageProperty());
    }

    /**
     * Arresta le attivita' in background gestite dal modello del client.
     */
    @Override
    public void shutdown() {
        clientModel.getTasksExecutor().shutdown();
    }

    /**
     * Annulla la composizione, pulisce lo stato temporaneo e ritorna alla vista
     * principale.
     *
     * @param mouseEvent evento di click che ha attivato l'annullamento
     */
    public void onCancel(MouseEvent mouseEvent) {
        clientModel.setShowError(false);
        clientModel.setErrorMessage("");
        composeModel.clearDraft();
        try {
            SceneManager.switchScene("home-view.fxml", clientModel);
        } catch (IOException e) {
            System.err.println("execption in switching to home-view");
        }
    }

    /**
     * Valida il form di composizione e invia l'email corrente se l'input e'
     * accettabile.
     *
     * @param mouseEvent evento di click che ha attivato l'invio
     */
    public void onSend(MouseEvent mouseEvent) {
        if (recipientsTxtField.getText().trim().isEmpty()){
            clientModel.setErrorMessage("Nessun destinatario inserito");
            clientModel.setShowError(true);
            return;
        }
        List<String> recipients = Arrays.stream(recipientsTxtField.getText().trim().split(";")).toList();
        System.out.println("recipients: " + recipients);
        if (!EmailValidator.allValid(recipients)) {
            clientModel.setErrorMessage("Not valid: " + EmailValidator.findInvalid(recipients));
            clientModel.setShowError(true);
            return;
        }
        if (argumentTxtField.getText().trim().isEmpty()) {
            clientModel.setErrorMessage("Nessun oggetto");
            clientModel.setShowError(true);
            return;
        }
        Email mail = new Email(clientModel.getEmail(), recipients, argumentTxtField.getText().trim(), mailTxtArea.getText().trim(), Date.from(Instant.now()));
        composeModel.setMail(mail);
        send();
    }

    /**
     * Avvia l'attivita' asincrona che invia al server l'email composta.
     */
    private void send () {
        Thread sendThread = new Thread(new Forwarding(clientModel, composeModel, 8080), "forwarding");
        sendThread.start();
    }

    /**
     * Nasconde il messaggio di errore quando l'utente modifica il form.
     *
     * @param keyEvent evento da tastiera emesso dal controllo modificato
     */
    public void onKeyPressedHideErrorLbl(KeyEvent keyEvent) {
        clientModel.setShowError(false);
    }

    /**
     * Pulisce tutti i campi modificabili del form di composizione.
     *
     * @param mouseEvent evento di click che ha attivato il reset
     */
    public void onReset(MouseEvent mouseEvent) {
        composeModel.clearDraft();
    }
}
