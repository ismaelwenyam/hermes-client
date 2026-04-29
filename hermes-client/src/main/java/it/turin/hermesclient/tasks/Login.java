package it.turin.hermesclient.tasks;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import it.turin.hermesclient.dto.Endpoint;
import it.turin.hermesclient.dto.Request;
import it.turin.hermesclient.dto.Response;
import it.turin.hermesclient.model.ClientModel;
import javafx.application.Platform;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
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
        Socket socket = null;
        PrintWriter out = null;
        Scanner in = null;
        Response response = null;
        try {
            socket = new Socket(InetAddress.getLocalHost(), port);
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("account", clientModel.getEmail());
            Request request = new Request(Endpoint.GET_USER, requestParams, null);
            Gson gson = new Gson();
            String jsonRequest = gson.toJson(request);
            System.out.println("sending login request...");
            out.println(jsonRequest);
            System.out.println("sent login request");
            in = new Scanner(socket.getInputStream());
            String line = "";
            System.out.println("waiting server response...");
            line = in.nextLine();
            if (line == null) {
                Platform.runLater(() -> {
                    clientModel.setShowError(true);
                    clientModel.setErrorMessage("internal server error");
                });
                return;
            }
            response = gson.fromJson(line, Response.class);
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
                    clientModel.setUserNotLoggedIn(false);
                    clientModel.setUserLoggedIn(true);
                });
            } else {
                Platform.runLater(() -> {
                    clientModel.setShowError(true);
                    clientModel.setErrorMessage("invalid mail");
                });
            }
        } catch (IOException e) {
            System.err.println("exception in socket connection: " + e.getMessage());
        } catch (JsonIOException e) {
            System.err.println("json exception: " + e.getMessage());
        } finally {
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.err.println("exception in closing resources: " + e.getMessage());
            }
        }
        System.out.println("end login task");
    }
}
