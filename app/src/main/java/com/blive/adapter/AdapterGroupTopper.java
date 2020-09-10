package com.blive.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blive.model.User;
import com.blive.R;
import com.bumptech.glide.Glide;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.net.URLDecoder;
import java.util.ArrayList;


public class AdapterGroupTopper extends RecyclerView.Adapter<AdapterGroupTopper.ViewHolder> {

    private Context mContext;
    private ArrayList<User> users;
    private Listener listener;

    public AdapterGroupTopper(Context mContext, ArrayList<User> users) {
        this.mContext = mContext;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gold_topper_list, parent, false);
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

            if (image != null && !image.isEmpty()) {
                Picasso.get().load(image).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(holder.iv);
            } else {
                Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(holder.iv);
            }

            Glide.with(mContext)
                    .load(user.getTools_applied())
                    .into((holder).ivEffect);

            holder.tvName.setText(user.getName());
            holder.goldCount.setText(user.getGift_value());

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "error " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv,ivEffect;
        TextView tvPosition, goldCount;
        TextView tvName;
        LinearLayout ll;

        private ViewHolder(View itemView) {
            super(itemView);

            ivEffect = itemView.findViewById(R.id.iv_effect);
            iv = itemView.findViewById(R.id.iv_user);
            tvName = itemView.findViewById(R.id.goldTopperName);
            goldCount = itemView.findViewById(R.id.toppergoldCount);
        }
    }

    public void setOnClickListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void OnClicked(User user);
    }
}
