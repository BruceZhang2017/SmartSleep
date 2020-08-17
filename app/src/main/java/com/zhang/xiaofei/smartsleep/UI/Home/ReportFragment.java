package com.zhang.xiaofei.smartsleep.UI.Home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.ansen.http.net.HTTPCaller;
import com.ansen.http.net.NameValuePair;
import com.ansen.http.net.RequestDataCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import com.loonggg.lib.alarmmanager.clock.SimpleDialog;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.enums.PopupAnimation;
import com.lxj.xpopup.enums.PopupPosition;
import com.lxj.xpopup.impl.AttachListPopupView;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorFragmentPagerAdapter;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;
//import com.umeng.socialize.ShareAction;
//import com.umeng.socialize.UMShareListener;
//import com.umeng.socialize.bean.SHARE_MEDIA;
//import com.umeng.socialize.media.UMImage;
//import com.umeng.socialize.media.UMWeb;
import com.zhang.xiaofei.smartsleep.Kit.DB.CacheUtil;
import com.zhang.xiaofei.smartsleep.Kit.DB.YMUserInfoManager;
import com.zhang.xiaofei.smartsleep.Model.Login.BaseProtocol;
import com.zhang.xiaofei.smartsleep.Model.Login.UserModel;
import com.zhang.xiaofei.smartsleep.Model.Record.BreathRecordManger;
import com.zhang.xiaofei.smartsleep.Model.Record.BreathRecordModel;
import com.zhang.xiaofei.smartsleep.Model.Record.HistoryBreath;
import com.zhang.xiaofei.smartsleep.Model.Record.HistoryBreathData;
import com.zhang.xiaofei.smartsleep.Model.Record.HistoryBreathList;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Login.LoginActivity;
import com.zhang.xiaofei.smartsleep.UI.Login.LoginMoreActivity;
import com.zhang.xiaofei.smartsleep.UI.Me.FeedbackActivity;
import com.zhang.xiaofei.smartsleep.YMApplication;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import me.curzbin.library.BottomDialog;
import me.curzbin.library.Item;
import me.curzbin.library.OnItemClickListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReportFragment extends Fragment implements CalendarView.OnCalendarRangeSelectListener,
        CalendarView.OnMonthChangeListener {
	private IndicatorViewPager indicatorViewPager;
	private LayoutInflater inflate;
	private TextView tvTitle;
	private CalendarView calendarView;
	private ImageButton btnRight;
	ReportDayFragment dayFragment;
	ReportWeekFragment weekFragment;
	ReportMonthFragment monthFragment;
	private TextView tvMonth;
	private ImageButton ibLeftPre;
	private ImageButton ibRightNex;
	private ScrollView scrollView;
	NoScrollViewPager viewPager;
	FixedIndicatorView indicator;
	private TextView tvBreath1;
	private TextView tvBreath2;
	private TextView tvBreath3;
	private TextView tvBreath4;
	private TextView tvBreath5;
	private TextView tvBreath6;
	private TextView tvBreath7;
	private TextView tvBreath8;
	private TextView tvBreath9;
	private TextView tvBreath10;
	private TextView tvBreath11;
	private TextView tvBreath12;
	private TextView tvBreath13;
	private TextView tvBreath14;
	private TextView tvBreath15;
	private TextView tvBreath16;
	private TextView tvBreath17;
	private TextView tvBreath18;
	private TextView tvBreath19;
	private TextView tvBreath20;
	private String startDate = "";
	private String endDate = "";
	public String serialId = "";
	private boolean bDevicePopShow = false; // 设备选择弹框是否可见
	private BottomDialog bottomDialog;
	private ImageView ivnull;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_tabmain, null);
		initUI(layout);
		return layout;
	}

	private void initUI(View view) {
		Resources res = getResources();

		viewPager = (NoScrollViewPager) view.findViewById(R.id.fragment_tabmain_viewPager);
		indicator = (FixedIndicatorView) view.findViewById(R.id.fragment_tabmain_indicator);
		ColorBar colorBar = new ColorBar(view.getContext(), Color.rgb(255,255,255), 10);
		colorBar.setWidth(60);
		colorBar.getSlideView().setBackground(getResources().getDrawable(R.drawable.block_half_corner_white));
		indicator.setScrollBar(colorBar);
		View vHeader = view.findViewById(R.id.include_title);
		ivnull = view.findViewById(R.id.iv_null);
		tvTitle = (TextView)view.findViewById(R.id.tv_title);
		tvTitle.setText(R.string.report_sleep_belt);
		tvTitle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (bDevicePopShow) {
					return;
				}
				bDevicePopShow = true;
				selectDevice(ivnull);
			}
		});
		ImageButton ibArrow = (ImageButton)view.findViewById(R.id.ib_arrow);
		ibArrow.setVisibility(View.VISIBLE);
		ibArrow.setImageResource(R.mipmap.report_icon_open);
		ibArrow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { // 切换不同的设备
				if (bDevicePopShow) {
					return;
				}
				bDevicePopShow = true;
				selectDevice(ivnull);
			}
		});

		btnRight = (ImageButton)view.findViewById(R.id.im_r);
		btnRight.setImageResource(R.mipmap.report_icon_share);
		btnRight.setVisibility(View.INVISIBLE);
		btnRight.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				bottomDialog = new BottomDialog(getActivity())
