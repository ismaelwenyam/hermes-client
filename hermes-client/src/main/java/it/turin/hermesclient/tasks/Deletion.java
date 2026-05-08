package it.turin.hermesclient.tasks;

import com.google.gson.Gson;
import it.turin.hermesclient.dto.Endpoint;
import it.turin.hermesclient.dto.Request;
import it.turin.hermesclient.dto.Response;
import it.turin.hermesclient.model.ClientModel;
import it.turin.hermesclient.model.Email;
import it.turin.hermesclient.network.ServerConnection;
import javafx.application.Platform;
import javafx.scene.paint.Color;


import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class Deletion implements Runnable {
    private final ClientModel clientModel;
    private final int port;


    public Deletion(ClientModel clientModel, int port) {
        this.clientModel = clientModel;
        this.port = port;
    }

    @Override
    public void run() {
        System.out.println("start deletion task");
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("account", clientModel.getEmail());
        requestParams.put("emailId", clientModel.getSelectedEmailId());
        Request<Email> request = new Request<>(Endpoint.DELETE_EMAIL, requestParams, null);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(request);
        String jsonResponse;
        try {
            jsonResponse = ServerConnection.sendRequest(jsonRequest, InetAddress.getLocalHost().getHostAddress(), port);
        } catch (UnknownHostException e) {
            System.err.println("unkown host exception in pooling: " + e.getMessage());
            return;
        } catch (IOException e) {
            System.err.println("server unavailable in pooling: " + e.getMessage());
            Platform.runLater(() -> {
                clientModel.updateServerStatus(false);
                clientModel.setServerStatusColor(Color.RED);
            });
            return;
        }
        Response response = gson.fromJson(jsonResponse, Response.class);
        if (response == null) {
            System.out.println("something went wrong in response from server");
            return;
        }
        System.out.println("received response: " + response);
        if (response.getStatusCode() == 200) {
            clientModel.removeEmail(Long.parseLong(clientModel.getSelectedEmailId()));
        } else {
            //TODO if couldn't delete, do something
            System.out.println("something went wrong in response from server");
        }
        System.out.println("end deletion task");
    }
}
