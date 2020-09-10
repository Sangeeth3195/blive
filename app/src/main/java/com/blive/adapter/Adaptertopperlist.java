package com.blive.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blive.R;
import com.blive.model.PkGiftDetailsModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by sans on 24-Oct-19.
 **/

public class Adaptertopperlist extends RecyclerView.Adapter<Adaptertopperlist.viewholder> {
    ArrayList<PkGiftDetailsModel.Application> data=new ArrayList<>();
    Context context;

    public Adaptertopperlist(ArrayList<PkGiftDetailsModel.Application> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public Adaptertopperlist.viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layout = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_toppers, viewGroup, false);
        return new Adaptertopperlist.viewholder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull Adaptertopperlist.viewholder viewjholder, int i) {

        viewjholder.ranktxt.setText("No:"+""+i+1);
        viewjholder.nametxt.setText(data.get(i).getName());
        viewjholder.giftvalue.setText("Gift value:"+data.get(i).getGift_value());


        Glide.with(context)
                .load(data.get(i).getProfile_pic())
                .into(viewjholder.imagetopper);


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView ranktxt,nametxt,giftvalue;
        ImageView imagetopper;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            imagetopper=itemView.findViewById(R.id.imagetopper);
            ranktxt=itemView.findViewById(R.id.ranktxt);
            nametxt=itemView.findViewById(R.id.nametxt);
            giftvalue=itemView.findViewById(R.id.giftvalue);
        }
    }
}
