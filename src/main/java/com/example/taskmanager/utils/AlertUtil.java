package com.example.taskmanager.utils;

import javafx.scene.control.Alert;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AlertUtil {
    private static final Logger LOGGER = Logger.getLogger(AlertUtil.class.getName());

    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void logError(String message, Exception e) {
        LOGGER.log(Level.SEVERE, message, e);
    }
}
