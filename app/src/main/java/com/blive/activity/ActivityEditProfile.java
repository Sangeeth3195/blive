package com.blive.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.blive.apiTasks.APITask_UpdateImage;
import com.blive.adapter.AdapterProfilePic;
import com.blive.BLiveApplication;
import com.blive.chat.chatmodels.ChatUser;
import com.blive.chat.chatutil.ChatUtils;
import com.blive.fragment.dialog.DatePickerFragment;
import com.blive.model.Country;
import com.blive.model.DataListResponse;
import com.blive.model.ProfileResponse;
import com.blive.model.User;
import com.blive.R;
import com.blive.service.ApiClientBase;
import com.blive.service.ApiInterface;
import com.blive.session.SessionUser;
import com.blive.utils.TimeUtils;
import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

import static com.blive.constant.Constants_app.SELECT_PHOTO;
import static com.blive.constant.Constants_app.TAKE_PHOTO;


public class ActivityEditProfile extends BaseBackActivity implements APITask_UpdateImage.Listener {

    @BindView(R.id.cv_image_1)
    CardView cvImage1;
    @BindView(R.id.cv_image_2)
    CardView cvImage2;
    @BindView(R.id.cv_image_3)
    CardView cvImage3;
    @BindView(R.id.iv_profile_1)
    ImageView ivImage1;
    @BindView(R.id.iv_profile_2)
    ImageView ivImage2;
    @BindView(R.id.iv_profile_3)
    ImageView ivImage3;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_gender)
    EditText etGender;
    @BindView(R.id.et_mobile)
    EditText etMobile;
    @BindView(R.id.et_dob)
    EditText etDob;
    @BindView(R.id.et_bid)
    EditText etBid;
    @BindView(R.id.et_bio)
    EditText etBio;
    @BindView(R.id.ll_mobile)
    LinearLayout llMobile;
    @BindView(R.id.sp_city)
    Spinner spCity;
    @BindView(R.id.sp_state)
    Spinner spState;
    @BindView(R.id.sp_country)
    Spinner spCountry;
    @BindView(R.id.et_refferal)
    EditText etRefferal;
    @BindView(R.id.iv_copy)
    ImageView ivCopy;

    private boolean isFirstImage = false, isSecondImage = false, isThirdImage = false;
    private Uri imageUri,photoURI, tempUri,resultUri;
    String currentPhotoPath;
    private User user;
    private Upload upload;
    private String username = "", gender = "", mobile = "", dob = "", bid = "", bio = "", hobbies = "", carrier = "", image1 = "", image2 = "", image3 = "",
            profilePic = "", profilePic1 = "", profilePic2 = "", city = "", state = "", country = "",referralId = "";
    private Button bSwap;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayList<Country> countries = new ArrayList<>();
    private ArrayList<Country> states = new ArrayList<>();
    private ArrayList<Country> cities = new ArrayList<>();
    private int i = 0, currentYear = 0, dobYear = 0, age = 0;
    private Calendar today;

    private ClipboardManager myClipboard;
    private ClipData myClip;

    private ChatUtils helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        BLiveApplication.setCurrentActivity(this);
        initUI();
    }

    private void initUI() {
        helper = new ChatUtils(this);
        setTitle("ME");

        myClipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        bSwap = findViewById(R.id.bSwap);
        today = Calendar.getInstance();
        currentYear = today.get(Calendar.YEAR);

        image1 = SessionUser.getUser().getProfile_pic();
        image2 = SessionUser.getUser().getProfile_pic1();
        image3 = SessionUser.getUser().getProfile_pic2();

        arrayList.add(image1);
        arrayList.add(image2);
        arrayList.add(image3);

        if (image1 != null && !image1.isEmpty())
            Picasso.get().load(image1).fit().centerCrop().into(ivImage1);

        if (image2 != null && !image2.isEmpty())
            Picasso.get().load(image2).fit().centerCrop().into(ivImage2);

        if (image3 != null && !image3.isEmpty())
            Picasso.get().load(image3).fit().centerCrop().into(ivImage3);

        username = SessionUser.getUser().getName();
        mobile = SessionUser.getUser().getMobile();
        bid = SessionUser.getUser().getReference_user_id();
        dob = SessionUser.getUser().getDate_of_birth();
        bio = SessionUser.getUser().getAbout_me();
        gender = SessionUser.getUser().getGender();
        carrier = SessionUser.getUser().getCarrier();
        hobbies = SessionUser.getUser().getHobbies();
        country = SessionUser.getUser().getCountry();
        state = SessionUser.getUser().getState();
        city = SessionUser.getUser().getCity();
        referralId = SessionUser.getUser().getUsername();

        spCountry.setPrompt("Select Country");
        spState.setPrompt("Select State");
        spCity.setPrompt("Select City");

        if (!SessionUser.getUser().getLogin_domain().equalsIgnoreCase("mobile"))
            llMobile.setVisibility(View.GONE);

        if (SessionUser.getUser().getIs_the_user_changed_his_blive_ID().equalsIgnoreCase("yes"))
            etBid.setEnabled(false);

        etName.setText(username);
        etMobile.setText(mobile);
        etGender.setText(gender);
        etBid.setText(bid);
        etBio.setText(bio);
        etRefferal.setText(referralId);

        ivCopy.setOnClickListener(v -> {
            String tvcopy = etRefferal.getText().toString();
            myClip = ClipData.newPlainText("text",tvcopy);
            myClipboard.setPrimaryClip(myClip);
            showToast("Referral Code Copied!");
        });

        if (!dob.equalsIgnoreCase("0")) {
            if (!dob.isEmpty()) {
                etDob.setText(TimeUtils.getDateString(SessionUser.getUser().getDate_of_birth()));
                dobYear = Integer.valueOf(TimeUtils.getYearString(SessionUser.getUser().getDate_of_birth()));
                age = currentYear - dobYear;
                etDob.setTextColor(getResources().getColor(R.color.black));
            }
        } else {
            etDob.setText(R.string.not_mentioned);
            etDob.setTextColor(getResources().getColor(R.color.red));
        }

        spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "country onItemSelected: " + position);
                country = parent.getSelectedItem().toString();
                String countryCode = "";
                for (int i = 0; i < countries.size(); i++) {
                    if (country.equalsIgnoreCase(countries.get(i).getName()))
                        countryCode = countries.get(i).getCode();
                }

                if (!countryCode.isEmpty())
                    getStates(countryCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e(TAG, "country onNothingSelected: ");
                country = "";
            }
        });

        spState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "state onItemSelected: " + position);
                state = parent.getSelectedItem().toString();
                String stateCode = "";
                for (int i = 0; i < states.size(); i++) {
                    if (state.equalsIgnoreCase(states.get(i).getName()))
                        stateCode = states.get(i).getCode();
                }
                if (!stateCode.isEmpty())
                    getCities(stateCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                state = SessionUser.getUser().getState();
            }
        });

        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                city = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                city = SessionUser.getUser().getCity();
            }
        });

        bSwap.setOnClickListener(new View.OnClickListener() {
            boolean isAnimating;

            @Override
            public void onClick(View v) {
                if (isAnimating)
                    return;
                isAnimating = true;

                View v1 = findViewById(R.id.ll_1);
                View v2 = findViewById(R.id.ll_2);
                View v3 = findViewById(R.id.ll_3);

                float x1, y1, x2, y2, x3, y3;
                x1 = getRelativeX(v1);//Use v1.getX() if v1 & v2 have same parent
                y1 = getRelativeY(v1);//Use v1.getY() if v1 & v2 have same parent
                x2 = getRelativeX(v2);//Use v2.getX() if v1 & v2 have same parent
                y2 = getRelativeY(v2);//Use v2.getY() if v1 & v2 have same parent
                x3 = getRelativeX(v3);
                y3 = getRelativeY(v3);

                float x_displacement = (x3 - x1);
                float y_displacement = (y3 - y1);

                float a_displacement = (x1 - x2);
                float b_displacement = (y1 - y2);

                float c_displacement = (x2 - x3);
                float d_displacement = (y2 - y3);

                v1.animate().xBy(x_displacement).yBy(y_displacement);
                v2.animate().xBy(a_displacement).yBy(b_displacement);
                v3.animate().xBy(c_displacement).yBy(d_displacement);

                long anim_duration = v1.animate().getDuration();

                new CountDownTimer(anim_duration + 10, anim_duration + 10) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        isAnimating = false;
                    }
                }.start();

                i = i + 1;
            }

            //returns x-pos relative to root layout
            private float getRelativeX(View myView) {
                if (myView.getParent() == myView.getRootView())
                    return myView.getX();
                else
                    return myView.getX() + getRelativeX((View) myView.getParent());
            }

            //returns y-pos relative to root layout
            private float getRelativeY(View myView) {
                if (myView.getParent() == myView.getRootView())
                    return myView.getY();
                else
                    return myView.getY() + getRelativeY((View) myView.getParent());
            }
        });

        if (utils.isNetworkAvailable()) {
            utils.showProgress();
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<DataListResponse> call = apiClient.getCountries();
            call.enqueue(new retrofit2.Callback<DataListResponse>() {
                @Override
                public void onResponse(@NonNull Call<DataListResponse> call, @NonNull Response<DataListResponse> response) {
                    utils.hideProgress();
                    DataListResponse dataListResponse = response.body();
                    if (dataListResponse != null) {
                        if (dataListResponse.getStatus().equalsIgnoreCase("success")) {
                            setCountries(dataListResponse.getList());
                        } else {
                            showToast(dataListResponse.getMessage());
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<DataListResponse> call, @NonNull Throwable t) {
                    utils.hideProgress();
                    showToast(t.getMessage());
                }
            });
        }
    }

    private void setCountries(ArrayList<Country> mCountries) {
        countries.clear();
        countries = mCountries;
        List<String> list = new ArrayList<>();
        for (int i = 0; i < countries.size(); i++) {
            list.add(countries.get(i).getName());
        }

        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCountry.setAdapter(countryAdapter);

        if (!country.contains("Not Mentioned")) {
            int position = countryAdapter.getPosition(country);
            spCountry.setSelection(position, true);
        }
    }

    private void getStates(String countryCode) {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<DataListResponse> call = apiClient.getStates(countryCode);
            call.enqueue(new retrofit2.Callback<DataListResponse>() {
                @Override
                public void onResponse(@NonNull Call<DataListResponse> call, @NonNull Response<DataListResponse> response) {
                    DataListResponse dataListResponse = response.body();
                    if (dataListResponse != null) {
                        if (dataListResponse.getStatus().equalsIgnoreCase("success")) {
                            setStates(dataListResponse.getList());
                        } else {
                            showToast(dataListResponse.getMessage());
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<DataListResponse> call, @NonNull Throwable t) {
                    showToast(t.getMessage());
                }
            });
        }
    }

    private void setStates(ArrayList<Country> mStates) {
        states.clear();
        states = mStates;
        List<String> statesList = new ArrayList<>();
        for (int i = 0; i < states.size(); i++) {
            statesList.add(states.get(i).getName());
        }

        ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, statesList);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spState.setAdapter(stateAdapter);

        if (!state.contains("Not Mentioned")) {
            int position = stateAdapter.getPosition(state);
            spState.setSelection(position, true);
        }
    }

    private void getCities(String stateCode) {
        if (utils.isNetworkAvailable()) {
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<DataListResponse> call = apiClient.getCities(stateCode);
            call.enqueue(new retrofit2.Callback<DataListResponse>() {
                @Override
                public void onResponse(@NonNull Call<DataListResponse> call, @NonNull Response<DataListResponse> response) {
                    DataListResponse dataListResponse = response.body();
                    if (dataListResponse != null) {
                        if (dataListResponse.getStatus().equalsIgnoreCase("success")) {
                            setCities(dataListResponse.getList());
                        } else {
                            showToast(dataListResponse.getMessage());
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<DataListResponse> call, @NonNull Throwable t) {
                    showToast(t.getMessage());
                }
            });
        }
    }

    private void setCities(ArrayList<Country> mCities) {
        cities.clear();
        cities = mCities;
        List<String> cityList = new ArrayList<>();
        for (int i = 0; i < cities.size(); i++) {
            cityList.add(cities.get(i).getName());
        }
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cityList);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCity.setAdapter(cityAdapter);

        if (!city.contains("Not Mentioned")) {
            int position = cityAdapter.getPosition(city);
            spCity.setSelection(position, true);
        }
    }

    @OnClick({R.id.ll_gender, R.id.et_gender})
    public void onClickGender() {
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View view = layoutInflater.inflate(R.layout.pop_up_gender, null);
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(ActivityEditProfile.this);
        alertDialogBuilder.setView(view);
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        RadioButton rbMale = view.findViewById(R.id.rb_male);
        RadioButton rbFemale = view.findViewById(R.id.rb_female);
        RadioButton rbPrivate = view.findViewById(R.id.rb_secret);

        if (gender != null) {
            if (gender.equalsIgnoreCase("Male"))
                rbMale.setChecked(true);
            else if (gender.equalsIgnoreCase("Female"))
                rbFemale.setChecked(true);
            else if (gender.equalsIgnoreCase("Private"))
                rbPrivate.setChecked(true);
        }

        rbMale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                rbFemale.setChecked(false);
                rbPrivate.setChecked(false);
                gender = "Male";
                etGender.setText(gender);
                alertDialog.dismiss();
            }
        });

        rbFemale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                rbMale.setChecked(false);
                rbPrivate.setChecked(false);
                gender = "Female";
                etGender.setText(gender);
                alertDialog.dismiss();
            }
        });

        rbPrivate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                rbMale.setChecked(false);
                rbFemale.setChecked(false);
                gender = "Private";
                etGender.setText(gender);
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @OnClick(R.id.et_dob)
    public void onClickDob() {

        DatePickerFragment dialogFragment = new DatePickerFragment();
        dialogFragment.show(getSupportFragmentManager(), "DatePickerFragment");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        dialogFragment.setListener((view, year, month, day) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            dobYear = year;
            age = currentYear - dobYear;
            SimpleDateFormat format = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
            etDob.setText(format.format(calendar.getTime()));
            dob = sdf.format(calendar.getTime());
            etDob.setTextColor(getResources().getColor(R.color.black));
        });
    }

    @OnClick(R.id.cv_image_1)
    public void onClickImage1() {
        isFirstImage = true;
        isSecondImage = false;
        isThirdImage = false;
        callCameraOrGallery();
    }

    @OnClick(R.id.cv_image_2)
    public void onClickImage2() {
        isFirstImage = false;
        isSecondImage = true;
        isThirdImage = false;
        callCameraOrGallery();
    }

    @OnClick(R.id.cv_image_3)
    public void onClickImage3() {
        isFirstImage = false;
        isSecondImage = false;
        isThirdImage = true;
        callCameraOrGallery();
    }

    private void callCameraOrGallery() {
        LayoutInflater factory = LayoutInflater.from(getApplicationContext( ));
        final View view = factory.inflate(R.layout.alert_media, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(ActivityEditProfile.this).create();
        alertDialog.setView(view);

        LinearLayout llGallery = view.findViewById(R.id.ll_gallery);
        LinearLayout llPhoto = view.findViewById(R.id.ll_photo);
        LinearLayout llProfilePhoto = view.findViewById(R.id.ll_profile_photo);

        llGallery.setOnClickListener(view1 -> {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickPhoto.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(pickPhoto, SELECT_PHOTO);
            alertDialog.dismiss();
        });

        llPhoto.setOnClickListener(view12 -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    photoURI = FileProvider.getUriForFile(this,
                            "com.blive.provider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, TAKE_PHOTO);
                }
            }
            alertDialog.dismiss();
        });

        llProfilePhoto.setOnClickListener(v -> {
            alertDialog.dismiss();
            LayoutInflater factory1 = LayoutInflater.from(getApplicationContext());
            final View view1 = factory1.inflate(R.layout.view_profile_pic, null);
            final AlertDialog alertDialog1 = new AlertDialog.Builder(ActivityEditProfile.this).create();
            alertDialog1.setView(view1);

            ViewPager viewPager = view1.findViewById(R.id.viewPager);

            AdapterProfilePic adapterProfilePic = new AdapterProfilePic(getApplicationContext(), arrayList);
            viewPager.setAdapter(adapterProfilePic);

            alertDialog1.setCancelable(true);
            alertDialog1.setCanceledOnTouchOutside(true);
            alertDialog1.show();
        });

        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @OnClick(R.id.rl_save)
    public void onClickSave() {
        if (validation())
            saveChanges();
    }

    public void saveChanges() {
        mobile = etMobile.getText().toString();
        bio = etBio.getText().toString();

        try {
            image1 = URLDecoder.decode(SessionUser.getUser().getProfile_pic(), "UTF-8");
            image2 = URLDecoder.decode(SessionUser.getUser().getProfile_pic1(), "UTF-8");
            image3 = URLDecoder.decode(SessionUser.getUser().getProfile_pic2(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (i % 3 == 0) {
            profilePic = image1;
            profilePic1 = image2;
            profilePic2 = image3;
        } else if (i % 2 == 0) {
            profilePic = image3;
            profilePic1 = image1;
            profilePic2 = image2;
        } else {
            profilePic = image2;
            profilePic1 = image1;
            profilePic2 = image3;
        }

        if (utils.isNetworkAvailable()) {
            utils.showProgress();
            final ApiInterface apiClient = ApiClientBase.getClient().create(ApiInterface.class);
            Call<ProfileResponse> call = apiClient.updateProfile(SessionUser.getUser().getUser_id(), username, mobile, dob, gender, city, state, country, bio, hobbies, carrier, profilePic, profilePic1, profilePic2, bid);
            call.enqueue(new retrofit2.Callback<ProfileResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                    utils.hideProgress();
                    ProfileResponse profileResponse = response.body();
                    if (response.code() == 200) {
                        if (profileResponse != null) {
                            if (profileResponse.getStatus().equalsIgnoreCase("success")) {
                                SessionUser.saveUser(profileResponse.getData().getUser());
                                createUser(profileResponse.getData().getUser());
                                showToast("Profile Updated successfully!");
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
            //Log.e(TAG, "saveChanges: Edit Profile " + username + mobile + dob + gender + bid + bio + hobbies + carrier + profilePic + profilePic1 + profilePic2 + city + state + country);
        }
    }

    private void createUser(final User newUser) {
        ChatUser chatUser = new ChatUser(newUser.getUser_id(), newUser.getUser_id(), (newUser.getProfile_pic() != null ? newUser.getProfile_pic() : ""),
                newUser.getName(), System.currentTimeMillis());
        BLiveApplication.getUserRef().child(newUser.getUser_id()).setValue(chatUser).addOnSuccessListener(aVoid -> {
            helper.setLoggedInUser(chatUser);
            utils.hideProgress();


        }).addOnFailureListener(e ->
                Toast.makeText(ActivityEditProfile.this, "Something went wrong, unable to create user.", Toast.LENGTH_LONG).show());
    }

    private boolean validation() {
        username = etName.getText().toString();
        bid = etBid.getText().toString();
        bio = etBio.getText().toString();

        if (username.length() < 3)
            showToast("Name should be minimum 3 characters !");
        else if (bid.length() < 4)
            showToast("BLive Id should be minimum 4 characters !");
        else if (bid.length() > 16)
            showToast("BLive Id should be maximum 16 characters !");
        else if (age < 18)
            showToast("Age must be least 18!");
        else
            return true;

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    imageUri = imageReturnedIntent.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(imageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    try{
                        tempUri = getImageUri(getApplicationContext(), selectedImage);
                    } catch (Exception e) {
                        Log.e("ErrorOccurred", e.getMessage());
                        Crashlytics.logException(e);
                    }
                    CropImage.activity(tempUri)
                            .start(this);
                }
                break;

            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //Bitmap photo = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    //Uri tempUri = getUri(getApplicationContext(), photo);
                    CropImage.activity(photoURI)
                            .start(this);
                }
                break;

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(imageReturnedIntent);
                if (resultCode == RESULT_OK) {
                    try{
                        resultUri = result.getUri();
                    } catch (Exception e) {
                        Log.e("ErrorOccurred", e.getMessage());
                        Crashlytics.logException(e);
                    }
                    String imagePath = resultUri.getPath();

                    user = new User();
                    if (isFirstImage) {
                        user.setProfile_pic(imagePath);
                        user.setProfile_pic1("");
                        user.setProfile_pic2("");
                    } else if (isSecondImage) {
                        user.setProfile_pic1(imagePath);
                        user.setProfile_pic("");
                        user.setProfile_pic2("");
                    } else if (isThirdImage) {
                        user.setProfile_pic("");
                        user.setProfile_pic1("");
                        user.setProfile_pic2(imagePath);
                    }

                    if (utils.isNetworkAvailable()) {
                        utils.showProgress();
                        upload = new Upload();
                        upload.execute();
                    }
                }
                break;

            case CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE:
                CropImage.ActivityResult results = CropImage.getActivityResult(imageReturnedIntent);
                Exception error = results.getError();
                Log.e(TAG, "onActivityResult: " + error);
                break;

        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "image.png", null);
        return Uri.parse(path);
    }

    public Uri getUri(Context inContext, Bitmap inImage){
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "image.png", null);
        return Uri.parse(path);
    }

    @Override
    public void onUpdateSuccess(User user) {

        if (!upload.isCancelled())
            upload.cancel(true);

        SessionUser.saveUser(user);

        mActivity.runOnUiThread(() -> {
            utils.hideProgress();
            try {
                if (isFirstImage) {
                    String image = URLDecoder.decode(user.getProfile_pic(), "UTF-8");
                    Picasso.get().load(image).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE)
                            .placeholder(R.drawable.cam).into(ivImage1);
                    isFirstImage = false;
                } else if (isSecondImage) {
                    String image = URLDecoder.decode(user.getProfile_pic1(), "UTF-8");
                    Picasso.get().load(image).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE)
                            .placeholder(R.drawable.cam).into(ivImage2);
                    isSecondImage = false;
                } else if (isThirdImage) {
                    String image = URLDecoder.decode(user.getProfile_pic2(), "UTF-8");
                    Picasso.get().load(image).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE)
                            .placeholder(R.drawable.cam).into(ivImage3);
                    isThirdImage = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onUpdateFailure(String mes) {
        mActivity.runOnUiThread(() -> {
            utils.hideProgress();
            showToast(mes);
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class Upload extends AsyncTask<Void, Integer, Void> {

        APITask_UpdateImage apiTask_updateImage = callAPI();

        @Override
        protected Void doInBackground(Void... params) {
            apiTask_updateImage.update(user);
            return null;
        }
    }

    private APITask_UpdateImage callAPI() {
        return new APITask_UpdateImage(this, getApplicationContext());
    }

    @OnClick(R.id.rl_back)
    public void onClickBack() {
        if (!dob.equalsIgnoreCase("0")) {
            if (isChanged()) {
                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            dialog.dismiss();
                            if (validation())
                                saveChanges();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                            if (SessionUser.getUser().getDate_of_birth().equalsIgnoreCase("0"))
                                showToast("Please Save Your Date Of Birth!");
                            else
                                finish();
                            break;
                    }
                };

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setMessage("Do you want to save the changes ?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            } else
                super.onBackPressed();
        } else
            showToast("Please Select Your Date Of Birth !");

    }

    @Override
    public void onBackPressed() {
        if (!dob.equalsIgnoreCase("0")) {
            if (isChanged()) {
                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            dialog.dismiss();
                            if (validation())
                                saveChanges();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                            if (SessionUser.getUser().getDate_of_birth().equalsIgnoreCase("0"))
                                showToast("Please Save Your Date Of Birth!");
                            else
                                finish();
                            break;
                    }
                };

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setMessage("Do you want to save the changes ?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            } else
                super.onBackPressed();
        } else
            showToast("Please Select Your Date Of Birth !");
    }

    private boolean isChanged() {
        if (!SessionUser.getUser().getName().equalsIgnoreCase(username))
            return true;
        else if (!SessionUser.getUser().getGender().equalsIgnoreCase(gender))
            return true;
        else if (!SessionUser.getUser().getMobile().equalsIgnoreCase(mobile))
            return true;
        else if (!SessionUser.getUser().getDate_of_birth().equalsIgnoreCase(dob))
            return true;
        else if (i % 3 == 0) {
            return false;
        } else if (i % 2 == 0) {
            return true;
        } else
            return true;
    }

}