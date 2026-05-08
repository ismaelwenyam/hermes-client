package it.turin.hermesclient.tasks;

import it.turin.hermesclient.model.ClientModel;
import it.turin.hermesclient.network.ServerConnection;
import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.InetAddress;

public class Ping implements Runnable {
    private final ClientModel clientModel;
    private final int port;

    public Ping(ClientModel clientModel, int port) {
        this.clientModel = clientModel;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            ServerConnection.ping(InetAddress.getLocalHost().getHostAddress(), port, 3000);
            Platform.runLater(() -> {
                clientModel.updateServerStatus(true);
                clientModel.setServerStatusColor(Color.GREEN);
            });
        } catch (IOException e) {
            System.err.println("socket connection timeout: " + e.getMessage());
            Platform.runLater(() -> {
                clientModel.updateServerStatus(false);
                clientModel.setServerStatusColor(Color.RED);
            });
        }
    }
}
