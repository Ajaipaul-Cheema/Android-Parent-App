package ca.cmpt276.parentapp.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * The FlipResultManager class stores a collection of
 * Flip Results. It contains a List that contains the results,
 * and the class also includes methods to add to, get, and iterate
 * the list. History of flips are saved between app runs.
 */
public class FlipResultManager implements Iterable<FlipResult> {

    private static final String FLIP_RESULT_PREF = "Flip_Result_Pref";
    private static final String PREFS = "Prefs";
    private ArrayList<FlipResult> flipHistoryList = new ArrayList<>();

    // singleton
    private static FlipResultManager instance;

    private FlipResultManager() {
    }

    public static FlipResultManager getInstance() {
        if (instance == null) {
            instance = new FlipResultManager();
        }
        return instance;
    }

    public void add(FlipResult toss) {
        flipHistoryList.add(toss);
    }

    public ArrayList<FlipResult> getFlipHistoryList() {
        return flipHistoryList;
    }

    @Override
    public Iterator<FlipResult> iterator() {
        return flipHistoryList.iterator();
    }

    public void saveFlipHistory(Context context) {
        SharedPreferences flipPrefs = context.getSharedPreferences(FLIP_RESULT_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor flipEditor = flipPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(flipHistoryList);
        flipEditor.putString(PREFS, json);
        flipEditor.apply();
    }

    public void loadFlipHistory(Context context) {
        SharedPreferences loadFlipPrefs = context.getSharedPreferences(FLIP_RESULT_PREF, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = loadFlipPrefs.getString(PREFS, "");
        Type type = new TypeToken<ArrayList<FlipResult>>() {
        }.getType();
        flipHistoryList = gson.fromJson(json, type);

        if (flipHistoryList == null) {
            flipHistoryList = new ArrayList<>();
        }
    }


}
