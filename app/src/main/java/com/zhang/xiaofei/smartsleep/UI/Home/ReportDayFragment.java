package com.zhang.xiaofei.smartsleep.UI.Home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.zhang.xiaofei.smartsleep.Kit.ReadTXT;
import com.zhang.xiaofei.smartsleep.Model.Alarm.AlarmModel;
import com.zhang.xiaofei.smartsleep.Model.Device.DeviceModel;
import com.zhang.xiaofei.smartsleep.Model.Record.RecordModel;
import com.zhang.xiaofei.smartsleep.R;

import java.io.InputStream;
import java.lang.reflect.Array;
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
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import io.feeeei.circleseekbar.CircleSeekBar;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ReportDayFragment extends LazyFragment { // 日报告
    private CircleSeekBar circlePercentView;
    private CirclePercentView circleSleep1;
    private CirclePercentView circleSleep2;
    private CirclePercentView circleSleep3;
    private CirclePercentView circleSleep4;
    private TextView tvCircleSleep1;
    private TextView tvCircleSleep2;
    private TextView tvCircleSleep3;
    private TextView tvCircleSleep4;
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
    private TextView tvSleepTime3; // 平均心率、体动 二选一
    private TextView tvSleepTime4; // 平均呼吸率、鼾声 二选一
    private ImageView ivSleepTime3;
    private ImageView ivSleepTime4;
    private TextView tvSimulationData;
    private TextView tvSleepBottomCount1;
    private TextView tvSleepBottomCount2;
    private TextView tvSleepBottomCount3;
    private ConstraintLayout cl5;
    private ConstraintLayout cl6;
    private ConstraintLayout cl11;
    private Realm mRealm;
    private int grade = 0;
    ArrayList<Entry> chart1Values;
    ArrayList<Entry> chart2Values;
    ArrayList<Entry> chart3Values;
    ArrayList<Entry> chart4Values;
    ArrayList<Entry> chart5Values;
    String sleepTime = "";
    String getupTime = "";
    LineDataSet set1;
    LineDataSet set2;
    LineDataSet set3;
    LineDataSet set4;
    LineDataSet set5;
    int tableRowCount = 0;
    int tableRowDuration = 0;
    Map<Integer, List<RecordModel>> mMap = new HashMap<Integer, List<RecordModel>>();
    boolean isBlet = true;
    boolean bChart1 = false;
    boolean bChart2 = false;
    boolean bChart3 = false;
    boolean bChart4 = false;
    boolean bChart5 = false;
    int[] cursor = new int[2];

    @Override
    protected View getPreviewLayout(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.layout_preview, container, false);
    }

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_tabmain_item);
        tvCircleSleep1 = (TextView)findViewById(R.id.tv_sleep_1);
        tvCircleSleep2 = (TextView)findViewById(R.id.tv_sleep_2);
        tvCircleSleep3 = (TextView)findViewById(R.id.tv_sleep_3);
        tvCircleSleep4 = (TextView)findViewById(R.id.tv_sleep_4);
        circlePercentView = (CircleSeekBar)findViewById(R.id.circle_percent_progress);
        circlePercentView.setCurProcess(grade);
        circleSleep1 = (CirclePercentView)findViewById(R.id.circle_sleep_1);
        circleSleep1.setBgColor(getResources().getColor(R.color.color_33315FEE));
        circleSleep1.setProgressColor(getResources().getColor(R.color.color_315FEE));
        circleSleep2 = (CirclePercentView)findViewById(R.id.circle_sleep_2);
        circleSleep2.setBgColor(getResources().getColor(R.color.color_33499BE5));
        circleSleep2.setProgressColor(getResources().getColor(R.color.color_499BE5));
        circleSleep3 = (CirclePercentView)findViewById(R.id.circle_sleep_3);
        circleSleep3.setBgColor(getResources().getColor(R.color.color_3367D9F1));
        circleSleep3.setProgressColor(getResources().getColor(R.color.color_67D9F1));
        circleSleep4 = (CirclePercentView)findViewById(R.id.circle_sleep_4);
        circleSleep4.setBgColor(getResources().getColor(R.color.color_3361D088));
        circleSleep4.setProgressColor(getResources().getColor(R.color.color_61D088));
        readSleepAndGetupData(); // 读取睡觉和起床信息
        readDataFromDB();
        initializeForChart(); // 睡眠质量初始化
        initializeForChart2();
        initializeForChart3();
        initializeForChart4();
        initializeForChart5();

        ibLeftPre = (ImageButton)findViewById(R.id.ib_left_pre);
        ibLeftPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preSleepAndGetupData();
                refreshAllChartX();
                if (sleepTime.length() > 0) {
                    refreshDateTimeData();
                }
                refreshData(isBlet);
                refreshGrade();
            }
        });
        ibRightNex = (ImageButton)findViewById(R.id.ib_right_next);
        ibRightNex.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                nextSleepAndGetupData();
                refreshAllChartX();
                if (sleepTime.length() > 0) {
                    refreshDateTimeData();
                }
                refreshData(isBlet);
                refreshGrade();
            }
        });

        initialText();

        tvSimulationData = (TextView)findViewById(R.id.tv_simulation_data);
        refreshDateTimeData();
        tvSimulationData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyDialog();
            }
        });

        cl5 = (ConstraintLayout)findViewById(R.id.cl_5);
        cl6 = (ConstraintLayout)findViewById(R.id.cl_6);
        cl11 = (ConstraintLayout)findViewById(R.id.cl_11);
        cl11.setVisibility(View.GONE);

    }

    private void refreshDateTimeData() {
        if (getupTime.length() > 0 && sleepTime.length() > 0) {
            String day = sleepTime.substring(0,11);
            String sleep = sleepTime.substring(11);
            String getup = getupTime.substring(11);
            tvSimulationData.setText(day + "\n" + sleep + " - " + getup);
            return;
        }
        tvSimulationData.setText(currentDate(System.currentTimeMillis()));
    }

    // 初始化UI
    private void initialText() {
        tvSleepReviewValue = (TextView)findViewById(R.id.tv_sleep_review_value);
        refreshGrade();
        tvTime1 = (TextView)findViewById(R.id.tv_time_1);
        tvTime2 = (TextView)findViewById(R.id.tv_time_2);
        tvTime3 = (TextView)findViewById(R.id.tv_time_3);
        tvTime4 = (TextView)findViewById(R.id.tv_time_4);
        tvTime5 = (TextView)findViewById(R.id.tv_time_5);
        tvTime6 = (TextView)findViewById(R.id.tv_time_6);

        tvSleepTime3 = (TextView)findViewById(R.id.tv_sleep_time_3);
        tvSleepTime4 = (TextView)findViewById(R.id.tv_sleep_time_4);
        ivSleepTime3 = (ImageView)findViewById(R.id.iv_time_3);
        ivSleepTime4 = (ImageView)findViewById(R.id.iv_time_4);

        tvSleepBottomCount1 = (TextView)findViewById(R.id.tv_sleep_bottom_count_1);
        tvSleepBottomCount2 = (TextView)findViewById(R.id.tv_sleep_bottom_count_2);
        tvSleepBottomCount3 = (TextView)findViewById(R.id.tv_sleep_bottom_count_3);

        refreshSleepStatisticsUI();
    }

    // 刷新静态数据
    private void refreshSleepStatistic(boolean isBlet) {
        if (isBlet) {
            tvSleepTime3.setText(R.string.report_heart_rate);
            tvSleepTime4.setText(R.string.report_respiratory_rate_aver);
            String unit4 = getResources().getString(R.string.common_times_minute);
            String[] array4 = {unit4};
            String content4 = "00" + unit4;
            tvTime3.setText(BigSmallFontManager.createTimeValue(content4, getActivity(), 13, array4));
            tvTime4.setText(BigSmallFontManager.createTimeValue(content4, getActivity(), 13, array4));
            ivSleepTime3.setImageResource(R.mipmap.report_icon_heart_rate);
            ivSleepTime4.setImageResource(R.mipmap.report_icon_respiratory_rate);
        } else {
            tvSleepTime3.setText(R.string.common_body_moves);
            tvSleepTime4.setText(R.string.common_snoring_times);
            String unit4 = getResources().getString(R.string.common_times);
            String[] array4 = {unit4};
            String content4 = "00" + unit4;
            tvTime3.setText(R.string.common_many);
            tvTime4.setText(BigSmallFontManager.createTimeValue(content4, getActivity(), 13, array4));
            ivSleepTime3.setImageResource(R.mipmap.icon_body_movement);
            ivSleepTime4.setImageResource(R.mipmap.icon_snore);
        }
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
        String str = simpleDateFormat.format(date);
        return str;
    }

    private String timeToHourMinute(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String str = simpleDateFormat.format(new Date(time + 0));
        return str;
    }

    private String timeToDate(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String str = simpleDateFormat.format(new Date(time * 1000));
        return str;
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

    // 将时间转换为秒制
    private long timeToLong(String value) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = simpleDateFormat.parse(value);
            return date.getTime() / 1000;
        } catch (ParseException exception) {

        }
        return 0;
    }

    @Override
    protected void onResumeLazy() {
        super.onResumeLazy();
        System.out.println("Report Day Fragment resume lazy");
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
            //textView.setVisibility(View.VISIBLE);
            //progressBar.setVisibility(View.GONE);
        }

        ;
    };

    // 初始化第一个表格
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
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            @Override
            public String getFormattedValue(float value) {
                long millis = 0;
                if (value < 0) {
                    millis = TimeUnit.MINUTES.toMillis((long) value + 24 * 60 + 16 * 60);
                } else {
                    millis = TimeUnit.MINUTES.toMillis((long) value + 16 * 60);
                }
                return mFormat.format(new Date(millis));
            }
        });
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelCount(tableRowCount,true);
        int sleep = hourMinuteToInt(sleepTime);
        int getup = hourMinuteToInt(getupTime);
        if (sleep > getup) {
            sleep = 24 * 60 - sleep;
        }
        xAxis.setAxisMinimum(sleep);
        xAxis.setAxisMaximum(getup);
        //xAxis.setAvoidFirstLastClipping(true);
        xAxis.setDrawLabels(true);

        YAxis yAxis = chart.getAxisLeft();
        // disable dual axis (only use LEFT axis)
        chart.getAxisRight().setEnabled(false);
        yAxis.setTextColor(getResources().getColor(R.color.tranparencyColor));
        yAxis.setAxisMaximum(5f);
        yAxis.setAxisMinimum(0f);
        yAxis.setSpaceTop(0);
        yAxis.setSpaceBottom(0);
        yAxis.setLabelCount(5, true);
        yAxis.setGranularityEnabled(true);
        yAxis.setGranularity(1f);
        yAxis.setDrawAxisLine(false);

        xAxis.setDrawLimitLinesBehindData(false);
        xAxis.setDrawLimitLinesBehindData(false);

        setCharData();

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend(); // 就是图表的名称
        l.setEnabled(false);
    }

    // 开始设置数据部分
    private void setCharData() {
        if (chart1Values == null) {
            chart1Values = new ArrayList<>();
        }

        if (bChart1) {
            set1.setValues(chart1Values);
            set1.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
            return;
        }
        bChart1 = true;
        // create a dataset and give it a type
        set1 = new LineDataSet(chart1Values, "");

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

    // 初始化第二个表格
    private void initializeForChart2() {
        int sleep = hourMinuteToInt(sleepTime);
        int getup = hourMinuteToInt(getupTime);
        if (sleep > getup) {
            sleep = 24 * 60 - sleep;
        }
        chart2 = initializeForChart(R.id.chart2, sleep, getup, 0f, 150f);
        chart2.getAxisLeft().setLabelCount(6, true);

        setData2();
    }

    private void setData2() {
        chart2Values = new ArrayList<>();

        for (Map.Entry<Integer, List<RecordModel>> entry: mMap.entrySet()) {
            float heart = 0;
            for (RecordModel model: entry.getValue()) {
                heart += model.getHeartRate();
            }
            chart2Values.add(new Entry(hourMinuteToInt(timeToDate(entry.getKey() * 60)), heart / entry.getValue().size()));
        }
        Collections.sort(chart2Values, comparatorByX);


        if (bChart2) {
            set2.setValues(chart2Values);
            set2.notifyDataSetChanged();
            chart2.getData().notifyDataChanged();
            chart2.notifyDataSetChanged();
            return;
        }
        bChart2 = true;
        // create a dataset and give it a type
        set2 = new LineDataSet(chart2Values, "");
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);
        set2.setColor(getResources().getColor(R.color.color_6EE1CA));
        set2.setValueTextColor(ColorTemplate.getHoloBlue());
        set2.setLineWidth(0.5f);
        set2.setDrawCircles(false);
        set2.setDrawValues(false);
        set2.setFillAlpha(65);
        set2.setFillColor(ColorTemplate.getHoloBlue());
        set2.setHighLightColor(Color.rgb(244, 117, 117));
        set2.setDrawCircleHole(true);

        // create a data object with the data sets
        set2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        LineData data = new LineData(set2);
        data.setValueTextColor(Color.WHITE);
        data.setHighlightEnabled(false);
        data.setValueTextSize(9f);

        // set data
        chart2.setData(data);
    }

    private void initializeForChart3() {
        int sleep = hourMinuteToInt(sleepTime);
        int getup = hourMinuteToInt(getupTime);
        if (sleep > getup) {
            sleep = 24 * 60 - sleep;
        }
        chart3 = initializeForChart(R.id.chart3, sleep, getup, 0f, 40f);
        setData3();
    }

    private void setData3() {

        chart3Values = new ArrayList<>();

        for (Map.Entry<Integer, List<RecordModel>> entry: mMap.entrySet()) {
            float heart = 0;
            for (RecordModel model: entry.getValue()) {
                heart += model.getBreathRate();
            }
            chart3Values.add(new Entry(hourMinuteToInt(timeToDate(entry.getKey() * 60)), heart / entry.getValue().size()));
        }
        Collections.sort(chart3Values, comparatorByX);

        if (bChart3) {
            set3 = (LineDataSet) chart3.getData().getDataSetByIndex(0);
            set3.setValues(chart3Values);
            set3.notifyDataSetChanged();
            chart3.getData().notifyDataChanged();
            chart3.notifyDataSetChanged();
            return;
        }
        bChart3 = true;
        // create a dataset and give it a type
        set3 = new LineDataSet(chart3Values, "");
        set3.setAxisDependency(YAxis.AxisDependency.LEFT);
        set3.setColor(getResources().getColor(R.color.color_6EE1CA));
        set3.setValueTextColor(ColorTemplate.getHoloBlue());
        set3.setLineWidth(0.5f);
        set3.setDrawCircles(false);
        set3.setDrawValues(false);
        set3.setFillAlpha(65);
        set3.setFillColor(ColorTemplate.getHoloBlue());
        set3.setHighLightColor(Color.rgb(244, 117, 117));
        set3.setDrawCircleHole(false);
        set3.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // create a data object with the data sets
        LineData data = new LineData(set3);
        data.setValueTextColor(Color.WHITE);
        data.setHighlightEnabled(false);
        data.setValueTextSize(9f);

        // set data
        chart3.setData(data);
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
        chart4.setScaleYEnabled(false);
        chart4.setDoubleTapToZoomEnabled(false);
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
        xAxis.setLabelCount(tableRowCount, true);
        xAxis.setGranularityEnabled(true);
        int sleep = hourMinuteToInt(sleepTime);
        int getup = hourMinuteToInt(getupTime);
        if (sleep > getup) {
            sleep = 24 * 60 - sleep;
        }
        xAxis.setAxisMinimum(sleep);
        xAxis.setAxisMaximum(getup);
        xAxis.setValueFormatter(new ValueFormatter() {

            private final SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value) {
                long millis = 0;
                if (value < 0) {
                    millis = TimeUnit.MINUTES.toMillis((long) value + 24 * 60 + 16 * 60);
                } else {
                    millis = TimeUnit.MINUTES.toMillis((long) value + 16 * 60);
                }
                return mFormat.format(new Date(millis));
            }
        });

        YAxis leftAxis = chart4.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setAxisLineDashedLine(new DashPathEffect(new float[]{5f, 5f}, 0f));
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(5f);
        leftAxis.setTextColor(getResources().getColor(R.color.color_FFC858));

        YAxis rightAxis = chart4.getAxisRight();
        rightAxis.setEnabled(false);

        leftAxis.setDrawLimitLinesBehindData(false);
        xAxis.setDrawLimitLinesBehindData(false);

        setData4();
    }

    private void setData4() {

        chart4Values = new ArrayList<>();

        for (Map.Entry<Integer, List<RecordModel>> entry: mMap.entrySet()) {
            float bodyMotion = 0;
            for (RecordModel model: entry.getValue()) {
                bodyMotion += model.getBodyMotion();
            }
            chart4Values.add(new Entry(hourMinuteToInt(timeToDate(entry.getKey() * 60)), bodyMotion / entry.getValue().size()));
        }
        Collections.sort(chart4Values, comparatorByX);

        if (bChart4) {
            set4 = (LineDataSet) chart4.getData().getDataSetByIndex(0);
            set4.setValues(chart4Values);
            set4.notifyDataSetChanged();
            chart4.getData().notifyDataChanged();
            chart4.notifyDataSetChanged();
            return;
        }
        bChart4 = true;
        // create a dataset and give it a type
        set4 = new LineDataSet(chart4Values, "");
        set4.setAxisDependency(YAxis.AxisDependency.LEFT);
        set4.setColor(getResources().getColor(R.color.color_6EE1CA));
        set4.setValueTextColor(ColorTemplate.getHoloBlue());
        set4.setLineWidth(0.5f);
        set4.setDrawCircles(false);
        set4.setDrawValues(false);
        set4.setFillAlpha(65);
        set4.setFillColor(ColorTemplate.getHoloBlue());
        set4.setDrawCircleHole(false);
        set4.setDrawVerticalHighlightIndicator(false);
        set4.setDrawHorizontalHighlightIndicator(false);
        set4.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // create a data object with the data sets
        LineData data = new LineData(set4);
        data.setValueTextColor(Color.WHITE);
        data.setHighlightEnabled(false);
        data.setValueTextSize(9f);

        // set data
        chart4.setData(data);
    }

    private void initializeForChart5() {
        int sleep = hourMinuteToInt(sleepTime);
        int getup = hourMinuteToInt(getupTime);
        if (sleep > getup) {
            sleep = 24 * 60 - sleep;
        }
        chart5 = initializeForChart(R.id.chart5, sleep, getup, 0f, 5f);
        chart5.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0) {
                    return getResources().getString(R.string.common_week);
                } else if (value == 5) {
                    return getResources().getString(R.string.common_harsh);
                }
                return "";
            }
        });

        setData5();
    }

    private void setData5() {

        chart5Values = new ArrayList<>();
        if (bChart5) {
            set5 = (LineDataSet) chart5.getData().getDataSetByIndex(0);
            set5.setValues(chart5Values);
            set5.notifyDataSetChanged();
            chart5.getData().notifyDataChanged();
            chart5.notifyDataSetChanged();
            return;
        }
        bChart5 = true;
        // create a dataset and give it a type
        set5 = new LineDataSet(chart5Values, "");
        set5.setAxisDependency(YAxis.AxisDependency.LEFT);
        set5.setColor(getResources().getColor(R.color.color_6EE1CA));
        set5.setValueTextColor(ColorTemplate.getHoloBlue());
        set5.setLineWidth(0.5f);
        set5.setDrawCircles(false);
        set5.setDrawValues(false);
        set5.setFillAlpha(65);
        set5.setFillColor(ColorTemplate.getHoloBlue());
        set5.setDrawCircleHole(false);
        set5.setDrawVerticalHighlightIndicator(false);
        set5.setDrawHorizontalHighlightIndicator(false);
        set5.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // create a data object with the data sets
        LineData data = new LineData(set5);
        data.setValueTextColor(Color.WHITE);
        data.setHighlightEnabled(false);
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
                    int month = calendar.getMonth();
                    int day = calendar.getDay();
                    String date = calendar.getYear() + "-" + (month > 9 ? ("" + month) : ("0" + month)) + "-" + (day > 9 ? ("" + day) : ("0" + day));
                    System.out.println("当前选择的日期为：" + date);
                    currentSleepAndGetupData(date);
                    dialog.hide();
                }
            }
        });
    }

    private void verifyDialog() {
        dialog = new Dialog(getActivity(), R.style.activity_translucent);
        dialog.setContentView(R.layout.layout_choose_date);
        mCalendarView = (CalendarView)dialog.getWindow().findViewById(R.id.calendarView);
        initData();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis((long)timeToLong(sleepTime) * 1000);
        int year = calendar.get(Calendar.YEAR);
        //获取月份，0表示1月份
        int month = calendar.get(Calendar.MONTH) + 1;
        //获取当前天数
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mCalendarView.scrollToCalendar(year, month, day);

        if(dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    private void readDataFromDB() {
        mMap.clear();
        mRealm = Realm.getDefaultInstance();
        RealmResults<RecordModel> list = mRealm.where(RecordModel.class)
                .greaterThan("time", timeToLong(sleepTime))
                .lessThan("time", timeToLong(getupTime))
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
        System.out.println( " 开始时间：" + sleepTime + "结束时间：" + getupTime + "获取到的数据量：" + list.size());
    }

    public void refreshData(boolean isBlet) {
        this.isBlet = isBlet;
        cl5.setVisibility(isBlet ? View.VISIBLE : View.GONE);
        cl6.setVisibility(isBlet ? View.VISIBLE : View.GONE);
        cl11.setVisibility(isBlet ? View.GONE : View.VISIBLE);

        readDataFromDB();
        setData2();
        chart2.invalidate();
        setData3();
        chart3.invalidate();
        setData4();
        chart4.invalidate();

        refreshSleepStatistic(isBlet);
        refreshSleepStatisticsUI();
    }

    Comparator<Entry> comparatorByX = new Comparator<Entry>() {
        @Override
        public int compare(Entry lhs, Entry rhs) {
            if(lhs.getX() > rhs.getX())
                return 1;
            return -1;
        }
    };

    private LineChart initializeForChart(int id, float minX, float maxX, float minY, float maxY) {
        LineChart chart = findViewById(id);
        // no description text
        chart.getDescription().setEnabled(false);
        // enable touch gestures
        chart.setTouchEnabled(true);
        chart.setDragDecelerationFrictionCoef(0.9f);
        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setHighlightPerDragEnabled(false);
        chart.setPinchZoom(false);
        chart.setScaleYEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        // set an alternative background color
        chart.setBackgroundColor(getResources().getColor(R.color.tranparencyColor));
        //chart3.setViewPortOffsets(10f, 0f, 0f, 0f);
        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        l.setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setAxisLineDashedLine(new DashPathEffect(new float[]{5f, 5f}, 0f));
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.argb(0,0,0,0));
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setCenterAxisLabels(false);
        xAxis.setLabelCount(tableRowCount, true);
        xAxis.setGranularityEnabled(true);
        xAxis.setAxisMinimum(minX);
        xAxis.setAxisMaximum(maxX);
        xAxis.setValueFormatter(new ValueFormatter() {

            private final SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value) {
                long millis = 0;
                if (value < 0) {
                    millis = TimeUnit.MINUTES.toMillis((long) value + 24 * 60 + 16 * 60);
                } else {
                    millis = TimeUnit.MINUTES.toMillis((long) value + 16 * 60);
                }
                return mFormat.format(new Date(millis));
            }
        });

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        //leftAxis.setTypeface(tfLight);
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(minY);
        leftAxis.setAxisMaximum(maxY);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setAxisLineDashedLine(new DashPathEffect(new float[]{5f, 5f}, 0f));
        leftAxis.setTextColor(getResources().getColor(R.color.color_FFC858));

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        leftAxis.setDrawLimitLinesBehindData(false);
        xAxis.setDrawLimitLinesBehindData(false);

        return chart;
    }

    // 刷新统计时间相关数据UI
    private void refreshSleepStatisticsUI() {
        Integer minTime = 0;
        Integer maxTime = 0;
        long averageHeart = 0;
        long averageBreath = 0;
        long averageTemp = 0;
        long averageHumidity = 0;
        Integer count = 0;
        Integer bodyMotion = 0;
        if (mMap.size() > 0) {
            for (Map.Entry<Integer, List<RecordModel>> entry: mMap.entrySet()) {
                for (RecordModel model: entry.getValue()) {
                    averageHeart += model.getHeartRate();
                    averageBreath += model.getBreathRate();
                    averageTemp += model.getTemperature();
                    averageHumidity += model.getHumidity();
                    bodyMotion += model.getBodyMotion();
                }
                count += entry.getValue().size();
                if (minTime == 0) {
                    minTime = entry.getKey();
                }
                if (entry.getKey() < minTime) {
                    minTime = entry.getKey();
                }
                if (maxTime == 0) {
                    maxTime = entry.getKey();
                }
                if (entry.getKey() > maxTime) {
                    maxTime = entry.getKey();
                }
            }
        }
        System.out.println("minTime: " + minTime + " maxTime: " + maxTime);
        String unit11 = getResources().getString(R.string.common_hour2);
        String unit12 = getResources().getString(R.string.common_minute3);
        String[] array1 = {unit11, unit12};
        if (minTime <= 0) {
            String content1 = "00" + unit11 + "00" + unit12;
            tvTime1.setText(BigSmallFontManager.createTimeValue(content1, getActivity(), 13, array1));
        } else {
            String hourAndMinute = timeToHourMinute((long)minTime * 60 * 1000);
            System.out.println("hourAndMinute: " + hourAndMinute);
            String[] array = hourAndMinute.split(":");
            if (array.length == 2) {
                String content1 = array[0] + unit11 + array[1] + unit12;
                tvTime1.setText(BigSmallFontManager.createTimeValue(content1, getActivity(), 13, array1));
            }
        }

        String unit21 = getResources().getString(R.string.common_hour);
        String unit22 = getResources().getString(R.string.common_minute);
        String[] array2 = {unit21, unit22};
        String hour = (maxTime - minTime) / 60 > 9 ? ("" + (maxTime - minTime) / 60) : ("0" + (maxTime - minTime) / 60);
        String minute =  (maxTime - minTime) % 60 > 9 ? ("" + (maxTime - minTime) % 60) : ("0" + (maxTime - minTime) % 60);
        String content2 = hour + unit21 + minute + unit22;
        if (minTime <= 0) {
            content2 = "00" + unit21 + "00" + unit22;
        }
        tvTime2.setText(BigSmallFontManager.createTimeValue(content2, getActivity(), 13, array2));

        String unit4 = getResources().getString(R.string.common_times_minute);
        String[] array4 = {unit4};
        if (count > 0) {
            String content3 = averageHeart / count + unit4;
            tvTime3.setText(BigSmallFontManager.createTimeValue(content3, getActivity(), 13, array4));
            String unit1 = getResources().getString(R.string.common_times_minute);
            tvSleepBottomCount1.setText(BigSmallFontManager.createTimeValue(content3, getActivity(), 13, unit1));
        } else {
            String content3 = "00" + unit4;
            tvTime3.setText(BigSmallFontManager.createTimeValue(content3, getActivity(), 13, array4));
            String unit1 = getResources().getString(R.string.common_times_minute);
            tvSleepBottomCount1.setText(BigSmallFontManager.createTimeValue(content3, getActivity(), 13, unit1));
        }
        if (count > 0) {
            String content4 = averageBreath / count + unit4;
            tvTime4.setText(BigSmallFontManager.createTimeValue(content4, getActivity(), 13, array4));
            String unit2 = getResources().getString(R.string.common_times_minute);
            tvSleepBottomCount2.setText(BigSmallFontManager.createTimeValue(content4, getActivity(), 13, unit2));
        } else {
            String content4 = "00" + unit4;
            String unit2 = getResources().getString(R.string.common_times_minute);
            tvTime4.setText(BigSmallFontManager.createTimeValue(content4, getActivity(), 13, array4));
            tvSleepBottomCount2.setText(BigSmallFontManager.createTimeValue(content4, getActivity(), 13, unit2));
        }
        String unit5 = "℃";
        String[] array5 = {unit5};
        if (count > 0) {
            String content5 = averageTemp / count + unit5;
            tvTime5.setText(BigSmallFontManager.createTimeValue(content5, getActivity(), 13, array5));
        } else {
            String content5 = "00" + unit5;
            tvTime5.setText(BigSmallFontManager.createTimeValue(content5, getActivity(), 13, array5));
        }
        String unit6 = "%";
        String[] array6 = {unit6};
        if (count > 0) {
            String content6 = averageHumidity / count + unit6;
            tvTime6.setText(BigSmallFontManager.createTimeValue(content6, getActivity(), 13, array6));
        } else {
            String content6 = "00" + unit6;
            tvTime6.setText(BigSmallFontManager.createTimeValue(content6, getActivity(), 13, array6));
        }

        String unit = getResources().getString(R.string.common_times);
        if (count > 0) {
            String content = bodyMotion / count + getResources().getString(R.string.common_times);
            tvSleepBottomCount3.setText(BigSmallFontManager.createTimeValue(content, getActivity(), 13, unit));
        } else {
            String content = "0 " + getResources().getString(R.string.common_times);
            tvSleepBottomCount3.setText(BigSmallFontManager.createTimeValue(content, getActivity(), 13, unit));
        }

        if (count > 0) {
            grade = 90 - (bodyMotion / 100) * 20; // 计算当前日期的得分
            grade = Math.min(90, grade);
            grade = Math.max(70, grade);
            refreshGrade();
            circlePercentView.setCurProcess(grade);
        } else {
            grade = 0;
            refreshGrade();
            circlePercentView.setCurProcess(grade);
        }
        System.out.println("计算得到的最终得分为：" + grade);
        calculateSleepValue();
    }

    private void readSleepAndGetupData() {
        if (SleepAndGetupTimeManager.times.size() == 0) {
            return;
        }
        String[] days = SleepAndGetupTimeManager.times.keySet().toArray(new String[0]);
        Arrays.sort(days);
        cursor[0] = days.length - 1;
        List<String> items = SleepAndGetupTimeManager.times.get(days[days.length - 1]);
        Collections.sort(items);
        cursor[1] = items.size() - 1;
        String duration = items.get(items.size() - 1);
        sleepTime = duration.split("&")[0];
        getupTime = duration.split("&")[1];
        int sleep = hourMinuteToInt(sleepTime);
        int getup = hourMinuteToInt(getupTime);
        if (sleep > getup) {
            sleep = 24 * 60 - sleep;
        }
        tableRowCount = 6;
        tableRowDuration = (getup - sleep) / 6;
    }

    private void preSleepAndGetupData() {
        if (SleepAndGetupTimeManager.times.size() == 0) {
            return;
        }
        String[] days = SleepAndGetupTimeManager.times.keySet().toArray(new String[0]);
        Arrays.sort(days);
        if (cursor[1] > 0) {
            cursor[1] -= 1;
        } else if (cursor[0] > 0) {
            cursor[0] -= 1;
            cursor[1] = SleepAndGetupTimeManager.times.get(days[cursor[0]]).size() - 1;
        } else {
            return;
        }
        List<String> items = SleepAndGetupTimeManager.times.get(days[cursor[0]]);
        Collections.sort(items);
        String duration = items.get(cursor[1]);
        sleepTime = duration.split("&")[0];
        getupTime = duration.split("&")[1];
        int sleep = hourMinuteToInt(sleepTime);
        int getup = hourMinuteToInt(getupTime);
        if (sleep > getup) {
            sleep = 24 * 60 - sleep;
        }
        tableRowCount = 6;
        tableRowDuration = (getup - sleep) / 6;
    }

    private void nextSleepAndGetupData() {
        if (SleepAndGetupTimeManager.times.size() == 0) {
            return;
        }
        String[] days = SleepAndGetupTimeManager.times.keySet().toArray(new String[0]);
        Arrays.sort(days);
        List<String> tem = SleepAndGetupTimeManager.times.get(days[cursor[0]]);
        if (cursor[1] < tem.size() - 1) {
            cursor[1] += 1;
        } else if (cursor[0] < days.length - 1) {
            cursor[0] += 1;
            cursor[1] = 0;
        } else {
            return;
        }
        List<String> items = SleepAndGetupTimeManager.times.get(days[cursor[0]]);
        Collections.sort(items);
        String duration = items.get(cursor[1]);
        sleepTime = duration.split("&")[0];
        getupTime = duration.split("&")[1];
        int sleep = hourMinuteToInt(sleepTime);
        int getup = hourMinuteToInt(getupTime);
        if (sleep > getup) {
            sleep = 24 * 60 - sleep;
        }
        tableRowCount = 6;
        tableRowDuration = (getup - sleep) / 6;
    }

    private void currentSleepAndGetupData(String day) {
        if (SleepAndGetupTimeManager.times.size() == 0) {
            return;
        }
        String[] days = SleepAndGetupTimeManager.times.keySet().toArray(new String[0]);
        Arrays.sort(days);
        int j = -1;
        for (int i = 0; i < days.length; i++) {
            if (day.equals(days[i])) {
                j = i;
                break;
            }
        }
        if (j < 0) {
            Toast.makeText(getActivity(), R.string.current_date_no_date, Toast.LENGTH_LONG).show();
            return;
        }
        cursor[0] = j;
        List<String> items = SleepAndGetupTimeManager.times.get(days[cursor[0]]);
        Collections.sort(items);
        cursor[1] = items.size() - 1;
        String duration = items.get(cursor[1]);
        sleepTime = duration.split("&")[0];
        getupTime = duration.split("&")[1];
        int sleep = hourMinuteToInt(sleepTime);
        int getup = hourMinuteToInt(getupTime);
        if (sleep > getup) {
            sleep = 24 * 60 - sleep;
        }
        tableRowCount = 6;
        tableRowDuration = (getup - sleep) / 6;
        refreshDateTimeData();
    }


    // 重新刷新日报
    public void refreshDayReport() {
        readSleepAndGetupData(); // 重新读取睡觉和起床时间
        refreshAllChartX();
        refreshData(isBlet);
    }

    private void refreshAllChartX() {
        refreshX(chart);
        refreshX(chart2);
        refreshX(chart3);
        refreshX(chart4);
        refreshX(chart5);
    }

    private void refreshX(LineChart chart) {
        int sleep = hourMinuteToInt(sleepTime);
        int getup = hourMinuteToInt(getupTime);
        if (sleep > getup) {
            sleep = 24 * 60 - sleep;
        }
        chart.getXAxis().setLabelCount(tableRowCount,true);
        chart.getXAxis().setAxisMinimum(sleep);
        chart.getXAxis().setAxisMaximum(getup);
    }

    // 计算熟睡、中睡，浅睡、清醒
    private void calculateSleepValue() {
        if (chart1Values == null) {
            chart1Values = new ArrayList<>();
        } else {
            chart1Values.clear();
        }

        if (mMap.size() > 0) {
            int deepSleep = 0;
            int middleSleep = 0;
            int cheapSleep = 0;
            int getup = 0;
            int time = 0;
            int bodyMotionCount = 0;
            int getupCount = 0;
            int i = 0;
            System.out.println("计算的次数C为：" + mMap.size());
            for (Map.Entry<Integer, List<RecordModel>> entry : mMap.entrySet()) { // 5分钟计算一次
                i += 1;
                if (time == 0) {
                    time = entry.getKey() + 5;
                }
                if (entry.getKey() <= time) {
                    if (i == mMap.size()) {
                        System.out.println("计算的次数B为：" + i);
                        time += 5;
                        if (getupCount > 0) {
                            getup += 1;
                            chart1Values.add(new Entry(hourMinuteToInt(timeToDate((time - 10) * 60)), 1));
                        } else {
                            if (bodyMotionCount > 6) {
                                getup += 1;
                                chart1Values.add(new Entry(hourMinuteToInt(timeToDate((time - 10) * 60)), 1));
                            } else if (bodyMotionCount >= 3) {
                                cheapSleep += 1;
                                chart1Values.add(new Entry(hourMinuteToInt(timeToDate((time - 10) * 60)), 2));
                            } else if (bodyMotionCount >= 1) {
                                middleSleep += 1;
                                chart1Values.add(new Entry(hourMinuteToInt(timeToDate((time - 10) * 60)), 3));
                            } else {
                                deepSleep += 1;
                                chart1Values.add(new Entry(hourMinuteToInt(timeToDate((time - 10) * 60)), 4));
                            }
                        }
                        break;
                    }
                } else {
                    System.out.println("计算的次数A为：" + i);
                    time += 5;
                    if (getupCount > 0) {
                        getup += 1;
                        chart1Values.add(new Entry(hourMinuteToInt(timeToDate((time - 10) * 60)), 1));
                    } else {
                        if (bodyMotionCount > 6) {
                            getup += 1;
                            chart1Values.add(new Entry(hourMinuteToInt(timeToDate((time - 10) * 60)), 1));
                        } else if (bodyMotionCount >= 3) {
                            cheapSleep += 1;
                            chart1Values.add(new Entry(hourMinuteToInt(timeToDate((time - 10) * 60)), 2));
                        } else if (bodyMotionCount >= 1) {
                            middleSleep += 1;
                            chart1Values.add(new Entry(hourMinuteToInt(timeToDate((time - 10) * 60)), 3));
                        } else {
                            deepSleep += 1;
                            chart1Values.add(new Entry(hourMinuteToInt(timeToDate((time - 10) * 60)), 4));
                        }
                    }
                    getupCount = 0;
                    bodyMotionCount = 0;
                }

                for (RecordModel model : entry.getValue()) { // 计算清醒和体动
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
            int c = deepSleep + middleSleep + cheapSleep + getup;
            System.out.println("计算获得的睡觉值：深睡-" + deepSleep + "中睡-" + middleSleep + "浅睡-" + cheapSleep + "清醒-" + getup);
            if (c > 0) {
                circleSleep1.setPercentage(deepSleep * 100 / c);
                circleSleep2.setPercentage(middleSleep * 100 / c);
                circleSleep3.setPercentage(cheapSleep * 100 / c);
                circleSleep4.setPercentage(getup * 100 / c);
                tvCircleSleep1.setText(deepSleep * 100 / c + "%");
                tvCircleSleep2.setText(middleSleep * 100 / c + "%");
                tvCircleSleep3.setText(cheapSleep * 100 / c + "%");
                tvCircleSleep4.setText(getup * 100 / c + "%");
            } else {
                circleSleep1.setPercentage(0);
                circleSleep2.setPercentage(0);
                circleSleep3.setPercentage(0);
                circleSleep4.setPercentage(0);
                tvCircleSleep1.setText("0%");
                tvCircleSleep2.setText("0%");
                tvCircleSleep3.setText("0%");
                tvCircleSleep4.setText("0%");
            }
        } else {
            System.out.println("不用计算的睡觉值：深睡-" + 0 + "中睡-" + 0 + "浅睡-" + 0 + "清醒-" + 0);
            circleSleep1.setPercentage(0);
            circleSleep2.setPercentage(0);
            circleSleep3.setPercentage(0);
            circleSleep4.setPercentage(0);
            tvCircleSleep1.setText("0%");
            tvCircleSleep2.setText("0%");
            tvCircleSleep3.setText("0%");
            tvCircleSleep4.setText("0%");
        }
        setCharData();
        chart.invalidate();
    }
}
