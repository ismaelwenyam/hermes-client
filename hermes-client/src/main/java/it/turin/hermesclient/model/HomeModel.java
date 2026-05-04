package it.turin.hermesclient.model;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.util.concurrent.locks.ReentrantLock;

public class HomeModel {
    private boolean serverLive = false;
    private final ReentrantLock lock = new ReentrantLock();

    private final ObservableList<Email> emails = FXCollections.observableArrayList();


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

    public ObservableList<Email> getEmails() {
        return emails;
    }

    public void addEmail(Email email){
        Platform.runLater(() -> {
            if (!emails.contains(email)) {
                emails.add(email);
            }
        });
    }
}
