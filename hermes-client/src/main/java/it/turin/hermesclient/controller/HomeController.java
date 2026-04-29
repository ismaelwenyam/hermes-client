package it.turin.hermesclient.controller;

import it.turin.hermesclient.model.ClientModel;
import it.turin.hermesclient.tasks.ScheduledTasksExecutor;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;

public class HomeController {
    public BorderPane homeView;
    private ClientModel clientModel;

    @FXML
    public Button newEmailButton;

    @FXML
    public Label loggedUser;

    @FXML
    public Circle serverStatus;

    public void onNewMessage(MouseEvent mouseEvent) {

    }

    public void init(ClientModel clientModel) {
        this.clientModel = clientModel;
        loggedUser.textProperty().bind(clientModel.emailProperty());
        serverStatus.fillProperty().bind(clientModel.serverOnProperty());
        homeView.visibleProperty().bind(clientModel.userLoggedInProperty());
        ScheduledTasksExecutor scheduledTasksExecutor = new ScheduledTasksExecutor(clientModel, 8080);
        new Thread(() -> {
            scheduledTasksExecutor.start();
        }).start();
    }

    public void shutdown() {
        //TODO stop scheduled tasks, like connection..
    }
}
