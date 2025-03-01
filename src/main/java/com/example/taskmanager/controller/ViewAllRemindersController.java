package com.example.taskmanager.controller;

import com.example.taskmanager.model.Reminder;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.storage.TaskStorage;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

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
    private TableColumn<Reminder, Void> actionsColumn;
    @FXML
    private Button closeButton;

    private final ObservableList<Reminder> reminders = FXCollections.observableArrayList();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {
        // ✅ Ensure correct mapping of columns
        taskColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTaskId()));
        typeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));
        dateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDateTime().toString()));

        //System.out.println("✅ Reminder Table initialized.");

        // Load reminders into the table
        loadReminders();
    }



    public void loadReminders() {
        List<Task> tasks = TaskStorage.loadTasks();
        reminders.clear();

        //System.out.println("🔍 Searching for reminders...");
        for (Task task : tasks) {
            //System.out.println("📌 Checking Task: " + task.getTitle() + " | Reminders: " + task.getReminders().size());
            reminders.addAll(task.getReminders());
        }

//        if (reminders.isEmpty()) {
//            System.out.println("⚠️ No reminders found.");
//        } else {
//            System.out.println("✅ Total reminders loaded: " + reminders.size());
//        }

        reminderTable.setItems(FXCollections.observableArrayList(reminders));
        reminderTable.refresh();
    }


    @FXML
    private void handleClose() {
        ((Stage) closeButton.getScene().getWindow()).close();
    }
}
