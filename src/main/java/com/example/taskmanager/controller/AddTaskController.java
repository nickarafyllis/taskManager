package com.example.taskmanager.controller;

import com.example.taskmanager.model.*;
import com.example.taskmanager.storage.AppState;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import static com.example.taskmanager.utils.AlertUtil.*;

public class AddTaskController {

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

    private MainController mainController;


    @FXML
    public void initialize() {
        // ✅ Load categories & priorities from memory instead of JSON files
        categoryComboBox.setItems(FXCollections.observableArrayList(AppState.getInstance().getCategories()));
        priorityComboBox.setItems(FXCollections.observableArrayList(AppState.getInstance().getPriorities()));
    }

    @FXML
    private void handleSaveTask() {
        String title = taskTitleInput.getText().trim();
        String description = taskDescriptionInput.getText().trim();
        String category = categoryComboBox.getValue();
        String priority = priorityComboBox.getValue();
        String deadline = (taskDeadlinePicker.getValue() != null) ? taskDeadlinePicker.getValue().toString() : "";

        // Validate required fields
        if (title.isEmpty() || description.isEmpty() || category == null || priority == null || deadline.isEmpty()) {
            showAlert("Error", "Title, Description, Category, Priority, and Deadline are required.");
            return;
        }

        // Create a new Task object
        Task newTask = new Task(title, description, new Category(category), new Priority(priority), deadline);

        // Add task to memory (not JSON)
        AppState.getInstance().getTasks().add(newTask);

        // Refresh UI in MainController if available
        if (mainController != null) {
            mainController.refreshTaskList();
        }

        // Close the window
        Stage stage = (Stage) saveTaskButton.getScene().getWindow();
        stage.close();
    }
}
