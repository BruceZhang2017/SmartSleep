package com.zhang.xiaofei.smartsleep.UI.Me;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.github.barteksc.pdfviewer.PDFView;
import com.zhang.xiaofei.smartsleep.Kit.Language.SpUtil;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Login.BaseAppActivity;
import com.zhang.xiaofei.smartsleep.YMApplication;

public class PDFActivity extends BaseAppActivity {

    PDFView pdfView;
    ImageButton ibLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        ibLeft = (ImageButton)findViewById(R.id.im_l);
        ibLeft.setImageResource(R.mipmap.suggest_icon_back);
        ibLeft.setVisibility(View.VISIBLE);
        ibLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pdfView = (PDFView)findViewById(R.id.pdfView);
        String language = SpUtil.getInstance(YMApplication.getContext()).getString(SpUtil.LANGUAGE);
        if (language.equals("en")) {
            pdfView.fromAsset("helpen.pdf").load();
        } else {
            pdfView.fromAsset("helpzh.pdf").load();
        }
    }
}