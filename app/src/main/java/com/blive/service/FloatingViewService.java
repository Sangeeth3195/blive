package com.blive.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.blive.activity.ActivityBlocked;
import com.blive.activity.ActivityScreenSharing;
import com.blive.activity.ActivityStreamDetails;
import com.blive.adapter.AdapterMessage;
import com.blive.agora.WorkerThread;
import com.blive.BLiveApplication;
import com.blive.agora.rtmChat.ChatHandler;
import com.blive.agora.rtmChat.ChatManager;
import com.blive.constant.Constants_app;
import com.blive.model.GenericResponse;
import com.blive.model.GiftMessage;
import com.blive.model.MessageBean;
import com.blive.R;
import com.blive.session.SessionUser;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmChannel;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmMessage;
import retrofit2.Call;
import retrofit2.Response;

import static com.blive.BLiveApplication.TAG;

public class FloatingViewService extends Service {

    private static final String CHANNEL_ID = "BLiveApplication";
    private WindowManager mWindowManager;
    private View mFloatingView, collapsedView;
    private WindowManager.LayoutParams params;
    private RelativeLayout rlMessage;
    private LinearLayout llMmessage;
    private LinearLayout llMessage;
    private ImageView closeButton, openButton, ivCallStop, ivVideoMute;
    private EditText userMessageBox;
    private ImageButton sendButton;
    private RecyclerView rvMessages;
    private LinearLayout llChat;
    private List<MessageBean> messageBeanList;
    private AdapterMessage adapter;
    private String level = "", selfName = "", channelName = "", time = "", input ="";
    private int channelUserCount, viewers = 0, likes = 0, gold = 0, oldGold = 0, muted = 0,floatingYPostion= 0,floatingXposition=0;
    private ArrayList<GiftMessage> giftMessages;
    private ArrayList<String> messagesList;
    private boolean isVideoMute = false,isKeyBoardEnabled = true;
    private long startTime, endTime;
    Handler handler = new Handler(Looper.getMainLooper());
    private SharedPreferences sharedPreferences;
    private RtmClient mRtmClient;
    private ChatManager mChatManager;
    private RtmChannel mRtmChannel;
    private ChatHandler mChatHandler;

    public FloatingViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try{
            input = intent.getStringExtra("inputExtra");
        } catch (Exception e) {
            Log.e("ErrorOccurred", e.getMessage());
            Crashlytics.logException(e);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Intent notificationIntent = new Intent(this, ActivityScreenSharing.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, 0);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("BLive Application")
                    .setContentText(input)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(1, notification);
        }

