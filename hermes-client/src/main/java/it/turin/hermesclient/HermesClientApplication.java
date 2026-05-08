package it.turin.hermesclient;

import it.turin.hermesclient.model.ClientModel;
import it.turin.hermesclient.utils.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class HermesClientApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        ClientModel clientModel = new ClientModel();
        stage.setTitle("Hermes");
        SceneManager.setStage(stage);
        SceneManager.switchScene("login-view.fxml", clientModel);
        stage.show();
    }
}
