package com.zhang.xiaofei.smartsleep.UI.Home;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shizhefei.fragment.LazyFragment;
import com.zhang.xiaofei.smartsleep.Kit.BigSmallFont.BigSmallFontManager;
import com.zhang.xiaofei.smartsleep.Kit.DisplayUtil;
import com.zhang.xiaofei.smartsleep.Model.Alarm.AlarmModel;
import com.zhang.xiaofei.smartsleep.Model.Record.RecordModel;
import com.zhang.xiaofei.smartsleep.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private ImageButton ibLeftPre;
    private ImageButton ibRightNex;
    private TextView tvSimulationData;
    private TextView tvSleepRank;
    private Realm mRealm;
    private int currentTime = 0;
    Map<Integer, List<RecordModel>> mMap = new HashMap<Integer, List<RecordModel>>();
    LineDataSet set1;
    LineDataSet set2;
    ArrayList<Entry> set1values;
    private Boolean bBelt = false; // 刷新睡眠带，睡眠纽扣
    int[] scores = new int[7]; // 得分
    int[] sleepOneDayTimes = new int[7]; // 深睡眠时长
    int nosleepTimeTotal = 0; // 清醒时长
    int noSleepMinuteCount = 0; // 清醒次数
    boolean bChart = false; //
    boolean bChart2 = false;

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
        getDayData(currentTime);
        System.out.println("当前周有的数据为：" + mMap.size());

        ibLeftPre = (ImageButton)findViewById(R.id.ib_left_pre);
        ibLeftPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTime -= 7 * 24 * 60 * 60;
                tvSimulationData.setText(getTextValue());
                getDayData(currentTime);
                System.out.println("当前周有的数据为：" + mMap.size());
            }
        });
        ibRightNex = (ImageButton)findViewById(R.id.ib_right_next);
        ibRightNex.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                currentTime += 7 * 24 * 60 * 60;
                tvSimulationData.setText(getTextValue());
                getDayData(currentTime);
                System.out.println("当前周有的数据为：" + mMap.size());
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

    private void getDayData(int startTime) {
        mMap.clear();
        mRealm = Realm.getDefaultInstance();
        RealmResults<RecordModel> list = mRealm.where(RecordModel.class)
                .greaterThan("time", startTime)
                .lessThan("time", startTime + 24 * 60 * 60 * 7)
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
        tvTime1 = (TextView)findViewById(R.id.tv_time_1);
        tvTime2 = (TextView)findViewById(R.id.tv_time_2);
        tvTime3 = (TextView)findViewById(R.id.tv_time_3);
        tvTime4 = (TextView)findViewById(R.id.tv_time_4);
        tvTime5 = (TextView)findViewById(R.id.tv_time_5);
        tvTime6 = (TextView)findViewById(R.id.tv_time_6);

        tvSleepTime4 = (TextView)findViewById(R.id.tv_sleep_time_4);
        tvSleepTime5 = (TextView)findViewById(R.id.tv_sleep_time_5);
        ivSleepTime4 = (ImageView)findViewById(R.id.iv_time_4);
        ivSleepTime5 =(ImageView)findViewById(R.id.iv_time_5);
        if (bBelt == false) {
            refreshSleepStatistic(false);
        }

    }

    public void refreshSleepStatistic(boolean isBlet) {
        System.out.println("周报中刷新UI");
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
        set1 = new LineDataSet(set1values, "");

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
        Integer minTime = 0;
        Integer maxTime = 0;
        long averageHeart = 0;
        long averageBreath = 0;
        Integer count = 0;
        Integer bodyMotion = 0;
        if (mMap.size() > 0) {
            for (Map.Entry<Integer, List<RecordModel>> entry: mMap.entrySet()) {
                if (entry.getKey() < currentTime / 60 + 24 * 60) {
                    for (RecordModel model: entry.getValue()) {
                        bodyMotion += model.getBodyMotion();
                        int grade = 90 - (bodyMotion / 100) * 20; // 计算当前日期的得分
                        grade = Math.min(90, grade);
                        grade = Math.max(70, grade);
                        if (scores[0] == 0) {
                            scores[0] = grade;
                        } else {
                            scores[0] = (scores[0] + grade) / 2;
                        }
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
                } else if (entry.getKey() < currentTime / 60 + 2 * 24 * 60) {
                    for (RecordModel model: entry.getValue()) {
                        bodyMotion += model.getBodyMotion();
                        int grade = 90 - (bodyMotion / 100) * 20; // 计算当前日期的得分
                        grade = Math.min(90, grade);
                        grade = Math.max(70, grade);
                        if (scores[1] == 0) {
                            scores[1] = grade;
                        } else {
                            scores[1] = (scores[1] + grade) / 2;
                        }
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
                } else if (entry.getKey() < currentTime / 60 + 3 * 24 * 60) {
                    for (RecordModel model: entry.getValue()) {
                        bodyMotion += model.getBodyMotion();
                        int grade = 90 - (bodyMotion / 100) * 20; // 计算当前日期的得分
                        grade = Math.min(90, grade);
                        grade = Math.max(70, grade);
                        if (scores[2] == 0) {
                            scores[2] = grade;
                        } else {
                            scores[2] = (scores[2] + grade) / 2;
                        }
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
                } else if (entry.getKey() < currentTime / 60 + 4 * 24 * 60) {
                    for (RecordModel model: entry.getValue()) {
                        bodyMotion += model.getBodyMotion();
                        int grade = 90 - (bodyMotion / 100) * 20; // 计算当前日期的得分
                        grade = Math.min(90, grade);
                        grade = Math.max(70, grade);
                        if (scores[3] == 0) {
                            scores[3] = grade;
                        } else {
                            scores[3] = (scores[3] + grade) / 2;
                        }
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
                } else if (entry.getKey() < currentTime / 60 + 5 * 24 * 60) {
                    for (RecordModel model: entry.getValue()) {
                        bodyMotion += model.getBodyMotion();
                        int grade = 90 - (bodyMotion / 100) * 20; // 计算当前日期的得分
                        grade = Math.min(90, grade);
                        grade = Math.max(70, grade);
                        if (scores[4] == 0) {
                            scores[4] = grade;
                        } else {
                            scores[4] = (scores[4] + grade) / 2;
                        }
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
                } else if (entry.getKey() < currentTime / 60 + 6 * 24 * 60) {
                    for (RecordModel model: entry.getValue()) {
                        bodyMotion += model.getBodyMotion();
                        int grade = 90 - (bodyMotion / 100) * 20; // 计算当前日期的得分
                        grade = Math.min(90, grade);
                        grade = Math.max(70, grade);
                        if (scores[5] == 0) {
                            scores[5] = grade;
                        } else {
                            scores[5] = (scores[5] + grade) / 2;
                        }
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
                } else {
                    for (RecordModel model: entry.getValue()) {
                        bodyMotion += model.getBodyMotion();
                        int grade = 90 - (bodyMotion / 100) * 20; // 计算当前日期的得分
                        grade = Math.min(90, grade);
                        grade = Math.max(70, grade);
                        if (scores[6] == 0) {
                            scores[6] = grade;
                        } else {
                            scores[6] = (scores[6] + grade) / 2;
                        }
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
        }
        if (tvTime4 != null && tvTime5 != null) {
            String unit4 = getResources().getString(R.string.common_times_minute);
            String[] array4 = {unit4};
            String content4 = (averageHeart > 9 ? "" + averageHeart : "0" + averageHeart)  + unit4;
            tvTime4.setText(BigSmallFontManager.createTimeValue(content4, getActivity(), 13, array4));
            String content5 = (averageBreath > 9 ? "" + averageBreath : "0" + averageBreath)  + unit4;
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
        chart.invalidate();

        int score = 0;
        for (int i = 0; i < scores.length; i++) {
            score += scores[i];
        }
        score = score / 7;
        String unit = getResources().getString(R.string.common_minute2);
        String[] array = {unit};
        String content = score + unit;
        tvSleepValue.setText(BigSmallFontManager.createTimeValue(content, getActivity(), 30, array));
        if (score >= 85) {
            tvSleepRank.setText(R.string.common_very_good);
        } else if (score >= 75) {
            tvSleepRank.setText(R.string.common_Awesome);
        } else {
            tvSleepRank.setText(R.string.common_so_so);
        }

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
            int time = 0, time2 = 0, time3 = 0, time4 = 0, time5 = 0, time6 = 0, time7 = 0;
            int bodyMotionCount = 0;
            int getupCount = 0;
            for (Map.Entry<Integer, List<RecordModel>> entry : mMap.entrySet()) {
                if (entry.getKey() < currentTime / 60 + 24 * 60) {
                    if (time == 0) { // 如果时间没赋过值，则将第一个数据赋值给它
                        deepSleep = 0;
                        time = entry.getKey() + 5;
                        getupCount = 0;
                        bodyMotionCount = 0;
                    }
                    if (entry.getKey() <= time) {

                    } else {
                        time += 5;
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
                    sleepOneDayTimes[0] = deepSleep * 5;
                } else if (entry.getKey() < currentTime / 60 + 24 * 60 * 2) {
                    if (time2 == 0) {
                        deepSleep = 0;
                        time2 = entry.getKey() + 5;
                        getupCount = 0;
                        bodyMotionCount = 0;
                    }
                    if (entry.getKey() <= time2) {

                    } else {
                        time2 += 5;
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
                    sleepOneDayTimes[1] = deepSleep * 5;
                } else if (entry.getKey() < currentTime / 60 + 24 * 60 * 3) {
                    if (time3 == 0) {
                        deepSleep = 0;
                        time3 = entry.getKey() + 5;
                        getupCount = 0;
                        bodyMotionCount = 0;
                    }
                    if (entry.getKey() <= time3) {

                    } else {
                        time3 += 5;
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
                    sleepOneDayTimes[2] = deepSleep * 5;
                } else if (entry.getKey() < currentTime / 60 + 24 * 60 * 4) {
                    if (time4 == 0) {
                        deepSleep = 0;
                        time4 = entry.getKey() + 5;
                        getupCount = 0;
                        bodyMotionCount = 0;
                    }
                    if (entry.getKey() <= time4) {

                    } else {
                        time4 += 5;
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
                    sleepOneDayTimes[3] = deepSleep * 5;
                } else if (entry.getKey() < currentTime / 60 + 24 * 60 * 5) {
                    if (time5 == 0) {
                        deepSleep = 0;
                        time5 = entry.getKey() + 5;
                        getupCount = 0;
                        bodyMotionCount = 0;
                    }
                    if (entry.getKey() <= time5) {

                    } else {
                        time5 += 5;
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
                    sleepOneDayTimes[4] = deepSleep * 5;
                } else if (entry.getKey() < currentTime / 60 + 24 * 60 * 6) {
                    if (time6 == 0) {
                        deepSleep = 0;
                        time6 = entry.getKey() + 5;
                        getupCount = 0;
                        bodyMotionCount = 0;
                    }
                    if (entry.getKey() <= time6) {

                    } else {
                        time6 += 5;
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
                    sleepOneDayTimes[5] = deepSleep * 5;
                } else {
                    if (time7 == 0) {
                        deepSleep = 0;
                        time7 = entry.getKey() + 5;
                        getupCount = 0;
                        bodyMotionCount = 0;
                    }
                    if (entry.getKey() <= time7) {

                    } else {
                        time7 += 5;
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
                    sleepOneDayTimes[6] = deepSleep * 5;
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

            if (chart2 == null) {
                return;
            }
            setCharData2();
            chart2.invalidate();
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
