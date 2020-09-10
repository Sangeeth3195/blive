package com.blive.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.blive.BLiveApplication;
import com.blive.R;
import com.blive.chat.chatmodels.ChatUser;
import com.blive.chat.chatutil.ChatUtils;
import com.blive.model.SignupResponse;
import com.blive.model.User;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.session.SessionLogin;
import com.blive.session.SessionUser;
import com.blive.utils.Utils;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.SignUpEvent;
import com.facebook.login.LoginManager;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sans on 28-11-2018.
 **/

public class ActivityNewUser extends BaseBackActivity implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_userName)
    EditText etUserName;
    @BindView(R.id.et_mobile)
    EditText etMobile;
    @BindView(R.id.rl_genderMale)
    RelativeLayout rlGenderMale;
    @BindView(R.id.rl_genderFeMale)
    RelativeLayout rlGenderFeMale;
    @BindView(R.id.tv_genderFeMaleText)
    TextView tvGenderFeMale;
    @BindView(R.id.tv_genderMaleText)
    TextView tvGenderMale;
    @BindView(R.id.input_mobile)
    TextInputLayout inputMobile;
    @BindView(R.id.input_name)
    TextInputLayout inputName;
    @BindView(R.id.input_username)
    TextInputLayout inputUserName;
    @BindView(R.id.et_referral)
    EditText etReferral;
    @BindView(R.id.input_referral)
    TextInputLayout inputReferral;
    ImageView ivPic;
    private String name = "", userName = "", gender = "Male", mobile = "", email = "", domain = "", image = "", referral = "", regId = "";
    String[] genders = {"Male", "Female"};
    private SharedPreferences sharedPreferences;

    private ChatUtils helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        BLiveApplication.setCurrentActivity(this);

        initUI();
    }

    protected void initUI() {
        helper = new ChatUtils(this);
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        email = intent.getStringExtra("email");
        domain = intent.getStringExtra("domain");
        image = intent.getStringExtra("image");
        mobile = intent.getStringExtra("mobile");

        displayFireBaseRegId();

        sharedPreferences = getApplicationContext().getSharedPreferences("Mobile_Domain", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("isDomain", domain);
        editor.putString("isValue", "0");
        editor.commit();

        if (!domain.equalsIgnoreCase("mobile")) {
            inputMobile.setVisibility(View.GONE);
            mobile = "";
        } else
            inputMobile.setVisibility(View.VISIBLE);

        ivPic = findViewById(R.id.ivPic);

        if (userName != null) {
            userName = userName.replace(" ", "");
            name = userName;
            userName = userName.replace(".", "");
            userName = userName.replace("#", "");
            userName = userName.replace("$", "");
            userName = userName.replace("[", "");
            userName = userName.replace("]", "");
            etName.setText(name);
            etUserName.setText(userName);
        }

        Log.e(TAG, "initUI: " + image);
        if (image != null && !image.isEmpty()) {
            Glide.with(this).load(image).into(ivPic);
        } else {
            Glide.with(this).load(R.drawable.user).into(ivPic);
        }

        if (mobile != null) {
            etMobile.setText(mobile);
            etMobile.setEnabled(false);
        }

        rlGenderMale.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.gender_male_background));
        rlGenderFeMale.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.gender_female_unselected));
        tvGenderFeMale.setTextColor(getApplicationContext().getResources().getColor(R.color.black));
        tvGenderMale.setTextColor(getApplicationContext().getResources().getColor(R.color.white));

        etName.requestFocus();

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (inputName.isErrorEnabled())
                    inputName.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (inputUserName.isErrorEnabled())
                    inputUserName.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etReferral.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (inputReferral.isErrorEnabled())
                    inputReferral.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputMobile.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void displayFireBaseRegId() {
        regId = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "displayFireBaseRegId: " + regId);
    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        gender = genders[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public void onGenderMaleClicked(View view) {
        gender = "Male";
        rlGenderMale.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.gender_male_background));
        rlGenderFeMale.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.gender_female_unselected));
        tvGenderFeMale.setTextColor(getApplicationContext().getResources().getColor(R.color.black));
        tvGenderMale.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
    }

    public void onGenderFeMaleClicked(View view) {
        gender = "Female";
        rlGenderMale.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.gender_male_unselected));
        rlGenderFeMale.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.gender_female_background));
        tvGenderFeMale.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
        tvGenderMale.setTextColor(getApplicationContext().getResources().getColor(R.color.black));
    }

    @OnClick(R.id.rl_create)
    public void onCreateAccount() {
        if (utils.isNetworkAvailable()) {
            if (domain.equalsIgnoreCase("mobile")) {
                if (isValidMobile()) {
                    callSignUpAPI();
                }
            } else {
                if (isValid()) {
                    callSignUpAPI();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        utils.deleteCache(this);
    }

    private boolean isValid() {

        userName = etUserName.getText().toString().trim();
        name = etName.getText().toString().trim();
        referral = etReferral.getText().toString().trim();

        if (name.isEmpty()) {
            inputName.setError("Name must not be empty");
            showToast("Name must not be empty");
        } else if (name.length() < 3) {
            inputName.setError("Name must be minimum 3 characters");
            showToast("Name must be minimum 3 characters");
        } else if (userName.isEmpty()) {
            inputUserName.setError("UserName must not be empty");
            showToast("UserName must not be empty");
        } else if (userName.length() < 3) {
            inputUserName.setError("UserName must be minimum 3 characters");
            showToast("UserName must be minimum 3 characters");
        } else
            return true;

        return false;
    }

    private boolean isValidMobile() {

        userName = etUserName.getText().toString().trim();
        mobile = etMobile.getText().toString().trim();
        name = etName.getText().toString().trim();

        if (name.isEmpty()) {
            inputName.setError("Name must not be empty");
            showToast("Name must not be empty");
        } else if (name.length() < 3) {
            inputName.setError("Name must be minimum 3 characters");
            showToast("Name must be minimum 3 characters");
        } else if (userName.isEmpty()) {
            inputUserName.setError("UserName must not be empty");
            showToast("UserName must not be empty");
        } else if (userName.length() < 3) {
            inputUserName.setError("UserName must be minimum 3 characters");
            showToast("UserName must be minimum 3 characters");
        } else if (mobile.length() == 0) {
            inputMobile.setError("Mobile number is missing");
            showToast("Mobile number is missing");
        } else if (mobile.length() < 10) {
            inputMobile.setError("Mobile number is missing");
            showToast("Invalid Mobile Number");
        } else if (referral.equals(userName)) {
            inputReferral.setError("Invalid Referral Code!");
            showToast("Invalid Referral Code!");
        } else
            return true;

        return false;
    }

    public void callSignUpAPI() {
        Utils utils = new Utils(this);
        Log.e("DeviceID: ", utils.getDeviceId(this));
        String deviceId = utils.getDeviceId(this);
        utils.showProgress();
        final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
        referral = etReferral.getText().toString().trim();
        Log.e(TAG, "Referral Code : " + referral);
        Call<SignupResponse> call = apiClient.createAccount(name, userName, email, domain, image, mobile, gender, referral, deviceId, regId);
        call.enqueue(new retrofit2.Callback<SignupResponse>() {
            @Override
            public void onResponse(@NonNull Call<SignupResponse> call, @NonNull Response<SignupResponse> response) {

                if (response.code() == 200) {
                    SignupResponse signupResponse = response.body();
                    if (signupResponse != null) {
                        if (signupResponse.getStatus().equalsIgnoreCase("success")) {
                            User user = signupResponse.getData().getUser_details();
                            SessionUser.saveUser(user);
                            SessionLogin.saveLoginSession();
                            createUser(user);

                            //changeActivity(ActivityHome.class);
                        } else {
                            showToast(signupResponse.getMessage());
                        }
                    } else {
                        utils.showToast(getString(R.string.server_error));
                    }
                } else {
                    checkResponseCode(response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<SignupResponse> call, @NonNull Throwable t) {
                utils.hideProgress();
                showToast(t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        SessionLogin.clearLoginSession();
        LoginManager.getInstance().logOut();
        finishAffinity();
        changeActivity(ActivitySignIn.class);
    }

    private void createUser(final User newUser) {
        ChatUser chatUser = new ChatUser(newUser.getUser_id(), newUser.getUser_id(), (newUser.getProfile_pic() != null ? newUser.getProfile_pic() : ""),
                newUser.getName(), System.currentTimeMillis());
        BLiveApplication.getUserRef().child(newUser.getUser_id()).setValue(chatUser).addOnSuccessListener(aVoid -> {
            helper.setLoggedInUser(chatUser);
            utils.hideProgress();
            try {
                Answers.getInstance().logSignUp(new SignUpEvent()
                        .putMethod(domain)
                        .putSuccess(true));
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
            finishAffinity();

            Intent intent = new Intent(ActivityNewUser.this, ActivityHome.class);
            intent.putExtra("from", "splash");
            startActivity(intent);
        }).addOnFailureListener(e ->
                Toast.makeText(ActivityNewUser.this, "Something went wrong, unable to create user.", Toast.LENGTH_LONG).show());
    }
}