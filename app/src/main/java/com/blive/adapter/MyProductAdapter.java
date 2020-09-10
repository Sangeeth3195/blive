package com.blive.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.SkuDetails;
import com.blive.R;
import com.blive.activity.ActivityInAppPurchase;

import java.util.List;

import static com.blive.activity.ActivityInAppPurchase.amount;
import static com.blive.activity.ActivityInAppPurchase.diamond;


public class MyProductAdapter extends RecyclerView.Adapter<MyProductAdapter.ViewHolder> {

   ActivityInAppPurchase activityInAppPurchase;
    List<SkuDetails> skuDetailsList;
    BillingClient billingClient;

    public MyProductAdapter(ActivityInAppPurchase activityInAppPurchase, List<SkuDetails> skuDetailsList, BillingClient billingClient) {
        this.activityInAppPurchase = activityInAppPurchase;
        this.skuDetailsList = skuDetailsList;
        this.billingClient = billingClient;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activityInAppPurchase.getBaseContext())
                .inflate(R.layout.layout_product_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        viewHolder.txtProduct.setText(skuDetailsList.get(i).getDescription());
        viewHolder.txt_product1.setText(skuDetailsList.get(i).getPrice());
        viewHolder.txt_product1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetailsList.get(i))
                        .build();
                billingClient.launchBillingFlow(activityInAppPurchase, billingFlowParams);
                diamond = skuDetailsList.get(i).getDescription();
                Log.i("autolog", "diamond: " + diamond);
         amount = skuDetailsList.get(i).getOriginalPrice();
                Log.i("autolog", "amount: " +amount);
            }
        });

//        viewHolder.setiProductClickListener(new IProductClickListener() {
//            @Override
//            public void onProductClickListener(View view, int position) {
//
//                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
//                        .setSkuDetails(skuDetailsList.get(i))
//                        .build();
//                billingClient.launchBillingFlow(activityInAppPurchase,billingFlowParams);
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return skuDetailsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtProduct, txt_product1;

        com.blive.IProductClickListener iProductClickListener;

        public void setiProductClickListener(com.blive.IProductClickListener iProductClickListener) {
            this.iProductClickListener = iProductClickListener;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProduct = itemView.findViewById(R.id.txt_product);
            txt_product1 = itemView.findViewById(R.id.txt_product1);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
//            iProductClickListener.onProductClickListener(itemView,getAdapterPosition());
        }
    }
}
