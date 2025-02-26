package com.example.taskmanager.controller;

import com.example.taskmanager.model.Reminder;
import com.example.taskmanager.storage.ReminderStorage;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ManageRemindersController {

    @FXML
    private TableView<Reminder> reminderTable;
    @FXML
    private TableColumn<Reminder, String> messageColumn;
    @FXML
    private TableColumn<Reminder, String> dateTimeColumn;
    @FXML
    private TableColumn<Reminder, String> taskIdColumn;
    @FXML
    private TextField reminderMessageInput;
    @FXML
    private DatePicker reminderDatePicker;
    @FXML
    private TextField reminderTimeInput;
    @FXML
    private TextField taskIdInput;
    @FXML
    private Button addReminderButton;
    @FXML
    private Button editReminderButton;
    @FXML
    private Button deleteReminderButton;

    private final ObservableList<Reminder> reminders = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Load reminders from storage
        refreshReminders();

        // Set up TableView columns
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        dateTimeColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        taskIdColumn.setCellValueFactory(new PropertyValueFactory<>("taskId"));

        // Attach data to TableView
        reminderTable.setItems(reminders);
    }

    private void refreshReminders() {
        List<Reminder> loadedReminders = ReminderStorage.loadReminders();
        if (loadedReminders == null) {
            loadedReminders = new ArrayList<>(); // Ensure it is not null
        }
        reminders.setAll(loadedReminders); // Convert to ObservableList
    }


    @FXML
    private void handleAddReminder() {
        String message = reminderMessageInput.getText();
        String date = (reminderDatePicker.getValue() != null) ? reminderDatePicker.getValue().toString() : "";
        String time = reminderTimeInput.getText();
        String taskId = taskIdInput.getText();

        if (message.isEmpty() || date.isEmpty() || time.isEmpty() || taskId.isEmpty()) {
            showAlert("Error", "All fields must be filled.");
            return;
        }

        try {
            LocalDateTime dateTime = LocalDateTime.parse(date + "T" + time);
            Reminder newReminder = new Reminder(message, dateTime, taskId);
            reminders.add(newReminder);
            ReminderStorage.saveReminders(reminders);

            // Refresh UI
            refreshReminders();
            clearFields();
        } catch (Exception e) {
            showAlert("Error", "Invalid date/time format. Use HH:mm for time.");
        }
    }

    @FXML
    private void handleEditReminder() {
        Reminder selectedReminder = reminderTable.getSelectionModel().getSelectedItem();
        if (selectedReminder == null) {
            showAlert("Error", "No reminder selected.");
            return;
        }

        // Get new values
        String newMessage = reminderMessageInput.getText();
        String newDate = (reminderDatePicker.getValue() != null) ? reminderDatePicker.getValue().toString() : "";
        String newTime = reminderTimeInput.getText();

        if (newMessage.isEmpty() || newDate.isEmpty() || newTime.isEmpty()) {
            showAlert("Error", "All fields must be filled.");
            return;
        }

        try {
            LocalDateTime newDateTime = LocalDateTime.parse(newDate + "T" + newTime);
            selectedReminder.setMessage(newMessage);
            selectedReminder.setDateTime(newDateTime);

            reminderTable.refresh(); // Update UI
            ReminderStorage.saveReminders(reminders);
            clearFields();
        } catch (Exception e) {
            showAlert("Error", "Invalid date/time format. Use HH:mm for time.");
        }
    }

    @FXML
    private void handleDeleteReminder() {
        Reminder selectedReminder = reminderTable.getSelectionModel().getSelectedItem();
        if (selectedReminder == null) {
            showAlert("Error", "No reminder selected.");
            return;
        }

        reminders.remove(selectedReminder);
        ReminderStorage.saveReminders(reminders);

        // Refresh UI
        refreshReminders();
    }

    private void clearFields() {
        reminderMessageInput.clear();
        reminderDatePicker.setValue(null);
        reminderTimeInput.clear();
        taskIdInput.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
