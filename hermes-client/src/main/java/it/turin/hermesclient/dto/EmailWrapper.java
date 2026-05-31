package it.turin.hermesclient.dto;

import it.turin.hermesclient.model.Email;

import java.util.List;

/**
 * DTO usato per trasportare una pagina di email insieme alla dimensione totale
 * della casella di posta.
 */
public class EmailWrapper {

    private long emailsCount;
    private boolean newMessage;
    private List<Email> emails;

    /**
     * Restituisce il numero totale di email disponibili sul server.
     *
     * @return conteggio totale delle email
     */
    public long getEmailsCount() {
        return emailsCount;
    }

    /**
     * Imposta il numero totale di email disponibili sul server.
     *
     * @param emailsCount conteggio totale delle email
     */
    public void setEmailsCount(long emailsCount) {
        this.emailsCount = emailsCount;
    }

    /**
     * Restituisce le email contenute nella pagina di risposta corrente.
     *
     * @return email della pagina
     */
    public List<Email> getEmails() {
        return emails;
    }

    /**
     * Imposta le email contenute nella pagina di risposta corrente.
     *
     * @param emails email della pagina
     */
    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }

    public boolean isNewMessage() {
        return newMessage;
    }

    public void setNewMessage(boolean newMessage) {
        this.newMessage = newMessage;
    }
}
