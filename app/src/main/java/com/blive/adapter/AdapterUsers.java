package com.blive.adapter;

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

import com.blive.model.User;
import com.blive.R;
import com.bumptech.glide.Glide;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

public class AdapterUsers extends RecyclerView.Adapter {

    private final int VIEW_ITEM = 1;

    private Context mContext;
    private ArrayList<User> users;
    private ListenerChannel listenerChannel;


    public AdapterUsers(Context mContext, ArrayList<User> users) {
        this.mContext = mContext;
        this.users = users;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
            vh = new DataViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_layout, parent, false);
            vh = new ProgressViewHolder(v);
//            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
//            vh = new DataViewHolder(v);
        }
        return vh;
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {

        TextView tvViewers;
        RelativeLayout layout;
        ImageView ivUser;
        AVLoadingIndicatorView avi;
        TextView tvTitle;

        private DataViewHolder(View itemView) {
            super(itemView);

            ivUser = itemView.findViewById(R.id.iv_user);
            tvTitle = itemView.findViewById(R.id.tv_title);
            layout = itemView.findViewById(R.id.rl);
            tvViewers = itemView.findViewById(R.id.tv_viewers);
            avi = itemView.findViewById(R.id.avi);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progress_bar_bottom);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        try {
            if (holder instanceof DataViewHolder) {
                final User user = users.get(position);
                String name = user.getName();
                ((DataViewHolder) holder).tvTitle.setText(name);
                ((DataViewHolder) holder).tvViewers.setText(user.getViewers_count());
                String image = user.getProfile_pic();
                if (!user.getProfile_pic().isEmpty()) {
                    if (image.contains(".webp"))
                        Glide.with(mContext).load(image).into(((DataViewHolder) holder).ivUser);
                    else
                        Glide.with(mContext)
                                .load(image)
                                .thumbnail(0.5f)
                                .into(((DataViewHolder) holder).ivUser);
//                        Picasso.get().load(image).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(((DataViewHolder) holder).ivUser);
                } else {
                    Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(((DataViewHolder) holder).ivUser);
                }
                ((DataViewHolder) holder).layout.setOnClickListener(v -> {
                    if (listenerChannel != null) {
                        listenerChannel.OnClicked(holder.getAdapterPosition(), users.get(holder.getAdapterPosition()));
                    }
                });
                try {
                    if (user.getStatus().equalsIgnoreCase("ACTIVE")) {
                        ((DataViewHolder) holder).avi.show();
                    } else {
                        ((DataViewHolder) holder).avi.hide();
                    }
                } catch (Exception e) {
                    Log.i("autolog", "e: " + e.getLocalizedMessage());
                }
            } else {
                ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public int getItemViewType(int position) {
        return users.get(position) != null ? VIEW_ITEM : VIEW_ITEM;
    }
    public void refresh(ArrayList<User> mNewUsers) {
        users.clear();
        users.addAll(mNewUsers);
        notifyDataSetChanged();
    }
    public void update(ArrayList<User> mNewUsers) {
        users.addAll(mNewUsers);
        notifyDataSetChanged();
    }
    public void removeLastItem() {
        users.remove(users.size() - 1);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return users.size();
    }
    public void setOnClickListener(ListenerChannel listenerChannel) {
        this.listenerChannel = listenerChannel;
    }
    public interface ListenerChannel {
        void OnClicked(int Position, User user);
    }
}