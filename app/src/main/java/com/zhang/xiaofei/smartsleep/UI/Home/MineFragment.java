package com.zhang.xiaofei.smartsleep.UI.Home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jpeng.jptabbar.JPTabBar;
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
        icons.add(R.mipmap.me_icon_setting);
        icons.add(R.mipmap.me_icon_suggest);
        List<String> appNames = new ArrayList<>();
        appNames.add(getResources().getString(R.string.mine_device_manage));
        appNames.add(getResources().getString(R.string.mine_ranking));
        appNames.add(getResources().getString(R.string.mine_feedback));
        appNames.add(getResources().getString(R.string.mine_settings));
        //适配adapter
        listView.setAdapter(new AppListAdapter(appNames));
        ivHead = (ImageView)layout.findViewById(R.id.iv_head);
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
            appNameTextView.setText(mAppNames.get(position));
            appIconImageView.setBackgroundResource((int)(icons.get(position)));
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
                    } else { // 好友排行
                        Intent intent = new Intent(((HomeActivity)getContext()), FriendRankActivity.class);
                        startActivity(intent);
                    }
                }
            });
            return convertView;
        }
    }

}


