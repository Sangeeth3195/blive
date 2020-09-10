package com.blive.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blive.BLiveApplication;
import com.blive.BuildConfig;
import com.blive.agora.rtmChat.ChatManager;
import com.blive.constant.Constants_api;
import com.blive.R;
import com.blive.model.GenericResponse;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.session.SessionLogin;
import com.blive.session.SessionUser;

import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sans on 24/08/2018.
 */

public class ActivitySettings extends BaseBackActivity {

    private ImageView phoneLogo, facebookLogo, googleLogo, twitterLogo, instgramLogo;
    private ChatManager mChatManager;
    private boolean isClicked = false;
    TextView versioncode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        BLiveApplication.setCurrentActivity(this);
        iniUI();
    }

    private void iniUI() {
        setTitle("Settings");

        phoneLogo = findViewById(R.id.phone_logo);
        facebookLogo = findViewById(R.id.facebook_logo);
        googleLogo = findViewById(R.id.google_logo);
        twitterLogo = findViewById(R.id.twitter_logo);
        instgramLogo = findViewById(R.id.instgram_logo);
        versioncode = findViewById(R.id.versioncode);
        mChatManager = BLiveApplication.getInstance().getChatManager();
        versioncode.setText("version " + BuildConfig.VERSION_NAME);
        String connected_account = SessionUser.getUser().getLogin_domain();
        switch (connected_account) {
            case "google":
                googleLogo.setVisibility(View.VISIBLE);
                break;
            case "facebook":
                facebookLogo.setVisibility(View.VISIBLE);
                break;
            case "instagram":
                instgramLogo.setVisibility(View.VISIBLE);
                break;
            case "twitter":
                twitterLogo.setVisibility(View.VISIBLE);
                break;
            case "mobile":
                phoneLogo.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        utils.deleteCache(this);
    }

    @OnClick(R.id.ll_logout)
    public void onClickLogout() {
        if (utils.isNetworkAvailable()) {
            utils.showProgress();
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GenericResponse> call = apiClient.logout(SessionUser.getUser().getUsername());
            call.enqueue(new retrofit2.Callback<GenericResponse>() {
                @Override
                public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                    utils.hideProgress();
                    GenericResponse genericResponse = response.body();
                    if (response.code() == 200) {
                        if (genericResponse != null) {
                            if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                                onLogout();
                            } else {
                                showToast(genericResponse.getMessage());
                            }
                        } else {
                            utils.showToast(getString(R.string.server_error));
                        }
                    } else {
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                    showToast(t.getMessage());
                }
            });
        }
    }

    @OnClick(R.id.ll_pin)
    public void onClickPin() {
        if (!isClicked) {
            isClicked = true;
            if (utils.isNetworkAvailable()) {
                changeActivity(ActivityPin.class);
            }
        }
    }

    @OnClick(R.id.ll_blockedlist)
    public void onClickBlockList() {
        if (!isClicked) {
            isClicked = true;
            if (utils.isNetworkAvailable()) {
                changeActivity(ActivityBlockList.class);
            }
        }
    }
    @OnClick(R.id.ll_cache)
    public void onClickcache() {
        if (!isClicked) {
            isClicked = true;
            utils.deleteCache(this);
            showToast("Cache Cleared");
        }
    }



    @OnClick(R.id.ll_privacy)
    public void onClickPrivacy() {
        if (!isClicked) {
            isClicked = true;
            if (utils.isNetworkAvailable()) {
                changeActivity(ActivityPrivacySettings.class);
            }
        }
    }

    @OnClick(R.id.ll_about_us)
    public void onClickAboutUs() {
        if (utils.isNetworkAvailable()) {
            if (!isClicked) {
                isClicked = true;
                Intent intent = new Intent(mActivity, ActivityWebView.class);
                intent.putExtra("title", "About Us");
                intent.putExtra("url", Constants_api.aboutUs);
                startActivity(intent);
            }
        }
    }

    @OnClick(R.id.ll_suggestions)
    public void onClickSuggestions() {
        if (!isClicked) {
            isClicked = true;
            if (utils.isNetworkAvailable()) {
                Intent intent = new Intent(mActivity, ActivityWebView.class);
                intent.putExtra("title", "Suggestions");
                intent.putExtra("url", Constants_api.suggestions + SessionUser.getUser().getUser_id());
                startActivity(intent);
            }
        }
    }

    @OnClick(R.id.ll_help)
    public void OnClickHelpClick() {
        if (!isClicked) {
            isClicked = true;
            if (utils.isNetworkAvailable()) {
                Intent intent = new Intent(mActivity, ActivityWebView.class);
                intent.putExtra("title", "Help and FAQ");
                intent.putExtra("url", Constants_api.helpAndFAQ);
                startActivity(intent);
            }
        }
    }

    public void onLogout() {
        SessionLogin.clearLoginSession();
        SessionUser.clearUserSession();
        mChatManager.logout();
        //BLiveApplication.getInstance().getmAgoraAPI().logout();
        BLiveApplication.getInstance().getGoogleSignInClient().signOut()
                .addOnCompleteListener(this, task -> {

                });
        finishAffinity();
        changeActivity(ActivitySignIn.class);
    }

    @Override
    public void onResume() {
        isClicked = false;
        super.onResume();
    }
}
