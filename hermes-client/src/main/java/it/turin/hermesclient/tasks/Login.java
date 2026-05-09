package it.turin.hermesclient.tasks;

import com.google.gson.Gson;
import it.turin.hermesclient.dto.Endpoint;
import it.turin.hermesclient.dto.Request;
import it.turin.hermesclient.dto.Response;
import it.turin.hermesclient.model.ClientModel;
import it.turin.hermesclient.network.ServerConnection;
import javafx.concurrent.Task;

import java.io.*;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class Login extends Task<Boolean> {
    private final ClientModel clientModel;
    private final int port;

    public Login(ClientModel clientModel, int port) {
        this.clientModel = clientModel;
        this.port = port;
    }

    @Override
    public Boolean call () {
        System.out.println("start login task");
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("account", clientModel.getEmail());
        Request<Void> request = new Request<>(Endpoint.GET_USER, requestParams, null);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(request);
        String jsonResponse;
        try {
            jsonResponse = ServerConnection.sendRequest(jsonRequest, InetAddress.getLocalHost().getHostAddress(), port);
        } catch (IOException e) {
            System.err.println("server unavailable in loging in: " + e.getMessage());
            updateMessage("connection refused");
            return false;
        }
        Response response = gson.fromJson(jsonResponse, Response.class);
        if (response == null) {
            updateMessage("internal server error");
            return false;
        }
        System.out.println("received response: " + response);
        if (response.getStatusCode() == 200) {
            return true;
        }
        updateMessage("email don't exists");
        System.out.println("end login task");
        return false;
    }
}
