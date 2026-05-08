package it.turin.hermesclient.tasks;

import com.google.gson.Gson;
import it.turin.hermesclient.dto.Endpoint;
import it.turin.hermesclient.dto.Request;
import it.turin.hermesclient.model.ClientModel;
import it.turin.hermesclient.model.Email;
import it.turin.hermesclient.network.ServerConnection;
import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class Ping implements Runnable {
    private final ClientModel clientModel;
    private final int port;

    public Ping(ClientModel clientModel, int port) {
        this.clientModel = clientModel;
        this.port = port;
    }

    @Override
    public void run() {
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("account", clientModel.getEmail());
        Request<Email> request = new Request<>(Endpoint.PING, requestParams, null);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(request);
        try {
            ServerConnection.sendRequest(jsonRequest, InetAddress.getLocalHost().getHostAddress(), port);
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
