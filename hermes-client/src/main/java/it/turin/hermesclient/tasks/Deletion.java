package it.turin.hermesclient.tasks;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import it.turin.hermesclient.dto.Endpoint;
import it.turin.hermesclient.dto.Request;
import it.turin.hermesclient.dto.Response;
import it.turin.hermesclient.model.ClientModel;
import it.turin.hermesclient.model.Email;
import it.turin.hermesclient.model.HomeModel;
import it.turin.hermesclient.network.ServerConnection;
import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Deletion implements Runnable {
    private final ClientModel clientModel;
    private final HomeModel homeModel;
    private final int port;


    public Deletion(ClientModel clientModel, HomeModel homeModel, int port) {
        this.clientModel = clientModel;
        this.homeModel = homeModel;
        this.port = port;
    }

    @Override
    public void run() {
        System.out.println("start deletion task");
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("account", clientModel.getEmail());
        requestParams.put("emailId", homeModel.getSelectedEmailId());
        Request<Email> request = new Request<>(Endpoint.DELETE_EMAIL, requestParams, null);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(request);
        String jsonResponse;
        try {
            jsonResponse = ServerConnection.sendRequest(jsonRequest, InetAddress.getLocalHost().toString(), port);
        } catch (UnknownHostException e) {
            System.err.println("unkown host exception in pooling: " + e.getMessage());
            return;
        } catch (IOException e) {
            System.err.println("server unavailable in pooling: " + e.getMessage());
            Platform.runLater(() -> {
                homeModel.updateServerStatus(false);
                clientModel.setServerOn(Color.RED);
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
            homeModel.removeEmail(Long.parseLong(homeModel.getSelectedEmailId()));
        } else {
            //TODO if couldn't delete, do something
            System.out.println("something went wrong in response from server");
        }
        System.out.println("end deletion task");
    }
}
