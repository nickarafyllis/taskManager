package com.example.taskmanager.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
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
        if (selectedPriority != null && !newPriority.isEmpty() && !priorities.contains(newPriority)) {
            priorities.set(priorities.indexOf(selectedPriority), newPriority);
            priorityInput.clear();
            savePriorities();
            refreshTaskCreationOptions();
        }
    }

    @FXML
    private void handleDeletePriority() {
        String selectedPriority = priorityListView.getSelectionModel().getSelectedItem();
        if (selectedPriority != null) {
            priorities.remove(selectedPriority);
            savePriorities();
            refreshTaskCreationOptions();
            loadPriorities();
        }
    }

    private void loadPriorities() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                Type listType = new TypeToken<List<String>>() {}.getType();
                List<String> loadedPriorities = gson.fromJson(reader, listType);
                priorities.setAll(loadedPriorities);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
}
