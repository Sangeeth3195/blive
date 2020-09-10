package com.blive.chat.chatview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blive.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class ImageViewerActivity extends AppCompatActivity {

    private static final String IMAGE_URL = ImageViewerActivity.class.getPackage().getName() + ".image_url";

    public static Intent newInstance(Context context, String imageUrl) {
        Intent intent = new Intent(context, ImageViewerActivity.class);
        intent.putExtra(IMAGE_URL, imageUrl);
        return intent;
    }

    PhotoView photoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        photoView = findViewById(R.id.photo_view);

        String imageUrl = getIntent().getStringExtra(IMAGE_URL);
        if (!TextUtils.isEmpty(imageUrl))
            Picasso.get()
                    .load(imageUrl)
                    .tag(this)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(photoView);
    }
}
