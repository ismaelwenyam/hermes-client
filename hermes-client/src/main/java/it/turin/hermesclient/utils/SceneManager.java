package it.turin.hermesclient.utils;

import it.turin.hermesclient.HermesClientApplication;
import it.turin.hermesclient.controller.ClientController;
import it.turin.hermesclient.model.ClientModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {
    private static Stage stage;

    public static void setStage(Stage s) {
        stage = s;
    }

    public static void switchScene(String fxml, ClientModel model) throws IOException {
        FXMLLoader loader = new FXMLLoader(HermesClientApplication.class.getResource(fxml));
        Scene scene = new Scene(loader.load());
        Object controller = loader.getController();
        if (controller instanceof ClientController clientController) {
            clientController.init(model);
        }
        stage.setScene(scene);
    }
}
