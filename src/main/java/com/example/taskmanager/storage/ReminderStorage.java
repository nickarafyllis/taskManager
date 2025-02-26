package com.example.taskmanager.storage;

import com.example.taskmanager.model.Reminder;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

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

public class ReminderStorage {
    private static final String FILE_PATH = "src/main/resources/medialab/reminders.json";
    private static final Logger LOGGER = Logger.getLogger(ReminderStorage.class.getName());

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                    (src, typeOfSrc, context) -> new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                    (json, type, context) -> LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .setPrettyPrinting()
            .create();

    public static void saveReminders(List<Reminder> reminders) {
        try {
            File file = new File(FILE_PATH);
            Files.createDirectories(Paths.get(file.getParent()));
            if (!file.exists()) file.createNewFile();

            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(reminders, writer);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving reminders", e);
        }
    }

    public static List<Reminder> loadReminders() {
        List<Reminder> reminders = new ArrayList<>();
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                LOGGER.log(Level.WARNING, "No reminders file found. Creating a new one.");
                saveReminders(reminders);
                return reminders;
            }

            try (FileReader reader = new FileReader(file)) {
                Type reminderListType = new TypeToken<List<Reminder>>() {}.getType();
                reminders = gson.fromJson(reader, reminderListType);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading reminders", e);
        }
        return reminders;
    }
}
