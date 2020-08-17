package com.zhang.xiaofei.smartsleep.Kit;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.music.MainMusicActivity;

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
        if (position == 1){
            holder.imageViewSample.setImageResource(R.mipmap.pic_1);
            holder.textViewFeature.setText(R.string.homepage_series_content_one);
            holder.textViewSample.setText(R.string.homepage_series_title_one);
        } else if (position == 2){
            holder.imageViewSample.setImageResource(R.mipmap.pic_2);
            holder.textViewFeature.setText(R.string.homepage_series_content_two);
            holder.textViewSample.setText(R.string.homepage_series_title_two);
        } else if (position == 3){
            holder.imageViewSample.setImageResource(R.mipmap.pic_3);
            holder.textViewFeature.setText(R.string.homepage_series_content_three);
            holder.textViewSample.setText(R.string.homepage_series_title_three);
        }
        holder.item_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int title = 0;
                int message = 0;
                if (position == 1) {
                    title = R.string.homepage_series_title_one;
                    message = R.string.homepage_series_content_one;
                } else if (position == 2) {
                    title = R.string.homepage_series_title_two;
                    message = R.string.homepage_series_content_two;
                } else {
                    title = R.string.homepage_series_title_three;
                    message = R.string.homepage_series_content_three;
                }
                Intent intentB = new Intent(context, MainMusicActivity.class);
                intentB.putExtra("title", title);
                intentB.putExtra("message", message);
                intentB.putExtra("position", position);
                context.startActivity(intentB);
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