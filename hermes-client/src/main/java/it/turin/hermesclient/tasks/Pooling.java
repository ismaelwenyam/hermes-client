package it.turin.hermesclient.tasks;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.turin.hermesclient.dto.EmailWrapper;
import it.turin.hermesclient.dto.Endpoint;
import it.turin.hermesclient.dto.Request;
import it.turin.hermesclient.dto.Response;
import it.turin.hermesclient.model.ClientModel;
import it.turin.hermesclient.model.Email;
import it.turin.hermesclient.network.ServerConnection;
import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.io.*;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pooling implements Runnable {
    private final ClientModel clientModel;
    private final int port;

    public Pooling(ClientModel clientModel, int port) {
        this.clientModel = clientModel;
        this.port = port;
    }

    @Override
    public void run() {
        if (!clientModel.isServerLive()) return;
        //TODO pooling must execute only if the number of mails present are less than the desired number
        System.out.println("start pooling task");
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("account", clientModel.getEmail());
        requestParams.put("page", clientModel.getPage());
        Request<Email> request = new Request<>(Endpoint.GET_EMAILS, requestParams, null);
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
        System.out.println("received response: " + jsonResponse);
        Type type = new TypeToken<Response<EmailWrapper>>() {}.getType();
        Response<EmailWrapper> response = gson.fromJson(jsonResponse, type);
        if (response == null) {
            System.out.println("failed to parse response");
            return;
        }
        if (response.getStatusCode() == 200) {
            EmailWrapper emailWrapper = response.getResponseBody();

            if (emailWrapper != null && emailWrapper.getEmails() != null) {
                List<Email> emails = emailWrapper.getEmails();
                for (Email mail : emails) {
                    clientModel.addEmail(mail);
                }
                Platform.runLater(() -> {
                    if (Long.parseLong(clientModel.getEmailsCount()) < emailWrapper.getEmailsCount()) {
                        clientModel.setNewMessage(true);
                    }
                    clientModel.setEmailsCount(String.valueOf(emailWrapper.getEmailsCount()));
                });
            }
        } else if (response.getStatusCode() == 404) {
            System.out.println("no emails found");
        } else {
            System.out.println("server error, status: " + response.getStatusCode());
        }
        System.out.println("end pooling task");
    }
}
