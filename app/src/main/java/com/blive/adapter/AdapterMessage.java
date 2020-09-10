package com.blive.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blive.activity.ActivityLiveRoom;
import com.blive.model.Gift;
import com.blive.model.MessageBean;
import com.blive.R;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;


import static com.crashlytics.android.core.CrashlyticsCore.TAG;

public class AdapterMessage extends RecyclerView.Adapter<AdapterMessage.MyViewHolder> {

    private List<MessageBean> messageBeanList;
    private ListenerMessage listenerMessage;
    private Context mContext;
    private ArrayList<Gift> giftsList;
    private ArrayList<String> ids;

    public AdapterMessage(Context context, List<MessageBean> messageBeanList) {
        mContext = context;
        this.messageBeanList = messageBeanList;
        ids = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            setupView(holder, position);
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    public void setGifts(ArrayList<Gift> giftsList) {
        this.giftsList = giftsList;
    }

    @Override
    public int getItemCount() {
        return messageBeanList.size();
    }

    private void setupView(MyViewHolder holder, int position) {

        MessageBean bean = messageBeanList.get(position);
        bean.setBeSelf(false);

        if (bean.isWarning()) {
            holder.tvWarning.setVisibility(View.VISIBLE);
            holder.rl.setVisibility(View.GONE);
        } else {
            holder.tvWarning.setVisibility(View.GONE);
            holder.rl.setVisibility(View.VISIBLE);
            String id = bean.getMessage().substring(0, 6);
            String level = bean.getMessage().substring(6, 8);

            try {
                int lvl = Integer.valueOf(level);

                if (position > ids.size())
                ids.add(id);
                Log.i("autolog", "lvl: " + lvl);

            if (lvl <= 5)
                holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_sandal);
            else if (lvl <= 10)
                holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_blue);
            else if (lvl <= 15)
                holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_light_red);
            else if (lvl <= 20)
                holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_green);
            else if (lvl <= 25)
                holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_brown);
            else if (lvl <= 30)
                holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_pink);
            else if (lvl <= 35)
                holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_deep_blue);
            else if (lvl <= 40)
                holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_black);
            else if (lvl <= 45)
                holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_green_yellow);
            else if (lvl <= 50)
                holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_red);
            else if (lvl <= 55)
                holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_green_greyish);
            else if (lvl <= 60)
                holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_yellow);
            else if (lvl <= 65)
                holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_teal);
            else if (lvl <= 70)
                holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_blue);
            else if (lvl <= 75)
                holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_voilet);
            else if (lvl <= 80)
                holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_pink_dark);
            else if (lvl <= 85)
                holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_lime);
            else if (lvl <= 90)
                holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_green);
            else if (lvl == 98)
                holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_pkbroad);
            else if (lvl == 99)
                holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_pkguest);

            holder.tvLevel.setText(level);

             if (lvl == 98)
                 holder.tvLevel.setText("★");
             else if (lvl == 99)
                 holder.tvLevel.setText("★");





                String message = " " + bean.getMessage().substring(8, bean.getMessage().length());
            Spannable spannable = new SpannableString(message);

            if (bean.isGift()) {
                spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.colorAccent)), 0, message.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                setGiftImage(holder, message);
            } else
                holder.ivGift.setVisibility(View.GONE);

            holder.textViewOtherMsg.setText(spannable, TextView.BufferType.SPANNABLE);
            holder.textViewOtherMsg.setTextColor(mContext.getResources().getColor(R.color.white));

            holder.textViewOtherMsg.setOnClickListener(v -> {
                Log.e(TAG, "setupView: " + " Clicked ");
                try {
                    if (listenerMessage != null) {
                        Log.e(TAG, "setupView: " + " Clicked  1");
                        listenerMessage.onMessageClicked(messageBeanList.get(holder.getAdapterPosition()).getAccount(), ids.get(holder.getAdapterPosition() - 1));
                    }
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
            });

            if (bean.getBackground() != 0) {
                holder.textViewOtherName.setBackgroundResource(bean.getBackground());
            }
            } catch (Exception e) {
                Log.i("autolog", "e: " + e);
                holder.rl.setVisibility(View.GONE);
            }
        }
    }

    private void setGiftImage(MyViewHolder holder, String message) {
        try {
            if (giftsList.size() > 0) {
                holder.ivGift.setVisibility(View.VISIBLE);
                for (int i = 0; i < giftsList.size(); i++) {
                    if (message.contains(giftsList.get(i).getName())) {
                        Glide.with(mContext).load(giftsList.get(i).getIcon()).into(holder.ivGift);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "setGiftImage: " + e);
            Crashlytics.logException(e);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewOtherName, tvLevel;
        private TextView tvWarning;
        private TextView textViewOtherMsg;
        private RelativeLayout rl;
        private ImageView ivGift;

        MyViewHolder(View itemView) {
            super(itemView);

            textViewOtherName = itemView.findViewById(R.id.tv_bg);
            textViewOtherMsg = itemView.findViewById(R.id.item_msg_l);
            tvWarning = itemView.findViewById(R.id.tv_warning);
            rl = itemView.findViewById(R.id.rl_message);
            ivGift = itemView.findViewById(R.id.iv_gift);
            tvLevel = itemView.findViewById(R.id.tv_level);
        }
    }

    public void setOnClickListener(ListenerMessage listenerMessage) {
        this.listenerMessage = listenerMessage;
    }

    public interface ListenerMessage {
        void onMessageClicked(String name, String id);
    }
}