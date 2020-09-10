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
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterGuest extends RecyclerView.Adapter<AdapterGuest.ViewHolder> {

    private Context mContext;
    private ArrayList<Audience> users;
    private Listener listener;

    public AdapterGuest(Context mContext, ArrayList<Audience> users) {
        this.mContext = mContext;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_guest, parent, false);
            return new ViewHolder(layout);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {
            final Audience user = users.get(position);

            String image =user.getProfile_pic();

            if (image != null && !image.isEmpty()) {
                Picasso.get().load(image).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(holder.iv);
            }else {
                Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(holder.iv);
            }

            holder.tvName.setText(user.getUsername());
            holder.rlReject.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onGiftSend(users.get(holder.getAdapterPosition()), holder.getAdapterPosition());
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
        RelativeLayout rlReject;

        private ViewHolder(View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.ivGuestPic);
            tvName = itemView.findViewById(R.id.tv_GuestName);
            rlReject = itemView.findViewById(R.id.onClickSend);
        }
    }

    public void setOnClickListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onGiftSend(Audience user, int position);
    }
}
