package it.turin.hermesclient.model;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

public class ComposeModel {
    private SimpleStringProperty argument = new SimpleStringProperty("");
    private SimpleStringProperty recipients = new SimpleStringProperty("");
    private SimpleStringProperty textBody = new SimpleStringProperty("");
    private Email mail;

    public Email getMail() {
        return new Email(mail.getSender(), mail.getRecipients(), mail.getArgument(), mail.getMailBody(), mail.getSentDate());
    }

    public void setMail(Email mail) {
        this.mail = mail;
    }

    public String getArgument() {
        return argument.get();
    }

    public SimpleStringProperty argumentProperty() {
        return argument;
    }

    public void setArgument(String argument) {
        this.argument.set(argument);
    }

    public String getRecipients() {
        return recipients.get();
    }

    public SimpleStringProperty recipientsProperty() {
        return recipients;
    }

    public void setRecipients(String recipients) {
        this.recipients.set(recipients);
    }

    public String getTextBody() {
        return textBody.get();
    }

    public SimpleStringProperty textBodyProperty() {
        return textBody;
    }

    public void setTextBody(String textBody) {
        this.textBody.set(textBody);
    }
}
