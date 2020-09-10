package com.blive.agora;

import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.blive.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class GridVideoViewContainersolo extends RelativeLayout {

    private ArrayList<VideoStatusData> mUsers = new ArrayList<>();
    private int mLocalUid;

    public GridVideoViewContainersolo(Context context) {
        super(context);
    }

    public GridVideoViewContainersolo(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GridVideoViewContainersolo(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initViewContainer(Context context, int broadcasterUid, HashMap<Integer, SurfaceView> uids, boolean newlyCreated, boolean isBroadcaster, boolean isGuest, boolean pknow) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.custom_stream, this, true);

        FrameLayout frameLayout = view.findViewById(R.id.broadcaster);

        int count = uids.size();
        //Log.e(TAG, "initViewContainer: uid1 " + broadcasterUid);
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        Log.e("", "initViewContainer: height " + height);
        Log.e("", "initViewContainer: width " + width);

        if (!newlyCreated) {
            mLocalUid = broadcasterUid;
        }

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
                    if (uids.size() == 2) {
                        if(entry.getKey() == mLocalUid){
                            mUsers.add(0, new VideoStatusData(mLocalUid, entry.getValue(), VideoStatusData.DEFAULT_STATUS, VideoStatusData.DEFAULT_VOLUME));
                        }else {
                            mUsers.add(1, new VideoStatusData(mLocalUid, entry.getValue(), VideoStatusData.DEFAULT_STATUS, VideoStatusData.DEFAULT_VOLUME));
                        }
                    } else
                        mUsers.add(0, new VideoStatusData(mLocalUid, entry.getValue(), VideoStatusData.DEFAULT_STATUS, VideoStatusData.DEFAULT_VOLUME));

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
                Log.w("", "after_changed remove not exited members " + (status.mUid & 0xFFFFFFFFL) + " " + status.mView);
                it.remove();
            }
        }

        if (count == 1) { // only local full view or or with one peer
            try {
                frameLayout.removeAllViews();
            } catch (Exception e) {
                Log.e("", "Exception: " + e);
            }

            final VideoStatusData user = mUsers.get(0);
            SurfaceView target = user.mView;

            frameLayout.addView(target);

        } else if (count == 2) {

            try {
                frameLayout.removeAllViews();
            } catch (Exception e) {
                Log.e("", "Exception: " + e);
            }

            final VideoStatusData user = mUsers.get(0);
            final VideoStatusData user1 = mUsers.get(1);

            SurfaceView target = user.mView;
            frameLayout.addView(target,0);

           /* if (height == 1280 && width == 720) {
                frameLayout1.addView(target1);
                frameLayout.addView(target);
            } else if (height == 1920 && width == 1080) {
                frameLayout.addView(target);
                frameLayout1.addView(target1);
            } else if (height == 1184 && width == 720) {
                frameLayout1.addView(target1);
                frameLayout.addView(target);
            } else if (width < 1000) {
                frameLayout.addView(target);
                frameLayout1.addView(target1);
            } else if (height > 2000 && width > 1000) {
                frameLayout.addView(target);
                frameLayout1.addView(target1);
            } else {
                frameLayout1.addView(target1);
                frameLayout.addView(target);
            }*/
        }else if (count==3){
            {

                try {
                    frameLayout.removeAllViews();
                } catch (Exception e) {
                    Log.e("", "Exception: " + e);
                }

                final VideoStatusData user = mUsers.get(0);
                final VideoStatusData user1 = mUsers.get(1);
                final VideoStatusData user2 = mUsers.get(2);

                SurfaceView target = user.mView;
                frameLayout.addView(target,0);

           /* if (height == 1280 && width == 720) {
                frameLayout1.addView(target1);
                frameLayout.addView(target);
            } else if (height == 1920 && width == 1080) {
                frameLayout.addView(target);
                frameLayout1.addView(target1);
            } else if (height == 1184 && width == 720) {
                frameLayout1.addView(target1);
                frameLayout.addView(target);
            } else if (width < 1000) {
                frameLayout.addView(target);
                frameLayout1.addView(target1);
            } else if (height > 2000 && width > 1000) {
                frameLayout.addView(target);
                frameLayout1.addView(target1);
            } else {
                frameLayout1.addView(target1);
                frameLayout.addView(target);
            }*/
            }
        }
    }
}