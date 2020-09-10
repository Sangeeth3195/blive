package com.blive.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import com.blive.agora.EngineConfig;
import com.blive.agora.MyEngineEventHandler;
import com.blive.agora.WorkerThread;
import com.blive.BLiveApplication;
import com.blive.constant.Constants_app;
import com.blive.R;
import com.blive.session.SessionLogin;
import com.blive.utils.Utils;
import com.crashlytics.android.Crashlytics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import io.agora.rtc.RtcEngine;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.blive.utils.Utils.makeLogTag;

public abstract class BaseActivity extends AppCompatActivity {

    private final static Logger log = LoggerFactory.getLogger(BaseActivity.class);
    protected static final String TAG = makeLogTag(BaseActivity.class);
    public Activity mActivity;
    public Utils utils;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = BaseActivity.this;
        utils = new Utils(mActivity);
        final View layout = findViewById(Window.ID_ANDROID_CONTENT);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                try{
                    initUI();
                }catch (Exception e) {
                    Crashlytics.logException(e);
                }
            }
        });
    }

    protected abstract void initUI();

    protected abstract void deInitUI();

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        try {
            super.setContentView(layoutResID);
            ButterKnife.bind(this);
        }catch (Exception r){
            r.printStackTrace();
        }
    }

    public void showToast(String mes) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast,
                null);

        TextView text = layout.findViewById(R.id.tv);
        text.setText(mes);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.FILL_HORIZONTAL|Gravity.BOTTOM, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);

        if (Build.VERSION.SDK_INT >= 21) {
            if(!isAppIsInBackground(mActivity))
                toast.show();
        } else {
            if(!isAppIsInBackground(mActivity))
                Toast.makeText(getApplicationContext(),mes,Toast.LENGTH_SHORT).show();
        }
    }
    public void showColorToast(String mes) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.color_toast,
                null);

        TextView text = layout.findViewById(R.id.tv);
        text.setText(mes);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.FILL_HORIZONTAL|Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);

        if (Build.VERSION.SDK_INT >= 21) {
            if(!isAppIsInBackground(mActivity))
                toast.show();
        } else {
            if(!isAppIsInBackground(mActivity))
                Toast.makeText(getApplicationContext(),mes,Toast.LENGTH_LONG).show();
        }
    }

    public void changeActivity(Class classname) {
        Intent intent = new Intent(mActivity, classname);
        startActivity(intent);
    }

    public void setStatusBarColor(int color){
        Window window = mActivity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(mActivity,color));
    }

    public void disableStatusBar(){
        if (Build.VERSION.SDK_INT >= 21) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    public void checkResponseCode(int code) {
        switch (code) {
            case 401:
                SessionLogin.clearLoginSession();
                Intent intent = new Intent(mActivity, ActivitySignIn.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
                break;
            case 500:
                showToast("Please try again Server Error!");
                break;
            default:
                showToast("Something went wrong Please try again!");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("");
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        assert am != null;
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : processInfo.pkgList) {
                    if (activeProcess.equals(context.getPackageName())) {
                        isInBackground = false;
                    }
                }
            }
        }

        return isInBackground;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        new Handler().postDelayed(() -> {
            if (isFinishing()) {
                return;
            }

            boolean checkPermissionResult = checkStoragePermission();

            if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M)) {
                // so far we do not use OnRequestPermissionsResultCallback
            }
        }, 500);
    }

    private boolean checkSelfPermissions() {
        return checkSelfPermission(Manifest.permission.RECORD_AUDIO, Constants_app.PERMISSION_REQ_ID_RECORD_AUDIO) &&
                checkSelfPermission(Manifest.permission.CAMERA, Constants_app.PERMISSION_REQ_ID_CAMERA) &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Constants_app.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE);
    }

    public boolean checkCamAndAudioPermission(){
        return checkSelfPermission(Manifest.permission.RECORD_AUDIO, Constants_app.PERMISSION_REQ_ID_RECORD_AUDIO) &&
                checkSelfPermission(Manifest.permission.CAMERA, Constants_app.PERMISSION_REQ_ID_CAMERA);
    }

    private boolean checkStoragePermission(){
        return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Constants_app.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected void onDestroy() {
        deInitUI();
        super.onDestroy();
    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }

        if (Manifest.permission.CAMERA.equals(permission)) {
            ((BLiveApplication) getApplication()).initWorkerThread();
        }
        return true;
    }


    protected RtcEngine rtcEngine() {
        return ((BLiveApplication) getApplication()).getWorkerThread().getRtcEngine();
    }

    protected final WorkerThread worker() {
        return ((BLiveApplication) getApplication()).getWorkerThread();
    }

    protected final EngineConfig config() {
        return ((BLiveApplication) getApplication()).getWorkerThread().getEngineConfig();
    }

    protected final MyEngineEventHandler event() {
        return ((BLiveApplication) getApplication()).getWorkerThread().eventHandler();
    }

    public final void showLongToast(final String msg) {
        this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.e(TAG,"onRequestPermissionsResult " + requestCode + " " + Arrays.toString(permissions) + " " + Arrays.toString(grantResults));
        switch (requestCode) {
            case Constants_app.PERMISSION_REQ_ID_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.CAMERA, Constants_app.PERMISSION_REQ_ID_CAMERA);
                } else {
                    finish();
                }
                break;
            }
            case Constants_app.PERMISSION_REQ_ID_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Constants_app.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE);
                    ((BLiveApplication) getApplication()).initWorkerThread();
                } else {
                    finish();
                }
                break;
            }
           /* case Constants_app.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION, Constants_app.PERMISSION_REQ_ID_FINE_LOCATION);
                } else {
                    finish();
                }
                break;
            }*/
        }
    }

    public void changeStatusBarColor(){
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black));
        }
    }
}
