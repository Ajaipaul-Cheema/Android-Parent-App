package ca.cmpt276.parentapp.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.widget.Toast;

import java.sql.Time;

import ca.cmpt276.parentapp.UI.TimeoutTimerActivity;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent("Timer")
                .putExtra("Dismiss Timer", intent.getAction()));
    }
}
