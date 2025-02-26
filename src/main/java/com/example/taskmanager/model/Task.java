package com.example.taskmanager.model;

import java.util.ArrayList;
import java.util.List;

public class Task {
    private String title;
    private String description;
    private Category category;
    private Priority priority;
    private String deadline; // Format: YYYY-MM-DD
    private TaskStatus status; // Open, In Progress, Postponed, Completed, Delayed
    private List<Reminder> reminders;

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
        In_progress,
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
    public void setStatus(TaskStatus status) {
        this.status = status; // Assigning the enum value correctly
    }

    public List<Reminder> getReminders() { return reminders; }
    public void addReminder(Reminder reminder) { this.reminders.add(reminder); }
    public void removeReminder(Reminder reminder) { this.reminders.remove(reminder); }
}


