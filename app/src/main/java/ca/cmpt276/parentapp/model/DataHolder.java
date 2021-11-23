package ca.cmpt276.parentapp.model;

/**
 * This class handles storing data of indexes
 * of the images that are saved to children
 */
public class DataHolder {

    private static final DataHolder instance = new DataHolder();
    private int idx = -1;

    public static DataHolder getInstance() {
        return instance;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }
}