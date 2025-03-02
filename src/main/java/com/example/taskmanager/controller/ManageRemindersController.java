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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ManageRemindersController {
    @FXML
    private Label taskTitleLabel;
    @FXML
    private TableView<Reminder> reminderTable;
    @FXML
    private TableColumn<Reminder, String> reminderTypeColumn;
    @FXML
    private TableColumn<Reminder, String> reminderDateColumn;
    @FXML
    private TableColumn<Reminder, Void> reminderActionsColumn;
    @FXML
    private ComboBox<String> reminderTypeComboBox;
    @FXML
    private DatePicker specificDatePicker;

    private Task task;
    private ObservableList<Reminder> reminders = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // ✅ Setup table columns
        reminderTypeColumn.setCellValueFactory(cellData -> {
            String type = cellData.getValue().getType();

            // ✅ If it's a custom date, rename it in the table view
            if ("Choose date".equals(type)) {
                return new SimpleStringProperty("Custom Date");
            }

            return new SimpleStringProperty(type);
        });

        reminderDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateTime().toString()));

        // ✅ Add "Delete" Button in each row
        reminderActionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    Reminder reminder = getTableView().getItems().get(getIndex());
                    handleDeleteReminder(reminder);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        // Ensure DatePicker is disabled initially
        specificDatePicker.setDisable(true);

        // Add a listener to enable DatePicker when "Choose date" is selected
        reminderTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("Choose date".equals(newVal)) {
                specificDatePicker.setDisable(false);
            } else {
                specificDatePicker.setDisable(true);
                specificDatePicker.setValue(null); // Reset the selected date
            }
        });
    }

    public void setTask(Task task) {
        this.task = task;
        taskTitleLabel.setText("Reminders for: " + task.getTitle());

        // ✅ Ensure reminders are loaded from saved JSON
        List<Task> savedTasks = TaskStorage.loadTasks();
        for (Task t : savedTasks) {
            if (t.getTitle().equals(task.getTitle())) {
                this.task.setReminders(t.getReminders());
                break;
            }
        }

        loadReminders(); // ✅ Call loadReminders() after setting task
    }

    private void loadReminders() {
//        System.out.println("🔄 Loading reminders for task: " + task.getTitle());
//
//        // ✅ Debug: Print all reminders
//        if (task.getReminders().isEmpty()) {
//            System.out.println("⚠️ No reminders found for this task.");
//        } else {
//            for (Reminder reminder : task.getReminders()) {
//                System.out.println("✅ Reminder: " + reminder.getType() + " | Date: " + reminder.getDateTime());
//            }
//        }

        reminders.setAll(task.getReminders()); // ✅ Get latest reminders from the task
        reminderTable.setItems(reminders);
        reminderTable.refresh(); // ✅ Force UI refresh
    }

    @FXML
    private void handleAddReminder() {
        // ✅ Prevent adding reminders if the task is completed
        if (task.getStatus() == Task.TaskStatus.Completed) {
            showAlert("Error", "You cannot add reminders to a completed task.");
            return;
        }

        String selectedType = reminderTypeComboBox.getValue();
        LocalDate reminderDate = null;

        if (selectedType == null) {
            showAlert("Error", "Please select a reminder type.");
            return;
        }

        LocalDate taskDeadline = task.getDeadlineAsLocalDate();
        if (taskDeadline == null) {
            showAlert("Error", "The task has no valid deadline.");
            return;
        }

        LocalDate today = LocalDate.now(); // ✅ Get today's date

        // Determine reminder date based on selection
        switch (selectedType) {
            case "One day before":
                reminderDate = taskDeadline.minusDays(1);
                break;
            case "One week before":
                reminderDate = taskDeadline.minusWeeks(1);
                break;
            case "One month before":
                reminderDate = taskDeadline.minusMonths(1);
                break;
            case "Choose date":
                if (specificDatePicker.getValue() == null) {
                    showAlert("Error", "Please select a date.");
                    return;
                }
                reminderDate = specificDatePicker.getValue();
                selectedType = "Custom Date"; // ✅ Display as "Custom Date" instead of "Choose Date"
                break;
        }

        // Ensure the reminder is in the future
        assert reminderDate != null;
        if (reminderDate.isBefore(today)) {
            showAlert("Error", "The reminder date cannot be in the past.");
            return;
        }

        // Ensure the reminder is before the task deadline
        if (reminderDate.isAfter(taskDeadline)) {
            showAlert("Error", "The reminder must be set before the task deadline.");
            return;
        }

        // Create and add the reminder (convert LocalDate to LocalDateTime at start of day)
        Reminder newReminder = new Reminder(reminderDate.atStartOfDay(), task.getTitle(), selectedType);
        task.addReminder(newReminder);

        // Save updated task list
        List<Task> tasks = TaskStorage.loadTasks();
        for (Task t : tasks) {
            if (t.getTitle().equals(task.getTitle())) {
                t.setReminders(task.getReminders());
                break;
            }
        }
        TaskStorage.saveTasks(tasks);

        // Refresh UI
        loadReminders();
        showAlert("Success", "Reminder added successfully.");
    }

    @FXML
    private void handleDeleteReminder(Reminder reminder) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete this reminder?");
        alert.setContentText("This action cannot be undone.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        // Remove reminder from the task
        task.getReminders().remove(reminder);

        // Save updated reminders in JSON
        List<Task> tasks = TaskStorage.loadTasks();
        for (Task t : tasks) {
            if (t.getTitle().equals(task.getTitle())) {
                t.setReminders(task.getReminders()); // Ensure update
                break;
            }
        }
        TaskStorage.saveTasks(tasks);

        // Refresh UI
        loadReminders();
        showAlert("Success", "Reminder deleted successfully.");
    }


    @FXML
    private void handleClose() {
        Stage stage = (Stage) taskTitleLabel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
