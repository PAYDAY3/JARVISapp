package com.example.jarvisassistant;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.GestureDetectorCompat;

public class GestureHelper implements View.OnTouchListener {

    private final GestureDetectorCompat gestureDetector;

    public GestureHelper(Context context, OnGestureListener listener) {
        gestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > 100 && Math.abs(velocityX) > 100) {
                        if (diffX > 0) {
                            listener.onSwipeRight();
                        } else {
                            listener.onSwipeLeft();
                        }
                    }
                } else {
                    if (Math.abs(diffY) > 100 && Math.abs(velocityY) > 100) {
                        if (diffY > 0) {
                            listener.onSwipeDown();
                        } else {
                            listener.onSwipeUp();
                        }
                    }
                }
                return false;
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    public interface OnGestureListener {
        void onSwipeRight();
        void onSwipeLeft();
        void onSwipeUp();
        void onSwipeDown();
    }
}

