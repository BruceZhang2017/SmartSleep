package com.zhang.xiaofei.smartsleep.Kit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;


import com.bumptech.glide.Glide;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.zhang.xiaofei.smartsleep.Kit.Webview.WebActivity;
import com.zhang.xiaofei.smartsleep.Kit.itemCommonBinder;
import com.zhang.xiaofei.smartsleep.Model.Packages.GoodsItem;
import com.zhang.xiaofei.smartsleep.UI.Login.LoginActivity;

import java.security.SecureRandom;
import java.util.List;


public class GoodsAdapter extends easyRegularAdapter<GoodsItem, itemCommonBinder> {

    public Context context;

    public GoodsAdapter(List<GoodsItem> stringList) {
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
        return itemCommonBinder.layout;
    }

    @Override
    protected itemCommonBinder newViewHolder(View view) {
        return new itemCommonBinder(view, true);
    }

    @Override
    public itemCommonBinder newFooterHolder(View view) {
        return new itemCommonBinder(view, false);
    }

    @Override
    public itemCommonBinder newHeaderHolder(View view) {
        return new itemCommonBinder(view, false);
    }


  /*  @Override
    public void onBindViewHolder(final SHol holder, int position) {
        if (position < getItemCount() && (customHeaderView != null ? position <= stringList.size() : position < stringList.size()) && (customHeaderView != null ? position > 0 : true)) {

            ((SHol) holder).textViewSample.setText(stringList.get(customHeaderView != null ? position - 1 : position));
            // ((ViewHolder) holder).itemView.setActivated(selectedItems.get(position, false));
            if (mDragStartListener != null) {
//                ((ViewHolder) holder).imageViewSample.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
//                            mDragStartListener.onStartDrag(holder);
//                        }
//                        return false;
//                    }
//                });

                ((SHol) holder).item_view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
            }
        }

    }
*/


    public final void insertOne(GoodsItem e) {
        insertLast(e);
    }

    public final void removeLastOne() {
        removeLast();
    }

/*
    @Override
    public long generateHeaderId(int position) {
        // URLogs.d("position--" + position + "   " + getItem(position));
        if (getItem(position).length() > 0)
            return getItem(position).charAt(0);
        else return -1;
    }*/

    @Override
    protected void withBindHolder(itemCommonBinder holder, GoodsItem data, int position) {
        holder.stateButtonLook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WebActivity.class);
                intent.putExtra("url", data.getLink());
                context.startActivity(intent);
            }
        });
        holder.textViewSample.setText(data.getProductName());
        holder.textViewFeature.setText(data.getTitle());
        Glide.with(context).load(data.getImgPath()).into(holder.imageViewSample);
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

    public void setOnDragStartListener(OnStartDragListener dragStartListener) {
        mDragStartListener = dragStartListener;

    }


}