package com.example.taskmanager.model;

import java.time.LocalDateTime;

public class Reminder {
    private LocalDateTime dateTime;
    private String taskId;
    private String type;  // Stores "One day before", "One week before", etc.

    public Reminder(LocalDateTime dateTime, String taskId, String type) {
        this.dateTime = dateTime;
        this.taskId = taskId;
        this.type = type;
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

    public String getType() {  // ✅ Getter for reminder type
        return type;
    }

    public void setType(String type) {  // ✅ Setter for reminder type
        this.type = type;
    }
}
