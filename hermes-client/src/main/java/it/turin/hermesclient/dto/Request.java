package it.turin.hermesclient.dto;

import java.util.Map;

/**
 * Contenitore generico di richiesta serializzato in JSON prima dell'invio al
 * server.
 *
 * @param <T> tipo del contenuto opzionale della richiesta
 */
public class Request<T> {

    private Endpoint endpoint;
    private Map<String, Object> requestParameters;
    private T body;

    /**
     * Crea una richiesta per l'endpoint indicato.
     *
     * @param endpoint operazione di destinazione
     * @param requestParameters parametri richiesti dall'endpoint
     * @param body contenuto opzionale della richiesta
     */
    public Request (Endpoint endpoint, Map<String, Object> requestParameters, T body) {
        this.endpoint = endpoint;
        this.requestParameters = requestParameters;
        this.body = body;
    }
}
