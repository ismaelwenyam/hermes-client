package it.turin.hermesclient.tasks;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import it.turin.hermesclient.dto.Endpoint;
import it.turin.hermesclient.dto.Request;
import it.turin.hermesclient.dto.Response;
import it.turin.hermesclient.model.LoginModel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;

public class LoginTask implements Callable<Response> {
    private final InetAddress inetAddress;
    private final int port;
    private final LoginModel loginModel;

    public LoginTask (InetAddress address, int port, LoginModel loginModel) {
        this.inetAddress = address;
        this.port = port;
        this.loginModel = loginModel;
    }

    @Override
    public Response call() {
        System.out.println("start login task");
        Socket socket = null;
        PrintStream out = null;
        Scanner in = null;
        Response response = null;
        try {
            socket = new Socket(inetAddress, port);
            out = new PrintStream(socket.getOutputStream(), true);
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("account", loginModel.getEmail());
            Request request = new Request(Endpoint.GET_USER, requestParams, null);
            Gson gson = new Gson();
            String jsonRequest = gson.toJson(request);
            System.out.println("sending login request...");
            out.println(jsonRequest);
            System.out.println("sent login request");
            out.flush();
            in = new Scanner(socket.getInputStream());
            String line = "";
            System.out.println("waiting server response...");
            while (in.hasNextLine()) {
                line = in.nextLine();
            }
            response = gson.fromJson(line, Response.class);
            System.out.println("received response: " + response);
            return response;
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
        return null;
    }
}
