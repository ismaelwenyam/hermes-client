package it.turin.hermesclient.tasks;

import com.google.gson.Gson;
import it.turin.hermesclient.dto.Endpoint;
import it.turin.hermesclient.dto.Request;
import it.turin.hermesclient.dto.Response;
import it.turin.hermesclient.model.ClientModel;
import it.turin.hermesclient.network.ServerConnection;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;


import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Attivita' monouso che elimina sul server l'email attualmente selezionata e la
 * rimuove dalla casella locale quando l'operazione riesce.
 */
public class Deletion extends Task<Response<?>> {
    private static final Gson gson = new Gson();

    private final ClientModel clientModel;
    private final int port;
    private final String emailId;


    /**
     * Crea un'attivita' di eliminazione.
     *
     * @param clientModel stato condiviso dell'applicazione
     * @param port porta del server
     */
    public Deletion(ClientModel clientModel, int port) {
        this.clientModel = clientModel;
        this.port = port;
        this.emailId = clientModel.getSelectedEmailId();
    }

    /**
     * Invia la richiesta di eliminazione per l'email selezionata e aggiorna il
     * modello locale dopo una risposta positiva.
     */
    @Override
    protected Response<?> call() throws Exception {
        System.out.println("start deletion task");
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("account", clientModel.getEmail());
        requestParams.put("emailId", emailId);
        Request<?> request = new Request<>(Endpoint.DELETE_EMAIL, requestParams, null);
        String jsonRequest = gson.toJson(request);
        String jsonResponse = ServerConnection.sendRequest(jsonRequest, InetAddress.getLocalHost().getHostAddress(), port);
        Response<?> response = gson.fromJson(jsonResponse, Response.class);
        if (response == null) {
            System.out.println("something went wrong in response from server");
            return null;
        }
        System.out.println("received response: " + response);
        return response;
    }

    @Override
    protected void succeeded() {
        Response<?> response = getValue();
        if (response == null) {
            return;
        }
        if (response.getStatusCode() == 200) {
            clientModel.removeEmail(Long.parseLong(emailId));
        } else {
            clientModel.setErrorMessage("something went wrong in response from server");
            clientModel.setShowError(true);
        }
        System.out.println("end deletion task");
    }

    @Override
    protected void failed() {
        Throwable error = getException();
        clientModel.setErrorMessage("error in deletion: " + error);
        clientModel.setShowError(true);
    }
}
