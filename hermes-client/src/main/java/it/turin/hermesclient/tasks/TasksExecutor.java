package it.turin.hermesclient.tasks;

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
     * @param ping attivita' periodica di verifica della raggiungibilita' del server
     * @param pooling attivita' di recupero della casella di posta
     * @param count attivita' di conteggio della casella di posta
     */
    public void start (Ping ping,Pooling pooling, Count count) {
        scheduledExecs.scheduleAtFixedRate(ping, 0, 5, TimeUnit.SECONDS);
        exec.execute(pooling);
        exec.execute(count);
    }

    /**
     * Arresta tutti gli executor service gestiti da questa istanza.
     */
    public void shutdown () {
        scheduledExecs.shutdown();
        exec.shutdownNow();
    }
}
