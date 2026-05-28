package it.turin.hermesclient.network;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Metodi di utilita' per la comunicazione socket a basso livello con il server
 * Hermes.
 */
public class ServerConnection {

    /**
     * Apre un socket, invia una richiesta JSON su una singola riga e legge la
     * risposta completa.
     *
     * @param request richiesta JSON da inviare
     * @param host host del server
     * @param port porta del server
     * @return risposta JSON, oppure {@code null} se il server restituisce un corpo vuoto
     * @throws IOException se il socket non puo' essere aperto o letto
     */
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

    /**
     * Verifica se una connessione socket puo' essere stabilita entro il timeout.
     *
     * @param host host del server
     * @param port porta del server
     * @param timeout timeout di connessione in millisecondi
     * @throws IOException se il server non puo' essere raggiunto
     */
    public static void ping (String host, int port, int timeout) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
        }
    }
}
