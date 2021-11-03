package ca.cmpt276.as3.parentapp.UI;

import android.content.Context;
import android.content.Intent;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import ca.cmpt276.as3.parentapp.R;
import ca.cmpt276.as3.parentapp.data.Child;
import ca.cmpt276.as3.parentapp.data.FlipRecord;
import ca.cmpt276.as3.parentapp.databinding.ActivityFlipCoinBinding;
import ca.cmpt276.as3.parentapp.utils.DBOpenHelper;
import ca.cmpt276.as3.parentapp.utils.SystemUtil;

import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

public class FlipCoinActivity extends AppCompatActivity {

    private ActivityFlipCoinBinding binding;
    private Spinner spinner;
    private List<Child> list = new ArrayList<>();
    private DBOpenHelper dbOpenHelper;
    private String firstChild;
    private String secondChild;
    private int flipnum=0;
    private TossImageView mTossImageView;
    private int curindex=0;
    private TextView tv_result;
    MediaPlayer player = new MediaPlayer();

    public static Intent makeLaunchIntent(Context c) {
        return new Intent(c, FlipCoinActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFlipCoinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        dbOpenHelper = new DBOpenHelper(this);
        list = dbOpenHelper.getAllChild();
        String [] childs=new String[list.size()];
        for(int i=0;i<list.size();i++){
            childs[i]=list.get(i).getChildname();
        }
        spinner = findViewById(R.id.sp_child);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, childs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(flipnum==0){
                    firstChild = childs[i];
                    flipnum=1;
                }
                else if(flipnum==1){
                    secondChild = childs[i];
                    flipnum=0;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        if(SystemUtil.lastnum!=0){
            curindex=SystemUtil.lastnum;
            curindex++;
            spinner.setSelection(curindex);
        }

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                toStart();
                curindex++;

                SystemUtil.lastnum = curindex;
            }
        });

        mTossImageView = findViewById(R.id.tiv);
        tv_result = findViewById(R.id.tv_result);

        Button viewButton = findViewById(R.id.viewButton);
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FlipCoinActivity.this,HistoryRecords.class));
            }
        });

        AssetManager assetManager = getResources().getAssets();
        try {
            AssetFileDescriptor fileDescriptor = assetManager.openFd("backmusic.mp3");
            player.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getStartOffset());
            player.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void toStart(){
        int rs=new Random().nextInt(2) == 0 ? TossImageView.RESULT_FRONT : TossImageView.RESULT_REVERSE;

        mTossImageView.cleareOtherAnimation();

        TranslateAnimation translateAnimation0 = new TranslateAnimation(0, 0, 0, -400);
        translateAnimation0.setDuration(3000);
        TranslateAnimation translateAnimation1 = new TranslateAnimation(0, 0, 0, 400);
        translateAnimation1.setDuration(3000);
        translateAnimation1.setStartOffset(3000);

        mTossImageView.setInterpolator(new DecelerateInterpolator())
                .setDuration(6000)
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

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                player.pause();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
                SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyddMMHHmmss");
                Date curDate =  new Date(System.currentTimeMillis());
                String flipno = "";

                FlipRecord data = new FlipRecord();
                if(flipnum==0) {
                    data.setChildname(firstChild);
                    flipno = formatter1.format(curDate);
                }else{
                    data.setChildname(secondChild);
                }
                data.setResult(rs+"");
                data.setTime(formatter.format(curDate));
                data.setFlipno(flipno);
                if(rs==1){
                    data.setIswin("y");
                }else{
                    data.setIswin("n");
                }
                boolean flag=dbOpenHelper.addRecord(data);
                disResult(flag,rs);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mTossImageView.startToss();
        player.start();
    }

    private void disResult(boolean flag,int result){
        if(flag){
            if(result==1) {
                tv_result.setText("Heads");
            }else{
                tv_result.setText("Tails");
            }

            if(curindex>(list.size()-1)){
                curindex=0;
            }
            spinner.setSelection(curindex);
        }else{
            Toast.makeText(FlipCoinActivity.this,"Record Failed",Toast.LENGTH_SHORT).show();
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_flip, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int item_id = item.getItemId();
        switch (item_id) {
            case R.id.view:
                startActivity(new Intent(FlipCoinActivity.this,HistoryRecords.class));
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }
}