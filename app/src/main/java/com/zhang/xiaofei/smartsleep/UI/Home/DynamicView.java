package com.zhang.xiaofei.smartsleep.UI.Home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.zhang.xiaofei.smartsleep.Kit.DisplayUtil;

import java.util.List;

public class DynamicView extends View {

    Paint mPaint;
    public int current = 0;
    public float rate = 0;
    public int[] values;
    public boolean bDrawChart = false;
    public int screenWidth = 0;
    public int height = 0;

    public DynamicView(Context context) {
        super(context);
        initialize();
    }

    public DynamicView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public DynamicView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public DynamicView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    public void initArray(int value) {
        final int count = value;
        values = new int[count];
    }

    private void initialize() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (current > 0) {
            mPaint.setColor(Color.BLUE);
            mPaint.setStrokeWidth(2);
            for (int i = 0; i < values.length; i++) {
                if (i == 0) {
                    float s = values[0];
                    s = height - s / rate;
                    //canvas.drawLine(49, height, 50, s, mPaint);
                } else {
                    float s = values[i - 1];
                    float e = values[i];
                    if (e == 0) {
                        continue;
                    }
                    s = height - s / rate;
                    e = height - e / rate;
                    canvas.drawLine(49 + i,s,50 + i,e,mPaint);
                }
            }
        }

        mPaint.setColor(Color.argb(38,255,255,255));
        mPaint.setStrokeWidth(1);
        canvas.drawLine(50, 0, 50, height, mPaint);
        canvas.drawLine(50, 0, screenWidth - 50, 0, mPaint);
        canvas.drawLine(50, height / 2, screenWidth - 50, height / 2, mPaint);
        canvas.drawLine(50, height / 4, screenWidth - 50, height / 4, mPaint);
        canvas.drawLine(50, height * 3 / 4, screenWidth - 50, height * 3 / 4, mPaint);
        canvas.drawLine(50, height, screenWidth - 50, height, mPaint);
        canvas.drawLine(screenWidth / 2, 0, screenWidth / 2, height, mPaint);
        canvas.drawLine(screenWidth * 3 / 4, 0, screenWidth * 3 / 4, height, mPaint);
        canvas.drawLine(screenWidth / 4, 0, screenWidth / 4, height, mPaint);
        canvas.drawLine(screenWidth - 50, 0, screenWidth -50 , height, mPaint);
    }
}
