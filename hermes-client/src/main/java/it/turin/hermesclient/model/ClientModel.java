package it.turin.hermesclient.model;

import it.turin.hermesclient.tasks.TasksExecutor;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.paint.Color;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class ClientModel {
    private boolean taskStarted = false;


    private final TasksExecutor tasksExecutor = new TasksExecutor();

    private SimpleStringProperty email = new SimpleStringProperty();
    private BooleanProperty userNotLoggedIn = new SimpleBooleanProperty(true);
    private BooleanProperty userLoggedIn = new SimpleBooleanProperty(true);
    private BooleanProperty showError = new SimpleBooleanProperty(false);
    private SimpleStringProperty errorMessage = new SimpleStringProperty();
    private ObjectProperty<Color> serverStatusColor = new SimpleObjectProperty<>(Color.RED);
    private SimpleStringProperty argument = new SimpleStringProperty("");
    private SimpleStringProperty recipients = new SimpleStringProperty("");


    private SimpleStringProperty emailsCount = new SimpleStringProperty("0");

    //home
    private final ObservableList<Email> emails = FXCollections.observableArrayList();
    private final SortedList<Email> sortedEmails = new SortedList<>(emails);
    private SimpleStringProperty page = new SimpleStringProperty("0");
    private BooleanProperty homeShowError = new SimpleBooleanProperty(false);
    private SimpleStringProperty homeErrorMessage = new SimpleStringProperty();
    private SimpleBooleanProperty newMessage = new SimpleBooleanProperty(false);

    public SimpleBooleanProperty serverLiveProperty() {
        return serverLive;
    }

    public void setServerLive(boolean serverLive) {
        this.serverLive.set(serverLive);
    }

    private SimpleBooleanProperty serverLive = new SimpleBooleanProperty(false);
    private final ReentrantLock lock = new ReentrantLock();
    private String selectedEmailId;

    private final Semaphore pool = new Semaphore(0);


    //


    //compose
    private SimpleStringProperty textBody = new SimpleStringProperty("");
    private Email mail;
    //

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

    public Color getServerStatusColor() {
        return serverStatusColor.get();
    }
    public ObjectProperty<Color> serverStatusColorProperty() {
        return serverStatusColor;
    }
    public void setServerStatusColor(Color serverStatusColor) {
        this.serverStatusColor.set(serverStatusColor);
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

    public String getEmailsCount() {
        return emailsCount.get();
    }

    public SimpleStringProperty emailsCountProperty() {
        return emailsCount;
    }

    public void setEmailsCount(String emailsCount) {
        this.emailsCount.set(emailsCount);
    }

    public ObservableList<Email> getEmails() {
        return emails;
    }

    public void addEmail(Email email){
        Platform.runLater(() -> {
            if (!emails.contains(email)) {
                emails.add(email);
            }
        });
    }

    public void removeEmail(long id) {
        for (Email email : emails) {
            if (email.getID() == id){
                Platform.runLater(() -> {
                    emails.remove(email);
                });
            }
        }

    }

    public SortedList<Email> getSortedEmails() {
        return sortedEmails;
    }



    public String getPage() {
        return page.get();
    }

    public SimpleStringProperty pageProperty() {
        return page;
    }

    public void setPage(String page) {
        this.page.set(page);
    }

    public boolean isServerLive() {
        lock.lock();
        try {
            return serverLive.get();
        } finally {
            lock.unlock();
        }
    }

    public void updateServerStatus(boolean status) {
        lock.lock();
        try {
            serverLive.set(status);
        } finally {
            lock.unlock();
        }
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

    public Email getMail() {
        return new Email(mail.getSender(), mail.getRecipients(), mail.getArgument(), mail.getMailBody(), mail.getSentDate());
    }

    public void setMail(Email mail) {
        this.mail = mail;
    }

    public String getSelectedEmailId() {
        return selectedEmailId;
    }

    public void setSelectedEmailId(String selectedEmailId) {
        this.selectedEmailId = selectedEmailId;
    }

    public boolean isHomeShowError() {
        return homeShowError.get();
    }

    public BooleanProperty homeShowErrorProperty() {
        return homeShowError;
    }

    public void setHomeShowError(boolean homeShowError) {
        this.homeShowError.set(homeShowError);
    }

    public String getHomeErrorMessage() {
        return homeErrorMessage.get();
    }

    public SimpleStringProperty homeErrorMessageProperty() {
        return homeErrorMessage;
    }

    public void setHomeErrorMessage(String homeErrorMessage) {
        this.homeErrorMessage.set(homeErrorMessage);
    }

    public TasksExecutor getTasksExecutor() {
        return tasksExecutor;
    }

    public boolean isNewMessage() {
        return newMessage.get();
    }

    public SimpleBooleanProperty newMessageProperty() {
        return newMessage;
    }

    public void setNewMessage(boolean newMessage) {
        this.newMessage.set(newMessage);
    }

    public boolean isTaskStarted() {
        return taskStarted;
    }

    public void setTaskStarted(boolean taskStarted) {
        this.taskStarted = taskStarted;
    }

    public Semaphore getPool() {
        return pool;
    }
}
