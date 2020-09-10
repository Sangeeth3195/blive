package com.blive.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blive.BLiveApplication;
import com.blive.R;
import com.blive.agora.rtmChat.ChatHandler;
import com.blive.agora.rtmChat.ChatManager;
import com.blive.constant.Constants_app;
import com.blive.fragment.FragmentLive;
import com.blive.model.ActiveUserResponse;
import com.blive.model.User;
import com.blive.model.UsersResponse;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.session.SessionUser;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import io.agora.rtc.Constants;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmClient;
import jp.wasabeef.picasso.transformations.BlurTransformation;
import retrofit2.Call;
import retrofit2.Response;

public class ActivityBroadcastStop extends BaseBackActivity {

    @BindView(R.id.ivImage)
    ImageView ivImage;

    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_name_1)
    TextView tv_name_1;
    @BindView(R.id.tv_name_2)
    TextView tv_name_2;
    @BindView(R.id.tv_name_3)
    TextView tv_name_3;
    int index = 0;
    private ArrayList<User> users;
    @BindView(R.id.iv_profile_1)
    ImageView iv_profile_1;
    @BindView(R.id.iv_profile_2)
    ImageView iv_profile_2;
    @BindView(R.id.iv_profile_3)
    ImageView iv_profile_3;
    @BindView(R.id.iv_profile_4)
    ImageView iv_profile_4;
    @BindView(R.id.id_1)
    LinearLayout id1;
    @BindView(R.id.id_2)
    LinearLayout id2;
    @BindView(R.id.id_3)
    LinearLayout id3;
    @BindView(R.id.id_4)
    LinearLayout id4;

    @BindView(R.id.ll_layout1)
    LinearLayout llLayout1;
    @BindView(R.id.ll_layout2)
    LinearLayout llLayout2;

    private User user;
    private FragmentLive fragmentLive;
    private int position = -1;
    String type = "", finalimage = "", image = "", image1 = "", image2 = "", image3 = "", image4 = "", image5 = "", image6 = "", image7 = "", image8 = "", image9 = "";
    TextView tvNames;
    private int i;
    private boolean isUserListEnd = false, isAPICalled = false, isWebError = false, isUserClicked = false;
    String imagings, profilePic, names, user_ids;
    private ImageView ivEffect, ivEffect1, ivEffect2, ivEffect3, ivEffect4;
    private ArrayList<User> activeUsers;
    ActiveUserResponse genericResponse;
    String base64;

    private static BLiveApplication mInstance;
    private static Activity activity = null;
    private ChatManager mChatManager;
    private User broadcaster;
    private ChatHandler mChatHandler;

    private RtmClient mRtmClient;

    public static Context getStaticContext() {
        return BLiveApplication.getInstance().getApplicationContext();
    }

    public static BLiveApplication getInstance() {
        return mInstance;
    }

    public static void setCurrentActivity(Activity mCurrentActivity) {
        activity = mCurrentActivity;
    }

    public static Activity getCurrentActivity() {
        return activity;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_stop);
        BLiveApplication.setCurrentActivity(this);
        initUI();
    }

    private void initUI() {

        tvNames = findViewById(R.id.tv_names);
        ivEffect = findViewById(R.id.iv_effect);
        ivEffect1 = findViewById(R.id.iv_effect1);
        ivEffect2 = findViewById(R.id.iv_effect2);
        ivEffect3 = findViewById(R.id.iv_effect3);
        ivEffect4 = findViewById(R.id.iv_effect4);

        Intent intent = getIntent();
        names = intent.getStringExtra("name");
        imagings = intent.getStringExtra("image");
        user_ids = intent.getStringExtra("user_id");

        tvNames.setText(names);



        Glide.with(getApplicationContext())
                .load(imagings)
                .into(ivEffect);

        if (imagings != null && !imagings.isEmpty()) {
            String base64 = imagings;
            try {
                profilePic = URLDecoder.decode(base64, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Picasso.get().load(profilePic).fit().transform(new BlurTransformation(mActivity)).centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).
                    placeholder(R.drawable.user).into(ivImage);
            Picasso.get().load(profilePic).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).
                    placeholder(R.drawable.user).into(iv);
        } else {
            Picasso.get().load(R.drawable.user).fit().transform(new BlurTransformation(mActivity)).centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).
                    placeholder(R.drawable.user).into(ivImage);
            Picasso.get().load(R.drawable.user).centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).
                    placeholder(R.drawable.user).into(iv);
        }
