package com.blive.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.blive.BLiveApplication;
import com.blive.model.PinResponse;
import com.blive.model.User;
import com.blive.R;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.session.SessionUser;
import com.crashlytics.android.Crashlytics;

import retrofit2.Call;
import retrofit2.Response;

public class ActivityPin extends BaseBackActivity {

    Button bUnlock;
    PinEntryEditText pinEntry, pinEntry1;
    String reEnterOtp, enterOtp;
    TextView tvtxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        BLiveApplication.setCurrentActivity(this);
        initUI();
    }

    private void initUI() {
        setTitle("PIN");

        bUnlock = findViewById(R.id.bt_unlock);
        pinEntry = findViewById(R.id.txt_pin_entry);
        pinEntry1 = findViewById(R.id.txt_pin_entry_1);
        tvtxt = findViewById(R.id.tv_txt);

        try {
            int pin = SessionUser.getUser().getPin();
            String tempPin = String.valueOf(pin);
            Log.e(TAG, "displayPopUp : 1"+pin );
            if (!tempPin.isEmpty()) {
                if (tempPin.length() == 4) {
                    bUnlock.setText("Submit");
                    tvtxt.setText("Change Pin");
                } else {
                    bUnlock.setText("Create");
                    tvtxt.setText("Create Pin");
                }
            } else {
                bUnlock.setText("Create");
                tvtxt.setText("Create Pin");
            }
        } catch (Exception e) {
            Log.e(TAG, "displayPopUp: " + e);
            Crashlytics.logException(e);
        }

        if (pinEntry != null) {
            pinEntry.setOnPinEnteredListener(str -> {
                Log.e(TAG, "displayCreatePassword: " + str);
                if (str.toString().length() == 4) {
                    enterOtp = pinEntry.getText().toString();
                } else {
                    showToast("Please Enter All Fields!");
                }
            });
        }
        if (pinEntry1 != null) {
            pinEntry1.setOnPinEnteredListener(str1 -> {
                if (str1.toString().length() == 4) {
                    reEnterOtp = pinEntry1.getText().toString();
                } else {
                    showToast("Please Enter All Fields!");
                }
            });
        }

        bUnlock.setOnClickListener(v -> {
            if (utils.isNetworkAvailable()) {
                if (enterOtp != null  && reEnterOtp != null && enterOtp.length() == 4 && reEnterOtp.length() == 4) {
                    if (enterOtp.equals(reEnterOtp)) {
                        updatePassword(reEnterOtp);
                    } else {
                        Toast.makeText(mActivity, "PIN Mismatch!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showToast("Please fill all Fields!");
                }
            }
        });
    }

    private void updatePassword(String confirmPassword) {
        if (utils.isNetworkAvailable()) {
            utils.showProgress();
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<PinResponse> call = apiClient.updatePin(confirmPassword, SessionUser.getUser().getUser_id());
            call.enqueue(new retrofit2.Callback<PinResponse>() {
                @Override
                public void onResponse(@NonNull Call<PinResponse> call, @NonNull Response<PinResponse> response) {
                    utils.hideProgress();
                    PinResponse pinResponse = response.body();
                    if (response.code() == 200) {
                        if (pinResponse != null) {
                            if (pinResponse.getStatus().equalsIgnoreCase("success")) {
                                User user = SessionUser.getUser();
                                user.setPin(pinResponse.getData().getPin());
                                SessionUser.saveUser(user);
                                Intent returnIntent = getIntent();
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();
                                showToast("PIN Updated Successfully!");
                            } else {
                                showToast(pinResponse.getMessage());
                            }
                        } else {
                            utils.showToast(getString(R.string.server_error));
                        }
                    } else {
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PinResponse> call, @NonNull Throwable t) {
                    showToast(t.getMessage());
                }
            });
        }
    }
}
