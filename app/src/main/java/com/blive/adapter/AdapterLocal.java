package com.blive.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blive.R;
import com.blive.model.User;
import com.bumptech.glide.Glide;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterLocal extends RecyclerView.Adapter<AdapterLocal.ViewHolder> {

    private Context mContext;
    private ArrayList<User> users;
    private AdapterLocal.Listener listener;

    public AdapterLocal(Context mContext, ArrayList<User> users) {
        this.mContext = mContext;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_global24hours, parent, false);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        try {
            final User user = users.get(position);

            String image = user.getProfile_pic();

            if (image != null && !image.isEmpty()) {
                Picasso.get().load(image).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(holder.iv);
            } else {
                Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(holder.iv);
            }

            holder.tvName.setText(user.getName());
            holder.tvReceived.setText(user.getGift_value());
            holder.tvLevel.setText(" Lv : " + user.getLevel());

            Glide.with(mContext)
                    .load(user.getTools_applied())//user.getTools_applied()
                    .into(holder.ivEffect);

            holder.ll.setOnClickListener(v -> {
                if (listener != null) {
                    listener.OnClicked(users.get(holder.getAdapterPosition()));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv,ivEffect;
        TextView tvName;
        TextView tvReceived, tvPosition, tvLevel;
        LinearLayout ll;
        RelativeLayout rlFollow;

        private ViewHolder(View itemView) {
            super(itemView);

            iv = itemView.findViewById(R.id.iv);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPosition = itemView.findViewById(R.id.tv_position);
            tvReceived = itemView.findViewById(R.id.tv_received);
            tvLevel = itemView.findViewById(R.id.tv_level);
            ll = itemView.findViewById(R.id.ll);
            rlFollow = itemView.findViewById(R.id.rl_follow);
            ivEffect = itemView.findViewById(R.id.iv_effect);
        }
    }

    public void setOnClickListener(Listener listener) {
        this.listener = (Listener) listener;
    }

    public interface Listener {
        void OnClicked(User user);
    }
}
