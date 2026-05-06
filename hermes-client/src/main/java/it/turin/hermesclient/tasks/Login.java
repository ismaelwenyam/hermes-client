package it.turin.hermesclient.tasks;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import it.turin.hermesclient.dto.Endpoint;
import it.turin.hermesclient.dto.Request;
import it.turin.hermesclient.dto.Response;
import it.turin.hermesclient.model.ClientModel;
import it.turin.hermesclient.network.ServerConnection;
import it.turin.hermesclient.utils.SceneManager;
import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Login implements Runnable {
    private final ClientModel clientModel;
    private final int port;

    public Login(ClientModel clientModel, int port) {
        this.clientModel = clientModel;
        this.port = port;
    }

    @Override
    public void run () {
        System.out.println("start login task");
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("account", clientModel.getEmail());
        Request request = new Request(Endpoint.GET_USER, requestParams, null);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(request);
        String jsonResponse;
        try {
            jsonResponse = ServerConnection.sendRequest(jsonRequest, InetAddress.getLocalHost().toString(), port);
        } catch (UnknownHostException e) {
            System.err.println("unkown host exception in loging in: " + e.getMessage());
            Platform.runLater(() -> {
                clientModel.setShowError(true);
                clientModel.setErrorMessage("internal server error");
            });
            return;
        } catch (IOException e) {
            System.err.println("server unavailable in loging in: " + e.getMessage());
            Platform.runLater(() -> {
                clientModel.setShowError(true);
                clientModel.setErrorMessage("internal server error");
            });
            return;
        }
        Response response = gson.fromJson(jsonResponse, Response.class);
        if (response == null) {
            Platform.runLater(() -> {
                clientModel.setShowError(true);
                clientModel.setErrorMessage("internal server error");
            });
            return;
        }
        System.out.println("received response: " + response);
        if (response.getStatusCode() == 200) {
            Platform.runLater(() -> {
                //clientModel.setUserNotLoggedIn(false);
                //clientModel.setUserLoggedIn(true);
                try {
                    SceneManager.switchScene("home-view.fxml", clientModel);
                } catch (IOException e) {
                    System.err.println("execption in switching scene");
                }
            });
        } else {
            Platform.runLater(() -> {
                clientModel.setShowError(true);
                clientModel.setErrorMessage("invalid mail");
            });
        }

        System.out.println("end login task");
    }
}
