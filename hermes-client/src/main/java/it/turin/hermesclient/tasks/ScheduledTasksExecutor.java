package it.turin.hermesclient.tasks;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledTasksExecutor {
    private final ScheduledExecutorService scheduledExecs = Executors.newScheduledThreadPool(2);

    public void start (Ping ping,Pooling pooling) {
        scheduledExecs.scheduleAtFixedRate(ping, 0, 20, TimeUnit.SECONDS);
        scheduledExecs.scheduleAtFixedRate(pooling, 0, 2, TimeUnit.SECONDS);
    }

    public void shutdown () {
        scheduledExecs.shutdown();
    }
}
