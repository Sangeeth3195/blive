package com.blive.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blive.activity.ActivityHome;
import com.blive.adapter.AdapterUsers;
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

public class FragmentNearBy extends BaseFragment implements AdapterUsers.ListenerChannel {

    @BindView(R.id.rv_users)
    RecyclerView rvUsers;
    @BindView(R.id.tv_no_users)
    TextView tvNoUsers;

    private SwipeRefreshLayout swipeRefreshLayout;
    private AdapterUsers adapterUsers;
    private User user;
    private boolean isUserListEnd = false, isClicked = false, isAPICalled = false, isRefreshing = false;
    private ArrayList<User> users, tempUser;
    private int position = -1, page = 1, lastPage = 1;
    private String countryName = "", cityName = "";

    public FragmentNearBy() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
    }

    private void initUI() {

        swipeRefreshLayout = mActivity.findViewById(R.id.swipeRefreshNearBy);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        users = new ArrayList<>();
        tempUser = new ArrayList<>();
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        rvUsers.setLayoutManager(manager);
        tempUser.add(null);
        adapterUsers = new AdapterUsers(mActivity, users);
        adapterUsers.setOnClickListener(this);
        rvUsers.setAdapter(adapterUsers);
        rvUsers.setVisibility(View.VISIBLE);

        countryName = SessionUser.getUser().getCountry();
        cityName = SessionUser.getUser().getCity();

        rvUsers.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (!isRefreshing) {
                if (!isUserListEnd) {
                    if (!isAPICalled) {
                        if (isLastItemDisplaying()) {
                            if (page < lastPage) {
                                page = page + 1;
                                getNearByUsers(page);
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
            getNearByUsers(page);
            swipeRefreshLayout.setRefreshing(false);
        });

        getNearByUsers(page);
    }

    public void getNearByUsers(int page) {
        if(utils.isNetworkAvailable()){
            if (!isAPICalled) {
                isAPICalled = true;
                if(page == 1)
                    adapterUsers.refresh(tempUser);
                else
                    adapterUsers.update(tempUser);

                final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                Call<UsersResponse> call = apiClient.getNearByUsers("all", cityName, String.valueOf(page), SessionUser.getUser().getUser_id());
                call.enqueue(new retrofit2.Callback<UsersResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
                        UsersResponse usersResponse = response.body();
                        if(response.code() == 200){
                            if (usersResponse != null) {
                                if (usersResponse.getStatus().equalsIgnoreCase("success")) {
                                    lastPage = usersResponse.getData().getLast_page();
                                    isAPICalled = false;
                                    isRefreshing = false;
                                    adapterUsers.removeLastItem();
                                    if (page == 1) {
                                        if (usersResponse.getData().getUsers().size() > 0) {
                                            rvUsers.setVisibility(View.VISIBLE);
                                            tvNoUsers.setVisibility(View.GONE);
                                            adapterUsers.refresh(usersResponse.getData().getUsers());
                                        } else {
                                            rvUsers.setVisibility(View.GONE);
                                            tvNoUsers.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        if (page == lastPage)
                                            isUserListEnd = true;
                                        adapterUsers.update(usersResponse.getData().getUsers());
                                    }
                                }
                            } else {
                                utils.showToast(getString(R.string.server_error));
                            }
                        }else {
                            checkResponseCode(response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UsersResponse> call, @NonNull Throwable t) {
                        adapterUsers.removeLastItem();
                        rvUsers.setVisibility(View.GONE);
                        tvNoUsers.setVisibility(View.VISIBLE);
                        showToast(t.getMessage());
                    }
                });
            }
        }
    }

    private boolean isLastItemDisplaying() {
        if (adapterUsers != null) {
            if (Objects.requireNonNull(rvUsers.getAdapter()).getItemCount() != 0) {
                int lastVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(rvUsers.getLayoutManager())).findLastCompletelyVisibleItemPosition();
                return lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == rvUsers.getAdapter().getItemCount() - 11;
            }
            return false;
        }
        return false;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_listview, container, false);
    }

    @Override
    public void OnClicked(int mPosition, User mUser) {
        final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
        Call<UsersResponse> call = apiClient.getBroadcastType(mUser.getUser_id());
/*
        call.enqueue(new retrofit2.Callback<UsersResponse>() {
            @Override
            public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
                utils.hideProgress();
                UsersResponse usersResponse = response.body();
                if (response.code() == 200) {
                    if (usersResponse != null) {
                        if (usersResponse.getStatus().equalsIgnoreCase("success")) {
                            if(!usersResponse.getData().getBroadcastType().equalsIgnoreCase(mUser.getBroadcast_type()))
                                if (mUser.getBroadcast_type().equalsIgnoreCase("pk")) {
                                    if (usersResponse.getData().getGuest_details().getChallenge_time_left().length() != 0) {
                                        mUser.setPkTimeLeft(Integer.parseInt(usersResponse.getData().getGuest_details().getChallenge_time_left()));
                                        mUser.setPk_broadcaster_id(usersResponse.getData().getGuest_details().getPk_broadcaster_id());
                                        mUser.setPk_guest_id(usersResponse.getData().getGuest_details().getPk_guest_id());
                                        Log.i("autolog", "usersResponse: " + usersResponse.getData().getGuest_details().getPk_guest_id() + usersResponse.getData().getGuest_details().getPk_broadcaster_id());
                                    }
                                }else {
                                    mUser.setBroadcast_type(usersResponse.getData().getBroadcastType());
                                }
                            */
/*if (mUser.getBroadcast_type().equalsIgnoreCase("pk")) {
                                if (usersResponse.getData().getGuest_details().getChallenge_time_left().length() != 0) {
                                    mUser.setPkTimeLeft(Integer.parseInt(usersResponse.getData().getGuest_details().getChallenge_time_left()));
                                }
                            }*//*


                            if (!mUser.getBroadcast_type().isEmpty()) {
                                user = mUser;
                                position = mPosition;
                                ((ActivityHome) Objects.requireNonNull(getActivity())).getUser(user, users, position);
                                //BLiveApplication.getInstance().getmAgoraAPI().login2(Constants_app.appId, SessionUser.getUser().getUsername().trim(), "_no_need_token", 0, "", 5, 1);
                            } else
                                showToast("User is Offline Now !");
                        } else {
                            if (!mUser.getBroadcast_type().isEmpty()) {
                                user = mUser;
                                position = mPosition;
                                ((ActivityHome) Objects.requireNonNull(getActivity())).getUser(user, users, position);
                                //BLiveApplication.getInstance().getmAgoraAPI().login2(Constants_app.appId, SessionUser.getUser().getUsername().trim(), "_no_need_token", 0, "", 5, 1);
                            } else
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
                showToast(t.getMessage());
            }
        });
*/

        call.enqueue(new retrofit2.Callback<UsersResponse>() {
            @Override
            public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
                UsersResponse usersResponse = response.body();
                if (response.code() == 200) {
                    if (usersResponse != null) {
                        if (usersResponse.getStatus().equalsIgnoreCase("success")) {
                            if (!usersResponse.getData().getBroadcastType().equalsIgnoreCase(mUser.getBroadcast_type()))
                                mUser.setBroadcast_type(usersResponse.getData().getBroadcastType());

                            if (mUser.getBroadcast_type().equalsIgnoreCase("pk")) {
                                if (usersResponse.getData().getGuest_details().getChallenge_time_left().length() != 0) {
                                    mUser.setPkTimeLeft(Integer.parseInt(usersResponse.getData().getGuest_details().getChallenge_time_left()));
                                    mUser.setPk_broadcaster_id(usersResponse.getData().getGuest_details().getPk_broadcaster_id());
                                    mUser.setPk_guest_id(usersResponse.getData().getGuest_details().getPk_guest_id());
                                    Log.i("autolog", "usersResponse: " + usersResponse.getData().getGuest_details().getPk_guest_id() + usersResponse.getData().getGuest_details().getPk_broadcaster_id());
                                }
                            }

                            if (!mUser.getBroadcast_type().isEmpty()) {
                                user = mUser;
                                position = mPosition;
                                ((ActivityHome) Objects.requireNonNull(getActivity())).getUser(user, users, position);
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
                showToast(t.getMessage());
            }
        });
    }

}