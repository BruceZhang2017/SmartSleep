package com.zhang.xiaofei.smartsleep.UI.Me;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.zhang.xiaofei.smartsleep.Kit.CleanCacheUtil.CleanCacheUtil;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Login.BaseAppActivity;

public class ClearCacheActivity extends BaseAppActivity {

    private TextView tvTitle;
    private ImageButton ibLeft;
    private Button btnClear;
    private TextView tvCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear_cache);
        tvTitle = (TextView)findViewById(R.id.tv_title);
        tvTitle.setText(R.string.mine_clear_cache);
        ibLeft = (ImageButton)findViewById(R.id.im_l);
        ibLeft.setImageResource(R.mipmap.suggest_icon_back);
        ibLeft.setVisibility(View.VISIBLE);
        ibLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvCache = (TextView)findViewById(R.id.tv_cache_value);
        try {
            tvCache.setText(CleanCacheUtil.getTotalCacheSize(getApplicationContext()));
        }catch (Exception e) {
            System.out.println("读取缓存大小出错");
        }
        btnClear = (Button)findViewById(R.id.btn_clear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CleanCacheUtil.clearAllCache(getApplicationContext());
                Toast.makeText(ClearCacheActivity.this, "缓存已清空", Toast.LENGTH_SHORT).show();
                tvCache.setText("0 B");
            }
        });
    }
}
