package com.blive.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.blive.activity.ActivityVideoPlayer;
import com.blive.model.Video;
import com.blive.R;
import com.blive.session.SessionUser;
import com.bumptech.glide.Glide;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.net.URLDecoder;
import java.util.ArrayList;

public class AdapterVideoPlaying extends RecyclerView.Adapter<AdapterVideoPlaying.ViewHolder> {

    private Context mContext;
    private ArrayList<Video> videos;

    private Listener listener;
    private VideoView playingVideoView;
    private SeekBar playingSeekbar;
    private int seekbarPausedPosition = 0, clickedPosition = -1;
    private ActivityVideoPlayer activityVideoPlayer;

    public AdapterVideoPlaying(Context mContext, ArrayList<Video> videos, ActivityVideoPlayer activityVideoPlayer) {
        this.mContext = mContext;
        this.videos = videos;
        this.activityVideoPlayer = activityVideoPlayer;
    }

    @NonNull
    @Override
    public AdapterVideoPlaying.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_playing_layout, parent, false);
            return new ViewHolder(layout);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {

            final Video video = videos.get(position);
            holder.uploaderName.setText(video.getName());
            String timing = video.getCreated_at() + " ago";
            holder.uploadedTime.setText(video.getUploadedTime());
            holder.progressBar.setVisibility(View.GONE);

            String videoUrl = URLDecoder.decode(video.getVideo_path(), "UTF-8");
            String profileUrl = URLDecoder.decode(video.getProfile_pic(), "UTF-8");

            Glide.with(mContext)
                    .load(videoUrl)
                    .into(holder.thumbnailImage);
            holder.thumbnailImage.setVisibility(View.VISIBLE);

            if (profileUrl.isEmpty()) {
                Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE)
                        .into(holder.uploaderProfileImage);
            } else {
                Glide.with(mContext)
                        .load(profileUrl)
                        .into(holder.uploaderProfileImage);
            }

            holder.videoview.setVisibility(View.VISIBLE);
            holder.videoview.setVideoURI(Uri.parse(videoUrl));// uri
            holder.likeCount.setText(String.valueOf(video.getVideo_Like_count()));
            holder.commentCount.setText("Comments");

            if(position == clickedPosition){

                holder.thumbnailImage.setVisibility(View.GONE);
                holder.playButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pause));
                if (holder.videoview.isPlaying()) {
                    holder.videoview.pause();
                    holder.playButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.play_video));
                    seekbarPausedPosition = holder.videoview.getCurrentPosition();
                    video.setVideoPausedPosition(seekbarPausedPosition);
                    return;
                }

                holder.videoview.setOnPreparedListener(mp -> {
                    holder.progressBar.setVisibility(View.GONE);
                    long duration = holder.videoview.getDuration();
                    holder.videoPlayerProgress.setMax((int) duration);
                    playingVideoView = holder.videoview;
                    playingSeekbar = holder.videoPlayerProgress;
                    holder.videoPlayerProgress.postDelayed(onEverySecond, 1000);

                    mp.setOnInfoListener((mp1, what, extra) -> {
                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START)
                            holder.progressBar.setVisibility(View.VISIBLE);
                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END)
                            holder.progressBar.setVisibility(View.GONE);
                        return false;
                    });

                });

                holder.videoview.setOnCompletionListener(mediaPlayer -> {
                    playingVideoView = holder.videoview;
                    playingSeekbar = holder.videoPlayerProgress;
                    holder.playButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.play_video));
                    video.setVideoPausedPosition(0);
                    holder.videoPlayerProgress.setProgress(0);
                });

                if (video.getVideoPausedPosition() != 0) {
                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.videoPlayerProgress.setProgress(video.getVideoPausedPosition());
                    holder.videoview.seekTo(video.getVideoPausedPosition());
                    video.setVideoPausedPosition(0);
                    playingVideoView = holder.videoview;
                    playingSeekbar = holder.videoPlayerProgress;
                    holder.videoPlayerProgress.postDelayed(onEverySecond, 1000);
                } else {
                    holder.videoPlayerProgress.setMax(10);

                }
                holder.videoview.requestFocus();
                holder.videoview.start();

                holder.playButton.setVisibility(View.GONE);

            }else {
                holder.playButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.play_video));
            }

            holder.playButton.setOnClickListener(v -> {
                if(holder.getAdapterPosition() != clickedPosition){
                    clickedPosition =  holder.getAdapterPosition();
                    notifyDataSetChanged();
                }else {
                    holder.thumbnailImage.setVisibility(View.GONE);
                    holder.playButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pause));
                    if (holder.videoview.isPlaying()) {
                        holder.videoview.pause();
                        holder.playButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.play_video));
                        seekbarPausedPosition = holder.videoview.getCurrentPosition();
                        video.setVideoPausedPosition(seekbarPausedPosition);

                        return;
                    }

                    holder.videoview.setOnPreparedListener(mp -> {
                        holder.progressBar.setVisibility(View.GONE);
                        holder.videoview.setVisibility(View.VISIBLE);
                        long duration = holder.videoview.getDuration();
                        holder.videoPlayerProgress.setMax((int) duration);
                        playingVideoView = holder.videoview;
                        playingSeekbar = holder.videoPlayerProgress;
                        holder.videoPlayerProgress.postDelayed(onEverySecond, 1000);

                        mp.setOnInfoListener((mp1, what, extra) -> {
                            if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START)
                                holder.progressBar.setVisibility(View.VISIBLE);
                            if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END)
                                holder.progressBar.setVisibility(View.GONE);
                            return false;
                        });
                    });

                    holder.videoview.setOnCompletionListener(mediaPlayer -> {
                        playingVideoView = holder.videoview;
                        playingSeekbar = holder.videoPlayerProgress;
                        holder.playButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.play_video));
                        video.setVideoPausedPosition(0);
                        holder.videoPlayerProgress.setProgress(0);
                    });

                    if (video.getVideoPausedPosition() != 0) {
                        holder.videoPlayerProgress.setProgress(video.getVideoPausedPosition());
                        holder.videoview.seekTo(video.getVideoPausedPosition());
                        video.setVideoPausedPosition(0);
                        playingVideoView = holder.videoview;
                        playingSeekbar = holder.videoPlayerProgress;
                        holder.videoPlayerProgress.postDelayed(onEverySecond, 1000);
                    } else {
                        holder.videoPlayerProgress.setMax(10);
                    }
                    holder.videoview.requestFocus();
                    holder.videoview.start();

                    holder.playButton.setVisibility(View.GONE);
                }
            });

            holder.itemView.setOnClickListener(view -> {
                if (holder.playButton.getVisibility() == View.VISIBLE) {
                    holder.playButton.setVisibility(View.GONE);
                } else {
                    holder.playButton.setVisibility(View.VISIBLE);
                }
            });

            holder.profileLayout.setOnClickListener(view -> {

            });

            holder.commentImage.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onCommentClicked(video);
                }
            });

            if (video.getLiked().equals("yes")) {
                holder.likeBtn.setChecked(true);
            }else if(video.getLiked().equals("no")){
                holder.likeBtn.setChecked(false);
            }

            holder.likeBtn.setOnCheckStateChangeListener((view, checked) -> {
                if (listener != null) {
                    if (checked) {
                        video.setLiked("yes");
                        listener.likeBtnClicked(videos.get(holder.getAdapterPosition()),
                                "like", "", holder.getAdapterPosition());
                        int videoLikeCount = video.getVideo_Like_count();
                        int totalCount = videoLikeCount + 1;
                        video.setVideo_Like_count(totalCount);
                        holder.likeCount.setText(String.valueOf(totalCount));
                    } else {
                        video.setLiked("no");
                        listener.likeBtnClicked(videos.get(holder.getAdapterPosition()),
                                "unlike", "", holder.getAdapterPosition());
                        int videoLikeCount = video.getVideo_Like_count();
                        if (videoLikeCount > 0) {
                            int totalCount = videoLikeCount - 1;
                            video.setVideo_Like_count(totalCount);
                            holder.likeCount.setText(String.valueOf(totalCount));
                        }
                    }
                }
            });

            if (!video.getUser_id().equals(SessionUser.getUser().getUser_id())) {
                holder.imgDeleteVideo.setVisibility(View.GONE);
            }


            holder.imgDeleteVideo.setOnClickListener(view -> {
                if (listener != null) {
                    deleteVideoAlert(video, holder.getAdapterPosition());
                }
            });

            holder.videoPlayerProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    if (fromUser) {
                        seekBar.setProgress(progress);
                        holder.videoview.seekTo(progress);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView playButton, commentImage, thumbnailImage, uploaderProfileImage, imgDeleteVideo;
        ShineButton likeBtn;
        VideoView videoview;
        SeekBar videoPlayerProgress;
        TextView uploadedTime, likeCount, commentCount;
        TextView uploaderName;
        ProgressBar progressBar;
        RelativeLayout profileLayout;

        private ViewHolder(View itemView) {
            super(itemView);

            playButton = itemView.findViewById(R.id.videoPlayButton);
            videoview = itemView.findViewById(R.id.videoView);
            videoPlayerProgress = itemView.findViewById(R.id.mediaPlayerDurationSeekbar);
            commentImage = itemView.findViewById(R.id.commentsBtn);
            thumbnailImage = itemView.findViewById(R.id.thumbnailImage);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            uploaderName = itemView.findViewById(R.id.videoUploaderName);
            uploadedTime = itemView.findViewById(R.id.videoUploadedTime);
            likeCount = itemView.findViewById(R.id.tv_likeCount);
            commentCount = itemView.findViewById(R.id.tv_commentCount);
            uploaderProfileImage = itemView.findViewById(R.id.userPic);
            imgDeleteVideo = itemView.findViewById(R.id.im_deleteVideo);
            progressBar = itemView.findViewById(R.id.progress_bar);
            profileLayout = itemView.findViewById(R.id.profile_lay);
        }
    }

    public void setOnClickListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void OnClicked(Video videos);
        void onCommentClicked(Video video);
        void onDeleteClicked(Video video, int adapterPosition);
        void likeBtnClicked(Video videos, String likeUnlikeText, String comments, int videoListPosition);
    }

    private Runnable onEverySecond = new Runnable() {

        @Override
        public void run() {

            if (playingSeekbar != null) {
                playingSeekbar.setProgress(playingVideoView.getCurrentPosition());
            }

            if (playingVideoView.isPlaying()) {
                playingSeekbar.postDelayed(onEverySecond, 1000);
            }
        }
    };

    private void deleteVideoAlert(Video videoDelete, int selectedPosition) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activityVideoPlayer);
        alertDialogBuilder.setMessage("Do you want to delete this video ? ");
        alertDialogBuilder.setPositiveButton("Yes",
                (arg0, arg1) -> {
                    listener.onDeleteClicked(videoDelete, selectedPosition);
                });

        alertDialogBuilder.setNegativeButton("No",
                (arg0, arg1) -> arg0.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void  addLikeStatus(int videoPosition,String likeStatus){
        Video video = videos.get(videoPosition);
        video.setLiked(likeStatus);
    }
}