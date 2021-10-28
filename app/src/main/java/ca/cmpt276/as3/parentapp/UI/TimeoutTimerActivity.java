package ca.cmpt276.as3.parentapp.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import ca.cmpt276.as3.parentapp.R;


import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import ca.cmpt276.as3.parentapp.databinding.ActivityTimeoutTimerBinding;

public class TimeoutTimerActivity extends AppCompatActivity {
    private static final long TIME_ON_LAUNCH = 300000;

    private TextView timerText;
    private Button startPauseButton;
    private Button resetButton;

    private CountDownTimer countDownTimer;

    private boolean isTimerRunning;

    private long timeLeftInTimer = TIME_ON_LAUNCH;

    private ActivityTimeoutTimerBinding binding;

    public static Intent makeLaunchIntent(Context c) {
        return new Intent(c, TimeoutTimerActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTimeoutTimerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        setUpTimer();
    }


    //  https://www.youtube.com/watch?v=zmjfAcnosS0 <- very helpful in this process
    private void setUpTimer() {

        timerText = findViewById(R.id.tv_timer);
        startPauseButton = findViewById(R.id.btn_start_and_pause_timer);
        resetButton = findViewById(R.id.btn_reset);
        resetButton.setVisibility(View.INVISIBLE);

        startPauseButton.setOnClickListener(v -> {
            if (isTimerRunning) {
                // Pause Button pressed
                countDownTimer.cancel();
                isTimerRunning = false;
                startPauseButton.setText("Start");
                resetButton.setVisibility(View.VISIBLE);
            } else {
                startCountdown();
            }
        });

        resetButton.setOnClickListener(v -> {
            timeLeftInTimer = TIME_ON_LAUNCH;
            changeTimerText();
            resetButton.setVisibility(View.INVISIBLE);
            startPauseButton.setVisibility(View.VISIBLE);
        });
        changeTimerText();
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(timeLeftInTimer, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftInTimer = l;
                changeTimerText();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                startPauseButton.setText("Start");
                startPauseButton.setVisibility(View.VISIBLE);
                resetButton.setVisibility(View.VISIBLE);
            }
        }.start();

        // Update button to pause because timer is currently running
        // change boolean to true as timer is now running, hide reset
        startPauseButton.setText("Pause");
        resetButton.setVisibility(View.INVISIBLE);
        isTimerRunning = true;
    }

    private void changeTimerText() {
        int mins = (int) (timeLeftInTimer / 1000) / 60;
        int secs = (int) (timeLeftInTimer / 1000) % 60;
        String updatedTextStr;
        updatedTextStr = "" + mins + ":";

        // case where seconds are <10, add a 0 so timer is 09, 08, not 9,8 etc.
        if (secs < 10) {
            updatedTextStr += "0";
        }
        updatedTextStr += secs;
        timerText.setText(updatedTextStr);

    }


}