package com.blive.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blive.R;
import com.blive.model.FCMModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by sans on 18-Sep-19.
 **/


public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.MyViewHolder> {


    Context context;
    ArrayList<FCMModel> fcmModels = new ArrayList<>();

    public NotificationListAdapter(Context context, ArrayList<FCMModel> fcmModels) {
        this.context = context;
        this.fcmModels = fcmModels;
    }

    @NonNull
    @Override
    public NotificationListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notificationlist_item, viewGroup, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationListAdapter.MyViewHolder myViewHolder, int i) {
        myViewHolder.title.setText(fcmModels.get(i).getNotificationTitle());
        Log.d("title", fcmModels.get(i).getNotificationImage());
        Log.d("title", String.valueOf(fcmModels.size()));
        myViewHolder.Content.setText(fcmModels.get(i).getNotificationcontent());
        myViewHolder.date.setText(fcmModels.get(i).getDate());
        Log.i("autolog", "fcmModels.get(i).getDate(): " + fcmModels.get(i).getDate());

        Glide.with(context)
                .load(fcmModels.get(i).getNotificationImage())
                .into(myViewHolder.Image);

    }

    @Override
    public int getItemCount() {
        return fcmModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, Content, date;
        ImageView Image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.Notification_title);
            Image = itemView.findViewById(R.id.Notification_image);
            Content = itemView.findViewById(R.id.Notification_content);
            date = itemView.findViewById(R.id.Notification_date);

        }
    }
}
