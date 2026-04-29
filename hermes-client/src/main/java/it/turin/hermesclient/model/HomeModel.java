package it.turin.hermesclient.model;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.util.concurrent.locks.ReentrantLock;

public class HomeModel {
    private ObjectProperty<Color> serverOn = new SimpleObjectProperty<>(Color.RED);
    private boolean serverLive = false;

    private final ReentrantLock lock = new ReentrantLock();
    private ObservableList<String> emails = FXCollections.observableArrayList();

    public Color getServerOn() {
        return serverOn.get();
    }
    public ObjectProperty<Color> serverOnProperty() {
        return serverOn;
    }
    public void setServerOn(Color serverOn) {
        this.serverOn.set(serverOn);
    }


    public boolean readServerStatus() {
        lock.lock();
        try {
            return serverLive;
        } finally {
            lock.unlock();
        }
    }

    public void updateServerStatus(boolean status) {
        lock.lock();
        try {
            serverLive = status;
        } finally {
            lock.unlock();
        }
    }

    public ObservableList<String> getEmails() {
        return emails;
    }

    public void addEmail(String emailArgument){
        Platform.runLater(() -> {
            if (!emails.contains(emailArgument)) {
                emails.add(emailArgument);
            }
        });
    }
}
