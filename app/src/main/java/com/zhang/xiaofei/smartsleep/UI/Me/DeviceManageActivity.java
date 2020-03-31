package com.zhang.xiaofei.smartsleep.UI.Me;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.ansen.http.net.HTTPCaller;
import com.ansen.http.net.Header;
import com.ansen.http.net.NameValuePair;
import com.ansen.http.net.RequestDataCallback;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.king.zxing.Intents;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.grid.BasicGridLayoutManager;
import com.mylhyl.circledialog.CircleDialog;
import com.zhang.xiaofei.smartsleep.Kit.DB.YMUserInfoManager;
import com.zhang.xiaofei.smartsleep.Model.Device.DeviceListModel;
import com.zhang.xiaofei.smartsleep.Model.Device.DeviceManager;
import com.zhang.xiaofei.smartsleep.Model.Device.DeviceModel;
import com.zhang.xiaofei.smartsleep.Model.Login.BaseProtocol;
import com.zhang.xiaofei.smartsleep.Model.Login.UserModel;
import com.zhang.xiaofei.smartsleep.Model.Record.RecordModel;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Home.BLESearchActivity;
import com.zhang.xiaofei.smartsleep.UI.Home.HomeActivity;
import com.zhang.xiaofei.smartsleep.UI.Login.BaseAppActivity;
import com.zhang.xiaofei.smartsleep.YMApplication;

import java.util.ArrayList;
import java.util.List;

