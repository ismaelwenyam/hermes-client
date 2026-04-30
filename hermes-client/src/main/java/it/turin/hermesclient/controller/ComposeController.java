package it.turin.hermesclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class ComposeController {
    @FXML public TextField argumentTxtField;
    @FXML public TextField recipientsTxtField;
    @FXML public TextArea mailTxtArea;
    @FXML public Label errorLabel;

    public void onCancel(MouseEvent mouseEvent) {
    }

    public void onSend(MouseEvent mouseEvent) {
    }
}
