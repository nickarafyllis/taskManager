package com.example.taskmanager.controller;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.storage.TaskStorage;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.List;

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

    private Task task;
    private MainController mainController;

    public void setTask(Task task) {
        this.task = task;

        // Load available categories and priorities
        loadCategories();
        loadPriorities();

        // Set current values
        taskTitleInput.setText(task.getTitle());
        taskDescriptionInput.setText(task.getDescription());

        // Ensure selected category/priority is set after ComboBoxes are populated
        categoryComboBox.setValue(task.getCategory() != null ? task.getCategory().getName() : "");
        priorityComboBox.setValue(task.getPriority() != null ? task.getPriority().getName() : "");

        // Set deadline
        taskDeadlinePicker.setValue(java.time.LocalDate.parse(task.getDeadline()));
    }
    private void loadCategories() {
        List<Task> tasks = TaskStorage.loadTasks();
        List<String> categories = tasks.stream()
                .map(task -> task.getCategory() != null ? task.getCategory().getName() : null)
                .distinct()
                .filter(name -> name != null && !name.isEmpty())
                .toList();

        categoryComboBox.getItems().setAll(categories);
    }

    private void loadPriorities() {
        List<Task> tasks = TaskStorage.loadTasks();
        List<String> priorities = tasks.stream()
                .map(task -> task.getPriority() != null ? task.getPriority().getName() : null)
                .distinct()
                .filter(name -> name != null && !name.isEmpty())
                .toList();

        priorityComboBox.getItems().setAll(priorities);
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

        // Load the existing task list
        List<Task> tasks = TaskStorage.loadTasks();

        // Find the task by its title and deadline
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getTitle().equals(task.getTitle()) && tasks.get(i).getDeadline().equals(task.getDeadline())) {
                // Update task details
                tasks.get(i).setTitle(taskTitleInput.getText());
                tasks.get(i).setDescription(taskDescriptionInput.getText());
                tasks.get(i).setCategory(new com.example.taskmanager.model.Category(categoryComboBox.getValue()));
                tasks.get(i).setPriority(new com.example.taskmanager.model.Priority(priorityComboBox.getValue()));
                tasks.get(i).setDeadline(taskDeadlinePicker.getValue().toString());

                // ✅ Check if the new deadline has passed, update status to "Delayed"
                if (taskDeadlinePicker.getValue().isBefore(java.time.LocalDate.now())) {
                    tasks.get(i).setStatus(Task.TaskStatus.Delayed);
                }

                break; // Stop searching after updating the task
            }
        }

        // Save the modified task list
        TaskStorage.saveTasks(tasks);

        // Ensure the UI refreshes with new data
        mainController.refreshTaskList();

        // Close the edit task window
        Stage stage = (Stage) saveTaskButton.getScene().getWindow();
        stage.close();
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}