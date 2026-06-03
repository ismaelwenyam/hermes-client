package it.turin.hermesclient.model;

import it.turin.hermesclient.tasks.TasksExecutor;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Stato condiviso per il client JavaFX Hermes.
 * <p>
 * Espone solo i dati che devono attraversare i cambi di scena, mentre lo
 * stato specifico di Home e Compose vive nei rispettivi model dedicati.
 * </p>
 */
public class ClientModel {
    private final TasksExecutor tasksExecutor = new TasksExecutor();
    private final HomeModel homeModel = new HomeModel();
    private final ComposeModel composeModel = new ComposeModel();

    private boolean taskStarted = false;

    private final SimpleStringProperty email = new SimpleStringProperty();
    private final BooleanProperty showError = new SimpleBooleanProperty(false);
    private final SimpleStringProperty errorMessage = new SimpleStringProperty();
    private final ObjectProperty<Color> serverStatusColor = new SimpleObjectProperty<>(Color.RED);
    private final SimpleBooleanProperty serverLive = new SimpleBooleanProperty(false);
    private final Semaphore poolingSem = new Semaphore(0);
    private final Semaphore countingSem = new Semaphore(0);
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Restituisce il modello specifico della vista Home.
     *
     * @return modello Home
     */
    public HomeModel getHomeModel() {
        return homeModel;
    }

    /**
     * Restituisce il modello specifico della vista Compose.
     *
     * @return modello Compose
     */
    public ComposeModel getComposeModel() {
        return composeModel;
    }

    /**
     * Restituisce l'indirizzo email dell'utente connesso.
     *
     * @return email dell'account corrente
     */
    public String getEmail() {
        return email.get();
    }

    /**
     * Restituisce la proprieta' che contiene l'indirizzo email dell'utente
     * connesso.
     *
     * @return proprieta' dell'email
     */
    public SimpleStringProperty emailProperty() {
        return email;
    }

    /**
     * Imposta l'indirizzo email dell'utente connesso.
     *
     * @param email email dell'account
     */
    public void setEmail(String email) {
        this.email.set(email);
    }

    /**
     * Restituisce la proprieta' che controlla la visibilita' dell'errore.
     *
     * @return proprieta' di visibilita' dell'errore
     */
    public BooleanProperty showErrorProperty() {
        return showError;
    }

    /**
     * Imposta la visibilita' dell'errore.
     *
     * @param showError {@code true} per mostrare un errore
     */
    public void setShowError(boolean showError) {
        this.showError.set(showError);
    }

    /**
     * Restituisce la proprieta' che contiene il messaggio di errore corrente.
     *
     * @return proprieta' del messaggio di errore
     */
    public SimpleStringProperty errorMessageProperty() {
        return errorMessage;
    }

    /**
     * Imposta il messaggio di errore corrente.
     *
     * @param errorMessage testo dell'errore
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage.set(errorMessage);
    }

    /**
     * Restituisce la proprieta' usata per collegare il colore dello stato del
     * server nell'interfaccia.
     *
     * @return proprieta' del colore dello stato del server
     */
    public ObjectProperty<Color> serverStatusColorProperty() {
        return serverStatusColor;
    }

    /**
     * Imposta il colore usato dall'interfaccia per rappresentare lo stato del
     * server.
     *
     * @param serverStatusColor colore dello stato del server
     */
    public void setServerStatusColor(Color serverStatusColor) {
        this.serverStatusColor.set(serverStatusColor);
    }

    /**
     * Aggiorna in modo thread-safe lo stato di raggiungibilita' del server.
     *
     * @param status nuovo stato di raggiungibilita' del server
     */
    public void updateServerStatus(boolean status) {
        lock.lock();
        try {
            serverLive.set(status);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Restituisce l'esecutore che gestisce attivita' ricorrenti e in background.
     *
     * @return esecutore delle attivita'
     */
    public TasksExecutor getTasksExecutor() {
        return tasksExecutor;
    }

    /**
     * Restituisce se l'insieme delle attivita' ricorrenti e' gia' stato avviato.
     *
     * @return {@code true} quando le attivita' in background sono in esecuzione
     */
    public boolean isTaskStarted() {
        return taskStarted;
    }

    /**
     * Segna l'insieme delle attivita' ricorrenti come avviato o arrestato.
     *
     * @param taskStarted flag di avvio delle attivita'
     */
    public void setTaskStarted(boolean taskStarted) {
        this.taskStarted = taskStarted;
    }

    /**
     * Restituisce il semaforo usato per risvegliare l'attivita' di pooling.
     *
     * @return semaforo di pooling
     */
    public Semaphore getPoolingSem() {
        return poolingSem;
    }

    /**
     * Restituisce il semaforo usato per risvegliare l'attivita' di conteggio.
     *
     * @return semaforo di conteggio
     */
    public Semaphore getCountingSem() {
        return countingSem;
    }
}
