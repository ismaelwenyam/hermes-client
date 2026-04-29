package it.turin.hermesclient.tasks;

import it.turin.hermesclient.model.ClientModel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledTasksExecutor {
    private final ClientModel clientModel;
    private final int port;
    private final ScheduledExecutorService scheduledExecs;

    public ScheduledTasksExecutor(ClientModel clientModel, int port) {
        this.clientModel = clientModel;
        this.port = port;
        this.scheduledExecs = Executors.newScheduledThreadPool(2);
    }

    public void start () {
        Connection connection = new Connection(clientModel, port);
        scheduledExecs.scheduleAtFixedRate(connection, 0, 10, TimeUnit.SECONDS);
    }

    public void stop () {
        scheduledExecs.shutdown();
    }
}
