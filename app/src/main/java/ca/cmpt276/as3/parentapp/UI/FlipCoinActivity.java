package ca.cmpt276.as3.parentapp.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import ca.cmpt276.as3.parentapp.databinding.ActivityFlipCoinBinding;

public class FlipCoinActivity extends AppCompatActivity {

    private ActivityFlipCoinBinding binding;

    public static Intent makeLaunchIntent(Context c) {
        return new Intent(c, FlipCoinActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFlipCoinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

    }

}