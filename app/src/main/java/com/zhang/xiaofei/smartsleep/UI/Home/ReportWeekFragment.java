package com.zhang.xiaofei.smartsleep.UI.Home;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.github.mikephil.charting.data.MLineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shizhefei.fragment.LazyFragment;
import com.zhang.xiaofei.smartsleep.Kit.Application.LogHelper;
import com.zhang.xiaofei.smartsleep.Kit.BigSmallFont.BigSmallFontManager;
import com.zhang.xiaofei.smartsleep.Kit.DisplayUtil;
import com.zhang.xiaofei.smartsleep.Model.Alarm.AlarmModel;
import com.zhang.xiaofei.smartsleep.Model.Record.RecordModel;
import com.zhang.xiaofei.smartsleep.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ReportWeekFragment extends LazyFragment {

    private LineChart chart; // 睡眠质量分析
    private LineChart chart2; // 翻身
    private TextView tvSleepValue;
    private TextView tvSleepAverageTime;
    private ConstraintLayout cl8;
    private TextView tvTime2; // 入睡时长
    private TextView tvTime4; // 平均心率
    private TextView tvTime5; // 平均呼吸率
    private TextView tvSleepTime4;
    private TextView tvSleepTime5;
    private ImageView ivSleepTime4;
    private ImageView ivSleepTime5;
    private ImageButton ibLeftPre;
    private ImageButton ibRightNex;
    private TextView tvSimulationData;
    private TextView tvSleepRank;
    private int currentTime = 0;
    MLineDataSet set1;
    LineDataSet set2;
    ArrayList<Entry> set1values;
    private Boolean bBelt = true; // 刷新睡眠带，睡眠纽扣
    int[] scores = new int[7]; // 得分
    int[] sleepOneDayTimes = new int[7]; // 深睡眠时长
    int nosleepTimeTotal = 0; // 清醒时长
    int noSleepMinuteCount = 0; // 清醒次数
    int startSleepTime = 0; // 开始入睡
    int sleepTotalTime = 0; // 睡觉总时长
    int heartAvarage = 0; // 平均心跳
    int breathAvarage = 0; // 平均呼吸率
    boolean bChart = false; //
    boolean bChart2 = false;
    private int sleepDataSize = 0;
    private int sleepTimesTotal = 0; // 总睡眠时长
    List<List<String>> sleepTimes = new ArrayList<>() ;
    ReportDataCalculater calculater = new ReportDataCalculater();

    @Override
    protected View getPreviewLayout(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.layout_preview, container, false);
    }

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_tabmain_item2);
        cl8 = (ConstraintLayout)findViewById(R.id.cl_8);
        initializeForChart(); // 睡眠质量初始化
        initializeForChart2();
        tvSleepValue = (TextView)findViewById(R.id.tv_sleep_value);
        tvSleepRank = (TextView)findViewById(R.id.tv_sleep_week_value);
        tvSleepAverageTime = (TextView)findViewById(R.id.tv_sleep_average_time);

        initialText();

        initialCurrentTime();
        readSleepAndGetupData();
        getWeekData();

        ibLeftPre = (ImageButton)findViewById(R.id.ib_left_pre);
        ibLeftPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("上一步");
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                currentTime -= 7 * 24 * 60 * 60;
                tvSimulationData.setText(getTextValue());
                readSleepAndGetupData();
                getWeekData();
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
        ibRightNex = (ImageButton)findViewById(R.id.ib_right_next);
        ibRightNex.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                currentTime += 7 * 24 * 60 * 60;
                tvSimulationData.setText(getTextValue());
                readSleepAndGetupData();
                getWeekData();
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
        tvSimulationData = (TextView)findViewById(R.id.tv_simulation_data);
        tvSimulationData.setText(getTextValue());


        addDots();
    }

    private String currentDate(long time) {
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
        calendar.set(Integer.parseInt(array[0]), Integer.parseInt(array[1]) - 1, Integer.parseInt(array[2]),0,0,0);
        int firstDayOfWeek = calendar.getFirstDayOfWeek();
        calendar.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);

        String strB = currentDate(calendar.getTimeInMillis());
        String[] arrayB = strB.split("-");
        Calendar calendarB = Calendar.getInstance();
        calendarB.set(Integer.parseInt(arrayB[0]), Integer.parseInt(arrayB[1]) - 1, Integer.parseInt(arrayB[2]),0,0,0);
        System.out.println("当前时间：" + currentTime);
        currentTime = (int)(calendarB.getTimeInMillis() / 1000);
    }

    private void readSleepAndGetupData() {
        sleepTimes.clear(); // 删除所有数据
        for (int i = 0; i < 7; i++) {
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
        }
        System.out.println("sleepTimes打印: " + LogHelper.ListToString(sleepTimes));
    }

    private void getWeekData() {
        sleepDataSize = 0;
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
        sleepTimesTotal = 0;
        for (int j = 0; j < 7; j++) {
            List<String> list = sleepTimes.get(j);
            sleepDataSize += list.size();
            if (list.size() > 0) {
                int score = 0;
                int deepSleep = 0;
                int haveGradeCount = 0;
                for (int i = 0; i < list.size(); i++) {
                    String duration = list.get(i);
                    String sleepTime = duration.split("&")[0];
                    String getupTime = duration.split("&")[1];
                    calculater.readDataFromDB(sleepTime, getupTime);
                    int[] array = calculater.calculateSleepValue(sleepTime, getupTime);
                    if (array[0] + array[1] + array[2] + array[3] > 0) {
                        int sleepB = hourMinuteToInt(sleepTime);
                        haveGradeCount += 1;
                        int totalTime = array[0] + array[1] + array[2] + array[3];
                        int deep =  array[0] * 100 / totalTime;
                        int middle = array[1] * 100 / totalTime;
                        int cheap = array[2] * 100 / totalTime;
                        int get = 100 - deep - middle - cheap;
                        sleepTimesTotal += totalTime;
                        float d = ((float)array[0]) / ((float)totalTime);
                        float d2 =  Math.abs((float)0.25 - d);
                        int d3 =  (int) (d2 * 100 * 0.5);
                        int d4 = (int) (array[10] * 0.5);
                        int grade = 0;
                        int c1 = 0;
                        if (totalTime > 10 * 60 * 60) {
                            c1 = totalTime / 3600 - 10;
                            grade = Math.max(93 - 3 * c1 - d3 - d4 - get, 20);
                        } else if (totalTime < 7 * 60 * 60) {
                            c1 = 7 - totalTime / 3600;
                            grade = Math.max(88 - 8 * c1 - d3 - d4 - get, 30);
                        } else {
                            grade = Math.max(100 - d3 - d4 - get, 40);
                        }
                        if (sleepB > 23 * 60 + 30) {
                            int value = 23 * 60 + 30 - sleepB;
                            grade -= value / 10;
                        }
                        if (sleepB < 12 * 60) {
                            int value = sleepB + 30;
                            grade -= value / 10;
                        }
                        System.out.println("打印计算的值周：" + grade + " " + c1 + " " + d3 + " " + d4 + " " + get);
//                        grade -= array[8] / 3600 * 2;
//                        grade -= array[9] / 3600 * 2;
                        grade = Math.max(0, grade);
                        grade = Math.min(100, grade);
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
                scores[j] = haveGradeCount > 0 ? score / haveGradeCount : 0;
                scores[j] = Math.min(100, scores[j]);
                System.out.println("周" + j + "得分" + scores[j]);
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

    private String getTextValue() {
        String value = "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis((long)currentTime * 1000);
        int firstDayOfWeek = calendar.getFirstDayOfWeek();
        calendar.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
        value += new SimpleDateFormat("MM-dd").format(calendar.getTime());
        value += " ";
        calendar.set(Calendar.DAY_OF_WEEK, firstDayOfWeek + 6);
        value += new SimpleDateFormat("MM-dd").format(calendar.getTime());
        return value;
    }

    @Override
    protected void onPauseLazy() {
        super.onPauseLazy();
        System.out.println("Report Week OnPause");
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
        System.out.println("周报初始化UI");
        tvSleepAverageTime = (TextView)findViewById(R.id.tv_sleep_average_time);
        String unit01 = getResources().getString(R.string.common_hour);
        String unit02 = getResources().getString(R.string.common_minute);
        String[] array = {unit01, unit02};
        String content = "00" + unit01 + "00" + unit02;
        tvSleepAverageTime.setText(BigSmallFontManager.createTimeValue(content, getActivity(), 20, array));
        tvTime2 = (TextView)findViewById(R.id.tv_time_2);
        tvTime4 = (TextView)findViewById(R.id.tv_time_4);
        tvTime5 = (TextView)findViewById(R.id.tv_time_5);

        tvSleepTime4 = (TextView)findViewById(R.id.tv_sleep_time_4);
        tvSleepTime5 = (TextView)findViewById(R.id.tv_sleep_time_5);
        ivSleepTime4 = (ImageView)findViewById(R.id.iv_time_4);
        ivSleepTime5 =(ImageView)findViewById(R.id.iv_time_5);
        if (bBelt == false) {
            refreshSleepStatistic(false);
        }

    }

    public void refreshSleepStatistic(boolean isBlet) {
        System.out.println("周报中刷新UI:" + isBlet);
        bBelt = isBlet;
        if (tvSleepTime4 == null) {
            System.out.println("UI控件为空");
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

    private void initializeForChart() {
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
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        // chart.setScaleXEnabled(true);
        // chart.setScaleYEnabled(true);
        // force pinch zoom along both axis
        chart.setPinchZoom(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setTextColor(getResources().getColor(R.color.color_B2C1E0));
        xAxis.setAxisMaximum(8f);
        xAxis.setAxisMinimum(0f);
        xAxis.setLabelCount(8);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                switch ((int)value) {
                    case 1:
                        return getResources().getString(R.string.middle_mon);
                    case 2:
                        return getResources().getString(R.string.middle_tue);
                    case 3:
                        return getResources().getString(R.string.middle_wed);
                    case 4:
                        return getResources().getString(R.string.middle_thu);
                    case 5:
                        return getResources().getString(R.string.middle_fri);
                    case 6:
                        return getResources().getString(R.string.middle_sat);
                    case 7:
                        return getResources().getString(R.string.middle_sun);
                    default:
                        return "";

                }
            }
        });

        YAxis yAxis = chart.getAxisLeft();
        // disable dual axis (only use LEFT axis)
        chart.getAxisRight().setEnabled(false);
        // horizontal grid lines
        //yAxis.enableGridDashedLine(10f, 10f, 0f);
        // axis range
        yAxis.setAxisMaximum(120f);
        yAxis.setAxisMinimum(0f);
        yAxis.setLabelCount(6);
        yAxis.setGranularityEnabled(true);
        yAxis.setGranularity(20f);
        yAxis.setTextColor(getResources().getColor(R.color.color_B2C1E0));
        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value <= 100) {
                    return "" + (int)value;
                }
                return "";
            }
        });

        yAxis.setDrawLimitLinesBehindData(false);
        xAxis.setDrawLimitLinesBehindData(false);

        setCharData();

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        l.setEnabled(false);
    }

    private void setCharData() {
        set1values = new ArrayList<>();

        if (scores.length > 0) {
            int value = 0;
            for (int i = 0; i < scores.length; i++) {
                if (scores[i] > 0) {
                    value = scores[i];
                    set1values.add(new Entry(i,scores[i]));
                }
            }
            System.out.println("获取到数据的数量为：" + set1values.size() + " " + value);
        }

        if (bChart) {
            set1.setValues(set1values);
            set1.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
            return;
        }
        bChart = true;
        // create a dataset and give it a type
        set1 = new MLineDataSet(set1values, "");

        set1.setDrawIcons(false);
        set1.setDrawHorizontalHighlightIndicator(false);
        set1.setDrawVerticalHighlightIndicator(false);

        // black lines and points
        set1.setColor(getResources().getColor(R.color.colorWhite));
        set1.setCircleColor(0xFF5DF2FF);
        //set1.setCircleColors(0xFF5DF2FF, 0x626AEA, 0xFF499BE5, 0xFFF3D032, 0xFFE92C2C);
        // 6EE1CA 100分 / 499BE5 80分 / 626AEA 60分 / F3D032 40分 / E92C2C 20分
        // line thickness and point size
        set1.setLineWidth(1f);
        // draw points as solid circles
        set1.setDrawCircleHole(false);
        set1.setCircleRadius(3f);

        // customize legend entry
        set1.setFormLineWidth(1f);
        set1.setFormSize(15.f);
        set1.setDrawValues(false);
        // text size of values
        set1.setValueTextSize(9f);
        set1.setDrawCircles(true);

        // set the filled area
        set1.setDrawFilled(false);
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

    private void initializeForChart2() {
        chart2 = findViewById(R.id.chart2);
        // background color
        chart2.setBackgroundColor(getResources().getColor(R.color.tranparencyColor));
        // disable description text
        chart2.getDescription().setEnabled(false);
        // enable touch gestures
        chart2.setTouchEnabled(true);
        // set listeners
        //chart.setOnChartValueSelectedListener(this);
        chart2.setDrawGridBackground(false);
        // enable scaling and dragging
        chart2.setDragEnabled(false);
        chart2.setScaleEnabled(false);
        // chart.setScaleXEnabled(true);
        // chart.setScaleYEnabled(true);
        // force pinch zoom along both axis
        chart2.setPinchZoom(false);

        MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.layout_customb_mark_view);
        mv.setChartView(chart2); // For bounds control
        chart2.setMarker(mv); // Set the marker to the chart

        XAxis xAxis = chart2.getXAxis();
        xAxis.setTextColor(getResources().getColor(R.color.color_B2C1E0));
        xAxis.setAxisMaximum(8f);
        xAxis.setAxisMinimum(0f);
        xAxis.setLabelCount(8);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                switch ((int)value) {
                    case 1:
                        return getResources().getString(R.string.middle_mon);
                    case 2:
                        return getResources().getString(R.string.middle_tue);
                    case 3:
                        return getResources().getString(R.string.middle_wed);
                    case 4:
                        return getResources().getString(R.string.middle_thu);
                    case 5:
                        return getResources().getString(R.string.middle_fri);
                    case 6:
                        return getResources().getString(R.string.middle_sat);
                    case 7:
                        return getResources().getString(R.string.middle_sun);
                    default:
                        return "";

                }
            }
        });

        YAxis yAxis = chart2.getAxisLeft();
        // disable dual axis (only use LEFT axis)
        chart2.getAxisRight().setEnabled(false);
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

        setCharData2();

        // get the legend (only possible after setting data)
        Legend l = chart2.getLegend();
        l.setEnabled(false);
    }

    private void setCharData2() {
        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < sleepOneDayTimes.length; i++) {
            if (sleepOneDayTimes[i] > 0) {
                values.add(new Entry(i, sleepOneDayTimes[i] / 60));
            }
        }

        if (bChart2) {
            set2.setValues(values);
            set2.notifyDataSetChanged();
            chart2.getData().notifyDataChanged();
            chart2.notifyDataSetChanged();
            return;
        }
        bChart2 = true;
        // create a dataset and give it a type
        set2 = new LineDataSet(values, "");

        set2.setDrawIcons(false);
        set2.setDrawHorizontalHighlightIndicator(false);
        set2.setDrawVerticalHighlightIndicator(false);

        // black lines and points
        set2.setColor(getResources().getColor(R.color.colorWhite));
        set2.setCircleColor(0xFF5DF2FF);

        // line thickness and point size
        set2.setLineWidth(1f);
        set2.setCircleRadius(3f);

        // draw points as solid circles
        set2.setDrawCircleHole(false);

        // customize legend entry
        set2.setFormLineWidth(1f);
        set2.setFormSize(15.f);
        set2.setDrawValues(false);
        // text size of values
        set2.setValueTextSize(9f);
        set2.setDrawCircles(true);
        // draw selection line as dashed
        //set1.enableDashedHighlightLine(10f, 5f, 0f);

        // set the filled area
        set2.setDrawFilled(false);
        set2.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return chart2.getAxisLeft().getAxisMinimum();
            }
        });

        set2.setMode(LineDataSet.Mode.LINEAR);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set2); // add the data sets

        // create a data object with the data sets
        LineData data = new LineData(dataSets);
        // set data
        chart2.setData(data);
    }

    public void dynamicAddCircleDot(int drawable, int value, int color, float left, float top, int id) {
        RoundedImageView roundedImageView = new RoundedImageView(getActivity());
        roundedImageView.setCornerRadius(DisplayUtil.dip2px(3, getActivity()));
        roundedImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        roundedImageView.setOval(true);
        if (id == 0) {
            roundedImageView.setId(R.id.score_1);
        } else if (id == 1) {
            roundedImageView.setId(R.id.score_2);
        } else if (id == 2) {
            roundedImageView.setId(R.id.score_3);
        } else if (id == 3) {
            roundedImageView.setId(R.id.score_4);
        } else {
            roundedImageView.setId(R.id.score_5);
        }
        roundedImageView.setBackgroundResource(color);
        roundedImageView.mutateBackground(true);
        ConstraintLayout.LayoutParams markLayoutParams = new ConstraintLayout.LayoutParams(
                DisplayUtil.dip2px(6, getActivity()),
                DisplayUtil.dip2px(6, getActivity())
        );
        markLayoutParams.leftToLeft = R.id.cl_8;
        markLayoutParams.topToTop = R.id.cl_8;
        markLayoutParams.leftMargin = DisplayUtil.dip2px(left, getActivity());
        markLayoutParams.topMargin = DisplayUtil.dip2px(top, getActivity());
        cl8.addView(roundedImageView, markLayoutParams);

        MarkView markView = new MarkView(this.getActivity());
        markView.textView.setText("" + value);
        markView.setBackgroundResource(drawable);
        ConstraintLayout.LayoutParams markLayoutParamsB = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        if (id == 0) {
            markLayoutParamsB.leftToLeft = R.id.score_1;
            markLayoutParamsB.rightToRight = R.id.score_1;
            markLayoutParamsB.bottomToTop = R.id.score_1;
        } else if (id == 1) {
            markLayoutParamsB.leftToLeft = R.id.score_2;
            markLayoutParamsB.rightToRight = R.id.score_2;
            markLayoutParamsB.bottomToTop = R.id.score_2;
        } else if (id == 2) {
            markLayoutParamsB.leftToLeft = R.id.score_3;
            markLayoutParamsB.rightToRight = R.id.score_3;
            markLayoutParamsB.bottomToTop = R.id.score_3;
        } else if (id == 3) {
            markLayoutParamsB.leftToLeft = R.id.score_4;
            markLayoutParamsB.rightToRight = R.id.score_4;
            markLayoutParamsB.bottomToTop = R.id.score_4;
        } else if (id == 4) {
            markLayoutParamsB.leftToLeft = R.id.score_5;
            markLayoutParamsB.rightToRight = R.id.score_5;
            markLayoutParamsB.bottomToTop = R.id.score_5;
        }
        markLayoutParamsB.bottomMargin = DisplayUtil.dip2px(2, getActivity());
        cl8.addView(markView, markLayoutParamsB);
    }

    private void addDots() {
        for (int i = 0; i < set1values.size(); i++) {
            Entry entry = set1values.get(i);
            float top = 150 * (120 - entry.getY()) / 120 + 30 + 2;
            float width = DisplayUtil.screenWidth(getActivity());
            float dpWidth = DisplayUtil.px2dip(width, getActivity());
            float left = 45 + entry.getX() * (dpWidth - 150) / 8 - 1;
            System.out.println("width: " + dpWidth + "left: " + left);
            int icon = 0;
            int drawable = 0;
//            if (i == 0) {
//                icon = R.mipmap.report_icon_score_1;
//                drawable = R.drawable.score_1;
//            } else if (i == 1) {
//                icon = R.mipmap.report_icon_score_2;
//                drawable = R.drawable.score_2;
//            } else if (i == 2) {
//                icon = R.mipmap.report_icon_score_3;
//                drawable = R.drawable.score_3;
//            } else if (i == 3) {
//                icon = R.mipmap.report_icon_score_4;
//                drawable = R.drawable.score_4;
//            } else if (i == 4) {
//                icon = R.mipmap.report_icon_score_5;
//                drawable = R.drawable.score_5;
//            }
//            dynamicAddCircleDot(icon, (int)entry.getY(), drawable, left, top, i);

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
            int avg = sleepTimesTotal/ 60 / Math.max(1, sleepDataSize);
            int hour = avg / 60;
            int minute = avg % 60;
            String content2 = (hour > 9 ? "" + hour : "0" + hour) + unit21 + (minute > 9 ? "" + minute : "0" + minute) + unit22;
            tvTime2.setText(BigSmallFontManager.createTimeValue(content2, getActivity(), 13, array2));
        }

        if (chart == null) {
            return;
        }
        setCharData();
        chart.invalidate();

        int score = 0;
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] > 0) {
                score += scores[i];
            }
        }
        score = score / Math.max(1, sleepDataSize);
        String unit = getResources().getString(R.string.common_minute2);
        String[] array = {unit};
        String content = score + unit;
        tvSleepValue.setText(BigSmallFontManager.createTimeValue(content, getActivity(), 30, array));
        if (score >= 85) {
            tvSleepRank.setText(R.string.common_very_good);
        } else if (score >= 70) {
            tvSleepRank.setText(R.string.common_Awesome);
        } else if (score >= 50){
            tvSleepRank.setText(R.string.common_so_so);
        } else if (score > 0) {
            tvSleepRank.setText(R.string.common_not_good);
        } else {
            tvSleepRank.setText(R.string.common_not_grade);
        }
        tvSleepRank.setTextColor(getResources().getColor(GradeColor.convertGradeToColor(score)));
        calculateSleepValue();
    }

    // 供外表调用，刷新UI
    public void refreshAllUI() {
        readSleepAndGetupData();
        getWeekData();
    }


    // 计算熟睡、中睡，浅睡、清醒
    private void calculateSleepValue() {
        if (tvSleepAverageTime != null) {
            int deepSleepAvg = 0;
            int days = 0;
            for (int i = 0; i < sleepOneDayTimes.length; i++) {
                deepSleepAvg += sleepOneDayTimes[i];
                if (sleepOneDayTimes[i] > 0) {
                    days++;
                }
            }
            String unit01 = getResources().getString(R.string.common_hour);
            String unit02 = getResources().getString(R.string.common_minute);
            String[] array1 = {unit01, unit02};
            int hour = deepSleepAvg / (days > 0 ? days : 7) / 60;
            int minute = deepSleepAvg / (days > 0 ? days : 7) % 60;
            String content1 = (hour > 9 ? "" + hour : "0" + hour) + unit01 + (minute > 9 ? "" + minute : "0" + minute) + unit02;
            tvSleepAverageTime.setText(BigSmallFontManager.createTimeValue(content1, getActivity(), 13, array1));
        }

        if (chart2 == null) {
            return;
        }
        setCharData2();
        chart2.invalidate();

    }
}
