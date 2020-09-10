package com.blive.agora;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.blive.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class GridVideoViewKaraokeDual extends RelativeLayout {

    private static final String TAG = "GridVideoViewKaraokeDual";

    private ArrayList<VideoStatusData> mUsers = new ArrayList<>();
    private int mLocalUid;
    FrameLayout frameLayout,frameLayout1;

    public GridVideoViewKaraokeDual(Context context) {
        super(context);
    }

    public GridVideoViewKaraokeDual(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GridVideoViewKaraokeDual(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initViewContainer(Context context, int broadcasterUid, HashMap<Integer, SurfaceView> uids, boolean newlyCreated,
                                  boolean isBroadcaster) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_karokegroup, this, true);

         frameLayout = view.findViewById(R.id.broadcaster);
         frameLayout1 = view.findViewById(R.id.first_guest);

        int count = uids.size();

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
                it.remove();
            }
        }

        if (count == 1) {
            frameLayout.setBackground(null);
            frameLayout1.setBackgroundResource(R.drawable.group_add1);

            final VideoStatusData user = mUsers.get(0);
            SurfaceView target = user.mView;
            removeAllViews();

            frameLayout.removeAllViews();
            frameLayout1.removeAllViews();
            frameLayout.addView(target);
        } else if (count == 2) {
            frameLayout.setBackground(null);
            frameLayout1.setBackground(null);

            removeAllViews();
            final VideoStatusData user = mUsers.get(0);
            final VideoStatusData user1 = mUsers.get(1);

            SurfaceView target = user.mView;
            SurfaceView target1 = user1.mView;

            frameLayout1.addView(target1);
            frameLayout.addView(target);

        }
    }

    public final void removeAllViews() {
        frameLayout.removeAllViews();
        frameLayout1.removeAllViews();
    }
}
