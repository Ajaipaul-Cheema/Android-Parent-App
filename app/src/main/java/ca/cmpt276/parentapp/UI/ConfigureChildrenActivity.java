package ca.cmpt276.parentapp.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ca.cmpt276.as3.parentapp.R;
import ca.cmpt276.as3.parentapp.databinding.ActivityConfigureChildrenBinding;


/**
 * The ConfigureChildrenActivity contains
 * the user-inputted children names. Names
 * can be added, editted and deleted from this
 * activity. The names are saved/loaded between application
 * uses.
 */
public class ConfigureChildrenActivity extends AppCompatActivity {

    ArrayList<String> childrenNames = new ArrayList<>();
    private static final String NAME_PREF = "NamePrefs";
    private static final String NAMES_PREF = "NamesSizePref";
    ListView childrenList;
    EditText childName;
    Button addChild, removeChild, editChild, launchPhotos;
    ArrayAdapter<String> childAdapter;
    String nameOfChild;
    int positionOfChild;

    public static Intent makeLaunchIntent(Context c) {
        return new Intent(c, ConfigureChildrenActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ca.cmpt276.as3.parentapp.databinding.ActivityConfigureChildrenBinding binding = ActivityConfigureChildrenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // https://www.geeksforgeeks.org/how-to-change-the-color-of-action-bar-in-an-android-app/
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor(getString(R.string.yellow_color)));
        actionBar.setBackgroundDrawable(colorDrawable);

        // load in children data
        loadChildrenData();

        // get id's of buttons, listview and edit text
        childrenList = findViewById(R.id.listChildren);
        childName = findViewById(R.id.editChildName);
        addChild = findViewById(R.id.btnAddChild);
        removeChild = findViewById(R.id.btnRemoveChild);
        editChild = findViewById(R.id.btnEditChild);

        // populate list of children
        populateChildrenList();

        handleAddChildButton();
        handleEditChildButton();
        handleRemoveChildButton();

        // save children data
        saveChildrenData();
    }

    public class ListAdapter extends ArrayAdapter<String> {

        public ListAdapter(Context context, ArrayList<String> userArrayList) {
            super(context, R.layout.list_children_view, userArrayList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_children_view, parent, false);
            }

            TextView name = convertView.findViewById(R.id.personName);
            launchPhotos = convertView.findViewById(R.id.addChildPicture);

            name.setText(childrenNames.get(position));

            return convertView;
        }
    }

    private void populateChildrenList() {
        childAdapter = new ListAdapter(ConfigureChildrenActivity.this, childrenNames);
        childrenList.setAdapter(childAdapter);
        childrenList.setOnItemClickListener((parent, view, position, id) -> {
            childName.setText(childrenNames.get(position));
            // sets cursor to the right of name when name is clicked upon
            if (childName.getText().length() > 0) {
                childName.setSelection(childName.getText().length());
            }
        });
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

    private void handleAddChildButton() {
        addChild.setOnClickListener(v -> {
            addChild();
            // clear edit text after adding name
            childName.getText().clear();
            saveChildrenData();
        });
    }

    private void handleEditChildButton() {
        editChild.setOnClickListener(v -> {
            editChild();
            // clear edit text after editing name
            childName.getText().clear();
            saveChildrenData();
        });
    }

    private void handleRemoveChildButton() {
        removeChild.setOnClickListener(v -> {
            removeChild();
            // clear edit text after removing name
            childName.getText().clear();
            saveChildrenData();
        });
    }

    private void addChild() {
        nameOfChild = childName.getText().toString();

        if (!nameOfChild.equals("")) {
            childAdapter.add(nameOfChild);
            // refresh
            childAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(ConfigureChildrenActivity.this, getString(R.string.non_empty_name), Toast.LENGTH_SHORT).show();
        }
    }

    private void editChild() {
        nameOfChild = childName.getText().toString();
        positionOfChild = childrenList.getCheckedItemPosition();

        if (!nameOfChild.equals("")) {
            childAdapter.remove(childrenNames.get(positionOfChild));
            childAdapter.insert(nameOfChild, positionOfChild);
            // refresh
            childAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(ConfigureChildrenActivity.this, getString(R.string.non_empty_name), Toast.LENGTH_SHORT).show();
        }
    }

    private void removeChild() {
        positionOfChild = childrenList.getCheckedItemPosition();

        if (positionOfChild >= 0) {
            childAdapter.remove(childrenNames.get(positionOfChild));
            // refresh
            childAdapter.notifyDataSetChanged();
        }
    }
}