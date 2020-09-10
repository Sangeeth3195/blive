package com.blive.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.blive.BLiveApplication;
import com.blive.R;
import com.blive.chat.chatmodels.ChatUser;
import com.blive.chat.chatutil.ChatUtils;
import com.blive.model.User;
import com.blive.session.SessionLogin;
import com.blive.session.SessionUser;
import com.blive.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivitySplash extends AppCompatActivity {
    private Activity mActivity;
    public Utils utils;
    ImageView ivSplash;
    private ChatUtils helper;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initUI();
    }

    private void initUI() {
        helper = new ChatUtils(this);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        ivSplash = findViewById(R.id.iv_splash);
        Glide.with(this)
                .load(R.drawable.splash_logo_1)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true))
                .into(ivSplash);
        Utils utils = new Utils(this);
        //Log.e("DeviceID: ", utils.getDeviceId(ActivitySplash.this));
        if (utils.isNetworkAvailable()) {
            mActivity = ActivitySplash.this;
            new Handler().postDelayed(this::moveToNextScreen, 3 * 1000); // wait for 3 seconds
        } else
            finish();
    }

    private void moveToNextScreen() {
        try {
            Utils utils = new Utils(this);
            if (utils.isNetworkAvailable()) {
                if (SessionLogin.getLoginSession()) {
                    if (getIntent().getExtras() != null) {
                        if (getIntent().getExtras().containsKey("userId")) {
                            createUser(1);
                        } else if (getIntent().getExtras().containsKey("message")) {
                            //Log.e(TAG, "NotificationReceived: ");
                        } else if (getIntent().getExtras().containsKey("_fbSourceApplicationHasBeenSet")) {
                            createUser(2);
                        } else {
                            Intent intent = new Intent(mActivity, ActivitySkip.class);
                            intent.putExtra("from", "splash");
                            finish();
                            startActivity(intent);
                        }
                    }
                } else {
                    Intent i = new Intent(mActivity, ActivitySignIn.class);
                    finish();
                    startActivity(i);
                }
            }
        } catch (Exception e) {
            Intent i = new Intent(mActivity, ActivitySignIn.class);
            finish();
            startActivity(i);
            e.printStackTrace();
        }
    }


    private void createUser(int i) {
        User newUser = new SessionUser().getUserData();
        ChatUser chatUser = new ChatUser(newUser.getUser_id(), newUser.getUser_id(), (newUser.getProfile_pic() != null ? newUser.getProfile_pic() : ""),
                newUser.getName(), System.currentTimeMillis());
        BLiveApplication.getUserRef().child(newUser.getUser_id()).setValue(chatUser).addOnSuccessListener(aVoid -> {
            helper.setLoggedInUser(chatUser);
            if (i == 1) {
                String value = getIntent().getExtras().getString("userId");
                Intent resultIntent = new Intent(mActivity, ActivityHome.class);
                resultIntent.putExtra("from", "splash");
                resultIntent.putExtra("userID", value);
                startActivity(resultIntent);
            } else if (i == 2) {
                Intent intent = new Intent(mActivity, ActivityHome.class);
                intent.putExtra("from", "splash");
                finish();
                startActivity(intent);
            }

        }).addOnFailureListener(e ->
                Toast.makeText(ActivitySplash.this, "Something went wrong, unable to create user.", Toast.LENGTH_LONG).show());
    }
}