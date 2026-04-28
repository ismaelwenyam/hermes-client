package it.turin.hermesclient.dto;

import java.io.Serial;
import java.io.Serializable;

public class Response implements Serializable {
    @Serial
    private static final long serialVersionUID = -21548922357797L;
    int statusCode;
    Object responseBody;

    public Response(int statusCode, Object responseBody) {
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int getStatusCode() {return this.statusCode;}
    public Object getResponseBody() {return this.responseBody;}

}