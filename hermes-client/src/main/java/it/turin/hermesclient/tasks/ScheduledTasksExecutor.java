package it.turin.hermesclient.tasks;

import it.turin.hermesclient.model.ClientModel;
import it.turin.hermesclient.model.HomeModel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledTasksExecutor {
    private final ClientModel clientModel;
    private final HomeModel homeModel;
    private final int port;
    private final ScheduledExecutorService scheduledExecs;

    public ScheduledTasksExecutor(ClientModel clientModel, HomeModel homeModel, int port) {
        this.clientModel = clientModel;
        this.homeModel = homeModel;
        this.port = port;
        this.scheduledExecs = Executors.newScheduledThreadPool(2);
    }

    public void start () {
        Connection connection = new Connection(clientModel, homeModel, port);
        Pooling pooling = new Pooling(homeModel, port);
        scheduledExecs.scheduleAtFixedRate(connection, 0, 20, TimeUnit.SECONDS);
        scheduledExecs.scheduleAtFixedRate(pooling, 0, 10, TimeUnit.SECONDS);
    }

    public void stop () {
        scheduledExecs.shutdown();
    }
}
