package com.zhang.xiaofei.smartsleep.UI.Home;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.enums.PopupAnimation;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorFragmentPagerAdapter;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;
import com.zhang.xiaofei.smartsleep.R;

public class ReportFragment extends Fragment {
	private IndicatorViewPager indicatorViewPager;
	private LayoutInflater inflate;
	private TextView tvTitle;
	private ImageButton btnRight;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_tabmain, null);
		initUI(layout);
		return layout;
	}

	private void initUI(View view) {
		Resources res = getResources();

		NoScrollViewPager viewPager = (NoScrollViewPager) view.findViewById(R.id.fragment_tabmain_viewPager);
		Indicator indicator = (Indicator) view.findViewById(R.id.fragment_tabmain_indicator);
		indicator.setScrollBar(new ColorBar(view.getContext(), Color.rgb(255,255,255), 5));
		View vHeader = view.findViewById(R.id.include_title);
		tvTitle = (TextView)view.findViewById(R.id.tv_title);
		tvTitle.setText(R.string.report_phone_data);
		ImageButton ibArrow = (ImageButton)view.findViewById(R.id.ib_arrow);
		ibArrow.setVisibility(View.VISIBLE);
		ibArrow.setImageResource(R.mipmap.report_icon_open);
		ibArrow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Context context =  (Context) getActivity();
				new XPopup.Builder(context)
						.atView(vHeader)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
						.popupAnimation(PopupAnimation.TranslateAlphaFromTop)
						.maxWidth(100)
						.asAttachList(
								new String[]{
										getResources().getString(R.string.report_sleep_button),
										getResources().getString(R.string.report_sleep_belt),
										getResources().getString(R.string.common_smart_phone)},
								null,
								new OnSelectListener() {
									@Override
									public void onSelect(int position, String text) {
										if (position == 2) {
											tvTitle.setText(R.string.report_phone_data);
										} else if (position == 1) {
											tvTitle.setText(R.string.report_sleep_belt);
										} else {
											tvTitle.setText(R.string.report_sleep_button);
										}
									}
								})
						.show();
			}
		});

		btnRight = (ImageButton)view.findViewById(R.id.im_r);
		btnRight.setImageResource(R.mipmap.report_icon_share);
		btnRight.setVisibility(View.VISIBLE);

		float unSelectSize = 14;
		float selectSize = unSelectSize * 1.2f;

		int selectColor = res.getColor(R.color.reportTopSelectedColor);
		int unSelectColor = res.getColor(R.color.reportTopNormalColor);
		indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(selectColor, unSelectColor).setSize(selectSize, unSelectSize));

		viewPager.setOffscreenPageLimit(3);

		indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
		inflate = LayoutInflater.from(view.getContext());

		// 注意这里 的FragmentManager 是 getChildFragmentManager(); 因为是在Fragment里面
		// 而在activity里面用FragmentManager 是 getSupportFragmentManager()
		indicatorViewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("cccc", "Fragment 所在的Activity onDestroy " + this);
	}

	private class MyAdapter extends IndicatorFragmentPagerAdapter {

		public MyAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public View getViewForTab(int position, View convertView, ViewGroup container) {
			if (convertView == null) {
				convertView = inflate.inflate(R.layout.tab_top, container, false);
			}
			TextView textView = (TextView) convertView;
			if (position == 0) {
				textView.setText(R.string.report_daily_report);
			} else if (position == 1) {
				textView.setText(R.string.report_weekly_report);
			} else {
				textView.setText(R.string.report_monthly_report);
			}
			return convertView;
		}

		@Override
		public Fragment getFragmentForPage(int position) {
			if (position == 0) {
				ReportDayFragment mainFragment = new ReportDayFragment();
				return mainFragment;
			} else if (position == 1) {
				ReportWeekFragment mainFragment = new ReportWeekFragment();
				return mainFragment;
			} else {
				ReportMonthFragment mainFragment = new ReportMonthFragment();
				return mainFragment;
			}
		}

	}

}
