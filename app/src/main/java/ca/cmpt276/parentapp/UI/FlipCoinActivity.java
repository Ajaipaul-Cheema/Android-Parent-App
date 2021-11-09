package ca.cmpt276.parentapp.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

import ca.cmpt276.as3.parentapp.R;
import ca.cmpt276.as3.parentapp.databinding.ActivityFlipCoinBinding;
import ca.cmpt276.parentapp.model.FlipResult;
import ca.cmpt276.parentapp.model.FlipResultManager;

import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * The FlipCoinActivity takes the child data
 * from ConfigureChildren, and asks those children
 * to choose h/t and flip the coin. Coin can be flipped
 * with 0 children configured. If there are children playing,
 * can click 'history' button to view flip history.
 * Index of last child who played is saved between app runs.
 */
public class FlipCoinActivity extends AppCompatActivity {

    private int playerCoinChoice;
    private FlipResultManager resultManager;
    ArrayList<String> childrenNames = new ArrayList<>();
    private static final String CHILDREN_INDEX_PREF = "children_pref";
    private static final String INDEX_PREF = "index_pref";
    private Boolean coinFlipDone;
    TextView childNameChoice;
    TextView resultOfFlip;
    private int childIdx;
    private static final String NAME_PREF = "NamePrefs";
    private static final String NAMES_PREF = "NamesSizePref";
    private TossImageView mTossImageView;
    MediaPlayer player = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        ca.cmpt276.as3.parentapp.databinding.ActivityFlipCoinBinding binding = ActivityFlipCoinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor(getString(R.string.brown_color)));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        loadData(this);
        resultManager = FlipResultManager.getInstance();
        resultManager.loadFlipHistory(this);

        playerCoinChoice = -1;
        coinFlipDone = false;
        childIdx = loadChildrenData();

        childNameChoice = findViewById(R.id.tvChildChoice);
        if (childrenNames.size() > 0) {
            childNameChoice.setText(childrenNames.get(childIdx) + getString(R.string.children_turn_string));
        }

        mTossImageView = findViewById(R.id.tiv);
        resultOfFlip = findViewById(R.id.tv_result);

        setUpHeadTailsButton();
        setUpFlipCoinButton();
        setUpHistoryButton();

        saveChildrenData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadChildrenData();
        resultManager.loadFlipHistory(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveChildrenData();
        resultManager.saveFlipHistory(this);
    }

    private void setUpFlipCoinButton() {
        Button flipCoinBtn = findViewById(R.id.btnFlipCoin);
        flipCoinBtn.setOnClickListener(view -> {
            if (childrenNames.size() <= 0) {
                resultManager.saveFlipHistory(this);
                coinToss();
            } else if (playerCoinChoice == -1) {
                resultManager.saveFlipHistory(this);
                Toast.makeText(this, getString(R.string.choose_head_or_tail_str), Toast.LENGTH_SHORT).show();
            } else {
                resultManager.saveFlipHistory(this);
                coinToss();
            }
        });
    }

    private void setUpHistoryButton() {
        Button historyButton = findViewById(R.id.viewButton);
        historyButton.setOnClickListener(view -> {
            Intent i = HistoryFlipView.makeLaunchIntent(FlipCoinActivity.this);
            startActivity(i);
        });
    }

    public static Intent makeLaunchIntent(Context c) {
        return new Intent(c, FlipCoinActivity.class);
    }

    private void setUpHeadTailsButton() {
        Button headsBtn = findViewById(R.id.btnChooseHeads);
        Button tailsBtn = findViewById(R.id.btnChooseTails);

        headsBtn.setOnClickListener(view -> {
            playerCoinChoice = 1;
        });

        tailsBtn.setOnClickListener(view -> {
            playerCoinChoice = 0;
        });
    }

    private void playCoinFlipSound() {
        player = MediaPlayer.create(this, R.raw.coin_flip_sound);
        player.start();
    }

    private void coinToss() {
        Random random = new Random();
        resultManager.loadFlipHistory(this);
        loadChildrenData();
        int tossResult = random.nextInt(2);
        resultOfFlip.setVisibility(View.INVISIBLE);
        childNameChoice.setVisibility(View.INVISIBLE);
        toStart(tossResult);

        if (childrenNames.size() > 0) {

            FlipResult toss = new FlipResult("" + childrenNames.get(childIdx), LocalDateTime.now(), tossResult, playerCoinChoice);
            resultManager.add(toss);

            childIdx++;
            checkIfIdxExits();

            if (coinFlipDone) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    resultOfFlip.setVisibility(View.VISIBLE);
                    childNameChoice.setVisibility(View.VISIBLE);
                    if (tossResult == 1) {
                        resultOfFlip.setText(R.string.results_heads);
                    } else {
                        resultOfFlip.setText(R.string.results_tails);
                    }
                    childNameChoice.setText(childrenNames.get(childIdx) + getString(R.string.children_turn_string));
                }, 2000);
            }
            playerCoinChoice = -1;
            saveChildrenData();
            resultManager.saveFlipHistory(this);

        }
    }

    // save & load inspired by https://www.youtube.com/watch?v=jcliHGR3CHo&t=343s
    private void loadData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAMES_PREF, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(NAME_PREF, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        childrenNames = gson.fromJson(json, type);

        if (childrenNames == null) {
            childrenNames = new ArrayList<>();
        }
    }

    private void saveChildrenData() {
        SharedPreferences prefs = this.getSharedPreferences(CHILDREN_INDEX_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(INDEX_PREF, childIdx);
        editor.apply();
    }

    private int loadChildrenData() {
        SharedPreferences prefs = this.getSharedPreferences(CHILDREN_INDEX_PREF, MODE_PRIVATE);
        if (childIdx > childrenNames.size()) {
            childIdx = 0;
        }
        return prefs.getInt(INDEX_PREF, childIdx);
    }

    private void checkIfIdxExits() {
        if (childIdx >= childrenNames.size()) {
            childIdx = 0;
        }
    }

    private void toStart(int choice) {

        int rs = choice == 0 ? TossImageView.RESULT_FRONT : TossImageView.RESULT_REVERSE;

        mTossImageView.cleareOtherAnimation();

        TranslateAnimation translateAnimation0 = new TranslateAnimation(0, 0, 0, -400);
        translateAnimation0.setDuration(750);
        TranslateAnimation translateAnimation1 = new TranslateAnimation(0, 0, 0, 400);
        translateAnimation1.setDuration(750);
        translateAnimation1.setStartOffset(750);

        mTossImageView.setInterpolator(new DecelerateInterpolator())
                .setDuration(1500)
                .setCircleCount(40)
                .setXAxisDirection(TossAnimation.DIRECTION_CLOCKWISE)
                .setYAxisDirection(TossAnimation.DIRECTION_NONE)
                .setZAxisDirection(TossAnimation.DIRECTION_NONE)
                .setResult(rs);

        mTossImageView.addOtherAnimation(translateAnimation0);
        mTossImageView.addOtherAnimation(translateAnimation1);
        mTossImageView.setTossAnimationListener(new TossAnimation.TossAnimationListener() {
            @Override
            public void onDrawableChange(int result, TossAnimation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {
                playCoinFlipSound();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                player.stop();
            }


            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mTossImageView.startToss();
        coinFlipDone = true;
    }

}