package com.blive.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.blive.BLiveApplication;
import com.blive.R;
import com.blive.session.SessionLogin;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.OnClick;

public class ActivityNoInternet extends BaseBackActivity {

    @BindView(R.id.iv)
    ImageView iv;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        BLiveApplication.setCurrentActivity(this);
        setTitle("No Connectivity");
        hideBack();
        Glide.with(this)
                .load(R.drawable.nosignal)
                .into(iv);
    }

    @OnClick(R.id.rl_tryAgain)
    public void onClick() {
        if (isNetworkAvailable()) {
            if(SessionLogin.getLoginSession()){
                finishAffinity();
                changeActivity(ActivityHome.class);
            }else {
                finishAffinity();
                changeActivity(ActivitySignIn.class);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        showToast("Press BACK again to exit");
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        return isConnected;
    }

}