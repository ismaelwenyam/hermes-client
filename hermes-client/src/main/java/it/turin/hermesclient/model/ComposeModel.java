package it.turin.hermesclient.model;

public class ComposeModel {
    private Email mail;

    public Email getMail() {
        return new Email(mail.getSender(), mail.getRecipients(), mail.getArgument(), mail.getMailBody(), mail.getSentDate());
    }

    public void setMail(Email mail) {
        this.mail = mail;
    }
}
