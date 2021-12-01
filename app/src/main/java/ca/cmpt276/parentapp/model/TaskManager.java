package ca.cmpt276.parentapp.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * This class handles managing multiple tasks
 * adding, editing and deleting tasks
 * and checking next child and saving & loading tasks
 */
public class TaskManager {

    private static final String TASKS_NAMES_PREFS = "TasksNamesPrefs";
    private static final String TASKS_PREFS = "TasksPrefs";

    ArrayList<Task> taskArrayList = new ArrayList<>();

    // singleton
    private static TaskManager instance;

    private TaskManager() {
    }

    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    public void addTask(Task task) {
        taskArrayList.add(task);
    }

    public void removeTask(int idx) {
        taskArrayList.remove(idx);
    }

    public void editTask(int idx, String newTask) {
        taskArrayList.get(idx).setTaskName(newTask);
    }

    public ArrayList<Task> getTaskList() {
        return taskArrayList;
    }

    public int getNextChild(String child, ArrayList<String> childrenList) {

        int index = 0;

        for (String names : childrenList) {
            if (names.equals(child)) {
                if (index + 1 >= childrenList.size()) {
                    return 0;
                } else {
                    return index + 1;
                }
            }
            index++;
        }
        return 0;
    }

    public boolean doesChildExist(String childName, ArrayList<String> childList) {

        if (!childName.equals("")) {
            for (String child : childList) {
                if (child.equals(childName)) {
                    return true;
                }
            }
        }
        return false;
    }

    // save & load inspired by https://www.youtube.com/watch?v=jcliHGR3CHo&t=343s
    public void saveTaskHistory(Context context) {
        SharedPreferences flipPrefs = context.getSharedPreferences(TASKS_NAMES_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor flipEditor = flipPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(taskArrayList);
        flipEditor.putString(TASKS_PREFS, json);
        flipEditor.apply();
    }

    public void loadTaskHistory(Context context) {
        SharedPreferences loadFlipPrefs = context.getSharedPreferences(TASKS_NAMES_PREFS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = loadFlipPrefs.getString(TASKS_PREFS, "");
        Type type = new TypeToken<ArrayList<Task>>() {
        }.getType();
        taskArrayList = gson.fromJson(json, type);

        if (taskArrayList == null) {
            taskArrayList = new ArrayList<>();
        }
    }

    public void ifEmptyFixChild(ArrayList<String> childrenList, TaskHistoryManager historyManager) {
        if (childrenList.size() <= 0) {
            for (Task t : taskArrayList) {
                t.setChildTurn("No child available");
            }
        } else {
            for (Task t : taskArrayList) {
                    if (t.getChildTurn().equals("No child available")) {
                        t.setChildTurn(childrenList.get(0));
                        historyManager.addChild(new ChildTurnData(t.getChildTurn(),t.getTaskName(), LocalDate.now()));
                    }
                    else if(!doesChildExist(t.getChildTurn(), childrenList)){
                        t.setChildTurn(childrenList.get(0));
                    }

            }
        }
    }
}
