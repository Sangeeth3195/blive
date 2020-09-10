package com.blive.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.blive.model.Comments;
import com.blive.model.Video;
import com.blive.R;
import com.blive.session.SessionUser;
import com.bumptech.glide.Glide;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterCommentsList extends RecyclerView.Adapter<AdapterCommentsList.ViewHolder> {

    private Context mContext;
    private List<Comments> videoComments;
    Video video;
    private AdapterVideoPlaying.Listener listener;
    private VideoView playingVideoView;
    private SeekBar playingSeekbar;

    public AdapterCommentsList(Context mContext, Video video, List<Comments> videoComments) {
        this.mContext = mContext;
        this.video = video;
        this.videoComments = videoComments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_list_layout, parent, false);
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

            final Comments comments = videoComments.get(position);
//            if (videoComments.size() > 0) {
//                for (int i = 0; i < video.getComments().size(); i++) {
                    if (SessionUser.getUser().getUsername().equals(comments.getName())) {
                        holder.roundedProfileBackOther.setVisibility(View.GONE);
                        holder.commentOthers.setVisibility(View.GONE);
                        holder.commentMe.setVisibility(View.VISIBLE);
                        holder.commentMe.setText(comments.getComment());

                        if(comments.getProfile_pic().isEmpty()){
                            Picasso.get().load(R.drawable.user).fit().centerCrop()
                                    .memoryPolicy(MemoryPolicy.NO_STORE)
                                    .placeholder(R.drawable.user)
                                    .into(holder.profileMe);
                        }else{
                            Glide.with(mContext)
                                    .load(comments.getProfile_pic())
                                    .into(holder.profileMe);
                        }
                    } else {
                        holder.roundedProfileBackMe.setVisibility(View.GONE);
                        holder.commentMe.setVisibility(View.GONE);
                        holder.commentOthers.setVisibility(View.VISIBLE);
                        holder.commentOthers.setText(comments.getComment());

                        if(comments.getProfile_pic().isEmpty()){
                            Picasso.get().load(R.drawable.user).fit().centerCrop()
                                    .memoryPolicy(MemoryPolicy.NO_STORE)
                                    .placeholder(R.drawable.user)
                                    .into(holder.profileOther);
                        }else{
                            Glide.with(mContext)
                                    .load(comments.getProfile_pic())
                                    .into(holder.profileOther);
                        }
                    }
//                }
//            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext,"Error "+ e.getMessage(),Toast.LENGTH_SHORT).show();
            Log.e("ErrorOccurred","ErrotPlace "+ e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return videoComments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView commentMe, commentOthers;
        ImageView profileMe,profileOther;
        CardView roundedProfileBackOther,roundedProfileBackMe;

        private ViewHolder(View itemView) {
            super(itemView);

            commentOthers = itemView.findViewById(R.id.comment_text_by_other);
            commentMe = itemView.findViewById(R.id.comment_text_by_me);
            profileMe = itemView.findViewById(R.id.profile_image_me);
            profileOther = itemView.findViewById(R.id.profile_image_other);
            roundedProfileBackOther = itemView.findViewById(R.id.profile_image_circle_other);
            roundedProfileBackMe = itemView.findViewById(R.id.profile_image_circle_me);

        }
    }
}
