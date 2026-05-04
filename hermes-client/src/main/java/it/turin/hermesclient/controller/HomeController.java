package it.turin.hermesclient.controller;

import it.turin.hermesclient.model.ClientModel;
import it.turin.hermesclient.model.Email;
import it.turin.hermesclient.model.HomeModel;
import it.turin.hermesclient.tasks.ScheduledTasksExecutor;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;

public class HomeController {

    private ClientModel clientModel;
    private HomeModel homeModel;

    @FXML public BorderPane homeView;

    @FXML public Label argument;
    @FXML public Label from;
    @FXML public Label to;
    @FXML public TextArea mailArea;
    @FXML public Label sentDate;

    @FXML public ListView<Email> emailList;
    @FXML public Button newEmailButton;
    @FXML public Label loggedUser;
    @FXML public Circle serverStatus;

    public void onNewMessage(MouseEvent mouseEvent) {

    }

    public void init(ClientModel clientModel) {
        this.clientModel = clientModel;
        this.homeModel = new HomeModel();
        loggedUser.textProperty().bind(clientModel.emailProperty());
        serverStatus.fillProperty().bind(clientModel.serverOnProperty());
        homeView.visibleProperty().bind(clientModel.userLoggedInProperty());
        emailList.setItems(homeModel.getEmails());
        emailList.setCellFactory(param -> new ListCell<>() {

            @Override
            protected void updateItem(Email mail, boolean empty) {
                super.updateItem(mail, empty);
                if (empty || mail == null || mail.getArgument() == null) {
                    setText("");
                } else {
                    setText(mail.getArgument());
                }
            }
        });
        emailList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            argument.setText(newValue.getArgument());
            from.setText(newValue.getSender());
            to.setText(newValue.getRecipients().toString());
            sentDate.setText(newValue.getSentDate().toString());
            mailArea.setText(newValue.getMailBody());
        });
        ScheduledTasksExecutor scheduledTasksExecutor = new ScheduledTasksExecutor(clientModel, homeModel, 8080);
        new Thread(() -> {
            scheduledTasksExecutor.start();
        }).start();
    }

    public void shutdown() {
        //TODO stop scheduled tasks, like connection..
    }

    public void onAnswer () {

    }

    public void onAnswerAll () {

    }

    public void onForward () {

    }

    public void onDelete () {

    }
}
