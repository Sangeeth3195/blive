package com.blive.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.blive.model.Audience;
import com.blive.R;
import com.bumptech.glide.Glide;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.net.URLDecoder;
import java.util.ArrayList;

public class AdapterImages extends RecyclerView.Adapter<AdapterImages.ViewHolder> {
    private Context mContext;
    private ListenerImage listenerImage;
    private ArrayList<Audience> audiences;

    public AdapterImages(Context mContext, ArrayList<Audience> audiences) {
        this.mContext = mContext;
        this.audiences = audiences;
    }

    @NonNull
    @Override
    public AdapterImages.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
            return new AdapterImages.ViewHolder(layout);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterImages.ViewHolder holder, final int position) {
        try {

            final Audience audience = audiences.get(position);
            String base64 = audience.getProfile_pic();
            String image = URLDecoder.decode(base64, "UTF-8");

            if (image != null && !image.isEmpty()) {
                Picasso.get().load(image).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(holder.iv);
            } else {
                Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(holder.iv);
            }

            if(!audiences.get(position).getDpEffects().isEmpty()){
                Glide.with(mContext)
                        .load(audience.getDpEffects())
                        .into(holder.ivEffect);
            }

            holder.iv.setOnClickListener(v -> {
                if (listenerImage != null) {
                    listenerImage.OnClickedAudience(audiences.get(holder.getAdapterPosition()));
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
        LinearLayout ll;

        private ViewHolder(View itemView) {
            super(itemView);

            iv = itemView.findViewById(R.id.iv);
            ivEffect = itemView.findViewById(R.id.iv_effect);
            ll = itemView.findViewById(R.id.ll);
        }
    }

    public void setOnClickListener(ListenerImage listenerImage) {
        this.listenerImage = listenerImage;
    }

    public interface ListenerImage {
        void OnClickedAudience(Audience audience);
    }
}