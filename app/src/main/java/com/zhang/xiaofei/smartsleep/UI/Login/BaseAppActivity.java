package com.zhang.xiaofei.smartsleep.UI.Login;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.zhang.xiaofei.smartsleep.Kit.AndroidWorkaround;
import com.zhang.xiaofei.smartsleep.Kit.Language.LanguageUtil;
import com.zhang.xiaofei.smartsleep.Kit.Language.SpUtil;
import com.zhang.xiaofei.smartsleep.Kit.Statusbar.StatusBarTrunsparent;
import com.zhang.xiaofei.smartsleep.YMApplication;

public class BaseAppActivity extends AppCompatActivity {

    public KProgressHUD hud;

    public boolean environmentForStore() { // 设置标识符 true or false
        return false;

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBarTrunsparent.statusBarTrunsparent(this);
        super.onCreate(savedInstanceState);
        if (AndroidWorkaround.checkDeviceHasNavigationBar(this)) {
            AndroidWorkaround.assistActivity(findViewById(android.R.id.content));
        }
        String language = SpUtil.getInstance(YMApplication.getContext()).getString(SpUtil.LANGUAGE);
        LanguageUtil.changeAppLanguage(this, language);
    }

    public void showHUD() {
        hud = KProgressHUD.create(this)
                .setSize(150,150)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
        hud.show();
    }

    public void hideHUD() {
        hud.dismiss();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        String language = SpUtil.getInstance(YMApplication.getContext()).getString(SpUtil.LANGUAGE);
        if (language.length() == 0) {
            language = LanguageUtil.getSystemLanguage();
        }
        Log.d("Language", "当前语言：" + language);
        super.attachBaseContext(LanguageUtil.attachBaseContext(newBase, language));
    }

}
