module com.example.taskmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.logging;

    opens com.example.taskmanager.model to com.google.gson;
    opens com.example.taskmanager.controller to javafx.fxml;
    exports com.example.taskmanager;
    exports com.example.taskmanager.controller;
}