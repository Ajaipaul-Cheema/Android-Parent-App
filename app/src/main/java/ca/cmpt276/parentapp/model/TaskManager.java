package ca.cmpt276.parentapp.model;

import java.util.ArrayList;

public class TaskManager {

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

    public void addTask(Task task){
        taskArrayList.add(task);
    }

    public void removeTask(int idx){
        taskArrayList.remove(idx);
    }

    public ArrayList<Task> getTaskList(){
        return taskArrayList;
    }

    public int getNextChild(String child, ArrayList<String> childrenList){

        int index = 0;

        for(String names: childrenList){
            if(names.equals(child)){
                if(index+1 >= childrenList.size()){
                    return 0;
                }
                else{
                    return index+1;
                }
            }
            index++;
        }
        return 0;
    }

    public int doesTaskExist(String taskInput){
        int idx = 0;

        for (Task task: taskArrayList){
            if (task.getTaskName().equals(taskInput)){
                return idx;
            }
        }
        return -1;
    }

}
