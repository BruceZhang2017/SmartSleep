package com.zhang.xiaofei.smartsleep.UI.Login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ansen.http.net.HTTPCaller;
import com.ansen.http.net.NameValuePair;
import com.ansen.http.net.RequestDataCallback;
import com.zhang.xiaofei.smartsleep.Kit.DB.YMUserInfoManager;
import com.zhang.xiaofei.smartsleep.Kit.NetworkUtil.NetworkUtils;
import com.zhang.xiaofei.smartsleep.Kit.ValidateHelper;
import com.zhang.xiaofei.smartsleep.Kit.Webview.WebActivity;
import com.zhang.xiaofei.smartsleep.Model.Login.UserModel;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Home.HomeActivity;
import com.zhang.xiaofei.smartsleep.YMApplication;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class LoginActivity extends BaseAppActivity {

    @BindView(R.id.btn_login) Button btnLogin;
    @BindView(R.id.et_phone) EditText etPhone;
    @BindView(R.id.et_pwd) EditText etPWD;
    @BindView(R.id.btn_area) Button btnArea;
    @BindView(R.id.btn_code) Button btnCode;
    @BindView(R.id.tv_agree_login)TextView tvAgree;
    static int kRequestCode = 1;
    private static String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        // 登录按钮点击
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = etPhone.getText().toString();
                String code = etPWD.getText().toString();
                String area = btnArea.getText().toString();
                if (!ValidateHelper.isPhoneNumber(phone)) {
                    Toast.makeText(LoginActivity.this, getResources().getText(R.string.login_input_phone_fail), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (code.length() <= 0) {
                    return;
                }
                Log.i(TAG, "Phone:" + phone + " area:" + area + " code:" + code);
                showHUD();
                //SMSSDK.submitVerificationCode(area, phone, code);
                login(phone, code, area);
            }
        });

        btnArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, CountryActivity.class);
                startActivityForResult(intent, kRequestCode);
            }
        });

        btnCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = etPhone.getText().toString();
                if (!ValidateHelper.isPhoneNumber(phone)) {
                    Toast.makeText(LoginActivity.this, getResources().getText(R.string.login_input_phone_fail), Toast.LENGTH_SHORT).show();
                    return;
                }
                showHUD();
                String zone = btnArea.getText().toString().replace("+", "");
                SMSSDK.getVerificationCode(zone, phone);
            }
        });

        SMSSDK.registerEventHandler(eh);
        String agree = getResources().getString(R.string.login_agree);
        String text = getResources().getString(R.string.login_agree) + "《" + getResources().getString(R.string.login_term_for_usage) + "》";
        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(text);
        // 单独设置字体颜色

        spannableBuilder.setSpan(new MyClickableSpan(), agree.length(), text.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvAgree.setText(spannableBuilder);
        tvAgree.setMovementMethod(LinkMovementMethod.getInstance());

    }

    class MyClickableSpan extends ClickableSpan {

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            //设置文本的颜色
            ds.setColor(0xFF8EA9E1);
            //超链接形式的下划线，false 表示不显示下划线，true表示显示下划线
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(LoginActivity.this, WebActivity.class);
            intent.putExtra("url", "https://www.baidu.com");
            startActivity(intent);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String countryName = bundle.getString("countryName");
                    String countryNumber = bundle.getString("countryNumber");

                    btnArea.setText(countryNumber);

                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void requestCode() {

    }

    EventHandler eh = new EventHandler(){
        @Override
        public void afterEvent(int event, int result, Object data) {
            // TODO 此处不可直接处理UI线程，处理后续操作需传到主线程中操作
            Message msg = new Message();
            msg.arg1 = event;
            msg.arg2 = result;
            msg.obj = data;
            mHandler.sendMessage(msg);
        }
    };

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            hideHUD();
            int result = msg.arg2;
            int event = msg.arg1;
            Object data = msg.obj;
            if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                    Toast.makeText(LoginActivity.this, getResources().getText(R.string.login_success), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, LoginMoreActivity.class);
                    startActivity(intent);
                }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                    //获取验证码成功
                    Toast.makeText(LoginActivity.this, getResources().getText(R.string.login_sms_tip), Toast.LENGTH_SHORT).show();
                }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                    //返回支持发送验证码的国家列表
                }
            }else{
                ((Throwable)data).printStackTrace();
                if (NetworkUtils.isNetWorkAvailable(LoginActivity.this)) {
                    Toast.makeText(LoginActivity.this, getResources().getText(R.string.mobile_error_tip), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, getResources().getText(R.string.common_check_network), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private void login(String phone, String code, String zone) {
        com.ansen.http.net.Header header = new com.ansen.http.net.Header("Content-Type", "application/x-www-form-urlencoded");
        List<NameValuePair> postParam = new ArrayList<>();
        postParam.add(new NameValuePair("phone",phone.trim()));
        postParam.add(new NameValuePair("code",code.trim()));
        postParam.add(new NameValuePair("zone", zone.trim().replace("+", "")));
        HTTPCaller.getInstance().post(
                UserModel.class,
                YMApplication.getInstance().domain() + "app/login/verifySmsCode",
                new com.ansen.http.net.Header[]{header},
                postParam,
                requestDataCallback
        );
    }

    private RequestDataCallback requestDataCallback = new RequestDataCallback<UserModel>() {
        @Override
        public void dataCallback(int status, UserModel user) {
            hideHUD();
            System.out.println("网络请求返回的Status:" + status);
            if(user==null || user.getCode() != 200){
                if (user == null) {
                    Toast.makeText(LoginActivity.this, getResources().getText(R.string.login_fail), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, user.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(LoginActivity.this, getResources().getText(R.string.login_success), Toast.LENGTH_SHORT).show();
                if (user.getUserInfo().getNikeName() != null && user.getUserInfo().getNikeName().length() > 0) {
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(LoginActivity.this, LoginMoreActivity.class);
                    startActivity(intent);
                }
                YMUserInfoManager userManager = new YMUserInfoManager( LoginActivity.this);
                userManager.saveUserInfo(user);
            }

        }

        @Override
        public void dataCallback(UserModel obj) {
            hideHUD();
            if (obj == null) {
                Toast.makeText(LoginActivity.this, getResources().getText(R.string.common_check_network), Toast.LENGTH_SHORT).show();
            }
        }
    };

    //退出时的时间
    private long mExitTime;
    //对返回键进行监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    //退出方法
    private void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(LoginActivity.this, getResources().getString(R.string.common_exit_app), Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            //用户退出处理
            finish();
            System.exit(0);
        }
    }

    @Override
    public void onBackPressed() {

    }

}
