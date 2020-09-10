package com.blive.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;


import com.blive.BLiveApplication;
import com.blive.R;
import com.blive.activity.ActivitySignIn;
import com.blive.agora.EngineConfig;
import com.blive.agora.MyEngineEventHandler;
import com.blive.agora.WorkerThread;
import com.blive.session.SessionLogin;
import com.blive.utils.Utils;
import com.crashlytics.android.Crashlytics;

import java.util.Objects;

import butterknife.ButterKnife;
import io.agora.rtc.RtcEngine;

import static com.blive.utils.Utils.makeLogTag;


public abstract class BaseFragment extends Fragment {

    public Activity mActivity;
    public Utils utils;
    protected static String TAG = "";

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = getActivity();
        utils = new Utils(mActivity);
        TAG = mActivity.getClass().getSimpleName();
        ButterKnife.bind(this, view);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected void changeActivity(Class clz) {
        Intent i = new Intent(getActivity(), clz);
        Objects.requireNonNull(getActivity()).startActivity(i);
    }

    protected void changeActivity(Class clz, Bundle bundle) {
        Intent i = new Intent(getActivity(), clz);
        i.putExtras(bundle);
        Objects.requireNonNull(getActivity()).startActivity(i);
    }

    protected void finish() {
        Objects.requireNonNull(getActivity()).finish();
    }

    protected void showKeyBoard(View view) {
        final InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    protected void hideKeyBoard(View view) {
        final InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showToast(String message) {
        try{
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast,
                    null);

            TextView text = layout.findViewById(R.id.tv);
            text.setText(message);

            Toast toast = new Toast(mActivity);
            toast.setGravity(Gravity.BOTTOM, 0, 50);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        } catch (Exception e) {
            Crashlytics.logException(e);
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

    protected RtcEngine rtcEngine() {
        return ((BLiveApplication) getActivity().getApplication()).getWorkerThread().getRtcEngine();
    }

    protected final WorkerThread worker() {
        return ((BLiveApplication) getActivity().getApplication()).getWorkerThread();
    }

    protected final EngineConfig config() {
        return ((BLiveApplication) getActivity().getApplication()).getWorkerThread().getEngineConfig();
    }

    protected final MyEngineEventHandler event() {
        return ((BLiveApplication) getActivity().getApplication()).getWorkerThread().eventHandler();
    }

}