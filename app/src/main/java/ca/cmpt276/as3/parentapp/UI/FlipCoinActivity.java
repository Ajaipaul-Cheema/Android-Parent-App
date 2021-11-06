package ca.cmpt276.as3.parentapp.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import ca.cmpt276.as3.parentapp.R;
import ca.cmpt276.as3.parentapp.databinding.ActivityFlipCoinBinding;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.ui.AppBarConfiguration;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FlipCoinActivity extends AppCompatActivity {

    private ActivityFlipCoinBinding binding;

    private int playerCoinChoice;
    private FlipResultManager resultManager;
    ArrayList<String> childrenNames = new ArrayList<>();
    private static final String CHILDREN_INDEX_PREF = "children_pref";
    private static final String INDEX_PREF = "index_pref";

    TextView childNameChoice;
    private int childIdx;
    private static final String NAME_PREF = "NamePrefs";
    private static final String NAMES_PREF = "NamesSizePref";
    private TossImageView mTossImageView;
    MediaPlayer player = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        binding = ActivityFlipCoinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        loadData(this);
        resultManager = FlipResultManager.getInstance();

        playerCoinChoice = -1;

        childNameChoice = findViewById(R.id.tvChildChoice);
        if(childrenNames.size()>0) {
            childNameChoice.setText(childrenNames.get(childIdx) + "'s turn to pick!");
        }

        mTossImageView = findViewById(R.id.tiv);
        setUpHeadTailsButton();
        setUpFlipCoinButton();
        setUpHistoryButton();


    }

    private void setUpFlipCoinButton(){
        Button flipCoinBtn = findViewById(R.id.btnFlipCoin);
        flipCoinBtn.setOnClickListener(view -> {
            if(childrenNames.size()<=0){
                coinToss();
            }
            else if(playerCoinChoice == -1){
                Toast.makeText(this, "PLEASE CHOSE H/T", Toast.LENGTH_SHORT).show();
            }
            else {
                coinToss();
            }
        });
    }

    private void setUpHistoryButton(){
        Button historyButton = findViewById(R.id.viewButton);
        historyButton.setOnClickListener(view -> {
            Intent i = HistoryFlipView.makeLaunchIntent(FlipCoinActivity.this);
            startActivity(i);
        });
    }



    public static Intent makeLaunchIntent(Context c) {
        return new Intent(c, FlipCoinActivity.class);
    }

    private void setUpHeadTailsButton(){
        Button headsBtn = findViewById(R.id.btnChooseHeads);
        Button tailsBtn = findViewById(R.id.btnChooseTails);

        headsBtn.setOnClickListener(view -> {
            playerCoinChoice = 1;
        });

        tailsBtn.setOnClickListener(view -> {
            playerCoinChoice = 0;
        });
    }

    private void coinToss(){
        Random random = new Random();

        int tossResult = random.nextInt(2);
        toStart(tossResult);

        if (childrenNames.size()>0) {

            FlipResult toss = new FlipResult("" + childrenNames.get(childIdx), LocalDateTime.now(), tossResult, playerCoinChoice);
            resultManager.add(toss);

            childIdx++;
            checkIfIdxExits();
            childNameChoice.setText(childrenNames.get(childIdx)+"'s turn to pick!");
            playerCoinChoice = -1;

        }
    }

    private void loadData(Context context){
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

    private void checkIfIdxExits(){
        if (childIdx>=childrenNames.size()){
            childIdx=0;
        }
    }

    private void toStart(int choice){
        
        int rs=choice == 0 ? TossImageView.RESULT_FRONT : TossImageView.RESULT_REVERSE;

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
                //playCoinFlipSound();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
               // player.stop();

            }


            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mTossImageView.startToss();
    }

    }