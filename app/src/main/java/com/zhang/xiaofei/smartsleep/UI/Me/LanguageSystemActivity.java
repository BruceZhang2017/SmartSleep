package com.zhang.xiaofei.smartsleep.UI.Me;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhang.xiaofei.smartsleep.Kit.Language.LanguageType;
import com.zhang.xiaofei.smartsleep.Kit.Language.LanguageUtil;
import com.zhang.xiaofei.smartsleep.Kit.Language.SpUtil;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Home.HomeActivity;
import com.zhang.xiaofei.smartsleep.UI.Login.BaseAppActivity;
import com.zhang.xiaofei.smartsleep.UI.Login.LoginActivity;
import com.zhang.xiaofei.smartsleep.UI.Login.StartPageActivity;
import com.zhang.xiaofei.smartsleep.YMApplication;

import java.util.ArrayList;
import java.util.List;

public class LanguageSystemActivity extends BaseAppActivity {

    private final String TAG = getClass().getSimpleName();
    private TextView tvTitle;
    private ImageButton ibLeft;
    private AppListAdapter adapter;
    private TextView tvCurrent;
    List<String> appNames;
    String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_system); //
        ListView listView = findViewById(R.id.lv_settings);
        appNames = new ArrayList<>();
        appNames.add(getResources().getString(R.string.common_language_chinese));
        appNames.add(getResources().getString(R.string.common_language_english));
        //适配adapter
        adapter = new AppListAdapter(appNames);
        listView.setAdapter(adapter);

        tvTitle = (TextView)findViewById(R.id.tv_title);
        tvTitle.setText(R.string.mine_select_system_language);
        ibLeft = (ImageButton)findViewById(R.id.im_l);
        ibLeft.setImageResource(R.mipmap.suggest_icon_back);
        ibLeft.setVisibility(View.VISIBLE);
        ibLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvCurrent = (TextView)findViewById(R.id.tv_currnet_language_value);
        language = SpUtil.getInstance(YMApplication.getContext()).getString(SpUtil.LANGUAGE);
        if (language.length() == 0) {
            language = LanguageUtil.getSystemLanguage();
        }
        Log.d(TAG, "当前语言：" + language);
        if (language.equals("en")) {
            tvCurrent.setText(appNames.get(1));
        } else {
            tvCurrent.setText(appNames.get(0));
        }
    }

    private class AppListAdapter extends BaseAdapter {
        //要填充的数据列表
        List<String> mAppNames;
        public AppListAdapter(List<String> appNames){
            this.mAppNames = appNames;
        }
        @Override
        public int getCount() {
            //返回数据总数
            return mAppNames.size();
        }
        @Override
        public Object getItem(int position) {
            //返回当前position位置的item
            return mAppNames.get(position);
        }
        @Override
        public long getItemId(int position) {
            //返回当前position位置的item的id
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //处理view与data，进行数据填充
            convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_language_item, parent,false);
            TextView textView = (TextView)convertView.findViewById(R.id.tv_title);
            textView.setText(mAppNames.get(position));
            ImageView ivSelected = (ImageView)convertView.findViewById(R.id.iv_select);
            ImageView ivLine = (ImageView)convertView.findViewById(R.id.iv_line);

            if (position == 0) {
                if(language.equals("zh")) {
                    ivSelected.setVisibility(View.VISIBLE);
                } else {
                    ivSelected.setVisibility(View.INVISIBLE);
                }
            } else {
                if (language.equals("en")) {
                    ivSelected.setVisibility(View.VISIBLE);
                } else {
                    ivSelected.setVisibility(View.INVISIBLE);
                }
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (position == 0) {
                        if (language.equals("en")) {
                            changeLanguage(LanguageType.CHINESE.getLanguage());
                            adapter.notifyDataSetChanged();
                            tvCurrent.setText(appNames.get(0));
                        }
                    } else if (position == 1) {
                        if (!language.equals("en")) {
                            changeLanguage(LanguageType.ENGLISH.getLanguage());
                            adapter.notifyDataSetChanged();
                            tvCurrent.setText(appNames.get(1));
                        }
                    }
                }
            });
            return convertView;
        }
    }

    /**
     * 如果是7.0以下，我们需要调用changeAppLanguage方法，
     * 如果是7.0及以上系统，直接把我们想要切换的语言类型保存在SharedPreferences中即可
     * 然后重新启动MainActivity
     * @param language
     */
    private void changeLanguage(String language) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            LanguageUtil.changeAppLanguage(YMApplication.getContext(), language);
        }
        SpUtil.getInstance(this).putString(SpUtil.LANGUAGE, language);
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
