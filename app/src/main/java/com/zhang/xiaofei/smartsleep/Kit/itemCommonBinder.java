package com.zhang.xiaofei.smartsleep.Kit;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deadline.statebutton.StateButton;
import com.makeramen.roundedimageview.RoundedImageView;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.zhang.xiaofei.smartsleep.Kit.Webview.WebActivity;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Login.LoginActivity;

/**
 * Created by hesk on 16/2/16.
 * this is the example holder for the simple adapter
 */
public class itemCommonBinder extends UltimateRecyclerviewViewHolder {
    public static final int layout = R.layout.layout_found_item_linear;
    public TextView textViewSample;
    public RoundedImageView imageViewSample;
    public TextView textViewFeature;
    public StateButton stateButtonLook;
    public RelativeLayout item_view;

    /**
     * give more control over NORMAL or HEADER view binding
     *
     * @param itemView view binding
     * @param isItem   bool
     */
    public itemCommonBinder(View itemView, boolean isItem) {
        super(itemView);
        if (isItem) {
            textViewSample = (TextView) itemView.findViewById(R.id.str_textview_holder);
            imageViewSample = (RoundedImageView) itemView.findViewById(R.id.str_image_holder);
            textViewFeature = (TextView) itemView.findViewById(R.id.str_textview_feature);
            item_view = (RelativeLayout) itemView.findViewById(R.id.str_item_view);
            stateButtonLook = (StateButton)itemView.findViewById(R.id.btn_look);
        }

    }

    @Override
    public void onItemSelected() {

    }

    @Override
    public void onItemClear() {

    }
}
