package ca.cmpt276.as3.parentapp.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.as3.parentapp.R;
import ca.cmpt276.as3.parentapp.adapter.HistoryRecordsAdapter;
import ca.cmpt276.as3.parentapp.data.FlipRecord;
import ca.cmpt276.as3.parentapp.utils.DBOpenHelper;

public class HistoryRecords extends AppCompatActivity {

    private DBOpenHelper dbOpenHelper;
    private List<FlipRecord> list = new ArrayList<>();
    private ListView listView;
    private HistoryRecordsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_records);
        dbOpenHelper = new DBOpenHelper(this);
        list = dbOpenHelper.getAllRecord();

        adapter = new HistoryRecordsAdapter(this,list);
        listView = findViewById(R.id.listview);
        listView.setAdapter(adapter);
    }
}