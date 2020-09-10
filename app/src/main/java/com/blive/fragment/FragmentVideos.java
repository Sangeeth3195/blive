package com.blive.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blive.activity.ActivityVideoPlayer;
import com.blive.apiTasks.APITask_UploadVideo;
import com.blive.activity.ActivityViewProfile;
import com.blive.adapter.AdapterVideos;
import com.blive.constant.Constants_app;
import com.blive.model.Video;
import com.blive.R;
import com.blive.model.VideosResponse;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.session.SessionUser;
import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.blive.constant.Constants_app.SELECT_VIDEO;
import static com.facebook.FacebookSdk.getApplicationContext;

public class FragmentVideos extends BaseFragment implements APITask_UploadVideo.Listener, AdapterVideos.Listener {

    @BindView(R.id.rv_videos)
    RecyclerView rvVideos;
    @BindView(R.id.tv_no_videos)
    TextView tvNoVideos;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private SwipeRefreshLayout swipeRefreshVideos;
    private String video;
    private Upload upload;
    private AdapterVideos adapterVideos;
    private int page = 1, lastPage = 1;
    private ArrayList<Video> videos, tempVideo;
    private boolean isUserListEnd = false, isAPICalled = false, isRefreshing = false, isResume = false;

    ProgressDialog pd;

