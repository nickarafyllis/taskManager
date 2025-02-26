package com.example.taskmanager.controller;

import com.example.taskmanager.model.*;
import com.example.taskmanager.storage.TaskStorage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

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

    private static final String CATEGORIES_FILE = "src/main/resources/medialab/categories.json";
    private static final String PRIORITIES_FILE = "src/main/resources/medialab/priorities.json";
    private static final String TASKS_FILE = "src/main/resources/medialab/tasks.json";
    private static final Gson gson = new Gson();

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        loadCategories();
        loadPriorities();
    }

    private void loadCategories() {
        File file = new File(CATEGORIES_FILE);
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                Type listType = new TypeToken<List<String>>() {}.getType();
                List<String> loadedCategories = gson.fromJson(reader, listType);
                if (loadedCategories == null) {
                    loadedCategories = List.of();
                }
                categoryComboBox.setItems(FXCollections.observableArrayList(loadedCategories));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            categoryComboBox.setItems(FXCollections.observableArrayList());
        }
    }

    private void loadPriorities() {
        File file = new File(PRIORITIES_FILE);
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                Type listType = new TypeToken<List<String>>() {}.getType();
                List<String> loadedPriorities = gson.fromJson(reader, listType);
                if (loadedPriorities == null) {
                    loadedPriorities = List.of();
                }
                priorityComboBox.setItems(FXCollections.observableArrayList(loadedPriorities));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            priorityComboBox.setItems(FXCollections.observableArrayList());
        }
    }

    @FXML
    private void handleSaveTask() {
        String title = taskTitleInput.getText();
        String description = taskDescriptionInput.getText();
        String category = categoryComboBox.getValue();
        String priority = priorityComboBox.getValue();
        String deadline = taskDeadlinePicker.getValue() != null ? taskDeadlinePicker.getValue().toString() : "";

        if (title.isEmpty() || description.isEmpty() || deadline.isEmpty()) {
            showAlert("Error", "Title, Description, and Deadline are required.");
            return;
        }

        Task newTask = new Task(title, description, new Category(category), new Priority(priority), deadline);

        List<Task> tasks = TaskStorage.loadTasks();
        tasks.add(newTask);
        TaskStorage.saveTasks(tasks);

        if (mainController != null) {
            mainController.refreshTaskList();
        }

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
