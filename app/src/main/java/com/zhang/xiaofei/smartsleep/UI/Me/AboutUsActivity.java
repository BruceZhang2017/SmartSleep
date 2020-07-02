package com.zhang.xiaofei.smartsleep.UI.Me;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhang.xiaofei.smartsleep.Kit.Application.LogcatHelper;
import com.zhang.xiaofei.smartsleep.Kit.Language.SpUtil;
import com.zhang.xiaofei.smartsleep.Kit.Webview.WebActivity;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Login.BaseAppActivity;
import com.zhang.xiaofei.smartsleep.Vendor.EsptouchDemoActivity;
import com.zhang.xiaofei.smartsleep.YMApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class AboutUsActivity extends BaseAppActivity {

    private ImageButton ibLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us); //
        ListView listView = findViewById(R.id.lv_settings);
        List<String> appNames = new ArrayList<>();
        appNames.add(getResources().getString(R.string.mine_checking_update));
        appNames.add(getResources().getString(R.string.login_term_for_usage));
        appNames.add(getResources().getString(R.string.upload_log));
        //适配adapter
        listView.setAdapter(new AboutUsActivity.AppListAdapter(appNames));

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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
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
            convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_settings_item, parent,false);
            TextView textView = convertView.findViewById(R.id.tv_title);
            textView.setText(mAppNames.get(position));
            TextView tvValue = convertView.findViewById(R.id.tv_value);
            if (position == 0) {
                tvValue.setText("V1.0.23");
            } else {
                tvValue.setText("");
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == 0) {
                        Toast.makeText(AboutUsActivity.this, R.string.latest_version_tip, Toast.LENGTH_SHORT).show();
                    } else if (position == 1) {
                        String language = SpUtil.getInstance(YMApplication.getContext()).getString(SpUtil.LANGUAGE);
                        String url = "";
                        if (language.equals("en")) {
                            url = "http://test2.5811.com.cn/fanqie/dayayiliao/agree/agree_en.html";
                        } else {
                            url = "http://test2.5811.com.cn/fanqie/dayayiliao/agree/yamind_agree.html";
                        }
                        Intent intent = new Intent(AboutUsActivity.this, WebActivity.class);
                        intent.putExtra("url", url);
                        AboutUsActivity.this.startActivity(intent);
                    } else {
                        showLogDialog(LogcatHelper.FILE_LOG_PATH);
                    }
                }
            });
            return convertView;
        }
    }

    private boolean checkLogFileIfExsit(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }
        return true;
    }

    private void uploadLog(String filePath) {
        if (!checkLogFileIfExsit(filePath)) {
            Toast.makeText(AboutUsActivity.this, "App不存在log文件，请开启相应权限", Toast.LENGTH_LONG).show();
            return;
        }
        Intent email = new Intent(android.content.Intent.ACTION_SEND);
// 附件
        File file = new File(filePath);
//邮件发送类型：带附件的邮件
        email.setType("application/octet-stream");
        //邮件接收者（数组，可以是多位接收者）
        String[] emailReciver = new String[]{"liangzhihao@yamind.com.cn","bruce.zhang@anker.com"};

        String  emailTitle = "日志文件";
        String emailContent = "附件里有日志文件";
//设置邮件地址
        email.putExtra(android.content.Intent.EXTRA_EMAIL, emailReciver);
//设置邮件标题
        email.putExtra(android.content.Intent.EXTRA_SUBJECT, emailTitle);
//设置发送的内容
        email.putExtra(android.content.Intent.EXTRA_TEXT, emailContent);
//附件
        Uri uri;
        uri = Uri.fromFile(file);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(this,"com.manage_system.fileProvider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        email.putExtra(Intent.EXTRA_STREAM, uri);
        //调用系统的邮件系统
        startActivity(Intent.createChooser(email, "请选择邮件发送软件"));
    }

    private void showLogDialog(String logpath) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入");    //设置对话框标题
        final EditText edit = new EditText(this);
        edit.setText(logpath);
        builder.setView(edit);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String filePath = edit.getText().toString();
                uploadLog(filePath);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setCancelable(true);    //设置按钮是否可以按返回键取消,false则不可以取消
        AlertDialog dialog = builder.create();  //创建对话框
        dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        dialog.show();
    }
}
