package com.blive.agora;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.blive.R;
import com.blive.model.Audience;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class GridVideoViewContainer9 extends RelativeLayout {

    private static final String TAG = "GridVideoViewContainer9";
    private ArrayList<VideoStatusData> mUsers = new ArrayList<>();
    private ArrayList<Audience> guests;
    FrameLayout frameLayout, frameLayout1, frameLayout2, frameLayout3, frameLayout4,
            frameLayout5,frameLayout6,frameLayout7,frameLayout8;
    private int mLocalUid;
    ArrayList<SurfaceView> surfaceViews=new ArrayList<>();

    public GridVideoViewContainer9(Context context) {
        super(context);
    }

    public GridVideoViewContainer9(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GridVideoViewContainer9(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initViewContainer(Context context, int broadcasterUid, HashMap<Integer, SurfaceView> uids, boolean newlyCreated,
                                  boolean isBroadcaster, ArrayList<Audience> guests) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_stream9, this, true);

        frameLayout = view.findViewById(R.id.broadcaster_group9);
        frameLayout1 = view.findViewById(R.id.first_guestgroup9);
        frameLayout2 = view.findViewById(R.id.second_guestgroup9);
        frameLayout3 = view.findViewById(R.id.third_guestgroup9);
        frameLayout4 = view.findViewById(R.id.fourth_guestgroup9);
        frameLayout5 = view.findViewById(R.id.fifth_guestgroup9);
        frameLayout6 = view.findViewById(R.id.sixth_guestgroup9);
        frameLayout7 = view.findViewById(R.id.seventh_guestgroup9);
        frameLayout8 = view.findViewById(R.id.eighth_guestgroup9);
        surfaceViews.clear();

        int count = uids.size();

        for (Integer key : uids.keySet()) {
            System.out.println(key);
        }
        this.guests = guests;
        if (!newlyCreated) {
            mLocalUid = broadcasterUid;
        }
        Log.e(TAG, "initViewContainer: " + mLocalUid);
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
                    Log.e(TAG, "initViewContainer: !found" + entry.getKey());
                    if (entry.getKey() == mLocalUid) {
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
            frameLayout3.setBackgroundResource(R.drawable.group_add);
            frameLayout4.setBackgroundResource(R.drawable.group_add);
            frameLayout5.setBackgroundResource(R.drawable.group_add);
            frameLayout6.setBackgroundResource(R.drawable.group_add);
            frameLayout7.setBackgroundResource(R.drawable.group_add);
            frameLayout8.setBackgroundResource(R.drawable.group_add);

            final VideoStatusData user = mUsers.get(0);

            if (user.mUid != broadcasterUid) {            // Guest and audiance
                SurfaceView target = user.mView;

                removeAllViews();

                frameLayout.addView(target);
            } else {                                       // Broadcaster
                SurfaceView target = user.mView;
                removeAllViews();
                frameLayout.addView(target);
            }
        } else if (count == 2) {
            frameLayout.setBackground(null);
            frameLayout1.setBackground(null);
            frameLayout2.setBackgroundResource(R.drawable.group_add);
            frameLayout3.setBackgroundResource(R.drawable.group_add);
            frameLayout4.setBackgroundResource(R.drawable.group_add);
            frameLayout5.setBackgroundResource(R.drawable.group_add);
            frameLayout6.setBackgroundResource(R.drawable.group_add);
            frameLayout7.setBackgroundResource(R.drawable.group_add);
            frameLayout8.setBackgroundResource(R.drawable.group_add);

            removeAllViews();

            final VideoStatusData user = mUsers.get(0);
            final VideoStatusData user1 = mUsers.get(1);

            if (user1.mUid != broadcasterUid) {                //Guest and audiance

                SurfaceView target = user.mView;
                SurfaceView target1 = user1.mView;

                frameLayout1.addView(target1);
                frameLayout.addView(target);
            } else {                                             //Broadcaster
                SurfaceView target = user.mView;
                SurfaceView target1 = user1.mView;

                frameLayout.addView(target1);
                frameLayout1.addView(target);
            }
        } else if (count == 3) {
            frameLayout.setBackground(null);
            frameLayout1.setBackground(null);
            frameLayout2.setBackground(null);
            frameLayout3.setBackgroundResource(R.drawable.group_add);
            frameLayout4.setBackgroundResource(R.drawable.group_add);
            frameLayout5.setBackgroundResource(R.drawable.group_add);
            frameLayout6.setBackgroundResource(R.drawable.group_add);
            frameLayout7.setBackgroundResource(R.drawable.group_add);
            frameLayout8.setBackgroundResource(R.drawable.group_add);

            removeAllViews();

            final VideoStatusData user = mUsers.get(0);
            final VideoStatusData user1 = mUsers.get(1);
            final VideoStatusData user2 = mUsers.get(2);

            Log.d(TAG, "initViewContainer: " + guests.size());
            Log.d(TAG, "initViewContainer: " + mUsers.size());
            for (int i = 0; i < mUsers.size(); i++) {
                if (broadcasterUid == mUsers.get(i).mUid) {
                    frameLayout.addView(mUsers.get(i).mView);
                }
            }

            for (int i = 0; i < guests.size(); i++) {
                for (int j = 0; j < mUsers.size(); j++) {
                    Log.d(TAG, "initViewContainer: " + guests.get(i).getId());
                    Log.d(TAG, "initViewContainer: " + mUsers.get(j).mUid);
                    if ((guests.get(i).getId()) == mUsers.get(j).mUid) {
                        Log.e(TAG, "initViewContainer: " + mUsers.get(j).mUid + j);
                        Log.e(TAG, "initViewContainer: " + guests.get(i).getUid() + j);
                        surfaceViews.add(mUsers.get(j).mView);
                    } else {
                        Log.d(TAG, "initViewContainer: ");
                    }
                }
            }
            Log.d(TAG, "initViewContainer: " + surfaceViews.size());

            frameLayout1.addView(surfaceViews.get(0));
            frameLayout2.addView(surfaceViews.get(1));

        } else if (count == 4) {
            frameLayout.setBackground(null);
            frameLayout1.setBackground(null);
            frameLayout2.setBackground(null);
            frameLayout3.setBackground(null);
            frameLayout4.setBackgroundResource(R.drawable.group_add);
            frameLayout5.setBackgroundResource(R.drawable.group_add);
            frameLayout6.setBackgroundResource(R.drawable.group_add);
            frameLayout7.setBackgroundResource(R.drawable.group_add);
            frameLayout8.setBackgroundResource(R.drawable.group_add);

            removeAllViews();

            final VideoStatusData user = mUsers.get(0);
            final VideoStatusData user1 = mUsers.get(1);
            final VideoStatusData user2 = mUsers.get(2);
            final VideoStatusData user3 = mUsers.get(3);

            for (int i = 0; i < guests.size(); i++) {
                for (int j = 0; j < mUsers.size(); j++) {
                    Log.d(TAG, "initViewContainer: " + guests.get(i).getId());
                    Log.d(TAG, "initViewContainer: " + mUsers.get(j).mUid);
                    if ((guests.get(i).getId()) == mUsers.get(j).mUid) {
                        Log.e(TAG, "initViewContainer: " + mUsers.get(j).mUid + j);
                        Log.e(TAG, "initViewContainer: " + guests.get(i).getUid() + j);
                        surfaceViews.add(mUsers.get(j).mView);
                    } else {
                        Log.d(TAG, "initViewContainer: ");
                    }
                }
            }
            Log.d(TAG, "initViewContainer: " + surfaceViews.size());

            removeAllViews();
            frameLayout1.addView(surfaceViews.get(0));
            frameLayout2.addView(surfaceViews.get(1));
            frameLayout3.addView(surfaceViews.get(2));

            Log.d(TAG, "initViewContainer: " + guests.size());
            Log.d(TAG, "initViewContainer: " + mUsers.size());
            for (int i = 0; i < mUsers.size(); i++) {
                if (broadcasterUid == mUsers.get(i).mUid) {
                    frameLayout.addView(mUsers.get(i).mView);
                }
            }
        } else if (count == 5) {
            frameLayout.setBackground(null);
            frameLayout1.setBackground(null);
            frameLayout2.setBackground(null);
            frameLayout3.setBackground(null);
            frameLayout4.setBackground(null);
            frameLayout5.setBackgroundResource(R.drawable.group_add);
            frameLayout6.setBackgroundResource(R.drawable.group_add);
            frameLayout7.setBackgroundResource(R.drawable.group_add);
            frameLayout8.setBackgroundResource(R.drawable.group_add);

            removeAllViews();

            final VideoStatusData user = mUsers.get(0);
            final VideoStatusData user1 = mUsers.get(1);
            final VideoStatusData user2 = mUsers.get(2);
            final VideoStatusData user3 = mUsers.get(3);
            final VideoStatusData user4 = mUsers.get(4);

            for (int i = 0; i < guests.size(); i++) {
                for (int j = 0; j < mUsers.size(); j++) {
                    Log.d(TAG, "initViewContainer: " + guests.get(i).getId());
                    Log.d(TAG, "initViewContainer: " + mUsers.get(j).mUid);
                    if ((guests.get(i).getId()) == mUsers.get(j).mUid) {
                        Log.e(TAG, "initViewContainer: " + mUsers.get(j).mUid + j);
                        Log.e(TAG, "initViewContainer: " + guests.get(i).getUid() + j);
                        surfaceViews.add(mUsers.get(j).mView);
                    } else {
                        Log.d(TAG, "initViewContainer: ");
                    }
                }
            }
            removeAllViews();
            frameLayout1.addView(surfaceViews.get(0));
            frameLayout2.addView(surfaceViews.get(1));
            frameLayout3.addView(surfaceViews.get(2));
            frameLayout4.addView(surfaceViews.get(3));

            Log.d(TAG, "initViewContainer: " + guests.size());
            Log.d(TAG, "initViewContainer: " + mUsers.size());
            for (int i = 0; i < mUsers.size(); i++) {
                if (broadcasterUid == mUsers.get(i).mUid) {
                    frameLayout.addView(mUsers.get(i).mView);
                }
            }
        } else if (count == 6) {

            frameLayout.setBackground(null);
            frameLayout1.setBackground(null);
            frameLayout2.setBackground(null);
            frameLayout3.setBackground(null);
            frameLayout4.setBackground(null);
            frameLayout5.setBackground(null);
            frameLayout6.setBackgroundResource(R.drawable.group_add);
            frameLayout7.setBackgroundResource(R.drawable.group_add);
            frameLayout8.setBackgroundResource(R.drawable.group_add);

            removeAllViews();

            final VideoStatusData user = mUsers.get(0);
            final VideoStatusData user1 = mUsers.get(1);
            final VideoStatusData user2 = mUsers.get(2);
            final VideoStatusData user3 = mUsers.get(3);
            final VideoStatusData user4 = mUsers.get(4);
            final VideoStatusData user5 = mUsers.get(5);

            for (int i = 0; i < guests.size(); i++) {
                for (int j = 0; j < mUsers.size(); j++) {
                    Log.d(TAG, "initViewContainer: " + guests.get(i).getId());
                    Log.d(TAG, "initViewContainer: " + mUsers.get(j).mUid);
                    if ((guests.get(i).getId()) == mUsers.get(j).mUid) {
                        Log.e(TAG, "initViewContainer: " + mUsers.get(j).mUid + j);
                        Log.e(TAG, "initViewContainer: " + guests.get(i).getUid() + j);
                        surfaceViews.add(mUsers.get(j).mView);
                    } else {
                        Log.d(TAG, "initViewContainer: ");
                    }
                }
            }
            Log.d(TAG, "initViewContainer: " + surfaceViews.size());
            removeAllViews();
            frameLayout1.addView(surfaceViews.get(0));
            frameLayout2.addView(surfaceViews.get(1));
            frameLayout3.addView(surfaceViews.get(2));
            frameLayout4.addView(surfaceViews.get(3));
            frameLayout5.addView(surfaceViews.get(4));

            Log.d(TAG, "initViewContainer: " + guests.size());
            Log.d(TAG, "initViewContainer: " + mUsers.size());
            for (int i = 0; i < mUsers.size(); i++) {
                if (broadcasterUid == mUsers.get(i).mUid) {
                    frameLayout.addView(mUsers.get(i).mView);
                }
            }
        } else if (count == 7) {
            frameLayout.setBackground(null);
            frameLayout1.setBackground(null);
            frameLayout2.setBackground(null);
            frameLayout3.setBackground(null);
            frameLayout4.setBackground(null);
            frameLayout5.setBackground(null);
            frameLayout6.setBackground(null);
            frameLayout7.setBackgroundResource(R.drawable.group_add);
            frameLayout8.setBackgroundResource(R.drawable.group_add);

            for (int i = 0; i < guests.size(); i++) {
                for (int j = 0; j < mUsers.size(); j++) {
                    Log.d(TAG, "initViewContainer: " + guests.get(i).getId());
                    Log.d(TAG, "initViewContainer: " + mUsers.get(j).mUid);
                    if ((guests.get(i).getId()) == mUsers.get(j).mUid) {
                        Log.e(TAG, "initViewContainer: " + mUsers.get(j).mUid + j);
                        Log.e(TAG, "initViewContainer: " + guests.get(i).getUid() + j);
                        surfaceViews.add(mUsers.get(j).mView);
                    } else {
                        Log.d(TAG, "initViewContainer: ");
                    }
                }
            }

            removeAllViews();

            frameLayout1.addView(surfaceViews.get(0));
            frameLayout2.addView(surfaceViews.get(1));
            frameLayout3.addView(surfaceViews.get(2));
            frameLayout4.addView(surfaceViews.get(3));
            frameLayout5.addView(surfaceViews.get(4));
            frameLayout6.addView(surfaceViews.get(5));

            for (int i = 0; i < mUsers.size(); i++) {
                if (broadcasterUid == mUsers.get(i).mUid) {
                    frameLayout.addView(mUsers.get(i).mView);
                }
            }
        } else if (count == 8) {

            frameLayout.setBackground(null);
            frameLayout1.setBackground(null);
            frameLayout2.setBackground(null);
            frameLayout3.setBackground(null);
            frameLayout4.setBackground(null);
            frameLayout5.setBackground(null);
            frameLayout6.setBackground(null);
            frameLayout7.setBackground(null);
            frameLayout8.setBackgroundResource(R.drawable.group_add);

            for (int i = 0; i < guests.size(); i++) {
                for (int j = 0; j < mUsers.size(); j++) {
                    Log.d(TAG, "initViewContainer: " + guests.get(i).getId());
                    Log.d(TAG, "initViewContainer: " + mUsers.get(j).mUid);
                    if ((guests.get(i).getId()) == mUsers.get(j).mUid) {
                        Log.e(TAG, "initViewContainer: " + mUsers.get(j).mUid + j);
                        Log.e(TAG, "initViewContainer: " + guests.get(i).getUid() + j);
                        surfaceViews.add(mUsers.get(j).mView);
                    } else {
                        Log.d(TAG, "initViewContainer: ");
                    }
                }
            }
            Log.d(TAG, "initViewContainer: " + surfaceViews.size());
            removeAllViews();
            frameLayout1.addView(surfaceViews.get(0));
            frameLayout2.addView(surfaceViews.get(1));
            frameLayout3.addView(surfaceViews.get(2));
            frameLayout4.addView(surfaceViews.get(3));
            frameLayout5.addView(surfaceViews.get(4));
            frameLayout6.addView(surfaceViews.get(5));
            frameLayout7.addView(surfaceViews.get(6));

            Log.d(TAG, "initViewContainer: " + guests.size());
            Log.d(TAG, "initViewContainer: " + mUsers.size());
            for (int i = 0; i < mUsers.size(); i++) {
                if (broadcasterUid == mUsers.get(i).mUid) {
                    frameLayout.addView(mUsers.get(i).mView);
                }
            }
        } else if (count == 9) {
            for (int i = 0; i < guests.size(); i++) {
                for (int j = 0; j < mUsers.size(); j++) {
                    Log.d(TAG, "initViewContainer: " + guests.get(i).getId());
                    Log.d(TAG, "initViewContainer: " + mUsers.get(j).mUid);
                    if ((guests.get(i).getId()) == mUsers.get(j).mUid) {
                        Log.e(TAG, "initViewContainer: " + mUsers.get(j).mUid + j);
                        Log.e(TAG, "initViewContainer: " + guests.get(i).getUid() + j);
                        surfaceViews.add(mUsers.get(j).mView);
                    } else {
                        Log.d(TAG, "initViewContainer: ");
                    }
                }
            }
            Log.d(TAG, "initViewContainer: " + surfaceViews.size());

            removeAllViews();

            frameLayout1.addView(surfaceViews.get(0));
            frameLayout2.addView(surfaceViews.get(1));
            frameLayout3.addView(surfaceViews.get(2));
            frameLayout4.addView(surfaceViews.get(3));
            frameLayout5.addView(surfaceViews.get(4));
            frameLayout6.addView(surfaceViews.get(5));
            frameLayout7.addView(surfaceViews.get(6));
            frameLayout8.addView(surfaceViews.get(7));

            Log.d(TAG, "initViewContainer: " + guests.size());
            Log.d(TAG, "initViewContainer: " + mUsers.size());
            for (int i = 0; i < mUsers.size(); i++) {
                if (broadcasterUid == mUsers.get(i).mUid) {
                    frameLayout.addView(mUsers.get(i).mView);
                }
            }
        }
    }

    protected final void stripSurfaceView(SurfaceView view) {
        ViewParent parent = view.getParent();
        if (parent != null) {
            ((FrameLayout) parent).removeView(view);
        }
    }

    public final void removeAllViews() {
        frameLayout.removeAllViews();
        frameLayout1.removeAllViews();
        frameLayout2.removeAllViews();
        frameLayout3.removeAllViews();
        frameLayout4.removeAllViews();
        frameLayout5.removeAllViews();
        frameLayout6.removeAllViews();
        frameLayout7.removeAllViews();
        frameLayout8.removeAllViews();
    }
}