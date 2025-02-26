package com.example.taskmanager.controller;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.storage.TaskStorage;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;

public class MainController {

    @FXML
    private TableView<Task> taskTable;
    @FXML
    private TableColumn<Task, String> titleColumn;
    @FXML
    private TableColumn<Task, String> statusColumn;
    @FXML
    private TableColumn<Task, String> descriptionColumn;
    @FXML
    private TableColumn<Task, String> categoryColumn;
    @FXML
    private TableColumn<Task, String> priorityColumn;
    @FXML
    private TableColumn<Task, String> deadlineColumn;
    @FXML
    private TextField taskTitleInput;
    @FXML
    private TextArea taskDescriptionInput;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    private final ObservableList<Task> taskList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        descriptionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
        categoryColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory() != null ? data.getValue().getCategory().getName() : "No Category"));
        priorityColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPriority() != null ? data.getValue().getPriority().getName() : "Default"));
        deadlineColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDeadline()));
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus().toString()));

        refreshTaskList(); // Ensure task list loads on startup
    }

    public void refreshTaskList() {
        List<Task> tasks = TaskStorage.loadTasks();
        taskList.setAll(tasks); // Reload the task list
        taskTable.setItems(taskList);
        taskTable.refresh(); // Ensure the UI updates
    }

    @FXML
    private void openAddTaskWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/add_task.fxml"));
            Parent root = loader.load();

            // Get the controller and pass MainController reference
            AddTaskController addTaskController = loader.getController();
            addTaskController.setMainController(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Task");
            stage.setScene(new Scene(root, 450, 400));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openManageCategoriesWindow() {
        openWindow("/views/manage_categories.fxml", "Manage Categories", 500, 400);
    }

    @FXML
    private void openManagePrioritiesWindow() {
        openWindow("/views/manage_priorities.fxml", "Manage Priorities", 500, 400);
    }

    @FXML
    private void openManageRemindersWindow() {
        openWindow("/views/manage_reminders.fxml", "Manage Reminders", 500, 400);
    }

    private void openWindow(String resource, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title);
            stage.setScene(new Scene(loader.load(), width, height));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditTask() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert("Selection Error", "No task selected.");
            return;
        }

        selectedTask.setTitle(taskTitleInput.getText());
        selectedTask.setDescription(taskDescriptionInput.getText());
        taskTable.refresh();  // Update UI
        TaskStorage.saveTasks(taskList);  // Save changes
    }

    @FXML
    private void handleDeleteTask() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert("Selection Error", "No task selected.");
            return;
        }

        taskList.remove(selectedTask);
        TaskStorage.saveTasks(taskList);  // Save after deletion
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
