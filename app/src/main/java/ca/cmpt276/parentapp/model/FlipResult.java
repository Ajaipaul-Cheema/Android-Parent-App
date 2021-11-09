package ca.cmpt276.parentapp.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import ca.cmpt276.as3.parentapp.R;

/**
 * The FlipResult class holds information about a
 * child's flip of the coin. Data such as child name,
 * child guess, time of flip and result of flip are included
 * in the class. Class also contains methods to get
 * this information
 */
public class FlipResult {

    private final String name;
    private final LocalDateTime timeFlip;
    private final int tossedResult;
    private final int guessedCoin;


    public FlipResult(String name, LocalDateTime timeFlip, int tossedResult, int guessedCoin) {
        this.name = name;
        this.timeFlip = timeFlip;
        this.tossedResult = tossedResult;
        this.guessedCoin = guessedCoin;
    }

    public String getTimeString() {
        DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("HH:mm");
        String timeStr = timeFlip.format(formatTime);
        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("MMM dd");
        return timeFlip.format(formatDate) + " @ " + timeStr;
    }

    public int getIcon() {

        if (tossedResult == guessedCoin) {
            return R.drawable.ic_baseline_check_24;
        } else {
            return R.drawable.ic_outline_cancel_24;
        }
    }

    public String getNameOfChild() {
        return name;
    }

    public String getFlippedResultString() {
        if (tossedResult == 1) {
            return "Heads";
        } else {
            return "Tails";
        }
    }

    public String getChildChoiceString() {
        if (guessedCoin == 1) {
            return "Heads";
        } else {
            return "Tails";
        }
    }

}
