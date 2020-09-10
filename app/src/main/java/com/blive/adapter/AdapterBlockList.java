package com.blive.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blive.model.User;
import com.blive.R;
import com.bumptech.glide.Glide;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.net.URLDecoder;
import java.util.ArrayList;

public class AdapterBlockList extends RecyclerView.Adapter {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private Context mContext;
    private ArrayList<User> users;
    private Listener listener;

    public AdapterBlockList(Context mContext, ArrayList<User> users) {
        this.mContext = mContext;
        this.users = users;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blocklist, parent, false);
            vh = new DataViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_layout, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        try {
            if (holder instanceof DataViewHolder) {
                final User user = users.get(position);

                String base64 = user.getProfile_pic();
                String image = URLDecoder.decode(base64,"UTF-8");

                Glide.with(mContext)
                        .load(user.getTools_applied())
                        .into(((DataViewHolder) holder).ivEffect);

                if (base64 != null && !base64.isEmpty()) {
                    Picasso.get().load(base64).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(((DataViewHolder) holder).iv);
                }else {
                    Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(((DataViewHolder) holder).iv);
                }

                ((DataViewHolder) holder).tvName.setText(user.getName());
                ((DataViewHolder) holder).tvId.setText("Blive ID: "+user.getReference_user_id());
                ((DataViewHolder) holder).ll.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.OnClicked(users.get(holder.getAdapterPosition()));
                    }
                });
            }else {
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

    public void refresh(ArrayList<User> mNewUsers){
        users.clear();
        users.addAll(mNewUsers);
        notifyDataSetChanged();
    }

    public void update(ArrayList<User> mNewUsers){
        users.addAll(mNewUsers);
        notifyDataSetChanged();
    }

    public void removeLastItem(){
        users.remove(users.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {
        ImageView iv,ivEffect;
        TextView tvName, tvId;
        LinearLayout ll;

        private DataViewHolder(View itemView) {
            super(itemView);
            ivEffect = itemView.findViewById(R.id.iv_effect);
            iv = itemView.findViewById(R.id.iv);
            tvName = itemView.findViewById(R.id.tv_name);
            tvId = itemView.findViewById(R.id.tv_id);
            ll = itemView.findViewById(R.id.ll);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progress_bar_bottom);
        }
    }

    public void setOnClickListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void OnClicked(User user);
    }
}