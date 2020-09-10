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

import com.blive.R;
import com.blive.model.User;
import com.bumptech.glide.Glide;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

public class AdapterUsersWeb extends RecyclerView.Adapter {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private Context mContext;
    private ArrayList<User> users;
    private ListenerChannel listenerChannel;
    OnLoadMoreListener loadMoreListener;
    boolean isLoading = false,
            isMoreDataAvailable = true;

    public AdapterUsersWeb(Context mContext, ArrayList<User> users) {
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
            Log.i("autolog", "position: " + position);
            if (holder instanceof DataViewHolder) {
                if (position >= getItemCount() - 1 && isMoreDataAvailable && !isLoading && loadMoreListener != null) {
                    Log.i("autolog", "position: " + position);
                    isLoading = true;
                    loadMoreListener.onLoadMore();
                }


                final User user = users.get(position);
                String name = user.getName();


                ((DataViewHolder) holder).tvTitle.setText(name);
                ((DataViewHolder) holder).avi.show();
                ((DataViewHolder) holder).tvViewers.setText(user.getViewers_count());

                String image = user.getProfile_pic();

                if (!user.getProfile_pic().isEmpty()) {
                    if (image.contains(".webp"))
                        Glide.with(mContext).load(image).into(((DataViewHolder) holder).ivUser);
                    else
                        Picasso.get().load(image).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(((DataViewHolder) holder).ivUser);
                } else {
                    Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(((DataViewHolder) holder).ivUser);
                }
                ((DataViewHolder) holder).layout.setOnClickListener(v -> {
                    if (listenerChannel != null) {
                        listenerChannel.OnClicked(holder.getAdapterPosition(), users.get(holder.getAdapterPosition()));
                    }
                });
            } else {
                ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return users.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public void refresh(ArrayList<User> mNewUsers) {
        Log.i("autolog", "mNewUsers: " + mNewUsers.size());
        users.clear();
        users.addAll(mNewUsers);
        if (users.size() > 4) {

            users.remove(0);
            users.remove(0);
            users.remove(0);
            users.remove(0);
            notifyDataSetChanged();
        } else {
            Log.i("autolog", "users:loop ");
            if (users.size() <= 4) {
                users.clear();
            }
            notifyDataSetChanged();
        }
        Log.i("autolog", "users: " + users.size());

    }

    public void update(ArrayList<User> mNewUsers) {
//        users.clear();
        users.addAll(mNewUsers);
        if (users.size() > 4) {
            users.remove(0);
            users.remove(0);
            users.remove(0);
            users.remove(0);

        } else {
            Log.i("autolog", "users:loop ");

        }
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

    public static class OnLoadMoreListener {
        public void onLoadMore() {
        }
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
        Log.d("Success", "Comes setLoadMoreListener");
    }

//    public void notifyDataChanged() {
//        notifyDataSetChanged();
//        isLoading = false;
//    }
}