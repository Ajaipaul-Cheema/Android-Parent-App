package ca.cmpt276.parentapp.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import java.util.ArrayList;
import ca.cmpt276.as3.parentapp.R;
import ca.cmpt276.as3.parentapp.databinding.ActivityTaskHistoryBinding;
import ca.cmpt276.parentapp.model.ChildTurnData;
import ca.cmpt276.parentapp.model.TaskHistoryManager;
import ca.cmpt276.parentapp.model.TaskManager;

public class TaskHistoryActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityTaskHistoryBinding binding;

    private int indexOfTask;
    private String nameOfTask;
    private ListView historyListView;
    TaskHistoryManager historyManager;
    TaskManager taskManager;
    TextView title;

    ArrayAdapter<ChildTurnData> adapter;

    ArrayList<ChildTurnData> historyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTaskHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        historyListView = findViewById(R.id.historyTasksList);
        historyManager = TaskHistoryManager.getInstance();
        taskManager = TaskManager.getInstance();

        extractIntentData();
        title = findViewById(R.id.tvTaskHistoryName);
        title.setText(nameOfTask);

        populateListView();
    }

    public static Intent makeLaunchIntent(Context c, int index) {
        Intent intent = new Intent(c, TaskHistoryActivity.class);
        intent.putExtra("Index",index);
        return intent;
    }

    private void extractIntentData(){
        Intent intent = getIntent();
        indexOfTask = intent.getIntExtra("Index",0);
        nameOfTask = taskManager.getTaskList().get(indexOfTask).getTaskName();
    }

    private void populateListView() {
        historyList = historyManager.getHistoryOfTask(nameOfTask);
        adapter = new  TaskHistoryActivity.MyListAdapter();
        historyListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private class MyListAdapter extends ArrayAdapter<ChildTurnData> {
        public MyListAdapter() {
            super(TaskHistoryActivity.this, R.layout.task_history_view, historyList);
        }

        @SuppressLint("SetTextI18n")
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View itemView = convertView;

            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.task_history_view, parent, false);
            }

            ChildTurnData turnData = historyList.get(position);

            TextView childName = itemView.findViewById(R.id.tvChildNameHistoryTask);

            TextView date = itemView.findViewById(R.id.tvTaskCompletionDate);

            childName.setText("" +turnData.getChild());

            date.setText("" + turnData.getDate().toString());

            return itemView;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }
}