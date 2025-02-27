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
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private TableColumn<Task, Void> editColumn;
    @FXML
    private Label totalTasksLabel;
    @FXML
    private Label completedTasksLabel;
    @FXML
    private Label delayedTasksLabel;
    @FXML
    private Label upcomingTasksLabel;
    @FXML
    private TableColumn<Task, String> reminderColumn;
    @FXML
    private TextField searchTitleField;
    @FXML
    private ComboBox<String> searchCategoryComboBox;
    @FXML
    private ComboBox<String> searchPriorityComboBox;

    private List<Task> allTasks = new ArrayList<>(); // Initialize to avoid null pointer




    private final ObservableList<Task> taskList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        taskTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        titleColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.15));
        descriptionColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.25));
        categoryColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.15));
        priorityColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.10));
        deadlineColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.15));
        statusColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.10));
        editColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.10));
        reminderColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.10));

        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        descriptionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
        categoryColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getCategory() != null ? data.getValue().getCategory().getName() : "No Category"));
        priorityColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getPriority() != null ? data.getValue().getPriority().getName() : "Default"));
        deadlineColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDeadline()));
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus().toString()));

        // Add Edit button inside the table
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
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });

        reminderColumn.setCellFactory(param -> new TableCell<>() {
            private final Button reminderButton = new Button("Reminders");

            {
                reminderButton.setOnAction(event -> {
                    Task task = getTableView().getItems().get(getIndex());
                    openManageRemindersWindow(task);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(reminderButton);
                }
            }
        });


        refreshTaskList();

        // Populate category and priority filters
        loadCategoriesAndPriorities();
    }

    // Load all categories & priorities into ComboBoxes
    private void loadCategoriesAndPriorities() {
        List<Task> tasks = TaskStorage.loadTasks();
        List<String> categories = tasks.stream()
                .map(task -> task.getCategory() != null ? task.getCategory().getName() : null)
                .filter(name -> name != null && !name.isEmpty())
                .distinct()
                .toList();

        List<String> priorities = tasks.stream()
                .map(task -> task.getPriority() != null ? task.getPriority().getName() : null)
                .filter(name -> name != null && !name.isEmpty())
                .distinct()
                .toList();

        searchCategoryComboBox.getItems().setAll(categories);
        searchPriorityComboBox.getItems().setAll(priorities);
    }

    // Handle search button click
    @FXML
    private void handleSearch() {
        if (allTasks == null) {
            allTasks = TaskStorage.loadTasks(); // ✅ Ensure allTasks is never null
        }

        String searchTitle = searchTitleField.getText().trim().toLowerCase();
        String selectedCategory = searchCategoryComboBox.getValue();
        String selectedPriority = searchPriorityComboBox.getValue();

        List<Task> filteredTasks = allTasks.stream()
                .filter(task -> (searchTitle.isEmpty() || task.getTitle().toLowerCase().contains(searchTitle)))
                .filter(task -> (selectedCategory == null || selectedCategory.isEmpty() ||
                        (task.getCategory() != null && task.getCategory().getName().equals(selectedCategory))))
                .filter(task -> (selectedPriority == null || selectedPriority.isEmpty() ||
                        (task.getPriority() != null && task.getPriority().getName().equals(selectedPriority))))
                .toList();

        taskList.setAll(filteredTasks);
    }


    // Handle clear button click
    @FXML
    private void clearSearch() {
        searchTitleField.clear();
        searchCategoryComboBox.getSelectionModel().clearSelection();
        searchPriorityComboBox.getSelectionModel().clearSelection();
        taskList.setAll(allTasks);
    }

    @FXML
    private void openManageRemindersWindow(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/task_reminders.fxml"));
            Parent root = loader.load();

            TaskNotificationController taskNotificationController = loader.getController();
            taskNotificationController.setTask(task);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Manage Reminders for " + task.getTitle());
            stage.setScene(new Scene(root, 400, 300));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void openAddTaskWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/add_task.fxml"));
            Parent root = loader.load();

            AddTaskController addTaskController = loader.getController();
            addTaskController.setMainController(this); // Pass the reference

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Task");
            stage.setScene(new Scene(root, 450, 400));
            stage.showAndWait();

            refreshTaskList(); // Refresh task list after adding
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

    private void addEditButtonToTable() {
        Callback<TableColumn<Task, Void>, TableCell<Task, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Task, Void> call(final TableColumn<Task, Void> param) {
                return new TableCell<>() {
                    private final Button editButton = new Button("Edit");

                    {
                        editButton.setOnAction(event -> {
                            Task task = getTableView().getItems().get(getIndex());
                            openEditTaskWindow(task);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(new HBox(editButton));
                        }
                    }
                };
            }
        };
        editColumn.setCellFactory(cellFactory);
    }

    public void refreshTaskList() {
        allTasks = TaskStorage.loadTasks(); // ✅ Ensure allTasks is always updated
        taskList.setAll(allTasks); // ✅ ObservableList is updated with allTasks
        taskTable.setItems(taskList);
        taskTable.refresh();

        updateTaskStatistics(allTasks);
    }

    private void updateTaskStatistics(List<Task> tasks) {
        int total = tasks.size();
        int completed = (int) tasks.stream().filter(task -> task.getStatus() == Task.TaskStatus.Completed).count();
        int delayed = (int) tasks.stream().filter(task -> task.getStatus() == Task.TaskStatus.Delayed).count();

        LocalDate today = LocalDate.now();
        int upcoming = (int) tasks.stream()
                .filter(task -> {
                    LocalDate deadline = LocalDate.parse(task.getDeadline());
                    return deadline.isAfter(today) && deadline.isBefore(today.plusDays(7));
                })
                .count();

        totalTasksLabel.setText("Total Tasks: " + total);
        completedTasksLabel.setText("Completed: " + completed);
        delayedTasksLabel.setText("Delayed: " + delayed);
        upcomingTasksLabel.setText("Upcoming (7 days): " + upcoming);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
