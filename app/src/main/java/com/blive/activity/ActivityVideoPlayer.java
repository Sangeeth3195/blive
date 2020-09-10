package com.blive.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.blive.R;
import com.blive.adapter.AdapterVideoPlayer;
import com.blive.model.GenericResponse;
import com.blive.model.Video;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.session.SessionUser;
import com.blive.utils.Utils;
import com.blive.utils.VideoDividerLine;
import com.crashlytics.android.Crashlytics;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class ActivityVideoPlayer extends BaseBackActivity implements AdapterVideoPlayer.Listener{

    RecyclerView rvVideos;
    RelativeLayout rl_backPressedBtn;
    int page = 1,position = 0;;
    private ArrayList<Video> videosList;
    String comments = "", likeUnlikeText = "";
    LinearLayoutManager layoutManager;
    private int videoSurfaceDefaultHeight = 0;
    private int screenDefaultHeight = 0;
    private int playPosition = -1,videoListPos = 0, deleteVideoPosition;
    public SimpleExoPlayer simpleExoPlayer;
    private PlayerView videoSurfaceView;
    private ProgressBar mProgressBar;
    private View viewHolderParent;
    private FrameLayout frameLayout;
    private ImageView thumbnail;
    private AdapterVideoPlayer adapterVideos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        initUI();
    }

    public void initUI() {
        utils = new Utils(this);
        rvVideos = findViewById(R.id.rv_videos);
        rl_backPressedBtn = findViewById(R.id.rlBack);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        rvVideos.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        rvVideos.addItemDecoration(new VideoDividerLine(this));
        page = 1;
        rl_backPressedBtn.setOnClickListener(v -> onBackClicked());

        Intent intent = getIntent();
        videosList = (ArrayList<Video>) intent.getSerializableExtra("video_list");
        position = intent.getIntExtra("position", 0);
        Log.e(TAG, "initUI: "+position);
        page = 1;
        adapterVideos = new AdapterVideoPlayer(videosList);
        rvVideos.setAdapter(adapterVideos);
        adapterVideos.setOnClickListener(this);
        videoSurfaceView = new PlayerView(ActivityVideoPlayer.this);
        playVideo(true);
        rvVideos.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Log.d(TAG, "onScrollStateChanged: called.");
                    // There's a special case when the end of the list has been reached.
                    // Need to handle that with this bit of logic
                    if(!recyclerView.canScrollVertically(1)) {
                        playVideo(true);
                    }
                    else{
                        playVideo(false);
                    }
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        try{
            rvVideos.scrollToPosition(position);
        //    rvVideos.canScrollVertically(1);
        }catch (Exception e){
            Log.e(TAG, "initUI: "+e );
            Crashlytics.logException(e);
        }
    }

    public void playVideo(boolean isEndOfList){
        int targetPosition;
        if(!isEndOfList){
            int startPosition = layoutManager.findFirstVisibleItemPosition();
            int endPosition = layoutManager.findLastVisibleItemPosition();
            // if there is more than 2 list-items on the screen, set the difference to be 1
            if (endPosition - startPosition > 1) {
                endPosition = startPosition + 1;
            }
            // something is wrong. return.
            if (startPosition < 0 || endPosition < 0) {
                return;
            }
            // if there is more than 1 list-item on the screen
            if (startPosition != endPosition) {
                int startPositionVideoHeight = getVisibleVideoSurfaceHeight(startPosition);
                int endPositionVideoHeight = getVisibleVideoSurfaceHeight(endPosition);
                targetPosition = startPositionVideoHeight > endPositionVideoHeight ? startPosition : endPosition;
            }
            else {
                targetPosition = startPosition;
            }
        }
        else{
            targetPosition = videosList.size() - 1;
        }
        // video is already playing so return
        if (targetPosition == playPosition) {
            return;
        }
        // set the position of the list-item that is to be played
        playPosition = targetPosition;
        if (videoSurfaceView == null) {
            return;
        }
        // remove any old surface views from previously playing videos
        removeVideoView(videoSurfaceView);
        int currentPosition = targetPosition - layoutManager.findFirstVisibleItemPosition();
        View child = rvVideos.getChildAt(currentPosition);
        if (child == null) {
            return;
        }

        AdapterVideoPlayer.ViewHolder holder = (AdapterVideoPlayer.ViewHolder) child.getTag();
        if (holder == null) {
            playPosition = -1;
            return;
        }
        thumbnail = holder.mCover;
        mProgressBar = holder.mProgressBar;
        viewHolderParent = holder.itemView;
        frameLayout = holder.videoLayout;
        ImageView playVideo = holder.ivPlayVideo;
        videoSurfaceView.setPlayer(simpleExoPlayer);
        frameLayout.addView(videoSurfaceView);
        frameLayout.setVisibility(View.VISIBLE);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                getApplicationContext(), Util.getUserAgent(getApplicationContext(), "RecyclerView VideoPlayer"));
        String videoUrl = null;
        try {
            videoUrl = URLDecoder.decode(videosList.get(targetPosition).getVideo_path(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e(TAG, "playVideo: " + e.getMessage() );
        }
        String mediaUrl = videoUrl;
        if (simpleExoPlayer == null) {
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getApplicationContext(),
                    new DefaultRenderersFactory(getApplicationContext()), new DefaultTrackSelector(), new DefaultLoadControl());
        }
        Uri videoURI = Uri.parse(videoUrl);
        MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(videoURI);
        videoSurfaceView.setPlayer(simpleExoPlayer);
        simpleExoPlayer.prepare(mediaSource, true, true);
        simpleExoPlayer.setPlayWhenReady(true);
        thumbnail.setVisibility(View.GONE);
        simpleExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == ExoPlayer.STATE_BUFFERING){
                    mProgressBar.setVisibility(View.VISIBLE);
                } else {
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
        if (mediaUrl != null) {
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(mediaUrl));
            simpleExoPlayer.prepare(videoSource);
            simpleExoPlayer.setPlayWhenReady(true);
            videoSurfaceView.setUseController(true);
        }
    }

    private int getVisibleVideoSurfaceHeight(int playPosition) {
        int at = playPosition - layoutManager.findFirstVisibleItemPosition();
        View child = rvVideos.getChildAt(at);
        if (child == null) {
            return 0;
        }
        int[] location01 = new int[2];
        child.getLocationInWindow(location01);

        if (location01[1] < 0) {
            return location01[1] + videoSurfaceDefaultHeight;
        } else {
            return screenDefaultHeight - location01[1];
        }
    }

    //remove the player from the row
    private void removeVideoView(PlayerView videoView) {
        ViewGroup parent = (ViewGroup) videoView.getParent();
        if (parent == null) {
            return;
        }
        int index = parent.indexOfChild(videoView);
        if (index >= 0) {
            parent.removeViewAt(index);
        }
    }

    public void onBackClicked() {
        finish();
    }

    @Override
    protected void onDestroy() {
        if(simpleExoPlayer != null){
            simpleExoPlayer.release();
        }
        super.onDestroy();
    }

    @Override
    public void onDeleteClicked(Video video, int adapterPosition) {
        utils.showProgress();
        deleteVideoPosition = adapterPosition;
        if (video.getId().equals(videosList.get(adapterPosition).getId())) {

            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GenericResponse> call = apiClient.video(SessionUser.getUser().getUser_id(),video.getId(),"delete");
            call.enqueue(new retrofit2.Callback<GenericResponse>() {
                @Override
                public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                    utils.hideProgress();
                    Log.e(TAG, "onResponse: "+ response);
                    GenericResponse genericResponse = response.body();
                    if (response.code() == 200) {
                        if (genericResponse != null) {
                            if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                                try {
                                  /*  videosList.remove(deleteVideoPosition);*/
                                    adapterVideos.notifyDataSetChanged();
                                    finish();
                                    showToast(genericResponse.getData().getMessage());
                                } catch (Exception e) {
                                    showToast(e.getMessage());
                                }
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
                    showToast(t.getMessage());
                }
            });
        } else {
            showToast("Oops !!!");
        }
    }

    @Override
    public void likeBtnClicked(Video videos, String likeUnlikeText, String comments, int videoListPosition) {
        this.comments = comments;
        this.videoListPos = videoListPosition;
        this.likeUnlikeText = likeUnlikeText;
        Log.e(TAG, "likeBtnClicked: "+likeUnlikeText);


        final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
        Call<GenericResponse> call = apiClient.video(SessionUser.getUser().getUser_id(),videos.getId(),likeUnlikeText);
        call.enqueue(new retrofit2.Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                GenericResponse genericResponse = response.body();
                if (response.code() == 200) {
                    if (genericResponse != null) {
                        if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                            if (!likeUnlikeText.isEmpty()) {
                                Video selectedVideo = videosList.get(videoListPos);
                                selectedVideo.setLiked(likeUnlikeText);
                                videosList.remove(videoListPos);
                                videosList.add(videoListPos, selectedVideo);
                                adapterVideos.addLikeStatus(videoListPos, likeUnlikeText);
                            }
                            showToast(genericResponse.getData().getMessage());
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
                showToast(t.getMessage());
            }
        });
    }
}
