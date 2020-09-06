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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

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
import com.mylhyl.circledialog.view.listener.OnAdPageChangeListener;
import com.shizhefei.fragment.LazyFragment;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
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

public class ReportDayFragment extends LazyFragment implements ViewPager.OnPageChangeListener { // 日报告
    private ImageButton ibLeftPre;
    private ImageButton ibRightNex;
    private TextView tvSimulationData;
    ViewPager viewPager;
    IndicatorViewPager indicatorViewPager;
    private LayoutInflater inflate;
    int total = 0;
    int current = 0; // 当前的位置
    MyAdapter adapter;

    @Override
    protected View getPreviewLayout(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.layout_preview, container, false);
    }

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_tabmain_item);
        calSleepAndGetup();
        viewPager = (ViewPager) findViewById(R.id.fragment_tabmain_viewPager);
        FixedIndicatorView indicator = (FixedIndicatorView) findViewById(R.id.guide_indicator);
        indicator.setVisibility(View.INVISIBLE);
        indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
        inflate = LayoutInflater.from(getApplicationContext());
        adapter = new MyAdapter(getChildFragmentManager());
        indicatorViewPager.setAdapter(adapter);
        current = total > 0 ? total - 1 : 0;
        if (current > 0) {
            viewPager.setCurrentItem(current, true); // 滑动到指定位置
        }
        viewPager.addOnPageChangeListener(this);

        ibLeftPre = (ImageButton)findViewById(R.id.ib_left_pre);
        ibLeftPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preSleepAndGetupData();
            }
        });
        ibRightNex = (ImageButton)findViewById(R.id.ib_right_next);
        ibRightNex.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                nextSleepAndGetupData();
            }
        });

        tvSimulationData = (TextView)findViewById(R.id.tv_simulation_data);
        tvSimulationData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        readSleepAndGetupData(); // 读取睡觉和起床信息
        if (total == 0) {
            ibLeftPre.setVisibility(View.INVISIBLE);
            ibRightNex.setVisibility(View.INVISIBLE);
            tvSimulationData.setText(currentDate(System.currentTimeMillis()));
        } else {
            ibRightNex.setVisibility(View.INVISIBLE);
        }
    }

    private class MyAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {

        public MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return Math.max(1, total);
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = inflate.inflate(R.layout.tab_guide, container, false);
            }
            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            String sleepTime = "";
            String getupTime = "";
            int count = 0;
            String[] days = SleepAndGetupTimeManager.times.keySet().toArray(new String[0]);
            Arrays.sort(days);
            for (String day: days) {
                List<String> items = SleepAndGetupTimeManager.times.get(day);
                for (String item: items) {
                    if (position == count) {
                        sleepTime = item.split("&")[0];
                        getupTime = item.split("&")[1];
                        return new ReportDayItemFragment(sleepTime, getupTime);
                    }
                    count += 1;
                }
            }
            return new ReportDayItemFragment(sleepTime, getupTime);
        }

    }

    private String[] getSleepAndGetupTime(int position) {
        String sleepTime = "";
        String getupTime = "";
        int count = 0;
        String[] days = SleepAndGetupTimeManager.times.keySet().toArray(new String[0]);
        Arrays.sort(days);
        for (String day: days) {
            List<String> items = SleepAndGetupTimeManager.times.get(day);
            for (String item: items) {
                if (position == count) {
                    sleepTime = item.split("&")[0];
                    getupTime = item.split("&")[1];
                    return new String[]{sleepTime, getupTime};
                }
                count += 1;
            }
        }
        return new String[0];
    }

    private void refreshDateTimeData(String sleepTime, String getupTime) {
        if (getupTime.length() > 11 && sleepTime.length() > 11) {
            String day = sleepTime.substring(0,11);
            String sleep = sleepTime.substring(11);
            String getup = getupTime.substring(11);
            tvSimulationData.setText(day + "\n" + sleep + " - " + getup);
            return;
        }
        tvSimulationData.setText(currentDate(System.currentTimeMillis()));
    }

    private String currentDate(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(time);
        String str = simpleDateFormat.format(date);
        return str;
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

    private void calSleepAndGetup() {
        total = 0;
        if (SleepAndGetupTimeManager.times.size() == 0) {
            return;
        }
        String[] days = SleepAndGetupTimeManager.times.keySet().toArray(new String[0]);
        Arrays.sort(days);
        for (String day: days) {
            List<String> items = SleepAndGetupTimeManager.times.get(day);
            total += items.size();
        }
    }


    private void readSleepAndGetupData() {
        if (SleepAndGetupTimeManager.times.size() == 0) {
            return;
        }
        if (total > 0) {
            String[] array = getSleepAndGetupTime(total - 1);
            if (array != null && array.length > 0) {
                refreshDateTimeData(array[0], array[1]);
            }
        }
    }

    private void preSleepAndGetupData() {
        if (SleepAndGetupTimeManager.times.size() == 0) {
            return;
        }
        current -= 1;
        viewPager.setCurrentItem(current, true);
        if (current == 0) {
            ibLeftPre.setVisibility(View.INVISIBLE);
            ibRightNex.setVisibility(View.VISIBLE);
        } else {
            ibLeftPre.setVisibility(View.VISIBLE);
        }
    }

    private void nextSleepAndGetupData() {
        if (SleepAndGetupTimeManager.times.size() == 0) {
            return;
        }
        current += 1;
        viewPager.setCurrentItem(current, true);
        if (current + 1 == total) {
            ibRightNex.setVisibility(View.INVISIBLE);
            ibLeftPre.setVisibility(View.VISIBLE);
        } else {
            ibRightNex.setVisibility(View.VISIBLE);
        }
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
//        String sleepTime = duration.split("&")[0];
//        String getupTime = duration.split("&")[1];
//        refreshDateTimeData(sleepTime, getupTime);
//    }

    @Override
    public void onPageSelected(int position) {
        System.out.println("onPageSelected " + position);
        current = position;
        String[] array = getSleepAndGetupTime(position);
        if (array != null && array.length > 0) {
            refreshDateTimeData(array[0], array[1]);
        }
        if (total > 0) {
            if (current == 0) {
                ibLeftPre.setVisibility(View.INVISIBLE);
                ibRightNex.setVisibility(View.VISIBLE);
            } else if (current + 1 == total) {
                ibLeftPre.setVisibility(View.VISIBLE);
                ibRightNex.setVisibility(View.INVISIBLE);
            } else {
                ibLeftPre.setVisibility(View.VISIBLE);
                ibRightNex.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        System.out.println("onPageSelected " + position + " positionOffset " + positionOffset);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        System.out.println("onPageScrollStateChanged " + state);
    }

    public void refreshDayReport() {
        if (adapter == null) {
            return;
        }
        calSleepAndGetup();
        adapter.notifyDataSetChanged();
        current = total > 0 ? total - 1 : 0;
        if (viewPager == null) {
            return;
        }
        if (total > 0) {
            viewPager.setCurrentItem(current, true); // 滑动到指定位置
            ibRightNex.setVisibility(View.INVISIBLE);
        }
    }
}
