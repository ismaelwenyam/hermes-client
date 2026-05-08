package it.turin.hermesclient.controller;

import it.turin.hermesclient.model.ClientModel;
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

public class ComposeController extends ClientController {
    private ClientModel clientModel;

    @FXML public TextField argumentTxtField;
    @FXML public TextField recipientsTxtField;
    @FXML public TextArea mailTxtArea;
    @FXML public Label errorLabel;
    @FXML public Circle serverStatus;

    public void init (ClientModel clientModel) {
        this.clientModel = clientModel;
        argumentTxtField.textProperty().bindBidirectional(clientModel.argumentProperty());
        recipientsTxtField.textProperty().bindBidirectional(clientModel.recipientsProperty());
        mailTxtArea.textProperty().bindBidirectional(clientModel.textBodyProperty());
        serverStatus.fillProperty().bind(clientModel.serverStatusColorProperty());
        errorLabel.visibleProperty().bind(clientModel.showErrorProperty());
        errorLabel.textProperty().bind(clientModel.errorMessageProperty());
        serverStatus.fillProperty().bind(clientModel.serverStatusColorProperty());
    }

    @Override
    public void shutdown() {
        clientModel.getTasksExecutor().shutdown();
    }

    public void onCancel(MouseEvent mouseEvent) {
        clientModel.setShowError(false);
        clientModel.setErrorMessage("");
        try {
            SceneManager.switchScene("home-view.fxml", clientModel);
        } catch (IOException e) {
            System.err.println("execption in switching to compose-view");
        }
    }

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
        }
        Email mail = new Email(clientModel.getEmail(), recipients, argumentTxtField.getText().trim(), mailTxtArea.getText().trim(), Date.from(Instant.now()));
        clientModel.setMail(mail);
        send();
    }

    private void send () {
        Thread sendThread = new Thread(new Forwarding(clientModel, 8080), "forwarding");
        sendThread.start();
    }

    public void onKeyPressedHideErrorLbl(KeyEvent keyEvent) {
        clientModel.setShowError(false);
    }

    public void onReset(MouseEvent mouseEvent) {
        argumentTxtField.clear();
        recipientsTxtField.clear();
        mailTxtArea.clear();
    }
}
