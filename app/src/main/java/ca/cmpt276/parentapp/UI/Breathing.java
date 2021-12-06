package ca.cmpt276.parentapp.UI;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import ca.cmpt276.as3.parentapp.R;

public class Breathing extends AppCompatActivity {
    private static final String BREATHS_INDEX_PREFS = "BREATHS_INDEX_PREFS";
    private static final String BREATH_PREF = "BREATH_PREF";
    private int numOfBreaths = 0;
    private MediaPlayer inhaleSound;
    private MediaPlayer exhaleSound;
    private ImageView breatheImage;
    private TextView guideText, helpText;
    private Button startButton;
    private EditText applyBreatheNum;
    private Button applyNumOfBreaths;
    Animation animationZoomIn, animationZoomOut;

    @SuppressLint({"SetTextI18n", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breathing);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Breathe");
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        breatheImage = findViewById(R.id.breathe);
        guideText = findViewById(R.id.guideText);
        applyBreatheNum = findViewById(R.id.editBreatheNum);
        applyNumOfBreaths = findViewById(R.id.applyBreathe);
        helpText = findViewById(R.id.helpText);
        helpText.setVisibility(View.VISIBLE);

        inhaleSound = MediaPlayer.create(this, R.raw.inhale_sound);
        exhaleSound = MediaPlayer.create(this, R.raw.exhale_sound);

        numOfBreaths = loadNumOfBreaths();

        guideText.setText(getString(R.string.let_s_take_n_breaths_together_choose_between_1_10_breaths));

        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> {
            if (numOfBreaths != 0) {
                applyBreatheNum.setEnabled(true);
                startButton.setText("In");
            } else {
                Toast.makeText(Breathing.this, "Choose breaths to begin with.", Toast.LENGTH_SHORT).show();
            }
        });

        startIntroAnimation();

