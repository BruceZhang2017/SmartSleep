package com.zhang.xiaofei.smartsleep.UI.Home;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ansen.http.net.HTTPCaller;
import com.ansen.http.net.RequestDataCallback;
import com.clj.blesample.FastBLEManager;
import com.clj.blesample.comm.Observer;
import com.clj.blesample.comm.ObserverManager;
import com.deadline.statebutton.StateButton;
import com.jpeng.jptabbar.JPTabBar;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.ximalaya.ting.android.opensdk.test.MainFragmentActivity;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import com.zhang.xiaofei.smartsleep.Kit.Application.ScreenInfoUtils;
import com.zhang.xiaofei.smartsleep.Kit.CardPager.CardItem;
import com.zhang.xiaofei.smartsleep.Kit.CardPager.CardPagerAdapter;
import com.zhang.xiaofei.smartsleep.Kit.CardPager.ShadowTransformer;
import com.zhang.xiaofei.smartsleep.Kit.DB.YMUserInfoManager;
import com.zhang.xiaofei.smartsleep.Kit.DisplayUtil;
import com.zhang.xiaofei.smartsleep.Kit.GlideImageLoader;
import com.zhang.xiaofei.smartsleep.Kit.SampleDataboxset;
import com.zhang.xiaofei.smartsleep.Kit.sectionHomePageAdapter;
import com.zhang.xiaofei.smartsleep.Model.Device.DeviceInfoManager;
import com.zhang.xiaofei.smartsleep.Model.Device.DeviceManager;
import com.zhang.xiaofei.smartsleep.Model.Device.DeviceModel;
import com.zhang.xiaofei.smartsleep.Model.Login.UserModel;
import com.zhang.xiaofei.smartsleep.Model.Packages.Goods;
import com.zhang.xiaofei.smartsleep.Model.Packages.GoodsItem;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.FoundGoods.BasicFunctions;
import com.zhang.xiaofei.smartsleep.UI.Me.DeviceManageActivity;
import com.zhang.xiaofei.smartsleep.UI.Me.FeedbackActivity;
import com.zhang.xiaofei.smartsleep.YMApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jpeng on 16-11-14.
 */
public class HomePageFragment extends BasicFunctions implements View.OnClickListener, TextWatcher, OnBannerListener, Observer {

    private Button btnBindDevice;
    private Button btnIgnore;
    private ImageButton ibHelpSleep;
    private ImageButton ibDevices;
    private ImageButton ibAlarm;
    private Banner banner;
    private StateButton sbCloseAdvertisement;
    private StateButton sbStartBuy;
    private TextView tvClassMore;
    @BindView(R.id.tv_title) TextView tvTitle;
    @BindView(R.id.im_r) ImageButton btnRight;
    private JPTabBar mTabBar;
    private sectionHomePageAdapter simpleRecyclerViewAdapter = null;
    private ViewPager mViewPager;
    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private TextView tvDeviceTitle;
    private TextView tvNoDeviceTip;
    private TextView tvTemprature;
    private TextView tvHumdity;
    private IndicatorViewPager indicatorViewPager;
    private LayoutInflater inflate;

