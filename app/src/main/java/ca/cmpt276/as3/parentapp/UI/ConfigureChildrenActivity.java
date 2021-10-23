package ca.cmpt276.as3.parentapp.UI;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import ca.cmpt276.as3.parentapp.databinding.ActivityConfigureChildrenBinding;

public class ConfigureChildrenActivity extends AppCompatActivity {

    private ActivityConfigureChildrenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityConfigureChildrenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
    }

}