package com.blive.custom;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import static com.blive.BLiveApplication.TAG;

/**
 * Created by sans on 24-08-2018.
 **/

public class OnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;

    public OnSwipeTouchListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public void onSwipeUp(){
        Log.e(TAG, "onSwipeUp: " );
    }

    public void onSwipeDown(){
        Log.e(TAG, "onSwipeDown: " );
    }

    public void onSwipeLeft() {
        Log.e(TAG, "onSwipeLeft: " );
    }

    public void onSwipeRight() {
        Log.e(TAG, "onSwipeRight: " );
    }

    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            switch (getSlope(e1.getX(), e1.getY(), e2.getX(), e2.getY())) {
                case 1:
                    onSwipeUp();
                    Log.e(TAG, "onFling: top");
                    return true;
                case 2:
                    onSwipeLeft();
                    Log.e(TAG, "onFling: left");
                    return true;
                case 3:
                    onSwipeDown();
                    Log.e(TAG, "onFling: down");
                    return true;
                case 4:
                    onSwipeRight();
                    Log.e(TAG, "onFling: right");
                    return true;
            }
            return false;
        }

        private int getSlope(float x1, float y1, float x2, float y2) {
            Double angle = Math.toDegrees(Math.atan2(y1 - y2, x2 - x1));
            if (angle > 45 && angle <= 135)
                // top
                return 1;
            if (angle >= 135 && angle < 180 || angle < -135 && angle > -180)
                // left
                return 2;
            if (angle < -45 && angle>= -135)
                // down
                return 3;
            if (angle > -45 && angle <= 45)
                // right
                return 4;
            return 0;
        }
    }
}