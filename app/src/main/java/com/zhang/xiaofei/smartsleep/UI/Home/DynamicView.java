package com.zhang.xiaofei.smartsleep.UI.Home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.zhang.xiaofei.smartsleep.Kit.DisplayUtil;

import java.util.List;

public class DynamicView extends View {

    Paint mPaint;
    public int current = 0;
    private int currentFinished = 0;
    public float rate = 0;
    public int[] values;
    public int screenWidth = 0;
    public int height = 0;
    public ValueAnimator valueAnimator;
    public float currentValue;
    public Boolean isAnimating = false;
    public Path animatorPath;

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
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);//绘制背景颜色
        if (values != null && values.length > 0) {
            mPaint.setColor(Color.WHITE);
            mPaint.setStrokeWidth(1);
            drawCurveStatic(canvas);
            drawCurveDynamic(canvas);
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
        canvas.drawLine((screenWidth - 100) * 3 / 4 + 50, 0, (screenWidth - 100) * 3 / 4 + 50, height, mPaint);
        canvas.drawLine((screenWidth - 100) / 4 + 50, 0, (screenWidth - 100) / 4 + 50, height, mPaint);
        canvas.drawLine(screenWidth - 50, 0, screenWidth - 50 , height, mPaint);
    }

    private void drawCurveStatic(Canvas canvas) {
        if (currentFinished <= 0) {
            return;
        }
        drawStaticCurve(canvas);
    }

    private void drawStaticCurve(Canvas canvas) {
        float d = (screenWidth - 100) / 10;
        float step = d / (values.length == 50 ? 5 : 25);
        Path path = new Path();
        PointF pre = new PointF();
        pre.set(50, (int)(height - values[0] / rate));
        path.moveTo(pre.x, pre.y);
        int max = currentFinished * (values.length == 50 ? 5 : 25);
        for (int i = 1; i < max; i++) {
            PointF next = new PointF();
            next.set(50 + step * i, height - values[i] / rate);
            float cW = pre.x + step / 2;

            PointF p1 = new PointF();//控制点1
            p1.set(cW, pre.y);

            PointF p2 = new PointF();//控制点2
            p2.set(cW, next.y);

            path.cubicTo(p1.x, p1.y, p2.x, p2.y, next.x, next.y);//创建三阶贝塞尔曲线

            pre = next;
        }
        canvas.drawPath(path, mPaint);
    }

    private void drawCurveDynamic(Canvas canvas) {
        Path dst = new Path();
        PathMeasure measure = new PathMeasure(animatorPath, false);
        measure.getSegment(0, currentValue * measure.getLength(), dst, true);
        canvas.drawPath(dst, mPaint);
    }

    public void startAnimator() {
        if (isAnimating) {
            return;
        }
        createAnimatorPath();
        initAnimator();
        valueAnimator.start();
    }

    private void initAnimator() {
        if (valueAnimator != null) {
            return;
        }
        int time = 1100;
        if (currentFinished + 1 > current) {
            time = 1800;
        }
        valueAnimator = ValueAnimator.ofFloat(0f, 1f).setDuration(time);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentValue = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                currentValue = 0f;
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                currentValue = 1f;
                isAnimating = false;
                finishAnimator();
            }
        });
        valueAnimator.setStartDelay(0);
    }

    private void finishAnimator() {
        animatorPath = null;
        currentFinished += 1; // 完成动画
        if (valueAnimator != null) {
            valueAnimator.cancel();
            valueAnimator = null;
        }
        if (currentFinished < 10) {
            if (currentFinished + 1 <= current) {
                startAnimator();
            }
        } else {
            System.out.println("当前绘制结束：" + values.length + "current:" + current + "finished:" + currentFinished);
            current = 0;
            currentFinished = 0;
        }
    }

    private void createAnimatorPath() {
        float d = (screenWidth - 100) / 10;
        float step = d / (values.length == 50 ? 5 : 25);
        if (animatorPath == null) {
            animatorPath = new Path();
            int index = currentFinished * (values.length == 50 ? 5 : 25);
            if (currentFinished > 0) {
                index -= 1;
            }
            PointF pre = new PointF();
            pre.set(50 + step * index, height - values[index] / rate);
            animatorPath.moveTo(pre.x, pre.y);
            System.out.println("动画起点：" + pre.x + " current: " + current);
            if (currentFinished > 0) {
                index += 1;
            }
            for (int i = index; i < (currentFinished + 1) * (values.length == 50 ? 5 : 25); i++) {
                PointF next = new PointF();
                next.set(50 + step * i, height - values[i] / rate);

                float cW = pre.x + step / 2;

                PointF p1 = new PointF();//控制点1
                p1.set(cW, pre.y);

                PointF p2 = new PointF();//控制点2
                p2.set(cW, next.y);

                animatorPath.cubicTo(p1.x, p1.y, p2.x, p2.y, next.x, next.y);//创建三阶贝塞尔曲线

                pre = next;

                if (i == (currentFinished + 1) * (values.length == 50 ? 5 : 25) - 1) {
                    System.out.println("最后的坐标：" + next.x + " 类型：" + (values.length == 50 ? 1 : 2));
                }
            }
        }
    }

    public void clearAnimator() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
            valueAnimator = null;
        }
    }
}
