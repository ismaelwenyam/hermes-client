package it.turin.hermesclient.tasks;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledTasksExecutor {
    private final Ping ping;
    private final Pooling pooling;
    private final ScheduledExecutorService scheduledExecs;

    public ScheduledTasksExecutor(Ping ping,Pooling pooling) {
        this.ping = ping;
        this.pooling = pooling;
        this.scheduledExecs = Executors.newScheduledThreadPool(2);
    }

    public void start () {
        scheduledExecs.scheduleAtFixedRate(ping, 0, 20, TimeUnit.SECONDS);
        scheduledExecs.scheduleAtFixedRate(pooling, 0, 10, TimeUnit.SECONDS);
    }

    public void stop () {
        scheduledExecs.shutdown();
    }
}
