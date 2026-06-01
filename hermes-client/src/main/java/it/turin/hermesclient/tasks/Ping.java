package it.turin.hermesclient.tasks;

import com.google.gson.Gson;
import it.turin.hermesclient.dto.Endpoint;
import it.turin.hermesclient.dto.Request;
import it.turin.hermesclient.model.ClientModel;
import it.turin.hermesclient.model.Email;
import it.turin.hermesclient.network.ServerConnection;
import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Attivita' periodica che controlla la raggiungibilita' del server e aggiorna lo
 * stato mostrato dall'interfaccia del client.
 */
public class Ping implements Runnable {
    private static final Gson gson = new Gson();
    private final ClientModel clientModel;
    private final int port;

    /**
     * Crea un'attivita' di ping.
     *
     * @param clientModel stato condiviso dell'applicazione
     * @param port porta del server
     */
    public Ping(ClientModel clientModel, int port) {
        this.clientModel = clientModel;
        this.port = port;
    }

    /**
     * Invia una richiesta di ping, aggiorna lo stato del server e risveglia il
     * l'attivita' di conteggio quando il server e' raggiungibile.
     */
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " - started");
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("account", clientModel.getEmail());
        Request<Email> request = new Request<>(Endpoint.PING, requestParams, null);
        String jsonRequest = gson.toJson(request);
        try {
            ServerConnection.sendRequest(jsonRequest, InetAddress.getLocalHost().getHostAddress(), port);
            clientModel.getCountingSem().release();
            Platform.runLater(() -> {
                clientModel.setShowError(false);
                clientModel.updateServerStatus(true);
                clientModel.setServerStatusColor(Color.GREEN);
            });
        } catch (Exception e) {
            System.err.println("ping failed: " + e.getMessage());
            Platform.runLater(() -> {
                clientModel.updateServerStatus(false);
                clientModel.setServerStatusColor(Color.RED);
                clientModel.setErrorMessage("ping " + e.getMessage());
                clientModel.setShowError(true);
            });
        }
        System.out.println(Thread.currentThread().getName() + " - completed");
    }
}
