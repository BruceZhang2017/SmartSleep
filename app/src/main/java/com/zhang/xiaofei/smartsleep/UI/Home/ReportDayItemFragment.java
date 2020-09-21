package com.zhang.xiaofei.smartsleep.UI.Home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.haibin.calendarview.CalendarView;
import com.shizhefei.fragment.LazyFragment;
import com.zhang.xiaofei.smartsleep.Kit.Application.LogHelper;
import com.zhang.xiaofei.smartsleep.Kit.Application.ScreenInfoUtils;
import com.zhang.xiaofei.smartsleep.Kit.BigSmallFont.BigSmallFontManager;
import com.zhang.xiaofei.smartsleep.Kit.DisplayUtil;
import com.zhang.xiaofei.smartsleep.Kit.ReadTXT;
import com.zhang.xiaofei.smartsleep.Model.Alarm.AlarmModel;
import com.zhang.xiaofei.smartsleep.Model.Device.DeviceModel;
import com.zhang.xiaofei.smartsleep.Model.Record.RecordModel;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Me.FeedbackActivity;
import com.zhang.xiaofei.smartsleep.YMApplication;

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
import java.util.HashSet;
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

public class ReportDayItemFragment extends LazyFragment { // 日报告
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
    CalendarView mCalendarView;
    private TextView tvSleepReviewValue;
    private TextView tvTime1; // 上床时间
    private TextView tvTime2; // 入睡时长
    private TextView tvTime3; // 清醒时间
    private TextView tvTime4; // 平均心率
    private TextView tvTime5; // 清醒时间
    private TextView tvTime6; // 平均心率
    private TextView tvTime7; // 呼吸暂停次数
    private TextView tvSleepTime3; // 平均心率、体动 二选一
    private TextView tvSleepTime4; // 平均呼吸率、鼾声 二选一
    private ImageView ivSleepTime3;
    private ImageView ivSleepTime4;
    private TextView tvSleepBottomCount1;
    private TextView tvSleepBottomCount2;
    private TextView tvSleepBottomCount3;
    private ConstraintLayout cl5;
    private ConstraintLayout cl6;
    private ConstraintLayout clRoot;
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
    List<RecordModel> mlist = new ArrayList<>();
    List<RecordModel> apneaList = new ArrayList<>();
    boolean isBlet = true;
    boolean bChart1 = false;
    boolean bChart2 = false;
    boolean bChart3 = false;
    boolean bChart4 = false;
    boolean bChart5 = false;
    private boolean bSleepBletValueRead = false; // 睡眠带值是否有值
    private int[] sleepbeltValue = new int[5]; // 得分， 入睡，睡眠时长，心率，呼吸率
    private int countApnea = 0; // 心跳暂停计数
    private List<ImageView> ivList = new ArrayList<>();
    private Set<Integer> setValue1 = new HashSet<Integer>();
    TextView tvSleepDesc;

    public ReportDayItemFragment(String sleepTime, String getupTime) {
        super();
        this.sleepTime = sleepTime;
        this.getupTime = getupTime;
        if (sleepTime.length() == 0) {
            Date date = new Date();
            this.sleepTime = timeToDate(date.getTime() / 1000);
            this.getupTime = this.sleepTime;
        }
    }

    public void refreshTime(String sleepTime, String getupTime) {
        this.sleepTime = sleepTime;
        this.getupTime = getupTime;
    }

    @Override
    protected View getPreviewLayout(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.layout_preview, container, false);
    }

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_report_day_item);
        clRoot = (ConstraintLayout)findViewById(R.id.constraint_layout_root);
        tvCircleSleep1 = (TextView)findViewById(R.id.tv_sleep_1);
        tvCircleSleep2 = (TextView)findViewById(R.id.tv_sleep_2);
        tvCircleSleep3 = (TextView)findViewById(R.id.tv_sleep_3);
        tvCircleSleep4 = (TextView)findViewById(R.id.tv_sleep_4);
        tvSleepDesc = (TextView)findViewById(R.id.tv_sleep_desc);
        circlePercentView = (CircleSeekBar)findViewById(R.id.circle_percent_progress);
        circlePercentView.setCurProcess(grade);
        circlePercentView.setReachedColor(getResources().getColor(GradeColor.convertGradeToColor(grade)));
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
        readDataFromDB();
        setChartData();
        initializeForChart(); // 睡眠质量初始化
        initializeForChart2();
        initializeForChart3();
        initializeForChart4();
        initialText();

        cl5 = (ConstraintLayout)findViewById(R.id.cl_5);
        cl6 = (ConstraintLayout)findViewById(R.id.cl_6);
    }