        curState.handleEntering();
        handleButtonClicks();
        saveNumOfBreaths();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onStart() {
        super.onStart();
        startButton.setText("Begin");
        if (numOfBreaths != 0) {
            guideText.setText("Time to do " + numOfBreaths + " breath(s) together");
        } else {
            guideText.setText(getString(R.string.let_s_take_n_breaths_together_choose_between_1_10_breaths));
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        startButton.setText("Begin");
        if (numOfBreaths != 0) {
            guideText.setText("Time to do " + numOfBreaths + " breath(s) together");
        } else {
            guideText.setText(getString(R.string.let_s_take_n_breaths_together_choose_between_1_10_breaths));
        }
    }

    // inspired by https://opencoursehub.cs.sfu.ca/bfraser/grav-cms/cmpt276/project (Dr. Brian Fraser's State Pattern Demo)
    private abstract static class State {
        void handleEntering() {
        }

        void handlePressingButton() {
        }

        void handleReleasingButton() {

        }

        void handleExiting() {
        }
    }

    private void changeState(State changedState) {
        curState.handleEntering();
        curState.handleExiting();
        curState = changedState;
    }

    private final State breatheInState = new InButtonState();
    private final State breatheOutState = new OutButtonState();
    private final State inactiveState = new IdleState();
    private final State afterBreathState = new AfterBreathState();
    private State curState = inactiveState;

    // inspired by https://stackoverflow.com/questions/11690504/how-to-use-view-ontouchlistener-instead-of-onclick
    @SuppressLint("ClickableViewAccessibility")
    private void handleButtonClicks() {
        final Button startButton = findViewById(R.id.startButton);
        startButton.setOnTouchListener((v, event) -> {
            if (numOfBreaths != 0) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        applyBreatheNum.setEnabled(false);
                        inhaleSound.setLooping(true);
                        inhaleSound.start();
                        curState.handlePressingButton();
                        break;

                    case MotionEvent.ACTION_UP:
                        inhaleSound.pause();
                        exhaleSound.setLooping(true);
                        exhaleSound.start();
                        curState.handleReleasingButton();
                        break;
                }
            } else {
                Toast.makeText(Breathing.this, "Choose breaths to begin with.", Toast.LENGTH_SHORT).show();
                applyBreatheNum.setEnabled(true);
            }
            return true;
        });
    }

    private void startZoomIn() {
        animationZoomIn = AnimationUtils.loadAnimation(Breathing.this, R.anim.zoom_in);
        breatheImage.setImageResource(R.drawable.breathe_in_circle);
        breatheImage.startAnimation(animationZoomIn);
    }

    private void startZoomOut() {
        animationZoomOut = AnimationUtils.loadAnimation(Breathing.this, R.anim.zoom_out);
        breatheImage.setImageResource(R.drawable.breathe_out_circle);
        breatheImage.startAnimation(animationZoomOut);
    }

    @SuppressLint("SetTextI18n")
    private void startIntroAnimation() {
        applyNumOfBreaths.setOnClickListener(v -> {
            if (!applyBreatheNum.getText().toString().equals("")) {
                numOfBreaths = Integer.parseInt(applyBreatheNum.getText().toString());

                if (numOfBreaths >= 1 && numOfBreaths <= 10) {
                    guideText.setText("Time to do " + numOfBreaths + " breath(s) together.");
                    applyBreatheNum.getText().clear();
                    startButton.setText("In");
                    helpText.setVisibility(View.VISIBLE);
                    helpText.setText("Hold button and breathe in");
                    saveNumOfBreaths();
                } else {
                    Toast.makeText(Breathing.this, "Num of breaths must be between 1-10.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void saveNumOfBreaths() {
        SharedPreferences prefs = this.getSharedPreferences(BREATHS_INDEX_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(BREATH_PREF, numOfBreaths);
        editor.apply();
    }

    private int loadNumOfBreaths() {
        SharedPreferences prefs = this.getSharedPreferences(BREATHS_INDEX_PREFS, MODE_PRIVATE);
        return prefs.getInt(BREATH_PREF, numOfBreaths);
    }

    // inspired by https://opencoursehub.cs.sfu.ca/bfraser/grav-cms/cmpt276/project (Dr. Brian Fraser's State Pattern Demo)
    private class OutButtonState extends State {
        Handler handler = new Handler();
        @SuppressLint("SetTextI18n")
        Runnable updateState = () -> {
            if (numOfBreaths != 0) {
                numOfBreaths--;
                saveNumOfBreaths();
            } else {
                startButton.setText("Good job");
            }
            if (numOfBreaths > 0) {
                changeState(breatheInState);
            } else {
                changeState(afterBreathState);
            }
        };

        @SuppressLint("SetTextI18n")
        @Override
        void handleEntering() {
            super.handleEntering();
            numOfBreaths = loadNumOfBreaths();
            if (numOfBreaths != 0) {
                applyBreatheNum.setEnabled(false);
                guideText.setText("Release button and breathe out");
                startButton.setText("In");
                helpText.setVisibility(View.VISIBLE);
                helpText.setText("Hold button and breathe in");
            } else {
                startButton.setText("You have finished all breaths!");
                if (animationZoomOut.hasEnded()) {
                    applyBreatheNum.setEnabled(true);
                }
            }
        }

        @Override
        void handleReleasingButton() {
            super.handleReleasingButton();
            handler.removeCallbacks(updateState);
            // https://coderedirect.com/questions/382862/handler-postdelayed-and-thread-sleep
            handler.postDelayed(updateState, 3000);
            startZoomOut();
            new Handler(Looper.getMainLooper()).postDelayed(() -> exhaleSound.pause(), 10000);
        }

        @Override
        void handlePressingButton() {
            super.handlePressingButton();
            exhaleSound.pause();
            handler.removeCallbacks(updateState);
        }

        @SuppressLint("SetTextI18n")
        @Override
        void handleExiting() {
            super.handleExiting();
            if (numOfBreaths != 0) {
                guideText.setText("Breaths left: " + numOfBreaths);
                applyBreatheNum.setEnabled(false);
            } else {
                startButton.setText("Good job");
                guideText.setText("You have finished all breaths! Click Good Job to edit breaths again.");
                helpText.setVisibility(View.INVISIBLE);
                if (animationZoomOut.hasEnded()) {
                    applyBreatheNum.setEnabled(true);
                }
            }
        }
    }

    private class InButtonState extends State {
        Handler handler = new Handler();
        Runnable updateState = () -> changeState(breatheOutState);

        @SuppressLint("SetTextI18n")
        @Override
        void handleEntering() {
            super.handleEntering();
            numOfBreaths = loadNumOfBreaths();
            startButton.setText("Out");
            helpText.setVisibility(View.VISIBLE);
            helpText.setText("Breathe Out");
        }

        @Override
        void handlePressingButton() {
            super.handlePressingButton();
            handler.removeCallbacks(updateState);
            handler.postDelayed(updateState, 3000);
            startZoomIn();
            new Handler(Looper.getMainLooper()).postDelayed(() -> inhaleSound.pause(), 10000);
        }

        @Override
        void handleReleasingButton() {
            super.handleReleasingButton();
            breatheImage.clearAnimation();
            inhaleSound.pause();
            handler.removeCallbacks(updateState);
        }

        @Override
        void handleExiting() {
            super.handleExiting();
            handler.removeCallbacks(updateState);
            applyBreatheNum.setEnabled(false);
        }
    }

    private class IdleState extends State {
        @SuppressLint("SetTextI18n")
        @Override
        void handleEntering() {
            super.handleEntering();
            numOfBreaths = loadNumOfBreaths();
            if (numOfBreaths != 0) {
                guideText.setText("Time to do " + numOfBreaths + " breath(s) together");
            }
        }

        @Override
        void handlePressingButton() {
            super.handlePressingButton();
            changeState(breatheInState);
            breatheInState.handlePressingButton();
        }

        @SuppressLint("SetTextI18n")
        @Override
        void handleExiting() {
            super.handleExiting();
            if (numOfBreaths != 0) {
                guideText.setText("Breaths left: " + numOfBreaths);
                helpText.setVisibility(View.VISIBLE);
                helpText.setText("Hold button and breathe in");
                startButton.setText("In");
            } else {
                startButton.setText("Good job");
                guideText.setText("You have finished all breaths!");
                helpText.setVisibility(View.INVISIBLE);
                if (animationZoomOut.hasEnded()) {
                    applyBreatheNum.setEnabled(true);
                }
            }
        }
    }

    private class AfterBreathState extends State {
        @SuppressLint("SetTextI18n")
        @Override
        void handleEntering() {
            super.handleEntering();
        }

        @Override
        void handlePressingButton() {
            super.handlePressingButton();
            changeState(inactiveState);
        }
    }
}