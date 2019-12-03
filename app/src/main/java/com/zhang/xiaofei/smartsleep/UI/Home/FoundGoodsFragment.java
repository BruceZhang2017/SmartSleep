package com.zhang.xiaofei.smartsleep.UI.Home;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.ansen.http.net.HTTPCaller;
import com.ansen.http.net.RequestDataCallback;
import com.jpeng.jptabbar.JPTabBar;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.zhang.xiaofei.smartsleep.Kit.DB.YMUserInfoManager;
import com.zhang.xiaofei.smartsleep.Kit.GoodsAdapter;
import com.zhang.xiaofei.smartsleep.Model.Login.UserModel;
import com.zhang.xiaofei.smartsleep.Model.Packages.Goods;
import com.zhang.xiaofei.smartsleep.Model.Packages.GoodsItem;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.FoundGoods.BasicFunctions;
import com.zhang.xiaofei.smartsleep.YMApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jpeng on 16-11-14.
 */
public class FoundGoodsFragment extends BasicFunctions {

    JPTabBar mTabBar;
    private GoodsAdapter simpleRecyclerViewAdapter = null;
    private TextView tvLeft;
    private List<GoodsItem> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.tab3,null);
        init(layout);
        return layout;
    }

    /**
     * 初始化
     */
    private void init(View layout) {
        mTabBar = ((HomeActivity)getActivity()).getTabbar();
        ultimateRecyclerView = (UltimateRecyclerView) layout.findViewById(R.id.ultimate_recycler_view);
        doURV(ultimateRecyclerView);
        tvLeft = (TextView)layout.findViewById(R.id.tv_left_title);
        tvLeft.setVisibility(View.VISIBLE);
        tvLeft.setText(R.string.tab_finding);

    }

    @Override
    protected void onLoadmore() {

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
    public void onResume() {
        super.onResume();
        if (list.size() <= 0) {
            downloadFoundGoods();
        }
    }

    @Override
    protected void doURV(UltimateRecyclerView urv) {
        ultimateRecyclerView.setHasFixedSize(false);
        simpleRecyclerViewAdapter = new GoodsAdapter(list);
        simpleRecyclerViewAdapter.context = getActivity();
        configLinearLayoutManager(ultimateRecyclerView);
        //enableParallaxHeader();
        enableEmptyViewPolicy();
        ultimateRecyclerView.setRecylerViewBackgroundColor(getResources().getColor(R.color.tranparencyColor));
        enableRefresh();
        ultimateRecyclerView.setItemViewCacheSize(simpleRecyclerViewAdapter.getAdditionalItems());


        ultimateRecyclerView.setAdapter(simpleRecyclerViewAdapter);
    }

    // 获取商品
    private void downloadFoundGoods() {
        YMUserInfoManager userManager = new YMUserInfoManager( getActivity());
        UserModel userModel = userManager.loadUserInfo();
        com.ansen.http.net.Header header = new com.ansen.http.net.Header("Content-Type", "application/x-www-form-urlencoded");
        com.ansen.http.net.Header headerToken = new com.ansen.http.net.Header("token", userModel.getToken());
//        HomeActivity activity = (HomeActivity)getActivity();
//        activity.showHUD();
        HTTPCaller.getInstance().post(
                Goods.class,
                YMApplication.getInstance().domain() + "app/store/list?userId=" + userModel.getUserInfo().getUserId(),
                new com.ansen.http.net.Header[]{header, headerToken},
                null,
                requestDataCallback
        );
    }

    private RequestDataCallback requestDataCallback = new RequestDataCallback<Goods>() {
        @Override
        public void dataCallback(int status, Goods goods) {
            //HomeActivity activity = (HomeActivity)getActivity();
            //activity.hideHUD();
            System.out.println("网络请求返回的Status:" + status);
            if(goods==null || goods.getCode() != 200){
                if (goods == null) {
                    Toast.makeText(getActivity(), "服务器异常", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), goods.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }else{
                System.out.println("获取到的数据count: " + goods.getData().length);
                list.clear();
                for (GoodsItem item:
                     goods.getData()) {
                    list.add(item);
                }
                simpleRecyclerViewAdapter.notifyDataSetChanged();
            }

        }

        @Override
        public void dataCallback(Goods obj) {
            //HomeActivity activity = (HomeActivity)getActivity();
            //activity.hideHUD();
            if (obj == null) {
                Toast.makeText(getActivity(), getResources().getText(R.string.common_check_network), Toast.LENGTH_SHORT).show();
            }
        }
    };
}
