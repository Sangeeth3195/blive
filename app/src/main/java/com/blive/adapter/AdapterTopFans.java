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

import com.blive.model.User;
import com.blive.R;
import com.bumptech.glide.Glide;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.net.URLDecoder;
import java.util.ArrayList;

public class AdapterTopFans extends RecyclerView.Adapter<AdapterTopFans.ViewHolder> {

    private Context mContext;
    private ArrayList<User> users;
    private Listener listener;

    public AdapterTopFans(Context mContext, ArrayList<User> users) {
        this.mContext = mContext;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_toplist, parent, false);
            return new ViewHolder(layout);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {
            final User user = users.get(position);
            String base64 = user.getProfile_pic();
            String image = URLDecoder.decode(base64, "UTF-8");

            Glide.with(mContext)
                    .load(user.getTools_applied())
                    .into((holder).ivEffect);

            if (image != null && !image.isEmpty()) {
                Picasso.get().load(image).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(holder.iv);
            } else {
                Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).into(holder.iv);
            }

            holder.tvName.setText(user.getName());
            holder.tvCoincount.setText(user.getGift_value());
            String levelTextLower = " Level " + user.getLevel();
            holder.tvLevel.setText(levelTextLower);

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

        ImageView iv, ivFirstImage, ivEffect;
        LinearLayout llLowerPosition;
        RelativeLayout ll;
        TextView tvCoincount, tvLevel;
        TextView tvName;

        private ViewHolder(View itemView) {
            super(itemView);

            ivEffect = itemView.findViewById(R.id.iv_effect);
            iv = itemView.findViewById(R.id.iv);
            ivFirstImage = itemView.findViewById(R.id.iv_profile_1);
            tvName = itemView.findViewById(R.id.tv_name);
            ll = itemView.findViewById(R.id.ll);
            tvCoincount = itemView.findViewById(R.id.tv_received);
            tvLevel = itemView.findViewById(R.id.tv_level);
            llLowerPosition = itemView.findViewById(R.id.ll_lower_top);
        }
    }

    public void setOnClickListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void OnClicked(User user);
    }
}