package com.blive.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.blive.R;
import com.blive.adapter.MyProductAdapter;
import com.blive.model.AccountResponseInapp;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.session.SessionUser;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityInAppPurchase extends AppCompatActivity implements PurchasesUpdatedListener {

    BillingClient billingClient;
    RecyclerView recyclerView;
    private ProgressDialog progressDoalog;
    public static String diamond = "";
    public static String amount = "";
    RelativeLayout rl_back;
    TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_purchase);
        progressDoalog = new ProgressDialog(ActivityInAppPurchase.this);
        progressDoalog.setMessage("Loading....");
        progressDoalog.show();
        setupBillingClient();
        recyclerView = findViewById(R.id.my_recycler_view);
        rl_back = findViewById(R.id.rl_back);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("Google Wallet");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void loadProductToRecycler(List<SkuDetails> list) {
        Log.i("autolog", "list: " + list);
        MyProductAdapter adapter = new MyProductAdapter(this, list, billingClient);
        recyclerView.setHasFixedSize(true);
        progressDoalog.dismiss();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);




    }

    private void setupBillingClient() {
        billingClient = BillingClient.newBuilder(this)
                .setListener(this)
                .enablePendingPurchases()
                .build();


        billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP,
                (billingResult, list) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                            && list != null) {
                        for (PurchaseHistoryRecord purchase : list) {
                            Log.i("autolog", "list: " + list.size());
                            // Process the result.
                            String purchasedSku = purchase.getSku();
                            Log.i("dev", "Purchased SKU: " + purchasedSku);
                            String purchaseToken = purchase.getPurchaseToken();
                            ConsumeParams consumeParams = ConsumeParams.newBuilder()
                                    .setPurchaseToken(purchaseToken)
                                    .build();
                            ConsumeResponseListener consumeResponseListener = new ConsumeResponseListener() {
                                @Override
                                public void onConsumeResponse(BillingResult billingResult, String s) {
                                    Log.i("autolog", "s: " + s);
                                    Log.i("autolog", "billingResult: " + billingResult.getDebugMessage());
                                }
                            };
                            billingClient.consumeAsync(consumeParams, consumeResponseListener); // Test to 'undo' the purchase TEST
                        }
                    }

                });

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
//                Toast.makeText(ActivityInAppPurchase.this, "Success", Toast.LENGTH_SHORT).show();
                if (billingClient.isReady()) {
                    SkuDetailsParams params = SkuDetailsParams.newBuilder()
                            .setSkusList(Arrays.asList("80_diamonds", "280_diamond", "480_diamond",
                                    "1000_diamond", "1330_diamonds", "3110_diamonds", "4550_diamonds", "10010_diamonds"))
                            .setType(BillingClient.SkuType.INAPP)
                            .build();
                    billingClient.querySkuDetailsAsync(params, (billingResult1, list) -> loadProductToRecycler(list)


                    );
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(ActivityInAppPurchase.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> list) {

        Log.i("autolog", "billingResult: " + billingResult.getResponseCode());

        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && list != null) {
            for (Purchase purchase : list) {
                Log.d("autolog", "onPurchasesUpdated() response: " + billingResult.getResponseCode());
                Log.i("dev", "successful purchase...");
                String purchasedSku = purchase.getSku();
                Log.i("dev", "Purchased SKU: " + purchasedSku);
                String purchaseToken = purchase.getPurchaseToken();


                ConsumeParams consumeParams = ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();


                ConsumeResponseListener consumeResponseListener = new ConsumeResponseListener() {
                    @Override
                    public void onConsumeResponse(BillingResult billingResult, String s) {
                        Log.i("autolog", "s: " + s);
                        Log.i("autolog", "billingResult: " + billingResult);

                    }
                };
                billingClient.consumeAsync(consumeParams, consumeResponseListener); // Test to 'undo' the purchase TEST
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Log.d("onPurchasesUpdated", "onPurchasesUpdated() response: User cancelled" + billingResult.getResponseCode());
        } else {
            // Handle any other error codes.
        }
        if (billingResult.getResponseCode() == 0) {
            try {


                Log.i("autolog", "list: " + list.get(0).getOriginalJson());
                Log.i("autolog", "list: " + list.size());
                JSONObject obj = new JSONObject(list.get(0).getOriginalJson());
                String orderId = obj.getString("orderId");
                String productId = obj.getString("productId");
                String packageName = obj.getString("packageName");
                String purchaseTime = obj.getString("purchaseTime");
                String purchaseState = obj.getString("purchaseState");
                String purchaseToken = obj.getString("purchaseToken");
                String acknowledged = obj.getString("acknowledged");

                /*Create handle for the RetrofitInstance interface*/
                final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
                Call<AccountResponseInapp> call = apiClient.getAllPhotos(SessionUser.getUser().getUser_id(), orderId, packageName, productId, purchaseTime, purchaseState, purchaseToken, acknowledged);
                call.enqueue(new Callback<AccountResponseInapp>() {
                    @Override
                    public void onResponse(Call<AccountResponseInapp> call, Response<AccountResponseInapp> response) {
                        Log.i("autolog", "response: " + response.raw().request().url());
                        if (response != null) {
                            Toast.makeText(ActivityInAppPurchase.this, "" + response.isSuccessful(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ActivityInAppPurchase.this, "" + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AccountResponseInapp> call, Throwable t) {
                        Log.i("autolog", "t: " + t.getMessage());
                        Toast.makeText(ActivityInAppPurchase.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(this, "Purchase Item :" + billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.i("autolog", "e: " + e.toString());
            }
        } else {
            if (billingResult.getResponseCode() == 7) {
                Toast.makeText(this, "you already owned Item", Toast.LENGTH_SHORT).show();
            }
            /*apiinterface service = RetrofitClientInstance.getRetrofitInstance().create(apiinterface.class);
            Call<AccountResponseInapp> call = service.getAllPhotos("100001", "", "", diamond, "", String.valueOf(billingResult.getResponseCode()), "", "");
            call.enqueue(new Callback<AccountResponseInapp>() {
                @Override
                public void onResponse(Call<AccountResponseInapp> call, Response<AccountResponseInapp> response) {
                    Log.i("autolog", "response: " + response.raw().request().url());
                    if (response != null) {
                        Toast.makeText(ActivityInAppPurchase.this, "" + response.isSuccessful(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ActivityInAppPurchase.this, "" + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<AccountResponseInapp> call, Throwable t) {
                    Log.i("autolog", "t: " + t.getMessage());
                    Toast.makeText(ActivityInAppPurchase.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                }
            });*/
//            Toast.makeText(this, "Purchase Item :" + billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
        }
    }

}
