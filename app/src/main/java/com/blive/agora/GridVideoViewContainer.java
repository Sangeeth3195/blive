package com.blive.agora;

import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.blive.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class GridVideoViewContainer extends RelativeLayout {

   /* private static final String TAG = "GridVideoViewContainer";
    private ArrayList<VideoStatusData> mUsers = new ArrayList<>();
    private int mLocalUid;

    public GridVideoViewContainer(Context context) {
        super(context);
    }

    public GridVideoViewContainer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GridVideoViewContainer(Context context, @Nullable AttributeSet attrs, int defStyle) {
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
        Log.e(TAG, "initViewContainer: height " + height);
        Log.e(TAG, "initViewContainer: width " + width);

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
                Log.w(TAG, "after_changed remove not exited members " + (status.mUid & 0xFFFFFFFFL) + " " + status.mView);
                it.remove();
            }
        }

        if (count == 1) { // only local full view or or with one peer
            if (frameLayout.getChildCount() > 0) {
                frameLayout.removeAllViews();
            }

            final VideoStatusData user = mUsers.get(0);
            SurfaceView target = user.mView;
            stripView(target);

            frameLayout.addView(target);

        } else if (count == 2) {

            if (frameLayout.getChildCount() > 0) {
                frameLayout.removeAllViews();
            }

            final VideoStatusData user = mUsers.get(0);
            final VideoStatusData user1 = mUsers.get(1);

            SurfaceView target = user.mView;
            stripView(target);

            frameLayout.addView(target,0);

           *//* if (height == 1280 && width == 720) {
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
            }*//*
        }
    }

    public static void stripView(SurfaceView view) {
        ViewParent parent = view.getParent();
        if (parent != null) {
            ((FrameLayout) parent).removeView(view);
        }
    }*/

    private static final String TAG = "GridVideoViewContainer";
    private ArrayList<VideoStatusData> mUsers = new ArrayList<>();
    private int mLocalUid;

    public GridVideoViewContainer(Context context) {
        super(context);
    }

    public GridVideoViewContainer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GridVideoViewContainer(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initViewContainer(Context context, int broadcasterUid, HashMap<Integer, SurfaceView> uids, boolean newlyCreated, boolean isBroadcaster, boolean isGuest, boolean pknow) {
        Log.i("autolog", "uids: " + uids.size());
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.custom_stream, this, true);

        FrameLayout frameLayout = view.findViewById(R.id.broadcaster);
        FrameLayout leftpk = view.findViewById(R.id.left_pk);
        FrameLayout rightpk = view.findViewById(R.id.right_pk);
        LinearLayout frames_pk = view.findViewById(R.id.frames_pk);

        int count = uids.size();
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;

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
                        if (entry.getKey() == mLocalUid) {
                            mUsers.add(0, new VideoStatusData(mLocalUid, entry.getValue(), VideoStatusData.DEFAULT_STATUS, VideoStatusData.DEFAULT_VOLUME));
                        } else {
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
                Log.w(TAG, "after_changed remove not exited members " + (status.mUid & 0xFFFFFFFFL) + " " + status.mView);
                it.remove();
            }
        }
        if (!pknow) {
            if (count == 1) {
                try {
                    frameLayout.removeAllViews();
                } catch (Exception e) {
                    Log.e("", "Exception: " + e);
                }
                if (frameLayout.getChildCount() > 0) {
                    frameLayout.removeAllViews();
                }
                final VideoStatusData user = mUsers.get(0);
                SurfaceView target = user.mView;
                stripView(target);

                if (rightpk.getChildCount() > 0) {
                    rightpk.removeAllViews();
                }
                if (leftpk.getChildCount() > 0) {
                    leftpk.removeAllViews();
                }
                rightpk.setVisibility(GONE);

                leftpk.setVisibility(GONE);
                frameLayout.setVisibility(VISIBLE);
                frameLayout.addView(target);

            } else if (count == 2) {
                try {
                    frameLayout.removeAllViews();
                } catch (Exception e) {
                    Log.e("", "Exception: " + e);
                }

                if (rightpk.getChildCount() > 0) {
                    rightpk.removeAllViews();
                }
                if (leftpk.getChildCount() > 0) {
                    leftpk.removeAllViews();
                }
                rightpk.setVisibility(GONE);

                leftpk.setVisibility(GONE);
                frameLayout.setVisibility(VISIBLE);
                final VideoStatusData user = mUsers.get(0);
                final VideoStatusData user1 = mUsers.get(1);

                SurfaceView target = user.mView;
                stripView(target);

                frameLayout.addView(target, 0);

            } else if (count == 3) {
                {
                    try {
                        frameLayout.removeAllViews();
                    } catch (Exception e) {
                        Log.e("", "Exception: " + e);
                    }

                    final VideoStatusData user = mUsers.get(0);
                    final VideoStatusData user1 = mUsers.get(1);
                    final VideoStatusData user2 = mUsers.get(2);

                    if (rightpk.getChildCount() > 0) {
                        rightpk.removeAllViews();
                    }
                    if (leftpk.getChildCount() > 0) {
                        leftpk.removeAllViews();
                    }
                    rightpk.setVisibility(GONE);

                    leftpk.setVisibility(GONE);
                    frameLayout.setVisibility(VISIBLE);
                    SurfaceView target = user.mView;
                    stripView(target);
                    frameLayout.addView(target, 0);
                }
            }
        } else {
            if (count == 1) {
                try {
                    frameLayout.removeAllViews();
                } catch (Exception e) {
                    Log.e(TAG, "Exception: " + e);
                }
                frameLayout.setVisibility(VISIBLE);
                frameLayout.removeAllViews();
                frames_pk.setVisibility(GONE);
                final VideoStatusData user = mUsers.get(0);
                SurfaceView target = null;
                for (int i = 0; i < mUsers.size(); i++) {
                    if (broadcasterUid == mUsers.get(i).mUid) {
                        target = mUsers.get(i).mView;
                        Log.i("autolog", "target: " + target);
                    } else {
                        target = mUsers.get(0).mView;
                    }
                }
                Log.i("autolog", "target: " + target);

                if (frameLayout.getChildCount() > 0) {
                    frameLayout.removeAllViews();
                }
                if (rightpk.getChildCount() > 0) {
                    rightpk.removeAllViews();
                }
                if (leftpk.getChildCount() > 0) {
                    leftpk.removeAllViews();
                }
                rightpk.setVisibility(GONE);

                leftpk.setVisibility(GONE);

                stripView(target);
                if (target.getParent() != null)
                    ((ViewGroup) (target.getParent())).removeAllViews();

                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                target.setZOrderOnTop(false);
                target.setZOrderMediaOverlay(false);
                target.setLayoutParams(lp);
                frameLayout.addView(target);

            } else if (count == 2) {
                if (pknow) {
                    if (isGuest) {
                    } else {
                        final VideoStatusData user = mUsers.get(0);
                        final VideoStatusData user1 = mUsers.get(1);

                        SurfaceView localView = user.mView;
                        SurfaceView remoteView = user1.mView;

                        frameLayout.setVisibility(GONE);
                        frameLayout.removeAllViews();
                        frames_pk.setVisibility(VISIBLE);
                        rightpk.setVisibility(VISIBLE);
                        leftpk.setVisibility(VISIBLE);
                        if (leftpk.getChildCount() > 0) {
                            leftpk.removeAllViews();
                        }

                        if (localView.getParent() != null)
                            ((ViewGroup) (localView.getParent())).removeAllViews();

                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        localView.setZOrderOnTop(false);
                        localView.setZOrderMediaOverlay(false);
                        localView.setLayoutParams(lp);
                        leftpk.addView(localView);

                        if (rightpk.getChildCount() > 0)
                            rightpk.removeAllViews();

                        if (remoteView.getParent() != null)
                            ((ViewGroup) (remoteView.getParent())).removeAllViews();

                        FrameLayout.LayoutParams lp1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        remoteView.setZOrderOnTop(false);
                        remoteView.setZOrderMediaOverlay(false);
                        remoteView.setLayoutParams(lp1);
                        rightpk.addView(remoteView);
                    }
                }
            }
        }
    }

    public static void stripView(SurfaceView view) {
        ViewParent parent = view.getParent();
        if (parent != null) {
            ((FrameLayout) parent).removeView(view);
        }
    }

}