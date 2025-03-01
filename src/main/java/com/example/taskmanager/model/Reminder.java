package com.example.taskmanager.model;

import java.time.LocalDateTime;

public class Reminder {
    private LocalDateTime dateTime;
    private String taskId;  // ✅ This stores the task title
    private String type;  // ✅ Stores "One day before", "One week before", etc.

    public Reminder(LocalDateTime dateTime, String taskId, String type) {
        this.dateTime = dateTime;
        this.taskId = taskId;
        this.type = type;
    }

    // ✅ Getter and Setter Methods
    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public String getTaskId() { return taskId; }  // ✅ Added this method
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
