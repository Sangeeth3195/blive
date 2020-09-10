package com.blive.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.http.SslError;
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
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blive.activity.ActivityHome;
import com.blive.adapter.AdapterUsers;
import com.blive.constant.Constants_api;
import com.blive.constant.Constants_app;
import com.blive.model.UsersResponse;
import com.blive.model.Country;
import com.blive.model.User;
import com.blive.R;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.session.SessionUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class FragmentUniverse extends BaseFragment implements AdapterUsers.ListenerChannel {

    @BindView(R.id.rv_newUsers)
    RecyclerView rvNewUsers;
    @BindView(R.id.tv_no_users)
    TextView tvNoUsers;
    @BindView(R.id.spCountry)
    SearchableSpinner spCountryName;
    @BindView(R.id.countryIcon)
    ImageView countryIcon;
    @BindView(R.id.webView)
    WebView webView;

    private SwipeRefreshLayout swipeRefreshUniverse;
    private User user;
    String regId;
    private ArrayList<User> users, tempUser;
    private int position = -1, page = 1, lastPage = 1, countryPosition = 0;
    String country;
    private AdapterUsers adapterUsers;
    private boolean isUserListEnd = false, isAPICalled = false, isRefreshing = false, isClicked = false;

    public FragmentUniverse() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        displayFirebaseRegId();
        initUI();
    }

    private void displayFirebaseRegId() {
        regId = FirebaseInstanceId.getInstance().getToken();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initUI() {

        webView.loadUrl(Constants_api.topperBanner);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(false);
        webView.setOnClickListener(null);
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.notification_error_ssl_cert_invalid);
                builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.proceed();
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.cancel();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        swipeRefreshUniverse = mActivity.findViewById(R.id.swipeRefreshUniverse);
        swipeRefreshUniverse.setColorSchemeResources(R.color.colorAccent);

        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        rvNewUsers.setLayoutManager(manager);
        rvNewUsers.setVisibility(View.GONE);
        rvNewUsers.setNestedScrollingEnabled(false);
        users = new ArrayList<>();
        tempUser = new ArrayList<>();
        country = SessionUser.getUser().getCountry();
        /* countryName.setText(country);*/

        Constants_app constants_app = new Constants_app();
        ArrayList<Country> countryArrayList = constants_app.getCountry(getActivity());
        ArrayList<String> countries = new ArrayList<>();
        for (int i = 0; i < countryArrayList.size(); i++) {
            countries.add(countryArrayList.get(i).getName());
            if(countryArrayList.get(i).getName().equalsIgnoreCase(SessionUser.getUser().getCountry()))
                countryPosition = i;
        }
        // Creating adapter for spinner
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, countries);
        spCountryName.setAdapter(countryAdapter);
        spCountryName.setSelection(countryPosition);


        tempUser.add(null);
        adapterUsers = new AdapterUsers(mActivity, users);
        adapterUsers.setOnClickListener(this);
        rvNewUsers.setAdapter(adapterUsers);
        rvNewUsers.setVisibility(View.VISIBLE);

        spCountryName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    Country countrySelected = countryArrayList.get(position);
                    country = countrySelected.getName();
                    page = 1;
                    getNewUsers(country, page);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rvNewUsers.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (!isRefreshing) {
                if (!isUserListEnd) {
                    if (isLastItemDisplaying()) {
                        if (page < lastPage) {
                            page = page + 1;
                            getNewUsers(country, page);
                        }
                    }
                }
            }
        });

        swipeRefreshUniverse.setOnRefreshListener(() -> {
            page = 1;
            isUserListEnd = false;
            isRefreshing = true;
            getNewUsers(country, page);
            swipeRefreshUniverse.setRefreshing(false);
        });

        getNewUsers(country, page);
    }

    private boolean isLastItemDisplaying() {
        if (adapterUsers != null) {
            if (Objects.requireNonNull(rvNewUsers.getAdapter()).getItemCount() != 0) {
                if (!isUserListEnd) {
                    int lastVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(rvNewUsers.getLayoutManager())).findLastCompletelyVisibleItemPosition();
                    return lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == rvNewUsers.getAdapter().getItemCount() - 11;
                }
            }
            return false;
        }
        return false;
    }

    public void getNewUsers(String country, int page) {
        if (utils.isNetworkAvailable()) {
            if (!isAPICalled) {
                isAPICalled = true;
                if (page > 1) {
                    adapterUsers.update(tempUser);
                } else
                    swipeRefreshUniverse.setRefreshing(true);

                final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                Call<UsersResponse> call = apiClient.getUniverseUsers("all", country, String.valueOf(page), SessionUser.getUser().getUser_id());
                call.enqueue(new retrofit2.Callback<UsersResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
                        UsersResponse usersResponse = response.body();
                        if (response.code() == 200) {
                            if (usersResponse != null) {
                                if (usersResponse.getStatus().equalsIgnoreCase("success")) {
                                    isAPICalled = false;
                                    isRefreshing = false;
                                    lastPage = usersResponse.getData().getLast_page();
                                    if (page == 1) {
                                        swipeRefreshUniverse.setRefreshing(false);
                                        if (usersResponse.getData().getUsers().size() > 0) {
                                            rvNewUsers.setVisibility(View.VISIBLE);
                                            tvNoUsers.setVisibility(View.GONE);
                                            adapterUsers.refresh(usersResponse.getData().getUsers());
                                        } else {
                                            rvNewUsers.setVisibility(View.GONE);
                                            tvNoUsers.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        adapterUsers.removeLastItem();
                                        if (page == lastPage)
                                            isUserListEnd = true;
                                        adapterUsers.update(usersResponse.getData().getUsers());
                                    }
                                } else {
                                    if (swipeRefreshUniverse.isRefreshing())
                                        swipeRefreshUniverse.setRefreshing(false);
                                    else if (adapterUsers.getItemCount() > 0)
                                        adapterUsers.removeLastItem();
                                    isRefreshing = false;
                                    rvNewUsers.setVisibility(View.GONE);
                                    tvNoUsers.setVisibility(View.VISIBLE);
                                    showToast(usersResponse.getMessage());
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
                        if (swipeRefreshUniverse.isRefreshing())
                            swipeRefreshUniverse.setRefreshing(false);
                        else if (adapterUsers.getItemCount() > 0)
                            adapterUsers.removeLastItem();
                        isRefreshing = false;
                        rvNewUsers.setVisibility(View.GONE);
                        tvNoUsers.setVisibility(View.VISIBLE);
                        showToast(t.getMessage());
                    }
                });
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_universe, container, false);
    }

    @Override
    public void OnClicked(int mPosition, User mUser) {
        final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
        Call<UsersResponse> call = apiClient.getBroadcastType(mUser.getUser_id());
        call.enqueue(new retrofit2.Callback<UsersResponse>() {
            @Override
            public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
                utils.hideProgress();
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
    }

    @OnClick(R.id.setCountry)
    public void onClickSetCountry() {
        assert getFragmentManager() != null;
    }

}
