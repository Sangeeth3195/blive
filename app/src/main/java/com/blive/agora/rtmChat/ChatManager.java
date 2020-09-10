package com.blive.agora.rtmChat;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;

import com.blive.BLiveApplication;
import com.blive.BuildConfig;
import com.blive.session.SessionUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmChannel;
import io.agora.rtm.RtmChannelAttribute;
import io.agora.rtm.RtmChannelListener;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmMessage;

public class ChatManager {
    private static final String TAG = ChatManager.class.getSimpleName();
    private Context mContext;
    private RtmClient mRtmClient;
    private RtmChannel mRtmChannel;
    private List<RtmClientListener> mListenerList = new ArrayList<>();
    private final List<ChatHandler> mChatHandlerList = new ArrayList<>();

    public ChatManager(Context context) {
        mContext = context;
    }

    public void init() {
        String appID = BuildConfig.private_app_id;
        try {
            mRtmClient = RtmClient.createInstance(mContext, appID, new RtmClientListener() {
                @Override
                public void onConnectionStateChanged(int state, int reason) {
                    Log.e(TAG, "onConnectionStateChanged: " + state + " " + reason);
                    for (RtmClientListener listener : mListenerList) {
                        listener.onConnectionStateChanged(state, reason);
                    }
                }

                @Override
                public void onMessageReceived(RtmMessage rtmMessage, String peerId) {
                    try {
                        Log.e(TAG, "onRTMMessageReceived: " + rtmMessage.getText().toString() + peerId);
                        for (RtmClientListener listener : mListenerList) {
                            listener.onMessageReceived(rtmMessage, peerId);
                        }
                        String message = rtmMessage.getText().toString();
                        Log.d(TAG, "onMessageReceived: " + message);
                        if (message.contains("PKRequest")) {
                            Log.d(TAG, "PK: ");
                            ActivityManager am = (ActivityManager) BLiveApplication.getCurrentActivity().getSystemService(Context.ACTIVITY_SERVICE);
                            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                            Intent intent = new Intent("pkGuestrequest");
                            intent.putExtra("data", message);
                            Log.d("NotifTestApp:", "sending broadcast" + intent.toString());
                            LocalBroadcastManager.getInstance(BLiveApplication.getStaticContext()).sendBroadcast(intent);
                        } else if (message.contains("pkGuestAccept")) {
                            Log.d(TAG, "PK: ");
                            ActivityManager am = (ActivityManager) BLiveApplication.getCurrentActivity().getSystemService(Context.ACTIVITY_SERVICE);
                            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                            Log.i("autolog", "cn: " + cn);
                            Intent intent = new Intent("pkGuestAccept");
                            intent.putExtra("data", message);
                            Log.d("NotifTestApp:", "sending broadcast" + intent.toString());
                            LocalBroadcastManager.getInstance(BLiveApplication.getStaticContext()).sendBroadcast(intent);
                        } else if (message.contains("PK_REJECTED")) {
                            Log.d(TAG, "PK: ");
                            ActivityManager am = (ActivityManager) BLiveApplication.getCurrentActivity().getSystemService(Context.ACTIVITY_SERVICE);
                            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                            Log.i("autolog", "cn: " + cn);
                            Intent intent = new Intent("pkReject");
                            Log.d("NotifTestApp:", "sending broadcast" + intent.toString());
                            LocalBroadcastManager.getInstance(BLiveApplication.getStaticContext()).sendBroadcast(intent);
                        } else if (message.contains("PK_MESSAGE")) {
                            Log.d(TAG, "PK: ");
                            ActivityManager am = (ActivityManager) BLiveApplication.getCurrentActivity().getSystemService(Context.ACTIVITY_SERVICE);
                            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                            Log.i("autolog", "cn: " + cn);
                            Intent intent = new Intent("PK_MESSAGE");
                            intent.putExtra("data", message);
                            intent.putExtra("from", peerId);
                            Log.d("NotifTestApp:", "sending broadcast" + intent.toString());
                            LocalBroadcastManager.getInstance(BLiveApplication.getStaticContext()).sendBroadcast(intent);
                        }

                    } catch (Exception e) {
                        Log.i("autolog", "Exception: " + e.toString());

                    }
                }

                @Override
                public void onTokenExpired() {
                    Log.e(TAG, "onTokenExpired: ");
                }

                @Override
                public void onPeersOnlineStatusChanged(Map<String, Integer> map) {

                }

            });
            /*if (BuildConfig.DEBUG) {
                mRtmClient.setParameters("{\"rtm.log_filter\": 65535}");
            }*/
        } catch (Exception e) {
            Log.i("autolog", "Exception: " + e.toString());
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtm sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    public void doLogin() {
        /*RtmTokenBuilder token = new RtmTokenBuilder(appId, appCertificate, SessionUser.getUser().getUsername());
        token.setPrivilege(AccessToken.Privileges.kRtmLogin, expireTimestamp);
        String result = null;
        try {
            result = token.buildToken();
            Log.e(TAG, "doLogin: "+result );
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(result);*/
        mRtmClient.login(null, SessionUser.getUser().getUsername().trim(), new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                Log.e(TAG, "RTM login success 1");
                SessionUser.isRtmLoggedIn(true);
                for (ChatHandler listener : mChatHandlerList) {
                    listener.onLoginSuccess();
                }
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                Log.e(TAG, "login failed: " + errorInfo.getErrorCode());
                Log.e(TAG, "login failed: " + errorInfo.getErrorDescription());
                SessionUser.isRtmLoggedIn(false);
            }
        });
    }

