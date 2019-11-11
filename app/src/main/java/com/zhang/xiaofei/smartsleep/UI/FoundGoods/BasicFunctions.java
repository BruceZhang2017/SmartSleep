package com.zhang.xiaofei.smartsleep.UI.FoundGoods;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.view.ActionMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.ansen.http.net.HTTPCaller;
import com.ansen.http.net.RequestDataCallback;
import com.marshalchen.ultimaterecyclerview.DragDropTouchListener;
import com.marshalchen.ultimaterecyclerview.ItemTouchListenerAdapter;
import com.marshalchen.ultimaterecyclerview.ObservableScrollState;
import com.marshalchen.ultimaterecyclerview.ObservableScrollViewCallbacks;
import com.marshalchen.ultimaterecyclerview.URLogs;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import com.marshalchen.ultimaterecyclerview.layoutmanagers.ClassicSpanGridLayoutManager;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.marshalchen.ultimaterecyclerview.ui.AnimationType;
import com.marshalchen.ultimaterecyclerview.layoutmanagers.ScrollSmoothLineaerLayoutManager;
import com.zhang.xiaofei.smartsleep.Kit.DB.YMUserInfoManager;
import com.zhang.xiaofei.smartsleep.Model.Login.BaseProtocol;
import com.zhang.xiaofei.smartsleep.Model.Login.UserModel;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Home.HomeActivity;
import com.zhang.xiaofei.smartsleep.YMApplication;

import okhttp3.Route;

import static com.mob.tools.utils.DeviceHelper.getApplication;

/**
 * Created by hesk on 19/2/16.
 */
public abstract class BasicFunctions extends Fragment {

    protected UltimateRecyclerView ultimateRecyclerView;

    protected void enableParallaxHeader() {
        ultimateRecyclerView.setParallaxHeader(getLayoutInflater().inflate(R.layout.parallax_recyclerview_header, ultimateRecyclerView.mRecyclerView, false));
        ultimateRecyclerView.setOnParallaxScroll(new UltimateRecyclerView.OnParallaxScroll() {
            @Override
            public void onParallaxScroll(float percentage, float offset, View parallax) {

            }
        });
    }

    protected void enableLoadMore() {
        // StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(simpleRecyclerViewAdapter);
        // ultimateRecyclerView.addItemDecoration(headersDecor);
        ultimateRecyclerView.setLoadMoreView(R.layout.custom_bottom_progressbar);

        ultimateRecyclerView.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore(int itemsCount, final int maxLastVisiblePosition) {
                status_progress = true;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        onLoadmore();
                        status_progress = false;
                    }
                }, 500);
            }
        });

    }

    protected abstract void onLoadmore();

    protected abstract void onFireRefresh();

    protected void enableRefresh() {
//        ultimateRecyclerView.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        onFireRefresh();
//                    }
//                }, 1000);
//            }
//        });
        //        ultimateRecyclerView.setDefaultSwipeToRefreshColorScheme(getResources().getColor(android.R.color.holo_blue_bright),
//                getResources().getColor(android.R.color.holo_green_light),
//                getResources().getColor(android.R.color.holo_orange_light),
//                getResources().getColor(android.R.color.holo_red_light));

    }

    protected final void configStaggerLayoutManager(UltimateRecyclerView rv, easyRegularAdapter ad) {
        StaggeredGridLayoutManager gaggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rv.setLayoutManager(gaggeredGridLayoutManager);
    }

    protected final void configLinearLayoutManager(UltimateRecyclerView rv) {
        final ScrollSmoothLineaerLayoutManager mgm = new ScrollSmoothLineaerLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false, 300);
        rv.setLayoutManager(mgm);
    }


    protected final void enableScrollControl() {
        ultimateRecyclerView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
            @Override
            public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
                URLogs.d("onScrollChanged: " + dragging);
            }

            @Override
            public void onDownMotionEvent() {

            }

            @Override
            public void onUpOrCancelMotionEvent(ObservableScrollState observableScrollState) {
                URLogs.d("onUpOrCancelMotionEvent");

            }
        });

        ultimateRecyclerView.showFloatingButtonView();
    }

    protected void enableEmptyViewPolicy() {
        //  ultimateRecyclerView.setEmptyView(R.layout.empty_view, UltimateRecyclerView.EMPTY_KEEP_HEADER_AND_LOARMORE);
        //    ultimateRecyclerView.setEmptyView(R.layout.empty_view, UltimateRecyclerView.EMPTY_KEEP_HEADER);
        //  ultimateRecyclerView.setEmptyView(R.layout.empty_view, UltimateRecyclerView.EMPTY_SHOW_LOADMORE_ONLY);
        //ultimateRecyclerView.setEmptyView(R.layout.empty_view, UltimateRecyclerView.EMPTY_CLEAR_ALL);
    }

    protected void enableSwipe() {

    }

    protected void enableItemClick() {
        ItemTouchListenerAdapter itemTouchListenerAdapter = new ItemTouchListenerAdapter(ultimateRecyclerView.mRecyclerView,
                new ItemTouchListenerAdapter.RecyclerViewOnItemClickListener() {
                    @Override
                    public void onItemClick(RecyclerView parent, View clickedView, int position) {
                    }

                    @Override
                    public void onItemLongClick(RecyclerView parent, View clickedView, int position) {
                        URLogs.d("onItemLongClick()" + isDrag);
                        if (isDrag) {
                            URLogs.d("onItemLongClick()" + isDrag);
                            //   dragDropTouchListener.startDrag();
                            //   ultimateRecyclerView.enableDefaultSwipeRefresh(false);
                        }

                    }
                });
        ultimateRecyclerView.mRecyclerView.addOnItemTouchListener(itemTouchListenerAdapter);
    }

    protected abstract void addButtonTrigger();

    protected abstract void removeButtonTrigger();

    protected void setupSpinnerAnimationSelection(Spinner spinner, ArrayAdapter<String> adapter) {
        adapter.add("- animator -");
        for (AnimationType type : AnimationType.values()) {
            adapter.add(type.name());
        }
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    ultimateRecyclerView.setItemAnimator(AnimationType.values()[position - 1].getAnimator());
                    ultimateRecyclerView.getItemAnimator().setAddDuration(300);
                    ultimateRecyclerView.getItemAnimator().setRemoveDuration(300);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void bButtons() {

    }

    protected void toggleButtonTrigger() {
        if (!status_progress) {
            isEnableAutoLoadMore = !isEnableAutoLoadMore;
            if (isEnableAutoLoadMore) {
                ultimateRecyclerView.reenableLoadmore();
            }
        }
    }

    protected ActionMode actionMode;
    protected LinearLayoutManager linearLayoutManager;
    private int moreNum = 2;
    protected boolean isDrag = true, isEnableAutoLoadMore = true, status_progress = false;
    private DragDropTouchListener dragDropTouchListener;


    protected abstract void doURV(UltimateRecyclerView urv);

}
