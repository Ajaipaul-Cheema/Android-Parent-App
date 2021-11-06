package ca.cmpt276.as3.parentapp.UI;

import java.util.ArrayList;
import java.util.Iterator;

public class FlipResultManager implements Iterable<FlipResult> {

    private ArrayList<FlipResult> flipHistoryList = new ArrayList<>();

    //simpleton

    private static FlipResultManager instance;
    private FlipResultManager(){

    }
    public static FlipResultManager getInstance(){
        if(instance==null){
            instance = new FlipResultManager();
        }
        return instance;
    }


    public void add(FlipResult toss) {
        flipHistoryList.add(toss);
    }

    public void remove(int i) {
        flipHistoryList.remove(flipHistoryList.get(i));
    }

    public FlipResult getIndexToss(int i){
        return flipHistoryList.get(i);
    }

    public int getSize() {
        return flipHistoryList.size();
    }

    public ArrayList<FlipResult> getFlipHistoryList() {
        return flipHistoryList;
    }

    @Override
    public Iterator<FlipResult> iterator() {
        return flipHistoryList.iterator();
    }


}
