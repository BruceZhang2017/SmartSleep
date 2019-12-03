package com.zhang.xiaofei.smartsleep.Kit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;


import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.sunofbeaches.himalaya.SearchActivity;
import com.zhang.xiaofei.smartsleep.Kit.itemCommonBinder;
import com.zhang.xiaofei.smartsleep.R;

import java.security.SecureRandom;
import java.util.List;


public class sectionHomePageAdapter extends easyRegularAdapter<String, itemHomePageBinder> {
    // private List<String> stringList;

    public Context context;

    public sectionHomePageAdapter(List<String> stringList) {
        super(stringList);
        //  this.stringList = stringList;
    }

    /**
     * the layout id for the normal data
     *
     * @return the ID
     */
    @Override
    protected int getNormalLayoutResId() {
        return itemHomePageBinder.layout;
    }

    @Override
    protected itemHomePageBinder newViewHolder(View view) {
        return new itemHomePageBinder(view, true);
    }



    @Override
    public itemHomePageBinder newFooterHolder(View view) {
        return new itemHomePageBinder(view, false);
    }

    @Override
    public itemHomePageBinder newHeaderHolder(View view) {
        return new itemHomePageBinder(view, false);
    }

    public final void insertOne(String e) {
        insertLast(e);
    }

    public final void removeLastOne() {
        removeLast();
    }

    @Override
    protected void withBindHolder(itemHomePageBinder holder, String data, int position) {
        if (position % 7 == 0){
            holder.imageViewSample.setImageResource(R.mipmap.pic_1);
            holder.textViewFeature.setText(R.string.homepage_series_title_one);
            holder.textViewSample.setText(R.string.homepage_series_content_one);
        } else if (position % 7 == 1){
            holder.imageViewSample.setImageResource(R.mipmap.pic_2);
            holder.textViewFeature.setText(R.string.homepage_series_title_two);
            holder.textViewSample.setText(R.string.homepage_series_content_two);
        } else if (position % 7 == 2){
            holder.imageViewSample.setImageResource(R.mipmap.pic_3);
            holder.textViewFeature.setText(R.string.homepage_series_title_three);
            holder.textViewSample.setText(R.string.homepage_series_content_three);
        } else if (position % 7 == 3){
            holder.imageViewSample.setImageResource(R.mipmap.pic_4);
            holder.textViewFeature.setText(R.string.homepage_series_title_four);
            holder.textViewSample.setText(R.string.homepage_series_content_four);
        } else if (position % 7 == 4){
            holder.imageViewSample.setImageResource(R.mipmap.pic_5);
            holder.textViewFeature.setText(R.string.homepage_series_title_five);
            holder.textViewSample.setText(R.string.homepage_series_content_five);
        } else if (position % 7 == 5){
            holder.imageViewSample.setImageResource(R.mipmap.pic_6);
            holder.textViewFeature.setText(R.string.homepage_series_title_six);
            holder.textViewSample.setText(R.string.homepage_series_content_six);
        } else if (position % 7 == 6){
            holder.imageViewSample.setImageResource(R.mipmap.pic_7);
            holder.textViewFeature.setText(R.string.homepage_series_title_seven);
            holder.textViewSample.setText(R.string.homepage_series_content_seven);
        }
        holder.item_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("在这里被点击了");
                String strSearch = "";
                if (position % 7 == 0){
                    strSearch = "深度睡眠";
                } else if (position % 7 == 1) {
                    strSearch = "助眠疗法";
                } else if (position % 7 == 2) {
                    strSearch = "瑜伽21天";
                } else if (position % 7 == 3) {
                    strSearch = "失眠专用";
                } else if (position % 7 == 4) {
                    strSearch = "经典古诗文";
                } else if (position % 7 == 5) {
                    strSearch = "冥想训练";
                } else if (position % 7 == 6) {
                    strSearch = "瑜伽入门";
                }
                Intent intent = new Intent(context, SearchActivity.class);
                intent.putExtra("key", strSearch);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        swapPositions(fromPosition, toPosition);
//        notifyItemMoved(fromPosition, toPosition);
        super.onItemMove(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        if (position > 0)
            removeAt(position);
        // notifyItemRemoved(position);
        //        notifyDataSetChanged();
        super.onItemDismiss(position);
    }

   /* public String getItem(int position) {
        if (customHeaderView != null)
            position--;
        if (position < stringList.size())
            return stringList.get(position);
        else
            return "";
    }*/
//
//    private int getRandomColor() {
//        SecureRandom rgen = new SecureRandom();
//        return Color.HSVToColor(150, new float[]{
//                rgen.nextInt(359), 1, 1
//        });
//    }

    public void setOnDragStartListener(OnStartDragListener dragStartListener) {
        mDragStartListener = dragStartListener;

    }


}