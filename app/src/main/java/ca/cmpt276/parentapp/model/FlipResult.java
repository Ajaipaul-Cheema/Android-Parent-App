package ca.cmpt276.parentapp.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    public String getNameOfChild() {
        return name;
    }

    public String getChoiceCoinString(){
        if (tossedResult == 1){
            return "Heads";
        }
        else{
            return "Tails";
        }
    }


    public String isGuessedCorrectly() {
        if(tossedResult == guessedCoin){
            return "WON";
        }
        else{
            return "LOST";
        }

    }
}
