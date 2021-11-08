package ca.cmpt276.parentapp.UI;

import android.content.Intent;
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

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        launchFlipCoin();
        launchTimeoutTimer();
        launchConfigureChildren();
    }

    private void launchFlipCoin() {
        Button launchHelp = findViewById(R.id.flipCoin);
        launchHelp.setOnClickListener(v -> {
            Intent i = FlipCoinActivity.makeLaunchIntent(MainActivity.this);
            startActivity(i);
        });
    }

    private void launchTimeoutTimer() {
        Button launchHelp = findViewById(R.id.timeoutTimer);
        launchHelp.setOnClickListener(v -> {
            Intent i = TimeoutTimerActivity.makeLaunchIntent(MainActivity.this);
            startActivity(i);
        });
    }

    private void launchConfigureChildren() {
        Button launchHelp = findViewById(R.id.configureChildren);
        launchHelp.setOnClickListener(v -> {
            Intent i = ConfigureChildrenActivity.makeLaunchIntent(MainActivity.this);
            startActivity(i);

        });
    }

}