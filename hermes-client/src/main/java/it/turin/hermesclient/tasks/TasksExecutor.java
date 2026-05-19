package it.turin.hermesclient.tasks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TasksExecutor {
    private final ScheduledExecutorService scheduledExecs = Executors.newScheduledThreadPool(2);
    private final ExecutorService exec = Executors.newFixedThreadPool(1);

    public void start (Ping ping,Pooling pooling, Count count) {
        scheduledExecs.scheduleAtFixedRate(ping, 0, 2, TimeUnit.SECONDS);
        scheduledExecs.scheduleAtFixedRate(count, 0, 10, TimeUnit.SECONDS);
        exec.execute(pooling);
    }

    public void shutdown () {
        scheduledExecs.shutdown();
        exec.shutdown();
    }
}
