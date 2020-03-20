package com.zhang.xiaofei.smartsleep.UI.Home;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.zhang.xiaofei.smartsleep.Kit.DB.YMUserInfoManager;
import com.zhang.xiaofei.smartsleep.Model.Login.UserModel;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Login.BaseAppActivity;

public class ShareSleepQualityActivity extends BaseAppActivity {

    private ImageButton ibLeft;
    private ImageButton ibRight;
    private TextView tvNickName;
    private RoundedImageView ivHead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_sleep_quality);
        ibLeft = (ImageButton)findViewById(R.id.im_l);
        ibLeft.setImageResource(R.mipmap.suggest_icon_back);
        ibLeft.setVisibility(View.VISIBLE);
        ibLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ibRight = (ImageButton)findViewById(R.id.im_r);
        ibRight.setImageResource(R.mipmap.xiangce);
        ibRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        tvNickName = (TextView)findViewById(R.id.tv_nickname);
        ivHead = (RoundedImageView)findViewById(R.id.iv_head);
        YMUserInfoManager userManager = new YMUserInfoManager(this);
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
}
