package it.turin.hermesclient.tasks;

import it.turin.hermesclient.model.ClientModel;
import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Connection implements Runnable {
    private final ClientModel clientModel;
    private final int port;

    public Connection (ClientModel clientModel, int port) {
        this.clientModel = clientModel;
        this.port = port;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket()) {
            System.out.println("checking server status");
            socket.connect(new InetSocketAddress(InetAddress.getLocalHost(), port), 3000);
            Platform.runLater(() -> {
                clientModel.setServerOn(Color.GREEN);
            });
        } catch (SocketTimeoutException e) {
            System.err.println("socket connection timeout: " + e.getMessage());
            Platform.runLater(() -> {
                clientModel.setServerOn(Color.RED);
            });
        } catch (IOException e) {
            System.err.println("io exception in socket connection: " + e.getMessage());
            Platform.runLater(() -> {
                clientModel.setServerOn(Color.RED);
            });
        }
    }
}
