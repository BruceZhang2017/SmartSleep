package com.zhang.xiaofei.smartsleep.Kit;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deadline.statebutton.StateButton;
import com.makeramen.roundedimageview.RoundedImageView;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.zhang.xiaofei.smartsleep.R;

/**
 * Created by hesk on 16/2/16.
 * this is the example holder for the simple adapter
 */
public class itemHomePageBinder extends UltimateRecyclerviewViewHolder {
    public static final int layout = R.layout.home_page_item_linear;
    public TextView textViewSample;
    public RoundedImageView imageViewSample;
    public TextView textViewFeature;
    public RelativeLayout item_view;

    /**
     * give more control over NORMAL or HEADER view binding
     *
     * @param itemView view binding
     * @param isItem   bool
     */
    public itemHomePageBinder(View itemView, boolean isItem) {
        super(itemView);
//            itemView.setOnTouchListener(new SwipeDismissTouchListener(itemView, null, new SwipeDismissTouchListener.DismissCallbacks() {
//                @Override
//                public boolean canDismiss(Object token) {
//                    Logs.d("can dismiss");
//                    return true;
//                }
//
//                @Override
//                public void onDismiss(View view, Object token) {
//                   // Logs.d("dismiss");
//                    remove(getPosition());
//
//                }
//            }));
        if (isItem) {
            textViewSample = (TextView) itemView.findViewById(R.id.str_textview_holder);
            imageViewSample = (RoundedImageView) itemView.findViewById(R.id.str_image_holder);
            textViewFeature = (TextView) itemView.findViewById(R.id.str_textview_feature);
            item_view = (RelativeLayout) itemView.findViewById(R.id.str_item_view);
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
