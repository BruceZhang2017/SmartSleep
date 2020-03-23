package com.zhang.xiaofei.smartsleep.UI.Me;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.zhang.xiaofei.smartsleep.R;

/**
 * Created by hesk on 3/2/16.
 */
public class ItemGridCellBinder extends UltimateRecyclerviewViewHolder {
    public static final int layout = R.layout.grid_item;
    public TextView textViewSample;
    public ImageView imageViewSample;
    public View item_view;
    public TextView textViewVersion;
    public ImageView ibDelete;

    public ItemGridCellBinder(View itemView, boolean isItem) {
        super(itemView);
        if (isItem) {
            textViewSample = (TextView) itemView.findViewById(R.id.example_row_tv_title);
            textViewVersion = (TextView) itemView.findViewById(R.id.example_row_tv_version);
            imageViewSample = (ImageView) itemView.findViewById(R.id.example_row_iv_image);
            item_view = itemView.findViewById(R.id.planview);
            ibDelete = (ImageView)itemView.findViewById(R.id.iv_delete);
            imageViewSample.post(new Runnable() {
                @Override
                public void run() {
                    int width = imageViewSample.getWidth();
                    int height = width * 230 / 320;
                    imageViewSample.setLayoutParams(new RelativeLayout.LayoutParams(width , height));
                }
            });
        }
    }

    @Override
    public void onItemSelected() {
        itemView.setBackgroundColor(Color.LTGRAY);
    }

    @Override
    public void onItemClear() {
        itemView.setBackgroundColor(0);
    }
}