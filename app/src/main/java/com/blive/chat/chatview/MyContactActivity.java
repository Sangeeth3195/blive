package com.blive.chat.chatview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blive.R;
import com.blive.activity.ActivitySignIn;
import com.blive.chat.chatadapter.ContactsAdapter;
import com.blive.chat.chatinterface.OnUserGroupItemClick;
import com.blive.chat.chatmodels.ChatUser;
import com.blive.chat.chatservice.FetchMyUsersService;
import com.blive.chat.chatutil.ChatUtils;
import com.blive.model.AudienceResponse;
import com.blive.model.User;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.session.SessionLogin;
import com.blive.session.SessionUser;
import com.blive.utils.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class MyContactActivity extends AppCompatActivity implements OnUserGroupItemClick {

    private RecyclerView myContactRecycler;
    private Utils utils;
    private int page = 1, lastPage = 0;
    private TextView tvNoFriendsList;
    private ArrayList<ChatUser> myUsers, finalUserList = new ArrayList<>();
    private ChatUtils helper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contact);

        uiInit();
        fetchContacts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(myUsersReceiver, new IntentFilter(ChatUtils.BROADCAST_MY_USERS));
    }

    private void uiInit() {
        helper = new ChatUtils(MyContactActivity.this);
        utils = new Utils(MyContactActivity.this);
        myContactRecycler = findViewById(R.id.myContactRecycler);
        tvNoFriendsList = findViewById(R.id.tvNoFriendsList);

        findViewById(R.id.back).setOnClickListener(v -> finish());

        myContactRecycler.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (page < lastPage) {
                page = page + 1;
                fetchContacts();
            }
        });

    }

    private void fetchFriends(int page) {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<AudienceResponse> call = apiClient.getInvitesList("friend", String.valueOf(page), SessionUser.getUser().getUser_id());
            call.enqueue(new retrofit2.Callback<AudienceResponse>() {
                @Override
                public void onResponse(@NonNull Call<AudienceResponse> call, @NonNull Response<AudienceResponse> response) {
                    AudienceResponse invitesResponse = response.body();
                    utils.hideProgress();
                    if (response.code() == 200) {
                        if (invitesResponse != null) {
                            if (invitesResponse.getStatus().equalsIgnoreCase("success")) {
                                finalUserList.clear();
                                for (ChatUser chatUser : myUsers) {
                                    for (User user : invitesResponse.getData().getUserList()) {
                                        if (chatUser != null && chatUser.getId() != null &&
                                                !chatUser.getId().equalsIgnoreCase(SessionUser.getUser().getUser_id())) {
                                            if (chatUser.getId().equals(user.getUser_id())) {
                                                finalUserList.add(chatUser);
                                            }
                                        }
                                    }
                                }
                                if (finalUserList.size() > 0) {
                                    myContactRecycler.setVisibility(View.VISIBLE);
                                    tvNoFriendsList.setVisibility(View.GONE);
                                    myContactRecycler.setAdapter(new ContactsAdapter(MyContactActivity.this, finalUserList));
                                } else {
                                    myContactRecycler.setVisibility(View.GONE);
                                    tvNoFriendsList.setVisibility(View.VISIBLE);
                                }
                            } else {
                                myContactRecycler.setVisibility(View.GONE);
                                tvNoFriendsList.setVisibility(View.VISIBLE);
                                utils.showToast(invitesResponse.getMessage());
                            }
                        } else {
                            myContactRecycler.setVisibility(View.GONE);
                            tvNoFriendsList.setVisibility(View.VISIBLE);
                            utils.showToast(getString(R.string.server_error));
                        }
                    } else {
                        myContactRecycler.setVisibility(View.GONE);
                        tvNoFriendsList.setVisibility(View.VISIBLE);
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AudienceResponse> call, @NonNull Throwable t) {
                    myContactRecycler.setVisibility(View.GONE);
                    tvNoFriendsList.setVisibility(View.VISIBLE);
                    utils.showToast(t.getMessage());
                    utils.hideProgress();
                }
            });
        }
    }

    private void checkResponseCode(int code) {
        switch (code) {
            case 401:
                SessionLogin.clearLoginSession();
                Intent intent = new Intent(MyContactActivity.this, ActivitySignIn.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case 500:
                utils.showToast("Please try again Server Error!");
                break;
            default:
                utils.showToast("Something went wrong Please try again!");
        }
    }

    private void fetchContacts() {
        if (!FetchMyUsersService.STARTED) {
            utils.showProgress();
            FetchMyUsersService.startMyUsersService(MyContactActivity.this, SessionUser.getUser().getUser_id(), "");
        }
    }

    private BroadcastReceiver myUsersReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            myUsers = intent.getParcelableArrayListExtra("data");
            helper.setCacheMyUsers(myUsers);
            if (myUsers != null) {
                fetchFriends(page);
            }
        }
    };

    @Override
    public void OnUserClick(ChatUser user, int position, View userImage) {
        startActivity(FireBaseChatActivity.newIntent(this, new ArrayList<>(), user));
    }
}
