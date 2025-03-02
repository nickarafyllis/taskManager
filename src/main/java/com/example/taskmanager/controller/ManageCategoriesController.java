package com.example.taskmanager.controller;

import com.example.taskmanager.model.Category;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.storage.AppState;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static com.example.taskmanager.utils.AlertUtil.*;

public class ManageCategoriesController {

    @FXML
    private ListView<String> categoryListView;
    @FXML
    private TextField categoryInput;

    private final ObservableList<String> categories = FXCollections.observableArrayList();
    private static final Logger LOGGER = Logger.getLogger(ManageCategoriesController.class.getName());

    @FXML
    public void initialize() {
        loadCategories();
        categoryListView.setItems(categories);
    }

    @FXML
    private void handleAddCategory() {
        String newCategory = categoryInput.getText().trim();
        if (newCategory.isEmpty()) {
            showAlert("Error", "Category name cannot be empty.");
            return;
        }

        if (!categories.contains(newCategory)) {
            categories.add(newCategory);
            categoryInput.clear();
            saveCategories();
            showAlert("Success", "Category added successfully.");
        } else {
            showAlert("Error", "Category already exists.");
        }
    }

    @FXML
    private void handleEditCategory() {
        String selectedCategory = categoryListView.getSelectionModel().getSelectedItem();
        String newCategory = categoryInput.getText().trim();

        if (selectedCategory == null) {
            showAlert("Error", "No category selected.");
            return;
        }
        if (newCategory.isEmpty()) {
            showAlert("Error", "New category name cannot be empty.");
            return;
        }
        if (categories.contains(newCategory)) {
            showAlert("Error", "Category already exists.");
            return;
        }

        // ✅ Update category name in the tasks
        for (Task task : AppState.getInstance().getTasks()) {
            if (task.getCategory() != null && task.getCategory().getName().equals(selectedCategory)) {
                task.setCategory(new Category(newCategory));
            }
        }

        // ✅ Update category in the list
        categories.set(categories.indexOf(selectedCategory), newCategory);

        // ✅ Save changes
        saveCategories();
        showAlert("Success", "Category updated successfully.");
    }


    @FXML
    private void handleDeleteCategory() {
        String selectedCategory = categoryListView.getSelectionModel().getSelectedItem();

        if (selectedCategory == null) {
            showAlert("Error", "No category selected.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete this category?");
        alert.setContentText("All tasks under this category and their reminders will also be deleted.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        // ✅ Remove category and update in-memory storage
        categories.remove(selectedCategory);
        AppState.getInstance().getTasks().removeIf(task ->
                task.getCategory() != null && task.getCategory().getName().equals(selectedCategory));

        saveCategories();
        showAlert("Success", "Category and all related tasks and reminders deleted.");
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) categoryListView.getScene().getWindow();
        stage.close();
    }

    private void loadCategories() {
        List<String> storedCategories = AppState.getInstance().getCategories();
        categories.setAll(storedCategories);
    }

    private void saveCategories() {
        AppState.getInstance().getCategories().clear();
        AppState.getInstance().getCategories().addAll(categories);
        //LOGGER.log(Level.INFO, "Categories updated in memory.");
    }
}
