package com.blive.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.blive.adapter.AdapterFriends;
import com.blive.BLiveApplication;
import com.blive.model.User;
import com.blive.R;
import com.blive.model.UsersResponse;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.session.SessionUser;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sans on 10-09-2018.
 **/

public class ActivityUsers extends BaseBackActivity implements AdapterFriends.Listener {

    @BindView(R.id.rv_users)
    RecyclerView rvUsers;
    @BindView(R.id.tv_no_users)
    TextView tvNoUsers;

    private String type = "", title = "";
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<User> users, tempUser;
    private AdapterFriends adapterFriends;
    private boolean isUserListEnd = false, isRefreshing = false, isAPICalled = false;
    private int page = 1, lastPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        BLiveApplication.setCurrentActivity(this);
        initUI();
    }

    private void initUI() {

        Intent intent = getIntent();
        if (intent != null) {
            type = intent.getStringExtra("type");
            title = intent.getStringExtra("title");
        }
        setTitle(title);
        users = new ArrayList<>();
        tempUser = new ArrayList<>();
        swipeRefreshLayout = mActivity.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        rvUsers.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        tempUser.add(null);
        adapterFriends = new AdapterFriends(this, users);
        adapterFriends.setOnClickListener(this);
        rvUsers.setAdapter(adapterFriends);
        rvUsers.setVisibility(View.VISIBLE);

        rvUsers.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (!isRefreshing) {
                if (!isUserListEnd) {
                    if (!isAPICalled) {
                        if (isLastItemDisplaying()) {
                            if (page < lastPage) {
                                page = page + 1;
                                getUsers(page);
                            }
                        }
                    }
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            page = 1;
            isRefreshing = true;
            isUserListEnd = false;
            getUsers(page);
            swipeRefreshLayout.setRefreshing(false);
        });

        getUsers(page);
    }

    private boolean isLastItemDisplaying() {
        if (adapterFriends != null) {
            if (Objects.requireNonNull(rvUsers.getAdapter()).getItemCount() != 0) {
                if (!isUserListEnd) {
                    int lastVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(rvUsers.getLayoutManager())).findLastCompletelyVisibleItemPosition();
                    return lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == rvUsers.getAdapter().getItemCount() - 11;
                }
            }
            return false;
        }
        return false;
    }

    public void getUsers(int page) {
        if (!isAPICalled) {
            isAPICalled = true;
            if (utils.isNetworkAvailable()) {
                if (page > 1) {
                    adapterFriends.update(tempUser);
                }else
                    swipeRefreshLayout.setRefreshing(true);
                final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                Call<UsersResponse> call = apiClient.getUsers(type, String.valueOf(page), SessionUser.getUser().getUser_id());
                call.enqueue(new retrofit2.Callback<UsersResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
                        UsersResponse usersResponse = response.body();
                        if (response.code() == 200) {
                            if (usersResponse != null) {
                                if (usersResponse.getStatus().equalsIgnoreCase("success")) {
                                    lastPage = usersResponse.getData().getLast_page();
                                    isAPICalled = false;
                                    isRefreshing = false;
                                    if (page == 1) {
                                        swipeRefreshLayout.setRefreshing(false);
                                        if (usersResponse.getData().getUsers().size() > 0) {
                                            rvUsers.setVisibility(View.VISIBLE);
                                            tvNoUsers.setVisibility(View.GONE);
                                            adapterFriends.refresh(usersResponse.getData().getUsers());
                                        } else {
                                            rvUsers.setVisibility(View.GONE);
                                            tvNoUsers.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        adapterFriends.removeLastItem();
                                        if (page == lastPage)
                                            isUserListEnd = true;
                                        adapterFriends.update(usersResponse.getData().getUsers());
                                    }
                                }
                            } else {
                                utils.showToast(getString(R.string.server_error));
                            }
                        } else {
                            checkResponseCode(response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UsersResponse> call, @NonNull Throwable t) {
                       if(swipeRefreshLayout.isRefreshing())
                           swipeRefreshLayout.setRefreshing(false);
                        rvUsers.setVisibility(View.GONE);
                        tvNoUsers.setVisibility(View.VISIBLE);
                        showToast(t.getMessage());
                    }
                });
            }
        }
    }

    @Override
    public void OnClicked(User user) {
        Intent intent = new Intent(mActivity, ActivityViewProfile.class);
        intent.putExtra("image", user.getProfile_pic());
        intent.putExtra("userId", user.getUser_id());
        intent.putExtra("from", "list");
        startActivity(intent);
    }

}