package com.example.taskmanager.controller;

import com.example.taskmanager.model.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class TaskNotificationController {

    @FXML
    private Label taskTitleLabel;
    @FXML
    private ComboBox<String> notificationCategoryComboBox;
    @FXML
    private DatePicker specificDatePicker;
    @FXML
    private Button saveNotificationButton;

    private Task task;


    @FXML
    private void initialize() {
        notificationCategoryComboBox.getItems().addAll(
                "One day before",
                "One week before",
                "One month before",
                "Choose date"
        );
    }

    public void setTask(Task task) {
        this.task = task;
        taskTitleLabel.setText("Manage Reminders for: " + task.getTitle());

        // Show date picker only when "Choose date" is selected
        notificationCategoryComboBox.setOnAction(event -> {
            String selected = notificationCategoryComboBox.getValue();
            specificDatePicker.setVisible("Choose date".equals(selected));
        });
    }

    @FXML
    private void saveNotification() {
        String selectedType = notificationCategoryComboBox.getValue();
        if (selectedType == null) {
            showAlert("Error", "Please select a reminder type.");
            return;
        }

        if ("Choose date".equals(selectedType) && specificDatePicker.getValue() == null) {
            showAlert("Error", "Please select a valid date.");
            return;
        }

        showAlert("Success", "Reminder set: " + selectedType);
        ((Stage) saveNotificationButton.getScene().getWindow()).close();  // Close window
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