        selfName = intent.getStringExtra("selfname");
        channelName = intent.getStringExtra("channelName");
        startTime = intent.getLongExtra("startTime", 0);
        muted = intent.getIntExtra("mute", 0);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Inflate the floating view layout we created
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);

        //do heavy work on a background thread
        //stopSelf();

        isKeyBoardEnabled = true;
        enableKeyPadInput(isKeyBoardEnabled);

        getLevel();

        //Specify the view position
        params.gravity = Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        giftMessages = new ArrayList<>();
        messagesList = new ArrayList<>();

        collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        rlMessage = mFloatingView.findViewById(R.id.rl_message);
        rvMessages = mFloatingView.findViewById(R.id.rv_messages);
        llMessage = mFloatingView.findViewById(R.id.ll_message);
        llChat = mFloatingView.findViewById(R.id.ll_chat);
        userMessageBox = mFloatingView.findViewById(R.id.et_message);
        sendButton = mFloatingView.findViewById(R.id.sendButton);
        openButton = mFloatingView.findViewById(R.id.open_button);
        ivCallStop = mFloatingView.findViewById(R.id.iv_callend);
        ivVideoMute = mFloatingView.findViewById(R.id.iv_videoMute);
        closeButton = mFloatingView.findViewById(R.id.close_button);
        collapsedView.setVisibility(View.VISIBLE);

        mChatManager = BLiveApplication.getInstance().getChatManager();
        mRtmClient = mChatManager.getRtmClient();
        mRtmChannel = mChatManager.getRtmChannel();
        mChatHandler = new ChatHandler() {
            @Override
            public void onLoginSuccess() {
                Log.e(TAG, "onLoginSuccess: " );
            }

            @Override
            public void onLoginFailed(ErrorInfo errorInfo) {
                Log.e(TAG, "onLoginFailed: " );
            }

            @Override
            public void onChannelJoinSuccess() {
                Log.e(TAG, "onChannelJoinSuccess: " );
            }

            @Override
            public void onChannelJoinFailed(ErrorInfo errorCode) {
                Log.e(TAG, "onChannelJoinFailed: " );
            }

            @Override
            public void onMessageReceived(RtmMessage message, RtmChannelMember fromMember) {
                handler.post(() -> onMessageReceive(message,fromMember));
            }

            @Override
            public void onMemberJoined(RtmChannelMember rtmChannelMember) {

            }

            @Override
            public void onMemberLeft(RtmChannelMember rtmChannelMember) {

            }
        };

        mChatManager.addChantHandler(mChatHandler);

        LinearLayoutManager layoutManagers = new LinearLayoutManager(this);
        layoutManagers.setOrientation(OrientationHelper.VERTICAL);
        rvMessages.setLayoutManager(layoutManagers);

        sharedPreferences = getApplicationContext().getSharedPreferences("VideoMute", MODE_PRIVATE);
        String video = sharedPreferences.getString("isVideoMute", "");

        if(video.equalsIgnoreCase("true")){
            isVideoMute = true;
            ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.play));
        }else if(video.equalsIgnoreCase("false")){
            ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.pause));
        }

        openButton.setOnClickListener(v -> {
            //Open the application  click.
            Intent bringToForegroundIntent = new Intent(FloatingViewService.this, ActivityScreenSharing.class);
            bringToForegroundIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(bringToForegroundIntent);
            //close the service and remove view from the view hierarchy
        });

        ivVideoMute.setOnClickListener(v -> {
            if (!isVideoMute) {
                isVideoMute = true;
                if (muted == 0)
                    worker().getRtcEngine().muteLocalAudioStream(true);
                    worker().getRtcEngine().muteAllRemoteAudioStreams(true);

                sharedPreferences = getApplicationContext().getSharedPreferences("VideoMute", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("isVideoMute", "true");
                editor.commit();

                sendChannelMessage( SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " Video Muted");
                ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.play));
                Toast.makeText(FloatingViewService.this, SessionUser.getUser().getName() + " " + "Video Muted ", Toast.LENGTH_SHORT).show();
            } else {
                isVideoMute = false;
                if (muted == 0) {
                    worker().getRtcEngine().muteLocalAudioStream(false);
                } else {
                    worker().getRtcEngine().muteLocalAudioStream(true);
                }

                sharedPreferences = getApplicationContext().getSharedPreferences("VideoMute", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("isVideoMute", "false");
                editor.commit();

                sendChannelMessage( SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " Video UnMuted");
                ivVideoMute.setBackground(getResources().getDrawable(R.mipmap.pause));
                Toast.makeText(FloatingViewService.this, SessionUser.getUser().getName() + " " + "Video UnMuted ", Toast.LENGTH_SHORT).show();
            }
        });

        ivCallStop.setOnClickListener(v -> callStatusAPI());

        sendButton.setOnClickListener(sendButtonListener);

        userMessageBox.setOnFocusChangeListener((v, hasFocus) -> {
            userMessageBox.requestFocus();
            showSoftKeyboard(v);
        });

        closeButton.setOnClickListener(view -> {
            isKeyBoardEnabled = false;
            enableKeyPadInput(isKeyBoardEnabled);
            collapsedView.setVisibility(View.VISIBLE);
            rlMessage.setVisibility(View.GONE);
            llChat.setVisibility(View.GONE);
            llMessage.setVisibility(View.GONE);
        });

        messageBeanList = new ArrayList<>();
        adapter = new AdapterMessage(this, messageBeanList);
        rvMessages.setAdapter(adapter);

        MessageBean messageBean = new MessageBean(selfName, getResources().getString(R.string.warning), true, true,false);
        messageBeanList.add(messageBean);
        adapter.notifyItemRangeChanged(messageBeanList.size(

        ), 1);
        rvMessages.scrollToPosition(messageBeanList.size() - 1);

        //Drag and move floating view using user's touch action.
        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);

                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {
                                //When user clicks on the image view of the collapsed layout,
                                //visibility of the collapsed layout will be changed to "View.GONE"
                                //and expanded view will become visible.
                                collapsedView.setVisibility(View.GONE);
                                rlMessage.setVisibility(View.VISIBLE);
                                llChat.setVisibility(View.VISIBLE);
                                llMessage.setVisibility(View.VISIBLE);
                            }
                        }

                        if(Ydiff == 0){
                            if(!isKeyBoardEnabled){
                                isKeyBoardEnabled = true;
                                enableKeyPadInput(isKeyBoardEnabled);
                                collapsedView.setVisibility(View.GONE);
                                rlMessage.setVisibility(View.VISIBLE);
                                llChat.setVisibility(View.VISIBLE);
                                llMessage.setVisibility(View.VISIBLE);
                            }
                        }
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        floatingXposition = initialX + (int) (event.getRawX() - initialTouchX);;
                        floatingYPostion =initialY + (int) (event.getRawY() - initialTouchY);
                        //Update the layout with new X & Y coordinate

                        if(params.x == 0 || params.y == 0){
                            if(!isKeyBoardEnabled){
                                isKeyBoardEnabled = true;
                                enableKeyPadInput(isKeyBoardEnabled);
                                collapsedView.setVisibility(View.GONE);
                                rlMessage.setVisibility(View.VISIBLE);
                                llChat.setVisibility(View.VISIBLE);
                                llMessage.setVisibility(View.VISIBLE);
                            }
                        }
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;

                }
                return false;
            }


        });

        mFloatingView.findViewById(R.id.root_container).setOnClickListener(v -> {

            Log.e("ClickedFloating","");
            if(!isKeyBoardEnabled){
                isKeyBoardEnabled = true;
                enableKeyPadInput(isKeyBoardEnabled);
                collapsedView.setVisibility(View.GONE);
                rlMessage.setVisibility(View.VISIBLE);
                llChat.setVisibility(View.VISIBLE);
                llMessage.setVisibility(View.VISIBLE);
            }
        });
    }

    private void enableKeyPadInput(Boolean enable) {
        if (enable) {
            Log.e("enableKeyPadInput", "true " + enable);
            try {
                mWindowManager.removeViewImmediate(mFloatingView);
            } catch (Exception e) {
                Log.e("enableKeyPadInput ", "error " + e.getMessage());
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                Log.e("enableKeyPadInput", "true " + enable);
                //Add the view to the window.
                params = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                        PixelFormat.TRANSLUCENT);
            } else {
                //Add the view to the window.
                params = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                        PixelFormat.TRANSLUCENT);
            }
            params.x = floatingXposition;
            params.y = floatingYPostion;
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(mFloatingView, params);
        } else {
            Log.e("enableKeyPadInput", "false " + enable);
            mWindowManager.removeViewImmediate(mFloatingView);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                Log.e("enableKeyPadInput", "false " + enable);
                //Add the view to the window.
                params = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
            } else {
                //Add the view to the window.
                params = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
            }
            params.x = floatingXposition;
            params.y = floatingYPostion;
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(mFloatingView, params);
        }
    }

    private View.OnClickListener sendButtonListener = v -> {
        try {
            getLevel();
            String msg = userMessageBox.getText().toString();
            msg = msg.trim();
            if (msg.length() > 0) {
                MessageBean messageBean = new MessageBean(selfName, SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " : " + msg, true, false,false);
                messageBeanList.add(messageBean);
                adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                rvMessages.scrollToPosition(messageBeanList.size() - 1);
                sendChannelMessage( SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " : " + msg);
                userMessageBox.setText("");
            } else {
                Toast.makeText(this, "Can't send Empty Message", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "onClick: " + e);
            Crashlytics.logException(e);
        }
    };

    /**
     * API CALLBACK: rtm channel event listener
     */
    private void onMessageReceive(final RtmMessage text, final RtmChannelMember fromMember) {
        handler.post(() -> {
            String account = fromMember.getUserId();
            String msg = text.getText();
            Log.e(TAG, "onMessageReceived account = " + account + " msg = " + msg);
            MessageBean messageBean;
            if (!account.equals(selfName)) {
                if (msg.contains("has arrived")) {
                    viewers = viewers + 1;
                }else if(msg.contains("AdMIn hA$ bLoCkEd yOu tEmPoRAriLy")){
                    stopService(new Intent(BLiveApplication.getStaticContext(), FloatingViewService.class));
                    Intent intent = new Intent(BLiveApplication.getCurrentActivity(), ActivityBlocked.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    BLiveApplication.getCurrentActivity().finish();
                    BLiveApplication.getCurrentActivity().startActivity(intent);
                }else if (msg.contains(": Has sent gIfTsEnTtOyOU")) {
                    String message = msg;
                    message = message.replace("gIfTsEnTtOyOU","");

                    messageBean = new MessageBean(account, message, false, false,true);
                    messageBeanList.add(messageBean);
                    adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                    rvMessages.scrollToPosition(messageBeanList.size() - 1);

                    GiftMessage giftMessage = new GiftMessage();
                    giftMessage.setAccount(account);
                    giftMessage.setMessage(message);
                    giftMessages.add(giftMessage);
                    messagesList.add(message);

                } else if (msg.contains("enTraNceEffEct")) {

                }else {
                    messageBean = new MessageBean(account, msg, false, false,false);
                    messageBeanList.add(messageBean);
                    adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                    rvMessages.scrollToPosition(messageBeanList.size() - 1);

                    if (msg.contains("liked")) {
                        likes = likes + 1;
                    } else if (msg.contains(": Has sent")) {
                        GiftMessage giftMessage = new GiftMessage();
                        giftMessage.setAccount(account);
                        giftMessage.setMessage(msg);
                        giftMessages.add(giftMessage);
                        messagesList.add(msg);
                    }
                }
            }
        });
    }

    /**
     * API CALL: send message to a channel
     */
    private void sendChannelMessage(String msg) {

        // step 1: create a message
        RtmMessage message = mRtmClient.createMessage();
        message.setText(msg);
        // step 2: send message to channel
        mRtmChannel.sendMessage(message, new ResultCallback<Void>() {

            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "onMessageSendSuccess: " );
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                // refer to RtmStatusCode.ChannelMessageState for the message state
                Log.e(TAG, "onMessageSendFailure: " );
                final int errorCode = errorInfo.getErrorCode();

            }
        });
    }

    private void getLevel() {
        try {
            level = SessionUser.getUser().getLevel();
            if (level.length() == 1) {
                level = "0" + level;
            }
        } catch (Exception e) {
            Log.e(TAG, "getLevel: " + e);
            Crashlytics.logException(e);
        }
    }

    private void callStatusAPI() {
        getLevel();
        collapsedView.setVisibility(View.GONE);
        rlMessage.setVisibility(View.GONE);
        llChat.setVisibility(View.GONE);
        llMessage.setVisibility(View.GONE);

        stopService(new Intent(BLiveApplication.getStaticContext(), FloatingViewService.class));

        sendChannelMessage( SessionUser.getUser().getUser_id() + level + SessionUser.getUser().getName() + " Broadcast has ended");

        Constants_app.cleanMessageListBeanList();
        endTime = System.currentTimeMillis();
        long mills = endTime - startTime;
        int hours = (int) (mills / (1000 * 60 * 60));
        int mins = (int) (mills / (1000 * 60)) % 60;
        int seconds = (int) (mills / 1000) % 60;
        String broadcastingTime = String.valueOf(hours) + ":" + String.valueOf(mins) + ":" + String.valueOf(seconds);
        Log.e(TAG, "callStatusAPI: " + broadcastingTime);
        int diff = gold - oldGold;
        int currentGold = Integer.valueOf(SessionUser.getUser().getCurrent_gold_value()) + diff;

        if (isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GenericResponse> call = apiClient.goLive(SessionUser.getUser().getUser_id(), "INACTIVE", "screenSharing", broadcastingTime,"","","","");
            call.enqueue(new retrofit2.Callback<GenericResponse>() {
                @Override
                public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                    GenericResponse genericResponse = response.body();
                    if (response.code() == 200) {
                        if (genericResponse != null) {
                            if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                                onStatusSuccess();
                            } else {
                                onStatusSuccess();
                                Toast.makeText(FloatingViewService.this,genericResponse.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(FloatingViewService.this,getString(R.string.server_error),Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(FloatingViewService.this,getString(R.string.server_error),Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                    Toast.makeText(FloatingViewService.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void onStatusSuccess() {

        endTime = System.currentTimeMillis();

        long mills = endTime - startTime;
        int hours = (int) (mills / (1000 * 60 * 60));
        int mins = (int) (mills / (1000 * 60)) % 60;

        if (hours > 1 && mins > 1)
            time = hours + " Hours " + mins + " minutes";
        else if (hours == 1 && mins > 1)
            time = hours + " Hour " + mins + " minutes";
        else
            time = hours + " Hour " + mins + " minute";

        Intent intent = new Intent(this, ActivityStreamDetails.class);
        intent.putExtra("gold", String.valueOf(gold - oldGold));
        intent.putExtra("viewers", String.valueOf(viewers));
        intent.putExtra("likes", String.valueOf(likes));
        intent.putExtra("time", String.valueOf(time));
        intent.putExtra("from", "screenShare");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }

    protected final WorkerThread worker() {
        return ((BLiveApplication) getApplication()).getWorkerThread();
    }

    public void showSoftKeyboard(View view) {
        userMessageBox.requestFocus();
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        return isConnected;
    }
}
