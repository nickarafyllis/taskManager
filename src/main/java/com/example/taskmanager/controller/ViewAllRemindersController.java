package com.example.taskmanager.controller;

import com.example.taskmanager.storage.AppState;
import com.example.taskmanager.model.Reminder;
import com.example.taskmanager.model.Task;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ViewAllRemindersController {
    @FXML
    private TableView<Reminder> reminderTable;
    @FXML
    private TableColumn<Reminder, String> taskColumn;
    @FXML
    private TableColumn<Reminder, String> typeColumn;
    @FXML
    private TableColumn<Reminder, String> dateColumn;
    @FXML
    private Button closeButton;

    private final ObservableList<Reminder> reminders = FXCollections.observableArrayList();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Logger LOGGER = Logger.getLogger(ViewAllRemindersController.class.getName());

    @FXML
    public void initialize() {
        // ✅ Ensure correct mapping of columns
        taskColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTaskId()));
        typeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));
        dateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDateTime().format(FORMATTER)));

        LOGGER.log(Level.INFO, "Reminder Table initialized.");

        // Load reminders into the table
        loadReminders();
    }

    public void loadReminders() {
        List<Task> tasks = AppState.getInstance().getTasks();
        reminders.clear();

        LOGGER.log(Level.INFO, "🔍 Searching for reminders...");
        for (Task task : tasks) {
            LOGGER.log(Level.INFO, "📌 Checking Task: " + task.getTitle() + " | Reminders: " + task.getReminders().size());
            reminders.addAll(task.getReminders());
        }

        if (reminders.isEmpty()) {
            LOGGER.log(Level.WARNING, "⚠️ No reminders found.");
        } else {
            LOGGER.log(Level.INFO, "✅ Total reminders loaded: " + reminders.size());
        }

        reminderTable.setItems(reminders);
        reminderTable.refresh();
    }

    @FXML
    private void handleClose() {
        ((Stage) closeButton.getScene().getWindow()).close();
    }
}
