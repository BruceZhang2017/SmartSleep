package com.zhang.xiaofei.smartsleep.Kit.DB;

import android.content.Context;
import android.content.SharedPreferences;

import com.zhang.xiaofei.smartsleep.Model.Login.UserInfoModel;
import com.zhang.xiaofei.smartsleep.Model.Login.UserModel;

public class YMUserInfoManager {

    private Context context;

    public YMUserInfoManager(Context context) {
        this.context = context;
    }

    public void saveUserInfo(UserModel userModel) {
        SharedPreferences preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("expire", userModel.getExpire());
        editor.putInt("userId", userModel.getUserInfo().getUserId());
        editor.putString("token", userModel.getToken());
        String photo = userModel.getUserInfo().getPhoto();
        if (photo != null && photo.length() > 0) {
            editor.putString("photo", userModel.getUserInfo().getPhoto());
        }
        String nickName = userModel.getUserInfo().getNikeName();
        if (nickName != null && nickName.length() > 0) {
            editor.putString("nickName", nickName);
        }
        int sex = userModel.getUserInfo().getSex();
        if (sex >= 0 && sex <= 1) {
            editor.putInt("sex", sex);
        }
        String birthday = userModel.getUserInfo().getBirthday();
        if (birthday != null && birthday.length() > 0) {
            editor.putString("birthday", birthday);
        }
        int height = userModel.getUserInfo().getHeight();
        if (height > 0) {
            editor.putInt("height", height);
        }
        int weight = userModel.getUserInfo().getWeight();
        if (weight > 0) {
            editor.putInt("weight", weight);
        }

        editor.commit();
    }

    public UserModel loadUserInfo() {
        SharedPreferences preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        String expire = preferences.getString("expire", "");
        if (expire.length() == 0) {
            return null;
        }
        int userId = preferences.getInt("userId", 0);
        String token = preferences.getString("token", "");
        String photo = preferences.getString("photo", "");
        String nickName = preferences.getString("nickName", "");
        int sex = preferences.getInt("sex", -1);
        String birthday = preferences.getString("birthday", "");
        int height = preferences.getInt("height", 0);
        int weight = preferences.getInt("weight", 0);
        UserModel userModel = new UserModel();
        userModel.setExpire(expire);
        userModel.setToken(token);
        UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.setUserId(userId);
        userInfoModel.setPhoto(photo);
        userInfoModel.setNikeName(nickName);
        userInfoModel.setSex(sex);
        userInfoModel.setBirthday(birthday);
        userInfoModel.setHeight(height);
        userInfoModel.setWeight(weight);
        userModel.setUserInfo(userInfoModel);
        return userModel;
    }

    public void clearUserInfo() {
        SharedPreferences preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.clear();
        editor.commit();
    }
}
