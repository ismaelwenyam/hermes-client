package it.turin.hermesclient.dto;

import java.util.Map;

public class Request<T> {

    private Endpoint endpoint;
    private Map<String, Object> requestParameters;
    private T body;

    public Request (Endpoint endpoint, Map<String, Object> requestParameters, T body) {
        this.endpoint = endpoint;
        this.requestParameters = requestParameters;
        this.body = body;
    }
}
