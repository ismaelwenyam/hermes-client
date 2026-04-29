package it.turin.hermesclient.model;

import javafx.beans.property.*;
import javafx.scene.paint.Color;

public class ClientModel {
    private SimpleStringProperty email = new SimpleStringProperty();
    private BooleanProperty userNotLoggedIn = new SimpleBooleanProperty(true);
    private BooleanProperty userLoggedIn = new SimpleBooleanProperty(true); //TODO must be setted
    private BooleanProperty showError = new SimpleBooleanProperty(false);
    private SimpleStringProperty errorMessage = new SimpleStringProperty();


    public String getEmail() {
        return email.get();
    }

    public SimpleStringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public boolean getUserNotLoggedIn() {
        return userNotLoggedIn.get();
    }

    public BooleanProperty userNotLoggedInProperty() {
        return userNotLoggedIn;
    }

    public void setUserNotLoggedIn(boolean userNotLoggedIn) {
        this.userNotLoggedIn.set(userNotLoggedIn);
    }

    public boolean isShowError() {
        return showError.get();
    }

    public BooleanProperty showErrorProperty() {
        return showError;
    }

    public void setShowError(boolean showError) {
        this.showError.set(showError);
    }

    public String getErrorMessage() {
        return errorMessage.get();
    }

    public SimpleStringProperty errorMessageProperty() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage.set(errorMessage);
    }

    public boolean isUserLoggedIn() {
        return userLoggedIn.get();
    }

    public BooleanProperty userLoggedInProperty() {
        return userLoggedIn;
    }

    public void setUserLoggedIn(boolean userLoggedIn) {
        this.userLoggedIn.set(userLoggedIn);
    }
}
