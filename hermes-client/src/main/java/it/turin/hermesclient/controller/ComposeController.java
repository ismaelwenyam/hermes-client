package it.turin.hermesclient.controller;

import it.turin.hermesclient.model.ClientModel;
import it.turin.hermesclient.utils.EmailValidator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.util.Arrays;
import java.util.List;

public class ComposeController {
    private ClientModel clientModel;

    @FXML public TextField argumentTxtField;
    @FXML public TextField recipientsTxtField;
    @FXML public TextArea mailTxtArea;
    @FXML public Label errorLabel;

    public void init (ClientModel clientModel) {
        this.clientModel = clientModel;
    }

    public void onCancel(MouseEvent mouseEvent) {
        //TODO turn to HomeView
    }

    public void onSend(MouseEvent mouseEvent) {
        if (recipientsTxtField.getText().trim().isEmpty()){
            errorLabel.setVisible(true);
            errorLabel.setText("Nessun destinatario inserito");
            return;
        }
        List<String> recipients = Arrays.stream(recipientsTxtField.getText().split(";")).toList();
        System.out.println("recipients: " + recipients);
        if (!EmailValidator.allValid(recipients)) {
            errorLabel.setVisible(true);
            errorLabel.setText(EmailValidator.findInvalid(recipients).toString());
            return;
        }
        if (argumentTxtField.getText().trim().isEmpty()) {
            errorLabel.setVisible(true);
            errorLabel.setText("Nessun oggetto");
        }

    }
}