import io.github.lizhangqu.coreprogress.ProgressUIListener;
import io.realm.Realm;
import io.realm.RealmResults;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class DeviceManageActivity extends BaseAppActivity implements EasyPermissions.PermissionCallbacks {

    protected UltimateRecyclerView listuv;
    protected GridJRAdapter mGridAdapter = null;
    private BasicGridLayoutManager mGridLayoutManager;
    private int columns = 2;
    private TextView tvTitle;
    private ImageButton ibLeft;
    private TextView tvRight;
    boolean isDrag = true;
    //private ItemTouchHelper mItemTouchHelper;
    public static final String TAG = "GLV";
    public static final int RC_CAMERA = 0X01;
    public static final int REQUEST_CODE_SCAN = 0X01;
    public Boolean isEdit = false;
    Realm mRealm;
    List<DeviceModel> team;
    private static final String DYNAMICACTION = "Filter";
    private static final String DEVICEACTION = "com.zhangxiaofei.broadcast.Filter";
    private DeviceReceiver dynamicReceiver;
    private int currentDeleteIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_manage);
        listuv = (UltimateRecyclerView) findViewById(R.id.device_list);
        mRealm = Realm.getDefaultInstance();
        mGridAdapter = new GridJRAdapter(getJRList());
        mGridAdapter.setSpanColumns(columns);
        mGridAdapter.activity = this;
        mGridLayoutManager = new BasicGridLayoutManager(this, columns, mGridAdapter);
        listuv.setLayoutManager(mGridLayoutManager);
        listuv.setHasFixedSize(false);
        listuv.setSaveEnabled(true);
        listuv.setClipToPadding(true);

        listuv.setAdapter(mGridAdapter);
        listuv.setItemAnimator(new DefaultItemAnimator());

        tvTitle = (TextView)findViewById(R.id.tv_title);
        tvTitle.setText(R.string.mine_device_manage);
        ibLeft = (ImageButton)findViewById(R.id.im_l);
        ibLeft.setImageResource(R.mipmap.suggest_icon_back);
        ibLeft.setVisibility(View.VISIBLE);
        ibLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvRight = (TextView)findViewById(R.id.tv_r);
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText(R.string.common_edit);
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEdit) {
                    isEdit = false;
                    tvRight.setText(R.string.common_edit);
                    tvRight.setTextColor(getResources().getColor(R.color.colorWhite));
                    mGridAdapter.isEdit = false;
                    mGridAdapter.notifyDataSetChanged();
                } else {
                    isEdit = true;
                    tvRight.setText(R.string.common_delete);
                    tvRight.setTextColor(getResources().getColor(R.color.color_F62C2C));
                    mGridAdapter.isEdit = true;
                    mGridAdapter.notifyDataSetChanged();
                }
            }
        });

        registerBroadcast();
    }

    private List<DeviceModel> getJRList() {
        team = new ArrayList<>();
        team.addAll(DeviceManager.getInstance().deviceList);
        team.add(new DeviceModel());
        return team;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Some permissions have been granted

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Some permissions have been denied
        // ...
    }

    /**
     * 检测拍摄权限
     */
    @AfterPermissionGranted(RC_CAMERA)
    private void checkCameraPermissions(){
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {//有权限
            startCodeScan();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "请至设置里打开相机权限。",
                    RC_CAMERA, perms);
        }
    }

    private void asyncThread(Runnable runnable){
        new Thread(runnable).start();
    }

    /**
     * 扫码
     */
    private void startScan(){
        Intent intent = new Intent(this, BLESearchActivity.class);
        startActivity(intent);
    }

    private void startCodeScan() {
        Intent intent = new Intent(this, EasyCaptureActivity.class);
        startActivityForResult(intent,REQUEST_CODE_SCAN);
    }

    public void showDialogBLEScanOrCodeScan() {
        new CircleDialog.Builder()
                .setTitle(getResources().getString(R.string.code_bt_scan))
                //标题字体颜值 0x909090 or Color.parseColor("#909090")
                .setPositive(getResources().getString(R.string.bt_scan), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startScan(); // 开始扫描
                    }
                })
                .setNeutral(getResources().getString(R.string.code_scan), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkCameraPermissions();
                    }
                })
                .setNegative(getResources().getString(R.string.middle_quit), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .show(getSupportFragmentManager());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
        unregisterBroadcast();
    }

    // 显示删除按钮
    public void deleteDevice(int index) {
        String mac = team.get(index).getMac();
        if (mac.length() != 17) {
            mac = team.get(index).getDeviceSerial();
        }
        new AlertView(
                getResources().getString(R.string.index_unbinding),
                mac,
                getResources().getString(R.string.middle_quit),
                new String[]{getResources().getString(R.string.middle_confirm)},
                null,
                this, AlertView.Style.Alert, new OnItemClickListener(){
            public void onItemClick(Object o,int position){
                System.out.println("将要删除的序号为：" + position);
                if (position == 0) {
                    currentDeleteIndex = index;
                    DeviceModel model = team.get(index);
                    deleteDevice(model.getDeviceSerial(), model.getMac());
                }
            }
        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_CODE_SCAN:
                    String result = data.getStringExtra(Intents.Scan.RESULT);
                    Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                    Intent intentBroadcast = new Intent();   //定义Intent
                    intentBroadcast.setAction(DYNAMICACTION);
                    intentBroadcast.putExtra("arg0", 0);
                    intentBroadcast.putExtra("result", result);
                    sendBroadcast(intentBroadcast);
                    break;
            }

        }
    }

        private void deleteDeviceFromDB(String mac) {
        final RealmResults<DeviceModel> deviceList = mRealm.where(DeviceModel.class).equalTo("mac", mac).findAll();
        if (deviceList != null && deviceList.size() > 0) {
            String serial = deviceList.get(0).getDeviceSerial();
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    deviceList.get(0).deleteFromRealm();
                    deleteDeviceNotify(serial, mac);
                }
            });
        }
        //deleteRecordDataFromDB(); // TODO: - 删除数据库里面的数据
    }

    private void deleteDeviceNotify(String serial, String mac) {
        Intent intentBroadcast = new Intent();   //定义Intent
        intentBroadcast.setAction(DYNAMICACTION);
        intentBroadcast.putExtra("arg0", 1);
        intentBroadcast.putExtra("serial", serial);
        intentBroadcast.putExtra("mac", mac);
        sendBroadcast(intentBroadcast);
    }

    // 删除数据库中所有的数据
    private void deleteRecordDataFromDB() {
        RealmResults<RecordModel> results = mRealm.where(RecordModel.class).findAll();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results.deleteAllFromRealm();
            }
        });
    }

    // 删除设备
    private void deleteDevice(String serial, String mac) {
        System.out.println("网络请求返回的serial:" + serial);
        showHUD();
        YMUserInfoManager userManager = new YMUserInfoManager( DeviceManageActivity.this);
        UserModel userModel = userManager.loadUserInfo();
        com.ansen.http.net.Header header = new com.ansen.http.net.Header("Content-Type", "multipart/form-data");
        com.ansen.http.net.Header headerToken = new com.ansen.http.net.Header("token", userModel.getToken());
        System.out.println("token:" + userModel.getToken());
        List<NameValuePair> postParam = new ArrayList<>();
        postParam.add(new NameValuePair("userId",userModel.getUserInfo().getUserId() + ""));
        postParam.add(new NameValuePair("serial",serial));
        postParam.add(new NameValuePair("mac",mac));
        postParam.add(new NameValuePair("version",1 + ""));

        HTTPCaller.getInstance().postFile(
                BaseProtocol.class,
                YMApplication.getInstance().domain() + "app/deviceManage/delBindDevice",
                new com.ansen.http.net.Header[]{header, headerToken},
                postParam,
                requestDataCallbackC
        );
    }

    private RequestDataCallback requestDataCallbackC = new RequestDataCallback<BaseProtocol>() {
        @Override
        public void dataCallback(int status, BaseProtocol user) {
            hideHUD();
            System.out.println("网络请求返回的Status:" + status);
            if(user==null){

            }else{
                if (user != null) {
                    System.out.println("网络请求返回的Code:" + user.getCode());
                    if (user.getCode() == 200) {
                        Toast.makeText(DeviceManageActivity.this, R.string.index_unbinding_success, Toast.LENGTH_SHORT).show();
                    }
                }
                if (status == 200) {
                    deleteDeviceFromDB(team.get(currentDeleteIndex).getMac());
                    team.remove(currentDeleteIndex);
                    mGridAdapter.notifyDataSetChanged();
                    if (team.size() == 0) {
                        tvRight.setText(" ");
                    }
                }
            }

        }

        @Override
        public void dataCallback(BaseProtocol obj) {
            hideHUD();
            if (obj == null) {
                Toast.makeText(DeviceManageActivity.this, getResources().getText(R.string.common_check_network), Toast.LENGTH_SHORT).show();
            }
        }
    };

    public class DeviceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DEVICEACTION)) {    //动作检测
                System.out.println("检测需要刷新设备");
                team.clear();
                RealmResults<DeviceModel> userList = mRealm.where(DeviceModel.class).findAll();
                if (userList != null && userList.size() > 0) {
                    for (DeviceModel model: userList) {
                        team.add(model);
                    }
                }
                team.add(new DeviceModel());
                mGridAdapter.notifyDataSetChanged();
            }
        }
    }

    private void registerBroadcast() {
        IntentFilter dynamic_filter = new IntentFilter();
        dynamic_filter.addAction(DEVICEACTION);    //添加动态广播的Action
        dynamicReceiver = new DeviceReceiver();
        registerReceiver(dynamicReceiver, dynamic_filter);    //注册自定义动态广播消息
    }

    private void unregisterBroadcast() {
        unregisterReceiver(dynamicReceiver);
    }

    // 跳转至OTA
    public void pushToOTA(int postion) {
        Intent intent = new Intent(DeviceManageActivity.this, OTAActivity.class);
        intent.putExtra("serial", team.get(postion).getDeviceSerial());
        if (team.get(postion).getDeviceType() == 3) {
            intent.putExtra("name", getResources().getString(R.string.homepage_breathing_machine));
            intent.putExtra("version", "1");
            intent.putExtra("mac", "00:00:00:00:00:00");
        } else {
            intent.putExtra("name", team.get(postion).getDeviceType() == 1 ? getResources().getString(R.string.report_yamy_sleep_belt) : getResources().getString(R.string.report_yamy_sleep_button));
            intent.putExtra("version", team.get(postion).getVersion());
            intent.putExtra("mac", team.get(postion).getMac());
        }
        startActivity(intent);
    }
}
