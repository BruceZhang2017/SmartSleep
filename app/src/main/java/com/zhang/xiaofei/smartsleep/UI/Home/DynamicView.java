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
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (current > 0) {
            for (int i = 0; i < values.length; i++) {
                if (i == 0) {
                    float s = values[0];
                    s = 100 - s / rate;
                    canvas.drawLine(49, 50, 50, s, mPaint);
                } else {
                    float s = values[i - 1];
                    float e = values[i];
                    if (e == 0) {
                        continue;
                    }
                    s = 100 - s / rate;
                    e = 100 - e / rate;
                    canvas.drawLine(49 + i,s,50 + i,e,mPaint);
                }
            }
        }
    }
}
