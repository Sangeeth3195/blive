package com.blive.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blive.R;
import com.blive.model.Video;
import com.blive.utils.BaseViewHolder;
import com.bumptech.glide.Glide;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

public class AdapterVideoPlayer extends RecyclerView.Adapter<BaseViewHolder> {

    public static final int VIEW_TYPE_EMPTY = 0;
    public static final int VIEW_TYPE_NORMAL = 1;
    private List<Video> mInfoList;
    private Listener listener;
    public AdapterVideoPlayer(List<Video> infoList) {
        mInfoList = infoList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }


    @Override
    public int getItemViewType(int position) {
        if (mInfoList != null && mInfoList.size() > 0) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public int getItemCount() {
        if (mInfoList != null && mInfoList.size() > 0) {
            return mInfoList.size();
        } else {
            return 1;
        }
    }

    public class ViewHolder extends BaseViewHolder {

        public TextView tvUploaderName,tvUploadTime,tvLikeCount;
        public FrameLayout videoLayout;
        public ImageView mCover,ivProfile,ivDelete,ivPlayVideo,ivPauseVideo;
        public ProgressBar mProgressBar;
        public final View parent;
        public ShineButton likeBtn;


        public ViewHolder(View itemView) {
            super(itemView);
            tvUploaderName = itemView.findViewById(R.id.tv_uploader_name);
            tvUploadTime = itemView.findViewById(R.id.tv_upload_time);
            videoLayout = itemView.findViewById(R.id.fl_video_layout);
            mCover = itemView.findViewById(R.id.cover);
            ivProfile = itemView.findViewById(R.id.iv_profile_img);
            mProgressBar = itemView.findViewById(R.id.progressBar);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            tvLikeCount = itemView.findViewById(R.id.tv_likeCount);
            ivDelete = itemView.findViewById(R.id.im_deleteVideo);
            ivPlayVideo = itemView.findViewById(R.id.iv_play_img);
            ivPauseVideo = itemView.findViewById(R.id.iv_play_pause);
            ivPauseVideo.setVisibility(View.GONE);
            parent = itemView;
        }

        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
            parent.setTag(this);
            Video videoInfo = mInfoList.get(position);
            tvUploaderName.setText(videoInfo.getName());
            tvUploadTime.setText(videoInfo.getUploadedTime());

            String videoUrl = null;
            try {
                videoUrl = URLDecoder.decode(videoInfo.getVideo_path(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String profileUrl = null;
            try {
                profileUrl = URLDecoder.decode(videoInfo.getProfile_pic(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (profileUrl.isEmpty()) {
                Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE)
                        .into(ivProfile);
            } else {
                Glide.with(itemView.getContext())
                        .load(profileUrl)
                        .into(ivProfile);
            }

            Glide.with(itemView.getContext())
                    .load(videoUrl)
                    .into(mCover);
            ivDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClicked(videoInfo,position);
                }
            });
            if (videoInfo.getLiked().equals("yes")) {
                likeBtn.setChecked(true);
            }else if(videoInfo.getLiked().equals("no")){
                likeBtn.setChecked(false);
            }
            tvLikeCount.setText(String.valueOf(videoInfo.getVideo_Like_count()));
            likeBtn.setOnCheckStateChangeListener((view, checked) -> {
                if (listener != null) {
                    if (checked) {
                        videoInfo.setLiked("yes");
                        listener.likeBtnClicked(mInfoList.get(position),
                                "like", "",position);
                        int videoLikeCount = videoInfo.getVideo_Like_count();
                        int totalCount = videoLikeCount + 1;
                        videoInfo.setVideo_Like_count(totalCount);
                       tvLikeCount.setText(String.valueOf(totalCount));
                    } else {
                        videoInfo.setLiked("no");
                        listener.likeBtnClicked(mInfoList.get(position),
                                "unlike", "",position);
                        int videoLikeCount = videoInfo.getVideo_Like_count();
                        if (videoLikeCount > 0) {
                            int totalCount = videoLikeCount - 1;
                            videoInfo.setVideo_Like_count(totalCount);
                            tvLikeCount.setText(String.valueOf(totalCount));
                        }
                    }
                }
            });
        }
    }

    public void addLikeStatus(int videoPosition, String likeStatus) {
        Video video = mInfoList.get(videoPosition);
        video.setLiked(likeStatus);
    }

    public void setOnClickListener(Listener listener) {
        this.listener = listener;
    }
    public interface Listener {
        void onDeleteClicked(Video video, int adapterPosition);
        void likeBtnClicked(Video videos, String likeUnlikeText, String comments, int videoListPosition);
    }
}
