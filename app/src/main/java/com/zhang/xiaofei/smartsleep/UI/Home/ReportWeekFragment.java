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
    private ImageButton ibLeftPre;
    private ImageButton ibRightNex;
    private TextView tvSimulationData;
    private Realm mRealm;
    private int currentTime = 0;
    Map<Integer, List<RecordModel>> mMap = new HashMap<Integer, List<RecordModel>>();
    LineDataSet set1;
    LineDataSet set2;
    ArrayList<Entry> set1values;
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
        String unit = getResources().getString(R.string.common_minute2);
        String[] array = {unit};
        String content = "00" + unit;
        tvSleepValue.setText(BigSmallFontManager.createTimeValue(content, getActivity(), 30, array));
        tvSleepAverageTime = (TextView)findViewById(R.id.tv_sleep_average_time);
        String unit01 = getResources().getString(R.string.common_hour);
        String unit02 = getResources().getString(R.string.common_minute);
        String[] array1 = {unit01, unit02};
        String content1 = "17" + unit01 + "30" + unit02;
        tvSleepAverageTime.setText(BigSmallFontManager.createTimeValue(content1, getActivity(), 13, array1));
        initialText();

        initialCurrentTime();
        mRealm = Realm.getDefaultInstance();
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
        String unit01 = getResources().getString(R.string.common_hour);
        String unit02 = getResources().getString(R.string.common_minute);
        String[] array = {unit01, unit02};
        String content = "00" + unit01 + "00" + unit02;
        tvSleepAverageTime.setText(BigSmallFontManager.createTimeValue(content, getActivity(), 20, array));
        tvTime1 = (TextView)findViewById(R.id.tv_time_1);
        String unit11 = getResources().getString(R.string.common_hour2);
        String unit12 = getResources().getString(R.string.common_minute3);
        String[] array1 = {unit11, unit12};
        String content1 = "00" + unit11 + "00" + unit12;
        tvTime1.setText(BigSmallFontManager.createTimeValue(content1, getActivity(), 13, array1));
        tvTime2 = (TextView)findViewById(R.id.tv_time_2);
        String unit21 = getResources().getString(R.string.common_hour);
        String unit22 = getResources().getString(R.string.common_minute);
        String[] array2 = {unit21, unit22};
        String content2 = "00" + unit21 + "00" + unit22;
        tvTime2.setText(BigSmallFontManager.createTimeValue(content2, getActivity(), 13, array2));
        tvTime3 = (TextView)findViewById(R.id.tv_time_3);
        tvTime3.setText(BigSmallFontManager.createTimeValue(content2, getActivity(), 13, array2));
        tvTime4 = (TextView)findViewById(R.id.tv_time_4);
        String unit4 = getResources().getString(R.string.common_times_minute);
        String[] array4 = {unit4};
        String content4 = "00" + unit4;
        tvTime4.setText(BigSmallFontManager.createTimeValue(content4, getActivity(), 13, array4));
        tvTime5 = (TextView)findViewById(R.id.tv_time_5);
        tvTime5.setText(BigSmallFontManager.createTimeValue(content4, getActivity(), 13, array4));
        tvTime6 = (TextView)findViewById(R.id.tv_time_6);
        tvTime6.setText(BigSmallFontManager.createTimeValue(content4, getActivity(), 13, array4));
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
            chart.setDragEnabled(false);
            chart.setScaleEnabled(false);
            // chart.setScaleXEnabled(true);
            // chart.setScaleYEnabled(true);
            // force pinch zoom along both axis
            chart.setPinchZoom(false);
        }

        XAxis xAxis;
        {   // // X-Axis Style // //
            xAxis = chart.getXAxis();
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
        }

        YAxis yAxis;
        {   // // Y-Axis Style // //
            yAxis = chart.getAxisLeft();
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
        }

        {
            // draw limit lines behind data instead of on top
            yAxis.setDrawLimitLinesBehindData(false);
            xAxis.setDrawLimitLinesBehindData(false);
        }

        setCharData();

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        l.setEnabled(false);
    }

    private void setCharData() {
        set1values = new ArrayList<>();

        set1values.add(new Entry(1, 60));
        set1values.add(new Entry(2, 80));
        set1values.add(new Entry(3, 100));
        set1values.add(new Entry(4, 40));

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(set1values);
            set1.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(set1values, "");

            set1.setDrawIcons(false);
            set1.setDrawHorizontalHighlightIndicator(false);
            set1.setDrawVerticalHighlightIndicator(false);

            // black lines and points
            set1.setColor(getResources().getColor(R.color.colorWhite));
            set1.setCircleColor(0xFF5DF2FF);

            // line thickness and point size
            set1.setLineWidth(1f);
            // draw points as solid circles
            set1.setDrawCircleHole(false);

            // customize legend entry
            set1.setFormLineWidth(1f);
            set1.setFormSize(15.f);
            set1.setDrawValues(false);
            // text size of values
            set1.setValueTextSize(9f);
            set1.setDrawCircles(false);

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
    }

    private void initializeForChart2() {
        {   // // Chart Style // //
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
        }

        XAxis xAxis;
        {   // // X-Axis Style // //
            xAxis = chart2.getXAxis();
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
        }

        YAxis yAxis;
        {   // // Y-Axis Style // //
            yAxis = chart2.getAxisLeft();
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
        }


        {
            // draw limit lines behind data instead of on top
            yAxis.setDrawLimitLinesBehindData(true);
            xAxis.setDrawLimitLinesBehindData(true);

        }

        setCharData2(7, 15);

        // get the legend (only possible after setting data)
        Legend l = chart2.getLegend();
        l.setEnabled(false);
    }

    private void setCharData2(int count, float range) {
        ArrayList<Entry> values = new ArrayList<>();

        values.add(new Entry(1, 6));
        values.add(new Entry(2, 8));

        if (chart2.getData() != null &&
                chart2.getData().getDataSetCount() > 0) {
            set2 = (LineDataSet) chart2.getData().getDataSetByIndex(0);
            set2.setValues(values);
            set2.notifyDataSetChanged();
            chart2.getData().notifyDataChanged();
            chart2.notifyDataSetChanged();
        } else {
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
                    return chart.getAxisLeft().getAxisMinimum();
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
            if (i == 0) {
                icon = R.mipmap.report_icon_score_1;
                drawable = R.drawable.score_1;
            } else if (i == 1) {
                icon = R.mipmap.report_icon_score_2;
                drawable = R.drawable.score_2;
            } else if (i == 2) {
                icon = R.mipmap.report_icon_score_3;
                drawable = R.drawable.score_3;
            } else if (i == 3) {
                icon = R.mipmap.report_icon_score_4;
                drawable = R.drawable.score_4;
            } else if (i == 4) {
                icon = R.mipmap.report_icon_score_5;
                drawable = R.drawable.score_5;
            }
            dynamicAddCircleDot(icon, (int)entry.getY(), drawable, left, top, i);

        }

    }
}
