package ca.cmpt276.parentapp.model;

import java.util.ArrayList;

/**
 * This class handles managing an arraylist of children
 */
public class ChildManager {
    private static ArrayList<Child> children = new ArrayList<>();
    private static ChildManager INSTANCE;


    private ChildManager() {
    }

    public static ChildManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChildManager();
        }
        return INSTANCE;
    }

}