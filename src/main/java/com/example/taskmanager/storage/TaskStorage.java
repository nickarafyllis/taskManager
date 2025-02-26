package com.example.taskmanager.storage;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.example.taskmanager.model.Task;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskStorage {
    private static final String FILE_PATH = "src/main/resources/medialab/tasks.json";
    private static final Logger LOGGER = Logger.getLogger(TaskStorage.class.getName());

    // Gson instance with LocalDateTime handling
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                    (src, typeOfSrc, context) -> new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                    (json, type, context) -> LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .setPrettyPrinting()
            .create();

    public static void saveTasks(List<Task> tasks) {
        try {
            File file = new File(FILE_PATH);
            Files.createDirectories(Paths.get(file.getParent()));
            if (!file.exists()) file.createNewFile();

            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(tasks, writer);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving tasks", e);
        }
    }

    public static List<Task> loadTasks() {
        List<Task> tasks = new ArrayList<>();
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                LOGGER.log(Level.WARNING, "No task file found. Creating new one.");
                saveTasks(tasks);
                return tasks;
            }

            try (FileReader reader = new FileReader(file)) {
                Type taskListType = new TypeToken<List<Task>>() {}.getType();
                tasks = gson.fromJson(reader, taskListType);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading tasks", e);
        }
        return tasks;
    }
}
