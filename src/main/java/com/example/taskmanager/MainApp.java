package com.example.taskmanager;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.storage.AppState;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import static com.example.taskmanager.utils.AlertUtil.*;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load all data from JSON into memory at startup
            AppState.getInstance(); // Calls constructor that loads data

            // Ensure the FXML file is loaded correctly
            URL fxmlLocation = getClass().getResource("/views/main.fxml");
            if (fxmlLocation == null) {
                throw new IOException("FXML file not found! Check path: /views/main.fxml");
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            BorderPane root = loader.load();

            // Create and style the scene
            Scene scene = new Scene(root, 1300, 600);
            URL cssLocation = getClass().getResource("/styles/styles.css");
            assert cssLocation != null;
            scene.getStylesheets().add(cssLocation.toExternalForm());

            // Set stage properties
            primaryStage.setTitle("MediaLab Assistant");
            primaryStage.setScene(scene);
            primaryStage.show();

            // Show notification popups for delayed tasks and today's reminders
            checkDelayedTasksAndReminders();

            // Ensure data is saved before application exits
            primaryStage.setOnCloseRequest(event -> {
                AppState.getInstance().saveData();
            });

        } catch (IOException e) {
            logError("Error loading main.fxml", e);
        }
    }

    private void checkDelayedTasksAndReminders() {
        List<Task> tasks = AppState.getInstance().getTasks(); // ✅ Fetch from memory
        LocalDate today = LocalDate.now();

        // Get delayed tasks
        List<String> delayedTaskNames = tasks.stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.Delayed)
                .map(Task::getTitle)
                .toList();

        // Get today's reminders
        List<String> todaysReminders = tasks.stream()
                .flatMap(task -> task.getReminders().stream()
                        .filter(reminder -> reminder.getDateTime().toLocalDate().isEqual(today))
                        .map(reminder -> task.getTitle() + " - " + reminder.getType()))
                .toList();

        // Build the alert message
        StringBuilder message = new StringBuilder();

        if (!delayedTaskNames.isEmpty()) {
            message.append((delayedTaskNames.size() == 1)
                    ? "There is 1 overdue task:\n"
                    : "There are " + delayedTaskNames.size() + " overdue tasks:\n");
            message.append("- ").append(String.join("\n- ", delayedTaskNames)).append("\n\n");
        }

        if (!todaysReminders.isEmpty()) {
            message.append((todaysReminders.size() == 1)
                    ? "There is 1 reminder for today:\n"
                    : "There are " + todaysReminders.size() + " reminders for today:\n");
            message.append("- ").append(String.join("\n- ", todaysReminders)).append("\n");
        }

        // Show the alert only if there are delayed tasks or reminders
        if (!message.isEmpty()) {
            showAlert("Task Notifications", message.toString());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
