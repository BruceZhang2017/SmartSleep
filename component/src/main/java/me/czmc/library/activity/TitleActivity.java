package me.czmc.library.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.czmc.library.R;
import me.czmc.library.dialog.ProgressDialog;
import me.czmc.library.utils.OSUtils;

/**
 * Created by czmc on 2016/10/8.
 */

public class TitleActivity extends AppCompatActivity {
    public ActionBar mActionbar;
    public ProgressDialog progressDialog;
    boolean isWhite;
    public TextView tv_title;
    public ImageView btn_left;
    public ImageView btn_right;
    public TextView tv_title_left;

    public void  setCustomContentView(int layoutId,String title){
        setContentViewWithTitle(layoutId, title, isWhite?R.mipmap.top_back:R.mipmap.icon_l_arrow, 0, "", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        }, null);
    }
    /**
     * 设定带title
     * @param layoutId
     * @param title
     * @param img_l_id
     * @param img_r_id
     * @param leftAction
     * @param rightAction
     */
    public void setContentViewWithTitle(int layoutId, String title, int img_l_id, int img_r_id,String right_text, View.OnClickListener leftAction, View.OnClickListener rightAction) {
        setContentView(layoutId);
//        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.layout_custom_action_bar);
        initCustomActionBar();
        if(isWhite) {
            if (OSUtils.isFlyme()) {
                FlymeSetStatusBarLightMode(getWindow(), true);
            }
            if (OSUtils.isMIUI()) {
                MIUISetStatusBarLightMode(getWindow(), true);
            }
        }
        View titlebar = mActionbar.getCustomView();

        tv_title = ((TextView)  titlebar.findViewById(R.id.tv_title));
        tv_title.setTextColor(isWhite? Color.BLACK:Color.WHITE);
        tv_title.setText(title);
        if (img_l_id > 0) {
            btn_left = (ImageView) titlebar.findViewById(R.id.im_l);
            btn_left.setVisibility(View.VISIBLE);
            btn_left.setImageResource(img_l_id);
            if(leftAction!=null) {
                btn_left.setOnClickListener(leftAction);
            }
        }
        if (img_r_id > 0) {
            btn_right = (ImageView) titlebar.findViewById(R.id.im_r);
            btn_right.setVisibility(View.VISIBLE);
            btn_right.setImageResource(img_r_id);
            if(rightAction!=null) {
                btn_right.setOnClickListener(rightAction);
            }
        }
        if (!TextUtils.isEmpty(right_text)) {
            TextView btn_right = (TextView) titlebar.findViewById(R.id.tv_r);
            btn_right.setVisibility(View.VISIBLE);
            btn_right.setText(right_text);
            if(rightAction!=null) {
                btn_right.setOnClickListener(rightAction);
            }
        }

        tv_title_left = ((TextView)  titlebar.findViewById(R.id.tv_left_title));
        tv_title_left.setText(R.string.tab_finding);
    }
    public void  setWhiteModel(boolean isWhite){
        this.isWhite=isWhite;
    }
    private boolean initCustomActionBar() {
        mActionbar = getSupportActionBar();
        //配合android:windowContentOverlay 去阴影
        //mActionbar.setElevation(1);
        if (mActionbar == null) {
            return false;
        }
        mActionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionbar.setDisplayShowCustomEnabled(true);
        mActionbar.setCustomView(R.layout.part_title);
        return true;
    }
    /**
     * 设置状态栏图标为深色和魅族特定的文字风格，Flyme4.0以上
     * 可以用来判断是否为Flyme用户
     * @param window 需要设置的窗口
     * @param dark 是否把状态栏字体及图标颜色设置为深色
     * @return  boolean 成功执行返回true
     *
     */
    public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     * @param window 需要设置的窗口
     * @param dark 是否把状态栏字体及图标颜色设置为深色
     * @return  boolean 成功执行返回true
     *
     */
    public static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field  field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if(dark){
                    extraFlagField.invoke(window,darkModeFlag,darkModeFlag);//状态栏透明且黑色字体
                }else{
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result=true;
            }catch (Exception e){

            }
        }
        return result;
    }
    /**
     * 显示进度条对话框
     */
    public void showProgress(String content){
        dismissProgress();
        progressDialog = ProgressDialog.show(this,content);
        progressDialog.setCancelable(false);
    }

    /**
     * 关闭进度条对话框
     */
    public void dismissProgress(){
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void showToast(String tip){
        Toast toast = Toast.makeText(this,tip,Toast.LENGTH_SHORT);
        toast .setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    /**
     * @param activity
     */
    public void startIntent(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }
}
