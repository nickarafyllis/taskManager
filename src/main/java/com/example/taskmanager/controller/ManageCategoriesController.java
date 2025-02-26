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
import java.util.ArrayList;
import java.util.List;

public class ManageCategoriesController {

    @FXML
    private ListView<String> categoryListView;
    @FXML
    private TextField categoryInput;

    private final ObservableList<String> categories = FXCollections.observableArrayList();
    private static final String FILE_PATH = "src/main/resources/medialab/categories.json";
    private static final Gson gson = new Gson();

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        loadCategories();
        categoryListView.setItems(categories);
    }

    @FXML
    private void handleAddCategory() {
        String newCategory = categoryInput.getText().trim();
        if (!newCategory.isEmpty() && !categories.contains(newCategory)) {
            categories.add(newCategory);
            categoryInput.clear();
            saveCategories();
            loadCategories(); // Ensure UI refresh after saving
            if (mainController != null) {
                mainController.refreshTaskList();
            }
        }
    }

    @FXML
    private void handleEditCategory() {
        String selectedCategory = categoryListView.getSelectionModel().getSelectedItem();
        String newCategory = categoryInput.getText().trim();
        if (selectedCategory != null && !newCategory.isEmpty() && !categories.contains(newCategory)) {
            categories.set(categories.indexOf(selectedCategory), newCategory);
            categoryInput.clear();
            saveCategories();
            loadCategories(); // Ensure UI refresh after saving
            if (mainController != null) {
                mainController.refreshTaskList();
            }
        }
    }

    @FXML
    private void handleDeleteCategory() {
        String selectedCategory = categoryListView.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            categories.remove(selectedCategory);
            saveCategories();
            loadCategories(); // Ensure UI refresh after saving
            if (mainController != null) {
                mainController.refreshTaskList();
            }
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) categoryListView.getScene().getWindow();
        stage.close();
    }

    private void loadCategories() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                Type listType = new TypeToken<List<String>>() {}.getType();
                List<String> loadedCategories = gson.fromJson(reader, listType);
                if (loadedCategories == null) {
                    loadedCategories = new ArrayList<>();
                }
                categories.setAll(loadedCategories);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            file.getParentFile().mkdirs(); // Ensure medialab directory exists
            saveCategories(); // Create an empty file if it doesn't exist
        }
    }

    private void saveCategories() {
        File file = new File(FILE_PATH);
        try {
            file.getParentFile().mkdirs(); // Ensure directory exists
            try (Writer writer = new FileWriter(file)) {
                gson.toJson(categories, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
