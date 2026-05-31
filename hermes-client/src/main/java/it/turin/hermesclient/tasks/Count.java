package it.turin.hermesclient.tasks;

import com.google.gson.Gson;
import it.turin.hermesclient.dto.Endpoint;
import it.turin.hermesclient.dto.Request;
import it.turin.hermesclient.dto.Response;
import it.turin.hermesclient.model.ClientModel;
import it.turin.hermesclient.network.ServerConnection;
import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Attivita' in background che richiede il numero totale di email per l'account
 * connesso.
 * <p>
 * L'attivita' attende sul semaforo di conteggio e di solito viene rilasciata dopo
 * un ping riuscito.
 */
public class Count implements Runnable {
    private static final Gson gson = new Gson();
    private final ClientModel clientModel;
    private final int port;

    /**
     * Crea un'attivita' di conteggio.
     *
     * @param clientModel stato condiviso dell'applicazione
     * @param port porta del server
     */
    public Count (ClientModel clientModel, int port) {
        this.clientModel = clientModel;
        this.port = port;
    }

    /**
     * Attende richieste di conteggio, le invia al server e aggiorna il modello
     * con la dimensione piu' recente della casella di posta.
     */
    @Override
    public void run() {
        System.out.println("start count");
        while (true) {
            try {
                clientModel.getCountingSem().acquire();
            } catch (InterruptedException e) {
                System.err.println("exeception occured in counting: " + e.getMessage() + " " + e);
                return;
            }
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("account", clientModel.getEmail());
            Request<Void> request  =new Request<>(Endpoint.COUNT, requestParams, null);
            String jsonRequest = gson.toJson(request);
            String jsonResponse;
            try {
                jsonResponse = ServerConnection.sendRequest(jsonRequest, InetAddress.getLocalHost().getHostAddress(), port);
            } catch (UnknownHostException e) {
                System.err.println("unkown host exception in count: " + e.getMessage());
                return;
            } catch (IOException e) {
                System.err.println("server unavailable in count: " + e.getMessage());
                Platform.runLater(() -> {
                    clientModel.updateServerStatus(false);
                    clientModel.setServerStatusColor(Color.RED);
                    clientModel.setErrorMessage("service unavailable");
                    clientModel.setShowError(true);
                });
                return;
            }
            System.out.println("received count response: " + jsonResponse);
            Response<?> response = gson.fromJson(jsonResponse, Response.class);
            if (response == null) {
                System.out.println("something went wrong in response from server");
                return;
            }
            System.out.println("received count response: " + response);
            if (response.getStatusCode() == 200) {
                Number n = (Number) response.getResponseBody();
                long newCount = n.longValue();
                Platform.runLater(() -> {
                    clientModel.setEmailsCount(String.valueOf(newCount));
                });
                if (newCount > Long.parseLong(clientModel.getEmailsCount())){
                    clientModel.getPoolingSem().release();
                }
            } else {
                System.out.println("server error, status: " + response.getStatusCode());
            }
            System.out.println("end count task");
        }
    }
}
