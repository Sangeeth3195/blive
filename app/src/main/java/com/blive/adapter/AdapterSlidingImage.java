package com.blive.adapter;

import android.content.Context;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blive.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.facebook.login.widget.ProfilePictureView.TAG;

public class AdapterSlidingImage extends PagerAdapter {

    private ArrayList<String> urls;
    private LayoutInflater inflater;
    private Context context;

    public AdapterSlidingImage(Context context,ArrayList<String> urls) {
        this.context = context;
        this.urls = urls;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.sliding_images_layout, view, false);

        assert imageLayout != null;
        final ImageView imageView = imageLayout.findViewById(R.id.sliding_image);

        try{
//            Glide.with(context).load(urls.get(position)).into(imageView);
            if(!urls.get(position).isEmpty())
                Picasso.get().load(urls.get(position)).centerCrop().fit().into(imageView);
            else
                Picasso.get().load(R.drawable.user).centerCrop().fit().into(imageView);
        }catch (Exception e){
            Log.e(TAG, "instantiateItem: "+e);
        }

        view.addView(imageLayout, 0);

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

}