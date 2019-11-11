package com.zhang.xiaofei.smartsleep.UI.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.Tools.CharacterParserUtil;
import com.zhang.xiaofei.smartsleep.Tools.CountryComparator;
import com.zhang.xiaofei.smartsleep.Tools.CountrySortAdapter;
import com.zhang.xiaofei.smartsleep.Tools.CountrySortModel;
import com.zhang.xiaofei.smartsleep.Tools.GetCountryNameSort;
import com.zhang.xiaofei.smartsleep.Tools.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CountryActivity extends BaseAppActivity {

    String TAG = "CountryActivity";

    private List<CountrySortModel> mAllCountryList;
    private ListView country_lv_countryList;
    private CountrySortAdapter adapter;
    private SideBar sideBar;
    private TextView dialog;
    private CountryComparator pinyinComparator;
    private GetCountryNameSort countryChangeUtil;
    private CharacterParserUtil characterParserUtil;
    private TextView tvTitle;
    private ImageButton ibLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_country);
        initView();
        setListener();
        getCountryList();

        tvTitle = (TextView)findViewById(R.id.tv_title);
        tvTitle.setText(R.string.middle_country_region);
        ibLeft = (ImageButton)findViewById(R.id.im_l);
        ibLeft.setImageResource(R.mipmap.suggest_icon_back);
        ibLeft.setVisibility(View.VISIBLE);
        ibLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 初始化界面
     */
    private void initView() {

        country_lv_countryList = (ListView) findViewById(R.id.country_lv_list);

        dialog = (TextView) findViewById(R.id.country_dialog);
        sideBar = (SideBar) findViewById(R.id.country_sidebar);
        sideBar.setTextView(dialog);

        mAllCountryList = new ArrayList<CountrySortModel>();
        pinyinComparator = new CountryComparator();
        countryChangeUtil = new GetCountryNameSort();
        characterParserUtil = new CharacterParserUtil();

        // 将联系人进行排序，按照A~Z的顺序
        Collections.sort(mAllCountryList, pinyinComparator);
        adapter = new CountrySortAdapter(this, mAllCountryList);
        country_lv_countryList.setAdapter(adapter);

    }

    /****
     * 添加监听
     */
    private void setListener() {

        // 右侧sideBar监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    country_lv_countryList.setSelection(position);
                }
            }
        });

        country_lv_countryList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
                String countryName = null;
                String countryNumber = null;

                countryName = mAllCountryList.get(position).countryName;
                countryNumber = mAllCountryList.get(position).countryNumber;

                Intent intent = new Intent();
                intent.setClass(CountryActivity.this, LoginActivity.class);
                intent.putExtra("countryName", countryName);
                intent.putExtra("countryNumber", countryNumber);
                setResult(RESULT_OK, intent);
                Log.e(TAG, "countryName: + " + countryName + "countryNumber: " + countryNumber);
                finish();

            }
        });

    }

    /**
     * 获取国家列表
     */
    private void getCountryList() {
        String[] countryList = getResources().getStringArray(R.array.country_code_list_ch);

        for (int i = 0, length = countryList.length; i < length; i++) {
            String[] country = countryList[i].split("\\*");

            String countryName = country[0];
            String countryNumber = country[1];
            String countrySortKey = characterParserUtil.getSelling(countryName);
            CountrySortModel countrySortModel = new CountrySortModel(countryName, countryNumber,
                    countrySortKey);
            String sortLetter = countryChangeUtil.getSortLetterBySortKey(countrySortKey);
            if (sortLetter == null) {
                sortLetter = countryChangeUtil.getSortLetterBySortKey(countryName);
            }

            countrySortModel.sortLetters = sortLetter;
            mAllCountryList.add(countrySortModel);
        }

        Collections.sort(mAllCountryList, pinyinComparator);
        adapter.updateListView(mAllCountryList);
        Log.e(TAG, "changdu" + mAllCountryList.size());
    }
}