//						.orientation(BottomDialog.HORIZONTAL)
//						.inflateMenu(R.menu.menu_main, new OnItemClickListener() {
//							@Override
//							public void click(Item item) {
//								if (item.getId() == R.id.action_weibo_share) {
//									weiboShare(); // 微博分享
//								} else if (item.getId() == R.id.action_weixin_share) {
//									weixinShare(); // 微信分享
//								} else if (item.getId() == R.id.action_pengyouquan_share) {
//
//								}else if (item.getId() == R.id.ic_github) {
//									Intent intent = new Intent(getActivity(), ShareSleepQualityActivity.class);
//									getActivity().startActivity(intent);
//								}
//								bottomDialog.dismiss();
//							}
//						});
//				bottomDialog.show();
			}
		});

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
		MyAdapter myAdapter = new MyAdapter(getChildFragmentManager());
		indicatorViewPager.setAdapter(myAdapter);
		indicatorViewPager.setPageMargin(10);
		//indicatorViewPager
		indicatorViewPager.setOnIndicatorItemClickListener(new Indicator.OnIndicatorItemClickListener() {
			@Override
			public boolean onItemClick(View clickItemView, int position) {
				TextView textView = (TextView)clickItemView;
				textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
				return false;
			}
		});

        calendarView = (CalendarView)view.findViewById(R.id.calendarView);
		tvMonth = (TextView)view.findViewById(R.id.tv_simulation_data);
		tvMonth.setText(currentDate(System.currentTimeMillis()));
		ibLeftPre = (ImageButton)view.findViewById(R.id.ib_left_pre);
		ibLeftPre.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				calendarView.scrollToPre(true);
			}
		});
		ibRightNex = (ImageButton)view.findViewById(R.id.ib_right_next);
		ibRightNex.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				calendarView.scrollToNext(true);
			}
		});

		scrollView = (ScrollView)view.findViewById(R.id.sv_main);
		tvBreath1 = (TextView)view.findViewById(R.id.tv_time_1);
		tvBreath2 = (TextView)view.findViewById(R.id.tv_time_2);
		tvBreath3 = (TextView)view.findViewById(R.id.tv_time_3);
		tvBreath4 = (TextView)view.findViewById(R.id.tv_time_4);
		tvBreath5 = (TextView)view.findViewById(R.id.tv_time_5);
		tvBreath6 = (TextView)view.findViewById(R.id.tv_time_6);
		tvBreath7 = (TextView)view.findViewById(R.id.tv_time_7);
		tvBreath8 = (TextView)view.findViewById(R.id.tv_time_8);
		tvBreath9 = (TextView)view.findViewById(R.id.tv_time_9);
		tvBreath10 = (TextView)view.findViewById(R.id.tv_time_10);
		tvBreath11 = (TextView)view.findViewById(R.id.tv_time_11);
		tvBreath12 = (TextView)view.findViewById(R.id.tv_time_12);
		tvBreath13 = (TextView)view.findViewById(R.id.tv_time_13);
		tvBreath14 = (TextView)view.findViewById(R.id.tv_time_14);
		tvBreath15 = (TextView)view.findViewById(R.id.tv_time_15);
		tvBreath16 = (TextView)view.findViewById(R.id.tv_time_16);
		tvBreath17 = (TextView)view.findViewById(R.id.tv_time_17);
		tvBreath18 = (TextView)view.findViewById(R.id.tv_time_18);
		tvBreath19 = (TextView)view.findViewById(R.id.tv_time_19);
		tvBreath20 = (TextView)view.findViewById(R.id.tv_time_20);

		BreathRecordModel model = BreathRecordManger.getInstance().getCache();
		if (model != null) {
			startDate = model.getStartDate();
			endDate = model.getEndDate();
			if (startDate != null && startDate.length() == 8 && endDate != null && endDate.length() == 8) {
				int y1 = Integer.parseInt(startDate.substring(0, 4));
				int m1 = Integer.parseInt(startDate.substring(4, 6));
				int d1 = Integer.parseInt(startDate.substring(6, 8));
				int y2 = Integer.parseInt(endDate.substring(0, 4));
				int m2 = Integer.parseInt(endDate.substring(4, 6));
				int d2 = Integer.parseInt(endDate.substring(6, 8));
				System.out.println("选择的时间" + y1 + " " + m1 + " " + d1 + " " + y2 + " " + m2 + " " + d2);
				calendarView.setSelectRangeMode();
				calendarView.setSelectCalendarRange(y1, m1, d1, y2, m2, d2);
				calendarView.setSelectStartCalendar(y1, m1, d1);
				calendarView.setSelectEndCalendar(y2, m2, d2);
			}
			refreshText(model);
		}
		calendarView.setOnCalendarRangeSelectListener(this);
		calendarView.setOnMonthChangeListener(this);
	}

	private void dismissDialog() {

	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		System.out.println("Report Fragment OnResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		System.out.println("Report Fragment onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		System.out.println("Report Fragment onStop");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("cccc", "Fragment 所在的Activity onDestroy " + this);
	}

	/// 切换设备
	private void selectDevice(View view) {
		Context context =  (Context) getActivity();
		new XPopup.Builder(context)
				.atView(view)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
				.offsetY(150)
				.popupAnimation(PopupAnimation.TranslateAlphaFromBottom)
				.asCustom(new CustomAttachPopup(getActivity())
						.bindItemLayout(R.layout.xpopup_adapger_m_text)
						.setStringData(new String[]{
										getResources().getString(R.string.report_sleep_belt),
										getResources().getString(R.string.report_sleep_button),
										getResources().getString(R.string.homepage_breathing)},
								null).setOnSelectListener(new OnSelectListener() {
							@Override
							public void onSelect(int position, String text) {
								if (position == 0) {
									tvTitle.setText(R.string.report_sleep_belt);
									dayFragment.refreshData(true);
									weekFragment.refreshSleepStatistic(true);
									monthFragment.refreshSleepStatistic(true);
									scrollView.setVisibility(View.INVISIBLE);
									viewPager.setVisibility(View.VISIBLE);
									indicator.setVisibility(View.VISIBLE);
								} else if (position == 1) {
									tvTitle.setText(R.string.report_sleep_button);
									dayFragment.refreshData(false);
									weekFragment.refreshSleepStatistic(false);
									monthFragment.refreshSleepStatistic(false);
									scrollView.setVisibility(View.INVISIBLE);
									viewPager.setVisibility(View.VISIBLE);
									indicator.setVisibility(View.VISIBLE);
								} else {
									tvTitle.setText(R.string.homepage_breathing);
									scrollView.setVisibility(View.VISIBLE);
									viewPager.setVisibility(View.INVISIBLE);
									indicator.setVisibility(View.INVISIBLE);
								}
							}
						}))
				.show();
	}

	private String currentDate(long time) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
		Date date = new Date(time);
		String str=simpleDateFormat.format(date);
		return str;
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
				dayFragment = new ReportDayFragment();
				return dayFragment;
			} else if (position == 1) {
				weekFragment = new ReportWeekFragment();
				return weekFragment;
			} else {
				monthFragment = new ReportMonthFragment();
				return monthFragment;
			}
		}

	}

	public class CustomAttachPopup extends AttachListPopupView {
		public CustomAttachPopup(@NonNull Context context) {
			super(context);
		}
		@Override
		protected int getImplLayoutId() {
			return R.layout.xpopup_attatch_impl_m_list;
		}

		@Override
		protected int getPopupLayoutId() {
			return R.layout.xpopup_attch_popup_m_view;
		}

		@Override
		protected void onDismiss() {
			super.onDismiss();
			System.out.println("自定义弹框消失");
			bDevicePopShow = false;  // 避免多次弹框，使用标示符。当弹框消失时，标志符重置。
		}
	}

	// 网络请求
	private void requestBreathData(String startDate, String endDate) {
		if (serialId == null || serialId.length() == 0) {
			return;
		}
		if (startDate == null || startDate.length() == 0) {
			return;
		}
		if (endDate == null || endDate.length() == 0) {
			return;
		}
		HomeActivity activity = (HomeActivity)getActivity();
        activity.showHUD();
		YMUserInfoManager userManager = new YMUserInfoManager(getActivity());
		UserModel userModel = userManager.loadUserInfo();
		com.ansen.http.net.Header header = new com.ansen.http.net.Header("Content-Type", "multipart/form-data");
		com.ansen.http.net.Header headerToken = new com.ansen.http.net.Header("token", userModel.getToken());
		List<NameValuePair> postParam = new ArrayList<>();
		postParam.add(new NameValuePair("startDate",startDate));
		postParam.add(new NameValuePair("endDate",endDate));
		postParam.add(new NameValuePair("serialId",serialId));
		HTTPCaller.getInstance().post(
				BreathRecordModel.class,
				YMApplication.getInstance().domain() + "app/sleep/getBreathingData",
				new com.ansen.http.net.Header[]{header, headerToken},
				postParam,
				requestDataCallback
		);
	}

	private RequestDataCallback requestDataCallback = new RequestDataCallback<BreathRecordModel>() {
		@Override
		public void dataCallback(int status, BreathRecordModel user) {
			System.out.println("网络请求返回的Status:" + status);
			HomeActivity activity = (HomeActivity)getActivity();
			activity.hideHUD();
			if(user==null || user.getCode() != 0){
				if (user.getCode() == 500 ) {
					Toast.makeText(getActivity(), user.getMsg(), Toast.LENGTH_SHORT).show();
				}
			}else{
				if (user.getCode() == 0) {
					refreshText(user);
					user.setStartDate(startDate);
					user.setEndDate(endDate);
					BreathRecordManger.getInstance().addCache(user);
					activity.refreshTab1();
				}
			}

		}

		@Override
		public void dataCallback(BreathRecordModel obj) {
			HomeActivity activity = (HomeActivity)getActivity();
			activity.hideHUD();
			if (obj == null) {
				Toast.makeText(getActivity(), getActivity().getResources().getText(R.string.common_check_network), Toast.LENGTH_SHORT).show();
			}
		}
	};

    @Override
    public void onMonthChange(int year, int month) {
        Log.e("onMonthChange", "  -- " + year + "  --  " + month);
		tvMonth.setText(year + "-" + month);
    }

    @Override
    public void onCalendarSelectOutOfRange(Calendar calendar) {
        // TODO: 2018/9/13 超出范围提示
    }

    @Override
    public void onSelectOutOfRange(Calendar calendar, boolean isOutOfMinRange) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCalendarRangeSelect(Calendar calendar, boolean isEnd) {
        if (!isEnd) {
			refreshText(null);
			BreathRecordManger.getInstance().deleteRecordModel();
			HomeActivity activity = (HomeActivity)getActivity();
			activity.refreshTab1();
			startDate = calendar.getYear() + "-" + (calendar.getMonth() > 9 ? ("" + calendar.getMonth()) : ("0" + calendar.getMonth())) + "-" + (calendar.getDay() > 9 ? ("" + calendar.getDay()) : ("0" + calendar.getDay()));
        } else {
			endDate = calendar.getYear() + "-" + (calendar.getMonth() > 9 ? ("" + calendar.getMonth()) : ("0" + calendar.getMonth())) + "-" + (calendar.getDay() > 9 ? ("" + calendar.getDay()) : ("0" + calendar.getDay()));
			requestBreathData(startDate, endDate);

        }
    }

    public void refreshText(BreathRecordModel model) {
		if (model == null) {
			tvBreath1.setText("0");
			tvBreath2.setText("0");
			tvBreath3.setText("0");
			tvBreath4.setText("0");
			tvBreath5.setText("0");
			tvBreath6.setText("0");
			tvBreath7.setText("0");
			tvBreath8.setText("0");
			tvBreath9.setText("0");
			tvBreath10.setText("0");
			tvBreath11.setText("0");
			tvBreath12.setText("0");
			tvBreath13.setText("0");
			tvBreath14.setText("0");
			tvBreath15.setText("0");
			tvBreath16.setText("0");
			tvBreath17.setText("0");
			tvBreath18.setText("0");
			tvBreath19.setText("0");
			tvBreath20.setText("0");
		} else {
			tvBreath1.setText(model.getUseDay() + "");
			tvBreath2.setText(model.getUseDay() + "");
			tvBreath3.setText(model.getUseTime());
			tvBreath4.setText(model.getAvgUseTime());
			tvBreath5.setText(model.getMaxAvg().getXqylpjz() + "");
			tvBreath6.setText(model.getInhaleStressNice() + "");
			tvBreath7.setText(model.getMaxAvg().getHqylpjz() + "");
			tvBreath8.setText(model.getExhaleStressNice() + "");
			tvBreath9.setText(model.getAvgAi() + "");
			tvBreath10.setText(model.getAvgHi() + "");
			tvBreath11.setText(model.getAvgAHI() + "");
			tvBreath12.setText(model.getMaxAvg().getCqlpjz() + "");
			tvBreath13.setText(model.getTidalVolume() + "");
			tvBreath14.setText(model.getTidalVolumeNice() + "");
			tvBreath15.setText(model.getMaxAvg().getFztqlpjz() + "");
			tvBreath16.setText(model.getMinuThroughput() + "");
			tvBreath17.setText(model.getMinuThroughputNice() + "");
			tvBreath18.setText(model.getMaxAvg().getHxplpjz() + "");
			tvBreath19.setText(model.getRespiratoryRate() + "");
			tvBreath20.setText(model.getRespiratoryRateNice() + "");
		}
	}

	public void refreshData(String serialId) {
    	this.serialId = serialId;
		requestBreathData(startDate, endDate);
		//getHuxijiData(1);
		requestData();
	}

	// 微博分享
