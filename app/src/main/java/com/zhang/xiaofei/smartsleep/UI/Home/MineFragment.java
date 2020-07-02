package com.zhang.xiaofei.smartsleep.UI.Home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jpeng.jptabbar.JPTabBar;
import com.zhang.xiaofei.smartsleep.Kit.DB.CacheUtil;
import com.zhang.xiaofei.smartsleep.Kit.DB.YMUserInfoManager;
import com.zhang.xiaofei.smartsleep.Model.Login.UserModel;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Login.LoginActivity;
import com.zhang.xiaofei.smartsleep.UI.Login.LoginMoreActivity;
import com.zhang.xiaofei.smartsleep.UI.Me.DeviceManageActivity;
import com.zhang.xiaofei.smartsleep.UI.Me.FeedbackActivity;
import com.zhang.xiaofei.smartsleep.UI.Me.FeedbackListActivity;
import com.zhang.xiaofei.smartsleep.UI.Me.FriendRankActivity;
import com.zhang.xiaofei.smartsleep.UI.Me.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jpeng on 16-11-14.
 */
public class MineFragment extends Fragment implements View.OnClickListener {

    List icons;
    private ImageButton ibEdit;
    private TextView tvID;
    private TextView tvNickName;
    private ImageView ivHead;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.tab4,null);
        ibEdit = (ImageButton)layout.findViewById(R.id.ib_edit);
        ibEdit.setOnClickListener(this);
        ListView listView = layout.findViewById(R.id.lv_items);
        icons = new ArrayList<Integer>();
        icons.add(R.mipmap.me_icon_equipment);
        icons.add(R.mipmap.me_icon_ranking);
        icons.add(R.mipmap.me_icon_suggest);
        icons.add(R.mipmap.me_icon_setting);
        icons.add(R.mipmap.tishi);
        icons.add(R.mipmap.tishi);
        List<String> appNames = new ArrayList<>();
        appNames.add(getResources().getString(R.string.mine_device_manage));
        appNames.add(getResources().getString(R.string.mine_ranking));
        appNames.add(getResources().getString(R.string.mine_feedback));
        appNames.add(getResources().getString(R.string.mine_settings));
        appNames.add(getResources().getString(R.string.off_bed_reminder));
        appNames.add(getResources().getString(R.string.abnormal_heart_rate_reminder));
        //适配adapter
        listView.setAdapter(new AppListAdapter(appNames));
        ivHead = (ImageView)layout.findViewById(R.id.iv_head);
        ivHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginMoreActivity.class);
                intent.putExtra("value",1);
                startActivity(intent);
            }
        });
        tvID = (TextView)layout.findViewById(R.id.tv_userId);
        tvNickName = (TextView)layout.findViewById(R.id.tv_nickname);
        YMUserInfoManager userManager = new YMUserInfoManager( getActivity());
        UserModel userModel = userManager.loadUserInfo();
        String photo = userModel.getUserInfo().getPhoto();
        if (photo != null && photo.startsWith("http")) {
            Glide.with(this).load(photo).placeholder(R.mipmap.login_icon_head).into(ivHead);
        }
        String nickname = userModel.getUserInfo().getNikeName();
        if (nickname != null && nickname.length() > 0) {
            tvNickName.setText(nickname);
        }
        int iID = userModel.getUserInfo().getUserId();
        if (iID > 0) {
            tvID.setText("ID " + iID);
        }
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        YMUserInfoManager userManager = new YMUserInfoManager( getActivity());
        UserModel userModel = userManager.loadUserInfo();
        String photo = userModel.getUserInfo().getPhoto();
        if (photo != null && photo.startsWith("http")) {
            Glide.with(this).load(photo).placeholder(R.mipmap.login_icon_head).into(ivHead);
        }
        String nickname = userModel.getUserInfo().getNikeName();
        if (nickname != null && nickname.length() > 0) {
            tvNickName.setText(nickname);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_edit:
                Intent intent = new Intent(getActivity(), LoginMoreActivity.class);
                intent.putExtra("value",1);
                startActivity(intent);
                break;
        }
    }

    public class AppListAdapter extends BaseAdapter {
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_me_item, parent,false);
            ImageView appIconImageView = convertView.findViewById(R.id.iv_icon);
            TextView appNameTextView = convertView.findViewById(R.id.tv_title);
            ImageView arrowImageView = convertView.findViewById(R.id.iv_arror);
            Switch mSwitch = convertView.findViewById(R.id.switch1);
            mSwitch.setVisibility(position < 4 ? View.INVISIBLE : View.VISIBLE);
            arrowImageView.setVisibility(position < 4 ? View.VISIBLE : View.INVISIBLE);
            appNameTextView.setText(mAppNames.get(position));
            appIconImageView.setBackgroundResource((int)(icons.get(position)));
            appIconImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (position == 4) {
                        Toast.makeText(getContext(), R.string.bed_away_15_minutes_tip, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    if (position == 5) {
                        Toast.makeText(getContext(), R.string.heart_rate_15_minutes_tip, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    return false;
                }
            });
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == 3) {
                        Intent intent = new Intent(((HomeActivity)getContext()), SettingsActivity.class);
                        startActivity(intent);
                    } else if (position == 2) {
                        Intent intent = new Intent(((HomeActivity)getContext()), FeedbackListActivity.class);
                        startActivity(intent);
                    } else if (position == 0) {
                        Intent intent = new Intent(((HomeActivity)getContext()), DeviceManageActivity.class);
                        startActivity(intent);
                    } else if (position == 4) {

                    } else if (position == 5) {

                    } else { // 好友排行
                        Intent intent = new Intent(((HomeActivity)getContext()), FriendRankActivity.class);
                        startActivity(intent);
                    }
                }
            });
            mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (position == 4) {
                        CacheUtil.getInstance(getContext()).putBool("GetAway", isChecked);
                    } else if (position == 5) {
                        CacheUtil.getInstance(getContext()).putBool("AbnormalHeartRate", isChecked);
                    }
                }
            });
            return convertView;
        }
    }

}


