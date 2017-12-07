package xyz.zzulu.jejuground.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.kakao.auth.Session;

public class SplashActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, SigninActivity.class);
            startActivity(intent);
            finish();
        }, 500);

    }
}
