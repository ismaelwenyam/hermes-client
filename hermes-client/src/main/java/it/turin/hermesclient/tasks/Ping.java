package it.turin.hermesclient.tasks;

import it.turin.hermesclient.model.ClientModel;
import it.turin.hermesclient.model.HomeModel;
import it.turin.hermesclient.network.ServerConnection;
import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.InetAddress;

public class Ping implements Runnable {
    private final ClientModel clientModel;
    private final HomeModel homeModel;
    private final int port;

    public Ping(ClientModel clientModel, HomeModel homeModel, int port) {
        this.clientModel = clientModel;
        this.homeModel = homeModel;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            ServerConnection.ping(InetAddress.getLocalHost().toString(), port, 3000);
            Platform.runLater(() -> {
                homeModel.updateServerStatus(true);
                clientModel.setServerOn(Color.GREEN);
            });
        } catch (IOException e) {
            System.err.println("socket connection timeout: " + e.getMessage());
            Platform.runLater(() -> {
                homeModel.updateServerStatus(false);
                clientModel.setServerOn(Color.RED);
            });
        }
    }
}
