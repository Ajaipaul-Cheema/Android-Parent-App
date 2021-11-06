package ca.cmpt276.as3.parentapp.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.navigation.ui.AppBarConfiguration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.as3.parentapp.databinding.ActivityHistoryFlipViewBinding;

import ca.cmpt276.as3.parentapp.R;

public class HistoryFlipView extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityHistoryFlipViewBinding binding;
    private List<FlipResult> myFlips = new ArrayList<>();
    private FlipResultManager resultManager;
    private static final String JSON_FILE_PATH = "./historyOfFlips.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        loadItems();
        resultManager = FlipResultManager.getInstance();
        binding = ActivityHistoryFlipViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        populateListView();
        saveItems();
    }

    @Override
    protected void onStart() {
        loadItems();
        populateListView();
        super.onStart();
    }

    private void saveItems() {
        Gson myGson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
                new TypeAdapter<LocalDateTime>() {
                    @Override
                    public void write(JsonWriter jsonWriter,
                                      LocalDateTime localDateTime) throws IOException {
                        jsonWriter.value(localDateTime.toString());
                    }
                    @Override
                    public LocalDateTime read(JsonReader jsonReader) throws IOException {
                        return LocalDateTime.parse(jsonReader.nextString());
                    }
                }).create();

        try {
            Writer writer = new FileWriter(JSON_FILE_PATH);
            myGson.toJson(myFlips, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadItems() {
        File file = new File(JSON_FILE_PATH);
        try {
            JsonElement fileElement = JsonParser.parseReader(new FileReader(file));
            JsonArray jsonArray = fileElement.getAsJsonArray();

            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject foodObject = jsonArray.get(i).getAsJsonObject();
                String foodDate = foodObject.get("timeFlip").getAsString();

                DateTimeFormatter formatted = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                LocalDateTime time = LocalDateTime.parse(foodDate, formatted);

                myFlips.add(new FlipResult(
                        foodObject.get("name").getAsString(),
                        time,
                        foodObject.get("tossedResult").getAsInt(),
                        foodObject.get("guessedCoin").getAsInt()
                ));
            }

        } catch (FileNotFoundException e) {
        }
    }



    public void getFlipList(){
        myFlips = resultManager.getFlipHistoryList();
    }

    public static Intent makeLaunchIntent(Context c) {
        return new Intent(c, HistoryFlipView.class);
    }


    private void populateListView() {
        getFlipList();
        ArrayAdapter<FlipResult> adapter = new MyListAdapter();
        ListView list = findViewById(R.id.flipsListView);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private class MyListAdapter extends ArrayAdapter<FlipResult>{
        public MyListAdapter(){
            super(HistoryFlipView.this, R.layout.item_view, myFlips);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View itemView = convertView;

            if (itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.item_view,parent,false);
            }

            FlipResult currentResult = myFlips.get(position);

            TextView childName = itemView.findViewById(R.id.tvChildName);
            TextView timeFlip = itemView.findViewById(R.id.tvChildResult);
            TextView flipResult = itemView.findViewById(R.id.tvFlipTime);

            childName.setText(currentResult.getNameOfChild() + " Picked");
            timeFlip.setText(currentResult.getTimeString());
            flipResult.setText(currentResult.getChoiceCoinString()+" - "+currentResult.isGuessedCorrectly());


            return itemView;
        }
    }
}