    public FragmentVideos() {
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

        videos = new ArrayList<>();
        tempVideo = new ArrayList<>();
        swipeRefreshVideos = mActivity.findViewById(R.id.swipeRefreshVideos);
        swipeRefreshVideos.setColorSchemeResources(R.color.colorAccent);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        rvVideos.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        tempVideo.add(null);

        adapterVideos = new AdapterVideos(mActivity, videos);
        adapterVideos.setOnClickListener(this);
        rvVideos.setAdapter(adapterVideos);
        rvVideos.setVisibility(View.VISIBLE);

        rvVideos.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (!isRefreshing) {
                if (isLastItemDisplaying()) {
                    if (page < lastPage) {
                        page = page + 1;
                        getVideos(page);
                    }
                }
            }
        });

        swipeRefreshVideos.setOnRefreshListener(() -> {
            page = 1;
            isRefreshing = true;
            isUserListEnd = false;
            getVideos(page);
            swipeRefreshVideos.setRefreshing(false);
        });

        getVideos(page);
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

    public void getVideos(int page) {
        if (!isAPICalled) {
            isAPICalled = true;
            if (utils.isNetworkAvailable()) {
                if (page > 1) {
                    adapterVideos.update(tempVideo);
                }else
                    swipeRefreshVideos.setRefreshing(true);
                final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                Call<VideosResponse> call = apiClient.getVideos( String.valueOf(page),SessionUser.getUser().getUser_id());
                call.enqueue(new retrofit2.Callback<VideosResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<VideosResponse> call, @NonNull Response<VideosResponse> response) {
                        VideosResponse videosResponse = response.body();
                        if (response.code() == 200) {
                            if (videosResponse != null) {
                                if (videosResponse.getStatus().equalsIgnoreCase("success")) {
                                    lastPage = videosResponse.getData().getLast_page();
                                    isAPICalled = false;
                                    isRefreshing = false;
                                    if (page == 1) {
                                        swipeRefreshVideos.setRefreshing(false);
                                        if (videosResponse.getData().getVideos().size() > 0) {
                                            rvVideos.setVisibility(View.VISIBLE);
                                            tvNoVideos.setVisibility(View.GONE);
                                            adapterVideos.refresh(videosResponse.getData().getVideos());
                                        } else {
                                            rvVideos.setVisibility(View.GONE);
                                            tvNoVideos.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        adapterVideos.removeLastItem();
                                        if (page == lastPage)
                                            isUserListEnd = true;
                                        adapterVideos.update(videosResponse.getData().getVideos());
                                    }
                                }else {
                                    if(adapterVideos.getItemCount()>0)
                                        adapterVideos.removeLastItem();
                                    else
                                        swipeRefreshVideos.setRefreshing(false);
                                    isRefreshing = false;
                                    rvVideos.setVisibility(View.GONE);
                                    tvNoVideos.setVisibility(View.VISIBLE);
                                    showToast(videosResponse.getMessage());
                                }
                            } else {
                                utils.showToast(getString(R.string.server_error));
                                adapterVideos.removeLastItem();
                                isRefreshing = false;
                                rvVideos.setVisibility(View.GONE);
                                tvNoVideos.setVisibility(View.VISIBLE);
                            }
                        } else {
                            checkResponseCode(response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<VideosResponse> call, @NonNull Throwable t) {
                        if(page == 1)
                            swipeRefreshVideos.setRefreshing(false);
                        else if(page > 1)
                            adapterVideos.removeLastItem();
                        isRefreshing = false;
                        rvVideos.setVisibility(View.GONE);
                        tvNoVideos.setVisibility(View.VISIBLE);
                        showToast(t.getMessage());
                    }
                });
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_videos, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_VIDEO) {
                try{
                    Uri selectedVideoUri = data.getData();
                    assert selectedVideoUri != null;
                    File file = new File(Objects.requireNonNull(selectedVideoUri.getPath()));
                    long length = file.length();
                    int fileSize = getVideoFileSize(selectedVideoUri);
                    if (fileSize > 40) {
                        showToast("Video File size must be less than 40 mb!");
                    } else {
                        video = getPath(selectedVideoUri);
                        uploadVideo();
                    }
                }catch (Exception e){
                    Crashlytics.logException(e);
                    showToast("Please try again later!");
                }
            }
        }
    }

    private int getVideoFileSize(Uri selectedVideoUri) {
        Cursor returnCursor =
                mActivity.getContentResolver().query(selectedVideoUri, null, null, null, null);
        assert returnCursor != null;
        returnCursor.moveToFirst();
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        sizeIndex = sizeIndex / 1000000;
        return sizeIndex;
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = mActivity.managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else return null;
    }

    private void uploadVideo() {
        if (!video.isEmpty()) {
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Uploading Video...");
            pd.setTitle("Processing...");
            pd.setCancelable(true);
            pd.setIndeterminate(true);
            pd.show();

            upload = new Upload();
            upload.execute();
        } else {
            showToast("Select a video to upload !");
        }
    }

    @Override
    public void onUploadSuccess(String msg) {

        pd.dismiss();
        if (!upload.isCancelled())
            upload.cancel(true);

        mActivity.runOnUiThread(() -> {
            pd.dismiss();
            showToast(msg);
            page = 1;
            getVideos(page);
        });
    }

    @Override
    public void onUploadFailure(String mes) {
        if (!upload.isCancelled())
            upload.cancel(true);

        mActivity.runOnUiThread(() -> {
            pd.dismiss();
            showToast(mes);
        });
    }

    @Override
    public void OnClickedProfile(Video video) {
        if (!SessionUser.getUser().getUser_id().equalsIgnoreCase(video.getUser_id())) {
            Intent intent = new Intent(mActivity, ActivityViewProfile.class);
            intent.putExtra("image", Constants_app.decodeImage(video.getProfile_pic()));
            intent.putExtra("userId", video.getUser_id());
            intent.putExtra("from", "list");
            startActivity(intent);
        }
    }

    @Override
    public void onVideoClicked(Video video, int position) {
        isResume = true;
        Intent intent = new Intent(mActivity, ActivityVideoPlayer.class);
        intent.putExtra("image", video.getId());
        intent.putExtra("imageUrl", video.getVideo_path());
        intent.putExtra("position", position);
        intent.putExtra("video_list", videos);
        startActivity(intent);
    }

    @SuppressLint("StaticFieldLeak")
    private class Upload extends AsyncTask<Void, Integer, Void> {
        APITask_UploadVideo apiTask_uploadVideo = callAPI();

        @Override
        protected Void doInBackground(Void... params) {
            apiTask_uploadVideo.upload(video);
            return null;
        }
    }

    private APITask_UploadVideo callAPI() {
        return new APITask_UploadVideo(this, getApplicationContext());
    }

    @SuppressLint("IntentReset")
    @OnClick(R.id.fab)
    public void onClickChoose() {
        Intent pickVideo = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        pickVideo.setType("video/*");
        startActivityForResult(pickVideo, SELECT_VIDEO);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isResume) {
            getVideos(page);
            isResume = false;
        }
    }
}
