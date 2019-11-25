package com.zhang.xiaofei.smartsleep.UI.Home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhang.xiaofei.smartsleep.R;

public class MarkView extends RelativeLayout {

    public TextView textView;

    public MarkView(Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_custom_mark_view, this);
        initView(view);
    }

    public void initView(View view) {
        textView = (TextView)view.findViewById(R.id.tvContent);

    }
}
