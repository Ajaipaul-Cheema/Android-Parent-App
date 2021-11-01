package ca.cmpt276.as3.parentapp.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import ca.cmpt276.as3.parentapp.R;
import ca.cmpt276.as3.parentapp.databinding.ActivityTimeoutTimerBinding;

public class TimeoutTimerActivity extends AppCompatActivity {

    private EditText customTime;
    private ImageButton useCustomTime;
    private TextView timerText;
    private Button startPauseButton;
    private Button resetButton;
    private Spinner dropDownMenu;

    private static CountDownTimer countDownTimer;

    private boolean isTimerRunning;
    private long startTime;
    private long endTime;
    private long timeLeftInTimer;

    private ActivityTimeoutTimerBinding binding;

    public static Intent makeLaunchIntent(Context c) {
        return new Intent(c, TimeoutTimerActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTimeoutTimerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        customTime = findViewById(R.id.editCustomText);
        useCustomTime = findViewById(R.id.useCustomTime);
        timerText = findViewById(R.id.tv_timer);
        startPauseButton = findViewById(R.id.btn_start_and_pause_timer);
        resetButton = findViewById(R.id.btn_reset);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        handleCustomTimeCheckButton();
        setUpDropDownList();
        setUpTimer();
    }

    private void handleCustomTimeCheckButton() {
        useCustomTime.setOnClickListener(v -> {
            dropDownMenu.setSelection(0);
            String inputTime = customTime.getText().toString();
            if (inputTime.isEmpty()) {
                Toast.makeText(TimeoutTimerActivity.this, "Custom time must be non-empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            long milliSecsInput = Long.parseLong(inputTime) * 60000;
            if (milliSecsInput == 0) {
                Toast.makeText(TimeoutTimerActivity.this, "Enter a positive number of mins!", Toast.LENGTH_SHORT).show();
                return;
            }
            setTime(milliSecsInput);
            customTime.setText("");
        });
    }

    private void setTime(long milliSecs) {
        startTime = milliSecs;
        resetCountdown();
    }

    //  https://stackoverflow.com/questions/12108893/set-onclicklistener-for-spinner-item
    private void setUpDropDownList() {
        dropDownMenu = findViewById(R.id.spinDropDownChoices);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.Timer_Choices,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropDownMenu.setAdapter(adapter);

        dropDownMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedTime = adapterView.getItemAtPosition(i).toString();

                switch (selectedTime) {
                    case "10 Minutes":
                        startTime = 600000;
                        if (isTimerRunning) {
                            pauseCountdown();
                        }
                        resetCountdown();
                        break;
                    case "5 Minutes":
                        startTime = 300000;
                        if (isTimerRunning) {
                            pauseCountdown();
                        }
                        resetCountdown();
                        break;
                    case "3 Minutes":
                        startTime = 180000;
                        if (isTimerRunning) {
                            pauseCountdown();
                        }
                        resetCountdown();
                        break;
                    case "2 Minutes":
                        startTime = 120000;
                        if (isTimerRunning) {
                            pauseCountdown();
                        }
                        resetCountdown();
                        break;
                    case "1 Minute":
                        startTime = 60000;
                        if (isTimerRunning) {
                            pauseCountdown();
                        }
                        resetCountdown();
                        break;

                    default:
                        assert true;

                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //  https://www.youtube.com/watch?v=zmjfAcnosS0 <- very helpful in this process
    private void setUpTimer() {
        resetButton.setVisibility(View.INVISIBLE);

        startPauseButton.setOnClickListener(v -> {
            dropDownMenu.setSelection(0);
            if (isTimerRunning) {
                customTime.setVisibility(View.VISIBLE);
                useCustomTime.setVisibility(View.VISIBLE);
                // Pause Button pressed
                pauseCountdown();
            } else {
                customTime.setVisibility(View.VISIBLE);
                useCustomTime.setVisibility(View.VISIBLE);
                if(timeLeftInTimer <= 0) {
                    Toast.makeText(this,"Choose a timer option to begin with.", Toast.LENGTH_SHORT).show();
                } else {
                    startCountdown();
                }

            }
        });

        resetButton.setOnClickListener(v -> resetCountdown());
        changeTimerText();
    }

    private void pauseCountdown() {
        countDownTimer.cancel();
        isTimerRunning = false;
        startPauseButton.setText("Start");
        resetButton.setVisibility(View.VISIBLE);
        customTime.setVisibility(View.VISIBLE);
        useCustomTime.setVisibility(View.VISIBLE);
    }

    private void resetCountdown() {
        timeLeftInTimer = startTime;
        changeTimerText();
        resetButton.setVisibility(View.INVISIBLE);
        startPauseButton.setVisibility(View.VISIBLE);
    }

    private void startCountdown() {
        endTime = System.currentTimeMillis() + timeLeftInTimer;
        countDownTimer = new CountDownTimer(timeLeftInTimer, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftInTimer = l;
                changeTimerText();
            }

            @Override
            public void onFinish() {
                if(isTimerRunning) {
                    Toast.makeText(TimeoutTimerActivity.this, "DONE", Toast.LENGTH_SHORT).show();
                    isTimerRunning = false;
                    startPauseButton.setText("Start");
                    startPauseButton.setVisibility(View.VISIBLE);
                    resetButton.setVisibility(View.VISIBLE);
                    customTime.setVisibility(View.VISIBLE);
                    useCustomTime.setVisibility(View.VISIBLE);
                }
            }
        }.start();

        // Update button to pause because timer is currently running
        // change boolean to true as timer is now running, hide reset
        startPauseButton.setText("Pause");
        resetButton.setVisibility(View.INVISIBLE);
        isTimerRunning = true;
    }


    private void changeTimerText() {
        int hours = (int) (timeLeftInTimer / 1000) / 3600;
        int mins = (int) ((timeLeftInTimer / 1000) % 3600) / 60;
        int secs = (int) (timeLeftInTimer / 1000) % 60;

        String updatedTextStr;
        if (hours > 0) {
            updatedTextStr = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, mins, secs);
        } else {
            updatedTextStr = String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
        }

        timerText.setText(updatedTextStr);

    }
}