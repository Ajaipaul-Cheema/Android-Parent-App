package ca.cmpt276.parentapp.model;

import androidx.annotation.NonNull;

/**
 * This class handles a Child object and appending,
 * getting and setting names to the object
 */
public class Child {
    private String name;

    public Child(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @NonNull
    @Override
    public String toString() {
        return "Child's Name: " + name;
    }

}