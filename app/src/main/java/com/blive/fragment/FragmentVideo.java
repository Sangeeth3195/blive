package com.blive.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blive.adapter.AdapterVideo;
import com.blive.R;
import com.blive.model.YoutubeResponse;
import com.blive.model.YoutubeVideo;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;



public class FragmentVideo extends BaseFragment implements AdapterVideo.ListenerGift {

    @BindView(R.id.tv_no_videos)
    TextView tvNoVideos;
    @BindView(R.id.rv_video)
    RecyclerView rvVideos;
    private SwipeRefreshLayout swipeRefreshVideos;
    private int page = 1, lastPage = 1;
    private AdapterVideo adapterVideos;
    private ArrayList<YoutubeResponse> getVideoList, tempUser;
    private boolean isUserListEnd = false, isAPICalled = false, isRefreshing = false, isResume = false;
    private RewardedVideoAd mRewardedVideoAd;

    public FragmentVideo() {

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


        getVideoList = new ArrayList<>();
        tempUser = new ArrayList<>();

        swipeRefreshVideos = mActivity.findViewById(R.id.swipeRefreshVideos);
        swipeRefreshVideos.setColorSchemeResources(R.color.colorAccent);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        rvVideos.setLayoutManager(layoutManager);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvVideos.setLayoutManager(llm);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);

        tempUser.add(null);
        adapterVideos = new AdapterVideo(getVideoList);
        rvVideos.setAdapter(adapterVideos);
        rvVideos.setVisibility(View.VISIBLE);

        rvVideos.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (!isRefreshing) {
                if (!isUserListEnd) {
                    if (isLastItemDisplaying()) {
                        if (page < lastPage) {
                            page = page + 1;
                            getVideos(page);
                        }
                    }
                }
            }
        });

        swipeRefreshVideos.setOnRefreshListener(() -> {
            page = 1;
            isUserListEnd = false;
            isRefreshing = true;
            getVideos(page);
            swipeRefreshVideos.setRefreshing(false);
        });
        getVideos(page);
    }

    public void getVideos(int page) {
        getVideoList.clear();
        if (utils.isNetworkAvailable()) {
            if (!isAPICalled) {
                isAPICalled = true;
                if (page > 1) {
                    adapterVideos.update(tempUser);
                } else
                    swipeRefreshVideos.setRefreshing(true);

                final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                Call<YoutubeVideo> call = apiClient.getYouTubeVideoList(page);
                call.enqueue(new retrofit2.Callback<YoutubeVideo>() {
                    @Override
                    public void onResponse(@NonNull Call<YoutubeVideo> call, @NonNull Response<YoutubeVideo> response) {
                        YoutubeVideo youtubeVideo = response.body();

                        if (swipeRefreshVideos.isRefreshing()) {
                            swipeRefreshVideos.setRefreshing(false);
                        }

                        if (response.code() == 200) {
                            if (youtubeVideo != null) {
                                if (youtubeVideo.getStatus().equalsIgnoreCase("success")) {
                                    isAPICalled = false;
                                    isRefreshing = false;
                                    lastPage = youtubeVideo.getData().getLast_page();
                                    setVideo(youtubeVideo.getData().getYoutubeResponses());
                                } else {

                                }
                            } else {
                                utils.showToast(getString(R.string.server_error));
                                rvVideos.setVisibility(View.GONE);
                                tvNoVideos.setVisibility(View.VISIBLE);
                            }
                        } else {
                            checkResponseCode(response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<YoutubeVideo> call, @NonNull Throwable t) {
                        if (swipeRefreshVideos.isRefreshing())
                            isRefreshing = false;
                        swipeRefreshVideos.setRefreshing(false);
                        rvVideos.setVisibility(View.GONE);
                        tvNoVideos.setVisibility(View.VISIBLE);
                        showToast(t.getMessage());
                    }
                });
            }
        }
    }

    private boolean isLastItemDisplaying() {
        if (adapterVideos != null) {
            if (Objects.requireNonNull(rvVideos.getAdapter()).getItemCount() != 0) {
                if (!isUserListEnd) {
                    int lastVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(rvVideos.getLayoutManager())).findLastCompletelyVisibleItemPosition();
                    return lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == rvVideos.getAdapter().getItemCount() - 11;
                }
            }
            return false;
        }
        return false;
    }

    private void setVideo(ArrayList<YoutubeResponse> youtubeResponses) {
        getVideoList.addAll(youtubeResponses);
        adapterVideos = new AdapterVideo(getVideoList);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvVideos.setLayoutManager(llm);
        rvVideos.setItemAnimator(new DefaultItemAnimator());
        rvVideos.setAdapter(adapterVideos);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video, container, false);
    }


    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (isResume) {
            getVideos(page);
            isResume = false;
    }
    }


    @Override
    public void OnClicked(String gift) {

    }
}
