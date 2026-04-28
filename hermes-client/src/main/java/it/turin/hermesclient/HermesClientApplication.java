package it.turin.hermesclient;

import it.turin.hermesclient.controller.LoginController;
import it.turin.hermesclient.model.LoginModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HermesClientApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HermesClientApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        LoginController controller = fxmlLoader.getController();
        controller.init(new LoginModel());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}
