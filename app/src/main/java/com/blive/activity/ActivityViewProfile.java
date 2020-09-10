package com.blive.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blive.adapter.AdapterProfilePic;
import com.blive.BLiveApplication;
import com.blive.chat.chatmodels.ChatUser;
import com.blive.chat.chatview.FireBaseChatActivity;
import com.blive.constant.Constants_app;
import com.blive.model.FollowResponse;
import com.blive.model.GenericResponse;
import com.blive.model.ProfileResponse;
import com.blive.model.User;
import com.blive.R;
import com.blive.model.UsersResponse;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.session.SessionUser;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import io.agora.rtc.Constants;
import jp.wasabeef.picasso.transformations.BlurTransformation;
import retrofit2.Call;
import retrofit2.Response;

public class ActivityViewProfile extends BaseBackActivity {

    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.bFans)
    ShineButton bFans;
    @BindView(R.id.bFollowers)
    ShineButton bFollowers;
    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.iv_blur)
    ImageView ivBlur;
    @BindView(R.id.tv_fans)
    TextView tvFans;
    @BindView(R.id.tv_gender)
    TextView tvGender;
    @BindView(R.id.tv_dob)
    TextView tvDob;
    @BindView(R.id.tv_age)
    TextView tvAge;
    @BindView(R.id.tv_followers)
    TextView tvFollowers;
    @BindView(R.id.tv_gold)
    TextView tvOverAllGold;
    @BindView(R.id.tv_level)
    TextView tvLevel;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.ll_follow)
    LinearLayout cvFollow;
    @BindView(R.id.ll_un_follow)
    LinearLayout cvUnFollow;
    @BindView(R.id.ll_block)
    LinearLayout cvBlock;
    @BindView(R.id.ll_unBlock)
    LinearLayout cvUnBlock;
    @BindView(R.id.cv_topFans)
    CardView cv_topFans;
    @BindView(R.id.ll_status)
    LinearLayout llStatus;
    @BindView(R.id.cv_image)
    CardView cvImage;
    @BindView(R.id.tv_reSeller)
    TextView tvreSeller;
    private String image = "", userId = "", followers = "", activationToken = "", following = "", from = "", isFriends = "";
    private User user;
    private ImageView ivEffect;
    private boolean isFollowing = false, isLoggedIn = false, isClicked = false;
    ImageView img_unfollow;
    private ChatUser chatUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        BLiveApplication.setCurrentActivity(this);
        initUI();
    }

    private void initUI() {

        Intent intent = getIntent();

        if (intent != null) {
            image = intent.getStringExtra("image");
            userId = intent.getStringExtra("userId");
            from = intent.getStringExtra("from");

            if (from.equalsIgnoreCase("chat")) {
                chatUser = intent.getParcelableExtra("chatUser");
            } else {
                BLiveApplication.getUserRef().child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            chatUser = dataSnapshot.getValue(ChatUser.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            if (image != null && !image.isEmpty()) {
                Picasso.get().load(image).fit().centerCrop().transform(new BlurTransformation(this)).memoryPolicy(MemoryPolicy.NO_STORE).
                        placeholder(R.drawable.user).centerCrop().into(ivBlur);
                Picasso.get().load(image).fit().centerCrop().into(iv);
            } else {
                Glide.with(this).load(R.drawable.user).into(iv);
                Glide.with(this).load(R.drawable.user).into(ivBlur);
            }
        }

        ivEffect = findViewById(R.id.iv_effect);
        img_unfollow = findViewById(R.id.img_unfollow);

        bFans.init(mActivity);
        bFollowers.init(mActivity);

        bFans.setChecked(true);
        bFans.setEnabled(false);
        bFollowers.setChecked(true);
        bFollowers.setEnabled(false);

        if (utils.isNetworkAvailable()) {
            utils.showProgress();
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<ProfileResponse> call = apiClient.getProfile(SessionUser.getUser().getUser_id(), userId);
            call.enqueue(new retrofit2.Callback<ProfileResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                    utils.hideProgress();
                    ProfileResponse profileResponse = response.body();
                    if (response.code() == 200) {
                        if (profileResponse != null) {
                            if (profileResponse.getStatus().equalsIgnoreCase("success")) {
                                Log.e(TAG, "onResponse: " + profileResponse.toString());
                                setProfileData(profileResponse.getData().getUser(), profileResponse.getData().getIsThisUserFollowing(), profileResponse.getData().getIsTheUserBlocked(), profileResponse.getData().getIsThisUserFriends(),profileResponse.getData().getUser().getRelationSymbol());
                            } else {
                                showToast(profileResponse.getMessage());
                            }
                        } else {
                            showToast(getString(R.string.server_error));
                        }
                    } else {
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                    utils.hideProgress();
                    showToast(t.getMessage());
                }
            });
        }
    }

    @Override
    public void onResume() {
        isClicked = false;
        super.onResume();
    }


    @OnClick(R.id.ll_follow)
    public void onClickFollow() {
        try {
            callFollowAPI();
        } catch (Exception e) {
            Log.e(TAG, "onClick: " + e);
            Crashlytics.logException(e);
        }
    }

    private void callFollowAPI() {
        if (utils.isNetworkAvailable()) {
            /* utils.showProgress();*/
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<FollowResponse> call = apiClient.follow("follow", user.getUser_id(), SessionUser.getUser().getUser_id());
            call.enqueue(new retrofit2.Callback<FollowResponse>() {
                @Override
                public void onResponse(@NonNull Call<FollowResponse> call, @NonNull Response<FollowResponse> response) {
                    FollowResponse followResponse = response.body();
                    /* utils.hideProgress();*/
                    if (response.code() == 200) {
                        if (followResponse != null) {
                            if (followResponse.getStatus().equalsIgnoreCase("success")) {
                                showToast(followResponse.getData().getMessage());
                                cvFollow.setVisibility(View.GONE);

                                cvUnFollow.setVisibility(View.VISIBLE);

                                int follow = Integer.valueOf(followers);
                                follow = follow + 1;
                                followers = String.valueOf(follow);
                                tvFollowers.setText(followers);
                                isFollowing = true;
                                User user = SessionUser.getUser();
                                user.setFans(followResponse.getData().getFans_count());
                                user.setFollowers(followResponse.getData().getFollower_count());
                                user.setFriends(followResponse.getData().getFriend_count());
                                SessionUser.saveUser(user);
                            } else {
                                showToast(followResponse.getMessage());
                            }
                        } else {
                            utils.showToast(getString(R.string.server_error));
                        }
                    } else {
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<FollowResponse> call, @NonNull Throwable t) {
                    showToast(t.getMessage());
                }
            });
        }
    }

    @OnClick(R.id.ll_message)
    public void onClickMessage() {
        if (chatUser != null && chatUser.getId() != null) {
            Intent intent = FireBaseChatActivity.newIntent(this, new ArrayList<>(), chatUser);
            startActivity(intent);
        } else {
            showToast("You are not able to chat with this user. You can chat only with online users..");
        }
    }

    @OnClick(R.id.cv_topFans)
    public void onClickTopFans() {
        if (!isClicked) {
            isClicked = true;
            if (utils.isNetworkAvailable()) {
                try {
                    Intent intent = new Intent(ActivityViewProfile.this, ActivityTopFans.class);
                    intent.putExtra("activationToken", activationToken);
                    intent.putExtra("user_id", user.getUser_id());
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "onClick: " + e);
                    Crashlytics.logException(e);
                }
            }
        }
    }

    @OnClick(R.id.ll_un_follow)
    public void onClickUnFollow() {
        try {
            callUnFollowAPI();
        } catch (Exception e) {
            Log.e(TAG, "onClick: " + e);
            Crashlytics.logException(e);
        }
    }

    private void callUnFollowAPI() {
        if (utils.isNetworkAvailable()) {
            /*  utils.showProgress();*/

            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<FollowResponse> call = apiClient.follow("unfollow", user.getUser_id(), SessionUser.getUser().getUser_id());
            call.enqueue(new retrofit2.Callback<FollowResponse>() {
                @Override
                public void onResponse(@NonNull Call<FollowResponse> call, @NonNull Response<FollowResponse> response) {
                    FollowResponse followResponse = response.body();
                    /*       utils.hideProgress();*/
                    if (response.code() == 200) {
                        if (followResponse != null) {
                            if (followResponse.getStatus().equalsIgnoreCase("success")) {
                                showToast(followResponse.getData().getMessage());
                                cvFollow.setVisibility(View.VISIBLE);
                                cvUnFollow.setVisibility(View.GONE);

                                int follow = Integer.valueOf(followers);
                                follow = follow - 1;
                                followers = String.valueOf(follow);
                                tvFollowers.setText(followers);
                                isFollowing = false;
                                checkPrivacy();
                            } else {
                                showToast(followResponse.getMessage());
                            }
                        } else {
                            utils.showToast(getString(R.string.server_error));
                        }
                    } else {
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<FollowResponse> call, @NonNull Throwable t) {
                    utils.hideProgress();
                    showToast(t.getMessage());
                }
            });
        }
    }

    @OnClick(R.id.ll_block)
    public void onBlock() {
        try {
            callBlockAPI();
        } catch (Exception e) {
            Log.e(TAG, "onClick: " + e);
            Crashlytics.logException(e);
        }
    }

    private void callBlockAPI() {
        if (utils.isNetworkAvailable()) {
            /* utils.showProgress();*/
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GenericResponse> call = apiClient.block("block", user.getUser_id(), SessionUser.getUser().getUser_id());
            call.enqueue(new retrofit2.Callback<GenericResponse>() {
                @Override
                public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                    GenericResponse genericResponse = response.body();
                    /* utils.hideProgress();*/
                    if (response.code() == 200) {
                        if (genericResponse != null) {
                            if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                                showToast(genericResponse.getData().getMessage());
                                cvBlock.setVisibility(View.GONE);
                                cvUnBlock.setVisibility(View.VISIBLE);
                            } else {
                                showToast(genericResponse.getMessage());
                            }
                        } else {
                            utils.showToast(getString(R.string.server_error));
                        }
                    } else {
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                    utils.hideProgress();
                    showToast(t.getMessage());
                }
            });
        }
    }

    @OnClick(R.id.ll_unBlock)
    public void onUnBlock() {
        try {
            callUnBlockAPI();
        } catch (Exception e) {
            Log.e(TAG, "onClick: " + e);
            Crashlytics.logException(e);
        }
    }

    private void callUnBlockAPI() {
        if (utils.isNetworkAvailable()) {
            /* utils.showProgress();*/
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GenericResponse> call = apiClient.block("unblock", user.getUser_id(), SessionUser.getUser().getUser_id());
            call.enqueue(new retrofit2.Callback<GenericResponse>() {
                @Override
                public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                    GenericResponse genericResponse = response.body();
                    /*  utils.hideProgress();*/
                    if (response.code() == 200) {
                        if (genericResponse != null) {
                            if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                                showToast(genericResponse.getData().getMessage());
                                cvUnBlock.setVisibility(View.GONE);
                                cvBlock.setVisibility(View.VISIBLE);
                            } else {
                                showToast(genericResponse.getMessage());
                            }
                        } else {
                            utils.showToast(getString(R.string.server_error));
                        }
                    } else {
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                    utils.hideProgress();
                    showToast(t.getMessage());
                }
            });
        }
    }

    @OnClick({R.id.iv_blur, R.id.iv, R.id.cv_image})
    public void onClickImage() {
        if (user.getStatus().equalsIgnoreCase("ACTIVE")) {
            Log.d("cRole", "cRole: "+ActivityLiveRoom.cRole+ActivityLiveRoom.isGuest1+ActivityLiveRoom.isGuest2);
            if (ActivityLiveRoom.cRole != 1&&!ActivityLiveRoom.isGuest1&&!ActivityLiveRoom.isGuest2) {
                gotolive(user);
            }else {
                showToast("You are a Broadcaster");
            }

            //utils.showProgress();
            //BLiveApplication.getInstance().getmAgoraAPI().login2(Constants_app.appId, SessionUser.getUser().getUsername().trim(), "_no_need_token", 0, "", 5, 1);
        }
    }

    @OnClick(R.id.rlBack)
    public void onClickBack() {
        finish();
    }

    public void setProfileData(User mUser, String mFollowing, String block, String isThisUserFriends,String relationship) {
        utils.hideProgress();
        user = mUser;
        following = mFollowing;
        isFriends = isThisUserFriends;
        tvName.setText(user.getName());
        tvFans.setText(user.getFans());
        followers = user.getFollowers();
        tvFollowers.setText(followers);
        tvLevel.setText(user.getLevel());
        activationToken = user.getActivation_code();
        tvOverAllGold.setText(user.getOver_all_gold());
        String base64 = user.getProfile_pic();

        String reseller = user.getReseller();

        tvreSeller.setText(reseller);

        try {
            image = URLDecoder.decode(base64, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Glide.with(this)
                .load(user.getTools_applied())
                .into(ivEffect);

        ArrayList<String> arrayList = new ArrayList<>();

        String image1 = Constants_app.decodeImage(user.getProfile_pic());
//        String image2 = Constants_app.decodeImage(user.getProfile_pic1());
//        String image3 = Constants_app.decodeImage(user.getProfile_pic2());

        arrayList.add(image1);
//        arrayList.add(image2);
//        arrayList.add(image3);

        LayoutInflater factory1 = LayoutInflater.from(getApplicationContext());
        final View view = factory1.inflate(R.layout.view_profile_pic, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(ActivityViewProfile.this).create();
        alertDialog.setView(view);

        ViewPager viewPager = view.findViewById(R.id.viewPager);

        AdapterProfilePic adapterProfilePic = new AdapterProfilePic(getApplicationContext(), arrayList);
        viewPager.setAdapter(adapterProfilePic);

        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);

        iv.setOnClickListener(v -> {
            alertDialog.show();
        });

        if (user.getStatus().equalsIgnoreCase("ACTIVE")) {
            llStatus.setVisibility(View.VISIBLE);
        } else {
            llStatus.setVisibility(View.GONE);
        }

        if (following.equalsIgnoreCase("yes")) {
            if (relationship.equalsIgnoreCase("3")){
                img_unfollow.setImageDrawable(getApplication().getDrawable(R.drawable.ic_mutual));
            }else {
                img_unfollow.setImageDrawable(getApplication().getDrawable(R.drawable.remove));
            }
            isFollowing = true;
            cvFollow.setVisibility(View.GONE);
            cvUnFollow.setVisibility(View.VISIBLE);
        } else {
            isFollowing = false;
            cvFollow.setVisibility(View.VISIBLE);
            cvUnFollow.setVisibility(View.GONE);
        }

        checkPrivacy();

        if (block.equalsIgnoreCase("yes")) {
            cvBlock.setVisibility(View.GONE);
            cvUnBlock.setVisibility(View.VISIBLE);
        } else {
            cvBlock.setVisibility(View.VISIBLE);
            cvUnBlock.setVisibility(View.GONE);
        }
    }

    private void checkPrivacy() {

        if (user.getIs_the_user_id_hidden().equalsIgnoreCase("yes")) {
            tvId.setText("BLive ID:" + getString(R.string.hidden));
        } else {
            tvId.setText("BLive ID:" + user.getReference_user_id());
        }

        if (user.getIs_the_age_hidden().equalsIgnoreCase("yes")) {
            tvAge.setText(getString(R.string.hidden));
        } else {
            tvAge.setText(user.getAge());
        }

        if (user.getIs_the_gender_hide().equalsIgnoreCase("yes")) {
            tvGender.setText(getString(R.string.hidden));
        } else {
            tvGender.setText(user.getGender());
        }

        if (user.getIs_the_dob_hidden().equalsIgnoreCase("yes")) {
            tvDob.setText(getString(R.string.hidden));
        } else {
            if (user.getDate_of_birth().equalsIgnoreCase("0"))
                tvDob.setText(getString(R.string.not_mentioned));
            else
                tvDob.setText(user.getDate_of_birth());
        }
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = getIntent();
        returnIntent.putExtra("follow", isFollowing);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @OnClick(R.id.rl_back)
    public void onBackClicked() {
        Intent returnIntent = getIntent();
        returnIntent.putExtra("follow", isFollowing);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }


    public void gotolive(User userData) {

//        ArrayList<User> newAUserList = new ArrayList<>();
//        newAUserList.add(userData);
//        ((ActivityHome) Objects.requireNonNull(getActivity())).getUser(userData, newAUserList, 0);

        Log.e(TAG, "OnClicked: times one");
//              utils.showProgress();
        final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
        Call<UsersResponse> call = apiClient.getBroadcastType(userData.getUser_id());
        call.enqueue(new retrofit2.Callback<UsersResponse>() {
            @Override
            public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
//                     utils.hideProgress();
                UsersResponse usersResponse = response.body();
                if (response.code() == 200) {
                    if (usersResponse != null) {
                        if (usersResponse.getStatus().equalsIgnoreCase("success")) {
                            if (!usersResponse.getData().getBroadcastType().equalsIgnoreCase(userData.getBroadcast_type()))
                                userData.setBroadcast_type(usersResponse.getData().getBroadcastType());

                            if (userData.getBroadcast_type().equalsIgnoreCase("pk")) {
                                if (usersResponse.getData().getGuest_details().getChallenge_time_left().length() != 0) {
                                    userData.setPkTimeLeft(Integer.parseInt(usersResponse.getData().getGuest_details().getChallenge_time_left()));
                                    userData.setPk_broadcaster_id(usersResponse.getData().getGuest_details().getPk_broadcaster_id());
                                    userData.setPk_guest_id(usersResponse.getData().getGuest_details().getPk_guest_id());
                                    userData.setPk_channelname(usersResponse.getData().getGuest_details().getPk_channelname());
                                    Log.i("autolog", "usersResponse: " + usersResponse.getData().getGuest_details().getPk_guest_id() + usersResponse.getData().getGuest_details().getPk_broadcaster_id());
                                }
                            }

                            if (!userData.getBroadcast_type().isEmpty()) {
                                user = userData;
                                int position = 1;
                                ArrayList<User> users = new ArrayList<>();
                                runOnUiThread(() -> {
                                    utils.hideProgress();
                                    if (user != null) {
                                        String base64 = user.getProfile_pic();
                                        try {
                                            image = URLDecoder.decode(base64, "UTF-8");
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
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
                                            intent.putExtra("rtmname", user.getUsername().trim());
                                            intent.putExtra("isSwiped", false);
                                            intent.putExtra("PKuserId", "0");
                                            intent.putExtra("broad_type", "solo_viewprofile");
                                            intent.putExtra("pkTimer", user.getPkTimeLeft());
                                            if (Constants_app.BAudiance == 1) {
                                                intent.putExtra("broadcasterAudience", false);
                                                Log.i("autolog", "broadcasterAudience: " + "broadcasterAudience");
                                                intent.putExtra("guestAudience", false);
                                            } else {
                                                intent.putExtra("broadcasterAudience", false);
                                                intent.putExtra("guestAudience", false);
                                                Log.i("autolog", "guestAudience: " + "guestAudience");
                                            }
                                            intent.putExtra("intermediateJoin", false);
                                            startActivity(intent);
                                            finishAffinity();
                                        } else if (user.getBroadcast_type().equalsIgnoreCase("pk")) {
                                            Log.d(TAG, "onChannelJoinSuccess: " + "pk");
                                            Intent intent = new Intent(mActivity, ActivityLiveRoom.class);
                                            intent.putExtra("mode", false);
                                            intent.putExtra("broad_type", "pk_viewprofile");
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
                                                intent.putExtra("guestAudience", false);
                                            } else {
                                                intent.putExtra("broadcasterAudience", false);
                                                intent.putExtra("guestAudience", true);
                                                Log.i("autolog", "guestAudience: " + "guestAudience");
                                            }
                                            intent.putParcelableArrayListExtra("users", users);
                                            intent.putExtra("position", position);
                                            intent.putExtra("isFollowing", user.getIsTheUserFollowing());
                                            intent.putExtra(Constants_app.ACTION_KEY_CROLE, Constants.CLIENT_ROLE_AUDIENCE);
                                            intent.putExtra(Constants_app.ACTION_KEY_ROOM_NAME, user.getUsername().trim());
                                            intent.putExtra("rtmname", user.getPk_channelname().trim());
                                            intent.putExtra("isSwiped", false);
                                            intent.putExtra("intermediateJoin", true);
                                            Log.d(TAG, "onChannelJoinSuccess: " + intent.getExtras().toString());
                                            startActivity(intent);
                                            finishAffinity();
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
                                            finishAffinity();
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
                                            finishAffinity();
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
                                            finishAffinity();
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
                                            finishAffinity();
                                        } /*else if (user.getBroadcast_type().equalsIgnoreCase("karaokeSolo")) {
                            Intent intent = new Intent(mActivity, ActivityKaraokeSolo.class);
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
                        } else if (user.getBroadcast_type().equalsIgnoreCase("karaokeGroup")) {
                            Intent intent = new Intent(mActivity, ActivityKaraokeDual.class);
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
                        }*/ else if (user.getBroadcast_type().equalsIgnoreCase("audio")) {
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
                                            finishAffinity();
                                        } else {
                                            showToast("User is Offline!");
                                        }
                                    }
                                });
                            } else
                                showToast("User is Offline Now !");
                        } else {
                            showToast("User is Offline Now !");
                        }
                    } else {
                        showToast(getString(R.string.server_error));
                    }
                } else {
                    checkResponseCode(response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<UsersResponse> call, @NonNull Throwable t) {
//                      utils.hideProgress();
                showToast(t.getMessage());
            }
        });
    }


}