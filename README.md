# 📝 Task Manager

Task Manager is a JavaFX-based application designed to help users efficiently create, manage, and track tasks. The application provides features such as task categorization, priority management, reminders, and more.
It was coded as an course project for the undergraduate course "Multimedia Technology" at ECE NTUA. 

## 📌 Features

✅ **Task Management:** Add, edit, delete, and track tasks.
✅ **Reminders System:** Set automated reminders for tasks.
✅ **Categories & Priorities:** Organize tasks with customizable categories and priorities.
✅ **Search & Filter:** Quickly find tasks by title, category, or priority.
✅ **Delayed Task Notifications:** Get alerts when tasks become overdue.
✅ **Data Persistence:** Uses JSON files to store tasks, categories, and priorities.

---
<img width="1218" height="549" alt="image" src="https://github.com/user-attachments/assets/f7702cbd-0068-417d-822f-ec5adddf2802" />
---

## 📦 Installation

### Prerequisites
- Java **17+**
- JavaFX **17+**
- Maven

### Steps
1. Clone this repository:
   ```sh
   git clone https://github.com/nickarafyllis/taskManager.git
   cd taskManager
   ```
2. Build the project using Maven:
   ```sh
   mvn clean install
   ```
3. Run the application:
   ```sh
   mvn javafx:run
   ```

---

## 🎮 Usage

- **Add a Task:** Click the `Add Task` button, enter details, and save.
- **Edit a Task:** Select a task and click `Edit` to modify its details.
- **Delete a Task:** Select a task and click `Delete`.
- **Manage Categories/Priorities:** Navigate to `Manage Categories` or `Manage Priorities` to customize task attributes.
- **Set Reminders:** Inside the task editor, add reminders to get alerts.
- **Search & Filter:** Use the search bar to filter tasks based on title, category, or priority.

---

## 🛠️ Configuration

### JSON Storage Structure

The application persists data in JSON format:
- `tasks.json` → Stores all task details.
- `categories.json` → Stores custom categories.
- `priorities.json` → Stores custom priorities.

All files are located in the `src/main/resources/medialab/` directory.

---

## 👩‍💻 Contributing

🚀 Contributions are welcome! To contribute:
1. Fork the repo
2. Create a new branch (`git checkout -b feature-branch`)
3. Make your changes
4. Commit (`git commit -m "Added new feature"`)
5. Push (`git push origin feature-branch`)
6. Submit a pull request

---

## 📜 License

This project is licensed under the **MIT License**.

---

## 📞 Support

For issues or feature requests, please [open an issue](https://github.com/nickarafyllis/taskManager/issues).

---

**Made with ❤️ by Nick**

