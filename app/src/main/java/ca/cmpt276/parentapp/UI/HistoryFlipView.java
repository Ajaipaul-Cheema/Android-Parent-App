package ca.cmpt276.parentapp.UI;

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

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.as3.parentapp.databinding.ActivityHistoryFlipViewBinding;

import ca.cmpt276.as3.parentapp.R;
import ca.cmpt276.parentapp.model.FlipResult;
import ca.cmpt276.parentapp.model.FlipResultManager;

public class HistoryFlipView extends AppCompatActivity {

    private static final String INDEX_PREF = "index_pref";
    private static final String CHILDREN_INDEX_PREF = "children_index_pref";
    private ActivityHistoryFlipViewBinding binding;
    private List<FlipResult> myFlips = new ArrayList<>();
    private FlipResult currentResult;
    private FlipResultManager resultManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        resultManager = FlipResultManager.getInstance();
        binding = ActivityHistoryFlipViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        populateListView();
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

            currentResult = resultManager.getResult().get(position);

            TextView childName = itemView.findViewById(R.id.tvChildName);
            TextView timeFlip = itemView.findViewById(R.id.tvChildResult);
            TextView flipResult = itemView.findViewById(R.id.tvFlipTime);

            childName.setText(currentResult.getNameOfChild() + " Picked");
            timeFlip.setText(currentResult.getTimeString());
            flipResult.setText(currentResult.getChoiceCoinString()+" - "+ currentResult.isGuessedCorrectly());


            return itemView;
        }
    }
}