//    private void refreshDateTimeData() {
//        if (getupTime.length() > 11 && sleepTime.length() > 11) {
//            String day = sleepTime.substring(0,11);
//            String sleep = sleepTime.substring(11);
//            String getup = getupTime.substring(11);
//            tvSimulationData.setText(day + "\n" + sleep + " - " + getup);
//            return;
//        }
//        tvSimulationData.setText(currentDate(System.currentTimeMillis()));
//    }

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
        tvTime7 = (TextView)findViewById(R.id.tv_time_7);

        tvSleepTime3 = (TextView)findViewById(R.id.tv_sleep_time_3);
        tvSleepTime4 = (TextView)findViewById(R.id.tv_sleep_time_4);
        ivSleepTime3 = (ImageView)findViewById(R.id.iv_time_3);
        ivSleepTime4 = (ImageView)findViewById(R.id.iv_time_4);

        tvSleepBottomCount1 = (TextView)findViewById(R.id.tv_sleep_bottom_count_1);
        tvSleepBottomCount2 = (TextView)findViewById(R.id.tv_sleep_bottom_count_2);
        tvSleepBottomCount3 = (TextView)findViewById(R.id.tv_sleep_bottom_count_3);
        tvSleepBottomCount3.setVisibility(View.INVISIBLE);
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
        circlePercentView.setReachedColor(getResources().getColor(GradeColor.convertGradeToColor(grade)));
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
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
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
        xAxis.setLabelCount(6,true);
        int sleep = hourMinuteToInt(sleepTime);
        int getup = hourMinuteToInt(getupTime);
        if (sleep > getup) {
            sleep = -24 * 60 + sleep;
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
        yAxis.setLabelCount(6, true);
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

        set1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
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
            sleep = -24 * 60 + sleep;
        }
        chart2 = initializeForChart(R.id.chart2, sleep, getup, 0f, 150f);
        chart2.getAxisLeft().setLabelCount(6, true);

        setData2();
    }

    private void setChartData() {
        if (chart2Values == null) {
            chart2Values = new ArrayList<>();
        } else {
            chart2Values.clear();
        }
        if (chart3Values == null) {
            chart3Values = new ArrayList<>();
        } else {
            chart3Values.clear();
        }
        if (chart4Values == null) {
            chart4Values = new ArrayList<>();
        } else {
            chart4Values.clear();
        }
        if (chart5Values == null) {
            chart5Values = new ArrayList<>();
        } else {
            chart5Values.clear();
        }
        int time = 0;
        int heart = 0;
        int breath = 0;
        int bodyMotion = 0;
        int snore = 0;
        int count = 0;
        int total = 0;
        int getup = hourMinuteToInt(getupTime);
        int sleep = hourMinuteToInt(sleepTime);
        if (sleep > getup) {
            sleep = -24 * 60 + sleep;
        }
        System.out.println("睡觉时间和起床时间：" + sleep + " " + getup);
        for (RecordModel model: mlist) {
            int t = hourMinuteToInt(timeToDate(model.getTime()));
            if (t > getup) {
                t = -24 * 60 + t;
            }
            if (time == 0) {
                time = t;
            } else if (time != t) {
                chart2Values.add(new Entry(time, heart / count));
                chart3Values.add(new Entry(time, breath / count));
                chart4Values.add(new Entry(time, bodyMotion));
                chart5Values.add(new Entry(time, snore));
                time = 0;
                heart = 0;
                count = 0;
                breath = 0;
                bodyMotion = 0;
                snore = 0;
            }

            heart += model.getHeartRate();
            breath += model.getBreathRate();
            bodyMotion += model.getBodyMotion();
            snore += model.getSnore();
            count += 1;
            total += 1;
            if (total == mlist.size() && time != 0) {
                chart2Values.add(new Entry(time, heart / count));
            }

            if (total == mlist.size() && time != 0) {
                chart3Values.add(new Entry(time, breath / count));
            }
            if (total == mlist.size() && time != 0) {
                chart4Values.add(new Entry(time, Math.min(bodyMotion, 5)));
            }
            if (total == mlist.size() && time != 0) {
                chart5Values.add(new Entry(time, snore));
            }
        }

        Collections.sort(chart4Values, comparatorByX);
        if (chart4Values.size() > 0) {
            if (chart4Values.get(0).getX() > sleep) {
                chart4Values.add(0, new Entry(chart4Values.get(0).getX() - 1, 0));
                chart4Values.add(0, new Entry(sleep, 0));
            }
            if (chart4Values.get(chart4Values.size() - 1).getX() < getup) {
                chart4Values.add(new Entry(chart4Values.get(chart4Values.size() - 1).getX() + 1, 0));
                chart4Values.add(new Entry(getup, 0));
            }
        }
        Collections.sort(chart2Values, comparatorByX);
        if (chart2Values.size() > 0) {
            if (chart2Values.get(0).getX() > sleep) {
                chart2Values.add(0, new Entry(chart2Values.get(0).getX() - 1, 0));
                chart2Values.add(0, new Entry(sleep, 0));
            }
            if (chart2Values.get(chart2Values.size() - 1).getX() < getup) {
                chart2Values.add(new Entry(chart2Values.get(chart2Values.size() - 1).getX() + 1, 0));
                chart2Values.add(new Entry(getup, 0));
            }
        }
        Collections.sort(chart3Values, comparatorByX);
        if (chart3Values.size() > 0) {
            if (chart3Values.get(0).getX() > sleep) {
                chart3Values.add(0, new Entry(chart3Values.get(0).getX() - 1, 0));
                chart3Values.add(0, new Entry(sleep, 0));
            }
            if (chart3Values.get(chart3Values.size() - 1).getX() < getup) {
                chart3Values.add(new Entry(chart3Values.get(chart3Values.size() - 1).getX() + 1, 0));
                chart3Values.add(new Entry(getup, 0));
            }
        }
        Collections.sort(chart5Values, comparatorByX);
        if (chart5Values.size() > 0) {
            if (chart5Values.get(0).getX() > sleep) {
                chart5Values.add(0, new Entry(chart5Values.get(0).getX() - 1, 0));
                chart5Values.add(0, new Entry(sleep, 0));
            }
            if (chart5Values.get(chart5Values.size() - 1).getX() < getup) {
                chart5Values.add(new Entry(chart5Values.get(chart5Values.size() - 1).getX() + 1, 0));
                chart5Values.add(new Entry(getup, 0));
            }
        }
    }

    private void setData2() {

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
        set2.setMode(LineDataSet.Mode.LINEAR);
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
            sleep = -24 * 60 + sleep;
        }
        chart3 = initializeForChart(R.id.chart3, sleep, getup, 0f, 40f);
        setData3();
    }

    private void setData3() {
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
        set3.setMode(LineDataSet.Mode.LINEAR);
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
        xAxis.setLabelCount(6, true);
        xAxis.setGranularityEnabled(true);
        int sleep = hourMinuteToInt(sleepTime);
        int getup = hourMinuteToInt(getupTime);
        if (sleep > getup) {
            sleep = -24 * 60 + sleep;
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
        set4.setMode(LineDataSet.Mode.LINEAR);
        // create a data object with the data sets
        LineData data = new LineData(set4);
        data.setValueTextColor(Color.WHITE);
        data.setHighlightEnabled(false);
        data.setValueTextSize(9f);

        // set data
        chart4.setData(data);
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
                    //currentSleepAndGetupData(date);
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
        mlist.clear();
        mRealm = Realm.getDefaultInstance();
        RealmResults<RecordModel> list = mRealm.where(RecordModel.class)
                .greaterThan("time", timeToLong(sleepTime))
                .lessThan("time", timeToLong(getupTime))
                .findAll().sort("time", Sort.ASCENDING);
        for (RecordModel model: list) {
            mlist.add(model);
        }
        System.out.println( " 开始时间：" + sleepTime + "结束时间：" + getupTime + "获取到的数据量：" + list.size());
    }

    public void refreshData(boolean isBlet) {
        this.isBlet = isBlet;
        cl5.setVisibility(isBlet ? View.VISIBLE : View.GONE);
        cl6.setVisibility(isBlet ? View.VISIBLE : View.GONE);

        readDataFromDB();
        setChartData();
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
        xAxis.setLabelCount(6, true);
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
        long averageHeart = 0;
        long averageBreath = 0;
        long averageTemp = 0;
        long averageHumidity = 0;
        long averageApnea = 0;
        Integer count = mlist.size();
        Integer bodyMotion = 0;
        Integer snore = 0;
        if (mlist.size() > 0) {
            apneaList.clear();
            if (ivList.size() > 0) {
                for (ImageView ivItem: ivList) {
                    clRoot.removeView(ivItem);
                }
                ivList.clear();
                countApnea = 0;
            }
            for (RecordModel model: mlist) {
                averageHeart += model.getHeartRate();
                averageBreath += model.getBreathRate();
                averageTemp += model.getTemperature();
                averageHumidity += model.getHumidity();
                bodyMotion += model.getBodyMotion();
                snore += model.getSnore();
                if (model.getBreatheStop() > 0) {
                    averageApnea += 1;
                    apneaList.add(model);
                    addApneaIcon(model.getTime(), model.getBreatheStop(), model.getHeartRate());
                }
            }
        }
        String unit11 = getResources().getString(R.string.common_hour2);
        String unit12 = getResources().getString(R.string.common_minute3);
        String[] array1 = {unit11, unit12};
        if (sleepTime.length() == 0) {
            String content1 = "00" + unit11 + "00" + unit12;
            tvTime1.setText(BigSmallFontManager.createTimeValue(content1, getActivity(), 13, array1));
        } else {
            String hourAndMinute = sleepTime.substring(11, 16);
            System.out.println("hourAndMinute: " + hourAndMinute);
            String[] array = hourAndMinute.split(":");
            if (array.length == 2) {
                String content1 = array[0] + unit11 + array[1] + unit12;
                tvTime1.setText(BigSmallFontManager.createTimeValue(content1, getActivity(), 13, array1));
                sleepbeltValue[1] = Integer.parseInt(array[0]) * 100 + Integer.parseInt(array[1]);
            }
        }

        String unit21 = getResources().getString(R.string.common_hour);
        String unit22 = getResources().getString(R.string.common_minute);
        String[] array2 = {unit21, unit22};
        if (sleepTime.length() == 0) {
            String content2 = "00" + unit21 + "00" + unit22;
            tvTime2.setText(BigSmallFontManager.createTimeValue(content2, getActivity(), 13, array2));
        } else {
            int sleep = hourMinuteToInt(sleepTime);
            int getup = hourMinuteToInt(getupTime);
            if (sleep > getup) {
                sleep = -24 * 60 + sleep;
            }
            String hour = (getup - sleep) / 60 > 9 ? ("" + (getup - sleep) / 60) : ("0" + (getup - sleep) / 60);
            String minute =  (getup - sleep) % 60 > 9 ? ("" + (getup - sleep) % 60) : ("0" + (getup - sleep) % 60);
            String content2 = hour + unit21 + minute + unit22;
            tvTime2.setText(BigSmallFontManager.createTimeValue(content2, getActivity(), 13, array2));
            sleepbeltValue[2] = ((getup - sleep) / 60) * 100 + ((getup - sleep) % 60);
        }

        String unit4 = getResources().getString(R.string.common_times_minute);
        String[] array4 = {unit4};
        if (count > 0) {
            String content3 = averageHeart / count + unit4;
            tvTime3.setText(BigSmallFontManager.createTimeValue(content3, getActivity(), 13, array4));
            String unit1 = getResources().getString(R.string.common_times_minute);
            tvSleepBottomCount1.setText(BigSmallFontManager.createTimeValue(content3, getActivity(), 13, unit1));
            sleepbeltValue[3] = (int)(averageHeart / count);
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
            sleepbeltValue[4] = (int)(averageBreath / count);
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
        String[] array7 = {getResources().getString(R.string.common_times)};
        String content7 = averageApnea + unit;
        tvTime7.setText(BigSmallFontManager.createTimeValue(content7, getActivity(), 13, array7));

        if (count > 0) {
            String content = bodyMotion + getResources().getString(R.string.common_times);
            tvSleepBottomCount3.setText(BigSmallFontManager.createTimeValue(content, getActivity(), 13, unit));
        } else {
            String content = "0 " + getResources().getString(R.string.common_times);
            tvSleepBottomCount3.setText(BigSmallFontManager.createTimeValue(content, getActivity(), 13, unit));
        }
        calculateSleepValue();
    }

//    private void currentSleepAndGetupData(String day) {
//        if (SleepAndGetupTimeManager.times.size() == 0) {
//            return;
//        }
//        String[] days = SleepAndGetupTimeManager.times.keySet().toArray(new String[0]);
//        Arrays.sort(days);
//        int j = -1;
//        for (int i = 0; i < days.length; i++) {
//            if (day.equals(days[i])) {
//                j = i;
//                break;
//            }
//        }
//        if (j < 0) {
//            Toast.makeText(getActivity(), R.string.current_date_no_date, Toast.LENGTH_LONG).show();
//            return;
//        }
//        cursor[0] = j;
//        List<String> items = SleepAndGetupTimeManager.times.get(days[cursor[0]]);
//        Collections.sort(items);
//        cursor[1] = items.size() - 1;
//        String duration = items.get(cursor[1]);
//        sleepTime = duration.split("&")[0];
//        getupTime = duration.split("&")[1];
//    }


    // 重新刷新日报
    public void refreshDayReport() {

        refreshAllChartX();
        refreshData(isBlet);
    }

    private void refreshAllChartX() {
        refreshX(chart);
        refreshX(chart2);
        refreshX(chart3);
        refreshX(chart4);
    }

    private void refreshX(LineChart chart) {
        int sleep = hourMinuteToInt(sleepTime);
        int getup = hourMinuteToInt(getupTime);
        if (sleep > getup) {
            sleep = -24 * 60 + sleep;
        }
        chart.getXAxis().setLabelCount(6,true);
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
        setValue1.clear();
        int getupT = hourMinuteToInt(getupTime);
        if (mlist.size() > 0) {
            int deepSleep = 0;
            int middleSleep = 0;
            int cheapSleep = 0;
            int getup = 0;
            int getupCount = 0;
            int bodyMotionCount = 0;
            int startTime = 0; // 开始计算的时间
            int i = 0;
            int apneaTotal = 0;
            int bodyMotionTotal = 0;
            int apneaCount = 0;
            for (RecordModel model : mlist) { // 计算清醒和体动
                i++;
                bodyMotionTotal += model.getBodyMotion();
                apneaTotal += model.getBreatheStop();
                if (model.getBreatheStop() > 0) {
                    apneaCount += 1;
                }
                if (startTime == 0) {
                    startTime = model.getTime();
                }

                int a = model.getGetupFlag(); // 离床 在床
                int b = model.getBodyMotion(); // 体动
                if (a == 0) { // 离床，清醒
                    int time = hourMinuteToInt(timeToDate(model.getTime()));
                    if (time > getupT) {
                        time = -24 * 60 + time;
                    }
                    if (!setValue1.contains(time)) {
                        chart1Values.add(new Entry(time,4));
                        setValue1.add(time);
                    }
                    if (startTime > 0) {
                        getup += model.getTime() - startTime;
                        startTime = model.getTime();
                    }
                    getupCount += 1;
                    continue;
                }
                if (model.getTime() - startTime >= 60 && bodyMotionCount >= 5) {
                    int time2 = hourMinuteToInt(timeToDate(startTime));
                    if (time2 > getupT) {
                        time2 = -24 * 60 + time2;
                    }
                    if (!setValue1.contains(time2)) {
                        chart1Values.add(new Entry(time2,3));
                        setValue1.add(time2);
                    }

                    int time = hourMinuteToInt(timeToDate(model.getTime()));
                    if (time > getupT) {
                        time = -24 * 60 + time;
                    }
                    if (!setValue1.contains(time)) {
                        chart1Values.add(new Entry(time,3));
                        setValue1.add(time);
                    }
                    cheapSleep += model.getTime() - startTime;
                    bodyMotionCount = 0;
                    startTime = model.getTime();
                    continue;
                }
                if (model.getTime() - startTime >= 3 * 60 && bodyMotionCount > 0) {
                    int time2 = hourMinuteToInt(timeToDate(startTime));
                    if (time2 > getupT) {
                        time2 = -24 * 60 + time2;
                    }
                    if (!setValue1.contains(time2)) {
                        chart1Values.add(new Entry(time2,2));
                        setValue1.add(time2);
                    }

                    int time = hourMinuteToInt(timeToDate(model.getTime()));
                    if (time > getupT) {
                        time = -24 * 60 + time;
                    }
                    if (!setValue1.contains(time)) {
                        chart1Values.add(new Entry(time,2));
                        setValue1.add(time);
                    }
                    middleSleep += model.getTime() - startTime;
                    bodyMotionCount = 0;
                    startTime = model.getTime();
                    continue;
                }
                if (model.getTime() - startTime >= 5 * 60) {
                    if (bodyMotionCount > 2) {
                        middleSleep += model.getTime() - startTime;
                        int time2 = hourMinuteToInt(timeToDate(startTime));
                        if (time2 > getupT) {
                            time2 = -24 * 60 + time2;
                        }
                        if (!setValue1.contains(time2)) {
                            chart1Values.add(new Entry(time2,2));
                            setValue1.add(time2);
                        }

                        int time = hourMinuteToInt(timeToDate(model.getTime()));
                        if (time > getupT) {
                            time = -24 * 60 + time;
                        }
                        if (!setValue1.contains(time)) {
                            chart1Values.add(new Entry(time,2));
                            setValue1.add(time);
                        }

                    } else {
                        deepSleep += model.getTime() - startTime;
                        int time2 = hourMinuteToInt(timeToDate(startTime));
                        if (time2 > getupT) {
                            time2 = -24 * 60 + time2;
                        }
                        if (!setValue1.contains(time2)) {
                            chart1Values.add(new Entry(time2,1));
                            setValue1.add(time2);
                        }

                        int time = hourMinuteToInt(timeToDate(model.getTime()));
                        if (time > getupT) {
                            time = -24 * 60 + time;
                        }
                        if (!setValue1.contains(time)) {
                            chart1Values.add(new Entry(time,1));
                            setValue1.add(time);
                        }
                    }
                    bodyMotionCount = 0;
                    startTime = model.getTime();
                    continue;
                }
                if (i == mlist.size()) {
                    if (model.getTime() - startTime <= 3 * 60) {
                        if (bodyMotionCount > 5) {
                            cheapSleep += model.getTime() - startTime;
                            int time2 = hourMinuteToInt(timeToDate(startTime));
                            if (time2 > getupT) {
                                time2 = -24 * 60 + time2;
                            }
                            if (!setValue1.contains(time2)) {
                                chart1Values.add(new Entry(time2,4));
                                setValue1.add(time2);
                            }

                            int time = hourMinuteToInt(timeToDate(model.getTime()));
                            if (time > getupT) {
                                time = -24 * 60 + time;
                            }
                            if (!setValue1.contains(time)) {
                                chart1Values.add(new Entry(time,4));
                                setValue1.add(time);
                            }
                        }else if (bodyMotionCount > 0) {
                            middleSleep += model.getTime() - startTime;
                            int time2 = hourMinuteToInt(timeToDate(startTime));
                            if (time2 > getupT) {
                                time2 = -24 * 60 + time2;
                            }
                            if (!setValue1.contains(time2)) {
                                chart1Values.add(new Entry(time2,3));
                                setValue1.add(time2);
                            }

                            int time = hourMinuteToInt(timeToDate(model.getTime()));
                            if (time > getupT) {
                                time = -24 * 60 + time;
                            }
                            if (!setValue1.contains(time)) {
                                chart1Values.add(new Entry(time,3));
                                setValue1.add(time);
                            }
                        } else {
                            deepSleep += model.getTime() - startTime;
                            int time2 = hourMinuteToInt(timeToDate(startTime));
                            if (time2 > getupT) {
                                time2 = -24 * 60 + time2;
                            }
                            if (!setValue1.contains(time2)) {
                                chart1Values.add(new Entry(time2,2));
                                setValue1.add(time2);
                            }

                            int time = hourMinuteToInt(timeToDate(model.getTime()));
                            if (time > getupT) {
                                time = -24 * 60 + time;
                            }
                            if (!setValue1.contains(time)) {
                                chart1Values.add(new Entry(time,2));
                                setValue1.add(time);
                            }

                        }
                    }
                }
                bodyMotionCount += b;
                continue;
            }
            if (chart1Values.size() > 0) {
                Collections.sort(chart1Values, comparatorByX);
                int sleep = hourMinuteToInt(sleepTime);
                int getupB = hourMinuteToInt(getupTime);
                if (sleep > getupB) {
                    sleep = -24 * 60 + sleep;
                }
                if (chart1Values.get(0).getX() > sleep) {
                    chart1Values.add(0, new Entry(chart1Values.get(0).getX() - 1, 4));
                    chart1Values.add(0, new Entry(sleep, 4));
                }
                if (chart1Values.get(chart1Values.size() - 1).getX() < getupB) {
                    chart1Values.add(new Entry(chart1Values.get(chart1Values.size() - 1).getX() + 1, 4));
                    chart1Values.add(new Entry(getupB, 4));
                }
            }
//            int k = 0;
//            for (Entry item : chart1Values) {
//                k += 1;
//                System.out.println("第" + k + "：" + item.getX());
//            }
            int c = deepSleep + middleSleep + cheapSleep + getup;
            if (c > 0) {
                int sleepB = hourMinuteToInt(sleepTime);
                System.out.println("计算得到的结果：" + deepSleep + " " + middleSleep + " " + cheapSleep + " " + getup);
                int deep =  deepSleep * 100 / c;
                int middle = middleSleep * 100 / c;
                int cheap = cheapSleep * 100 / c;
                int get = 100 - deep - middle - cheap;
                circleSleep1.setPercentage(deep);
                circleSleep2.setPercentage(middle);
                circleSleep3.setPercentage(cheap);
                circleSleep4.setPercentage(get);
                tvCircleSleep1.setText(deep + "%");
                tvCircleSleep2.setText(middle + "%");
                tvCircleSleep3.setText(cheap + "%");
                tvCircleSleep4.setText(get + "%");
                float d = ((float)deepSleep) / ((float)c);
                float d2 =  Math.abs((float)0.25 - d);
                int d3 =  (int) (d2 * 100 * 0.5);
                int d4 = (int) (apneaCount * 0.5);
                int c1 = 0;
                if (c > 10 * 60 * 60) {
                    c1 = c / 3600 - 10;
                    grade = Math.max(93 - 3 * c1 - d3 - d4 - get, 20);
                } else if (c < 7 * 60 * 60) {
                    c1 = 7 - c / 3600;
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
                System.out.println("打印计算的值日：" + grade + " " + c1 + " " + d3 + " " + d4 + " " + get);
                grade = Math.max(0, grade);
//                grade -= bodyMotionTotal / 3600 * 2;
//                grade -= apneaTotal / 3600 * 2;
                grade = Math.min(100, grade);
            } else {
                circleSleep1.setPercentage(0);
                circleSleep2.setPercentage(0);
                circleSleep3.setPercentage(0);
                circleSleep4.setPercentage(0);
                tvCircleSleep1.setText("0%");
                tvCircleSleep2.setText("0%");
                tvCircleSleep3.setText("0%");
                tvCircleSleep4.setText("0%");
                grade = 0;
            }
        } else {
            grade = 0;
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
        refreshGrade();
        circlePercentView.setCurProcess(grade);
        circlePercentView.setReachedColor(getResources().getColor(GradeColor.convertGradeToColor(grade)));
        System.out.println("计算得到的最终得分为：" + grade);
        sleepbeltValue[0] = grade;
        if (grade >= 95) {
            tvSleepDesc.setText(R.string.score_95_comment);
        } else if (grade >= 80) {
            tvSleepDesc.setText(R.string.score_80_comment);
        } else if (grade >= 60) {
            tvSleepDesc.setText(R.string.score_60_comment);
        } else {
            tvSleepDesc.setText(R.string.score_40_comment);
        }
        if (!bSleepBletValueRead) {
            YMApplication.getInstance().setSleepbeltValue(sleepbeltValue);
        }
        bSleepBletValueRead = true;
        setCharData();
        chart.invalidate();
    }

    private void addApneaIcon(int time, int stopTime, int heart) {
        if (countApnea >= 20) {
            return;
        }
        int sleep = hourMinuteToInt(sleepTime);
        int getup = hourMinuteToInt(getupTime);
        int now = hourMinuteToInt(timeToDate(time));

        int rId = 0;
        countApnea += 1;
        if (countApnea == 1) {
            rId = R.id.iv_apnea_1;
        } else if (countApnea == 2) {
            rId = R.id.iv_apnea_2;
        } else if (countApnea == 3) {
            rId = R.id.iv_apnea_3;
        } else if (countApnea == 4) {
            rId = R.id.iv_apnea_4;
        } else if (countApnea == 5) {
            rId = R.id.iv_apnea_5;
        } else if (countApnea == 6) {
            rId = R.id.iv_apnea_6;
        } else if (countApnea == 7) {
            rId = R.id.iv_apnea_7;
        } else if (countApnea == 8) {
            rId = R.id.iv_apnea_8;
        } else if (countApnea == 9) {
            rId = R.id.iv_apnea_9;
        } else if (countApnea == 10) {
            rId = R.id.iv_apnea_10;
        } else if (countApnea == 11) {
            rId = R.id.iv_apnea_11;
        } else if (countApnea == 12) {
            rId = R.id.iv_apnea_12;
        } else if (countApnea == 13) {
            rId = R.id.iv_apnea_13;
        } else if (countApnea == 14) {
            rId = R.id.iv_apnea_14;
        } else if (countApnea == 15) {
            rId = R.id.iv_apnea_15;
        } else if (countApnea == 16) {
            rId = R.id.iv_apnea_16;
        } else if (countApnea == 17) {
            rId = R.id.iv_apnea_17;
        } else if (countApnea == 18) {
            rId = R.id.iv_apnea_18;
        } else if (countApnea == 19) {
            rId = R.id.iv_apnea_19;
        } else if (countApnea == 20) {
            rId = R.id.iv_apnea_20;
        }
        ImageView ivLeft = new ImageView(getActivity());
        ivLeft.setId(rId);
        ivLeft.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ivLeft.setImageResource(R.mipmap.report_icon_apnea);

        ConstraintLayout.LayoutParams ivLeftLayoutParams = new ConstraintLayout.LayoutParams(
                DisplayUtil.dip2px(26, getActivity()), DisplayUtil.dip2px(26, getActivity()));
        float width = ScreenInfoUtils.getScreenWidth(getActivity()) - DisplayUtil.dip2px(43, getActivity());
        float x = (float)(now - sleep) / (getup - sleep) * width;
        if (x >= width - DisplayUtil.dip2px(52, getActivity())) {
            x = width - DisplayUtil.dip2px(52, getActivity());
        }
        ivLeftLayoutParams.setMarginStart((int)x);
        ivLeftLayoutParams.topMargin = DisplayUtil.dip2px(20, getActivity());
        ivLeftLayoutParams.topToTop = R.id.chart1;
        ivLeftLayoutParams.leftToLeft = R.id.chart1;
        ivLeft.setLayoutParams(ivLeftLayoutParams);
        ivLeft.setOnClickListener(clickListener);
        clRoot.addView(ivLeft);
        ivList.add(ivLeft);
    }

    View.OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ApneaDialogActivity.class);
            if (v.getId() == R.id.iv_apnea_1) {
                if (apneaList.size() <= 0){
                    return;
                }
                RecordModel model = apneaList.get(0);
                intent.putExtra("time", model.getTime());
                intent.putExtra("stopTime", model.getBreatheStop());
                intent.putExtra("heart", model.getHeartRate());
            } else if(v.getId() == R.id.iv_apnea_2) {
                if (apneaList.size() < 1){
                    return;
                }
                RecordModel model = apneaList.get(1);
                intent.putExtra("time", model.getTime());
                intent.putExtra("stopTime", model.getBreatheStop());
                intent.putExtra("heart", model.getHeartRate());
            } else if(v.getId() == R.id.iv_apnea_3) {
                if (apneaList.size() < 2){
                    return;
                }
                RecordModel model = apneaList.get(2);
                intent.putExtra("time", model.getTime());
                intent.putExtra("stopTime", model.getBreatheStop());
                intent.putExtra("heart", model.getHeartRate());
            } else if(v.getId() == R.id.iv_apnea_4) {
                if (apneaList.size() < 3){
                    return;
                }
                RecordModel model = apneaList.get(3);
                intent.putExtra("time", model.getTime());
                intent.putExtra("stopTime", model.getBreatheStop());
                intent.putExtra("heart", model.getHeartRate());
            } else if(v.getId() == R.id.iv_apnea_5) {
                if (apneaList.size() < 4){
                    return;
                }
                RecordModel model = apneaList.get(4);
                intent.putExtra("time", model.getTime());
                intent.putExtra("stopTime", model.getBreatheStop());
                intent.putExtra("heart", model.getHeartRate());
            } else if(v.getId() == R.id.iv_apnea_6) {
                if (apneaList.size() < 5){
                    return;
                }
                RecordModel model = apneaList.get(5);
                intent.putExtra("time", model.getTime());
                intent.putExtra("stopTime", model.getBreatheStop());
                intent.putExtra("heart", model.getHeartRate());
            } else if(v.getId() == R.id.iv_apnea_7) {
                if (apneaList.size() < 6){
                    return;
                }
                RecordModel model = apneaList.get(6);
                intent.putExtra("time", model.getTime());
                intent.putExtra("stopTime", model.getBreatheStop());
                intent.putExtra("heart", model.getHeartRate());
            } else if(v.getId() == R.id.iv_apnea_8) {
                if (apneaList.size() < 7){
                    return;
                }
                RecordModel model = apneaList.get(7);
                intent.putExtra("time", model.getTime());
                intent.putExtra("stopTime", model.getBreatheStop());
                intent.putExtra("heart", model.getHeartRate());
            } else if(v.getId() == R.id.iv_apnea_9) {
                if (apneaList.size() < 8){
                    return;
                }
                RecordModel model = apneaList.get(8);
                intent.putExtra("time", model.getTime());
                intent.putExtra("stopTime", model.getBreatheStop());
                intent.putExtra("heart", model.getHeartRate());
            } else if(v.getId() == R.id.iv_apnea_10) {
                if (apneaList.size() < 9){
                    return;
                }
                RecordModel model = apneaList.get(9);
                intent.putExtra("time", model.getTime());
                intent.putExtra("stopTime", model.getBreatheStop());
                intent.putExtra("heart", model.getHeartRate());
            } else if(v.getId() == R.id.iv_apnea_11) {
                if (apneaList.size() < 10){
                    return;
                }
                RecordModel model = apneaList.get(10);
                intent.putExtra("time", model.getTime());
                intent.putExtra("stopTime", model.getBreatheStop());
                intent.putExtra("heart", model.getHeartRate());
            } else if(v.getId() == R.id.iv_apnea_12) {
                if (apneaList.size() < 11){
                    return;
                }
                RecordModel model = apneaList.get(11);
                intent.putExtra("time", model.getTime());
                intent.putExtra("stopTime", model.getBreatheStop());
                intent.putExtra("heart", model.getHeartRate());
            } else if(v.getId() == R.id.iv_apnea_13) {
                if (apneaList.size() < 12){
                    return;
                }
                RecordModel model = apneaList.get(12);
                intent.putExtra("time", model.getTime());
                intent.putExtra("stopTime", model.getBreatheStop());
                intent.putExtra("heart", model.getHeartRate());
            } else if(v.getId() == R.id.iv_apnea_14) {
                if (apneaList.size() < 13){
                    return;
                }
                RecordModel model = apneaList.get(13);
                intent.putExtra("time", model.getTime());
                intent.putExtra("stopTime", model.getBreatheStop());
                intent.putExtra("heart", model.getHeartRate());
            } else if(v.getId() == R.id.iv_apnea_15) {
                if (apneaList.size() < 14){
                    return;
                }
                RecordModel model = apneaList.get(14);
                intent.putExtra("time", model.getTime());
                intent.putExtra("stopTime", model.getBreatheStop());
                intent.putExtra("heart", model.getHeartRate());
            } else if(v.getId() == R.id.iv_apnea_16) {
                if (apneaList.size() < 15){
                    return;
                }
                RecordModel model = apneaList.get(15);
                intent.putExtra("time", model.getTime());
                intent.putExtra("stopTime", model.getBreatheStop());
                intent.putExtra("heart", model.getHeartRate());
            } else if(v.getId() == R.id.iv_apnea_17) {
                if (apneaList.size() < 16){
                    return;
                }
                RecordModel model = apneaList.get(16);
                intent.putExtra("time", model.getTime());
                intent.putExtra("stopTime", model.getBreatheStop());
                intent.putExtra("heart", model.getHeartRate());
            } else if(v.getId() == R.id.iv_apnea_18) {
                if (apneaList.size() < 17){
                    return;
                }
                RecordModel model = apneaList.get(17);
                intent.putExtra("time", model.getTime());
                intent.putExtra("stopTime", model.getBreatheStop());
                intent.putExtra("heart", model.getHeartRate());
            } else if(v.getId() == R.id.iv_apnea_19) {
                if (apneaList.size() < 18){
                    return;
                }
                RecordModel model = apneaList.get(18);
                intent.putExtra("time", model.getTime());
                intent.putExtra("stopTime", model.getBreatheStop());
                intent.putExtra("heart", model.getHeartRate());
            } else if(v.getId() == R.id.iv_apnea_20) {
                if (apneaList.size() < 19){
                    return;
                }
                RecordModel model = apneaList.get(19);
                intent.putExtra("time", model.getTime());
                intent.putExtra("stopTime", model.getBreatheStop());
                intent.putExtra("heart", model.getHeartRate());
            }
            startActivity(intent);
        }
    };
}
