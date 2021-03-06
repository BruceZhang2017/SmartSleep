package com.zhang.xiaofei.smartsleep.UI.Home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.haibin.calendarview.CalendarView;
import com.shizhefei.fragment.LazyFragment;
import com.zhang.xiaofei.smartsleep.Kit.BigSmallFont.BigSmallFontManager;
import com.zhang.xiaofei.smartsleep.Model.Alarm.AlarmModel;
import com.zhang.xiaofei.smartsleep.Model.Record.RecordModel;
import com.zhang.xiaofei.smartsleep.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.feeeei.circleseekbar.CircleSeekBar;
import io.realm.Realm;
import io.realm.RealmResults;

public class ReportDayFragment extends LazyFragment { // 日报告

    private TextView textView;
    private CircleSeekBar circlePercentView;
    private CirclePercentView circleSleep1;
    private CirclePercentView circleSleep2;
    private CirclePercentView circleSleep3;
    private CirclePercentView circleSleep4;
    private LineChart chart; // 睡眠质量分析
    private LineChart chart2; // 翻身
    private LineChart chart3; // 心率
    private LineChart chart4; // 呼吸率
    private LineChart chart5;
    CalendarView mCalendarView;
    private ImageButton ibLeftPre;
    private ImageButton ibRightNex;
    private TextView tvSleepReviewValue;
    private TextView tvTime1; // 上床时间
    private TextView tvTime2; // 入睡时长
    private TextView tvTime3; // 清醒时间
    private TextView tvTime4; // 平均心率
    private TextView tvTime5; // 清醒时间
    private TextView tvTime6; // 平均心率
    private TextView tvSimulationData;
    private TextView tvSleepBottomCount1;
    private TextView tvSleepBottomCount2;
    private TextView tvSleepBottomCount3;
    private ConstraintLayout cl5;
    private ConstraintLayout cl6;
    private ConstraintLayout cl11;
    private Realm mRealm;
    private int currentTime = 0;
    private int grade = 83;
    ArrayList<Entry> chart2Values;
    ArrayList<Entry> chart3Values;
    int sleepTime = -3600;
    int getupTime = 7 * 60 * 60;

