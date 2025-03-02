package com.example.taskmanager.controller;

import com.example.taskmanager.model.*;
import com.example.taskmanager.storage.AppState;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;
import static com.example.taskmanager.utils.AlertUtil.*;

public class EditTaskController {

    @FXML
    private TextField taskTitleInput;
    @FXML
    private TextArea taskDescriptionInput;
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private ComboBox<String> priorityComboBox;
    @FXML
    private DatePicker taskDeadlinePicker;
    @FXML
    private Button saveTaskButton;
    @FXML
    private Button deleteTaskButton;

    private Task task;
    private MainController mainController;

    public void setTask(Task task) {
        this.task = task;

        // Load categories & priorities from in-memory AppState
        categoryComboBox.setItems(FXCollections.observableArrayList(AppState.getInstance().getCategories()));
        priorityComboBox.setItems(FXCollections.observableArrayList(AppState.getInstance().getPriorities()));

        // Set task details
        taskTitleInput.setText(task.getTitle());
        taskDescriptionInput.setText(task.getDescription());

        // Set selected category & priority
        if (task.getCategory() != null) {
            categoryComboBox.getSelectionModel().select(task.getCategory().getName());
        }
        if (task.getPriority() != null) {
            priorityComboBox.getSelectionModel().select(task.getPriority().getName());
        }

        // Set deadline
        if (task.getDeadline() != null && !task.getDeadline().isEmpty()) {
            taskDeadlinePicker.setValue(LocalDate.parse(task.getDeadline()));
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleSaveTask() {
        if (taskTitleInput.getText().isEmpty() || taskDescriptionInput.getText().isEmpty()) {
            showAlert("Error", "Title and Description cannot be empty.");
            return;
        }

        // ✅ Retrieve in-memory task list
        List<Task> tasks = AppState.getInstance().getTasks();

        for (Task t : tasks) {
            if (t.getTitle().equals(task.getTitle()) && t.getDeadline().equals(task.getDeadline())) {
                t.setTitle(taskTitleInput.getText());
                t.setDescription(taskDescriptionInput.getText());
                t.setCategory(new Category(categoryComboBox.getValue()));
                t.setPriority(new Priority(priorityComboBox.getValue()));

                // ✅ Store previous status before modifying the deadline
                Task.TaskStatus previousStatus = t.getStatus();

                // ✅ Update deadline
                t.setDeadline(taskDeadlinePicker.getValue().toString());

                // ✅ Adjust status based on new deadline
                if (previousStatus == Task.TaskStatus.Delayed && taskDeadlinePicker.getValue().isAfter(LocalDate.now())) {
                    t.setStatus(Task.TaskStatus.Open); // Change status back if deadline is in the future
                } else if (taskDeadlinePicker.getValue().isBefore(LocalDate.now())) {
                    t.setStatus(Task.TaskStatus.Delayed); // Keep delayed if still past due
                }
                break;
            }
        }

        // UI Refresh
        if (mainController != null) {
            mainController.refreshTaskList();
        }

        // Close window
        Stage stage = (Stage) saveTaskButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleDeleteTask() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete this task?");
        alert.setContentText("This will also delete all associated reminders.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        // Modify in-memory data
        List<Task> tasks = AppState.getInstance().getTasks();
        tasks.removeIf(t -> t.getTitle().equals(task.getTitle()) && t.getDeadline().equals(task.getDeadline()));

        // UI Refresh
        if (mainController != null) {
            mainController.refreshTaskList();
        }

        // Close window
        Stage stage = (Stage) deleteTaskButton.getScene().getWindow();
        stage.close();
    }
}
