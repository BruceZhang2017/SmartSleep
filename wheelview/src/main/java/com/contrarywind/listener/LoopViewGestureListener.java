package com.contrarywind.listener;

import android.view.MotionEvent;

import com.contrarywind.view.WheelView;


/**
 * 手势监听
 */
public final class LoopViewGestureListener extends android.view.GestureDetector.SimpleOnGestureListener {

    private final WheelView wheelView;


    public LoopViewGestureListener(WheelView wheelView) {
        this.wheelView = wheelView;
    }

    @Override
    public final boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        System.out.println("Wheel View OnFling");
        wheelView.getParent().requestDisallowInterceptTouchEvent(true);
        wheelView.scrollBy(velocityY);
        return true;
    }
}
