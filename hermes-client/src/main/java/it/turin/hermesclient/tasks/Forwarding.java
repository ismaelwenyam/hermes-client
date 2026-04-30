package it.turin.hermesclient.tasks;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import it.turin.hermesclient.dto.Endpoint;
import it.turin.hermesclient.dto.Request;
import it.turin.hermesclient.dto.Response;
import it.turin.hermesclient.model.ComposeModel;
import it.turin.hermesclient.model.Email;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Forwarding implements Runnable {
    private final ComposeModel composeModel;
    private final int port;

    public Forwarding(ComposeModel composeModel, int port) {
        this.composeModel = composeModel;
        this.port = port;
    }


    @Override
    public void run() {
        //TODO if server is offline do something
        System.out.println("start forwarding task");
        Socket socket = null;
        PrintWriter out = null;
        Scanner in = null;
        Response response = null;
        try {
            socket = new Socket(InetAddress.getLocalHost(), port);
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            Request<Email> request = new Request<>(Endpoint.POST_EMAIL, null, composeModel.getMail());
            Gson gson = new Gson();
            String jsonRequest = gson.toJson(request);
            System.out.println("sending request...");
            out.println(jsonRequest);
            System.out.println("sent request");
            in = new Scanner(socket.getInputStream());
            String line = "";
            System.out.println("waiting server response...");
            line = in.nextLine();
            if (line == null) {
                System.out.println("something went wrong in response from server");
                return;
            }
            response = gson.fromJson(line, Response.class);
            if (response == null) {
                System.out.println("something went wrong in response from server");
                return;
            }
            System.out.println("received response: " + response);
            if (response.getStatusCode() == 200) {
                //TODO mail sent
            } else {
                System.out.println("something went wrong in response from server");
            }
        } catch (IOException e) {
            System.err.println("exception in socket connection: " + e.getMessage());
        } catch (JsonIOException e) {
            System.err.println("json exception: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("exception in forwarding: " + e.getMessage());
        } finally {
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.err.println("exception in closing resources: " + e.getMessage());
            }
        }
        System.out.println("end forwarding task");
    }
}
