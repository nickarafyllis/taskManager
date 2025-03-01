package com.example.taskmanager.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Task {
    private String title;
    private String description;
    private Category category;
    private Priority priority;
    private String deadline; // Stored as String in format "YYYY-MM-DD"
    private TaskStatus status; // Open, In Progress, Postponed, Completed, Delayed
    private List<Reminder> reminders;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Task(String title, String description, Category category, Priority priority, String deadline) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.deadline = deadline;
        this.status = TaskStatus.Open;
        this.reminders = new ArrayList<>();
    }

    public enum TaskStatus {
        Open,
        In_Progress,
        Postponed,
        Completed,
        Delayed
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public List<Reminder> getReminders() {
        if (reminders == null) {
            reminders = new ArrayList<>();
        }
        return reminders;
    }

    public void addReminder(Reminder reminder) { this.reminders.add(reminder); }
    public void removeReminder(Reminder reminder) { this.reminders.remove(reminder); }

    // Convert deadline to LocalDate
    public LocalDate getDeadlineAsLocalDate() {
        return (deadline != null && !deadline.isEmpty()) ? LocalDate.parse(deadline, FORMATTER) : null;
    }

    // Set deadline from LocalDate
    public void setDeadlineFromLocalDate(LocalDate date) {
        this.deadline = (date != null) ? date.format(FORMATTER) : null;
    }
}
