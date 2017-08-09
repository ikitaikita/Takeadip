package com.takeadip.takeadip;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ImageView;

public class SplashAcivity extends Activity {

    private static final int SLEEP_TIME = 2;
    private ImageView splashImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_acivity);
        splashImage = (ImageView) findViewById(R.id.splashImage);
        int orient = getWindowManager().getDefaultDisplay().getOrientation();
        if (orient == 0) {
            splashImage.setBackgroundResource(R.mipmap.splash_portrait);
        } else {
            splashImage.setBackgroundResource(R.mipmap.splash_landscape);
        }
        Thread thread = new Thread(new SleepWorker());
        thread.start();
    }
    private void goToMainActivity()
    {
        Intent intent = new Intent(this, MainTabActivity.class);
        this.startActivity(intent);
        this.finish();
    }

    private class SleepWorker implements Runnable {

        public void run(){
            try {
                Thread.sleep(SLEEP_TIME * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            goToMainActivity();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int orient = getWindowManager().getDefaultDisplay().getOrientation();
        if (orient == 0) {
            splashImage.setBackgroundResource(R.mipmap.splash_portrait);
        } else {
            splashImage.setBackgroundResource(R.mipmap.splash_landscape);
        }
    }
}
