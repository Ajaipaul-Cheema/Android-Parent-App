package ca.cmpt276.parentapp.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ca.cmpt276.parentapp.UI.TimeoutTimerActivity;

/**
 * NotificationReceiver class uses a
 * BroadcastReceiver to register an
 * action button which will dismiss
 * the timers sounds.
 */
public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // inspired by https://www.youtube.com/watch?v=rRoHBWKQoRE
        String sound = intent.getStringExtra("Dismiss Timer");
        if (sound.equals("Timer has finished.")) {
            TimeoutTimerActivity.stopSound();
        }

    }
}
