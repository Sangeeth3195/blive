package com.blive.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.blive.BLiveApplication;
import com.blive.R;
import com.blive.agora.rtmChat.ChatHandler;
import com.blive.agora.rtmChat.ChatManager;
import com.blive.session.SessionLogin;
import com.blive.session.SessionUser;

import butterknife.BindView;

/**
 * Created by sans on 07-01-2019.
 **/

public class ActivityBlocked extends BaseBackActivity {

    @BindView(R.id.tv)
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked);
        initUI();
    }

    private void initUI() {
        hideBack();
        setTitle("Blocked");
        ChatManager mChatManager = BLiveApplication.getInstance().getChatManager();
        mChatManager.leaveChannel();
        mChatManager.logout();
        tv.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto","support@blive24hrs.com", null));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Support");
            intent.putExtra(Intent.EXTRA_TEXT, "");
            startActivity(Intent.createChooser(intent, "Choose an Email client :"));
        });
        try{
            ((BLiveApplication) getApplication()).deInitWorkerThread();
        }catch (Exception e){
            Log.e(TAG, "initUI: "+e );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(SessionLogin.getLoginSession()){
            SessionLogin.clearLoginSession();
            SessionUser.clearUserSession();
        }
    }

    public void onClickedExit(View view) {
        if(SessionLogin.getLoginSession()){
            SessionLogin.clearLoginSession();
            SessionUser.clearUserSession();
        }
        finishAffinity();
    }
}
