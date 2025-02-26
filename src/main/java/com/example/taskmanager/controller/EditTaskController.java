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
        taskTitleInput.setText(task.getTitle());
        taskDescriptionInput.setText(task.getDescription());
        categoryComboBox.setValue(task.getCategory() != null ? task.getCategory().getName() : "");
        priorityComboBox.setValue(task.getPriority() != null ? task.getPriority().getName() : "");
        taskDeadlinePicker.setValue(task.getDeadlineAsLocalDate()); // Now this works!
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleSaveTask() {
        task.setTitle(taskTitleInput.getText());
        task.setDescription(taskDescriptionInput.getText());
        task.setCategory(new com.example.taskmanager.model.Category(categoryComboBox.getValue()));
        task.setPriority(new com.example.taskmanager.model.Priority(priorityComboBox.getValue()));
        task.setDeadlineFromLocalDate(taskDeadlinePicker.getValue()); // Now this works!

        List<Task> tasks = TaskStorage.loadTasks();
        TaskStorage.saveTasks(tasks);

        mainController.refreshTaskList();

        // Close the window
        Stage stage = (Stage) saveTaskButton.getScene().getWindow();
        stage.close();
    }
}