//	UMShareListener umShareListener = new UMShareListener() {
//		@Override
//		public void onStart(SHARE_MEDIA platform) {
//			// 分享开始的回调
//		}
//
//		@Override
//		public void onResult(SHARE_MEDIA platform) {
//			Toast.makeText(ReportFragment.this.getActivity(), platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
//		}
//
//		@Override
//		public void onError(SHARE_MEDIA platform, Throwable t) {
//			Toast.makeText(ReportFragment.this.getActivity(),platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
//		}
//
//		@Override
//		public void onCancel(SHARE_MEDIA platform) {
//			Toast.makeText(ReportFragment.this.getActivity(),platform + " 分享取消了", Toast.LENGTH_SHORT).show();
//		}
//	};

    // 重新刷新日报
	public void refreshDayReport() {
		if (dayFragment == null) {
			return;
		}
		dayFragment.refreshDayReport();
		if (weekFragment == null) {
			return;
		}
		weekFragment.refreshAllUI();
		if (monthFragment == null) {
			return;
		}
		monthFragment.refreshAllUI();
	}

	// 微博分享事件
//	private void weiboShare() {
//		UMImage image = new UMImage(ReportFragment.this.getActivity(), R.mipmap.ic_launcher);//分享图标
//		final UMWeb web = new UMWeb("http://www.yamind.cn"); //切记切记 这里分享的链接必须是http开头
//		web.setTitle("睡眠带");//标题
//		web.setThumb(image);  //缩略图
//		web.setDescription("日报告");//描述
//		new ShareAction(ReportFragment.this.getActivity()).setPlatform(SHARE_MEDIA.SINA)
//				.withMedia(web)
//				.setCallback(umShareListener)
//				.share();
//	}

