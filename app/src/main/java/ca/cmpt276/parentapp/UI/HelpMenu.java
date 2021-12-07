package ca.cmpt276.parentapp.UI;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import ca.cmpt276.as3.parentapp.R;
import ca.cmpt276.as3.parentapp.databinding.ActivityHelpMenuBinding;

/**
 * This class shows the citations for this project and the creators
 */
public class HelpMenu extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ca.cmpt276.as3.parentapp.databinding.ActivityHelpMenuBinding binding = ActivityHelpMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        hyperLink();
        setSupportActionBar(binding.toolbar);

        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor(getString(R.string.yellow_brown_color)));
        actionBar.setBackgroundDrawable(colorDrawable);

    }


    private void hyperLink() {
        TextView linkText = findViewById(R.id.hyperlink_class);
        linkText.setMovementMethod(LinkMovementMethod.getInstance());

        TextView linkText2 = findViewById(R.id.hyperlink_class2);
        linkText2.setMovementMethod(LinkMovementMethod.getInstance());

        TextView linkText3 = findViewById(R.id.hyperlink_class3);
        linkText3.setMovementMethod(LinkMovementMethod.getInstance());

        TextView linkText4 = findViewById(R.id.hyperlink_class4);
        linkText4.setMovementMethod(LinkMovementMethod.getInstance());

        TextView linkText5 = findViewById(R.id.hyperlink_class5);
        linkText5.setMovementMethod(LinkMovementMethod.getInstance());

        TextView linkText6 = findViewById(R.id.hyperlink_class6);
        linkText6.setMovementMethod(LinkMovementMethod.getInstance());

        TextView linkText7 = findViewById(R.id.hyperlink_class7);
        linkText7.setMovementMethod(LinkMovementMethod.getInstance());

        TextView linkText8 = findViewById(R.id.hyperlink_class8);
        linkText8.setMovementMethod(LinkMovementMethod.getInstance());

        TextView linkText9 = findViewById(R.id.hyperlink_class9);
        linkText9.setMovementMethod(LinkMovementMethod.getInstance());

        TextView linkText10 = findViewById(R.id.hyperlink_class10);
        linkText10.setMovementMethod(LinkMovementMethod.getInstance());
    }
}