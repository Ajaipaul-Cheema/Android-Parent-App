package ca.cmpt276.as3.parentapp.UI;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import ca.cmpt276.as3.parentapp.R;
import ca.cmpt276.as3.parentapp.databinding.ActivityMainBinding;

import android.widget.Button;

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
        Button launchGame = findViewById(R.id.flipCoin);
        launchGame.setOnClickListener(v -> {
            Intent i = FlipCoinActivity.makeLaunchIntent(MainActivity.this);
            startActivity(i);
        });
    }

    private void launchTimeoutTimer() {
        Button launchGame = findViewById(R.id.timeoutTimer);
        launchGame.setOnClickListener(v -> {
            Intent i = TimeoutTimerActivity.makeLaunchIntent(MainActivity.this);
            startActivity(i);
        });
    }

    private void launchConfigureChildren() {
        Button launchGame = findViewById(R.id.configureChildren);
        launchGame.setOnClickListener(v -> {
            Intent i = ConfigureChildrenActivity.makeLaunchIntent(MainActivity.this);
            startActivity(i);
        });
    }



}