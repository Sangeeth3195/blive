package com.blive.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.widget.Switch;

import com.blive.BLiveApplication;
import com.blive.model.ProfileResponse;
import com.blive.R;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.session.SessionUser;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class ActivityPrivacySettings extends BaseBackActivity {

    @BindView(R.id.bliveIdSwitch)
    Switch bliveSwitch;
    @BindView(R.id.dobSwitch)
    Switch dobSwitch;
    @BindView(R.id.genderSwitch)
    Switch genderSwitch;
    @BindView(R.id.locationSwitch)
    Switch locationSwitch;
    @BindView(R.id.ageSwitch)
    Switch ageSwitch;

    private String bid_hidden = "", dob_hidden = "", gender_hidden = "", location_hidden = "", age_hidden = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_settings);
        BLiveApplication.setCurrentActivity(this);
        initUI();
    }

    private void initUI() {

        setTitle("Privacy Settings");

        bid_hidden = SessionUser.getUser().getIs_the_user_id_hidden();
        dob_hidden = SessionUser.getUser().getIs_the_dob_hidden();
        gender_hidden = SessionUser.getUser().getIs_the_gender_hide();
        location_hidden = SessionUser.getUser().getIs_the_location_hidden();
        age_hidden = SessionUser.getUser().getIs_the_age_hidden();

        if (bid_hidden.equals("no")) {
            bliveSwitch.setChecked(false);
        } else {
            bliveSwitch.setChecked(true);
        }
        if (dob_hidden.equals("no")) {
            dobSwitch.setChecked(false);
        } else {
            dobSwitch.setChecked(true);
        }
        if (gender_hidden.equals("no")) {
            genderSwitch.setChecked(false);
        } else {
            genderSwitch.setChecked(true);
        }

        if (location_hidden.equals("no")) {
            locationSwitch.setChecked(false);
        } else {
            locationSwitch.setChecked(true);
        }

        if (age_hidden.equals("no")) {
            ageSwitch.setChecked(false);
        } else {
            ageSwitch.setChecked(true);
        }

        bliveSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                bid_hidden = "yes";
            } else {
                bid_hidden = "no";
            }
        });

        dobSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                dob_hidden = "yes";
            } else {
                dob_hidden = "no";
            }
        });

        genderSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                gender_hidden = "yes";
            } else {
                gender_hidden = "no";
            }
        });

        locationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                location_hidden = "yes";
            } else {
                location_hidden = "no";
            }
        });

        ageSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                age_hidden = "yes";
            } else {
                age_hidden = "no";
            }
        });
    }

    public void saveChanges() {
        if (utils.isNetworkAvailable()) {
            utils.showProgress();
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<ProfileResponse> call = apiClient.updatePrivacy(SessionUser.getUser().getUser_id(),bid_hidden,age_hidden,dob_hidden,gender_hidden, "no" );
            call.enqueue(new retrofit2.Callback<ProfileResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                    utils.hideProgress();
                    ProfileResponse profileResponse = response.body();
                    if (response.code() == 200) {
                        if (profileResponse != null) {
                            if (profileResponse.getStatus().equalsIgnoreCase("success")) {
                                SessionUser.saveUser(profileResponse.getData().getUser());
                                Intent returnIntent = new Intent();
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();
                            } else {
                                showToast(profileResponse.getMessage());
                            }
                        } else {
                            showToast(getString(R.string.server_error));
                        }
                    } else {
                        checkResponseCode(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                    utils.hideProgress();
                    showToast(t.getMessage());
                }
            });
        }
    }

    @OnClick(R.id.rl_back)
    public void onClickBack() {
        if(validation()){
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        dialog.dismiss();
                        saveChanges();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        finish();
                        break;
                }
            };
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setMessage("Do you want to save the changes ?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }else
            finish();
    }

    @Override
    public void onBackPressed() {
        if(validation()){
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        dialog.dismiss();
                        saveChanges();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        finish();
                        break;
                }
            };
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setMessage("Do you want to save the changes ?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }else
            finish();
    }

    private boolean validation() {
        return !bid_hidden.equalsIgnoreCase(SessionUser.getUser().getIs_the_user_id_hidden()) ||
                !age_hidden.equalsIgnoreCase(SessionUser.getUser().getIs_the_age_hidden()) ||
                !dob_hidden.equalsIgnoreCase(SessionUser.getUser().getIs_the_dob_hidden()) ||
                !gender_hidden.equalsIgnoreCase(SessionUser.getUser().getIs_the_gender_hide());
    }

}
