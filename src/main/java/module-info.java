module boulahri.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens boulahri.app to javafx.fxml;
    exports boulahri.app;
    opens boulahri.app.controllers to javafx.fxml;
    exports boulahri.app.controllers;
}
