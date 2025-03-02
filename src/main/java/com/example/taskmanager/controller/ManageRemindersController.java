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
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static com.example.taskmanager.utils.AlertUtil.*;

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
    private static final Logger LOGGER = Logger.getLogger(ManageRemindersController.class.getName());

    @FXML
    public void initialize() {
        // ✅ Setup table columns
        reminderTypeColumn.setCellValueFactory(cellData -> {
            String type = cellData.getValue().getType();
            return new SimpleStringProperty("Choose date".equals(type) ? "Custom Date" : type);
        });

        reminderDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getDateTime().toLocalDate().toString()));

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
                setGraphic(empty ? null : deleteButton);
            }
        });

        // Ensure DatePicker is disabled initially
        specificDatePicker.setDisable(true);

        // Enable DatePicker only if "Choose date" is selected
        reminderTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isCustomDate = "Choose date".equals(newVal);
            specificDatePicker.setDisable(!isCustomDate);
            if (!isCustomDate) specificDatePicker.setValue(null);
        });
    }

    public void setTask(Task task) {
        this.task = task;
        taskTitleLabel.setText("Reminders for: " + task.getTitle());

        // ✅ Load reminders from in-memory storage
        List<Task> tasks = AppState.getInstance().getTasks();
        tasks.stream()
                .filter(t -> t.getTitle().equals(task.getTitle()))
                .findFirst()
                .ifPresent(t -> this.task.setReminders(t.getReminders()));

        loadReminders();
    }

    private void loadReminders() {
        reminders.setAll(task.getReminders());
        reminderTable.setItems(reminders);
        reminderTable.refresh();
    }

    @FXML
    private void handleAddReminder() {
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

        LocalDate today = LocalDate.now();

        // Determine reminder date
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
                selectedType = "Custom Date";
                break;
        }

        // Validate reminder date
        if (reminderDate == null || reminderDate.isBefore(today)) {
            showAlert("Error", "The reminder date cannot be in the past.");
            return;
        }

        if (reminderDate.isAfter(taskDeadline)) {
            showAlert("Error", "The reminder must be set before the task deadline.");
            return;
        }

        // Create and add reminder
        Reminder newReminder = new Reminder(reminderDate.atStartOfDay(), task.getTitle(), selectedType);
        task.addReminder(newReminder);

        // Save updated task list in memory
        List<Task> tasks = AppState.getInstance().getTasks();
        tasks.stream()
                .filter(t -> t.getTitle().equals(task.getTitle()))
                .findFirst()
                .ifPresent(t -> t.setReminders(task.getReminders()));

        LOGGER.log(Level.INFO, "Reminder added: " + newReminder.getType() + " for task: " + task.getTitle());

        // ✅ Refresh UI
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

        // Remove reminder from task
        task.getReminders().remove(reminder);

        // Update in-memory storage
        List<Task> tasks = AppState.getInstance().getTasks();
        tasks.stream()
                .filter(t -> t.getTitle().equals(task.getTitle()))
                .findFirst()
                .ifPresent(t -> t.setReminders(task.getReminders()));

        LOGGER.log(Level.INFO, "Reminder deleted: " + reminder.getType() + " for task: " + task.getTitle());

        // Refresh UI
        loadReminders();
        showAlert("Success", "Reminder deleted successfully.");
    }

    @FXML
    private void handleClose() {
        ((Stage) taskTitleLabel.getScene().getWindow()).close();
    }
}
