package ca.cmpt276.parentapp.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import ca.cmpt276.as3.parentapp.R;

public class FlipResult {

    private String name;
    private LocalDateTime timeFlip;
    private int tossedResult;
    private int guessedCoin;


    public FlipResult(String name, LocalDateTime timeFlip, int tossedResult, int guessedCoin) {
        this.name = name;
        this.timeFlip = timeFlip;
        this.tossedResult = tossedResult;
        this.guessedCoin = guessedCoin;
    }

    public String getTimeString(){
        DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("HH:mm");
        String timeStr = timeFlip.format(formatTime);
        LocalDateTime date = timeFlip;
        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("MMM dd");
        String dateTimeCustom = date.format(formatDate) + " @ " + timeStr;
        return dateTimeCustom;
    }

    public int getIcon(){

        if (tossedResult == guessedCoin){
            return R.drawable.ic_baseline_check_24;
        }
        else{
            return R.drawable.ic_outline_cancel_24;
        }
    }

    public String getNameOfChild() {
        return name;
    }

    public String getFlippedResultString(){
        if (tossedResult == 1){
            return "Heads";
        }
        else{
            return "Tails";
        }
    }

    public String getChildChoiceString(){
        if (guessedCoin == 1){
            return "Heads";
        }
        else{
            return "Tails";
        }
    }

}
