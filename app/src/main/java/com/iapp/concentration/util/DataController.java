package com.iapp.concentration.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class DataController {

    private static final String FILE_NAME = "data";
    private static final String USER_NAME_KEY = "user_name";
    private static final String TASKS_KEY = "tasks";

    private static DataController dataController = null;
    private final List<Task> tasks = new ArrayList<>();
    private final SharedPreferences sharedPreferences;

    private DataController(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        loadTasks();
    }

    public static DataController getInstance(Context context) {
        if (dataController != null) {
            return dataController;
        }
        dataController = new DataController(context);
        return dataController;
    }

    public String getUserName() {
        return sharedPreferences.getString(USER_NAME_KEY, "");
    }

    public void saveUserName(String newUserName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_NAME_KEY, newUserName);
        editor.apply();
    }

    public void addNewTask(Task task) {
        tasks.add(task);
        saveTasks();
    }

    public List<Task> getCopyTasks() {
        List<Task> copy = new ArrayList<>();
        for (Task task : tasks) {
            copy.add(task.getCopy());
        }
        return copy;
    }

    public void updateTask(Task newState) {
        Task find = null;
        for (Task task : tasks) {
            if (task.id == newState.id) {
                find = task;
                break;
            }
        }

        if (find != null) {
            find.minute = newState.minute;
            find.done = newState.done;
            find.hour = newState.hour;
            find.name = newState.name;
            find.date = newState.date;
        }

        saveTasks();
    }

    public void removeTask(Task idTask) {
        Task find = null;
        for (Task task : tasks) {
            if (task.id == idTask.id) {
                find = task;
                break;
            }
        }

        if (find != null) {
            tasks.remove(find);
        }

        saveTasks();
    }

    private void saveTasks() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(tasks);
        editor.putString(TASKS_KEY, json);
        editor.apply();
    }

    private void loadTasks() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(TASKS_KEY,null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<Task>>(){}.getType();
            tasks.addAll(gson.fromJson(json,type));
        }
    }
}
