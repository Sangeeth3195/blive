package com.blive.activity;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blive.R;
import com.blive.model.GenericResponse;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.session.SessionUser;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Response;

public class ActivityScanner extends BaseScannerActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_scanner);
        setupToolbar();

        ViewGroup contentFrame = findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(final Result rawResult) {

        Toast.makeText(this, rawResult.getText(), Toast.LENGTH_SHORT).show();
        getQRcode(String.valueOf(rawResult));
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            getQRcode(String.valueOf(rawResult));
            mScannerView.resumeCameraPreview(ActivityScanner.this);
        }, 5000);
    }

    private void getQRcode(String rawResult) {
        if (utils.isNetworkAvailable()) {
            utils.showProgress();
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<GenericResponse> call = apiClient.scanQrCode(rawResult, SessionUser.getUser().getUser_id());
            call.enqueue(new retrofit2.Callback<GenericResponse>() {
                @Override
                public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                    utils.hideProgress();
                    GenericResponse genericResponse = response.body();
                    if (response.code() == 200) {
                        if (genericResponse != null) {
                            if (genericResponse.getStatus().equalsIgnoreCase("success")) {
                                showToast(genericResponse.getMessage());
                            } else {
                                showToast(genericResponse.getMessage());
                            }
                        } else {
                            utils.showToast(getString(R.string.server_error));
                        }
                    } else {
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                    showToast(t.getMessage());
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
