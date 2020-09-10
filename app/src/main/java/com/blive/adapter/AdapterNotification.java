package com.blive.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blive.model.Notification;
import com.blive.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.net.URLDecoder;
import java.util.ArrayList;


/**
 * Created by sans on 14-09-2018.
 **/

public class AdapterNotification extends RecyclerView.Adapter<AdapterNotification.ViewHolder> {

    private Context mContext;
    private ArrayList<Notification> notifications;
    private Listener listener;

    public AdapterNotification(Context mContext, ArrayList<Notification> notifications) {
        this.mContext = mContext;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public AdapterNotification.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notifications, parent, false);
            return new AdapterNotification.ViewHolder(layout);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterNotification.ViewHolder holder, final int position) {
        try {
            final Notification notification = notifications.get(position);

/*
            if (notification.getBroadcast_type().equalsIgnoreCase("pk")){
                holder.ll.setVisibility(View.GONE);
            }
*/
            Log.d("private", "onBindViewHolder: "+notification.getUser_id()+notification.getStatus());

            holder.tvMessage.setText(notification.getMessage());
            holder.tv_bliveid.setText("ID: "+notification.getUser_id());
            holder.tv_timelabel.setText(notification.getTime());
            if (notification.getStatus().equalsIgnoreCase("PRIVATE")){
                holder.tv_type.setText("\uD83D\uDD12"+" Private Live");
                holder.tv_type.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            }else {
                holder.tv_type.setText("In " + notification.getBroadcast_type() + "- Say hii..");
            }


            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.OnClicked(notification);
                }
            });

            String base64 = notification.getProfile_pic();
            String image = URLDecoder.decode(base64,"UTF-8");

            if (image != null && !image.isEmpty()) {
                Picasso.get().load(image).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(holder.iv);
            }else {
                Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(holder.iv);
            }

//            holder.tvDate.setText(TimeUtils.getDate(notification.getCreated_at()));
//            holder.tvTime.setText(TimeUtils.getTimeFromStringIn24Hours(notification.getCreated_at()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        Log.i("autolog", "notifications.size(): " + notifications.size());
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv;
        TextView tvMessage;
        TextView tvDate,tvTime,tv_bliveid,tv_type,tv_timelabel;
        CardView ll;

        private ViewHolder(View itemView) {
            super(itemView);

            iv = itemView.findViewById(R.id.iv);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            tv_bliveid = itemView.findViewById(R.id.tv_bliveid);
            tv_type = itemView.findViewById(R.id.tv_type);
            tv_timelabel = itemView.findViewById(R.id.tv_timelabel);
            ll = itemView.findViewById(R.id.layout);
        }
    }

    public void setOnClickListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void OnClicked(Notification notification);
    }
}