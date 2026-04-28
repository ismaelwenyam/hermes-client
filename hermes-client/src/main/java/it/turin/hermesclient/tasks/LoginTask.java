package it.turin.hermesclient.tasks;

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
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            socket = new Socket(inetAddress, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("account", loginModel.getEmail());
            Request request = new Request(Endpoint.GET_USER, requestParams, null);
            out.writeObject(request);
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            return (Response) in.readObject();
        } catch (IOException e) {
            System.err.println("exception in socket connection: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("exception caught in casting response: " + e.getMessage());
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
