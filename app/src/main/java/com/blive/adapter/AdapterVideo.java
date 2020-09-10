package com.blive.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.blive.R;
import com.blive.model.Gift;
import com.blive.model.YoutubeResponse;
import com.blive.utils.BaseViewHolder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;

import java.util.ArrayList;

public class AdapterVideo extends RecyclerView.Adapter<BaseViewHolder> {

    public static final int VIEW_TYPE_NORMAL = 1;
    private static ArrayList<YoutubeResponse> youtubeResponses;
    static DisplayMetrics displayMetrics = new DisplayMetrics();
    private static AdapterVideo.ListenerGift listenerGift;
    public AdapterVideo(ArrayList<YoutubeResponse> youtubeVideos) {
        this.youtubeResponses = youtubeVideos;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_youtubevideo, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
            return youtubeResponses.size();
    }

    public void setItems(ArrayList<YoutubeResponse> youtubeVideos) {
        youtubeResponses = youtubeVideos;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.textViewTitle)
        TextView textWaveTitle;
        @BindView(R.id.btnPlay)
          ImageView playButton;
        @BindView(R.id.imageViewItem)
          ImageView imageViewItems;
        @BindView(R.id.youtube_view)
          YouTubePlayerView youTubePlayerView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
            final YoutubeResponse mYoutubeVideo = youtubeResponses.get(position);
            Log.e("", "onBind: " + position );
            ((Activity) itemView.getContext()).getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            if (mYoutubeVideo.getTitle() != null) {
                textWaveTitle.setText(mYoutubeVideo.getTitle());
            }

            if (mYoutubeVideo.getImageUrl() != null) {
                Glide.with(itemView.getContext())
                        .load(mYoutubeVideo.getImageUrl()).
                        apply(new RequestOptions().override(width - 36, 200))
                        .into(imageViewItems);
            }

            imageViewItems.setVisibility(View.VISIBLE);
            playButton.setVisibility(View.VISIBLE);
            youTubePlayerView.setVisibility(View.GONE);

            playButton.setOnClickListener(view -> {
                imageViewItems.setVisibility(View.GONE);
                youTubePlayerView.setVisibility(View.VISIBLE);
                playButton.setVisibility(View.GONE);
                youTubePlayerView.initialize(initializedYouTubePlayer -> initializedYouTubePlayer.addListener(
                        new AbstractYouTubePlayerListener() {
                            @Override
                            public void onReady() {
                                initializedYouTubePlayer.loadVideo(mYoutubeVideo.getVideoId(), 0);
                            }
                        }), true);
            });
        }
    }

    public void refresh(ArrayList<YoutubeResponse> youtubeResponses) {
        youtubeResponses.clear();
        youtubeResponses.addAll(youtubeResponses);
        notifyDataSetChanged();
    }

    public void update(ArrayList<YoutubeResponse> youtubeResponses) {
        youtubeResponses.addAll(youtubeResponses);
        notifyDataSetChanged();
    }

    public void removeLastItem() {
        youtubeResponses.remove(youtubeResponses.size() - 1);
        notifyDataSetChanged();
    }

    public void setOnClickListener(AdapterVideo.ListenerGift listenerGift) {
        this.listenerGift = listenerGift;
    }

    public interface ListenerGift {
        void OnClicked(String gift);
    }
}