    private ViewPager viewPager;
    private FixedIndicatorView indicator;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.tab1, null);
        ButterKnife.bind(this, layout);
        init(layout);
        showDeviceList();
        return layout;
    }

    private void init(View layout) {
        tvTitle.setText(R.string.common_app_name);

        btnRight.setImageResource(R.mipmap.home_icon_more);
        btnRight.setVisibility(View.VISIBLE);
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRightButton(v);
            }
        });
        mTabBar = ((HomeActivity)getActivity()).getTabbar();

        ultimateRecyclerView = (UltimateRecyclerView) layout.findViewById(R.id.ultimate_recycler_view);
        ultimateRecyclerView.setNormalHeader(setupHeaderView());
        doURV(ultimateRecyclerView);

        downloadFoundAdvertises();

        ObserverManager.getInstance().addObserver(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ObserverManager.getInstance().deleteObserver(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bind_device:
                HomeActivity activity = (HomeActivity)getActivity();
                activity.checkCameraPermissions();
                break;
            case R.id.ib_help_sleep:
                Toast.makeText(getActivity(), "正在更换喜马拉雅的SDK", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ib_devices:
                Intent intentC = new Intent(getActivity(), DeviceManageActivity.class);
                startActivity(intentC);
                break;
            case R.id.ib_alarm: // 闹钟 AlarmActivity
                Intent intentD = new Intent(getActivity(), AlarmActivity.class);
                YMUserInfoManager userManager = new YMUserInfoManager( getActivity());
                UserModel userModel = userManager.loadUserInfo();
                intentD.putExtra("userId", userModel.getUserInfo().getUserId());
                startActivity(intentD);
                break;
            case R.id.btn_close_advertisement:
                hideAdvertisement();
                break;
            case R.id.tv_sleep_class_more:
                Intent intentE = new Intent(getActivity(), SleepClassActivity.class);
                startActivity(intentE);
                break;
            case R.id.btn_ignore:
                hideNoDevice(true);
                mViewPager.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void hideNoDevice(Boolean value) {
        tvDeviceTitle.setVisibility(value ? View.INVISIBLE : View.VISIBLE);
        tvNoDeviceTip.setVisibility(value ? View.INVISIBLE : View.VISIBLE);
        btnBindDevice.setVisibility(value ? View.INVISIBLE : View.VISIBLE);
        btnIgnore.setVisibility(value ? View.INVISIBLE : View.VISIBLE);
    }

    private void hideAdvertisement() {
        banner.setVisibility(View.GONE);
        sbCloseAdvertisement.setVisibility(View.GONE);
        sbStartBuy.setVisibility(View.GONE);
    }

    private void showAdvertisement() {
        banner.setVisibility(View.VISIBLE);
        sbCloseAdvertisement.setVisibility(View.VISIBLE);
    }

    // 右上角按钮点击事件
    private void handleRightButton(View v) {
        Context context =  (Context) getActivity();
        new XPopup.Builder(context)
                .atView(v)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                .asAttachList(
                        new String[]{
                                getResources().getString(R.string.index_binding_device),
                                getResources().getString(R.string.index_search_device),
                                getResources().getString(R.string.mine_feedback)},
                        new int[]{
                                R.mipmap.home_icon_more_binding,
                                R.mipmap.home_icon_more_search,
                                R.mipmap.home_icon_more_suggest},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                if (position == 2) {
                                    Intent intent = new Intent(getActivity(), FeedbackActivity.class);
                                    startActivity(intent);
                                } else if (position == 1) {
                                    showDialog();
                                } else {
                                    HomeActivity activity = (HomeActivity)getActivity();
                                    activity.checkCameraPermissions();
                                }
                            }
                        })
                .show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s!=null&&s.toString().equals("0")){
            mTabBar.showBadge(0, ""+0,true);
            mTabBar.hideBadge(0);
            return;
        }
        if (s.toString().equals("")) {
            mTabBar.showBadge(0, ""+0,true);
            return;
        }
        int count = Integer.parseInt(s.toString());
        if(mTabBar!=null)
            mTabBar.showBadge(0, count+"",true);
    }

    @Override
    public void OnBannerClick(int position) {

    }


    //如果你需要考虑更好的体验，可以这么操作
    @Override
    public void onStart() {
        super.onStart();
        //开始轮播
        if (banner.getVisibility() == View.VISIBLE) {
            banner.startAutoPlay();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //结束轮播
        if (banner.getVisibility() == View.VISIBLE) {
            banner.stopAutoPlay();
        }
    }

    @Override
    protected void onLoadmore() {
        SampleDataboxset.insertMoreWhole(simpleRecyclerViewAdapter, 2);
    }

    @Override
    protected void onFireRefresh() {
        // simpleRecyclerViewAdapter.insertLast(moreNum++ + "  Refresh things");
        ultimateRecyclerView.setRefreshing(false);
        //   ultimateRecyclerView.scrollBy(0, -50);
        //  linearLayoutManager.scrollToPosition(0);
        ultimateRecyclerView.scrollVerticallyTo(0);
        //ultimateRecyclerView.setAdapter(simpleRecyclerViewAdapter);
        //simpleRecyclerViewAdapter.notifyDataSetChanged();
        simpleRecyclerViewAdapter.removeAll();
        ultimateRecyclerView.disableLoadmore();
        ultimateRecyclerView.showEmptyView();
    }

    @Override
    protected void addButtonTrigger() {

    }

    @Override
    protected void removeButtonTrigger() {

    }

    @Override
    protected void doURV(UltimateRecyclerView urv) {
        //  ultimateRecyclerView.setInflater(LayoutInflater.from(getApplicationContext()));
        ultimateRecyclerView.setHasFixedSize(true);
        ultimateRecyclerView.setClipToPadding(false);
        ArrayList<String> list = new ArrayList<>();
        list.add("o2fn31");
        list.add("of2n32");
        list.add("of3n36");
        simpleRecyclerViewAdapter = new sectionHomePageAdapter(list);
        configLinearLayoutManager(ultimateRecyclerView);
        //enableParallaxHeader();
        enableEmptyViewPolicy();
        enableLoadMore();
        ultimateRecyclerView.setRecylerViewBackgroundColor(getResources().getColor(R.color.tranparencyColor));
        enableRefresh();
        // enableScrollControl();
        // enableSwipe();
        // enableItemClick();
        ultimateRecyclerView.setItemViewCacheSize(simpleRecyclerViewAdapter.getAdditionalItems());


        ultimateRecyclerView.setAdapter(simpleRecyclerViewAdapter);
    }

    // 顶部视图
    private View setupHeaderView() {
        View custom_header = LayoutInflater.from(getActivity()).inflate(R.layout.layout_home_page_header, null, false);

        btnBindDevice = (Button)custom_header.findViewById(R.id.btn_bind_device);
        btnBindDevice.setOnClickListener(this);
        ibHelpSleep = (ImageButton)custom_header.findViewById(R.id.ib_help_sleep);
        ibHelpSleep.setOnClickListener(this);
        ibDevices = (ImageButton)custom_header.findViewById(R.id.ib_devices);
        ibDevices.setOnClickListener(this);
        ibAlarm = (ImageButton)custom_header.findViewById(R.id.ib_alarm);
        ibAlarm.setOnClickListener(this);
        btnIgnore = (Button)custom_header.findViewById(R.id.btn_ignore);
        btnIgnore.setOnClickListener(this);
        tvDeviceTitle = (TextView)custom_header.findViewById(R.id.tv_device_title);
        tvNoDeviceTip = (TextView)custom_header.findViewById(R.id.tv_no_device_tip);
        tvTemprature = (TextView)custom_header.findViewById(R.id.tv_temperature_value);
        tvHumdity = (TextView)custom_header.findViewById(R.id.tv_humidity_value);
        if (DeviceManager.getInstance().deviceList.size() > 0) {
            String mac = DeviceManager.getInstance().deviceList.get(DeviceManager.getInstance().currentDevice).getMac();
            String value = DeviceInfoManager.getInstance().hashMap.get(mac);
            if (value != null) {
                String[] array = value.split("-");
                if (array != null && array.length >= 2) {
                    tvTemprature.setText(array[0] + "℃");
                    tvHumdity.setText(array[1] + "%");
                }
            }
        }
        banner = (Banner)custom_header.findViewById(R.id.banner);

        sbCloseAdvertisement = (StateButton)custom_header.findViewById(R.id.btn_close_advertisement);
        sbCloseAdvertisement.setOnClickListener(this);
        sbStartBuy = (StateButton)custom_header.findViewById(R.id.btn_start_buy);
        tvClassMore = (TextView)custom_header.findViewById(R.id.tv_sleep_class_more);
        tvClassMore.setOnClickListener(this);
        initialHead(custom_header);
        initDevice(custom_header);
        hideAdvertisement();
        return custom_header;
    }

    private void initialHead(View view) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mViewPager.setVisibility(View.INVISIBLE);
        mCardAdapter = new CardPagerAdapter();
        mCardAdapter.addCardItem(new CardItem(R.string.common_my_device, R.string.common_my_device));
        mCardAdapter.addCardItem(new CardItem(R.string.common_my_device, R.string.common_my_device));
        mCardAdapter.addCardItem(new CardItem(R.string.common_my_device, R.string.common_my_device));
        mCardAdapter.addCardItem(new CardItem(R.string.common_my_device, R.string.common_my_device));

        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);

        //mCardShadowTransformer.enableScaling(true);
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(getActivity(), R.style.activity_translucent);
        dialog.setContentView(R.layout.layout_search_device);

        if(dialog!=null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    // 获取首页广告 Start
    private void downloadFoundAdvertises() {
        YMUserInfoManager userManager = new YMUserInfoManager( getActivity());
        UserModel userModel = userManager.loadUserInfo();
        com.ansen.http.net.Header header = new com.ansen.http.net.Header("Content-Type", "application/x-www-form-urlencoded");
        com.ansen.http.net.Header headerToken = new com.ansen.http.net.Header("token", userModel.getToken());
        HomeActivity activity = (HomeActivity)getActivity();
        HTTPCaller.getInstance().post(
                Goods.class,
                YMApplication.getInstance().domain() + "app/advert/homeAdvert",
                new com.ansen.http.net.Header[]{header, headerToken},
                null,
                requestDataCallback
        );
    }

    private RequestDataCallback requestDataCallback = new RequestDataCallback<Goods>() {
        @Override
        public void dataCallback(int status, Goods goods) {
            System.out.println("网络请求返回的Status:" + status);
            if(goods==null || goods.getCode() != 200){
                if (goods == null) {
                    Toast.makeText(getActivity(), "服务器异常", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(getActivity(), goods.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }else{
                System.out.println("获取到的数据count: " + goods.getData().length);
//                List list = new ArrayList<String>();
//                for (GoodsItem item: goods.getData()) {
//                    list.add(item.)
//                }
//                List<?> images = new ArrayList(list);
//                banner.setImages(images)
//                        .setImageLoader(new GlideImageLoader())
//                        .setOnBannerListener(HomePageFragment.this)
//                        .start();
            }

        }

        @Override
        public void dataCallback(Goods obj) {
            if (obj == null) {
                Toast.makeText(getActivity(), getResources().getText(R.string.common_check_network), Toast.LENGTH_SHORT).show();
            }
        }
    };
    //// 获取首页广告 Start -- end

    private IndicatorViewPager.IndicatorPagerAdapter adapter = new IndicatorViewPager.IndicatorViewPagerAdapter() {

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = inflate.inflate(R.layout.tab_device_guide, container, false);

            }
            return convertView;
        }

        @Override
        public View getViewForPage(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = LayoutInflater.from(container.getContext())
                        .inflate(R.layout.layout_homepage_device, container, false);
            }
            TextView tvDeviceName = (TextView)convertView.findViewById(R.id.tv_device_name);
            if (DeviceManager.getInstance().deviceList.size() > position) {
                tvDeviceName.setText(DeviceManager.getInstance().deviceList.get(position).getDeviceType() == 1 ? getResources().getString(R.string.report_yamy_sleep_belt) : getResources().getString(R.string.report_yamy_sleep_button));
            }
            String unit = getResources().getString(R.string.common_minute2);
            String[] array = {unit};
            String content = 83 + "" + unit;
            TextView tvGrade = (TextView)convertView.findViewById(R.id.tv_sleep_review_value);
            tvGrade.setText(content);
            String unit21 = getResources().getString(R.string.common_hour);
            String unit22 = getResources().getString(R.string.common_minute);
            String content2 = "23" + unit21 + "30" + unit22;
            TextView tvSleepTime = (TextView)convertView.findViewById(R.id.tv_sleep_time);
            tvSleepTime.setText(content2);
            TextView tvSleepDuration = (TextView)convertView.findViewById(R.id.tv_sleep_duration);
            tvSleepDuration.setText("08" + unit21 + "00" + unit22);
            TextView tvBodyMove = (TextView)convertView.findViewById(R.id.tv_body_value);
            tvBodyMove.setText(R.string.common_many);
            TextView tvDotValue = (TextView)convertView.findViewById(R.id.tv_dot_value);
            tvDotValue.setText(5 + "" + getResources().getString(R.string.common_times));
            ImageView ivBluetooth = (ImageView)convertView.findViewById(R.id.iv_bluetooth);
            System.out.println("刷新蓝牙图表");
            if (((DeviceManager.getInstance().connectedCurrentDevice >> position) & 0x01) > 0) {
                ivBluetooth.setImageResource(R.mipmap.bluetooth2);
            } else {
                ivBluetooth.setImageResource(R.mipmap.bluetooth1);
            }
            return convertView;
        }

        @Override
        public int getItemPosition(Object object) {
            //这是ViewPager适配器的特点,有两个值 POSITION_NONE，POSITION_UNCHANGED，默认就是POSITION_UNCHANGED,
            // 表示数据没变化不用更新.notifyDataChange的时候重新调用getViewForPage
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return DeviceManager.getInstance().deviceList.size();
        }
    };

    // 初始化顶部设备UI
    private void initDevice(View custom_header) {
        viewPager = (ViewPager) custom_header.findViewById(R.id.guide_viewPager);
        viewPager.setVisibility(View.INVISIBLE);
        indicator = (FixedIndicatorView) custom_header.findViewById(R.id.guide_indicator);
        indicator.setVisibility(View.INVISIBLE);
        indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
        inflate = LayoutInflater.from(getActivity());
        indicatorViewPager.setAdapter(adapter);
        indicatorViewPager.setOnIndicatorPageChangeListener(new IndicatorViewPager.OnIndicatorPageChangeListener() {
            @Override
            public void onIndicatorPageChange(int preItem, int currentItem) {
                DeviceManager.getInstance().currentDevice = currentItem;
                ((HomeActivity)getActivity()).exchangeDevice(DeviceManager.getInstance().currentDevice);
                refreshTempratureAndHumdity(0,0);
            }
        });
    }

    // 显示所有设备
    public void showDeviceList() {
        if (viewPager == null) {
            return;
        }
        if (DeviceManager.getInstance().deviceList.size() == 0) {
            ConstraintLayout.LayoutParams paramsB = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            paramsB.leftToLeft = R.id.cl_main;
            paramsB.topToBottom = R.id.btn_bind_device;
            paramsB.leftMargin = DisplayUtil.dip2px(40, getActivity());
            paramsB.topMargin = DisplayUtil.dip2px(40, getActivity());
            ibHelpSleep.setLayoutParams(paramsB);
            refreshDevice(false);
            return;
        }
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)viewPager.getLayoutParams();
        params.height = (ScreenInfoUtils.getScreenWidth(getActivity()) - DisplayUtil.dip2px(40, getActivity())) * 681 / 960;
        viewPager.setLayoutParams(params);
        ConstraintLayout.LayoutParams paramsB = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        paramsB.leftToLeft = R.id.cl_main;
        paramsB.topToBottom = R.id.guide_indicator;
        paramsB.leftMargin = DisplayUtil.dip2px(40, getActivity());
        paramsB.topMargin = DisplayUtil.dip2px(20, getActivity());
        ibHelpSleep.setLayoutParams(paramsB);
        refreshDevice(true);
    }

    private void refreshDevice(boolean value) {
        if (viewPager == null) {
            return;
        }
        viewPager.setVisibility(value ? View.VISIBLE : View.INVISIBLE);
        indicator.setVisibility(value ? View.VISIBLE : View.INVISIBLE);
        hideNoDevice(value);
        if (value) {
            adapter.notifyDataSetChanged();
        }
    }

    // 刷新温度和湿度
    public void refreshTempratureAndHumdity(float temprature, float humdity) {
        if (tvTemprature != null) {
            tvTemprature.setText(temprature + "℃");
        }
        if (tvHumdity != null) {
            tvHumdity.setText(humdity + "%");
        }
    }

    @Override
    public void connectedState(boolean connected, String mac) {
        System.out.println("收到蓝牙变化的通知：" + connected + " " + mac);
        for (int i=0;i < DeviceManager.getInstance().deviceList.size();i++) {
            if (DeviceManager.getInstance().deviceList.get(i).getMac().equals(mac)) {
                DeviceManager.getInstance().connectedCurrentDevice = (((DeviceManager.getInstance().connectedCurrentDevice >> (i + 1)) << 1) + (connected ? 1 : 0)) << i;
                System.out.println("connectedCurrentDevice：" + DeviceManager.getInstance().connectedCurrentDevice);
                adapter.notifyDataSetChanged();
                break;
            }
        }
    }
}
