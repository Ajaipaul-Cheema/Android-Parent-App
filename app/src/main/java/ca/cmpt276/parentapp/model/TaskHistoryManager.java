package ca.cmpt276.parentapp.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

public class TaskHistoryManager {

    private static final String TASKS_HISTORY_NAMES_PREFS = "TasksHistoryNamesPrefs";
    private static final String TASKS_HISTORY_PREFS = "TasksHistoryPrefs";

    ArrayList<ChildTurnData> taskHistoryList = new ArrayList<>();

    // singleton
    private static TaskHistoryManager instance;

    private TaskHistoryManager() {
    }

    public static TaskHistoryManager getInstance() {
        if (instance == null) {
            instance = new TaskHistoryManager();
        }
        return instance;
    }

    public void addChild(ChildTurnData data) {
        taskHistoryList.add(data);
    }

    // https://stackoverflow.com/questions/8104692/how-to-avoid-java-util-concurrentmodificationexception-when-iterating-through-an
    public void remove(String task){
        for (Iterator<ChildTurnData> iterator = taskHistoryList.iterator(); iterator.hasNext();){
            ChildTurnData child = iterator.next();
            if (child.getTask().equals(task)){
                iterator.remove();
            }
        }
    }

    public void editTask(String oldTask, String newTask){
        for (ChildTurnData child : taskHistoryList){
            if (child.getTask().equals(oldTask)){
                child.setTask(newTask);
            }
        }
    }

    public ArrayList<ChildTurnData> getTaskList() {
        return taskHistoryList;
    }

    public void editChild(String oldName, String newName){
        for (ChildTurnData child : taskHistoryList){
            if(child.getChild().equals(oldName)){
                child.setChild(newName);
            }
        }
    }

    public ArrayList<ChildTurnData> getHistoryOfTask(String taskName){

        ArrayList<ChildTurnData> newTaskList = new ArrayList<>();

        for(ChildTurnData c : taskHistoryList){
            if(c.getTask().equals(taskName)){
                newTaskList.add(c);
            }
        }
        return newTaskList;
    }


    // save & load inspired by https://www.youtube.com/watch?v=jcliHGR3CHo&t=343s
    public void saveTaskHistory(Context context) {
        SharedPreferences flipPrefs = context.getSharedPreferences(TASKS_HISTORY_NAMES_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor flipEditor = flipPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(taskHistoryList);
        flipEditor.putString(TASKS_HISTORY_PREFS, json);
        flipEditor.apply();
    }

    public void loadTaskHistory(Context context) {
        SharedPreferences loadFlipPrefs = context.getSharedPreferences(TASKS_HISTORY_NAMES_PREFS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = loadFlipPrefs.getString(TASKS_HISTORY_PREFS, "");
        Type type = new TypeToken<ArrayList<ChildTurnData>>() {
        }.getType();
        taskHistoryList = gson.fromJson(json, type);

        if (taskHistoryList == null) {
            taskHistoryList = new ArrayList<>();
        }
    }

}
