package ca.cmpt276.parentapp.model;

import java.time.LocalDate;

/**
 * This class handles creating a task
 * and managing its attributes
 */
public class Task {
    private String taskName;
    private String childTurn;
    private LocalDate dateOfTask;

    public Task(String taskName, String childTurn, LocalDate dateOfTask) {
        this.taskName = taskName;
        this.childTurn = childTurn;
        this.dateOfTask = dateOfTask;
    }

    public String getChildTurn() {
        return childTurn;
    }

    public void setChildTurn(String childTurn) {
        this.childTurn = childTurn;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

}
