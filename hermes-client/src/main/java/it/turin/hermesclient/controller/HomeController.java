package it.turin.hermesclient.controller;

import it.turin.hermesclient.model.ClientModel;
import it.turin.hermesclient.model.HomeModel;
import it.turin.hermesclient.tasks.ScheduledTasksExecutor;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;

public class HomeController {
    public BorderPane homeView;
    private ClientModel clientModel;
    private HomeModel homeModel;

    @FXML
    public Label sentDate;

    @FXML
    public ListView<String> emailList;

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
        this.homeModel = new HomeModel();
        loggedUser.textProperty().bind(clientModel.emailProperty());
        serverStatus.fillProperty().bind(homeModel.serverOnProperty());
        homeView.visibleProperty().bind(clientModel.userLoggedInProperty());
        emailList.setItems(homeModel.getEmails());
        ScheduledTasksExecutor scheduledTasksExecutor = new ScheduledTasksExecutor(homeModel, 8080);
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
