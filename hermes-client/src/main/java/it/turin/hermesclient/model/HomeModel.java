package it.turin.hermesclient.model;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Stato specifico della vista Home.
 * Contiene la paginazione, la lista delle email e i flag usati dal controller
 * principale e dai task di polling e conteggio.
 */
public class HomeModel {
    private final SimpleStringProperty emailsCount = new SimpleStringProperty("0");
    private final ObservableList<Email> emails = FXCollections.observableArrayList();
    private final SortedList<Email> sortedEmails = new SortedList<>(emails);
    private final SimpleStringProperty pageGui = new SimpleStringProperty("1");
    private int serverPage = 0;
    private final SimpleBooleanProperty newMessage = new SimpleBooleanProperty(false);
    private String selectedEmailId;
    private boolean fetchNewMail = false;

    /**
     * Crea il modello Home e imposta l'ordinamento delle email per data di invio
     * decrescente.
     */
    public HomeModel() {
        sortedEmails.setComparator(Email::compareTo);
    }

    /**
     * Restituisce il numero totale noto di email.
     *
     * @return conteggio delle email come testo
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
    public void addEmail(Email email) {
        Platform.runLater(() -> {
            if (!emails.contains(email)) {
                emails.add(email);
            }
        });
    }

    /**
     * Aggiunge piu' email alla casella di posta con un solo aggiornamento della
     * lista osservabile.
     *
     * @param newEmails email da aggiungere
     */
    public void addEmails(Collection<Email> newEmails) {
        if (newEmails == null || newEmails.isEmpty()) {
            return;
        }
        Platform.runLater(() -> {
            List<Email> toAdd = new ArrayList<>();
            for (Email email : newEmails) {
                if (!emails.contains(email)) {
                    toAdd.add(email);
                }
            }
            if (!toAdd.isEmpty()) {
                emails.addAll(toAdd);
            }
        });
    }

    /**
     * Rimuove un'email dalla casella di posta tramite identificativo.
     *
     * @param id identificativo dell'email
     */
    public void removeEmail(long id) {
        emails.removeIf(email -> email.getID() == id);
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
    public int getServerPage() {
        return serverPage;
    }

    /**
     * Imposta l'indice di pagina a base zero usato per le richieste al server.
     *
     * @param serverPage indice di pagina a base zero
     */
    public void setServerPage(int serverPage) {
        this.serverPage = serverPage;
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
     * Restituisce se e' disponibile una nuova email da segnalare.
     *
     * @return {@code true} quando la notifica di nuovo messaggio e' attiva
     */
    public boolean isNewMessage() {
        return newMessage.get();
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
     * Restituisce se la prossima richiesta di pooling deve marcare una nuova
     * email.
     *
     * @return {@code true} quando la prossima sincronizzazione deve chiedere i
     * nuovi messaggi
     */
    public boolean isFetchNewMail() {
        return fetchNewMail;
    }

    /**
     * Imposta se la prossima richiesta di pooling deve marcare una nuova email.
     *
     * @param fetchNewMail flag per la prossima sincronizzazione
     */
    public void setFetchNewMail(boolean fetchNewMail) {
        this.fetchNewMail = fetchNewMail;
    }
}
