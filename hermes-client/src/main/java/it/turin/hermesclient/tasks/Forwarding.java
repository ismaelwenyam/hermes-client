package it.turin.hermesclient.tasks;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.turin.hermesclient.dto.Endpoint;
import it.turin.hermesclient.dto.Request;
import it.turin.hermesclient.dto.Response;
import it.turin.hermesclient.model.ClientModel;
import it.turin.hermesclient.model.ComposeModel;
import it.turin.hermesclient.model.Email;
import it.turin.hermesclient.network.ServerConnection;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Attivita' monouso che invia al server l'email preparata nel modello del client.
 */
public class Forwarding extends Task<Response<?>> {
    private static final Gson gson = new Gson();
    private final ClientModel clientModel;
    private final ComposeModel composeModel;
    private final Email mail;
    private final int port;

    /**
     * Crea un'attivita' di inoltro.
     *
     * @param clientModel stato condiviso dell'applicazione
     * @param composeModel stato specifico della vista Compose
     * @param port porta del server
     */
    public Forwarding(ClientModel clientModel, ComposeModel composeModel, int port) {
        this.clientModel = clientModel;
        this.composeModel = composeModel;
        this.port = port;
        this.mail = composeModel.getMail();
    }


    /**
     * Invia l'email preparata e aggiorna lo stato della composizione in base
     * alla risposta del server.
     */
    @Override
    public Response<?> call() throws IOException {
        System.out.println("start forwarding task");
        Request<Email> request = new Request<>(Endpoint.POST_EMAIL, null, mail);
        String jsonRequest = gson.toJson(request);
        String jsonResponse = ServerConnection.sendRequest(jsonRequest, InetAddress.getLocalHost().getHostAddress(), port);
        System.out.println("received response: " + jsonResponse);
        Response<?> response = gson.fromJson(jsonResponse, Response.class);
        if (response == null) {
            System.out.println("something went wrong in response from server");
            return null;
        }
        System.out.println("end forwarding task");
        return response;
    }

    @Override
    protected void failed() {
        Throwable error = getException();
        clientModel.setErrorMessage("error forwarding mail: " + error.getMessage());
        clientModel.setShowError(true);
    }

    @Override
    protected void succeeded() {
        Response<?> response = getValue();
        System.out.println("received response: " + response);
        if (response.getStatusCode() == 200) {
            composeModel.clearDraft();
            clientModel.setShowError(false);
        } else {
            Type emailListType = new TypeToken<List<String>>() {}.getType();

            List<String> emails = gson.fromJson(
                    gson.toJson(response.getResponseBody()),
                    emailListType
            );
            clientModel.setErrorMessage("not found: " + emails);
            clientModel.setShowError(true);
            composeModel.setArgument(mail.getArgument());
            composeModel.setTextBody(mail.getMailBody());
            System.out.println("something went wrong in response from server");
        }
    }
}
