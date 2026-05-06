package it.turin.hermesclient.network;

import java.io.*;
import java.net.Socket;

public class ServerConnection {

    public static String sendRequest (String request, String host, int port) throws IOException {
        try (Socket socket = new Socket(host, port)) {
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            out.println(request);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            String jsonResponse = sb.toString();
            if (jsonResponse.isEmpty()) {
                System.out.println("empty response from server");
                return null;
            }
            System.out.println("received response: " + jsonResponse);
            return jsonResponse;
        }
    }
}
