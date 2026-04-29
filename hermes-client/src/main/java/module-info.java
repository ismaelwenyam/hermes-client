module it.turin.hermesclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens it.turin.hermesclient to javafx.fxml;
    opens it.turin.hermesclient.controller to javafx.fxml;
    opens it.turin.hermesclient.dto to com.google.gson;
    opens it.turin.hermesclient.model to com.google.gson;
    exports it.turin.hermesclient;
}