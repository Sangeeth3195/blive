package com.blive.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blive.model.Audience;
import com.blive.R;
import com.bumptech.glide.Glide;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.net.URLDecoder;
import java.util.ArrayList;

public class AdapterActiveViewers extends RecyclerView.Adapter<AdapterActiveViewers.ViewHolder> {
    private Context mContext;
    private ListenerActiveViers listenerImage;
    private ArrayList<Audience> audiences;

    public AdapterActiveViewers(Context mContext, ArrayList<Audience> audiences) {
        this.mContext = mContext;
        this.audiences = audiences;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.active_viewrs, parent, false);
            return new ViewHolder(layout);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {

            final Audience audience = audiences.get(position);
            String base64 = audience.getProfile_pic();
            String image = URLDecoder.decode(base64, "UTF-8");

            holder.tvActiveViewer.setText(audience.getName());
            if (image != null && !image.isEmpty()) {
                Picasso.get().load(image).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(holder.iv);
            } else {
                Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(holder.iv);
            }

            if (!audiences.get(position).getDpEffects().isEmpty()) {
                Glide.with(mContext)
                        .load(audience.getDpEffects())
                        .into(holder.ivEffect);
            }

            holder.itemView.setOnClickListener(view -> {
                if (listenerImage != null) {
                    listenerImage.onClickedActiveAudience(audiences.get(holder.getAdapterPosition()));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return audiences.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv, ivEffect;
        TextView tvActiveViewer;
        private ViewHolder(View itemView) {
            super(itemView);

            iv = itemView.findViewById(R.id.iv);
            ivEffect = itemView.findViewById(R.id.iv_effect);
            tvActiveViewer = itemView.findViewById(R.id.tv_active_viewers_name);
        }
    }

    public void setOnClickListener(ListenerActiveViers listenerImage) {
        this.listenerImage = listenerImage;
    }

    public interface ListenerActiveViers {
        void onClickedActiveAudience(Audience audience);
    }
}
