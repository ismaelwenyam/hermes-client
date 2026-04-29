package it.turin.hermesclient.tasks;

import it.turin.hermesclient.model.HomeModel;
import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Connection implements Runnable {
    private final HomeModel homeModel;
    private final int port;

    public Connection (HomeModel homeModel, int port) {
        this.homeModel = homeModel;
        this.port = port;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket()) {
            System.out.println("checking server status");
            socket.connect(new InetSocketAddress(InetAddress.getLocalHost(), port), 3000);
            homeModel.updateServerStatus(true);
            Platform.runLater(() -> {
                homeModel.setServerOn(Color.GREEN);
            });
        } catch (SocketTimeoutException e) {
            homeModel.updateServerStatus(false);
            System.err.println("socket connection timeout: " + e.getMessage());
            Platform.runLater(() -> {
                homeModel.setServerOn(Color.RED);
            });
        } catch (IOException e) {
            homeModel.updateServerStatus(false);
            System.err.println("io exception in socket connection: " + e.getMessage());
            Platform.runLater(() -> {
                homeModel.setServerOn(Color.RED);
            });
        }
    }
}
