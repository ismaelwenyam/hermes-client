package it.turin.hermesclient.tasks;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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

/**
 * Attivita' in background che recupera pagine di email per l'account connesso.
 * <p>
 * L'attivita' attende sul semaforo di pooling e aggiunge al modello condiviso le
 * email appena ricevute.
 */
public class Pooling implements Runnable {
    private static final Gson gson = new Gson();
    private final ClientModel clientModel;
    private final int port;

    /**
     * Crea un'attivita' di pooling.
     *
     * @param clientModel stato condiviso dell'applicazione
     * @param port porta del server
     */
    public Pooling(ClientModel clientModel, int port) {
        this.clientModel = clientModel;
        this.port = port;
    }

    /**
     * Attende richieste di pooling, recupera dal server la pagina corrente e
     * aggiorna la casella di posta locale.
     */
    @Override
    public void run() {
        System.out.println("start pooling");
        while (true) {
            System.out.println("pooling...");
            try {
                clientModel.getPoolingSem().acquire();
            } catch (InterruptedException e) {
                System.err.println("exeception occured in pooling: " + e.getMessage() + " " + e);
                return;
            }
            System.out.println("pool-t2");
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("account", clientModel.getEmail());
            requestParams.put("page", String.valueOf(clientModel.getPage()));
            Request<Email> request = new Request<>(Endpoint.GET_EMAILS, requestParams, null);
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
            Response<EmailWrapper> response = null;
            try {
                response = gson.fromJson(jsonResponse, type);
            } catch (JsonSyntaxException e) {
                Platform.runLater(() -> {
                    clientModel.setErrorMessage(e.getMessage());
                    clientModel.setShowError(true);
                });
                return;
            }
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
                }
            } else if (response.getStatusCode() == 404) {
                System.out.println("no emails found");
            } else {
                System.out.println("server error, status: " + response.getStatusCode());
            }
            System.out.println("end pooling task");
        }
    }
}
