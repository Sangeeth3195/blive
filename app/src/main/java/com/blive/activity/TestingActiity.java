package com.blive.activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.blive.BLiveApplication;
import com.blive.BuildConfig;
import com.blive.R;
import com.blive.agora.AGEventHandler;
import com.blive.agora.WorkerThread;
import com.blive.constant.Constants_app;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

/**
 * Created by sans on 13-Nov-19.
 **/

public class TestingActiity extends BaseActivity {
    FrameLayout videoView;
    WorkerThread workerThread;
    RtcEngine mRtcEngine;

    @Override
    protected void initUI() {
        videoView = findViewById(R.id.videoView);
        BLiveApplication.setCurrentActivity(this);
        initializeEngine();
        setupRemoteVideo(19009);
    }

    private void initEngineAndJoinChannel() {
    }

    @Override
    protected void deInitUI() {

    }

    private void setupRemoteVideo(int uid) {
        Log.i("autolog", "uid: " + uid);

        // Create a SurfaceView object.

        SurfaceView mRemoteView;
        videoView.removeAllViews();


        mRemoteView = RtcEngine.CreateRendererView(getBaseContext());
        videoView.addView(mRemoteView);
        // Set the remote video view.
        rtcEngine().setupRemoteVideo(new VideoCanvas(mRemoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid));

    }

    private void doConfigEngine(int cRole) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int prefIndex = pref.getInt(Constants_app.PrefManager.PREF_PROPERTY_PROFILE_IDX, Constants_app.DEFAULT_PROFILE_IDX);
        if (prefIndex > Constants_app.VIDEO_PROFILES.length - 1) {
            prefIndex = Constants_app.DEFAULT_PROFILE_IDX;
        }
        int vProfile = Constants_app.VIDEO_PROFILES[prefIndex];

        worker().configEngine1(cRole, vProfile);
    }


    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // Listen for the onJoinChannelSuccess callback.
        // This callback occurs when the local user successfully joins the channel.
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("agora", "Join channel success, uid: " + (uid & 0xFFFFFFFFL));
                }
            });
        }

        @Override
        // Listen for the onFirstRemoteVideoDecoded callback.
        // This callback occurs when the first video frame of the broadcaster is received and decoded after the broadcaster successfully joins the channel.
        // You can call the setupRemoteVideo method in this callback to set up the remote video view.
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("agora", "First remote video decoded, uid: " + (uid & 0xFFFFFFFFL));
                    setupRemoteVideo(uid);
                }
            });
        }

        @Override
        // Listen for the onUserOffline callback.
        // This callback occurs when the broadcaster leaves the channel or drops offline.
        public void onUserOffline(final int uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("agora", "User offline, uid: " + (uid & 0xFFFFFFFFL));

                }
            });
        }
    };

    private void initializeEngine() {
        try {
            Log.d("autolog", "initializeEngine: ");
            String appId = BuildConfig.private_app_id;
            mRtcEngine = RtcEngine.create(getBaseContext(), appId, mRtcEventHandler);
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            mRtcEngine.setClientRole(2);
            mRtcEngine.joinChannel(null, "SJHInfotech", "Blive", 19010);


        } catch (Exception e) {
            Log.i("autolog", "e: " + e);
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RtcEngine.destroy();
        mRtcEngine.leaveChannel();

    }
}
