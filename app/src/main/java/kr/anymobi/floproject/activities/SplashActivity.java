package kr.anymobi.floproject.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import kr.anymobi.floproject.R;

public class SplashActivity extends Activity {

    private final long DELAY_TIME = 2000; // 2초 대기.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, PlayerActivity.class));
            finish();
        }, DELAY_TIME);
    }

}