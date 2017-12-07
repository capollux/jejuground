package xyz.zzulu.jejuground.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import xyz.zzulu.jejuground.activities.MainActivity;
import xyz.zzulu.jejuground.activities.SigninActivity;

/**
 * Created by hwangjw on 2017. 11. 15..
 */

public abstract class BaseAppCompatActivity extends AppCompatActivity {

    protected void redirectSigninActivity() {
        final Intent intent = new Intent(this, SigninActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity(intent);
    }

    protected void redirectMainActivity() {
        final Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
