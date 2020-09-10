package com.blive.activity;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.blive.BLiveApplication;
import com.blive.R;
import com.blive.agora.rtmChat.ChatHandler;
import com.blive.agora.rtmChat.ChatManager;
import com.blive.constant.Constants_app;
import com.blive.model.User;
import com.blive.model.UsersResponse;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.session.SessionUser;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import io.agora.rtc.Constants;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmChannel;
import io.agora.rtm.RtmChannelAttribute;
import io.agora.rtm.RtmChannelListener;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmMessage;
import retrofit2.Call;
import retrofit2.Response;

public class DeeplinkActivity extends BaseActivity {
    TextView deeplinktxt;
    String userid = "";
    public static String username = "";
    public static User userDatamodel;
    public static ArrayList<User> userDatamodelArray;
    private RtmClient mRtmClient;
    private ChatManager mChatManager;
    private RtmChannel mRtmChannel;
    private ChatHandler mChatHandler;
    private User user;
    private ArrayList<User> users;
    String image = "";
    private int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BLiveApplication.setCurrentActivity(this);

        setContentView(R.layout.activity_deeplink);
        deeplinktxt = findViewById(R.id.deeplinktxt);
        deeplinktxt.setVisibility(View.GONE);
        Intent intent = getIntent();
        if (intent.getData() != null) {
            Uri data = intent.getData();
            Log.d("data", data.toString());
            String queryparams = data.getQueryParameter("user_id");
            userid = queryparams;
            if (userid.equalsIgnoreCase("")) {
                showToast("Data Does not exist");
                Intent intent1 = new Intent(this, ActivityHome.class);
                startActivity(intent1);
                finish();

            } else {
                if (userid.equalsIgnoreCase(SessionUser.getUser().getUser_id())) {
                    showToast("You are on the same channel");
                    Intent intent1 = new Intent(this, ActivityHome.class);
                    startActivity(intent1);
                    finish();
                } else {
                    callGetUserData(queryparams);

                }
            }
//            deeplinktxt.setText(queryparams);


            Log.d("queryparams", "queryparams - " + queryparams);
        } else {
            Log.d("queryparams", "queryparams - " + "No Image");
        }


    }

    public DeeplinkActivity() {
    }

    private void callGetUserData(String notificationUserId) {
        if (utils.isNetworkAvailable()) {
            utils.showProgress();
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<UsersResponse> call = apiClient.getUserData(SessionUser.getUser().getUser_id(), notificationUserId);
            call.enqueue(new retrofit2.Callback<UsersResponse>() {
                @Override
                public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
                    utils.hideProgress();
                    UsersResponse usersResponse = response.body();
                    if (response.code() == 200) {
                        if (usersResponse != null) {
                            if (usersResponse.getStatus().equalsIgnoreCase("success")) {

                                user = usersResponse.getData().getUser();
                                Log.i("autolog", "user: " + user.getUsername());


                                channeljoin();
                                if (!SessionUser.getRtmLoginSession()) {
                                    doLogin1(user.getUsername());
                                } else {
                                    mChatManager.createChannel(user.getUsername());
                                }
                                doLogin1(user.getUsername());


                                if (usersResponse.getData().getUser().getStatus().equalsIgnoreCase("INACTIVE")) {
                                    showToast("Currently " + usersResponse.getData().getUser().getName() + " is Offline!");
                                } else {
                                    if (!usersResponse.getData().getUser().getBroadcast_type().isEmpty())
                                        moveNotificationUserToLive(usersResponse.getData().getUser());
                                    else
                                        showToast("Currently " + usersResponse.getData().getUser().getName() + " is Offline!");

                                    Intent intent1 = new Intent(DeeplinkActivity.this, ActivityHome.class);
                                    startActivity(intent1);
                                    finish();
                                }
                            } else {
                                showToast("Currently the user is Offline!");
                                Intent intent1 = new Intent(DeeplinkActivity.this, ActivityHome.class);
                                startActivity(intent1);
                                finish();
                            }
                        } else {
                            utils.showToast(getString(R.string.server_error));
                            Intent intent1 = new Intent(DeeplinkActivity.this, ActivityHome.class);
                            startActivity(intent1);
                            finish();
                        }
                    } else {
                        checkResponseCode(response.code());
                        Intent intent1 = new Intent(DeeplinkActivity.this, ActivityHome.class);
                        startActivity(intent1);
                        finish();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UsersResponse> call, @NonNull Throwable t) {
                    showToast(t.getMessage());
                    Intent intent1 = new Intent(DeeplinkActivity.this, ActivityHome.class);
                    startActivity(intent1);
                    finish();
                }
            });
        }
    }

   /* public void moveNotificationUserToLive(User userData) {

        ArrayList<User> newAUserList = new ArrayList<>();
        userDatamodelArray=new ArrayList<>();


        newAUserList.add(userData);


//        ActivityHome.getUser(userData,newAUserList,0);


        Intent intent1 = new Intent(DeeplinkActivity.this, ActivityHome.class);
        username=userData.getUsername();
        userDatamodel=userData;
        Log.i("autolog", "userDatamodelArray: " + userDatamodelArray.size());
        userDatamodelArray.clear();
//        userDatamodelArray=newAUserList;

        startActivity(intent1);
        finish();

    }*/

    public void moveNotificationUserToLive(User userData) {
        ArrayList<User> newAUserList = new ArrayList<>();
        newAUserList.add(userData);
//        ((ActivityHome) Objects.requireNonNull(getApplicationContext())).getUser(userData, newAUserList, 0);
    }


    @Override
    protected void initUI() {

    }

    @Override
    protected void deInitUI() {

    }

    private void doLogin1(String mChannelName) {
        mRtmClient.login(null, SessionUser.getUser().getUsername().trim(), new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                Log.e(TAG, "login success");
                mActivity.runOnUiThread(() -> {
                    SessionUser.isRtmLoggedIn(true);
                    mChatManager.createChannel(mChannelName);
                });
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                Log.e(TAG, "login failed: " + errorInfo.getErrorCode());
                mActivity.runOnUiThread(() -> {
                    if (errorInfo.getErrorCode() == 8) {
                        mChatManager.createChannel(SessionUser.getUser().getUsername().trim());
                        logoutRtm(SessionUser.getUser().getUsername().trim());
                    } else {
                        mChatManager.doLogin();
                    }
                    //
                });
            }
        });
    }

    public void logoutRtm(String mChannelname) {
        mRtmClient.logout(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "logout onSuccess: ");
                doLogin1(mChannelname);
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                Log.e(TAG, "logout Error: " + errorInfo.getErrorCode() + "  " + errorInfo.getErrorDescription());
            }
        });
    }


    public void channeljoin() {

        mChatManager = BLiveApplication.getInstance().getChatManager();
        mRtmClient = mChatManager.getRtmClient();


        mRtmChannel = mRtmClient.createChannel(user.getUsername(), new RtmChannelListener() {
            @Override
            public void onMemberCountUpdated(int i) {

            }

            @Override
            public void onAttributesUpdated(List<RtmChannelAttribute> list) {

            }

            @Override
            public void onMessageReceived(RtmMessage message, RtmChannelMember fromMember) {
                String text = message.getText();
                Log.i("autolog", "text: " + text);
                String fromUser = fromMember.getUserId();
                Log.i("autolog", "fromUser: " + fromUser);
                Log.e(TAG, "onRTMMessageReceived: account = " + fromMember.getUserId() + " msg = " + message.getText());

            }

            @Override
            public void onMemberJoined(RtmChannelMember rtmChannelMember) {
                Log.i("autolog", "member: " + rtmChannelMember);
                runOnUiThread(() -> {
                    Log.i("autolog", "rtmChannelMember: " + rtmChannelMember.getChannelId() + rtmChannelMember.getUserId());

                });


            }

            @Override
            public void onMemberLeft(RtmChannelMember member) {
                Log.i("autolog", "member: " + member);
                runOnUiThread(() -> {
                });


            }
        });



        mChatHandler = new ChatHandler() {
            @Override
            public void onLoginSuccess() {
                Log.e(TAG, "onLoginSuccess: ");
            }

            @Override
            public void onLoginFailed(ErrorInfo errorInfo) {
                Log.e(TAG, "onLoginFailed: ");
                runOnUiThread(() -> {
                    if (errorInfo.getErrorCode() == 8)
                        try {
                            if (user.getUsername() != null) {
                                mChatManager.createChannel(user.getUsername().trim());
                            } else {
                                mChatManager.doLogin(user.getUsername());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                });
            }

            @Override
            public void onChannelJoinSuccess() {
                runOnUiThread(() -> {
                    utils.hideProgress();
                    if (user != null) {
                        String base64 = user.getProfile_pic();
                        try {
                            image = URLDecoder.decode(base64, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        mRtmClient = mChatManager.getRtmClient();
                        if (user.getBroadcast_type().equalsIgnoreCase("solo")) {
                            Log.d(TAG, "onChannelJoinSuccess: " + "solo");

                            Intent intent = new Intent(mActivity, ActivityLiveRoom.class);
                            intent.putExtra("mode", false);
                            intent.putExtra("name", user.getUsername());
                            intent.putExtra("selfname", SessionUser.getUser().getName());
                            intent.putExtra("image", image);
                            intent.putExtra("token", user.getActivation_code());
                            intent.putExtra("broadcasterId", user.getUser_id());
                            intent.putExtra("received", user.getReceived());
                            Log.e(TAG, "onChannelJoinSuccess: " + user.getReceived());
                            intent.putExtra("broadcaster", user);
                            intent.putParcelableArrayListExtra("users", users);
                            intent.putExtra("position", position);
                            intent.putExtra("isFollowing", user.getIsTheUserFollowing());
                            intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_AUDIENCE);
                            intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, user.getUsername().trim());
                            intent.putExtra("isSwiped", false);
                            intent.putExtra("PKuserId", 0);
                            intent.putExtra("broad_type", "solo");


                            intent.putExtra("pkTimer", user.getPkTimeLeft());
                            if (Constants_app.BAudiance == 1) {
                                intent.putExtra("broadcasterAudience", false);
                                Log.i("autolog", "broadcasterAudience: " + "broadcasterAudience");
                                intent.putExtra("guestAudience", false);   //
                            } else {
                                intent.putExtra("broadcasterAudience", false);
                                intent.putExtra("guestAudience", false);   //
                                Log.i("autolog", "guestAudience: " + "guestAudience");
                            }
                            intent.putExtra("intermediateJoin", false);


                            startActivity(intent);
                        } else if (user.getBroadcast_type().equalsIgnoreCase("pk")) {

                            Log.d(TAG, "onChannelJoinSuccess: " + "pk");
                            Intent intent = new Intent(mActivity, ActivityLiveRoom.class);
                            intent.putExtra("mode", false);
                            intent.putExtra("broad_type", "pk");
                            intent.putExtra("name", user.getUsername());
                            intent.putExtra("selfname", SessionUser.getUser().getName());
                            intent.putExtra("image", image);
                            intent.putExtra("token", user.getActivation_code());
                            intent.putExtra("broadcasterId", user.getPk_broadcaster_id());
                            intent.putExtra("PKuserId", user.getPk_guest_id());
                            Log.i("autolog", "pkUser_id(): " + user.getPk_guest_id());
                            intent.putExtra("received", user.getReceived());
                            intent.putExtra("broadcaster", user);
                            intent.putExtra("pkTimer", user.getPkTimeLeft());
                            Log.i("autolog", "user.getPk_channelname(): " + user.getPk_channelname());
                            Log.i("autolog", "user.getPk_channelname(): " + user.getUsername());
                            if (user.getPk_channelname().equalsIgnoreCase(user.getUsername())) {
                                intent.putExtra("broadcasterAudience", true);
                                Log.i("autolog", "broadcasterAudience: " + "broadcasterAudience");
                                intent.putExtra("guestAudience", false);   //
                            } else {
                                intent.putExtra("broadcasterAudience", false);
                                intent.putExtra("guestAudience", true);   //
                                Log.i("autolog", "guestAudience: " + "guestAudience");
                            }
                            intent.putParcelableArrayListExtra("users", users);
                            intent.putExtra("position", position);
                            intent.putExtra("isFollowing", user.getIsTheUserFollowing());
                            intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_AUDIENCE);
                            intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, user.getPk_channelname().trim());
                            intent.putExtra("isSwiped", false);
                            intent.putExtra("intermediateJoin", true);
                            Log.d(TAG, "onChannelJoinSuccess: " + intent.getExtras().toString());
                            startActivity(intent);
                        } else if (user.getBroadcast_type().equalsIgnoreCase("groupOf3")) {
                            Intent intent = new Intent(mActivity, ActivityGroupCalls3.class);
                            intent.putExtra("mode", false);
                            intent.putExtra("name", user.getUsername());
                            intent.putExtra("selfname", SessionUser.getUser().getName());
                            intent.putExtra("image", image);
                            intent.putExtra("token", user.getActivation_code());
                            intent.putExtra("broadcasterId", user.getUser_id());
                            intent.putExtra("received", user.getReceived());
                            intent.putExtra("broadcaster", user);
                            intent.putParcelableArrayListExtra("users", users);
                            intent.putExtra("position", position);
                            intent.putExtra("isFollowing", user.getIsTheUserFollowing());
                            intent.putExtra("isSwiped", false);
                            intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_AUDIENCE);
                            intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, user.getUsername().trim());
                            startActivity(intent);
                        } else if (user.getBroadcast_type().equalsIgnoreCase("groupOf6")) {
                            Intent intent = new Intent(mActivity, ActivityGroupCalls6.class);
                            intent.putExtra("mode", false);
                            intent.putExtra("name", user.getUsername());
                            intent.putExtra("selfname", SessionUser.getUser().getName());
                            intent.putExtra("image", image);
                            intent.putExtra("token", user.getActivation_code());
                            intent.putExtra("broadcasterId", user.getUser_id());
                            intent.putExtra("received", user.getReceived());
                            intent.putExtra("broadcaster", user);
                            intent.putParcelableArrayListExtra("users", users);
                            intent.putExtra("position", position);
                            intent.putExtra("isFollowing", user.getIsTheUserFollowing());
                            intent.putExtra("isSwiped", false);
                            intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_AUDIENCE);
                            intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, user.getUsername().trim());
                            startActivity(intent);
                        } else if (user.getBroadcast_type().equalsIgnoreCase("groupOf9")) {
                            Intent intent = new Intent(mActivity, ActivityGroupCalls9.class);
                            intent.putExtra("mode", false);
                            intent.putExtra("name", user.getUsername());
                            intent.putExtra("selfname", SessionUser.getUser().getName());
                            intent.putExtra("image", image);
                            intent.putExtra("token", user.getActivation_code());
                            intent.putExtra("broadcasterId", user.getUser_id());
                            intent.putExtra("received", user.getReceived());
                            intent.putExtra("broadcaster", user);
                            intent.putParcelableArrayListExtra("users", users);
                            intent.putExtra("position", position);
                            intent.putExtra("isFollowing", user.getIsTheUserFollowing());
                            intent.putExtra("isSwiped", false);
                            intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_AUDIENCE);
                            intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, user.getUsername().trim());
                            startActivity(intent);
                        } else if (user.getBroadcast_type().equalsIgnoreCase("screenSharing")) {
                            Intent intent = new Intent(mActivity, ActivityScrShareViewers.class);
                            intent.putExtra("mode", false);
                            intent.putExtra("name", user.getUsername());
                            intent.putExtra("selfname", SessionUser.getUser().getUsername());
                            intent.putExtra("image", image);
                            intent.putExtra("token", user.getActivation_code());
                            intent.putExtra("broadcasterId", user.getUser_id());
                            intent.putExtra("received", user.getReceived());
                            intent.putExtra("broadcaster", user);
                            intent.putParcelableArrayListExtra("users", users);
                            intent.putExtra("position", position);
                            intent.putExtra("isSwiped", false);
                            intent.putExtra("isFollowing", user.getIsTheUserFollowing());
                            intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_AUDIENCE);
                            intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, user.getUsername().trim());
                            startActivity(intent);
                        } else if (user.getBroadcast_type().equalsIgnoreCase("audio")) {
                            Intent intent = new Intent(mActivity, ActivityAudioCall.class);
                            intent.putExtra("mode", false);
                            intent.putExtra("name", user.getUsername());
                            intent.putExtra("selfname", SessionUser.getUser().getUsername());
                            intent.putExtra("image", image);
                            intent.putExtra("token", user.getActivation_code());
                            intent.putExtra("broadcasterId", user.getUser_id());
                            intent.putExtra("received", user.getReceived());
                            intent.putExtra("broadcaster", user);
                            intent.putParcelableArrayListExtra("users", users);
                            intent.putExtra("position", position);
                            intent.putExtra("isSwiped", false);
                            intent.putExtra("isFollowing", user.getIsTheUserFollowing());
                            intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_AUDIENCE);
                            intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, user.getUsername().trim());
                            startActivity(intent);
                        } else {
                            showToast("User is Offline!");
                        }
                    }
                });
            }

            @Override
            public void onChannelJoinFailed(ErrorInfo errorInfo) {
                runOnUiThread(() -> {
                    Log.e(TAG, "channel join failed: ");
                    if (errorInfo.getErrorCode() == 1) {
                        Log.e(TAG, "run: " + errorInfo.getErrorDescription());
                        doLogin1(user.getUsername().trim());
                    }
                });
            }

            @Override
            public void onMessageReceived(RtmMessage message, RtmChannelMember fromMember) {

            }

            @Override
            public void onMemberJoined(RtmChannelMember rtmChannelMember) {
                Log.e(TAG, "onRTMMemberJoined: ");
            }

            @Override
            public void onMemberLeft(RtmChannelMember rtmChannelMember) {

            }
        };

        mChatManager.addChantHandler(mChatHandler);


        mChatManager.leaveChannel();
    }

}
