package it.turin.hermesclient.tasks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TasksExecutor {
    private final ScheduledExecutorService scheduledExecs = Executors.newScheduledThreadPool(1);
    private final ExecutorService exec = Executors.newFixedThreadPool(2);

    public void start (Ping ping,Pooling pooling, Count count) {
        scheduledExecs.scheduleAtFixedRate(ping, 0, 5, TimeUnit.SECONDS);
        exec.execute(pooling);
        exec.execute(count);
    }

    public void shutdown () {
        scheduledExecs.shutdown();
        exec.shutdownNow();
    }
}
