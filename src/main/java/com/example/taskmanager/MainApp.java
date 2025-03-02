package com.example.taskmanager;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.storage.TaskStorage;
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
            // Ensure the FXML file is loaded correctly
            URL fxmlLocation = getClass().getResource("/views/main.fxml");
            if (fxmlLocation == null) {
                throw new IOException("FXML file not found! Check path: /views/main.fxml");
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            BorderPane root = loader.load();

            // Create scene
            Scene scene = new Scene(root, 1300, 600);

            // Load CSS file
            URL cssLocation = getClass().getResource("/styles/styles.css");
            if (cssLocation == null) {
                throw new IOException("CSS file not found! Check path: /styles/styles.css");
            }
            scene.getStylesheets().add(cssLocation.toExternalForm()); // Apply CSS

            // Set stage properties
            primaryStage.setTitle("MediaLab Assistant");
            primaryStage.setScene(scene);
            primaryStage.show();

            // Check for delayed tasks and show popup if needed
            checkDelayedTasksAndReminders();

        } catch (IOException e) {
            logError("Error loading main.fxml", e);
        }
    }

    private void checkDelayedTasksAndReminders() {
        List<Task> tasks = TaskStorage.loadTasks();
        LocalDate today = LocalDate.now();

        // ✅ Get delayed tasks
        List<String> delayedTaskNames = tasks.stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.Delayed)
                .map(Task::getTitle)
                .toList();

        // ✅ Get today's reminders
        List<String> todaysReminders = tasks.stream()
                .flatMap(task -> task.getReminders().stream()
                        .filter(reminder -> reminder.getDateTime().toLocalDate().isEqual(today))
                        .map(reminder -> task.getTitle() + " - " + reminder.getType()))
                .toList();

        // ✅ Build the alert message
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

        // ✅ Show the alert only if there are delayed tasks or reminders
        if (!message.isEmpty()) {
            showAlert("Task Notifications", message.toString());
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}

