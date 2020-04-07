package com.zhang.xiaofei.smartsleep.UI.Home;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

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
import com.haibin.calendarview.CalendarView;
import com.shizhefei.fragment.LazyFragment;
import com.zhang.xiaofei.smartsleep.Kit.BigSmallFont.BigSmallFontManager;
import com.zhang.xiaofei.smartsleep.Kit.DisplayUtil;
import com.zhang.xiaofei.smartsleep.Model.Alarm.AlarmModel;
import com.zhang.xiaofei.smartsleep.Model.Record.RecordModel;
import com.zhang.xiaofei.smartsleep.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ReportMonthFragment extends LazyFragment {

    private LineChart chart; // 睡眠质量分析
    private TextView tvSleepAverageTime;
    private TextView tvTime1; // 上床时间
    private TextView tvTime2; // 入睡时长
    private TextView tvTime3; // 清醒时间
    private TextView tvTime4; // 平均心率
    private TextView tvTime5; // 平均呼吸率
    private TextView tvTime6; // 清醒次数
    private TextView tvSleepTime4;
    private TextView tvSleepTime5;
    private ImageView ivSleepTime4;
    private ImageView ivSleepTime5;
    private CalendarView calendarView;
    private ImageButton ibLeftPre;
    private ImageButton ibRightNex;
    private TextView tvSimulationData;
    private Realm mRealm;
    private int currentTime = 0;
    private int year = 0;
    private int month = 0;
    private int days = 0;
    private Boolean bBelt = true; // 刷新睡眠带，睡眠纽扣
    int[] sleepOneDayTimes = new int[31]; // 深睡眠时长
    int nosleepTimeTotal = 0; // 清醒时长
    int noSleepMinuteCount = 0; // 清醒次数
    Map<Integer, List<RecordModel>> mMap = new HashMap<Integer, List<RecordModel>>();

    @Override
    protected View getPreviewLayout(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.layout_preview, container, false);
    }

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_tabmain_item3);
        initializeForChart(); // 睡眠质量初始化
        initialText();
        initialCalendarView();
        year = calendarView.getCurYear();
        month = calendarView.getCurMonth();
        initialCurrentTime();
        getDayData(currentTime);
        ibLeftPre = (ImageButton)findViewById(R.id.ib_left_pre);
        ibLeftPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.scrollToPre();
                if (month > 1) {
                    month--;
                } else {
                    year--;
                    month = 12;
                }
                tvSimulationData.setText(year + "-" + (month > 9 ? ("" + month) : ("0" + month)));
                initialCurrentTime();
            }
        });
        ibRightNex = (ImageButton)findViewById(R.id.ib_right_next);
        ibRightNex.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                calendarView.scrollToNext();
                if (month < 11) {
                    month++;
                } else {
                    year++;
                    month = 1;
                }
                tvSimulationData.setText(year + "-" + (month > 9 ? ("" + month) : ("0" + month)));
                initialCurrentTime();
            }
        });
        tvSimulationData = (TextView)findViewById(R.id.tv_simulation_data);
        tvSimulationData.setText(currentDate(System.currentTimeMillis()));
    }

    private String currentDate(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        Date date = new Date(time);
        String str = simpleDateFormat.format(date);
        return str;
    }

    private void initialCurrentTime() {
        String str = currentDate(System.currentTimeMillis());
        String[] array = str.split("-");
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 0,0,0,0);
        currentTime = (int)(calendar.getTimeInMillis() / 1000);
    }

    private int currentMonthHaveHowMuchDays() {
        Calendar c= Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH,month - 1);//注意一定要写5，不要写6！Calendar.MONTH是从0到11的！
        int n = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        return n;
    }

    private void getDayData(int startTime) {
        mMap.clear();
        mRealm = Realm.getDefaultInstance();
        RealmResults<RecordModel> list = mRealm.where(RecordModel.class)
                .greaterThan("time", startTime)
                .lessThan("time", startTime + 24 * 60 * 60 * currentMonthHaveHowMuchDays())
                .findAll().sort("time", Sort.ASCENDING);
        for (RecordModel model: list) {
            if (mMap.containsKey((Integer) (model.getTime() / 60))) {
                List<RecordModel> temlist = mMap.get((Integer) (model.getTime() / 60));
                temlist.add(model);
                mMap.put((Integer) (model.getTime() / 60), temlist);
            } else {
                List<RecordModel> temlist = new ArrayList<RecordModel>();
                temlist.add(model);
                mMap.put((Integer) (model.getTime() / 60), temlist);
            }
        }
        refreshSleepStatisticsUI();
    }

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        handler.removeMessages(1);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {

        }
    };

    private void initialText() {
        tvSleepAverageTime = (TextView)findViewById(R.id.tv_sleep_average_time);
        tvTime1 = (TextView)findViewById(R.id.tv_time_1);
        tvTime2 = (TextView)findViewById(R.id.tv_time_2);
        tvTime3 = (TextView)findViewById(R.id.tv_time_3);
        tvTime4 = (TextView)findViewById(R.id.tv_time_4);
        tvTime5 = (TextView)findViewById(R.id.tv_time_5);
        tvTime6 = (TextView)findViewById(R.id.tv_time_6);

        tvSleepTime4 = (TextView)findViewById(R.id.tv_sleep_time_4);
        tvSleepTime5 = (TextView)findViewById(R.id.tv_sleep_time_5);
        ivSleepTime4 = (ImageView)findViewById(R.id.iv_time_4);
        ivSleepTime5 = (ImageView)findViewById(R.id.iv_time_5);
        if (bBelt == false) {
            refreshSleepStatistic(false);
        }
    }

    private void initializeForChart() {
        chart = findViewById(R.id.chart1);
        // background color
        chart.setBackgroundColor(getResources().getColor(R.color.tranparencyColor));
        // disable description text
        chart.getDescription().setEnabled(false);
        // enable touch gestures
        chart.setTouchEnabled(true);
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

        XAxis xAxis = chart.getXAxis();
        xAxis.setTextColor(getResources().getColor(R.color.color_B2C1E0));
        // vertical grid lines
        // xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setAxisMaximum(30f);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(getResources().getColor(R.color.color_B2C1E0));
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "" + (int)value;
            }
        });

        YAxis yAxis = chart.getAxisLeft();
        // disable dual axis (only use LEFT axis)
        chart.getAxisRight().setEnabled(false);
        yAxis.setAxisMaximum(18f);
        yAxis.setAxisMinimum(0f);
        yAxis.setLabelCount(6);
        yAxis.setGranularityEnabled(true);
        yAxis.setGranularity(3f);
        yAxis.setTextColor(getResources().getColor(R.color.color_B2C1E0));
        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value <= 15) {
                    return "" + (int)value;
                }
                return "";
            }
        });
        yAxis.setTextColor(getResources().getColor(R.color.color_B2C1E0));

        yAxis.setDrawLimitLinesBehindData(true);
        xAxis.setDrawLimitLinesBehindData(true);

        setCharData();

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        l.setEnabled(false);
    }

    private void setCharData() {
        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < currentMonthHaveHowMuchDays(); i++) {
            if (sleepOneDayTimes[i] > 0) {
                values.add(new Entry(i, sleepOneDayTimes[i] / 60));
            }
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
            set1.setColor(getResources().getColor(R.color.colorWhite));
            set1.setCircleColor(getResources().getColor(R.color.color_5DF2FF));

            // line thickness and point size
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);

            // draw points as solid circles
            set1.setDrawCircleHole(false);

            // customize legend entry
            set1.setFormLineWidth(1f);
            set1.setFormSize(15.f);
            set1.setDrawValues(false);
            // text size of values
            set1.setValueTextSize(9f); // 值大小
            set1.setDrawCircles(true);

            // set the filled area
            set1.setDrawFilled(false); // 提充背景色
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });

            set1.setMode(LineDataSet.Mode.LINEAR);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets

            // create a data object with the data sets
            LineData data = new LineData(dataSets);
            // set data
            chart.setData(data);
        }
    }

    private void initialCalendarView() {
        calendarView = (CalendarView)findViewById(R.id.calendarView);
        calendarView.setEnabled(false);
        calendarView.setClickable(false);
        calendarView.setMonthViewScrollable(false);
        calendarView.setWeekViewScrollable(false);
        calendarView.getMonthViewPager().setClickable(false);
        calendarView.getWeekViewPager().setClickable(false);
        calendarView.clearSchemeDate();
        calendarView.clearSingleSelect();
        calendarView.getMonthViewPager().setClickable(false);
        calendarView.setClickable(false);
        calendarView.setOnClickListener(null);
        calendarView.getMonthViewPager().setOnClickListener(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            calendarView.getMonthViewPager().setOnContextClickListener(null);
        }
    }

    public void refreshSleepStatistic(boolean isBlet) {
        bBelt = isBlet;
        if (tvSleepTime4 == null) {
            return;
        }
        if (isBlet) {
            tvSleepTime4.setText(R.string.report_heart_rate);
            tvSleepTime5.setText(R.string.report_respiratory_rate_aver);
            String unit4 = getResources().getString(R.string.common_times_minute);
            String[] array4 = {unit4};
            String content4 = "00" + unit4;
            tvTime4.setText(BigSmallFontManager.createTimeValue(content4, getActivity(), 13, array4));
            tvTime5.setText(BigSmallFontManager.createTimeValue(content4, getActivity(), 13, array4));
            ivSleepTime4.setImageResource(R.mipmap.report_icon_heart_rate);
            ivSleepTime5.setImageResource(R.mipmap.report_icon_respiratory_rate);
        } else {
            tvSleepTime4.setText(R.string.common_body_moves);
            tvSleepTime5.setText(R.string.common_snoring_times);
            String unit4 = getResources().getString(R.string.common_times);
            String[] array4 = {unit4};
            String content4 = "00" + unit4;
            tvTime4.setText(R.string.common_many);
            tvTime5.setText(BigSmallFontManager.createTimeValue(content4, getActivity(), 13, array4));
            ivSleepTime4.setImageResource(R.mipmap.icon_body_movement);
            ivSleepTime5.setImageResource(R.mipmap.icon_snore);
        }
    }

    // 刷新统计时间相关数据UI
    private void refreshSleepStatisticsUI() {
        long averageHeart = 0;
        long averageBreath = 0;
        if (mMap.size() > 0) {
            for (Map.Entry<Integer, List<RecordModel>> entry: mMap.entrySet()) {
                for (RecordModel model: entry.getValue()) {
                    if (averageHeart == 0) {
                        averageHeart = model.getHeartRate();
                    } else {
                        averageHeart = (averageHeart + model.getHeartRate()) / 2;
                    }
                    if (averageBreath == 0) {
                        averageBreath = model.getBreathRate();
                    } else {
                        averageBreath = (averageBreath + model.getBreathRate()) / 2;
                    }
                }
            }
        }

        if (tvTime4 != null && tvTime5 != null) {
            String unit4 = getResources().getString(R.string.common_times_minute);
            String[] array4 = {unit4};
            String content4 = averageHeart > 9 ? "" + averageHeart : "0" + averageHeart  + unit4;
            tvTime4.setText(BigSmallFontManager.createTimeValue(content4, getActivity(), 13, array4));
            String content5 = averageBreath > 9 ? "" + averageBreath : "0" + averageBreath  + unit4;
            tvTime5.setText(BigSmallFontManager.createTimeValue(content5, getActivity(), 13, array4));
        }
        if (tvTime2 != null) {
            String unit21 = getResources().getString(R.string.common_hour);
            String unit22 = getResources().getString(R.string.common_minute);
            String[] array2 = {unit21, unit22};
            int hour = mMap.size() / 60;
            int minute = mMap.size() % 60;
            String content2 = (hour > 9 ? "" + hour : "0" + hour) + unit21 + (minute > 9 ? "" + minute : "0" + minute) + unit22;
            tvTime2.setText(BigSmallFontManager.createTimeValue(content2, getActivity(), 13, array2));
        }

        refreshStartOrEndSleepUI();
        if (chart == null) {
            return;
        }
        setCharData();

        calculateSleepValue();
    }

    private void refreshStartOrEndSleepUI() {
        if (tvTime1 == null) {
            return;
        }
        RealmResults<AlarmModel> userList = mRealm.where(AlarmModel.class).findAll();
        if (userList != null && userList.size() > 0) {
            int sleepH = 0, sleepM = 0;
            for (AlarmModel model : userList) {
                if (model.getType() == 0) {

                } else {
                    sleepH = model.getHour();
                    sleepM = model.getMinute();
                }
            }
            String unit11 = getResources().getString(R.string.common_hour2);
            String unit12 = getResources().getString(R.string.common_minute3);
            String[] array1 = {unit11, unit12};
            if (sleepH <= 0 && sleepM <= 0) {
                String content1 = "00" + unit11 + "00" + unit12;
                tvTime1.setText(BigSmallFontManager.createTimeValue(content1, getActivity(), 13, array1));
            } else {
                String content1 = (sleepH > 9 ? "" + sleepH : "0" + sleepH) + unit11 + (sleepM > 9 ? "" + sleepM : "0" + sleepM) + unit12;
                tvTime1.setText(BigSmallFontManager.createTimeValue(content1, getActivity(), 13, array1));
            }
        }
    }

    // 供外表调用，刷新UI
    public void refreshAllUI() {
        getDayData(currentTime);
    }


    // 计算熟睡、中睡，浅睡、清醒
    private void calculateSleepValue() {
        if (mMap.size() > 0) {
            int deepSleep = 0;
            int middleSleep = 0;
            int cheapSleep = 0;
            int getup = 0;
            int[] times = new int[31];
            int bodyMotionCount = 0;
            int getupCount = 0;
            for (Map.Entry<Integer, List<RecordModel>> entry : mMap.entrySet()) {
                for (int i = 0; i < currentMonthHaveHowMuchDays(); i++) {
                    if (entry.getKey() < currentTime / 60 + 24 * 60 * (i + 1)) {
                        if (times[i] == 0) { // 如果时间没赋过值，则将第一个数据赋值给它
                            deepSleep = 0;
                            times[i] = entry.getKey() + 5;
                            getupCount = 0;
                            bodyMotionCount = 0;
                        }
                        if (entry.getKey() <= times[i]) {

                        } else {
                            times[i] += 5;
                            if (getupCount > 0) {
                                getup += 1;
                            } else {
                                if (bodyMotionCount > 6) {
                                    getup += 1;
                                } else if (bodyMotionCount >= 3) {
                                    cheapSleep += 1;
                                } else if (bodyMotionCount >= 1) {
                                    middleSleep += 1;
                                } else {
                                    deepSleep += 1;
                                }
                            }
                            getupCount = 0;
                            bodyMotionCount = 0;
                        }
                        sleepOneDayTimes[i] = deepSleep * 5;
                    }
                }

                for (RecordModel model : entry.getValue()) {
                    int a = model.getGetupFlag();
                    int b = model.getBodyMotion();
                    if (a == 0) {
                        getupCount += 1;
                    }
                    if (b > 0) {
                        bodyMotionCount += 1;
                    }
                }

            }
            nosleepTimeTotal = getup * 5;
            nosleepTimeTotal = getup;

            if (tvSleepAverageTime != null) {
                int deepSleepAvg = 0;
                for (int i = 0; i < sleepOneDayTimes.length; i++) {
                    deepSleepAvg += sleepOneDayTimes[i];
                }
                String unit01 = getResources().getString(R.string.common_hour);
                String unit02 = getResources().getString(R.string.common_minute);
                String[] array1 = {unit01, unit02};
                int hour = deepSleepAvg / 7 / 60;
                int minute = deepSleepAvg / 7 % 60;
                String content1 = (hour > 9 ? "" + hour : "0" + hour) + unit01 + (minute > 9 ? "" + minute : "0" + minute) + unit02;
                tvSleepAverageTime.setText(BigSmallFontManager.createTimeValue(content1, getActivity(), 13, array1));
            }

            if (tvTime3 != null && tvTime6 != null) {
                String unit21 = getResources().getString(R.string.common_hour);
                String unit22 = getResources().getString(R.string.common_minute);
                String[] array2 = {unit21, unit22};
                int hour = nosleepTimeTotal / 60;
                int minute = nosleepTimeTotal % 60;
                String content2 = (hour > 9 ? "" + hour : "0" + hour) + unit21 + (minute > 9 ? "" + minute : "0" + minute) + unit22;
                tvTime3.setText(BigSmallFontManager.createTimeValue(content2, getActivity(), 13, array2));
                String unit6 = getResources().getString(R.string.common_times);
                String[] array6 = {unit6};
                String content6 = noSleepMinuteCount > 9 ? "" + noSleepMinuteCount : "0" + noSleepMinuteCount + unit6;
                tvTime6.setText(BigSmallFontManager.createTimeValue(content6, getActivity(), 13, array6));
            }

        } else {
            if (tvTime3 != null && tvTime6 != null) {
                String unit21 = getResources().getString(R.string.common_hour);
                String unit22 = getResources().getString(R.string.common_minute);
                String[] array2 = {unit21, unit22};
                String content2 = "00" + unit21 + "00" + unit22;
                tvTime3.setText(BigSmallFontManager.createTimeValue(content2, getActivity(), 13, array2));
                String unit6 = getResources().getString(R.string.common_times);
                String[] array6 = {unit6};
                String content6 = "00" + unit6;
                tvTime6.setText(BigSmallFontManager.createTimeValue(content6, getActivity(), 13, array6));
            }
        }

    }
}
