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
import com.zhang.xiaofei.smartsleep.Model.Record.RecordModel;
import com.zhang.xiaofei.smartsleep.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    private CalendarView calendarView;
    private ImageButton ibLeftPre;
    private ImageButton ibRightNex;
    private TextView tvSimulationData;
    private Realm mRealm;
    private int currentTime = 0;
    private int year = 0;
    private int month = 0;

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
        mRealm = Realm.getDefaultInstance();
        initialCalendarView();
        year = calendarView.getCurYear();
        month = calendarView.getCurMonth();
        ibLeftPre = (ImageButton)findViewById(R.id.ib_left_pre);
        ibLeftPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //currentTime -= 7 * 24 * 60 * 60;
                //tvSimulationData.setText(getTextValue());
                calendarView.scrollToPre();
                if (month > 1) {
                    month--;
                } else {
                    year--;
                    month = 12;
                }
                tvSimulationData.setText(year + "-" + (month > 9 ? ("" + month) : ("0" + month)));
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
        calendar.set(Integer.parseInt(array[0]), Integer.parseInt(array[1]) - 1, Integer.parseInt(array[2]),0,0,0);

    }

//    private void getDayData(int startTime) {
//        mMap.clear();
//        RealmResults<RecordModel> list = mRealm.where(RecordModel.class)
//                .greaterThan("time", startTime)
//                .lessThan("time", startTime + 24 * 60 * 60 * 7)
//                .findAll().sort("time", Sort.ASCENDING);
//        for (RecordModel model: list) {
//            if (mMap.containsKey((Integer) (model.getTime() / 60))) {
//                List<RecordModel> temlist = mMap.get((Integer) (model.getTime() / 60));
//                temlist.add(model);
//                mMap.put((Integer) (model.getTime() / 60), temlist);
//            } else {
//                List<RecordModel> temlist = new ArrayList<RecordModel>();
//                temlist.add(model);
//                mMap.put((Integer) (model.getTime() / 60), temlist);
//            }
//        }
//    }

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
        }

        YAxis yAxis;
        {   // // Y-Axis Style // //
            yAxis = chart.getAxisLeft();
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
        }


        {
            // draw limit lines behind data instead of on top
            yAxis.setDrawLimitLinesBehindData(true);
            xAxis.setDrawLimitLinesBehindData(true);

        }

        setCharData(7, 15);

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
            // draw selection line as dashed
            //set1.enableDashedHighlightLine(10f, 5f, 0f);

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

    }
}
