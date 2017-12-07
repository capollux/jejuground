package xyz.zzulu.jejuground.activities;

import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.oreum.tamra.Tamra;
import com.kakao.oreum.tamra.base.Config;
import com.kakao.oreum.tamra.base.Region;
import com.kakao.oreum.tamra.base.Spot;
import com.kakao.oreum.tamra.error.TamraErrorFilters;
import com.kakao.oreum.tamra.error.TamraInitException;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import xyz.zzulu.jejuground.GlobalApplication;
import xyz.zzulu.jejuground.R;
import xyz.zzulu.jejuground.fragments.MapFragment;
import xyz.zzulu.jejuground.fragments.SpotFragment;
import xyz.zzulu.jejuground.fragments.dummy.SpotContent;

public class MainActivity extends BaseAppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MapFragment.OnFragmentInteractionListener, SpotFragment.OnListFragmentInteractionListener {

    private static final String LOG_TAG = "MainActivity";

    public static final String serverIP = "https://jejuground.zzulu.xyz/api/v1";
//    public static final String serverIP = "http://192.168.148.3:3000/api/v1";

    NavigationView navigationView;

    NetworkImageView nivProfileImage;
    TextView tvNickname;
    TextView tvEmail;

    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name_ko);
        setSupportActionBar(toolbar);

        queue = Volley.newRequestQueue(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        nivProfileImage = (NetworkImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        tvNickname = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nickname);
        tvEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.email);

        requestMe();

        initTamra();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(LOG_TAG);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initTamra() {
        String appkey = "be9b590b3b2641ae97314c7dec6098d4";
        Config config = Config.forTesting(
                getApplicationContext(),
                appkey).onSimulation("Nexus 5X");

        Tamra.init(config);

        // monitor region
        Tamra.startMonitoring(Region.of(3));

        // check errors
        Tamra.recentErrors()
                .filter(TamraErrorFilters.causedBy(TamraInitException.class))
                .foreach(tamraError -> Log.d(LOG_TAG, tamraError.toString()));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//
//        //noinspection SimplifiableIfStatement
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                break;
//
//            case R.id.action_settings:
//                Intent intent = new Intent(this, SettingsActivity.class);
//                startActivity(intent);
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (item.getItemId()) {

            case R.id.map:
                fragmentManager.beginTransaction().replace(R.id.fragment_container, MapFragment.newInstance()).commit();
                break;

            case R.id.flags:
                fragmentManager.beginTransaction().replace(R.id.fragment_container, SpotFragment.newInstance(1)).commit();
                break;

            case R.id.unlink:
                onClickUnlink();
                break;

            case R.id.logout:
                onClickLogout();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void onClickLogout() {
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                redirectSigninActivity();
            }
        });
    }

    private void onClickUnlink() {
        final String appendMessage = getString(R.string.com_kakao_confirm_unlink);
        new AlertDialog.Builder(this)
                .setMessage(appendMessage)
                .setPositiveButton(getString(R.string.com_kakao_ok_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserManagement.requestUnlink(new UnLinkResponseCallback() {
                                    @Override
                                    public void onFailure(ErrorResult errorResult) {
                                        Logger.e(errorResult.toString());
                                    }

                                    @Override
                                    public void onSessionClosed(ErrorResult errorResult) {
                                        redirectSigninActivity();
                                    }

                                    @Override
                                    public void onNotSignedUp() {
                                        redirectSigninActivity(); // or Sign Up (if you don't use auto sign up)
                                    }

                                    @Override
                                    public void onSuccess(Long userId) {

                                        String url = serverIP + "/users/" + String.valueOf(userId);

                                        StringRequest strRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
//                                                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        queue.add(strRequest);


                                        redirectSigninActivity();
                                    }
                                });
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getString(R.string.com_kakao_cancel_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

    }

    /**
     * 사용자의 상태를 알아 보기 위해 me API 호출을 한다.
     */
    protected void requestMe() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    redirectSigninActivity();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectSigninActivity();
            }

            @Override
            public void onSuccess(UserProfile userProfile) {

                navigationView.getMenu().getItem(0).setChecked(true);
                onNavigationItemSelected(navigationView.getMenu().getItem(0));

                String userCreateUrl = serverIP + "/users";

                StringRequest strRequest = new StringRequest(Request.Method.POST, userCreateUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
//                        Logger.d(userProfile.toString());
                        setCurrentUser(userProfile);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("user[kakao_id]", String.valueOf(userProfile.getId()));
                        params.put("user[nickname]", String.valueOf(userProfile.getNickname()));
                        params.put("user[email]", String.valueOf(userProfile.getEmail()));
                        params.put("user[profile_image_url]", String.valueOf(userProfile.getProfileImagePath()));
                        params.put("user[thumbnail_image_url]", String.valueOf(userProfile.getThumbnailImagePath()));

                        return params;
                    }
                };

                queue.add(strRequest);
            }

            @Override
            public void onNotSignedUp() {
                redirectSigninActivity(); // or Sign Up (if you don't use auto sign up)
            }
        });
    }

    private void setCurrentUser(final UserProfile userProfile) {
        Log.d(LOG_TAG, userProfile.toString());
        nivProfileImage.setBackground(new ShapeDrawable(new OvalShape()));
        nivProfileImage.setClipToOutline(true);

        setProfileURL(userProfile.getThumbnailImagePath());

        tvNickname.setText(userProfile.getNickname());

        tvEmail.setText(userProfile.getEmail());
    }

    private void setProfileURL(final String profileImageURL) {
        if (nivProfileImage != null && profileImageURL != null) {
            Application app = GlobalApplication.getGlobalApplicationContext();
            if (app == null)
                throw new UnsupportedOperationException("needs com.kakao.GlobalApplication in order to use ImageLoader");
            nivProfileImage.setImageUrl(profileImageURL, ((GlobalApplication) app).getImageLoader());
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(ImageView iv, SpotContent.SpotItem item) {

        queue = Volley.newRequestQueue(this);

        String url = serverIP + "/flags";

        StringRequest strRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("true")){
                    iv.setImageDrawable(ContextCompat.getDrawable( getApplicationContext(), R.drawable.visited_spot ));
                    Toast.makeText(getApplicationContext(), "깃발을 세웠습니다 :)", Toast.LENGTH_SHORT).show();
                } else {
                    iv.setImageDrawable(ContextCompat.getDrawable( getApplicationContext(), R.drawable.not_visited_spot ));
                    Toast.makeText(getApplicationContext(), "깃발이 제거되었습니다 :(", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization: Token token", String.valueOf(UserProfile.loadFromCache().getId()));

                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("spot[spot_id]", String.valueOf(item.spotId));
                params.put("spot[region]", String.valueOf(item.region));
                params.put("spot[name]", String.valueOf(item.description));
                params.put("spot[indoor]", String.valueOf(item.indoor));
                params.put("spot[lat]", String.valueOf(item.lat));
                params.put("spot[lng]", String.valueOf(item.lng));
                return params;
            }
        };
        queue.add(strRequest);

    }


}


