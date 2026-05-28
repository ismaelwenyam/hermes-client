package it.turin.hermesclient.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * Contenitore generico di risposta ricevuto dal server.
 *
 * @param <T> tipo del contenuto della risposta
 */
public class Response<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -21548922357797L;
    int statusCode;
    T responseBody;

    /**
     * Crea un contenitore di risposta.
     *
     * @param statusCode codice di stato del protocollo restituito dal server
     * @param responseBody contenuto della risposta
     */
    public Response(int statusCode, T responseBody) {
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    /**
     * Restituisce il codice di stato del protocollo.
     *
     * @return codice di stato restituito dal server
     */
    public int getStatusCode() {return this.statusCode;}

    /**
     * Restituisce il contenuto della risposta.
     *
     * @return contenuto della risposta
     */
    public T getResponseBody() {return this.responseBody;}

    @Override
    public String toString() {
        return "Response{" +
                "statusCode=" + statusCode +
                ", responseBody=" + responseBody +
                '}';
    }
}
