package com.example.taskmanager.controller;

import com.example.taskmanager.storage.AppState;
import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static com.example.taskmanager.utils.AlertUtil.*;

public class ManagePrioritiesController {

    @FXML
    private ListView<String> priorityListView;
    @FXML
    private TextField priorityInput;

    private final ObservableList<String> priorities = FXCollections.observableArrayList();
    private static final Logger LOGGER = Logger.getLogger(ManagePrioritiesController.class.getName());

    @FXML
    public void initialize() {
        loadPriorities();
        priorityListView.setItems(priorities);
    }

    @FXML
    private void handleAddPriority() {
        String newPriority = priorityInput.getText().trim();
        if (newPriority.isEmpty()) {
            showAlert("Error", "Priority name cannot be empty.");
            return;
        }

        if (!priorities.contains(newPriority)) {
            priorities.add(newPriority);
            priorityInput.clear();
            savePriorities();
            showAlert("Success", "Priority added successfully.");
        } else {
            showAlert("Error", "Priority already exists.");
        }
    }

    @FXML
    private void handleEditPriority() {
        String selectedPriority = priorityListView.getSelectionModel().getSelectedItem();
        String newPriority = priorityInput.getText().trim();

        if (selectedPriority == null) {
            showAlert("Error", "No priority selected.");
            return;
        }
        if (newPriority.isEmpty()) {
            showAlert("Error", "New priority name cannot be empty.");
            return;
        }
        if (priorities.contains(newPriority)) {
            showAlert("Error", "Priority already exists.");
            return;
        }
        if (selectedPriority.equals("Default")) {
            showAlert("Error", "The 'Default' priority cannot be renamed.");
            return;
        }

        // ✅ Update priority name in tasks
        for (Task task : AppState.getInstance().getTasks()) {
            if (task.getPriority() != null && task.getPriority().getName().equals(selectedPriority)) {
                task.setPriority(new Priority(newPriority));
            }
        }

        // ✅ Update priority in the list
        priorities.set(priorities.indexOf(selectedPriority), newPriority);

        // ✅ Save changes
        savePriorities();
        showAlert("Success", "Priority updated successfully.");
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

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete priority: " + selectedPriority);
        alert.setContentText("All tasks with this priority will be reassigned to 'Default'.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        // ✅ Remove priority and update in-memory storage
        priorities.remove(selectedPriority);
        List<Task> tasks = AppState.getInstance().getTasks();
        for (Task task : tasks) {
            if (task.getPriority() != null && task.getPriority().getName().equals(selectedPriority)) {
                task.setPriority(new Priority("Default")); // Assign "Default"
            }
        }

        savePriorities();
        showAlert("Success", "Priority deleted, and all affected tasks reassigned.");
    }

    private void loadPriorities() {
        List<String> storedPriorities = AppState.getInstance().getPriorities();

        if (!storedPriorities.contains("Default")) {
            storedPriorities.addFirst("Default"); // Ensure "Default" is always first
        }
        priorities.setAll(storedPriorities);
    }

    private void savePriorities() {
        AppState.getInstance().getPriorities().clear();
        AppState.getInstance().getPriorities().addAll(priorities);
        //LOGGER.log(Level.INFO, "Priorities updated in memory.");
    }
}
