package xyz.zzulu.jejuground.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import xyz.zzulu.jejuground.R;

public class SigninActivity extends BaseAppCompatActivity {

    private SessionCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen(); // Session 의 상태를 체크후 Login을 시도한다.
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }


    /**
     * 세션 콜백
     */
    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            redirectMainActivity();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logger.e(exception);
            }
        }
    }
}