    @Override
    protected View getPreviewLayout(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.layout_preview, container, false);
    }

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);

        setContentView(R.layout.fragment_tabmain_item);
        circlePercentView = (CircleSeekBar)findViewById(R.id.circle_percent_progress);
        circlePercentView.setCurProcess(grade);
        circleSleep1 = (CirclePercentView)findViewById(R.id.circle_sleep_1);
        circleSleep1.setPercentage(6);
        circleSleep1.setBgColor(getResources().getColor(R.color.color_33315FEE));
        circleSleep1.setProgressColor(getResources().getColor(R.color.color_315FEE));
        circleSleep2 = (CirclePercentView)findViewById(R.id.circle_sleep_2);
        circleSleep2.setPercentage(55);
        circleSleep2.setBgColor(getResources().getColor(R.color.color_33499BE5));
        circleSleep2.setProgressColor(getResources().getColor(R.color.color_499BE5));
        circleSleep3 = (CirclePercentView)findViewById(R.id.circle_sleep_3);
        circleSleep3.setPercentage(23);
        circleSleep3.setBgColor(getResources().getColor(R.color.color_3367D9F1));
        circleSleep3.setProgressColor(getResources().getColor(R.color.color_67D9F1));
        circleSleep4 = (CirclePercentView)findViewById(R.id.circle_sleep_4);
        circleSleep4.setPercentage(34);
        circleSleep4.setBgColor(getResources().getColor(R.color.color_3361D088));
        circleSleep4.setProgressColor(getResources().getColor(R.color.color_61D088));

        initialCurrentTime();
        mRealm = Realm.getDefaultInstance();

        RealmResults<AlarmModel> userList = mRealm.where(AlarmModel.class).findAll();
        if (userList != null && userList.size() > 0) {
            for (AlarmModel model: userList) {
                if (model.getType() == 0) {
                    getupTime = model.getHour() * 60 * 60 + model.getMinute() * 60;
                } else {
                    if (model.getHour() < 12) {
                        sleepTime = model.getHour() * 60 * 60 + model.getMinute() * 60;
                    } else {
                        sleepTime = (model.getHour() * 60 * 60 + model.getMinute() * 60) - 24 * 60 * 60;
                    }
                }
            }
        }

        initializeForChart(); // 睡眠质量初始化
        initializeForChart2();
        initializeForChart3();
        initializeForChart4();
        initializeForChart5();

        ibLeftPre = (ImageButton)findViewById(R.id.ib_left_pre);
        ibLeftPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTime -= 24 * 60 * 60;
                grade -= 1;
                tvSimulationData.setText(currentDate((long)currentTime * 1000));
                circlePercentView.setCurProcess(grade);
                refreshGrade();
                setData2();
            }
        });
        ibRightNex = (ImageButton)findViewById(R.id.ib_right_next);
        ibRightNex.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                currentTime += 24 * 60 * 60;
                grade += 1;
                tvSimulationData.setText(currentDate((long)currentTime * 1000));
                circlePercentView.setCurProcess(grade);
                refreshGrade();
                setData2();
            }
        });

        initialText();

        tvSimulationData = (TextView)findViewById(R.id.tv_simulation_data);
        tvSimulationData.setText(currentDate(System.currentTimeMillis()));
        tvSimulationData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyDialog();
            }
        });

        tvSleepBottomCount1 = (TextView)findViewById(R.id.tv_sleep_bottom_count_1);
        tvSleepBottomCount2 = (TextView)findViewById(R.id.tv_sleep_bottom_count_2);
        tvSleepBottomCount3 = (TextView)findViewById(R.id.tv_sleep_bottom_count_3);
        String content1 = "70 " + getResources().getString(R.string.common_times_minute);
        String unit1 = getResources().getString(R.string.common_times_minute);
        tvSleepBottomCount1.setText(BigSmallFontManager.createTimeValue(content1, getActivity(), 13, unit1));
        String content2 = "20 " + getResources().getString(R.string.common_times_minute);
        String unit2 = getResources().getString(R.string.common_times_minute);
        tvSleepBottomCount2.setText(BigSmallFontManager.createTimeValue(content2, getActivity(), 13, unit2));
        String content = "2 " + getResources().getString(R.string.common_times);
        String unit = getResources().getString(R.string.common_times);
        tvSleepBottomCount3.setText(BigSmallFontManager.createTimeValue(content, getActivity(), 13, unit));

        System.out.println("当前的时间端：" + (currentTime + sleepTime) + " " + (currentTime + getupTime));

        cl5 = (ConstraintLayout)findViewById(R.id.cl_5);
        cl6 = (ConstraintLayout)findViewById(R.id.cl_6);
        cl11 = (ConstraintLayout)findViewById(R.id.cl_11);
        cl11.setVisibility(View.GONE);
    }

    private void initialText() {
        tvSleepReviewValue = (TextView)findViewById(R.id.tv_sleep_review_value);
        refreshGrade();
        tvTime1 = (TextView)findViewById(R.id.tv_time_1);
        String unit11 = getResources().getString(R.string.common_hour2);
        String unit12 = getResources().getString(R.string.common_minute3);
        String[] array1 = {unit11, unit12};
        String content1 = "23" + unit11 + "30" + unit12;
        tvTime1.setText(BigSmallFontManager.createTimeValue(content1, getActivity(), 13, array1));
        tvTime2 = (TextView)findViewById(R.id.tv_time_2);
        String unit21 = getResources().getString(R.string.common_hour);
        String unit22 = getResources().getString(R.string.common_minute);
        String[] array2 = {unit21, unit22};
        String content2 = "17" + unit21 + "30" + unit22;
        tvTime2.setText(BigSmallFontManager.createTimeValue(content2, getActivity(), 13, array2));
        tvTime3 = (TextView)findViewById(R.id.tv_time_3);
        tvTime4 = (TextView)findViewById(R.id.tv_time_4);
        String unit4 = getResources().getString(R.string.common_times_minute);
        String[] array4 = {unit4};
        String content4 = "60" + unit4;
        tvTime3.setText(BigSmallFontManager.createTimeValue(content4, getActivity(), 13, array4));
        tvTime4.setText(BigSmallFontManager.createTimeValue(content4, getActivity(), 13, array4));
        String unit5 = "℃";
        String[] array5 = {unit5};
        String content5 = "20" + unit5;
        tvTime5 = (TextView)findViewById(R.id.tv_time_5);
        tvTime5.setText(BigSmallFontManager.createTimeValue(content5, getActivity(), 13, array5));
        String unit6 = "%";
        String[] array6 = {unit6};
        String content6 = "48" + unit6;
        tvTime6 = (TextView)findViewById(R.id.tv_time_6);
        tvTime6.setText(BigSmallFontManager.createTimeValue(content6, getActivity(), 13, array6));
    }

    private void refreshGrade() {
        String unit = getResources().getString(R.string.common_minute2);
        String[] array = {unit};
        String content = grade + "" + unit;
        tvSleepReviewValue.setText(BigSmallFontManager.createTimeValue(content, getActivity(), 25, array));
    }

    private String currentDate(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(time);
        String str=simpleDateFormat.format(date);
        return str;
    }

    private void initialCurrentTime() {
        String str = currentDate(System.currentTimeMillis());
        String[] array = str.split("-");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(array[0]), Integer.parseInt(array[1]) - 1, Integer.parseInt(array[2]),0,0,0);
        currentTime = (int)(calendar.getTimeInMillis() / 1000);
        System.out.println("当前时间：" + currentTime);
    }

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        handler.removeMessages(1);
        mRealm.close();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            textView.setVisibility(View.VISIBLE);
            //progressBar.setVisibility(View.GONE);
        }

        ;
    };

    private void setCharData(int count, float range) {

        ArrayList<Entry> values = new ArrayList<>();
        long now = currentTime + sleepTime;
        long to = currentTime + getupTime;
        System.out.println("时间段: " + now + " " + to);
        for (float i = now; i < to; i += 3600) {
            float val = (float) (Math.random() * range);
            values.add(new Entry(i, val));
        }

        LineDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "");

            set1.setDrawIcons(false);
            set1.setDrawHorizontalHighlightIndicator(false);
            set1.setDrawVerticalHighlightIndicator(false);

            // black lines and points
            set1.setColor(getResources().getColor(R.color.colorWhite)); // 线颜色
            set1.setCircleColor(Color.BLACK); // 点颜色

            // line thickness and point size
            set1.setLineWidth(0.5f);
            set1.setCircleRadius(3f);

            // draw points as solid circles
            set1.setDrawCircleHole(false);

            // customize legend entry
            set1.setFormLineWidth(1f);
            set1.setFormSize(15.f);
            set1.setDrawValues(false);
            // text size of values
            set1.setValueTextSize(9f);
            set1.setDrawCircles(false);
            // draw selection line as dashed
            //set1.enableDashedHighlightLine(10f, 5f, 0f);

            // set the filled area
            set1.setDrawFilled(true);
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.BLACK);
            }
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });

            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets

            // create a data object with the data sets
            LineData data = new LineData(dataSets);
            // set data
            chart.setData(data);
        }
    }

    private void initializeForChart() {
        {   // // Chart Style // //
            chart = findViewById(R.id.chart1);
            // background color
            chart.setBackgroundColor(getResources().getColor(R.color.tranparencyColor));
            // disable description text
            chart.getDescription().setEnabled(false);
            // enable touch gestures
            chart.setTouchEnabled(false);
            // set listeners
            //chart.setOnChartValueSelectedListener(this);
            chart.setDrawGridBackground(false);
            // enable scaling and dragging
            chart.setDragEnabled(true);
            chart.setScaleEnabled(true);
            // chart.setScaleXEnabled(true);
            // chart.setScaleYEnabled(true);
            // force pinch zoom along both axis
            chart.setPinchZoom(false);
        }

        XAxis xAxis;
        {   // // X-Axis Style // //
            xAxis = chart.getXAxis();
            xAxis.setTextColor(getResources().getColor(R.color.color_B2C1E0));
            xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            @Override
            public String getFormattedValue(float value) {
                long millis = TimeUnit.SECONDS.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        });
            xAxis.setLabelCount(8);
            xAxis.setGranularityEnabled(true);
            xAxis.setGranularity(3600f);
            xAxis.setAxisMinimum(currentTime + sleepTime);
            xAxis.setAxisMaximum(currentTime + getupTime);
        }

        YAxis yAxis;
        {   // // Y-Axis Style // //
            yAxis = chart.getAxisLeft();
            // disable dual axis (only use LEFT axis)
            chart.getAxisRight().setEnabled(false);
            yAxis.setTextColor(getResources().getColor(R.color.tranparencyColor));
            yAxis.setAxisMaximum(5f);
            yAxis.setAxisMinimum(0f);
            yAxis.setSpaceTop(0);
            yAxis.setSpaceBottom(0);
            yAxis.setLabelCount(5);
            yAxis.setGranularityEnabled(true);
            yAxis.setGranularity(1f);
        }


        {
            // draw limit lines behind data instead of on top
            //yAxis.setDrawLimitLinesBehindData(true);
            //xAxis.setDrawLimitLinesBehindData(true);

        }

        setCharData(45, 4);

        // draw points over time
        chart.animateX(100);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend(); // 就是图表的名称
        l.setEnabled(false);
    }

    private void initializeForChart2() {
        chart2 = findViewById(R.id.chart2);

        // no description text
        chart2.getDescription().setEnabled(false);

        // enable touch gestures
        chart2.setTouchEnabled(true);

        chart2.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        chart2.setDragEnabled(true);
        chart2.setScaleEnabled(true);
        chart2.setDrawGridBackground(false);
        chart2.setHighlightPerDragEnabled(false);
        chart2.setPinchZoom(false);

        // set an alternative background color
        chart2.setBackgroundColor(getResources().getColor(R.color.tranparencyColor));
        //chart2.setViewPortOffsets(0f, 0f, 0f, 0f); // 设置上下左右偏移

        // get the legend (only possible after setting data)
        Legend l = chart2.getLegend();
        l.setEnabled(false);

        XAxis xAxis = chart2.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        //xAxis.setTypeface(tfLight);
        xAxis.setAxisLineDashedLine(new DashPathEffect(new float[]{5f, 5f}, 0f));
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.argb(0,0,0,0));
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setCenterAxisLabels(false);
        xAxis.setLabelCount(8);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(3600f);
        xAxis.setAxisMinimum(currentTime + sleepTime);
        xAxis.setAxisMaximum(currentTime + getupTime);
        xAxis.setValueFormatter(new ValueFormatter() {

            private final SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value) {

                long millis = TimeUnit.SECONDS.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        });

        YAxis leftAxis = chart2.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setAxisLineDashedLine(new DashPathEffect(new float[]{5f, 5f}, 0f));
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(150f);
        leftAxis.setTextColor(getResources().getColor(R.color.color_FFC858));
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        YAxis rightAxis = chart2.getAxisRight();
        rightAxis.setEnabled(false);

        setData2();
    }

    private void setData2() {
        boolean flag = false;
        if (chart2Values != null) {
            flag = true;
            chart2Values.clear();
        } else {
            chart2Values = new ArrayList<>();
        }

        RealmResults<RecordModel> list = mRealm.where(RecordModel.class)
                .greaterThan("time", currentTime + sleepTime)
                .lessThan("time", currentTime + getupTime)
                .findAll();
        if (list.size() > 100) {
            for (RecordModel model: list) {
                System.out.println("获取到的数据时间：" + model.getTime());
                chart2Values.add(new Entry(model.getTime(), model.getHeartRate()));
            }
        } else {
            for (int i = (currentTime + sleepTime); i < (currentTime + getupTime); i += 60) {
                float y = getRandom(10, 0);
                chart2Values.add(new Entry(i, y + 55));
            }
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(chart2Values, "");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(getResources().getColor(R.color.color_6EE1CA));
        set1.setValueTextColor(ColorTemplate.getHoloBlue());
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);

        // create a data object with the data sets
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        chart2.setData(data);
        if (flag) {
            chart2.notifyDataSetChanged();
        }
    }

    private void initializeForChart3() {
        chart3 = findViewById(R.id.chart3);

        // no description text
        chart3.getDescription().setEnabled(false);

        // enable touch gestures
        chart3.setTouchEnabled(true);

        chart3.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        chart3.setDragEnabled(true);
        chart3.setScaleEnabled(true);
        chart3.setDrawGridBackground(false);
        chart3.setHighlightPerDragEnabled(false);
        chart3.setPinchZoom(false);
        // set an alternative background color
        chart3.setBackgroundColor(getResources().getColor(R.color.tranparencyColor));
        //chart3.setViewPortOffsets(10f, 0f, 0f, 0f);

        // get the legend (only possible after setting data)
        Legend l = chart3.getLegend();
        l.setEnabled(false);

        XAxis xAxis = chart3.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setAxisLineDashedLine(new DashPathEffect(new float[]{5f, 5f}, 0f));
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.argb(0,0,0,0));
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setCenterAxisLabels(false);
        xAxis.setLabelCount(8);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(3600f);
        xAxis.setAxisMinimum(currentTime + sleepTime);
        xAxis.setAxisMaximum(currentTime + getupTime);
        xAxis.setValueFormatter(new ValueFormatter() {

            private final SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value) {

                long millis = TimeUnit.SECONDS.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        });

        YAxis leftAxis = chart3.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        //leftAxis.setTypeface(tfLight);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(40f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setAxisLineDashedLine(new DashPathEffect(new float[]{5f, 5f}, 0f));
        leftAxis.setTextColor(getResources().getColor(R.color.color_FFC858));

        YAxis rightAxis = chart3.getAxisRight();
        rightAxis.setEnabled(false);

        setData3();
    }

    private void setData3() {
        boolean flag = false;
        if (chart3Values != null) {
            flag = true;
            chart3Values.clear();
        } else {
            chart3Values = new ArrayList<>();
        }

        RealmResults<RecordModel> list = mRealm.where(RecordModel.class)
                .greaterThan("time", currentTime + sleepTime)
                .lessThan("time", currentTime + getupTime)
                .findAll();
        System.out.println("获取到的数据总量：" + list.size());
        if (list.size() > 100) {
            for (RecordModel model: list) {
                chart3Values.add(new Entry(model.getTime(), model.getBreathRate()));
            }
        } else {
            for (int i = (currentTime + sleepTime); i < (currentTime + getupTime); i += 60) {
                float y = getRandom(10, 0);
                chart3Values.add(new Entry(i, y + 15));
            }
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(chart3Values, "");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(getResources().getColor(R.color.color_6EE1CA));
        set1.setValueTextColor(ColorTemplate.getHoloBlue());
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // create a data object with the data sets
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        chart3.setData(data);
        if (flag) {
            chart3.notifyDataSetChanged();
        }
    }

    private void initializeForChart4() {
        chart4 = findViewById(R.id.chart4);

        // no description text
        chart4.getDescription().setEnabled(false);

        // enable touch gestures
        chart4.setTouchEnabled(true);

        chart4.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        chart4.setDragEnabled(true);
        chart4.setScaleEnabled(true);
        chart4.setDrawGridBackground(false);
        chart4.setHighlightPerDragEnabled(false);
        chart4.setPinchZoom(false);
        // set an alternative background color
        chart4.setBackgroundColor(getResources().getColor(R.color.tranparencyColor));
        //chart4.setViewPortOffsets(0f, 0f, 0f, 0f);

        // get the legend (only possible after setting data)
        Legend l = chart4.getLegend();
        l.setEnabled(false);

        XAxis xAxis = chart4.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setAxisLineDashedLine(new DashPathEffect(new float[]{5f, 5f}, 0f));
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.argb(0,0,0,0));
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setCenterAxisLabels(false);
        xAxis.setLabelCount(8);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {

            private final SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value) {

                long millis = TimeUnit.SECONDS.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        });

        YAxis leftAxis = chart4.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setAxisLineDashedLine(new DashPathEffect(new float[]{5f, 5f}, 0f));
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(5f);
        leftAxis.setTextColor(getResources().getColor(R.color.color_FFC858));

        YAxis rightAxis = chart4.getAxisRight();
        rightAxis.setEnabled(false);

        setData4(200, 3);
    }

    private void setData4(int count, float range) {

        // now in hours
        long now = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis());
        ArrayList<Entry> values = new ArrayList<>();
        // count = hours
        float to = now + count;
        // increment by 1 hour
        for (float x = now; x < to; x++) {
            float y = getRandom(range, 0);
            values.add(new Entry(x, y)); // add one entry per hour
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, "");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(getResources().getColor(R.color.color_6EE1CA));
        set1.setValueTextColor(ColorTemplate.getHoloBlue());
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setDrawCircleHole(false);
        set1.setDrawVerticalHighlightIndicator(false);
        set1.setDrawHorizontalHighlightIndicator(false);
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // create a data object with the data sets
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        chart4.setData(data);
    }

    private void initializeForChart5() {
        chart5 = findViewById(R.id.chart5);

        // no description text
        chart5.getDescription().setEnabled(false);

        // enable touch gestures
        chart5.setTouchEnabled(true);

        chart5.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        chart5.setDragEnabled(true);
        chart5.setScaleEnabled(true);
        chart5.setDrawGridBackground(false);
        chart5.setHighlightPerDragEnabled(false);
        chart5.setPinchZoom(false);
        // set an alternative background color
        chart5.setBackgroundColor(getResources().getColor(R.color.tranparencyColor));
        //chart4.setViewPortOffsets(0f, 0f, 0f, 0f);

        // get the legend (only possible after setting data)
        Legend l = chart5.getLegend();
        l.setEnabled(false);

        XAxis xAxis = chart5.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setAxisLineDashedLine(new DashPathEffect(new float[]{5f, 5f}, 0f));
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.argb(0,0,0,0));
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setCenterAxisLabels(false);
        xAxis.setLabelCount(8);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {

            private final SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value) {

                long millis = TimeUnit.SECONDS.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        });

        YAxis leftAxis = chart5.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setAxisLineDashedLine(new DashPathEffect(new float[]{5f, 5f}, 0f));
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(2f);
        leftAxis.setTextColor(getResources().getColor(R.color.color_FFC858));

        YAxis rightAxis = chart5.getAxisRight();
        rightAxis.setEnabled(false);

        setData5(200, 2);
    }

    private void setData5(int count, float range) {

        // now in hours
        long now = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis());
        ArrayList<Entry> values = new ArrayList<>();
        // count = hours
        float to = now + count;
        // increment by 1 hour
        for (float x = now; x < to; x++) {
            float y = getRandom(range, 0);
            values.add(new Entry(x, y)); // add one entry per hour
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, "");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(getResources().getColor(R.color.color_6EE1CA));
        set1.setValueTextColor(ColorTemplate.getHoloBlue());
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setDrawCircleHole(false);
        set1.setDrawVerticalHighlightIndicator(false);
        set1.setDrawHorizontalHighlightIndicator(false);
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // create a data object with the data sets
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        chart5.setData(data);
    }

    protected float getRandom(float range, float start) {
        return (float) (Math.random() * range) + start;
    }

    Dialog dialog;

    private void initData() {
        mCalendarView.setSelectSingleMode();
        mCalendarView.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
            @Override
            public void onCalendarOutOfRange(com.haibin.calendarview.Calendar calendar) {

            }

            @Override
            public void onCalendarSelect(com.haibin.calendarview.Calendar calendar, boolean isClick) {
                if (isClick) {
                    currentTime = (int)(calendar.getTimeInMillis() / 1000);
                    tvSimulationData.setText(currentDate((long)currentTime * 1000));
                    dialog.hide();
                }
            }
        });
    }

    private void verifyDialog()
    {
        dialog = new Dialog(getActivity(), R.style.activity_translucent);
        dialog.setContentView(R.layout.layout_choose_date);
        mCalendarView = (CalendarView)dialog.getWindow().findViewById(R.id.calendarView);
        initData();
        if(dialog!=null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void refreshData(boolean isBlet) {
        cl5.setVisibility(isBlet ? View.VISIBLE : View.GONE);
        cl6.setVisibility(isBlet ? View.VISIBLE : View.GONE);
        cl11.setVisibility(isBlet ? View.GONE : View.VISIBLE);
    }

}
