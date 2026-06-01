package it.turin.hermesclient.tasks;

import it.turin.hermesclient.model.ClientModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Gestisce gli executor service usati dalle attivita' in background del client.
 */
public class TasksExecutor {
    private final ScheduledExecutorService scheduledExecs = Executors.newScheduledThreadPool(1);
    private final ExecutorService exec = Executors.newFixedThreadPool(2);

    /**
     * Avvia il ping periodico e le attivita' di pooling e conteggio a lunga durata.
     *
     * @param clientModel modello generale del client
     * @param port porta di connessione
     */
    public void start (ClientModel clientModel, int port) {
        scheduledExecs.scheduleAtFixedRate(new Ping(clientModel, port), 0, 10, TimeUnit.SECONDS);
        exec.execute(new Pooling(clientModel, port));
        exec.execute(new Count(clientModel, port));
    }

    /**
     * Arresta tutti gli executor service gestiti da questa istanza.
     */
    public void shutdown () {
        scheduledExecs.shutdown();
        exec.shutdownNow();
    }
}
