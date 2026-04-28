package it.turin.hermesclient.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class Request implements Serializable {
    @Serial
    private static final long serialVersionUID = -21548922354897L;

    private final Endpoint endpoint;
    private final Map<String, Object> requestParameters;
    private final Object body;

    public Request (Endpoint endpoint, Map<String, Object> requestParameters, Object body) {
        this.endpoint = endpoint;
        this.requestParameters = requestParameters;
        this.body = body;
    }

    public Endpoint getEndpoint() {return this.endpoint;}
    public String getRequestParameter(String par) {
        return (String) requestParameters.get(par);
    }
    public Object getBody() {return this.body;}

    public boolean validParams (String... params) {
        for (String param:
                params) {
            if (param == null || param.isBlank()) return false;
        }
        return true;
    }
    public boolean validObject() {
        throw new UnsupportedOperationException("TODO");
    }
}
