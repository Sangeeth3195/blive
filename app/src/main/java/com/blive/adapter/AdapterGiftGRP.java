package com.blive.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blive.model.Gift;
import com.blive.R;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import static com.blive.BLiveApplication.TAG;

public class AdapterGiftGRP extends RecyclerView.Adapter<AdapterGiftGRP.ViewHolder> {

    private Context mContext;
    private ArrayList<Gift> gifts;
    private ListenerGift listenerGift;
    private int selected = -1;

    public AdapterGiftGRP(Context mContext, ArrayList<Gift> gifts) {
        this.mContext = mContext;
        this.gifts = gifts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gift, parent, false);
            return new ViewHolder(layout);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void onGiftReset() {
        selected = -1;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterGiftGRP.ViewHolder holder, final int position) {
        try {
            final Gift gift = gifts.get(position);

            try {
                Glide.with(mContext)
                        .load(gift.getIcon())
                        .into(holder.iv);
            } catch (Exception e) {
                Crashlytics.logException(e);
            }

            if (selected != -1) {
                if (position == selected) {
                    holder.ll.setBackground(mContext.getResources().getDrawable(R.drawable.border_orange));
                } else
                    holder.ll.setBackground(mContext.getResources().getDrawable(R.color.gift_trans));
            }

            if(!gift.getPrice().equals("0")){
                holder.tvPrice.setText(gift.getPrice());
            }else{
                holder.tvPrice.setVisibility(View.GONE);
                Glide.with(mContext)
                        .load(R.drawable.free_icon)
                        .into(holder.ivHeart);
            }

            if(gift.getPrice().equals("0")){
                holder.llGiftlayout.setVisibility(View.GONE);
                holder.tvPrice.setVisibility(View.GONE);
                holder.tvName.setVisibility(View.GONE);
            }

            holder.tvName.setText(gift.getName());

            holder.ll.setOnClickListener(v -> {
                if (listenerGift != null) {
                    if(holder.getAdapterPosition() >=0){
                      /*  holder.ll.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));*/
                        holder.ll.setBackground(mContext.getResources().getDrawable(R.drawable.border_orange));
                        selected = holder.getAdapterPosition();
                        listenerGift.OnClicked(gifts.get(holder.getAdapterPosition()));
                        notifyDataSetChanged();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "onBindViewHolder : " + e);
        }
    }

    @Override
    public int getItemCount() {
        return gifts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv,ivHeart;
        TextView tvName, tvPrice;
        LinearLayout ll,llGiftlayout;

        private ViewHolder(View itemView) {
            super(itemView);

            llGiftlayout = itemView.findViewById(R.id.ll_giftlayout);
            iv = itemView.findViewById(R.id.iv);
            ivHeart = itemView.findViewById(R.id.iv_default_heart);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            ll = itemView.findViewById(R.id.ll);

        }
    }

    public void setOnClickListener(ListenerGift listenerGift) {
        this.listenerGift = listenerGift;
    }

    public interface ListenerGift {
        void OnClicked(Gift gift);
    }

}
