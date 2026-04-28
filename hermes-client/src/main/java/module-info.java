module it.turin.hermesclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens it.turin.hermesclient to javafx.fxml;
    opens it.turin.hermesclient.controller to javafx.fxml;
    exports it.turin.hermesclient;
}