package com.blive.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blive.model.Audience;
import com.blive.R;
import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.net.URLDecoder;
import java.util.ArrayList;

/**
 * Created by sans on 15-10-2018.
 **/

public class AdapterRequests extends RecyclerView.Adapter<AdapterRequests.ViewHolder> {

    private Context mContext;
    private ArrayList<Audience> users;
    private Listener listener;

    public AdapterRequests(Context mContext, ArrayList<Audience> users) {
        this.mContext = mContext;
        this.users = users;
    }

    @NonNull
    @Override
    public AdapterRequests.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
            return new AdapterRequests.ViewHolder(layout);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterRequests.ViewHolder holder, final int position) {
        try {
            final Audience user = users.get(position);

            String base64 = user.getProfile_pic();
            String image = URLDecoder.decode(base64,"UTF-8");

            if (image != null && !image.isEmpty()) {
                Picasso.get().load(image).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(holder.iv);
            }else {
                Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(holder.iv);
            }

            holder.tvName.setText(user.getName());

            holder.rlAccept.setOnClickListener(v -> {
                if (listener != null) {
                    try {
                        listener.onAcceptRequest(users.get(holder.getAdapterPosition()),holder.getAdapterPosition());
                    }catch (Exception e){
                        Crashlytics.logException(e);
                    }
                }
            });

            holder.rlReject.setOnClickListener(v -> {
                if (listener != null) {
                    try {
                        listener.onRejectRequest(users.get(holder.getAdapterPosition()),holder.getAdapterPosition());
                    }catch (Exception e){
                        Crashlytics.logException(e);
                    }

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

        ImageView iv;
        TextView tvName;
        RelativeLayout rlAccept,rlReject;

        private ViewHolder(View itemView) {
            super(itemView);

            iv = itemView.findViewById(R.id.iv);
            tvName = itemView.findViewById(R.id.tv_name);
            rlAccept = itemView.findViewById(R.id.rl_accept);
            rlReject = itemView.findViewById(R.id.rl_reject);
        }
    }

    public void setOnClickListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onAcceptRequest(Audience user,int position);
        void onRejectRequest(Audience user,int position);
    }
}