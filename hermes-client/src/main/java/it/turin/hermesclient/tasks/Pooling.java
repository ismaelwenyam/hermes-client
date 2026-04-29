package it.turin.hermesclient.tasks;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import it.turin.hermesclient.dto.Endpoint;
import it.turin.hermesclient.dto.Request;
import it.turin.hermesclient.dto.Response;
import it.turin.hermesclient.model.Email;
import it.turin.hermesclient.model.HomeModel;
import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Pooling implements Runnable {
    private final HomeModel homeModel;
    private final int port;

    public Pooling(HomeModel homeModel, int port) {
        this.homeModel = homeModel;
        this.port = port;
    }

    @Override
    public void run() {
        if (!homeModel.readServerStatus()) return;
        System.out.println("start pooling task");
        Socket socket = null;
        PrintWriter out = null;
        Scanner in = null;
        Response response = null;
        try {
            socket = new Socket(InetAddress.getLocalHost(), port);
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("account", "francesco@hermes.it"); //Retrieved email from client model
            Request request = new Request(Endpoint.GET_EMAILS, requestParams, null);
            Gson gson = new Gson();
            String jsonRequest = gson.toJson(request);
            System.out.println("sending pool request...");
            out.println(jsonRequest);
            System.out.println("sent pool request");
            in = new Scanner(socket.getInputStream());
            String line = "";
            System.out.println("waiting server response...");
            line = in.nextLine();
            if (line == null) {
                System.out.println("something went wrong in pooling from server");
                return;
            }
            response = gson.fromJson(line, Response.class);
            if (response == null) {
                System.out.println("something went wrong in pooling from server");
                return;
            }
            System.out.println("received response: " + response);
            if (response.getStatusCode() == 200) {
                Type emailListType = new TypeToken<List<Email>>() {}.getType();

                List<Email> emails = gson.fromJson(
                        gson.toJson(response.getResponseBody()),
                        emailListType
                );
                for (Email mail : emails) {
                    homeModel.addEmail(mail.getArgument());
                }
            } else {
                System.out.println("something went wrong in pooling from server");
            }
        } catch (IOException e) {
            System.err.println("exception in socket connection: " + e.getMessage());
        } catch (JsonIOException e) {
            System.err.println("json exception: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("exception in pooling: " + e.getMessage());
        } finally {
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.err.println("exception in closing resources: " + e.getMessage());
            }
        }
        System.out.println("end pooling task");
    }
}
