package it.turin.hermesclient.model;

import it.turin.hermesclient.tasks.TasksExecutor;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.paint.Color;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Modello di stato condiviso per il client JavaFX Hermes.
 * <p>
 * Espone proprieta' JavaFX usate dai controller per il binding e conserva le
 * primitive di coordinamento usate dalle attivita' in background.
 */
public class ClientModel {
    private boolean taskStarted = false;
    private final TasksExecutor tasksExecutor = new TasksExecutor();

    private SimpleStringProperty email = new SimpleStringProperty();
    private BooleanProperty showError = new SimpleBooleanProperty(false);
    private SimpleStringProperty errorMessage = new SimpleStringProperty();
    private ObjectProperty<Color> serverStatusColor = new SimpleObjectProperty<>(Color.RED);
    private SimpleBooleanProperty serverLive = new SimpleBooleanProperty(false);
    private final Semaphore poolingSem = new Semaphore(0);
    private final Semaphore countingSem = new Semaphore(0);
    private final ReentrantLock lock = new ReentrantLock();


    //home
    private SimpleStringProperty emailsCount = new SimpleStringProperty("0");
    private final ObservableList<Email> emails = FXCollections.observableArrayList();
    private final SortedList<Email> sortedEmails = new SortedList<>(emails);
    private SimpleStringProperty pageGui = new SimpleStringProperty("1");
    private int page = 0;
    private SimpleBooleanProperty newMessage = new SimpleBooleanProperty(false);
    private String selectedEmailId;

    //compose
    private SimpleStringProperty argument = new SimpleStringProperty("");
    private SimpleStringProperty recipients = new SimpleStringProperty("");
    private SimpleStringProperty textBody = new SimpleStringProperty("");
    private Email mail;
    //

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
     * Restituisce la proprieta' che contiene l'oggetto della composizione.
     *
     * @return proprieta' dell'oggetto
     */
    public SimpleStringProperty argumentProperty() {
        return argument;
    }

    /**
     * Imposta l'oggetto corrente della composizione.
     *
     * @param argument oggetto dell'email
     */
    public void setArgument(String argument) {
        this.argument.set(argument);
    }

    /**
     * Restituisce la proprieta' che contiene i destinatari della composizione.
     *
     * @return proprieta' dei destinatari
     */
    public SimpleStringProperty recipientsProperty() {
        return recipients;
    }

    /**
     * Imposta i destinatari della composizione come stringa separata da punto e
     * virgola.
     *
     * @param recipients testo dei destinatari
     */
    public void setRecipients(String recipients) {
        this.recipients.set(recipients);
    }

    /**
     * Restituisce il numero totale noto di email.
     *
     * @return conteggio delle email come testo per il binding dell'interfaccia
     */
    public String getEmailsCount() {
        return emailsCount.get();
    }

    /**
     * Restituisce la proprieta' che contiene il numero totale noto di email.
     *
     * @return proprieta' del conteggio delle email
     */
    public SimpleStringProperty emailsCountProperty() {
        return emailsCount;
    }

    /**
     * Imposta il numero totale noto di email.
     *
     * @param emailsCount conteggio delle email come testo
     */
    public void setEmailsCount(String emailsCount) {
        this.emailsCount.set(emailsCount);
    }

    /**
     * Restituisce la lista osservabile che rappresenta la casella di posta.
     *
     * @return lista osservabile delle email
     */
    public ObservableList<Email> getEmails() {
        return emails;
    }

    /**
     * Aggiunge un'email alla casella di posta sul thread dell'applicazione
     * JavaFX quando non e' gia' presente.
     *
     * @param email email da aggiungere
     */
    public void addEmail(Email email){
        Platform.runLater(() -> {
            if (!emails.contains(email)) {
                emails.add(email);
            }
        });
    }

    /**
     * Rimuove un'email dalla casella di posta tramite identificativo.
     *
     * @param id identificativo dell'email
     */
    public void removeEmail(long id) {
        for (Email email : emails) {
            if (email.getID() == id){
                Platform.runLater(() -> {
                    emails.remove(email);
                });
            }
        }

    }

    /**
     * Restituisce la vista ordinata della casella di posta usata dal controller
     * principale.
     *
     * @return lista ordinata delle email
     */
    public SortedList<Email> getSortedEmails() {
        return sortedEmails;
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
     * Restituisce la proprieta' che contiene il corpo della composizione.
     *
     * @return proprieta' del corpo della composizione
     */
    public SimpleStringProperty textBodyProperty() {
        return textBody;
    }

    /**
     * Imposta il corpo corrente della composizione.
     *
     * @param textBody testo del corpo della composizione
     */
    public void setTextBody(String textBody) {
        this.textBody.set(textBody);
    }

    /**
     * Restituisce una copia dell'email attualmente preparata per l'invio.
     *
     * @return email da inviare
     */
    public Email getMail() {
        return new Email(mail.getSender(), mail.getRecipients(), mail.getArgument(), mail.getMailBody(), mail.getSentDate());
    }

    /**
     * Memorizza l'email attualmente preparata per l'invio.
     *
     * @param mail email da inviare
     */
    public void setMail(Email mail) {
        this.mail = mail;
    }

    /**
     * Restituisce l'identificativo dell'email selezionata.
     *
     * @return identificativo dell'email selezionata, o stringa vuota se nessuna email e' selezionata
     */
    public String getSelectedEmailId() {
        return selectedEmailId;
    }

    /**
     * Imposta l'identificativo dell'email selezionata.
     *
     * @param selectedEmailId identificativo dell'email selezionata
     */
    public void setSelectedEmailId(String selectedEmailId) {
        this.selectedEmailId = selectedEmailId;
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
     * Restituisce la proprieta' che controlla la visibilita' della notifica di
     * nuovo messaggio.
     *
     * @return proprieta' del nuovo messaggio
     */
    public SimpleBooleanProperty newMessageProperty() {
        return newMessage;
    }

    /**
     * Imposta se deve essere mostrata una notifica di nuovo messaggio.
     *
     * @param newMessage {@code true} quando e' disponibile nuova posta
     */
    public void setNewMessage(boolean newMessage) {
        this.newMessage.set(newMessage);
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

    /**
     * Restituisce il numero della pagina corrente mostrato nell'interfaccia.
     *
     * @return numero di pagina a base uno come testo
     */
    public String getPageGui() {
        return pageGui.get();
    }

    /**
     * Restituisce la proprieta' che contiene il numero della pagina corrente
     * dell'interfaccia.
     *
     * @return proprieta' della pagina dell'interfaccia
     */
    public SimpleStringProperty pageGuiProperty() {
        return pageGui;
    }

    /**
     * Imposta il numero della pagina corrente mostrato nell'interfaccia.
     *
     * @param pageGui numero di pagina a base uno come testo
     */
    public void setPageGui(String pageGui) {
        this.pageGui.set(pageGui);
    }

    /**
     * Restituisce l'indice di pagina a base zero usato per le richieste al
     * server.
     *
     * @return indice di pagina a base zero
     */
    public int getPage() {
        return page;
    }

    /**
     * Imposta l'indice di pagina a base zero usato per le richieste al server.
     *
     * @param page indice di pagina a base zero
     */
    public void setPage(int page) {
        this.page = page;
    }
}
