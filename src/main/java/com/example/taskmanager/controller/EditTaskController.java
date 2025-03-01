package com.example.taskmanager.controller;

import com.example.taskmanager.model.*;
import com.example.taskmanager.storage.TaskStorage;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EditTaskController {
    private static final Logger LOGGER = Logger.getLogger(EditTaskController.class.getName());

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

    private static final Gson gson = new Gson();

    private Task task;
    private MainController mainController;

    public void setTask(Task task) {
        this.task = task;

        // ✅ Load categories & priorities from memory (not a separate storage class)
        loadCategories();
        loadPriorities();

        // ✅ Set task details
        taskTitleInput.setText(task.getTitle());
        taskDescriptionInput.setText(task.getDescription());

        // ✅ Set selected category & priority
        if (task.getCategory() != null) {
            categoryComboBox.getSelectionModel().select(task.getCategory().getName());
        }
        if (task.getPriority() != null) {
            priorityComboBox.getSelectionModel().select(task.getPriority().getName());
        }

        // ✅ Set deadline
        taskDeadlinePicker.setValue(java.time.LocalDate.parse(task.getDeadline()));
    }

    private void loadCategories() {
        File file = new File("src/main/resources/medialab/categories.json");
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                Type listType = new TypeToken<List<String>>() {}.getType();
                List<String> loadedCategories = gson.fromJson(reader, listType);
                if (loadedCategories == null) {
                    loadedCategories = List.of();
                }
                categoryComboBox.setItems(FXCollections.observableArrayList(loadedCategories));
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error loading categories", e);
            }
        } else {
            categoryComboBox.setItems(FXCollections.observableArrayList());
        }
    }

    private void loadPriorities() {
        File file = new File("src/main/resources/medialab/priorities.json");
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                Type listType = new TypeToken<List<String>>() {}.getType();
                List<String> loadedPriorities = gson.fromJson(reader, listType);
                if (loadedPriorities == null) {
                    loadedPriorities = List.of();
                }
                priorityComboBox.setItems(FXCollections.observableArrayList(loadedPriorities));
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error loading priorities", e);
            }
        } else {
            priorityComboBox.setItems(FXCollections.observableArrayList());
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

        List<Task> tasks = TaskStorage.loadTasks();

        for (Task t : tasks) {
            if (t.getTitle().equals(task.getTitle()) && t.getDeadline().equals(task.getDeadline())) {
                t.setTitle(taskTitleInput.getText());
                t.setDescription(taskDescriptionInput.getText());
                t.setCategory(new Category(categoryComboBox.getValue()));
                t.setPriority(new Priority(priorityComboBox.getValue()));

                // Store the previous status before modifying the deadline
                Task.TaskStatus previousStatus = t.getStatus();

                // Update the deadline
                t.setDeadline(taskDeadlinePicker.getValue().toString());

                // If the previous status was "Delayed" and the new deadline is in the future
                if (previousStatus == Task.TaskStatus.Delayed && taskDeadlinePicker.getValue().isAfter(java.time.LocalDate.now())) {
                    // Restore to "Open" or another valid state
                    t.setStatus(Task.TaskStatus.Open);  // Change this if there was another state before "Delayed"
                } else if (taskDeadlinePicker.getValue().isBefore(java.time.LocalDate.now())) {
                    // If the new deadline is still in the past, keep it as "Delayed"
                    t.setStatus(Task.TaskStatus.Delayed);
                }

                break;
            }
        }

        // ✅ Save the modified task list
        TaskStorage.saveTasks(tasks);

        // ✅ Refresh UI
        mainController.refreshTaskList();

        // ✅ Close the edit task window
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

        List<Task> tasks = TaskStorage.loadTasks();
        tasks.removeIf(t -> t.getTitle().equals(task.getTitle()) && t.getDeadline().equals(task.getDeadline()));
        TaskStorage.saveTasks(tasks);

        // Refresh UI in the main controller
        if (mainController != null) {
            mainController.refreshTaskList();
        }

        Stage stage = (Stage) deleteTaskButton.getScene().getWindow();
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