    public void doLogin(String mChannelName) {
        Log.i("autolog-fromlive--chat", "mChannelName: " + mChannelName);
        try {
            mRtmClient.login(null, SessionUser.getUser().getUsername().trim(), new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void responseInfo) {
                    Log.e(TAG, "RTM login success 2");
                    SessionUser.isRtmLoggedIn(true);
                    for (ChatHandler listener : mChatHandlerList) {
                        listener.onLoginSuccess();
                    }
                    createChannel(mChannelName);
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    Log.e(TAG, "RTM login failed: " + errorInfo.getErrorCode());
                    Log.e(TAG, "RTM login failed: " + errorInfo.getErrorDescription());
                    if (errorInfo.getErrorCode() != 8)
                        SessionUser.isRtmLoggedIn(false);
                    for (ChatHandler listener : mChatHandlerList) {
                        listener.onLoginFailed(errorInfo);
                    }
                    if (errorInfo.getErrorCode()==8){
                        logout();
                        doLogin(mChannelName);
                    }
                }
            });

        } catch (Exception e) {
            Log.i("autolog", "e: " + e.toString());
            Log.i("autolog", "e: " + e);

        }

    }

    public void logout() {
        mRtmClient.logout(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "onRtmLogoutSuccess: ");
                SessionUser.isRtmLoggedIn(false);
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
            }
        });
    }

    public void createChannel(String mChannelName) {
        Log.e(TAG, "RTM createChannel: " + mChannelName);
        leaveChannel();
        try {
            mRtmChannel = mRtmClient.createChannel(mChannelName, new RtmChannelListener() {
                @Override
                public void onMemberCountUpdated(int i) {

                }

                @Override
                public void onAttributesUpdated(List<RtmChannelAttribute> list) {

                }

                @Override
                public void onMessageReceived(RtmMessage rtmMessage, RtmChannelMember rtmChannelMember) {
                    Log.e(TAG, "onMessageReceived: " + rtmMessage.getText());
                    for (ChatHandler listener : mChatHandlerList) {
                        listener.onMessageReceived(rtmMessage, rtmChannelMember);
                    }
                }

                @Override
                public void onMemberJoined(RtmChannelMember rtmChannelMember) {
                    Log.e(TAG, "onRTMMemberJoined: ");
                    for (ChatHandler listener : mChatHandlerList) {
                        listener.onMemberJoined(rtmChannelMember);
                    }
                }

                @Override
                public void onMemberLeft(RtmChannelMember rtmChannelMember) {
                    Log.e(TAG, "onRTMMemberLeft: ");
                    for (ChatHandler listener : mChatHandlerList) {
                        listener.onMemberLeft(rtmChannelMember);
                    }
                }
            });
        } catch (Exception e) {
            Log.i("autolog", "e: " + e);
        }
        if (mRtmChannel == null) {
            Log.e(TAG, "joinChannel: " + null);
            return;
        }
        // join the channel
        mRtmChannel.join(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                Log.e(TAG, "RTM channel join Success: ");
                for (ChatHandler listener : mChatHandlerList) {
                    Log.e(TAG, "onSuccess: Channel Joining ");
                    listener.onChannelJoinSuccess();
                }
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                Log.e(TAG, "RTM join channel failed " + errorInfo.getErrorCode());
                for (ChatHandler listener : mChatHandlerList) {
                    listener.onChannelJoinFailed(errorInfo);
                }
            }
        });
    }

    public void leaveChannel() {
        if (mRtmChannel != null) {
            mRtmChannel.leave(null);
            mRtmChannel.release();
            mRtmChannel = null;
        }
    }

    public RtmClient getRtmClient() {
        return mRtmClient;
    }

    public RtmChannel getRtmChannel() {
        return mRtmChannel;
    }

    public void registerListener(RtmClientListener listener) {
        mListenerList.add(listener);
    }

    public void unregisterListener(RtmClientListener listener) {
        mListenerList.remove(listener);
    }

    public void addChantHandler(ChatHandler handler) {
        this.mChatHandlerList.add(handler);
    }

    public void removeChatHandler(ChatHandler handler) {
        this.mChatHandlerList.remove(handler);
    }
}