package ca.cmpt276.parentapp.UI;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import ca.cmpt276.as3.parentapp.R;
import ca.cmpt276.as3.parentapp.databinding.ActivityConfigureChildrenBinding;
import ca.cmpt276.parentapp.model.DataHolder;
import ca.cmpt276.parentapp.model.TaskHistoryManager;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * The ConfigureChildrenActivity contains
 * the user-inputted children names. Names
 * can be added, edited and deleted from this
 * activity. The names are saved/loaded between application
 * uses.
 */
public class ConfigureChildrenActivity extends AppCompatActivity {

    private static final String NAME_PREF = "NamePrefs";
    private static final String NAMES_PREF = "NamesSizePref";
    ArrayList<String> childNames = new ArrayList<>();
    ArrayList<String> image_path = new ArrayList<>();
    ListView childrenList;
    EditText childName;
    TaskHistoryManager taskHistory;
    Button addChild, removeChild, editChild, launchPhotos;
    ArrayAdapter<String> childAdapter;
    String nameOfChild;
    int positionOfChild;
    CircleImageView circleImageView;
    private DataHolder childIdxData;

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
        circleImageView = findViewById(R.id.profile_pic);
        taskHistory = TaskHistoryManager.getInstance();

        childIdxData = DataHolder.getInstance();

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

    private void populateChildrenList() {
        childAdapter = new ListAdapter(ConfigureChildrenActivity.this, childNames);
        childrenList.setAdapter(childAdapter);
        childrenList.setOnItemClickListener((parent, view, position, id) -> {
            childName.setText(childNames.get(position));
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
        String json = gson.toJson(childNames);
        editor.putString(NAME_PREF, json);
        editor.apply();
    }

    private void loadChildrenData() {
        SharedPreferences sharedPreferences = getSharedPreferences(NAMES_PREF, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(NAME_PREF, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        childNames = gson.fromJson(json, type);

        if (childNames == null) {
            childNames = new ArrayList<>();
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
            taskHistory.editChild(childNames.get(positionOfChild),nameOfChild);
            childAdapter.remove(childNames.get(positionOfChild));
            childAdapter.insert(nameOfChild, positionOfChild);
            // refresh
            childAdapter.notifyDataSetChanged();
            taskHistory.saveTaskHistory(this);
        } else {
            Toast.makeText(ConfigureChildrenActivity.this, getString(R.string.non_empty_name), Toast.LENGTH_SHORT).show();
        }
    }

    private void removeChild() {
        positionOfChild = childrenList.getCheckedItemPosition();

        if (positionOfChild >= 0) {
            childAdapter.remove(childNames.get(positionOfChild));
            // refresh
            childAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
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

            TextView userName = convertView.findViewById(R.id.personName);
            launchPhotos = convertView.findViewById(R.id.addChildPicture);
            CircleImageView circleImageView = convertView.findViewById(R.id.profile_pic);

            image_path = getIntent().getStringArrayListExtra("IMAGE_PATH_LIST");

            if (image_path != null) {
                if (image_path.size() != 0 && childNames.size() != 0) {
                    try {
                        File f = new File(image_path.get(0), childNames.get(0) + ".jpg");
                        Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                        circleImageView.setImageBitmap(b);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }

            launchPhotos.setOnClickListener(v -> {
                Intent i = PhotosActivity.makeLaunchIntent(ConfigureChildrenActivity.this);
                childIdxData.setIdx(position);
                startActivity(i);
                finish();
            });

            userName.setText(childNames.get(position));

            return convertView;
        }
    }


}