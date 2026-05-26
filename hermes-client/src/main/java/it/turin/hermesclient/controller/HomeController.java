package it.turin.hermesclient.controller;

import it.turin.hermesclient.model.ClientModel;
import it.turin.hermesclient.model.Email;
import it.turin.hermesclient.tasks.*;
import it.turin.hermesclient.utils.SceneManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;

import java.io.IOException;

public class HomeController extends ClientController {
    private ClientModel clientModel;
    int nrElements = 3; //TODO edit to 5


    @FXML public BorderPane homeView;
    @FXML public Label argument;
    @FXML public Label from;
    @FXML public Label to;
    @FXML public TextArea mailArea;
    @FXML public Label sentDate;

    @FXML public ListView<Email> emailList;
    @FXML public Button newEmailButton;
    @FXML public Label loggedUser;
    @FXML public Circle serverStatus;
    @FXML public Label page;
    @FXML public Label totalMails;

    @FXML  public Label homeErrorLabel;
    @FXML public Label newMessageLabel;


    public void init(ClientModel clientModel) {
        this.clientModel = clientModel;
        clientModel.getSortedEmails().setComparator(Email::compareTo);
        startTasks();
        loggedUser.textProperty().bind(clientModel.emailProperty());
        serverStatus.fillProperty().bind(clientModel.serverStatusColorProperty());
        homeErrorLabel.textProperty().bind(clientModel.errorMessageProperty());
        homeErrorLabel.visibleProperty().bind(clientModel.showErrorProperty());
        newMessageLabel.visibleProperty().bind(clientModel.newMessageProperty());

        //pooling execution result
        int p = clientModel.getPage();
        int f = p * nrElements;
        int t = Math.min(f + nrElements, clientModel.getSortedEmails().size());
        emailList.setItems(FXCollections.observableArrayList(clientModel.getSortedEmails().subList(f, t)));

        clientModel.getEmails().addListener((javafx.collections.ListChangeListener<Email>) c -> {
            updatePage();
        });
        totalMails.textProperty().bind(clientModel.emailsCountProperty());
        page.textProperty().bind(clientModel.pageGuiProperty());
        emailList.setCellFactory(param -> new ListCell<>() {

            @Override
            protected void updateItem(Email mail, boolean empty) {
                super.updateItem(mail, empty);
                if (empty || mail == null || mail.getArgument() == null) {
                    setText("");
                } else {
                    setText(mail.getArgument());
                }
            }
        });
        emailList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            clientModel.setNewMessage(false);
            if (newValue != null) {
                clientModel.setSelectedEmailId(String.valueOf(newValue.getID()));
                argument.setText(newValue.getArgument());
                from.setText(newValue.getSender());
                String recipients = "";
                for (String mail : newValue.getRecipients()) {
                    recipients = recipients.concat(mail).concat(";");
                }
                to.setText(recipients);
                sentDate.setText(newValue.getSentDate().toString());
                mailArea.setText(newValue.getMailBody());
            } else {
                clientModel.setSelectedEmailId("");
                argument.setText("");
                from.setText("");
                to.setText("");
                sentDate.setText("");
                mailArea.setText("");
            }
        });
    }

    @Override
    public void shutdown() {
        clientModel.getTasksExecutor().shutdown();
    }

    private void startTasks () {
        if (!clientModel.isTaskStarted()){
            clientModel.getTasksExecutor().start(new Ping(clientModel, 8080), new Pooling(clientModel, 8080), new Count(clientModel, 8080));
            clientModel.setTaskStarted(true);
        }
    }

    public void onNewMessage(MouseEvent mouseEvent) {
        clientModel.setArgument("");
        clientModel.setRecipients("");
        clientModel.setTextBody("");
        try {
            SceneManager.switchScene("compose-view.fxml", clientModel);
        } catch (IOException e) {
            System.err.println("execption in switching to compose-view");
        }
    }

    public void onAnswer () {
        if (clientModel.getSelectedEmailId() == null || clientModel.getSelectedEmailId().trim().isEmpty()) {
            clientModel.setErrorMessage("Select the mail to which answer");
            clientModel.setShowError(true);
            return;
        }
        clientModel.setArgument("RE: " + argument.getText());
        clientModel.setRecipients(from.getText());
        try {
            SceneManager.switchScene("compose-view.fxml", clientModel);
        } catch (IOException e) {
            System.err.println("execption in switching to compose-view");
        }
    }

    public void onAnswerAll () {
        if (clientModel.getSelectedEmailId() == null || clientModel.getSelectedEmailId().trim().isEmpty()) {
            clientModel.setErrorMessage("Select the mail to which answer all");
            clientModel.setShowError(true);
            return;
        }
        clientModel.setArgument("RE: " + argument.getText());
        String sender = from.getText();
        String[] r = to.getText().split(";");
        StringBuilder recipients = new StringBuilder();
        for (String mail : r) {
            if (!mail.equalsIgnoreCase(clientModel.getEmail())){
                recipients.append(mail).append(";");
            }
        }
        clientModel.setRecipients(sender.concat(";").concat(recipients.toString()));
        try {
            SceneManager.switchScene("compose-view.fxml", clientModel);
        } catch (IOException e) {
            System.err.println("execption in switching to compose-view");
        }
    }

    public void onForward () {
        if (clientModel.getSelectedEmailId() == null || clientModel.getSelectedEmailId().trim().isEmpty()) {
            clientModel.setErrorMessage("Select the mail to forward");
            clientModel.setShowError(true);
            return;
        }
        clientModel.setArgument(argument.getText());
        clientModel.setTextBody(mailArea.getText());
        clientModel.setRecipients("");
        try {
            SceneManager.switchScene("compose-view.fxml", clientModel);
        } catch (IOException e) {
            System.err.println("execption in switching to compose-view");
        }
    }

    public void onDelete () {
        if (clientModel.getSelectedEmailId() == null || clientModel.getSelectedEmailId().trim().isEmpty()) {
            clientModel.setErrorMessage("Select the mail to delete");
            clientModel.setShowError(true);
            return;
        }
        Thread deleteThread = new Thread(new Deletion(clientModel, 8080), "deletion-task");
        deleteThread.start();
    }

    public void onPrevious(MouseEvent mouseEvent) {
        int pageGui = Integer.parseInt(clientModel.getPageGui());
        if (pageGui <= 1) return;
        pageGui -= 1;
        clientModel.setPage(clientModel.getPage() - 1);
        clientModel.setPageGui(String.valueOf(pageGui));
        updatePage();

    }

    public void onNext(MouseEvent mouseEvent) {
        System.out.println("page_gui: " + clientModel.getPageGui() + " - page: " + clientModel.getPage());
        int pageGui = Integer.parseInt(clientModel.getPageGui());
        if (pageGui >= (double) Integer.parseInt(clientModel.getEmailsCount()) / nrElements) return;
        pageGui += 1;
        clientModel.setPage(clientModel.getPage() + 1);
        clientModel.setPageGui(String.valueOf(pageGui));
        System.out.println("page_gui: " + clientModel.getPageGui() + " - page: " + clientModel.getPage());
        clientModel.getPoolingSem().release();
        updatePage();
    }

    private void updatePage() {
        int from = clientModel.getPage() * nrElements;
        int to = Math.min(from + nrElements, clientModel.getSortedEmails().size());
        emailList.setItems(
                FXCollections.observableArrayList(
                        clientModel.getSortedEmails().subList(from, to)
                )
        );
    }
}
