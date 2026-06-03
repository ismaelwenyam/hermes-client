package it.turin.hermesclient.model;

import javafx.beans.property.SimpleStringProperty;

/**
 * Stato specifico della vista di composizione.
 * Conserva i campi modificabili del form e l'email pronta per l'invio.
 */
public class ComposeModel {
    private final SimpleStringProperty argument = new SimpleStringProperty("");
    private final SimpleStringProperty recipients = new SimpleStringProperty("");
    private final SimpleStringProperty textBody = new SimpleStringProperty("");
    private Email mail;

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
     * Restituisce una copia difensiva dell'email attualmente preparata per
     * l'invio.
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
     * Pulisce i campi modificabili e annulla l'email preparata.
     */
    public void clearDraft() {
        argument.set("");
        recipients.set("");
        textBody.set("");
        mail = null;
    }
}