//        callAPIOffline();
    }

    private void callAPIOffline() {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<ActiveUserResponse> call = apiClient.offlines(user_ids);
            call.enqueue(new retrofit2.Callback<ActiveUserResponse>() {
                @Override
                public void onResponse(@NonNull Call<ActiveUserResponse> call, @NonNull Response<ActiveUserResponse> response) {
                    genericResponse = response.body();
                    if (response.code() == 200) {
                        if (genericResponse != null) {
                            if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                                setData(genericResponse.getData().getActiveUsers());
                            } else {
                                runOnUiThread(() -> {
                                    try {

                                    } catch (Exception e) {
                                        Crashlytics.logException(e);
                                    }
                                });
                            }
                        } else {
                            runOnUiThread(() -> {
                                try {
                                    Log.e(TAG, "Broadcast has ended: User Offline Two");
                                } catch (Exception e) {
                                    Crashlytics.logException(e);
                                }
                            });
                        }
                    } else {
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ActiveUserResponse> call, @NonNull Throwable t) {

                    showToast(t.getMessage());
                }
            });
        }
    }

    private void setData(ArrayList<User> mActiveUsers) {
        activeUsers = mActiveUsers;
        Log.d(TAG, "setData: ");
        if (activeUsers.size() > 0) {
            int size = activeUsers.size();
            if (size <= 4) {
                switch (size) {
                    case 1:
                        id1.setVisibility(View.VISIBLE);
                        tv_name.setText(activeUsers.get(0).getName());
                        Log.d(TAG, "setData: "+"1");

                        image = activeUsers.get(0).getProfile_pic();

                        if (image != null && !image.isEmpty()) {
                            String base64 = image;
                            try {
                                image1 = URLDecoder.decode(base64, "UTF-8");
                                Log.e(TAG, image1);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            Picasso.get().load(image1).fit().centerCrop().into(iv_profile_1);
                        } else {
                            Glide.with(this).load(R.drawable.user).into(iv_profile_1);
                        }

                        break;
                    case 2:
                        llLayout1.setVisibility(View.VISIBLE);
                        id1.setVisibility(View.VISIBLE);
                        id2.setVisibility(View.VISIBLE);
                        id3.setVisibility(View.INVISIBLE);
                        id4.setVisibility(View.INVISIBLE);
                        tv_name.setText(activeUsers.get(0).getName());
                        tv_name_1.setText(activeUsers.get(1).getName());
                        Log.d(TAG, "setData: "+"2");



                        image1 = activeUsers.get(0).getProfile_pic();

                        if (image1 != null && !image1.isEmpty()) {
                            String base64 = image1;
                            try {
                                image1 = URLDecoder.decode(base64, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            Picasso.get().load(image1).fit().centerCrop().into(iv_profile_1);
                        } else {
                            Glide.with(this).load(R.drawable.user).into(iv_profile_1);
                        }

                        image2 = activeUsers.get(1).getProfile_pic();

                        if (image2 != null && !image2.isEmpty()) {
                            String base64 = image2;
                            try {
                                image2 = URLDecoder.decode(base64, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            Picasso.get().load(image2).fit().centerCrop().into(iv_profile_2);
                        } else {
                            Glide.with(this).load(R.drawable.user).into(iv_profile_2);
                        }

                        break;
                    case 3:
                        Log.d(TAG, "setData: "+"3");
                        id1.setVisibility(View.VISIBLE);
                        id2.setVisibility(View.VISIBLE);
                        id3.setVisibility(View.VISIBLE);
                        id4.setVisibility(View.INVISIBLE);
                        tv_name.setText(activeUsers.get(0).getName());
                        tv_name_1.setText(activeUsers.get(1).getName());
                        tv_name_2.setText(activeUsers.get(2).getName());

                        image3 = activeUsers.get(0).getProfile_pic();

                        if (image3 != null && !image3.isEmpty()) {
                            Picasso.get().load(image3).fit().centerCrop().into(iv_profile_1);
                        } else {
                            Glide.with(this).load(R.drawable.user).into(iv_profile_1);
                        }

                        image4 = activeUsers.get(1).getProfile_pic();

                        if (image4 != null && !image4.isEmpty()) {
                            Picasso.get().load(image4).fit().centerCrop().into(iv_profile_2);
                        } else {
                            Glide.with(this).load(R.drawable.user).into(iv_profile_2);
                        }
                        image5 = activeUsers.get(2).getProfile_pic();

                        if (image5 != null && !image5.isEmpty()) {
                            Picasso.get().load(image4).fit().centerCrop().into(iv_profile_2);
                        } else {
                            Glide.with(this).load(R.drawable.user).into(iv_profile_2);
                        }

                        break;
                    case 4:
                        Log.d(TAG, "setData: "+"4");
                        llLayout2.setVisibility(View.GONE);
                        tv_name.setText(activeUsers.get(0).getName());
                        tv_name_1.setText(activeUsers.get(1).getName());
                        tv_name_2.setText(activeUsers.get(2).getName());
                        tv_name_3.setText(activeUsers.get(3).getName());

                        image6 = activeUsers.get(0).getProfile_pic();

                        if (image6 != null && !image6.isEmpty()) {
                            String base64 = image6;
                            try {
                                image6 = URLDecoder.decode(base64, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            Picasso.get().load(image6).fit().centerCrop().into(iv_profile_1);
                        } else {
                            Glide.with(this).load(R.drawable.user).into(iv_profile_1);
                        }

                        image7 = activeUsers.get(1).getProfile_pic();

                        if (image7 != null && !image7.isEmpty()) {
                            String base64 = image7;
                            try {
                                image7 = URLDecoder.decode(base64, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            Picasso.get().load(image7).fit().centerCrop().into(iv_profile_2);
                        } else {
                            Glide.with(this).load(R.drawable.user).into(iv_profile_2);
                        }
                        image8 = activeUsers.get(2).getProfile_pic();

                        if (image8 != null && !image8.isEmpty()) {
                            String base64 = image8;
                            try {
                                image8 = URLDecoder.decode(base64, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            Picasso.get().load(image8).fit().centerCrop().into(iv_profile_3);
                        } else {
                            Glide.with(this).load(R.drawable.user).into(iv_profile_3);
                        }
                        image9 = activeUsers.get(3).getProfile_pic();

                        if (image9 != null && !image9.isEmpty()) {
                            String base64 = image9;
                            try {
                                image9 = URLDecoder.decode(base64, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            Picasso.get().load(image9).fit().centerCrop().into(iv_profile_4);
                        } else {
                            Glide.with(this).load(R.drawable.user).into(iv_profile_4);
                        }
                        break;
                }
            } else {
                id1.setVisibility(View.VISIBLE);
                id2.setVisibility(View.VISIBLE);
                id3.setVisibility(View.VISIBLE);
                id4.setVisibility(View.VISIBLE);

                tv_name.setText(activeUsers.get(0).getName());
                tv_name_1.setText(activeUsers.get(1).getName());
                tv_name_2.setText(activeUsers.get(2).getName());
                tv_name_3.setText(activeUsers.get(3).getName());

                ArrayList<User> users = new ArrayList<>();
                for (int i = 4; i < activeUsers.size(); i++) {
                    users.add(activeUsers.get(i));
                }
            }
        } else {
            id1.setVisibility(View.GONE);
            id2.setVisibility(View.GONE);
            id3.setVisibility(View.GONE);
            id4.setVisibility(View.GONE);
        }

        iv_profile_1.setOnClickListener(v -> {
//            OnNext(0);
        });
        iv_profile_2.setOnClickListener(v -> {
//            OnNext(1);
        });

        iv_profile_3.setOnClickListener(v -> {
//            OnNext(2);
        });
        iv_profile_4.setOnClickListener(v -> {
//            OnNext(3);
        });

    }

    public void getUser(User mUser, ArrayList<User> mUsers, int mPosition) {
        utils.showProgress();
        user = mUser;
        position = mPosition;
        users = mUsers;
        if (SessionUser.getRtmLoginSession())
            mChatManager.createChannel(mUser.getUsername().trim());
        else {
            mChatManager.doLogin(mUser.getUsername().trim());
        }
    }

    public void OnNext(int mPosition) {
        final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
        Call<UsersResponse> call = apiClient.getBroadcastType(activeUsers.get(mPosition).getUser_id());
        call.enqueue(new retrofit2.Callback<UsersResponse>() {
            @Override
            public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
                utils.hideProgress();
                UsersResponse usersResponse = response.body();
                if (response.code() == 200) {
                    if (usersResponse != null) {
                        if (usersResponse.getStatus().equalsIgnoreCase("success")) {
                            if (!usersResponse.getData().getBroadcastType().equalsIgnoreCase(activeUsers.get(mPosition).getBroadcast_type()))
                                activeUsers.get(mPosition).setBroadcast_type(usersResponse.getData().getBroadcastType());

                            if (!activeUsers.get(mPosition).getBroadcast_type().isEmpty()) {
                                if (activeUsers != null) {
                                    base64 = activeUsers.get(mPosition).getProfile_pic();
                                    try {
                                        image = URLDecoder.decode(base64, "UTF-8");
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    if (activeUsers.get(mPosition).getBroadcast_type().equalsIgnoreCase("solo")) {
                                        Intent intent = new Intent(mActivity, ActivityLiveRoom.class);
                                        intent.putExtra("mode", false);
                                        intent.putExtra("name", activeUsers.get(mPosition).getUsername());
                                        intent.putExtra("selfname", SessionUser.getUser().getName());
                                        intent.putExtra("image", image);
                                        intent.putExtra("token", activeUsers.get(mPosition).getActivation_code());
                                        intent.putExtra("broadcasterId", activeUsers.get(mPosition).getUser_id());
                                        intent.putExtra("received", activeUsers.get(mPosition).getReceived());
                                        intent.putExtra("broadcaster", activeUsers.get(mPosition));
                                        intent.putParcelableArrayListExtra("users", activeUsers);
                                        intent.putExtra("position", mPosition);
                                        intent.putExtra("isFollowing", activeUsers.get(mPosition).getIsTheUserFollowing());
                                        intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_AUDIENCE);
                                        intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, activeUsers.get(mPosition).getUsername().trim());
                                        intent.putExtra("isSwiped", false);
                                        startActivity(intent);
                                    } else if (activeUsers.get(mPosition).getBroadcast_type().equalsIgnoreCase("groupOf3")) {
                                        Intent intent = new Intent(mActivity, ActivityGroupCalls3.class);
                                        intent.putExtra("mode", false);
                                        intent.putExtra("name", activeUsers.get(mPosition).getUsername());
                                        intent.putExtra("selfname", SessionUser.getUser().getName());
                                        intent.putExtra("image", image);
                                        intent.putExtra("token", activeUsers.get(mPosition).getActivation_code());
                                        intent.putExtra("broadcasterId", activeUsers.get(mPosition).getUser_id());
                                        intent.putExtra("received", activeUsers.get(mPosition).getReceived());
                                        intent.putExtra("broadcaster", activeUsers.get(mPosition));
                                        intent.putExtra("broadcast_type", activeUsers.get(mPosition).getBroadcast_type());
                                        intent.putParcelableArrayListExtra("users", activeUsers);
                                        intent.putExtra("position", mPosition);
                                        intent.putExtra("isFollowing", activeUsers.get(mPosition).getIsTheUserFollowing());
                                        intent.putExtra("isSwiped", false);
                                        intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_AUDIENCE);
                                        intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, activeUsers.get(mPosition).getUsername().trim());
                                        startActivity(intent);
                                    } else if (activeUsers.get(mPosition).getBroadcast_type().equalsIgnoreCase("screenSharing")) {
                                        Intent intent = new Intent(mActivity, ActivityScrShareViewers.class);
                                        intent.putExtra("mode", false);
                                        intent.putExtra("name", activeUsers.get(mPosition).getUsername());
                                        intent.putExtra("selfname", SessionUser.getUser().getName());
                                        intent.putExtra("image", image);
                                        intent.putExtra("usercount", activeUsers.get(mPosition).getViewers_count());
                                        intent.putExtra("token", activeUsers.get(mPosition).getActivation_code());
                                        intent.putExtra("broadcasterId", activeUsers.get(mPosition).getUser_id());
                                        intent.putExtra("received", activeUsers.get(mPosition).getReceived());
                                        intent.putExtra("broadcaster", activeUsers.get(mPosition));
                                        intent.putParcelableArrayListExtra("users", activeUsers);
                                        intent.putExtra("position", mPosition);
                                        intent.putExtra("isFollowing", activeUsers.get(mPosition).getIsTheUserFollowing());
                                        intent.putExtra("isSwiped", false);
                                        intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_AUDIENCE);
                                        intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, activeUsers.get(mPosition).getUsername().trim());
                                        startActivity(intent);
                                    } /*else if (activeUsers.get(mPosition).getBroadcast_type().equalsIgnoreCase("karaokeSolo")) {
                                        Intent intent = new Intent(mActivity, ActivityKaraokeSolo.class);
                                        intent.putExtra("mode", false);
                                        intent.putExtra("name", activeUsers.get(mPosition).getUsername());
                                        intent.putExtra("selfname", SessionUser.getUser().getName());
                                        intent.putExtra("image", image);
                                        intent.putExtra("token", activeUsers.get(mPosition).getActivation_code());
                                        intent.putExtra("broadcasterId", activeUsers.get(mPosition).getUser_id());
                                        intent.putExtra("received", activeUsers.get(mPosition).getReceived());
                                        intent.putExtra("broadcaster", activeUsers.get(mPosition));
                                        intent.putParcelableArrayListExtra("users", activeUsers);
                                        intent.putExtra("position", mPosition);
                                        intent.putExtra("isFollowing", activeUsers.get(mPosition).getIsTheUserFollowing());
                                        intent.putExtra("isSwiped", false);
                                        intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_AUDIENCE);
                                        intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, activeUsers.get(mPosition).getUsername().trim());
                                        startActivity(intent);
                                    } else if (activeUsers.get(mPosition).getBroadcast_type().equalsIgnoreCase("karaokeGroup")) {
                                        Intent intent = new Intent(mActivity, ActivityKaraokeDual.class);
                                        intent.putExtra("mode", false);
                                        intent.putExtra("name", activeUsers.get(mPosition).getUsername());
                                        intent.putExtra("selfname", SessionUser.getUser().getName());
                                        intent.putExtra("image", image);
                                        intent.putExtra("token", activeUsers.get(mPosition).getActivation_code());
                                        intent.putExtra("broadcasterId", activeUsers.get(mPosition).getUser_id());
                                        intent.putExtra("received", activeUsers.get(mPosition).getReceived());
                                        intent.putExtra("broadcaster", activeUsers.get(mPosition));
                                        intent.putParcelableArrayListExtra("users", activeUsers);
                                        intent.putExtra("position", mPosition);
                                        intent.putExtra("isFollowing", activeUsers.get(mPosition).getIsTheUserFollowing());
                                        intent.putExtra("isSwiped", false);
                                        intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_AUDIENCE);
                                        intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, activeUsers.get(mPosition).getUsername().trim());
                                        startActivity(intent);
                                    }*/ else if (activeUsers.get(mPosition).getBroadcast_type().equalsIgnoreCase("audio")) {
                                        Intent intent = new Intent(mActivity, ActivityAudioCall.class);
                                        intent.putExtra("mode", false);
                                        intent.putExtra("name", activeUsers.get(mPosition).getUsername());
                                        intent.putExtra("selfname", SessionUser.getUser().getName());
                                        intent.putExtra("image", image);
                                        intent.putExtra("token", activeUsers.get(mPosition).getActivation_code());
                                        intent.putExtra("broadcasterId", activeUsers.get(mPosition).getUser_id());
                                        intent.putExtra("received", activeUsers.get(mPosition).getReceived());
                                        intent.putExtra("broadcaster", activeUsers.get(mPosition));
                                        intent.putParcelableArrayListExtra("users", activeUsers);
                                        intent.putExtra("position", mPosition);
                                        intent.putExtra("isFollowing", activeUsers.get(mPosition).getIsTheUserFollowing());
                                        intent.putExtra("isSwiped", false);
                                        intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_AUDIENCE);
                                        intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, activeUsers.get(mPosition).getUsername().trim());
                                        startActivity(intent);
                                    } else if (activeUsers.get(mPosition).getBroadcast_type().equalsIgnoreCase("pk")) {
                                        showToast("Coming Soon!");
                                    }
                                } else
                                    showToast("User is Offline Now !");
                            }
                        } else {
                            showToast("User is Offline Now !");
                        }
                    } else {
                        checkResponseCode(response.code());
                        showToast(getString(R.string.server_error));
                    }
                } else {
                    checkResponseCode(response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<UsersResponse> call, @NonNull Throwable t) {
                showToast(t.getMessage());
            }
        });
    }


    @OnClick(R.id.cv_ok)
    public void onClickOk() {
        finishAffinity();
        changeActivity(ActivityHome.class);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
        changeActivity(ActivityHome.class);
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
}
