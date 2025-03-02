package com.example.taskmanager.storage;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.Category;
import com.example.taskmanager.model.Priority;
import com.example.taskmanager.utils.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppState {
    private static final Logger LOGGER = Logger.getLogger(AppState.class.getName());

    // ✅ Use GsonBuilder to register LocalDateTime adapter
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setPrettyPrinting()
            .create();

    private static List<Task> tasks = new ArrayList<>();
    private static List<String> categories = new ArrayList<>();
    private static List<String> priorities = new ArrayList<>();

    private static final String TASKS_FILE = "src/main/resources/medialab/tasks.json";
    private static final String CATEGORIES_FILE = "src/main/resources/medialab/categories.json";
    private static final String PRIORITIES_FILE = "src/main/resources/medialab/priorities.json";

    private static final AppState instance = new AppState();

    private AppState() {
        loadData();
    }

    public static AppState getInstance() {
        return instance;
    }

    private void loadData() {
        tasks = loadFromFile(TASKS_FILE, new TypeToken<List<Task>>() {}.getType());
        categories = loadFromFile(CATEGORIES_FILE, new TypeToken<List<String>>() {}.getType());
        priorities = loadFromFile(PRIORITIES_FILE, new TypeToken<List<String>>() {}.getType());

        if (tasks == null) tasks = new ArrayList<>();
        if (categories == null) categories = new ArrayList<>();
        if (priorities == null) priorities = new ArrayList<>();

        //LOGGER.info("✅ Data successfully loaded into memory.");
    }

    public void saveData() {
        saveToFile(TASKS_FILE, tasks);
        saveToFile(CATEGORIES_FILE, categories);
        saveToFile(PRIORITIES_FILE, priorities);
        //LOGGER.info("✅ Data successfully saved to JSON files.");
    }

    private <T> List<T> loadFromFile(String filePath, Type type) {
        File file = new File(filePath);
        if (!file.exists()) return new ArrayList<>();

        try (Reader reader = new FileReader(file)) {
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading data from " + filePath, e);
            return new ArrayList<>();
        }
    }

    private <T> void saveToFile(String filePath, List<T> data) {
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving data to " + filePath, e);
        }
    }

    public List<Task> getTasks() { return tasks; }
    public List<String> getCategories() { return categories; }
    public List<String> getPriorities() { return priorities; }
}
