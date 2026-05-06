package it.turin.hermesclient.tasks;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import it.turin.hermesclient.dto.Endpoint;
import it.turin.hermesclient.dto.Request;
import it.turin.hermesclient.dto.Response;
import it.turin.hermesclient.model.ClientModel;
import it.turin.hermesclient.model.ComposeModel;
import it.turin.hermesclient.model.Email;
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
import java.util.List;
import java.util.Scanner;

public class Forwarding implements Runnable {
    private final ClientModel clientModel;
    private final ComposeModel composeModel;
    private final int port;

    public Forwarding(ClientModel clientModel, ComposeModel composeModel, int port) {
        this.clientModel = clientModel;
        this.composeModel = composeModel;
        this.port = port;
    }


    @Override
    public void run() {
        //TODO if server is offline do something
        System.out.println("start forwarding task");
        Request<Email> request = new Request<>(Endpoint.POST_EMAIL, null, composeModel.getMail());
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
        System.out.println("received response: " + jsonResponse);
        Response response = gson.fromJson(jsonResponse, Response.class);
        if (response == null) {
            System.out.println("something went wrong in response from server");
            return;
        }
        System.out.println("received response: " + response);
        if (response.getStatusCode() == 200) {
            Platform.runLater(() -> {
                clientModel.setArgument("");
                clientModel.setRecipients("");
                composeModel.setTextBody("");
            });
        } else {
            Type emailListType = new TypeToken<List<String>>() {}.getType();

            List<String> emails = gson.fromJson(
                    gson.toJson(response.getResponseBody()),
                    emailListType
            );
            Platform.runLater(() -> {
                clientModel.setErrorMessage("not found: " + emails);
                clientModel.setShowError(true);
                clientModel.setArgument(composeModel.getMail().getArgument());
                String unvalidEmails = "";
                for (String e : emails){
                    unvalidEmails = unvalidEmails.concat(e + ";");
                }
                clientModel.setRecipients(unvalidEmails);
                composeModel.setTextBody(composeModel.getMail().getMailBody());
            });
            System.out.println("something went wrong in response from server");
        }
        System.out.println("end forwarding task");
    }
}
