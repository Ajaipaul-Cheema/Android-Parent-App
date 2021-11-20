package ca.cmpt276.parentapp.UI;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import ca.cmpt276.as3.parentapp.R;
import ca.cmpt276.as3.parentapp.databinding.ActivityMainBinding;

/**
 * MainActivity class is the 'hub' of the application,
 * the class contains methods which allows user access
 * to other methods.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ca.cmpt276.as3.parentapp.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor(getString(R.string.yellow_brown_color)));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        launchFlipCoin();
        launchTimeoutTimer();
        launchConfigureChildren();
        launchConfigureTasks();
        launchHelpMenu();
    }

    private void launchFlipCoin() {
        Button launchFlipCoin = findViewById(R.id.flipCoin);
        launchFlipCoin.setOnClickListener(v -> {
            Intent i = FlipCoinActivity.makeLaunchIntent(MainActivity.this);
            startActivity(i);
        });
    }

    private void launchTimeoutTimer() {
        Button launchTimer = findViewById(R.id.timeoutTimer);
        launchTimer.setOnClickListener(v -> {
            Intent i = TimeoutTimerActivity.makeLaunchIntent(MainActivity.this);
            startActivity(i);
        });
    }

    private void launchConfigureChildren() {
        Button launchConfigChildren = findViewById(R.id.configureChildren);
        launchConfigChildren.setOnClickListener(v -> {
            Intent i = ConfigureChildrenActivity.makeLaunchIntent(MainActivity.this);
            startActivity(i);

        });
    }

    private void launchConfigureTasks() {
        Button launchConfigTasks = findViewById(R.id.configureTasks);
        launchConfigTasks.setOnClickListener(v -> {
            Intent i = TasksActivity.makeLaunchIntent(MainActivity.this);
            startActivity(i);

        });
    }

    private void launchHelpMenu() {
        Button help = findViewById(R.id.help);
        help.setOnClickListener(view ->  {
            Intent i = new Intent(MainActivity.this, HelpMenu.class);
            startActivity(i);
        });
    }
}