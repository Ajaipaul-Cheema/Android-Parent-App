package ca.cmpt276.parentapp.UI;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
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
import ca.cmpt276.parentapp.model.NotificationReceiver;

public class TimeoutTimerActivity extends AppCompatActivity {

    private static final String NOTIFICATION_CHANNEL_NAME = "Dismiss Timer";
    private static final String NOTIFICATION_CHANNEL_DESCRIPTION = "notification for timer";
    private static final String NOTIFICATION_CHANNEL_ID = "notification timer ID";
    private static final String ACTION_NAME = "Timer";
    private EditText customTime;
    private ImageButton useCustomTime;
    private TextView timerText;
    private TextView spinnerTitle;
    private TextView customChoiceTitle;
    private Button startPauseButton;
    private Button resetButton;
    private Spinner dropDownMenu;
    private static final String timer_Prefs = "timer_prefs";
    private static final String timeStart = "startTime";
    private static final String timeLeft = "millisLeft";
    private static final String timeRunning = "timerRunning";
    private static final String timeAtEnd = "endTime";
    private static CountDownTimer countDownTimer;
    private boolean isTimerRunning;
    private long startTime;
    private long endTime;
    private long timeLeftInTimer;
    public static Vibrator timerVibrator;
    public static MediaPlayer alarmSound;
    private ActivityTimeoutTimerBinding binding;

    public static Intent makeLaunchIntent(Context c) {
        return new Intent(c, TimeoutTimerActivity.class);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            alarmSound.stop();
            timerVibrator.cancel();
        }
    };

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            registerReceiver(broadcastReceiver, new IntentFilter(ACTION_NAME));
            startService(new Intent(getBaseContext(), BroadcastReceiver.class));
        }

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#C19A6B"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);


        handleCustomTimeCheckButton();
        setUpDropDownList();
        setUpTimer();
    }

    private void handleCustomTimeCheckButton() {
        customChoiceTitle = findViewById(R.id.tvCustomTitle);
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

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences prefs = getSharedPreferences(timer_Prefs, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong(timeStart, startTime);
        editor.putLong(timeLeft, timeLeftInTimer);
        editor.putBoolean(timeRunning, isTimerRunning);
        editor.putLong(timeAtEnd, endTime);

        editor.apply();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences(timer_Prefs, MODE_PRIVATE);
        startTime = prefs.getLong(timeStart, 0);
        timeLeftInTimer = prefs.getLong(timeLeft, startTime);
        isTimerRunning = prefs.getBoolean(timeRunning, false);

        changeTimerText();
        changeVisibilityPostClick();

        if (isTimerRunning) {
            endTime = prefs.getLong("endTime", 0);
            timeLeftInTimer = endTime - System.currentTimeMillis();
            if (timeLeftInTimer < 0) {
                timeLeftInTimer = 0;
                isTimerRunning = false;
                changeTimerText();
                changeVisibilityPostClick();
            } else {
                startCountdown();
            }
        }
    }

    private void setTime(long milliSecs) {
        startTime = milliSecs;
        resetCountdown();
    }

    //  https://stackoverflow.com/questions/12108893/set-onclicklistener-for-spinner-item
    private void setUpDropDownList() {
        spinnerTitle = findViewById(R.id.tvDropDownTitle);
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
                if (timeLeftInTimer <= 0) {
                    Toast.makeText(this, "Choose a timer option to begin with.", Toast.LENGTH_SHORT).show();
                } else {
                    startCountdown();
                }

            }
        });

        resetButton.setOnClickListener(v -> {
            resetCountdown();
        });
        changeTimerText();
    }

    private void pauseCountdown() {
        countDownTimer.cancel();
        isTimerRunning = false;
        changeVisibilityPostClick();
    }

    private void resetCountdown() {
        timeLeftInTimer = startTime;
        changeTimerText();
        changeVisibilityPostClick();
    }

    private void playAlarmSound() {
        alarmSound = MediaPlayer.create(TimeoutTimerActivity.this, R.raw.best_wake_up_tone);
        alarmSound.setLooping(true);
        alarmSound.start();
    }

    private void vibrateDevice() {
        timerVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        if (!timerVibrator.hasVibrator()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            timerVibrator.vibrate(
                    VibrationEffect.createOneShot(11000, VibrationEffect.DEFAULT_AMPLITUDE)
            );
        } else {
            long[] timerPattern = {0, 5500, 20, 5500};
            timerVibrator.vibrate(timerPattern, -1);
        }
    }

    private void showNotification() {
           NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                        NOTIFICATION_CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setDescription(NOTIFICATION_CHANNEL_DESCRIPTION);
                notificationChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(notificationChannel);
            }

        Intent activityIntent = new Intent(this, TimeoutTimerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);

        Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
        broadcastIntent.putExtra(NOTIFICATION_CHANNEL_NAME, "Timer has finished.");
        PendingIntent actionIntent = PendingIntent.getBroadcast(this,
                0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.timernotifypicture)
                    .setContentTitle("Timeout Timer")
                    .setContentText("Timer has finished.")
                    .setColor(Color.YELLOW)
                    .setAutoCancel(true)
                    .addAction(R.drawable.timernotifypicture, "Dismiss Timer Sound", actionIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE);
            mBuilder.setContentIntent(actionIntent);
            mBuilder.setContentIntent(contentIntent);
            notificationManager.notify(0, mBuilder.build());

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
                if (isTimerRunning) {
                    vibrateDevice();
                    playAlarmSound();
                    showNotification();
                    isTimerRunning = false;
                    changeVisibilityPostClick();
                }
            }
        }.start();

        // Update button to pause because timer is currently running
        // change boolean to true as timer is now running, hide reset button
        isTimerRunning = true;
        changeVisibilityPostClick();
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

    private void changeVisibilityPostClick() {
        if (isTimerRunning) {
            customTime.setVisibility(View.INVISIBLE);
            useCustomTime.setVisibility(View.INVISIBLE);
            customChoiceTitle.setVisibility(View.INVISIBLE);
            resetButton.setVisibility(View.INVISIBLE);
            spinnerTitle.setVisibility(View.INVISIBLE);
            dropDownMenu.setVisibility(View.INVISIBLE);
            startPauseButton.setText("Pause");
        } else {
            customTime.setVisibility(View.VISIBLE);
            spinnerTitle.setVisibility(View.VISIBLE);
            customChoiceTitle.setVisibility(View.VISIBLE);
            useCustomTime.setVisibility(View.VISIBLE);
            dropDownMenu.setVisibility(View.VISIBLE);
            startPauseButton.setText("Start");

            if (timeLeftInTimer < 1000) {
                startPauseButton.setVisibility(View.INVISIBLE);
            } else {
                startPauseButton.setVisibility(View.VISIBLE);
            }

            if (timeLeftInTimer < startTime) {
                resetButton.setVisibility(View.VISIBLE);
            } else {
                resetButton.setVisibility(View.INVISIBLE);
            }
        }


    }
}