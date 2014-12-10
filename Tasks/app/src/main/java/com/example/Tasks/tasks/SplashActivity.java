package com.example.Tasks.tasks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.Tasks.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  // no title bar
        setContentView(R.layout.splash);

        TimerTask task = new TimerTask() {  // create thread for Splash screen
            @Override
            public void run() {  // to group_main screen
                Intent toMainScreen = new Intent(SplashActivity.this, GroupMainActivity.class);
                startActivity(toMainScreen);
                SplashActivity.this.finish();  // make sure Splash screen is off
            }
        };
        new Timer().schedule(task, 1500);  // show Splash screen for 1.5 seconds
    }
}
