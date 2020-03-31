package com.zhang.xiaofei.smartsleep.Model.Device;

import android.content.Context;
import android.content.Intent;

import com.ansen.http.net.HTTPCaller;
import com.ansen.http.net.NameValuePair;
import com.ansen.http.net.RequestDataCallback;
import com.zhang.xiaofei.smartsleep.Kit.DB.YMUserInfoManager;
import com.zhang.xiaofei.smartsleep.Model.Login.UserModel;
import com.zhang.xiaofei.smartsleep.UI.Home.HomeActivity;
import com.zhang.xiaofei.smartsleep.UI.Login.LoginActivity;
import com.zhang.xiaofei.smartsleep.UI.Login.StartPageActivity;
import com.zhang.xiaofei.smartsleep.YMApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.realm.Realm;
import io.realm.RealmResults;

import static io.realm.Sort.ASCENDING;
import static io.realm.Sort.DESCENDING;
import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

public class DeviceManager {

    public static DeviceManager getInstance() {
        return ObserverManagerHolder.sObserverManager;
    }

    private static class ObserverManagerHolder {
        private static final DeviceManager sObserverManager = new DeviceManager();
    }

    private Lock lock = new ReentrantLock();// 锁对象
    public List<DeviceModel> deviceList = new ArrayList<DeviceModel>();
    public int currentDevice = 0;
    public int connectedCurrentDevice = 0;
    public int scaningCurrentDevice = 0;
    public Context context;

    public void readDB() {
        deviceList.clear();
        YMUserInfoManager userManager = new YMUserInfoManager( context);
        UserModel userModel = userManager.loadUserInfo();
        if (userModel == null) {
            return;
        }
        Realm mRealm = Realm.getDefaultInstance();
        RealmResults<DeviceModel> userList = mRealm.where(DeviceModel.class)
                .equalTo("userId", userModel.getUserInfo().getUserId())
                .sort("bindTime", ASCENDING)
                .findAll();
        if (userList != null && userList.size() > 0) {
            for (DeviceModel model: userList) {
                deviceList.add(model);
            }
            if (deviceList.size() > 1) {
                Collections.reverse(deviceList);
            }
        }
    }

    public void downloadDeviceList() {
        YMUserInfoManager userManager = new YMUserInfoManager( context);
        UserModel userModel = userManager.loadUserInfo();
        if (userModel == null) {
            return;
        }
        com.ansen.http.net.Header header = new com.ansen.http.net.Header("Content-Type", "application/x-www-form-urlencoded");
        com.ansen.http.net.Header headerToken = new com.ansen.http.net.Header("token", userModel.getToken());
        List<NameValuePair> postParam = new ArrayList<>();
        postParam.add(new NameValuePair("userId", "" + userModel.getUserInfo().getUserId()));

        HTTPCaller.getInstance().post(
                DeviceListModel.class,
                YMApplication.getInstance().domain() + "app/deviceManage/list?userId=" + userModel.getUserInfo().getUserId(),
                new com.ansen.http.net.Header[]{header, headerToken},
                postParam,
                requestDataCallback
        );
    }

    private RequestDataCallback requestDataCallback = new RequestDataCallback<DeviceListModel>() {
        @Override
        public void dataCallback(int status, DeviceListModel model) {

            System.out.println("网络请求返回的Status:" + status);
            if(model == null || model.getCode() != 200){
                if (model != null) { // 会提示用户没有绑定设备
                    //Toast.makeText(DeviceManageActivity.this, model.getMsg(), Toast.LENGTH_SHORT).show();
                }
                if (model.getCode() == 401) {
                    Intent it = new Intent(getApplicationContext(), LoginActivity.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(it);
                }
            }else{
                if (model.getData() != null && model.getData().length > 0) {
                    saveDeviceListToDB(model.getData());
                }
            }

        }

        @Override
        public void dataCallback(DeviceListModel obj) {

        }
    };

    // 将设备信息保存到数据库中
    private void saveDeviceListToDB(DeviceModel[] models) {
        lock.lock();
        if (models.length > 0) {
            int count = 0;
            for (int i = 0; i < models.length; i++) {
                boolean value = false;
                for (DeviceModel model: deviceList) {
                    if (model.getMac().equals(models[i].getMac())) {
                        value = true;
                        break;
                    }
                }
                if (value == false) {
                    count += 1;
                    addDeviceToDB(models[i]);
                    deviceList.add(models[i]);
                }
            }
            if (count > 0) {
                ((HomeActivity)context).refreshTab1();
            }
        }
        lock.unlock();
    }

    private void addDeviceToDB(DeviceModel deviceModel) {
        Realm mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DeviceModel model = realm.createObject(DeviceModel.class);
                model.setUserId(deviceModel.getUserId());
                model.setMac(deviceModel.getMac());
                model.setDeviceType(deviceModel.getDeviceType());
                model.setBindTime(deviceModel.getBindTime());
                model.setDeviceSerial(deviceModel.getDeviceSerial());
                model.setVersion(deviceModel.getVersion());
                model.setId(deviceModel.getId());
                model.setUpToCloud(true);
            }
        });
    }
}
