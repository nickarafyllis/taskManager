package com.example.taskmanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;


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

            Scene scene = new Scene(root, 900, 400);
            primaryStage.setTitle("MediaLab Assistant");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

