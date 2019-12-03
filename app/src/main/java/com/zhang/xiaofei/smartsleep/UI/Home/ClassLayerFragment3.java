package com.zhang.xiaofei.smartsleep.UI.Home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.shizhefei.fragment.LazyFragment;
import com.zhang.xiaofei.smartsleep.Kit.SampleDataboxset;
import com.zhang.xiaofei.smartsleep.Kit.sectionHomePageAdapter;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.FoundGoods.BasicFunctions;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class ClassLayerFragment3 extends BasicFunctions {
    public static final String INTENT_STRING_TABNAME = "intent_String_tabName";
    public static final String INTENT_INT_POSITION = "intent_int_position";
    private String tabName;
    private int position;
    private TextView textView;
    private ProgressBar progressBar;
    private sectionHomePageAdapter simpleRecyclerViewAdapter = null;
    private ImageView ivNull;
    private TextView tvNull;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_class_item3, null);

        init(layout);
        return layout;
    }

    private void init(View layout) {
        ultimateRecyclerView = (UltimateRecyclerView) layout.findViewById(R.id.ultimate_recycler_view);
        doURV(ultimateRecyclerView);
        ultimateRecyclerView.setVisibility(View.INVISIBLE);
        ivNull = (ImageView)layout.findViewById(R.id.iv_null);
        tvNull = (TextView)layout.findViewById(R.id.tv_null);
    }

    @Override
    protected void onLoadmore() {
        //SampleDataboxset.insertMoreWhole(simpleRecyclerViewAdapter, 2);
    }

    @Override
    protected void doURV(UltimateRecyclerView urv) {
        //  ultimateRecyclerView.setInflater(LayoutInflater.from(getApplicationContext()));
        ultimateRecyclerView.setHasFixedSize(true);
        ultimateRecyclerView.setClipToPadding(false);
        ArrayList<String> list = new ArrayList<>();
        list.add("o2fn31");
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

    @Override
    protected void onFireRefresh() {

    }

    @Override
    protected void addButtonTrigger() {

    }

    @Override
    protected void removeButtonTrigger() {

    }
}
