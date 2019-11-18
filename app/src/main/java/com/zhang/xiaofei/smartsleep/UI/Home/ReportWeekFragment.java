package com.zhang.xiaofei.smartsleep.UI.Home;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.shizhefei.fragment.LazyFragment;
import com.zhang.xiaofei.smartsleep.Kit.BigSmallFont.BigSmallFontManager;
import com.zhang.xiaofei.smartsleep.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ReportWeekFragment extends LazyFragment {

    private LineChart chart; // 睡眠质量分析
    private LineChart chart2; // 翻身
    private TextView tvSleepValue;
    private TextView tvSleepAverageTime;
    private TextView tvTime1; // 上床时间
    private TextView tvTime2; // 入睡时长
    private TextView tvTime3; // 清醒时间
    private TextView tvTime4; // 平均心率
    private TextView tvTime5; // 平均呼吸率
    private TextView tvTime6; // 清醒次数

    @Override
    protected View getPreviewLayout(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.layout_preview, container, false);
    }

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_tabmain_item2);
        initializeForChart(); // 睡眠质量初始化
        initializeForChart2();
        tvSleepValue = (TextView)findViewById(R.id.tv_sleep_value);
        String unit = getResources().getString(R.string.common_minute2);
        String[] array = {unit};
        String content = "100" + unit;
        tvSleepValue.setText(BigSmallFontManager.createTimeValue(content, getActivity(), 30, array));
        tvSleepAverageTime = (TextView)findViewById(R.id.tv_sleep_average_time);
        String unit01 = getResources().getString(R.string.common_hour);
        String unit02 = getResources().getString(R.string.common_minute);
        String[] array1 = {unit01, unit02};
        String content1 = "17" + unit01 + "30" + unit02;
        tvSleepAverageTime.setText(BigSmallFontManager.createTimeValue(content1, getActivity(), 13, array1));
        initialText();
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
        String content = "17" + unit01 + "30" + unit02;
        tvSleepAverageTime.setText(BigSmallFontManager.createTimeValue(content, getActivity(), 20, array));
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
        tvTime3.setText(BigSmallFontManager.createTimeValue(content2, getActivity(), 13, array2));
        tvTime4 = (TextView)findViewById(R.id.tv_time_4);
        String unit4 = getResources().getString(R.string.common_times_minute);
        String[] array4 = {unit4};
        String content4 = "16" + unit4;
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
            yAxis.setDrawLimitLinesBehindData(true);
            xAxis.setDrawLimitLinesBehindData(true);

        }

        setCharData(6, 80);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        l.setEnabled(false);
    }

    private void setCharData(int count, float range) {
        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < count; i++) {
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
            set1.setColor(getResources().getColor(R.color.colorWhite));
            set1.setCircleColor(Color.BLACK);

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
            set1.setValueTextSize(9f);
            set1.setDrawCircles(false);
            // draw selection line as dashed
            //set1.enableDashedHighlightLine(10f, 5f, 0f);

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
            chart2.setDragEnabled(true);
            chart2.setScaleEnabled(true);
            // chart.setScaleXEnabled(true);
            // chart.setScaleYEnabled(true);
            // force pinch zoom along both axis
            chart2.setPinchZoom(false);
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
        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range);
            values.add(new Entry(i, val));
        }

        LineDataSet set1;

        if (chart2.getData() != null &&
                chart2.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart2.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            chart2.getData().notifyDataChanged();
            chart2.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "");

            set1.setDrawIcons(false);
            set1.setDrawHorizontalHighlightIndicator(false);
            set1.setDrawVerticalHighlightIndicator(false);

            // black lines and points
            set1.setColor(getResources().getColor(R.color.colorWhite));
            set1.setCircleColor(Color.BLACK);

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
            set1.setValueTextSize(9f);
            set1.setDrawCircles(false);
            // draw selection line as dashed
            //set1.enableDashedHighlightLine(10f, 5f, 0f);

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
            chart2.setData(data);
        }
    }
}
