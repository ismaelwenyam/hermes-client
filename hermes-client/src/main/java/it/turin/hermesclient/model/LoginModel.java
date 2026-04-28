package it.turin.hermesclient.model;

import javafx.beans.property.SimpleStringProperty;

public class LoginModel {
    private SimpleStringProperty email;


    public LoginModel () {
        email = new SimpleStringProperty();
    }

    public String getEmail() {
        return email.get();
    }

    public SimpleStringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }


}
