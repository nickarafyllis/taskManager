package com.example.taskmanager.model;

import java.time.LocalDateTime;

public class Reminder {
    private String message;
    private LocalDateTime dateTime;
    private String taskId; // To associate with a task

    public Reminder(String message, LocalDateTime dateTime, String taskId) {
        this.message = message;
        this.dateTime = dateTime;
        this.taskId = taskId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
