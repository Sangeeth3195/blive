package com.blive.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.blive.R;

public class ActivityWallet extends BaseBackActivity {

    ImageView ivgWallet;
    ImageView tvmore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        setTitle("Wallet");

        ivgWallet = findViewById(R.id.iv_gWallet);
        tvmore = findViewById(R.id.tv_more);

        ivgWallet.setOnClickListener(v -> {
            changeActivity(ActivityInAppPurchase.class);
        });

        /*tvmore.setOnClickListener(v -> {
            Intent intent = new Intent(this, ActivityAdvancedWV.class);
            intent.putExtra("title", "Wallet");
            intent.putExtra("from", "profile");
            intent.putExtra("url", Constants_api.wallet + SessionUser.getUser().getUser_id());
            startActivity(intent);
        });*/
    }
}
