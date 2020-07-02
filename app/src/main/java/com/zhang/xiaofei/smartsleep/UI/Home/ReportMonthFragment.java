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
import com.github.mikephil.charting.data.MLineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.haibin.calendarview.CalendarView;
import com.shizhefei.fragment.LazyFragment;
import com.zhang.xiaofei.smartsleep.Kit.BigSmallFont.BigSmallFontManager;
import com.zhang.xiaofei.smartsleep.Kit.DisplayUtil;
import com.zhang.xiaofei.smartsleep.Model.Alarm.AlarmModel;
import com.zhang.xiaofei.smartsleep.Model.Record.HistoryBreath;
import com.zhang.xiaofei.smartsleep.Model.Record.RecordModel;
import com.zhang.xiaofei.smartsleep.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
    private View vCalendarViewCover;
    private int currentTime = 0;
    private int year = 0;
    private int month = 0;
    private Boolean bBelt = true; // 刷新睡眠带，睡眠纽扣
    int[] sleepOneDayTimes = new int[31]; // 深睡眠时长
    int[] scores = new int[31];
    int nosleepTimeTotal = 0; // 清醒时长
    int noSleepMinuteCount = 0; // 清醒次数
    int startSleepTime = 0; // 开始入睡
    int sleepTotalTime = 0; // 睡眠总时长
    int heartAvarage = 0; // 平均心跳
    int breathAvarage = 0; // 平均呼吸率
    MLineDataSet set1;
    boolean bChart = false;
    List<List<String>> sleepTimes = new ArrayList<>() ;
    ReportDataCalculater calculater = new ReportDataCalculater();

    @Override
    protected View getPreviewLayout(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.layout_preview, container, false);
    }

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_tabmain_item3);
        vCalendarViewCover = findViewById(R.id.v_calendarview_cover);
        vCalendarViewCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        initializeForChart(); // 睡眠质量初始化
        initialText();
        initialCalendarView();
        year = calendarView.getCurYear();
        month = calendarView.getCurMonth();
        initialCurrentTime();
        readSleepAndGetupData();
        getMonthData();
        ibLeftPre = (ImageButton)findViewById(R.id.ib_left_pre);
        ibLeftPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.scrollToPre();
                calendarView.clearSchemeDate();
                calendarView.clearSingleSelect();
                if (month > 1) {
                    month--;
                } else {
                    year--;
                    month = 12;
                }
                tvSimulationData.setText(year + "-" + (month > 9 ? ("" + month) : ("0" + month)));
                initialCurrentTime();
                readSleepAndGetupData();
                getMonthData();
            }
        });
        ibRightNex = (ImageButton)findViewById(R.id.ib_right_next);
        ibRightNex.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                calendarView.scrollToNext();
                calendarView.clearSchemeDate();
                calendarView.clearSingleSelect();
                if (month < 11) {
                    month++;
                } else {
                    year++;
                    month = 1;
                }
                tvSimulationData.setText(year + "-" + (month > 9 ? ("" + month) : ("0" + month)));
                initialCurrentTime();
                readSleepAndGetupData();
                getMonthData();
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

    private String currentDateTime(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(time);
        String str = simpleDateFormat.format(date);
        return str;
    }

    // 将时间转换为秒制
    private long timeToLong(String value) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = simpleDateFormat.parse(value);
            return date.getTime() / 1000;
        } catch (ParseException exception) {

        }
        return 0;
    }

    // 将时间转换为秒制
    private long timeToLongB(String value) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = simpleDateFormat.parse(value);
            return date.getTime() / 1000;
        } catch (ParseException exception) {

        }
        return 0;
    }

    // 将时间字符串转换为小时和分钟
    private int hourMinuteToInt(String value) {
        if (value.length() == 0) {
            return 0;
        }
        String hourMinute = value.substring(11);
        String[] array = hourMinute.split(":");
        return Integer.parseInt(array[0]) * 60 + Integer.parseInt(array[1]);
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

    private void readSleepAndGetupData() {
        sleepTimes.clear(); // 删除所有数据
        for (int i = 0; i < currentMonthHaveHowMuchDays(); i++) {
            List<String> list = new ArrayList<>();
            sleepTimes.add(list);
        }
        if (SleepAndGetupTimeManager.times.size() == 0) {
            return;
        }
        String[] days = SleepAndGetupTimeManager.times.keySet().toArray(new String[0]);
        Arrays.sort(days);
        for (int i = 0; i < days.length; i++) {
            if (timeToLong(days[i]) < currentTime) {
                continue;
            }
            int j = (int) ((timeToLong(days[i]) - currentTime) / (24 * 60 * 60));
            if (j < 0 || j >= sleepTimes.size()) {
                continue;
            }
            List<String> list = sleepTimes.get(j);
            list.addAll(SleepAndGetupTimeManager.times.get(days[i]));
            sleepTimes.set(j, list);
            System.out.println("年份：" + year + "月份：" + month + "当前月份第" + j + "天有" + list.size() + "段数据");
        }
    }

    private void getMonthData() {
        int count = 0;
        int heart = 0;
        int breath = 0;
        int startSleep = 0;
        sleepTotalTime = 0;
        noSleepMinuteCount = 0;
        nosleepTimeTotal = 0;
        heartAvarage = 0;
        breathAvarage = 0;
        startSleepTime = 0;
        for (int j = 0; j < currentMonthHaveHowMuchDays(); j++) {
            List<String> list = sleepTimes.get(j);
            if (list.size() > 0) {
                int score = 0;
                int deepSleep = 0;
                for (int i = 0; i < list.size(); i++) {
                    String duration = list.get(i);
                    String sleepTime = duration.split("&")[0];
                    String getupTime = duration.split("&")[1];
                    calculater.readDataFromDB(sleepTime, getupTime);
                    int[] array = calculater.calculateSleepValue(sleepTime, getupTime);
                    if (array[0] + array[1] + array[2] + array[3] > 0) {
                        int totalTime = array[0] + array[1] + array[2] + array[3];
                        int grade = 0;
                        if (totalTime > 10 * 60 * 60) {
                            grade = 100 * array[0] / totalTime;
                        } else if (totalTime < 7 * 60 * 60) {
                            grade = 90 * array[0] / totalTime;
                        } else {
                            grade = Math.min(120 * array[0] / totalTime, 100);
                        }
                        score += grade;
                        deepSleep += array[0] / 60; // 深睡眠时长
                        noSleepMinuteCount += array[5]; // 清醒次数，起床次数
                        nosleepTimeTotal += array[3] / 60;
                        count += 1;
                        heart += array[6];
                        breath += array[7];
                        startSleep += hourMinuteToInt(sleepTime);
                    }
                    sleepTotalTime += (timeToLongB(getupTime) - timeToLongB(sleepTime)) / 60;

                }
                scores[j] = score / list.size();
                sleepOneDayTimes[j] = deepSleep;
            } else {
                scores[j] = 0;
                sleepOneDayTimes[j] = 0;
            }
        }
        if (count > 0) {
            heartAvarage = heart / count;
            breathAvarage = breath / count;
            startSleepTime = startSleep / count;
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
        System.out.println("当前月数据：" + values.size());
        if (bChart) {
            set1.setValues(values);
            set1.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
            return;
        }
        bChart = true;
        set1 = new MLineDataSet(values, "");

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
        calendarView.getMonthViewPager().setLongClickable(false);
        calendarView.setClickable(false);
        calendarView.setOnClickListener(null);
        calendarView.getMonthViewPager().setOnClickListener(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            calendarView.getMonthViewPager().setOnContextClickListener(null);
        }
        calendarView.setOnTouchListener(null);
        calendarView.setLongClickable(false);
        calendarView.setTouchDelegate(null);
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
        if (tvTime4 != null && tvTime5 != null) {
            String unit4 = getResources().getString(R.string.common_times_minute);
            String[] array4 = {unit4};
            String content4 = (heartAvarage > 9 ? "" + heartAvarage : "0" + heartAvarage)  + unit4;
            tvTime4.setText(BigSmallFontManager.createTimeValue(content4, getActivity(), 13, array4));
            String content5 = (breathAvarage > 9 ? "" + breathAvarage : "0" + breathAvarage)  + unit4;
            tvTime5.setText(BigSmallFontManager.createTimeValue(content5, getActivity(), 13, array4));
        }
        if (tvTime2 != null) {
            String unit21 = getResources().getString(R.string.common_hour);
            String unit22 = getResources().getString(R.string.common_minute);
            String[] array2 = {unit21, unit22};
            int hour = sleepTotalTime / 60;
            int minute = sleepTotalTime % 60;
            String content2 = (hour > 9 ? "" + hour : "0" + hour) + unit21 + (minute > 9 ? "" + minute : "0" + minute) + unit22;
            tvTime2.setText(BigSmallFontManager.createTimeValue(content2, getActivity(), 13, array2));
        }

        refreshStartOrEndSleepUI();
        if (chart == null) {
            return;
        }
        setCharData();
        chart.invalidate();
        calculateSleepValue();
        refreshCalenderView();
    }

    private void refreshStartOrEndSleepUI() {
        if (tvTime1 == null) {
            return;
        }
        String unit11 = getResources().getString(R.string.common_hour2);
        String unit12 = getResources().getString(R.string.common_minute3);
        String[] array1 = {unit11, unit12};
        if (startSleepTime > 0) {
            int sleepH = startSleepTime / 60;
            int sleepM = startSleepTime % 60;
            if (sleepH <= 0 && sleepM <= 0) {
                String content1 = "00" + unit11 + "00" + unit12;
                tvTime1.setText(BigSmallFontManager.createTimeValue(content1, getActivity(), 13, array1));
            } else {
                String content1 = (sleepH > 9 ? "" + sleepH : "0" + sleepH) + unit11 + (sleepM > 9 ? "" + sleepM : "0" + sleepM) + unit12;
                tvTime1.setText(BigSmallFontManager.createTimeValue(content1, getActivity(), 13, array1));
            }
        } else {
            String content1 = "00" + unit11 + "00" + unit12;
            tvTime1.setText(BigSmallFontManager.createTimeValue(content1, getActivity(), 13, array1));
        }
    }

    // 供外表调用，刷新UI
    public void refreshAllUI() {
        readSleepAndGetupData();
        getMonthData();
    }


    // 计算熟睡、中睡，浅睡、清醒
    private void calculateSleepValue() {
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
            String content6 = (noSleepMinuteCount > 9 ? "" + noSleepMinuteCount : "0" + noSleepMinuteCount) + unit6;
            tvTime6.setText(BigSmallFontManager.createTimeValue(content6, getActivity(), 13, array6));
        }

    }

    private void refreshCalenderView() {
        Map<String, com.haibin.calendarview.Calendar> map = new HashMap<>();
        for (int i = 0; i < currentMonthHaveHowMuchDays(); i++) {
            if (scores[i] >= 70) {
                String date = currentDateTime((long)(currentTime + i * 24 * 60 * 60) * 1000);
                String[] array = date.split("-");
                if (array.length == 3) {
                    int year = Integer.parseInt(array[0]);
                    int month = Integer.parseInt(array[1]);
                    int day = Integer.parseInt(array[2]);
                    int color = 0;
                    if (scores[i] >= 85) {
                        color = 0xFF6EE1CA;
                    } else if (scores[i] >= 75) {
                        color = 0xFF499BE5;
                    } else {
                        color = 0xFF626AEA;
                    }
                    map.put(getSchemeCalendar(year, month, day, color).toString(),
                            getSchemeCalendar(year, month, day, color));
                }
            }
        }
        if (map.size() > 0) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    calendarView.setSchemeDate(map);
                }
            });
        }
    }

    private com.haibin.calendarview.Calendar getSchemeCalendar(int year, int month, int day, int color) {
        com.haibin.calendarview.Calendar calendar = new com.haibin.calendarview.Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        return calendar;
    }
}
