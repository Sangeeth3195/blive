package com.blive.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.blive.R;
import com.blive.session.SessionLogin;
import com.blive.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.blive.utils.Utils.makeLogTag;

public class BaseBackActivity extends AppCompatActivity {

    protected static final String TAG = makeLogTag(BaseBackActivity.class);
    public Activity mActivity;
    public Utils utils;

    @Nullable
    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    @Nullable
    @BindView(R.id.rl_skip)
    RelativeLayout rlSkip;
    @Nullable
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = BaseBackActivity.this;
        utils = new Utils(mActivity);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        try {
            super.setContentView(layoutResID);
            ButterKnife.bind(this);
        } catch (Exception r) {
            r.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        utils.deleteCache(mActivity);
        super.onDestroy();
    }

    public void showToast(String mes) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast, null);

        TextView text = layout.findViewById(R.id.tv);
        text.setText(mes);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        if (!isAppIsInBackground(mActivity))
            toast.show();
    }

    public void changeActivity(Class classname) {
        Intent intent = new Intent(mActivity, classname);
        startActivity(intent);
    }

    public void changeActivity(Class clz, Bundle bundle) {
        Intent i = new Intent(mActivity, clz);
        i.putExtras(bundle);
        startActivity(i);
    }

    public void setTitle(String title) {
        assert tvTitle != null;
        tvTitle.setText(title);
    }

    @OnClick(R.id.rl_back)
    public void onClickBack() {
        finish();
    }

    public void hideBack() {
        assert rlBack != null;
        rlBack.setVisibility(View.INVISIBLE);
    }

    public void showSkip() {
        assert rlSkip != null;
        rlSkip.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.rl_skip)
    public void onClickSkip(){
        finish();
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

    public void changeStatusBarColor(){
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black));
        }
    }
}