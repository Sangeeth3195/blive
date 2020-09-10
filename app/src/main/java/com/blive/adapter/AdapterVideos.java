package com.blive.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blive.constant.Constants_app;
import com.blive.model.Video;
import com.blive.R;
import com.bumptech.glide.Glide;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterVideos extends RecyclerView.Adapter {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private Context mContext;
    private ArrayList<Video> videos;
    private Listener listener;

    public AdapterVideos(Context mContext, ArrayList<Video> videos) {
        this.mContext = mContext;
        this.videos = videos;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
            vh = new DataViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_layout, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {

        ImageView thumbImage, iv,ivEffect;
        TextView tvName, tvTime;
        RelativeLayout uploaderProfileLayout;

        private DataViewHolder(View itemView) {
            super(itemView);

            ivEffect = itemView.findViewById(R.id.iv_effect);
            iv = itemView.findViewById(R.id.videoUploaderImage);
            thumbImage = itemView.findViewById(R.id.videoImageThumbnail);
            tvName = itemView.findViewById(R.id.videoUploaderName);
            tvTime = itemView.findViewById(R.id.videoUploadedTime);
            uploaderProfileLayout = itemView.findViewById(R.id.uploader_profile_layout);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progress_bar_bottom);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        try {
            if (holder instanceof DataViewHolder) {
                final Video video = videos.get(position);

                ((DataViewHolder) holder).tvName.setText(video.getName());

                String image = Constants_app.decodeImage(video.getProfile_pic());

                if (image.isEmpty()) {
                    Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(((DataViewHolder) holder).iv);
                } else {
                    Picasso.get().load(image).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(((DataViewHolder) holder).iv);
                }

                Log.e("Video Thumbnail", "onBindViewHolder: " + video.getThumnail());
                Glide.with(mContext)
                        .load(video.getThumnail())
                        .into(((DataViewHolder) holder).thumbImage);

                Glide.with(mContext)
                        .load("https://blive.s3.ap-south-1.amazonaws.com/WebpageAsserts/DBeffect/5.webp")
                        .into(((DataViewHolder) holder).ivEffect);

                ((DataViewHolder) holder).tvTime.setText(video.getUploadedTime());

                ((DataViewHolder) holder).thumbImage.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onVideoClicked(videos.get(holder.getAdapterPosition()), holder.getAdapterPosition());
                    }
                });

                ((DataViewHolder) holder).uploaderProfileLayout.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.OnClickedProfile(videos.get(holder.getAdapterPosition()));
                    }
                });

                ((DataViewHolder) holder).tvName.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.OnClickedProfile(videos.get(holder.getAdapterPosition()));
                    }
                });
            } else {
                ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("clickError", "clickedError" + e);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return videos.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public void refresh(ArrayList<Video> mVideos){
        videos.clear();
        videos.addAll(mVideos);
        notifyDataSetChanged();
    }

    public void update(ArrayList<Video> mVideos){
        videos.addAll(mVideos);
        notifyDataSetChanged();
    }

    public void removeLastItem(){
        videos.remove(videos.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public void setOnClickListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void OnClickedProfile(Video video);
        void onVideoClicked(Video video, int position);
    }
}