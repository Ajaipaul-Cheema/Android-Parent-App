package ca.cmpt276.parentapp.model;

import java.time.LocalDate;

public class ChildTurnData {

    private String name;
    private String task;
    private LocalDate date;

    public ChildTurnData(String name, String task, LocalDate date) {
        this.name = name;
        this.task = task;
        this.date = date;
    }

    public String getChild() {
        return name;
    }

    public void setChild(String name) {
        this.name = name;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ChildTurnData{" +
                "child=" + name +
                ", task='" + task + '\'' +
                ", date=" + date +
                '}';
    }
}
