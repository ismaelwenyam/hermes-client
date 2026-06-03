package it.turin.hermesclient.controller;

import it.turin.hermesclient.model.ClientModel;
import it.turin.hermesclient.model.Email;
import it.turin.hermesclient.model.ComposeModel;
import it.turin.hermesclient.model.HomeModel;
import it.turin.hermesclient.tasks.*;
import it.turin.hermesclient.utils.SceneManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;

import java.io.IOException;

/**
 * Controller della vista principale della casella di posta.
 * <p>
 * Mostra la pagina corrente delle email, aggiorna i dettagli della selezione e
 * avvia le attivita' in background che mantengono sincronizzati stato del server,
 * conteggio delle email e lista dei messaggi.
 */
public class HomeController extends ClientController {
    private static final int SERVER_PAGE_SIZE = 10;
    private ClientModel clientModel;
    private HomeModel homeModel;
    private ComposeModel composeModel;
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
    @FXML public MenuButton elemsXPage;

    @FXML  public Label homeErrorLabel;
    @FXML public Label newMessageLabel;


    /**
     * Inizializza la vista principale, collega le label al modello e configura
     * rendering e gestione della selezione della lista.
     *
     * @param clientModel stato condiviso dell'applicazione
     */
    public void init(ClientModel clientModel) {
        this.clientModel = clientModel;
        this.homeModel = clientModel.getHomeModel();
        this.composeModel = clientModel.getComposeModel();
        startTasks();
        loggedUser.textProperty().bind(clientModel.emailProperty());
        serverStatus.fillProperty().bind(clientModel.serverStatusColorProperty());
        homeErrorLabel.textProperty().bind(clientModel.errorMessageProperty());
        homeErrorLabel.visibleProperty().bind(clientModel.showErrorProperty());
        newMessageLabel.visibleProperty().bind(homeModel.newMessageProperty());

        int pageGui = Math.max(1, Integer.parseInt(homeModel.getPageGui()));
        int f = (pageGui - 1) * nrElements;
        int t = Math.min(f + nrElements, homeModel.getSortedEmails().size());
        emailList.setItems(FXCollections.observableArrayList(homeModel.getSortedEmails().subList(f, t)));

        homeModel.getEmails().addListener((javafx.collections.ListChangeListener<Email>) c -> {
            updatePage();
        });
        totalMails.textProperty().bind(homeModel.emailsCountProperty());
        page.textProperty().bind(homeModel.pageGuiProperty());
        elemsXPage.setPopupSide(Side.TOP);
        elemsXPage.getItems().forEach(mi -> {
            mi.setOnAction(event -> {
                elemsXPage.setText(mi.getText());
            });
        });
        elemsXPage.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equalsIgnoreCase(oldValue)) {
                int previousNrElements = nrElements;
                int currentFirstIndex = (Math.max(1, Integer.parseInt(homeModel.getPageGui())) - 1) * previousNrElements;
                nrElements = Integer.parseInt(newValue);
                int newPageGui = (currentFirstIndex / nrElements) + 1;
                homeModel.setPageGui(String.valueOf(Math.max(1, newPageGui)));
                updatePage();
            }
        });
        emailList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Email mail, boolean empty) {
                super.updateItem(mail, empty);
                if (empty) {
                    setText("");
                } else if (mail == null || mail.getArgument() == null)  {
                    setText("no argument");
                } else {
                    setText(mail.getArgument());
                }
            }
        });
        emailList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                homeModel.setSelectedEmailId(String.valueOf(newValue.getID()));
                argument.setText(newValue.getArgument());
                from.setText(newValue.getSender());
                String recipients = "";
                for (String mail : newValue.getRecipients()) {
                    recipients = recipients.concat(mail).concat(";");
                }
                to.setText(recipients);
                sentDate.setText(newValue.getSentDate().toString());
                mailArea.setText(newValue.getMailBody());
                acknowledgeNewestMail(newValue);
            } else {
                homeModel.setSelectedEmailId("");
                argument.setText("");
                from.setText("");
                to.setText("");
                sentDate.setText("");
                mailArea.setText("");
            }
        });
    }

    /**
     * Spegne la notifica solo quando l'utente apre la mail piu' recente
     * visibile nella lista corrente.
     *
     * @param selectedEmail email appena selezionata
     */
    private void acknowledgeNewestMail(Email selectedEmail) {
        if (!homeModel.isNewMessage()) {
            return;
        }
        if (selectedEmail == null) {
            return;
        }
        if (!emailList.getItems().isEmpty() && emailList.getItems().get(0).equals(selectedEmail)) {
            homeModel.setNewMessage(false);
        }
    }

    /**
     * Arresta le attivita' in background gestite dal modello del client.
     */
    @Override
    public void shutdown() {
        clientModel.getTasksExecutor().shutdown();
    }

    /**
     * Avvia il ping periodico e le attivita' usate per pooling e aggiornamento del
     * conteggio, se non sono gia' stati avviati.
     */
    private void startTasks () {
        if (!clientModel.isTaskStarted()){
            clientModel.getTasksExecutor().start(clientModel, 8080);
            clientModel.setTaskStarted(true);
        }
    }

    /**
     * Apre la vista di composizione per un nuovo messaggio.
     *
     * @param mouseEvent evento di click che ha attivato l'azione
     */
    public void onNewMessage(MouseEvent mouseEvent) {
        composeModel.clearDraft();
        try {
            SceneManager.switchScene("compose-view.fxml", clientModel);
        } catch (IOException e) {
            System.err.println("execption in switching to compose-view");
        }
    }

    /**
     * Prepara una risposta all'email attualmente selezionata.
     */
    public void onAnswer () {
        if (homeModel.getSelectedEmailId() == null || homeModel.getSelectedEmailId().trim().isEmpty()) {
            clientModel.setErrorMessage("Select the mail to which answer");
            clientModel.setShowError(true);
            return;
        }
        composeModel.setArgument("RE: " + argument.getText());
        composeModel.setRecipients(from.getText());
        try {
            SceneManager.switchScene("compose-view.fxml", clientModel);
        } catch (IOException e) {
            System.err.println("execption in switching to compose-view");
        }
    }

    /**
     * Prepara una risposta indirizzata al mittente e a tutti i destinatari,
     * escluso l'account connesso.
     */
    public void onAnswerAll () {
        if (homeModel.getSelectedEmailId() == null || homeModel.getSelectedEmailId().trim().isEmpty()) {
            clientModel.setErrorMessage("Select the mail to which answer all");
            clientModel.setShowError(true);
            return;
        }
        composeModel.setArgument("RE: " + argument.getText());
        String sender = from.getText();
        String[] r = to.getText().split(";");
        StringBuilder recipients = new StringBuilder();
        for (String mail : r) {
            if (!mail.equalsIgnoreCase(clientModel.getEmail())){
                recipients.append(mail).append(";");
            }
        }
        composeModel.setRecipients(sender.concat(";").concat(recipients.toString()));
        try {
            SceneManager.switchScene("compose-view.fxml", clientModel);
        } catch (IOException e) {
            System.err.println("execption in switching to compose-view");
        }
    }

    /**
     * Apre la vista di composizione con il contenuto dell'email selezionata
     * pronto per l'inoltro.
     */
    public void onForward () {
        if (homeModel.getSelectedEmailId() == null || homeModel.getSelectedEmailId().trim().isEmpty()) {
            clientModel.setErrorMessage("Select the mail to forward");
            clientModel.setShowError(true);
            return;
        }
        composeModel.setArgument("FWD: " + argument.getText());
        composeModel.setTextBody(mailArea.getText());
        composeModel.setRecipients("");
        try {
            SceneManager.switchScene("compose-view.fxml", clientModel);
        } catch (IOException e) {
            System.err.println("execption in switching to compose-view");
        }
    }

    /**
     * Avvia l'attivita' di eliminazione per l'email selezionata.
     */
    public void onDelete () {
        if (homeModel.getSelectedEmailId() == null || homeModel.getSelectedEmailId().trim().isEmpty()) {
            clientModel.setErrorMessage("Select the mail to delete");
            clientModel.setShowError(true);
            return;
        }
        Thread deleteThread = new Thread(new Deletion(clientModel, homeModel, 8080), "deletion-task");
        deleteThread.start();
    }

    /**
     * Sposta la lista della casella di posta alla pagina precedente, se
     * disponibile.
     *
     * @param mouseEvent evento di click che ha attivato la navigazione
     */
    public void onPrevious(MouseEvent mouseEvent) {
        int pageGui = Integer.parseInt(homeModel.getPageGui());
        if (pageGui <= 1) return;
        pageGui -= 1;
        homeModel.setPageGui(String.valueOf(pageGui));
        updatePage();

    }

    /**
     * Sposta la lista della casella di posta alla pagina successiva, se
     * disponibile, e risveglia l'attivita' di pooling per richiedere altri dati.
     *
     * @param mouseEvent evento di click che ha attivato la navigazione
     */
    public void onNext(MouseEvent mouseEvent) {
        int pageGui = Integer.parseInt(homeModel.getPageGui());
        pageGui += 1;
        homeModel.setPageGui(String.valueOf(pageGui));
        updatePage();
    }

    /**
     * Aggiorna in modo asincrono la lista visibile delle email in base alla
     * pagina corrente e al numero di elementi per pagina.
     * <p>
     * Se la pagina richiesta supera le email gia' caricate, richiede al modello
     * di recuperare altri dati dal server; se invece tutte le email sono gia'
     * disponibili, riallinea la pagina all'ultima valida.
     * </p>
     */
    private void updatePage() {
        new Thread(() -> {
            int loadedEmails = homeModel.getSortedEmails().size();
            int totalEmails = Integer.parseInt(homeModel.getEmailsCount());
            int currentPageGui = Math.max(1, Integer.parseInt(homeModel.getPageGui()));
            int effectivePageGui = currentPageGui;
            int from = (currentPageGui - 1) * nrElements;
            int requestedEnd = currentPageGui * nrElements;

            if (loadedEmails == 0) {
                Platform.runLater(() -> emailList.setItems(FXCollections.observableArrayList()));
                return;
            }

            if (requestedEnd > loadedEmails) {
                if (loadedEmails < totalEmails) {
                    homeModel.setServerPage(loadedEmails / SERVER_PAGE_SIZE);
                    clientModel.getPoolingSem().release();
                    return;
                } else {
                    effectivePageGui = Math.max(1, (int) Math.ceil((double) loadedEmails / nrElements));
                    homeModel.setPageGui(String.valueOf(effectivePageGui));
                }
            }

            int finalFrom = (effectivePageGui - 1) * nrElements;
            int finalTo = Math.min(finalFrom + nrElements, loadedEmails);
            Platform.runLater(() -> {
                emailList.setItems(
                        FXCollections.observableArrayList(
                                homeModel.getSortedEmails().subList(finalFrom, finalTo)
                        )
                );
            });
        }).start();
    }
}
