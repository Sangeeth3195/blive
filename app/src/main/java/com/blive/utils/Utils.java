package com.blive.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import com.afollestad.materialdialogs.MaterialDialog;
import com.blive.activity.ActivityNoInternet;
import com.blive.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.util.Date;

import static com.blive.activity.BaseActivity.isAppIsInBackground;
import static com.facebook.FacebookSdk.getApplicationContext;


public class Utils {
    MaterialDialog progressDialog;
    private Activity mActivity;
    private AlertDialog alertDialog;
    private static final String LOG_PREFIX = "blive";
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    private static final int MAX_LOG_TAG_LENGTH = 23;

    public Utils(Activity activity) {
        this.mActivity = activity;
    }

    public static boolean isEmpty(String text) {
        return text == null || text.equals("") || text.matches(" *");
    }

    @SuppressLint("HardwareIds")
    public String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String makeLogTag(Class cls) {
        return makeLogTag(cls.getSimpleName());
    }

    public static String makeLogTag(String str) {
        if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
        }

        return LOG_PREFIX + str;
    }

    public static Point getDisplaySize(WindowManager windowManager) {
        try {
            Display display = windowManager.getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getMetrics(displayMetrics);
            return new Point(displayMetrics.widthPixels, displayMetrics.heightPixels);
        } catch (Exception e) {
            e.printStackTrace();
            return new Point(0, 0);
        }
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static void alertBox(Context context, String msg) {
        new MaterialDialog.Builder(context)
                .content(msg)
                .backgroundColorRes(R.color.white)
                .contentColorRes(R.color.black)
                .positiveText("OK")
                .show();
    }

    public static void alertBox(Context context, int id) {
        new MaterialDialog.Builder(context)
                .content(context.getResources().getString(id))
                .backgroundColorRes(R.color.white)
                .contentColorRes(R.color.black)

                .positiveText("OK")
                .show();
    }

    public void showProgress() {
        LayoutInflater inflater = mActivity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.loading, null);
        alertDialog = new AlertDialog.Builder(mActivity, R.style.Dialog).create();
        alertDialog.setView(view);

        AVLoadingIndicatorView loading = view.findViewById(R.id.avi);
        loading.show();
        alertDialog.show();
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(false);
    }

    public void showProgressText(String message) {
        progressDialog = new MaterialDialog.Builder(mActivity)
                .backgroundColorRes(R.color.white)
                .contentColorRes(R.color.black)
                .content(message)
                .progress(true, 0)
                .show();
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public void hideProgress() {
        if (alertDialog != null) {
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        }
    }

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void showToast(String mes) {
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast,
                null);

        TextView text = layout.findViewById(R.id.tv);
        text.setText(mes);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);

        if (Build.VERSION.SDK_INT >= 21) {
            if (!isAppIsInBackground(mActivity))
                toast.show();
        } else {
            if (!isAppIsInBackground(mActivity))
                Toast.makeText(getApplicationContext(), mes, Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        if (isConnected)
            return true;
        else {
            Intent intent = new Intent(mActivity, ActivityNoInternet.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            mActivity.startActivity(intent);
            return false;
        }
    }

    @SuppressLint("HardwareIds")
    public String getDeviceIMEI() {
        String deviceUniqueIdentifier = null;
        deviceUniqueIdentifier = Settings.Secure.getString(mActivity.getContentResolver(), Settings.Secure.ANDROID_ID);
        return deviceUniqueIdentifier;
    }

    /*public static String getDecodedImage(String image) {
        String decodedImage = null;
        try {
            decodedImage = URLDecoder.decode(image, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decodedImage;
    }*/

    public void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public static int getTimestamp() {
        return (int)((new Date().getTime())/1000);
    }

}

