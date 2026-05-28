package it.turin.hermesclient.model;

import java.io.Serializable;
import java.util.*;

/**
 * Rappresentazione immutabile di un messaggio email scambiato tra client e
 * server.
 */
public class Email implements Serializable, Comparable<Email> {

    private static final long serialVersionUID = -21548911354897L;

    private long ID;
    private final String sender;
    private final List<String> recipients;
    private final String argument;
    private final String mailBody;
    private final Date sentDate;

    /**
     * Crea un messaggio email.
     *
     * @param sender indirizzo del mittente
     * @param recipients indirizzi dei destinatari
     * @param argument oggetto dell'email
     * @param mailBody corpo dell'email
     * @param sentDate data di invio
     */
    public Email(String sender, List<String> recipients, String argument, String mailBody, Date sentDate) {
        this.sender = sender;
        this.recipients = recipients;
        this.argument = argument;
        this.mailBody = mailBody;
        this.sentDate = sentDate;
    }

    /**
     * Restituisce l'identificativo dell'email lato server.
     *
     * @return identificativo dell'email
     */
    public long getID() {return this.ID;}

    /**
     * Imposta l'identificativo dell'email lato server.
     *
     * @param id identificativo dell'email
     */
    public void setID(long id){this.ID = id;}

    /**
     * Restituisce la data di invio.
     *
     * @return data di invio
     */
    public Date getSentDate() {return this.sentDate;}

    /**
     * Restituisce l'indirizzo del mittente.
     *
     * @return indirizzo del mittente
     */
    public String getSender() {return this.sender;}

    /**
     * Restituisce l'oggetto dell'email.
     *
     * @return oggetto dell'email
     */
    public String getArgument() {return this.argument;}

    /**
     * Restituisce il corpo dell'email.
     *
     * @return corpo dell'email
     */
    public String getMailBody() {return this.mailBody;}

    /**
     * Restituisce una copia difensiva della lista dei destinatari.
     *
     * @return indirizzi dei destinatari
     */
    public List<String> getRecipients(){
        return new ArrayList<>(this.recipients);
    }

    @Override
    public String toString() {
        return "Email{" +
                "ID='" + ID + '\'' +
                ", sender='" + sender + '\'' +
                ", recipients=" + recipients +
                ", argument='" + argument + '\'' +
                ", mailBody='" + mailBody + '\'' +
                ", sentDate=" + sentDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Email email)) return false;
        return ID == email.ID;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ID);
    }

    /**
     * Confronta le email per data di invio in ordine decrescente.
     *
     * @param o email da confrontare
     * @return risultato del confronto per ordinamento dalla piu' recente
     */
    @Override
    public int compareTo(Email o) {
        return o.getSentDate().compareTo(this.sentDate);
    }
}
