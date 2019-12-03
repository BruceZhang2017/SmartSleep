package com.zhang.xiaofei.smartsleep.UI.Home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Login.BaseAppActivity;

import static com.mob.MobSDK.getContext;

public class SleepClassActivity extends BaseAppActivity {

    private IndicatorViewPager indicatorViewPager;
    private LayoutInflater inflate;
    private ImageButton ibLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_class);
        ViewPager viewPager = (ViewPager) findViewById(R.id.fragment_tabmain_viewPager);
        Indicator indicator = (Indicator) findViewById(R.id.fragment_tabmain_indicator);
        ColorBar colorBar = new ColorBar(getContext(), Color.rgb(255,255,255), 10);
        colorBar.setWidth(60);
        colorBar.getSlideView().setBackground(getResources().getDrawable(R.drawable.block_half_corner_white));
        indicator.setScrollBar(colorBar);

        float unSelectSize = 14;
        float selectSize = unSelectSize * 1.2f;

        int selectColor = getResources().getColor(R.color.reportTopSelectedColor);
        int unSelectColor = getResources().getColor(R.color.reportTopNormalColor);
        indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(selectColor, unSelectColor).setSize(selectSize, unSelectSize));

        viewPager.setOffscreenPageLimit(3);

        indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
        inflate = LayoutInflater.from(getContext());

        // 注意这里 的FragmentManager 是 getChildFragmentManager(); 因为是在Fragment里面
        // 而在activity里面用FragmentManager 是 getSupportFragmentManager()
        indicatorViewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));

        ibLeft = (ImageButton)findViewById(R.id.ib_back);
        ibLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class MyAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {

        public MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = inflate.inflate(R.layout.tab_top, container, false);
            }
            TextView textView = (TextView) convertView;
            if (position == 0) {
                textView.setText(getResources().getString(R.string.middle_wellness));
            } else {
                textView.setText(getResources().getString(R.string.common_how_to_use));
            }
            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            if (position == 0) {
                ClassLayerFragment1 mainFragment = new ClassLayerFragment1();
                return mainFragment;
            } else {
                ClassLayerFragment3 mainFragment = new ClassLayerFragment3();
                return mainFragment;
            }
        }
    }
}