//	private void weixinShare() {
//		UMImage image = new UMImage(ReportFragment.this.getActivity(), R.mipmap.ic_launcher);//分享图标
//		final UMWeb web = new UMWeb("http://www.yamind.cn"); //切记切记 这里分享的链接必须是http开头
//		web.setTitle("睡眠带");//标题
//		web.setThumb(image);  //缩略图
//		web.setDescription("日报告");//描述
//		new ShareAction(ReportFragment.this.getActivity()).setPlatform(SHARE_MEDIA.WEIXIN)
//				.withMedia(web)
//				.setCallback(umShareListener)
//				.share();
//	}

//	private void pengyouquanShare() {
//		UMImage image = new UMImage(ReportFragment.this.getActivity(), R.mipmap.ic_launcher);//分享图标
//		final UMWeb web = new UMWeb("http://www.yamind.cn"); //切记切记 这里分享的链接必须是http开头
//		web.setTitle("睡眠带");//标题
//		web.setThumb(image);  //缩略图
//		web.setDescription("日报告");//描述
//		new ShareAction(ReportFragment.this.getActivity()).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
//				.withMedia(web)
//				.setCallback(umShareListener)
//				.share();
//	}

	// 获取呼吸机那些日期有数据
	private void getHuxijiData(int page) {
		System.out.println("获取数据库数据：" + page);
		if (serialId == null || serialId.length() == 0) {
			return;
		}
		YMUserInfoManager userManager = new YMUserInfoManager(getActivity());
		UserModel userModel = userManager.loadUserInfo();
		com.ansen.http.net.Header header = new com.ansen.http.net.Header("Content-Type", "application/json; charset=utf-8");
		com.ansen.http.net.Header headerToken = new com.ansen.http.net.Header("token", userModel.getToken());
		List<NameValuePair> postParam = new ArrayList<>();
		postParam.add(new NameValuePair("sortOrder","asc"));
		postParam.add(new NameValuePair("pageSize",1000 + ""));
		postParam.add(new NameValuePair("pageNumber",page + ""));
		postParam.add(new NameValuePair("serialId",serialId));
		HTTPCaller.getInstance().post(
				HistoryBreathList.class,
				YMApplication.getInstance().domain() + "app/deviceManage/getHistorySetData",
				new com.ansen.http.net.Header[]{header, headerToken},
				postParam,
				requestHistoryDataCallback
		);
	}

	private class History {
		String sortOrder;
		int pageSize;
		int pageNumber;
		String serialId;

		public String getSortOrder() {
			return sortOrder;
		}

		public void setSortOrder(String sortOrder) {
			this.sortOrder = sortOrder;
		}

		public int getPageSize() {
			return pageSize;
		}

		public void setPageSize(int pageSize) {
			this.pageSize = pageSize;
		}

		public int getPageNumber() {
			return pageNumber;
		}

		public void setPageNumber(int pageNumber) {
			this.pageNumber = pageNumber;
		}

		public String getSerialId() {
			return serialId;
		}

		public void setSerialId(String serialId) {
			this.serialId = serialId;
		}
	}

	private RequestDataCallback requestHistoryDataCallback = new RequestDataCallback<HistoryBreathList>() {
		@Override
		public void dataCallback(int status, HistoryBreathList list) {
			System.out.println("网络请求返回的Status:" + status);
			if(list==null || list.getCode() != 200){
				if (list.getCode() == 500 ) {
					Toast.makeText(getActivity(), list.getMsg(), Toast.LENGTH_SHORT).show();
				}
			}else{
				if (list.getCode() == 200) {
					refreshCalendarViewWithHasData(list);
				}
			}

		}

		@Override
		public void dataCallback(HistoryBreathList obj) {
			HomeActivity activity = (HomeActivity)getActivity();
			if (obj == null) {
				Toast.makeText(getActivity(), getActivity().getResources().getText(R.string.common_check_network), Toast.LENGTH_SHORT).show();
			}
		}
	};

	private void refreshCalendarViewWithHasData(HistoryBreathList list) {
		if (list.getData() == null) {
			return;
		}
		HistoryBreathData data = list.getData();
		if (data.getRows() == null) {
			return;
		}
		if (data.getRows().length <= 0) {
			return;
		}
		Map<String, Calendar> map = new HashMap<>();
		for (HistoryBreath breath : data.getRows()) {
			String date = breath.getUseDate();
			if (date.length() == 10) {
				String[] array = date.split("-");
				if (array.length == 3) {
					int year = Integer.parseInt(array[0]);
					int month = Integer.parseInt(array[1]);
					int day = Integer.parseInt(array[2]);
					map.put(getSchemeCalendar(year, month, day, 0xFF40db25).toString(),
							getSchemeCalendar(year, month, day, 0xFF40db25));
				}
			}
		}
		if (map.size() > 0) {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					calendarView.setSchemeDate(map);
				}
			});
		}
	}

	private Calendar getSchemeCalendar(int year, int month, int day, int color) {
		Calendar calendar = new Calendar();
		calendar.setYear(year);
		calendar.setMonth(month);
		calendar.setDay(day);
		calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
		return calendar;
	}

	private void requestData() {
		if (serialId == null || serialId.length() == 0) {
			return;
		}
		OkHttpClient okHttpClient  = new OkHttpClient.Builder()
				.connectTimeout(10, TimeUnit.SECONDS)
				.writeTimeout(10,TimeUnit.SECONDS)
				.readTimeout(20, TimeUnit.SECONDS)
				.build();

		History history = new History();
		history.setSortOrder("asc");
		history.setPageSize(1000);
		history.setPageNumber(1);
		history.setSerialId(serialId);
		//使用Gson 添加 依赖 compile 'com.google.code.gson:gson:2.8.1'
		Gson gson = new Gson();
		//使用Gson将对象转换为json字符串
		String json = gson.toJson(history);

		//MediaType  设置Content-Type 标头中包含的媒体类型值
		RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
				, json);
		YMUserInfoManager userManager = new YMUserInfoManager(getActivity());
		UserModel userModel = userManager.loadUserInfo();
		Request request = new Request.Builder()
				.url(YMApplication.getInstance().domain() + "app/deviceManage/getHistorySetData")//请求的url
				.addHeader("token", userModel.getToken())
				.post(requestBody)
				.build();

		//创建/Call
		Call call = okHttpClient.newCall(request);
		//加入队列 异步操作
		call.enqueue(new Callback() {
			//请求错误回调方法
			@Override
			public void onFailure(Call call, IOException e) {
				System.out.println("连接失败");
			}
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				Type type = new TypeToken<HistoryBreathList>() {}.getType();
				HistoryBreathList list = gson.fromJson(response.body().string(), type);
				refreshCalendarViewWithHasData(list);
			}
		});
	}

	public void showSyncDataDialog() {
		// 这里的属性可以一直设置，因为每次设置后返回的是一个builder对象
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

		// 设置提示框的标题
		builder.setTitle(R.string.sync_reports).
				// 设置确定按钮
						setPositiveButton(R.string.middle_confirm, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//do something
						HomeActivity activity = (HomeActivity)getActivity();
						activity.showProgressBarHUDAndHide();
						CacheUtil.getInstance(getContext()).putBool("SyncData", true);
						YMApplication.getInstance().downloadSleepAndGetupTime();
						SleepDataUploadManager uploadManager = new SleepDataUploadManager();
						uploadManager.uploadSleepData();
					}
				}).
				// 设置取消按钮,null是什么都不做
						setNegativeButton(R.string.middle_quit, null);
		// 生产对话框
		AlertDialog alertDialog = builder.create();
		// 显示对话框
		alertDialog.show();

	}


}
