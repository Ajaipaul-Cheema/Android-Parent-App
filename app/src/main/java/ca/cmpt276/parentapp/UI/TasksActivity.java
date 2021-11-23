package ca.cmpt276.parentapp.UI;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ca.cmpt276.as3.parentapp.R;
import ca.cmpt276.as3.parentapp.databinding.ActivityTasksBinding;

import ca.cmpt276.parentapp.model.Task;
import ca.cmpt276.parentapp.model.TaskManager;

public class TasksActivity extends AppCompatActivity {


    ArrayList<String> childrenNames = new ArrayList<>();
    private static final String NAME_PREF = "NamePrefs";
    private static final String NAMES_PREF = "NamesSizePref";
    private Button addTaskButton;
    private EditText taskName;
    private ListView tasksListView;
    private String nameOfTask;
    private String currChildName;
    private int nextChildIdx;
    private ImageButton deleteTask;
    private ImageButton editTask;
    ArrayAdapter<Task> adapter;

    TaskManager taskManager;

    ArrayList<Task> taskList;


    public static Intent makeLaunchIntent(Context c) {
        return new Intent(c, TasksActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ca.cmpt276.as3.parentapp.databinding.ActivityTasksBinding binding = ActivityTasksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // https://www.geeksforgeeks.org/how-to-change-the-color-of-action-bar-in-an-android-app/
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor(getString(R.string.yellow_brown_color)));
        actionBar.setBackgroundDrawable(colorDrawable);


        loadChildrenData();


        tasksListView = findViewById(R.id.listTasks);
        taskName = findViewById(R.id.editTaskName);
        addTaskButton = findViewById(R.id.btnAddTask);


        taskManager = TaskManager.getInstance();
        taskManager.loadTaskHistory(this);

        // populate list of tasks
        populateListView();


        handleAddTaskButton();


        saveChildrenData();
        taskManager.saveTaskHistory(this);
    }


    private void handleAddTaskButton() {
        addTaskButton.setOnClickListener(v -> {
            addTask();
            // clear edit text after adding name
            taskName.getText().clear();
        });
    }


    private void addTask() {
        nameOfTask = taskName.getText().toString();

        if (!nameOfTask.equals("")) {
            if (childrenNames.size() <= 0) {
                Task newTask = new Task(nameOfTask, getString(R.string.noChildString));
                taskManager.addTask(newTask);
            } else {
                Task newTask = new Task(nameOfTask, childrenNames.get(0));
                taskManager.addTask(newTask);
            }

            populateListView();
        } else {
            Toast.makeText(this, getString(R.string.non_empty_name), Toast.LENGTH_SHORT).show();
        }
    }


    // save & load inspired by https://www.youtube.com/watch?v=jcliHGR3CHo&t=343s
    private void saveChildrenData() {
        SharedPreferences sharedPreferences = getSharedPreferences(NAMES_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(childrenNames);
        editor.putString(NAME_PREF, json);
        editor.apply();
    }

    private void loadChildrenData() {
        SharedPreferences sharedPreferences = getSharedPreferences(NAMES_PREF, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(NAME_PREF, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        childrenNames = gson.fromJson(json, type);

        if (childrenNames == null) {
            childrenNames = new ArrayList<>();
        }
    }


    // https://developer.android.com/guide/topics/ui/dialogs
    private void taskFinshedPopup(int pos) {
        currChildName = taskList.get(pos).getChildTurn();
        nextChildIdx = taskManager.getNextChild(currChildName, childrenNames);

        if (nextChildIdx >= childrenNames.size()) {
            nextChildIdx = 0;
        }

        AlertDialog confirmPopup = new AlertDialog.Builder(TasksActivity.this)
                .setTitle(childrenNames.get(nextChildIdx) + getString(R.string.NextTurnString))
                .setMessage(getString(R.string.ConfirmationStringEndTurn) + currChildName +
                        getString(R.string.WhichTaskString) + taskList.get(pos).getTaskName() +
                        getString(R.string.ChildNameString) + childrenNames.get(nextChildIdx) + getString(R.string.NextChildString))
                .setIcon(R.drawable.happychild)
                .setPositiveButton(R.string.StringConfirmButton, null)
                .setNegativeButton(R.string.StringCancelButton, null).show();
        Button confirmButton = confirmPopup.getButton(AlertDialog.BUTTON_POSITIVE);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskList.get(pos).setChildTurn(childrenNames.get(nextChildIdx));
                adapter.notifyDataSetChanged();
                taskManager.saveTaskHistory(TasksActivity.this);
                confirmPopup.cancel();
            }
        });
    }

    private void deleteTask(int position) {
        deleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskManager.removeTask(position);
                populateListView();
            }
        });
    }

    private void editTask(int position) {

        editTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameOfTask = taskName.getText().toString();
                if (!nameOfTask.equals("")) {
                    taskManager.editTask(position, nameOfTask);
                    populateListView();
                } else {
                    Toast.makeText(TasksActivity.this, R.string.editTaskHelpToast, Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        loadChildrenData();
        taskManager.loadTaskHistory(this);
        taskManager.ifEmptyFixChild(childrenNames);
        populateListView();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        taskManager.saveTaskHistory(this);
    }

    private void populateListView() {
        taskList = taskManager.getTaskList();
        adapter = new TasksActivity.MyListAdapter();
        tasksListView.setAdapter(adapter);
        tasksListView.setOnItemClickListener((parent, view, position, id) -> {
            taskName.setText(taskList.get(position).getTaskName());
            // sets cursor to the right of name when name is clicked upon
            if (taskName.getText().length() > 0) {
                if (childrenNames.size() > 0) {
                    taskName.setSelection(taskName.getText().length());
                    taskFinshedPopup(position);
                } else {
                    taskName.setSelection(taskName.getText().length());
                }
            }
        });
        adapter.notifyDataSetChanged();
    }

    private class MyListAdapter extends ArrayAdapter<Task> {
        public MyListAdapter() {
            super(TasksActivity.this, R.layout.task_view, taskList);
        }

        @SuppressLint("SetTextI18n")
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View itemView = convertView;

            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.task_view, parent, false);
            }


            Task task = taskList.get(position);

            TextView taskName = itemView.findViewById(R.id.tvTaskName);
            TextView childName = itemView.findViewById(R.id.tvChildNameTask);
            deleteTask = itemView.findViewById(R.id.btnDeleteTask);
            editTask = itemView.findViewById(R.id.btnEditTask);


            deleteTask.setFocusable(false);
            editTask.setFocusable(false);

            deleteTask.setFocusableInTouchMode(false);
            editTask.setFocusableInTouchMode(false);

            deleteTask(position);
            editTask(position);


            taskName.setText("" + task.getTaskName());
            childName.setText("" + task.getChildTurn());

            return itemView;
        }
    }
}
