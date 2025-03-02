package com.example.taskmanager.controller;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.storage.AppState;
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
import javafx.scene.layout.HBox;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;
import static com.example.taskmanager.utils.AlertUtil.*;

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
    private TableColumn<Task, Void> editColumn;
    @FXML
    private TableColumn<Task, Void> reminderColumn;
    @FXML
    private Label totalTasksLabel;
    @FXML
    private Label completedTasksLabel;
    @FXML
    private Label delayedTasksLabel;
    @FXML
    private Label upcomingTasksLabel;
    @FXML
    private TextField searchTitleField;
    @FXML
    private ComboBox<String> searchCategoryComboBox;
    @FXML
    private ComboBox<String> searchPriorityComboBox;

    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

    private final ObservableList<Task> taskList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configureTableColumns();
        loadCategoriesAndPriorities();
        refreshTaskList();
    }

    private void configureTableColumns() {
        taskTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        descriptionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
        categoryColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory() != null ? data.getValue().getCategory().getName() : "No Category"));
        priorityColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPriority() != null ? data.getValue().getPriority().getName() : "Default"));
        deadlineColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDeadline()));

        configureStatusColumn();
        configureEditButtonColumn();
        configureReminderButtonColumn();
    }

    private void configureStatusColumn() {
        statusColumn.setCellFactory(param -> new TableCell<>() {
            private final ComboBox<String> statusComboBox = new ComboBox<>();
            private final Button saveButton = new Button("Save");
            private String currentStatus;

            {
                statusComboBox.getItems().addAll("Open", "In_Progress", "Completed", "Delayed");
                saveButton.setVisible(false);

                statusComboBox.setOnAction(event -> {
                    String selectedStatus = statusComboBox.getValue();
                    saveButton.setVisible(!selectedStatus.equals(currentStatus));
                });

                saveButton.setOnAction(event -> {
                    Task task = getTableView().getItems().get(getIndex());
                    if (task != null) {
                        task.setStatus(Task.TaskStatus.valueOf(statusComboBox.getValue().replace(" ", "_")));
                        AppState.getInstance().saveData();
                        getTableView().refresh();
                        saveButton.setVisible(false);
                        updateTaskStatistics();
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Task task = getTableView().getItems().get(getIndex());
                    currentStatus = task.getStatus().toString();
                    statusComboBox.setValue(currentStatus);
                    saveButton.setVisible(false);
                    setGraphic(new HBox(5, statusComboBox, saveButton));
                }
            }
        });
    }

    private void configureEditButtonColumn() {
        editColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setOnAction(event -> {
                    Task task = getTableView().getItems().get(getIndex());
                    openEditTaskWindow(task);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : editButton);
            }
        });
    }

    @FXML
    private void openEditTaskWindow(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/edit_task.fxml"));
            Parent root = loader.load();

            EditTaskController editTaskController = loader.getController();
            editTaskController.setTask(task);
            editTaskController.setMainController(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Task");
            stage.setScene(new Scene(root, 450, 400));
            stage.showAndWait();

            refreshTaskList();
        } catch (IOException e) {
            logError("Error opening Edit Task window", e);
        }
    }


    private void configureReminderButtonColumn() {
        reminderColumn.setCellFactory(param -> new TableCell<>() {
            private final Button reminderButton = new Button("Reminders");

            {
                reminderButton.setOnAction(event -> {
                    Task task = getTableView().getItems().get(getIndex());
                    openManageRemindersWindow(task);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : reminderButton);
            }
        });
    }

    private void loadCategoriesAndPriorities() {
        searchCategoryComboBox.setItems(FXCollections.observableArrayList(AppState.getInstance().getCategories()));
        searchPriorityComboBox.setItems(FXCollections.observableArrayList(AppState.getInstance().getPriorities()));
    }

    @FXML
    private void handleSearch() {
        String searchTitle = searchTitleField.getText().trim().toLowerCase();
        String selectedCategory = searchCategoryComboBox.getValue();
        String selectedPriority = searchPriorityComboBox.getValue();

        List<Task> filteredTasks = AppState.getInstance().getTasks().stream()
                .filter(task -> searchTitle.isEmpty() || task.getTitle().toLowerCase().contains(searchTitle))
                .filter(task -> selectedCategory == null || task.getCategory() != null && task.getCategory().getName().equals(selectedCategory))
                .filter(task -> selectedPriority == null || task.getPriority() != null && task.getPriority().getName().equals(selectedPriority))
                .toList();

        taskList.setAll(filteredTasks);
    }

    @FXML
    private void clearSearch() {
        searchTitleField.clear();
        searchCategoryComboBox.getSelectionModel().clearSelection();
        searchPriorityComboBox.getSelectionModel().clearSelection();
        refreshTaskList();
    }

    public void refreshTaskList() {
        taskList.setAll(AppState.getInstance().getTasks());
        taskTable.setItems(taskList);
        taskTable.refresh();
        updateTaskStatistics();
    }

    private void updateTaskStatistics() {
        List<Task> tasks = AppState.getInstance().getTasks();
        int total = tasks.size();
        int completed = (int) tasks.stream().filter(task -> task.getStatus() == Task.TaskStatus.Completed).count();
        int delayed = (int) tasks.stream().filter(task -> task.getStatus() == Task.TaskStatus.Delayed).count();
        int upcoming = (int) tasks.stream()
                .filter(task -> {
                    LocalDate deadline = LocalDate.parse(task.getDeadline());
                    return deadline.isAfter(LocalDate.now()) && deadline.isBefore(LocalDate.now().plusDays(7));
                }).count();

        totalTasksLabel.setText("Total Tasks: " + total);
        completedTasksLabel.setText("Completed: " + completed);
        delayedTasksLabel.setText("Delayed: " + delayed);
        upcomingTasksLabel.setText("Upcoming (7 days): " + upcoming);
    }
    @FXML
    private void openManageRemindersWindow(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/manage_reminders.fxml"));
            Parent root = loader.load();

            ManageRemindersController controller = loader.getController();
            controller.setTask(task); // Pass the task to the controller

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Manage Reminders");
            stage.setScene(new Scene(root, 500, 400));
            stage.showAndWait();

            refreshTaskList(); // Refresh the task list after closing
        } catch (IOException e) {
            logError("Error opening Manage Reminders window", e);
        }
    }

    private void openModalWindow(String resource, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title);
            stage.setScene(new Scene(loader.load(), width, height));
            stage.showAndWait();
        } catch (IOException e) {
            logError("Error opening window: " + title, e);
        }
    }

    @FXML
    private void openAddTaskWindow() {
        openModalWindow("/views/add_task.fxml", "Add Task", 450, 400);
        refreshTaskList();
    }

    @FXML
    private void openManageCategoriesWindow() {
        openModalWindow("/views/manage_categories.fxml", "Manage Categories", 500, 400);
        loadCategoriesAndPriorities();
        refreshTaskList();
    }

    @FXML
    private void openManagePrioritiesWindow() {
        openModalWindow("/views/manage_priorities.fxml", "Manage Priorities", 500, 400);
        loadCategoriesAndPriorities();
        refreshTaskList();
    }

    @FXML
    private void openViewAllRemindersWindow() {
        openModalWindow("/views/view_all_reminders.fxml", "All Active Reminders", 650, 400);
    }
}
