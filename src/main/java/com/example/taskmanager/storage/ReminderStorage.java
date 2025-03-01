package com.example.taskmanager.storage;

import com.example.taskmanager.model.Reminder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReminderStorage {

    private static final String FILE_PATH = "src/main/resources/medialab/reminders.json";
    private static final Gson gson = new Gson();

    public static List<Reminder> loadReminders() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();

        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Reminder>>() {}.getType();
            List<Reminder> reminders = gson.fromJson(reader, listType);
            return (reminders != null) ? reminders : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static List<Reminder> loadRemindersForTask(String taskTitle) {
        return loadReminders().stream()
                .filter(reminder -> reminder.getTaskId().equals(taskTitle))
                .collect(Collectors.toList());
    }

    public static void updateReminder(Reminder updatedReminder) {
        List<Reminder> reminders = loadReminders().stream()
                .map(reminder -> reminder.getTaskId().equals(updatedReminder.getTaskId()) &&
                        reminder.getType().equals(updatedReminder.getType()) ?
                        updatedReminder : reminder)
                .collect(Collectors.toList());
        saveReminders(reminders);
    }

    public static void deleteReminder(String taskTitle, String reminderType) {
        List<Reminder> reminders = loadReminders().stream()
                .filter(reminder -> !(reminder.getTaskId().equals(taskTitle) && reminder.getType().equals(reminderType)))
                .collect(Collectors.toList());

        saveReminders(reminders);
    }

    public static void deleteRemindersForTask(String taskTitle) {
        List<Reminder> remainingReminders = loadReminders().stream()
                .filter(reminder -> !reminder.getTaskId().equals(taskTitle))
                .collect(Collectors.toList());

        saveReminders(remainingReminders);
    }

    public static void saveReminder(Reminder newReminder) {
        List<Reminder> reminders = loadReminders();
        reminders.add(newReminder);
        saveReminders(reminders);
    }

    public static void saveReminders(List<Reminder> reminders) {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(reminders, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
