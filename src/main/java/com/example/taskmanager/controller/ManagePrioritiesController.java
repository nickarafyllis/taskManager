package com.example.taskmanager.controller;

import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.storage.TaskStorage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

public class ManagePrioritiesController {

    @FXML
    private ListView<String> priorityListView;
    @FXML
    private TextField priorityInput;

    private final ObservableList<String> priorities = FXCollections.observableArrayList();
    private static final String FILE_PATH = "src/main/resources/medialab/priorities.json";
    private static final Gson gson = new Gson();

    @FXML
    public void initialize() {
        loadPriorities();
        priorityListView.setItems(priorities);
    }

    @FXML
    private void handleAddPriority() {
        String newPriority = priorityInput.getText().trim();
        if (!newPriority.isEmpty() && !priorities.contains(newPriority)) {
            priorities.add(newPriority);
            priorityInput.clear();
            savePriorities();
            refreshTaskCreationOptions();
        }
    }

    @FXML
    private void handleEditPriority() {
        String selectedPriority = priorityListView.getSelectionModel().getSelectedItem();
        String newPriority = priorityInput.getText().trim();

        if (selectedPriority == null || newPriority.isEmpty() || priorities.contains(newPriority)) {
            return; // Invalid input
        }

        if (selectedPriority.equals("Default")) {
            showAlert("Error", "The 'Default' priority cannot be renamed.");
            return;
        }

        priorities.set(priorities.indexOf(selectedPriority), newPriority);
        priorityInput.clear();
        savePriorities();
        refreshTaskCreationOptions();
    }


    @FXML
    private void handleDeletePriority() {
        String selectedPriority = priorityListView.getSelectionModel().getSelectedItem();

        if (selectedPriority == null) {
            showAlert("Error", "No priority selected.");
            return;
        }

        if (selectedPriority.equals("Default")) {
            showAlert("Error", "The 'Default' priority cannot be deleted.");
            return;
        }

        // Confirm deletion
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete priority: " + selectedPriority);
        alert.setContentText("All tasks with this priority will be reassigned to 'Default'.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        // Remove from list and save
        priorities.remove(selectedPriority);
        savePriorities();

        // ✅ Update tasks that had the deleted priority
        List<Task> tasks = TaskStorage.loadTasks();
        for (Task task : tasks) {
            if (task.getPriority() != null && task.getPriority().getName().equals(selectedPriority)) {
                task.setPriority(new Priority("Default")); // Assign "Default"
            }
        }
        TaskStorage.saveTasks(tasks);

        refreshTaskCreationOptions();
        loadPriorities();
    }


    private void loadPriorities() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                Type listType = new TypeToken<List<String>>() {}.getType();
                List<String> loadedPriorities = gson.fromJson(reader, listType);

                if (!loadedPriorities.contains("Default")) {
                    loadedPriorities.add(0, "Default"); // Ensure Default is always first
                }
                priorities.setAll(loadedPriorities);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            priorities.setAll("Default"); // First-time initialization
            savePriorities(); // Save the default list
        }
    }


    private void savePriorities() {
        File file = new File(FILE_PATH);
        try {
            file.getParentFile().mkdirs(); // Ensure directory exists
            try (Writer writer = new FileWriter(file)) {
                gson.toJson(priorities, writer);
            }
            loadPriorities();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshTaskCreationOptions() {
        // Placeholder for UI update logic to refresh the dropdown menu in task creation
        //System.out.println("Priorities updated! UI needs to refresh task creation dropdown.");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
