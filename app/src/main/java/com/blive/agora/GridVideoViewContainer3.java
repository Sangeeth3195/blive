package com.blive.agora;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.blive.R;
import com.blive.model.Audience;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class GridVideoViewContainer3 extends RelativeLayout {

    private static final String TAG = "GridVideoViewContainer3";
    private ArrayList<VideoStatusData> mUsers = new ArrayList<>();
    FrameLayout frameLayout,frameLayout1,frameLayout2;
    private ArrayList<Audience> guests;
    private int mLocalUid;

    public GridVideoViewContainer3(Context context) {
        super(context);
    }

    public GridVideoViewContainer3(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GridVideoViewContainer3(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @SuppressLint("ResourceType")
    public void initViewContainer(Context context, int broadcasterUid, HashMap<Integer, SurfaceView> uids, boolean newlyCreated,
                                  boolean isBroadcaster, ArrayList<Audience> guests) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_stream3, this, true);

         frameLayout = view.findViewById(R.id.broadcaster);
         frameLayout1 = view.findViewById(R.id.guest1);
         frameLayout2 = view.findViewById(R.id.guest2);

        int count = uids.size();
        Log.e("", "initViewContainer: isBroadcaster "+isBroadcaster);
        Log.e(TAG, "initViewContainer: broadcasterUid "+broadcasterUid);
        for (Integer key : uids.keySet()) {
            System.out.println(key);
        }
        this.guests = guests;
        if (!newlyCreated) {
            mLocalUid = broadcasterUid;
        }
        Log.e(TAG, "initViewContainer: "+mLocalUid);
        for (HashMap.Entry<Integer, SurfaceView> entry : uids.entrySet()) {
            if (entry.getKey() == 0 || entry.getKey() == mLocalUid) {
                boolean found = false;
                for (VideoStatusData status : mUsers) {
                    if ((status.mUid == entry.getKey() && status.mUid == 0) || status.mUid == mLocalUid) { // first time
                        status.mUid = mLocalUid;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                        Log.e(TAG, "initViewContainer: !found"+entry.getKey());
                        if(entry.getKey() == mLocalUid){
                            mUsers.add(0, new VideoStatusData(mLocalUid, entry.getValue(), VideoStatusData.DEFAULT_STATUS, VideoStatusData.DEFAULT_VOLUME));
                        }
                }
            } else {
                boolean found = false;
                for (VideoStatusData status : mUsers) {
                    if (status.mUid == entry.getKey()) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    mUsers.add(new VideoStatusData(entry.getKey(), entry.getValue(), VideoStatusData.DEFAULT_STATUS, VideoStatusData.DEFAULT_VOLUME));
                }
            }
        }

        Iterator<VideoStatusData> it = mUsers.iterator();
        while (it.hasNext()) {
            VideoStatusData status = it.next();

            if (uids.get(status.mUid) == null) {
                Log.w(TAG, "after_changed remove not exited members " + (status.mUid & 0xFFFFFFFFL) + " " + status.mView);
                it.remove();
            }
        }

        if (count == 1) {
            frameLayout.setBackground(null);
            frameLayout1.setBackgroundResource(R.drawable.group_add);
            frameLayout2.setBackgroundResource(R.drawable.group_add);

            final VideoStatusData user = mUsers.get(0);
            Log.e(TAG, "initViewContainer: 1 "+user.mUid);
            Log.e(TAG, "initViewContainer: 1 "+user.mStatus);
            if(user.mUid != broadcasterUid){
                SurfaceView target = user.mView;
                removeAllViews();

                frameLayout.removeAllViews();
                frameLayout1.removeAllViews();
                frameLayout1.addView(target);
            }else {
                SurfaceView target = user.mView;
                removeAllViews();

                frameLayout.removeAllViews();
                frameLayout1.removeAllViews();
                frameLayout.addView(target);
            }

        } else if (count == 2) {
            frameLayout.setBackground(null);
            frameLayout1.setBackground(null);
            frameLayout2.setBackgroundResource(R.drawable.group_add);

            removeAllViews();
            final VideoStatusData user = mUsers.get(0);
            final VideoStatusData user1 = mUsers.get(1);

            SurfaceView target = user.mView;
            SurfaceView target1 = user1.mView;

            frameLayout1.addView(target1);
            frameLayout.addView(target);

        } else if (count == 3) {
            frameLayout.setBackground(null);
            frameLayout1.setBackground(null);
            frameLayout2.setBackground(null);

            final VideoStatusData user = mUsers.get(0);
            final VideoStatusData user1 = mUsers.get(1);
            final VideoStatusData user2 = mUsers.get(2);

            if(guests.get(0).getId() == user1.mUid){
                SurfaceView target = user.mView;
                SurfaceView target1 = user1.mView;
                SurfaceView target2 = user2.mView;
                removeAllViews();

                frameLayout1.addView(target1);
                frameLayout2.addView(target2);
                frameLayout.addView(target);
            }
            else {
                SurfaceView target = user.mView;
                SurfaceView target1 = user1.mView;
                SurfaceView target2 = user2.mView;
                removeAllViews();

                frameLayout1.addView(target2);
                frameLayout2.addView(target1);
                frameLayout.addView(target);
            }
        }
    }

    public final void removeAllViews() {
        frameLayout.removeAllViews();
        frameLayout1.removeAllViews();
        frameLayout2.removeAllViews();
    }

}