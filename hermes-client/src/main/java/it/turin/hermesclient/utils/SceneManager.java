package it.turin.hermesclient.utils;

import it.turin.hermesclient.HermesClientApplication;
import it.turin.hermesclient.controller.ClientController;
import it.turin.hermesclient.model.ClientModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Helper centralizzato per cambiare scena JavaFX mantenendo il modello
 * condiviso del client.
 */
public class SceneManager {
    private static Stage stage;

    /**
     * Memorizza lo stage principale usato dall'applicazione.
     *
     * @param s stage principale
     */
    public static void setStage(Stage s) {
        stage = s;
    }

    /**
     * Carica una vista FXML, inizializza il relativo controller con il modello
     * condiviso e la imposta come scena corrente.
     *
     * @param fxml nome della risorsa FXML
     * @param model stato condiviso dell'applicazione
     * @throws IOException se la risorsa FXML non puo' essere caricata
     */
    public static void switchScene(String fxml, ClientModel model) throws IOException {
        FXMLLoader loader = new FXMLLoader(HermesClientApplication.class.getResource(fxml));
        Scene scene = new Scene(loader.load());
        Object controller = loader.getController();
        if (controller instanceof ClientController clientController) {
            clientController.init(model);
            stage.setOnCloseRequest(e -> {
                clientController.shutdown();
            });
        }
        stage.setScene(scene);
    }
}
