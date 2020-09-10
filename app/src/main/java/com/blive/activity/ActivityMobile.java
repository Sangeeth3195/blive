package com.blive.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.blive.BLiveApplication;
import com.blive.R;
import com.blive.chat.chatmodels.ChatUser;
import com.blive.chat.chatutil.ChatUtils;
import com.blive.constant.Constants_app;
import com.blive.model.AccountResponse;
import com.blive.model.Country;
import com.blive.model.User;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.session.SessionLogin;
import com.blive.session.SessionUser;
import com.blive.utils.Utils;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sans on 21-02-2019.
 **/

public class ActivityMobile extends BaseBackActivity {

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private EditText etPhoneNumber, etOTP;
    private String country = "", countryCode = "", phoneNumber = "", mobile = "";
    private Button bVerify, bSendOTP;
    private TextView bReset;
    private int countryPosition = 0;
    @BindView(R.id.spCountry)
    SearchableSpinner spCountryName;

    private ChatUtils helper;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_mobile);
        initUI();
    }

    private void initUI() {
        helper = new ChatUtils(this);
        bVerify = findViewById(R.id.bVerify);
        bSendOTP = findViewById(R.id.bSendOtp);
        bReset = findViewById(R.id.bReset);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etOTP = findViewById(R.id.et_otp);

        Constants_app constants_app = new Constants_app();
        ArrayList<Country> countryArrayList = constants_app.getCountry(this);
        ArrayList<String> countries = new ArrayList<>();
        for (int i = 0; i < countryArrayList.size(); i++) {
            countries.add(countryArrayList.get(i).getName());
        }
        // Creating adapter for spinner
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(mActivity, R.layout.spinner_country, countries);
        spCountryName.setAdapter(countryAdapter);
        spCountryName.setSelection(countryPosition);

        spCountryName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    Country countrySelected = countryArrayList.get(position);
                    country = countrySelected.getName();
                    countryCode = countrySelected.getDialCode();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                utils.hideProgress();
                if (e.toString().contains("The format of the phone number provided is incorrect")) {
                    showToast("Please check country code and mobile number");
                } else
                    showToast("Please try again later!");

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded

                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.e(TAG, "onCodeSent:" + verificationId);
                Log.e(TAG, "onCodeSent:" + token);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String verificationId) {

            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        utils.deleteCache(this);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(mActivity, ActivitySignIn.class);
        startActivity(intent);
        Objects.requireNonNull(this).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        showToast("Verified Successfully");
                        FirebaseUser user = task.getResult().getUser();
                        checkUser(mobile, mobile);
                    } else {
                        utils.hideProgress();
                        // Sign in failed, display a message and update the UI
                        showToast("Verification Failed");
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                        }
                    }
                });
    }

    private void checkUser(String email, String mobile) {
        Utils utils = new Utils(this);
        String deviceId = utils.getDeviceId(this);
        utils.showProgress();
        final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
        Call<AccountResponse> call = apiClient.checkAccount(email, mobile, "mobile", deviceId);
        call.enqueue(new retrofit2.Callback<AccountResponse>() {
            @Override
            public void onResponse(@NonNull Call<AccountResponse> call, @NonNull Response<AccountResponse> response) {
                utils.hideProgress();
                if (response.code() == 200) {
                    AccountResponse accountResponse = response.body();
                    if (accountResponse != null) {
                        if (accountResponse.getStatus().equalsIgnoreCase("success")) {
                            if (accountResponse.getData().getMessage().equalsIgnoreCase("New user")) {
                                callNewUser();
                            } else if (accountResponse.getData().getMessage().equalsIgnoreCase("Already exsits")) {
                                //callOldUser(accountResponse.getData().getUser());
                                createUser(accountResponse.getData().getUser());
                            } else if (accountResponse.getData().getMessage().equalsIgnoreCase("Admin_BlocKEd")) {
                                Intent intent = new Intent(BLiveApplication.getCurrentActivity(), ActivityBlocked.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                BLiveApplication.getCurrentActivity().finish();
                                BLiveApplication.getCurrentActivity().startActivity(intent);
                            }
                        }
                    } else {
                        utils.showToast(getString(R.string.server_error));
                    }
                } else {
                    checkResponseCode(response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccountResponse> call, @NonNull Throwable t) {
                utils.hideProgress();
                showToast(t.getMessage());
            }
        });
    }

    private void callNewUser() {
        Intent intent = new Intent(ActivityMobile.this, ActivityNewUser.class);
        intent.putExtra("mobile", mobile);
        intent.putExtra("domain", "mobile");
        intent.putExtra("email", mobile);
        intent.putExtra("image", "");
        startActivity(intent);
    }

    private void callOldUser(User user) {
       /* if (user.getIs_this_user_blocked().equalsIgnoreCase("YES")) {
            utils.hideProgress();
            finishAffinity();
            changeActivity(ActivityBlocked.class);
        } else {*/
        Answers.getInstance().logLogin(new LoginEvent()
                .putMethod(user.getLogin_domain())
                .putSuccess(true));
        SessionUser.saveUser(user);
        SessionLogin.saveLoginSession();
        utils.hideProgress();
        finishAffinity();
        changeActivity(ActivityHome.class);
    }


    private void createUser(final User newUser) {
        ChatUser chatUser = new ChatUser(newUser.getUser_id(), newUser.getUser_id(), (newUser.getProfile_pic() != null ? newUser.getProfile_pic() : ""),
                newUser.getName(), System.currentTimeMillis());
        BLiveApplication.getUserRef().child(newUser.getUser_id()).setValue(chatUser).addOnSuccessListener(aVoid -> {
            helper.setLoggedInUser(chatUser);
            utils.hideProgress();
            callOldUser(newUser);

        }).addOnFailureListener(e ->
                Toast.makeText(ActivityMobile.this, "Something went wrong, unable to create user.", Toast.LENGTH_LONG).show());
    }
    /*   }*/

    @OnClick(R.id.bVerify)
    public void onVerifyClicked() {
        utils.showProgress();
        try {
            String code = etOTP.getText().toString().trim();
            if (code.length() > 1) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                Log.e(TAG, "onVerifyClicked: " + credential.getSmsCode());
                if (code.equalsIgnoreCase(credential.getSmsCode())) {
                    signInWithPhoneAuthCredential(credential);
                } else {
                    utils.hideProgress();
                    showToast("Invalid OTP code!");
                }
            } else {
                utils.hideProgress();
                showToast("Please Enter OTP to verify!");
            }
        } catch (Exception e) {
            utils.hideProgress();
            showToast("Verification failed please try after some time!");
        }
    }

    private String getE164Number() {
        return countryCode + etPhoneNumber.getText().toString().replaceAll("\\D", "").trim();
        // return PhoneNumberUtils.formatNumberToE164(mPhoneNumber.getText().toString(), mCountryIso);
    }

    private boolean isValidMobile(String phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length() < 7 || phone.length() > 15) {
                // if(phone.length() != 10) {
                etPhoneNumber.setError("Not Valid Number");
            } else {
                check = true;
            }
        }
        return check;
    }

    public void onSendOTPClicked(View view) {
        if (utils.isNetworkAvailable()) {
            if (!countryCode.isEmpty()) {
                if (isValidMobile(etPhoneNumber.getText().toString().trim())) {
                    phoneNumber = getE164Number();
                    mobile = getE164Number();
                    Log.e(TAG, "onVerifyClicked: " + phoneNumber);
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            this,               // Activity (for callback binding)
                            mCallbacks);        // OnVerificationStateChangedCallbacks

                    bSendOTP.setVisibility(View.GONE);
                    etOTP.setVisibility(View.VISIBLE);
                    bVerify.setVisibility(View.VISIBLE);
                    bReset.setVisibility(View.VISIBLE);
                    etPhoneNumber.setEnabled(false);
                }
            } else
                showToast("Select a Country !");

        }
    }

    public void onResetCLicked(View view) {
        etPhoneNumber.setText("");
        bReset.setVisibility(View.GONE);
        etOTP.setVisibility(View.GONE);
        etOTP.setText("");
        bVerify.setVisibility(View.GONE);
        bSendOTP.setVisibility(View.VISIBLE);
        etPhoneNumber.setEnabled(true);
    }

}
