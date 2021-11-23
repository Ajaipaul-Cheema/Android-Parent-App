package ca.cmpt276.parentapp.UI;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import ca.cmpt276.as3.parentapp.R;
import ca.cmpt276.as3.parentapp.databinding.ActivityPhotosBinding;
import ca.cmpt276.parentapp.model.ChildManager;
import ca.cmpt276.parentapp.model.DataHolder;

/**
 * This class handles adding a picture from camera or gallery onto
 * an imageview in configure children activity
 */
public class PhotosActivity extends AppCompatActivity {

    private static final String IMAGE_NAME_PREF = "image_name_pref";
    private static final String IMAGE_PREFS = "image_prefs";
    private ArrayList<String> permissionsToRequest;
    private final ArrayList<String> permissionsRejected = new ArrayList<>();
    private final ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 107;
    ArrayList<String> children_names = new ArrayList<>();
    private static final String NAME_PREF = "NamePrefs";
    private static final String NAMES_PREF = "NamesSizePref";
    ArrayList<String> image_path = new ArrayList<>();
    Bitmap imageBitmap;
    Bitmap bitmap;
    private DataHolder data;
    private ImageView image;
    private ActivityResultLauncher<Intent> camera_launcher;
    private ActivityResultLauncher<String> gallery_launcher;
    ChildManager childManager;

    public static Intent makeLaunchIntent(Context c) {
        return new Intent(c, PhotosActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ca.cmpt276.as3.parentapp.databinding.ActivityPhotosBinding binding = ActivityPhotosBinding.inflate(getLayoutInflater());
        setContentView(R.layout.content_photos);
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        image = findViewById(R.id.image);
        permissions.add(CAMERA);
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);
        childManager = ChildManager.getInstance();

        // https://www.geeksforgeeks.org/how-to-change-the-color-of-action-bar-in-an-android-app/
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor(getString(R.string.yellow_color)));
        actionBar.setBackgroundDrawable(colorDrawable);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[0]), ALL_PERMISSIONS_RESULT);
        }

        data = DataHolder.getInstance();

        loadImagePaths();
        loadChildrenData();

        launchCamera();
        launchGallery();

        launchPictureButton();
        launchGalleryButton();

        saveImagePaths();
        saveChildrenData();
        launchConfigureChildren();
    }

    private void saveImagePaths() {
        SharedPreferences sharedPreferences = getSharedPreferences(IMAGE_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(image_path);
        editor.putString(IMAGE_NAME_PREF, json);
        editor.apply();
    }

    private void loadImagePaths() {
        SharedPreferences sharedPreferences = getSharedPreferences(IMAGE_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(IMAGE_NAME_PREF, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        image_path = gson.fromJson(json, type);

        if (image_path == null) {
            image_path = new ArrayList<>();
        }
    }

    // permission related content is inspired by https://www.journaldev.com/23219/android-capture-image-camera-gallery-using-fileprovider
    private ArrayList<String> findUnAskedPermissions(ArrayList<String> permissions) {
        ArrayList<String> result = new ArrayList<>();
        for (String permission : permissions) {
            if (hasPermission(permission)) {
                result.add(permission);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeMorePhotos()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED);
            }
        }
        return false;
    }

    private void showMessage(String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean canMakeMorePhotos() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ALL_PERMISSIONS_RESULT) {
            for (String allPermissions : permissionsToRequest) {
                if (hasPermission(allPermissions)) {
                    permissionsRejected.add(allPermissions);
                }
            }

            if (permissionsRejected.size() > 0) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                        showMessage("These permissions are mandatory for the application. Please allow access.",
                                (dialog, which) -> {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(permissionsRejected.toArray(new String[0]), ALL_PERMISSIONS_RESULT);
                                    }
                                });
                    }
                }
            }
        }
    }

    // save & load inspired by https://www.youtube.com/watch?v=jcliHGR3CHo&t=343s
    private void saveChildrenData() {
        SharedPreferences sharedPreferences = getSharedPreferences(NAMES_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(children_names);
        editor.putString(NAME_PREF, json);
        editor.apply();
    }

    private void loadChildrenData() {
        SharedPreferences sharedPreferences = getSharedPreferences(NAMES_PREF, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(NAME_PREF, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        children_names = gson.fromJson(json, type);

        if (children_names == null) {
            children_names = new ArrayList<>();
        }
    }

    private void launchCamera() {
        int idx = data.getIdx();
        camera_launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Bundle extras = data.getExtras();
                        imageBitmap = (Bitmap) extras.get("data");
                        image_path.add(saveToInternalStorage(imageBitmap, children_names.get(idx)));
                        loadImageFromStorage(image_path.get(0), children_names.get(idx));
                    }
                }
        );
    }

    private void launchGallery() {
        gallery_launcher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    image.setImageBitmap(bitmap);
                    image_path.add(saveToInternalStorage(bitmap, children_names.get(data.getIdx())));
                    loadImageFromStorage(image_path.get(0), children_names.get(data.getIdx()));
                });
    }

    private void launchPictureButton() {
        Button launchPictureButton = findViewById(R.id.camera_button);
        launchPictureButton.setOnClickListener(view -> {
            Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            camera_launcher.launch(pictureIntent);
        });
    }

    private void launchGalleryButton() {
        Button launchGalleryButton = findViewById(R.id.gallery_button);
        launchGalleryButton.setOnClickListener(v -> gallery_launcher.launch("image/*"));
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String filename) {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getDir("children_images", Context.MODE_PRIVATE);
        File path = new File(directory, filename + ".jpg");

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(path);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void loadImageFromStorage(String path, String filename) {

        try {
            File file = new File(path, filename + ".jpg");
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            image.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void launchConfigureChildren() {
        Button launchConfigureChildren = findViewById(R.id.configActivity);
        launchConfigureChildren.setOnClickListener(view -> {
            Intent intent = new Intent(PhotosActivity.this, ConfigureChildrenActivity.class);
            intent.putExtra("IMAGE_PATH_LIST", image_path);
            startActivity(intent);
            finish();
        });
    }
}