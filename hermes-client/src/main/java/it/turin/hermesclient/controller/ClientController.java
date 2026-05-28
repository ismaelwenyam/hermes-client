package it.turin.hermesclient.controller;

import it.turin.hermesclient.model.ClientModel;

/**
 * Classe base per tutti i controller JavaFX che partecipano al ciclo di vita
 * del client Hermes.
 */
public abstract class ClientController {
    /**
     * Inizializza il controller con il modello condiviso del client.
     *
     * @param clientModel stato e servizi condivisi dell'applicazione
     */
    public abstract void init(ClientModel clientModel);

    /**
     * Rilascia le risorse gestite dal controller prima della chiusura della vista.
     */
    public abstract void shutdown();
}
