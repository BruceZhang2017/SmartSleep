package com.zhang.xiaofei.smartsleep.UI.Home;

import android.content.Intent;
import android.os.Handler;

import com.github.mikephil.charting.formatter.IFillFormatter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhang.xiaofei.smartsleep.Kit.Application.LogInterceptor;
import com.zhang.xiaofei.smartsleep.Kit.Application.SerialHandler;
import com.zhang.xiaofei.smartsleep.Kit.DB.CacheUtil;
import com.zhang.xiaofei.smartsleep.Kit.DB.YMUserInfoManager;
import com.zhang.xiaofei.smartsleep.Model.Device.DeviceModel;
import com.zhang.xiaofei.smartsleep.Model.Login.UserModel;
import com.zhang.xiaofei.smartsleep.Model.Record.RecordModel;
import com.zhang.xiaofei.smartsleep.YMApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SleepDataUploadManager {

    // 向服务器上报睡眠数据
    public void uploadSleepData(boolean bNeedDownload) {
        boolean value = CacheUtil.getInstance(YMApplication.getContext()).getBool("SyncData");
        if (!value) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("将本地数据与服务器端数据同步");
                readDataFromDB(bNeedDownload);
            }
        }).start();
    }

    // 上报至服务器端
    private void uploadCloud(String serial, String dateTime, String data, int deviceId) {
        YMUserInfoManager userInfoManager = new YMUserInfoManager(YMApplication.getContext());
        UserModel model = userInfoManager.loadUserInfo();
        if (model == null) {
            return;
        }
        OkHttpClient okHttpClient  = new OkHttpClient.Builder().addInterceptor(new LogInterceptor())
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        //MediaType  设置Content-Type 标头中包含的媒体类型值
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", model.getUserInfo().getUserId() + "")
                .add("serial", SerialHandler.handleSerial(serial))
                .add("sleepDataTime", dateTime)
                .add("data", data)
                .build();
        Request request = new Request.Builder()
                .url(YMApplication.getInstance().domain() + "app/sleep/saveSleepData")//请求的url
                .addHeader("token", model.getToken())
                .post(requestBody)
                .build();
        //创建/Call
        Call call = okHttpClient.newCall(request);
        //加入队列 异步操作
        call.enqueue(new Callback() {
            //请求错误回调方法
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("网络请求失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    System.out.println("更新数据库数据");
                    Map t = null;
                    try {
                        Gson gson = new Gson();
                        t = gson.fromJson(response.body().string(), Map.class);
                        int code = Integer.parseInt(t.get("code").toString());
                        if (code == 200) {
                            updateDataFromDB(serial, dateTime, deviceId);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // 从数据库中读取数据
    public void readDataFromDB(boolean bNeedDownload) {
        Realm mRealm = Realm.getDefaultInstance();
        YMUserInfoManager userInfoManager = new YMUserInfoManager(YMApplication.getContext());
        UserModel userModel = userInfoManager.loadUserInfo();
        if (userModel == null) {
            return;
        }
        if (userModel.getUserInfo() == null) {
            return;
        }
        int userId = userModel.getUserInfo().getUserId();
        RealmResults<DeviceModel> deviceList = mRealm.where(DeviceModel.class).equalTo("userId", userId).equalTo("deviceType", 1).findAll();
        if (deviceList.size() <= 0) {
            return;
        }
        int deviceId = deviceList.get(0).getId();
        String serial = deviceList.get(0).getDeviceSerial();
        if (bNeedDownload) {
            downloadSleepDataFromCloud(serial); // 将该需要的数据下载下来
        }
        RealmResults<RecordModel> list = mRealm.where(RecordModel.class)
                .equalTo("isSyncCloud", false)
                .equalTo("deviceId", deviceId)
                .findAll().sort("time", Sort.ASCENDING);
        System.out.println("读取到的数据总数为：" + list.size());
        if (list.size() > 0) {
            JSONObject tmpObj = null;
            JSONArray jsonArray = new JSONArray();
            for (RecordModel model: list) {
                tmpObj = new JSONObject();
                try {
                    tmpObj.put("userId" , model.getUserId());
                    tmpObj.put("deviceId" , model.getDeviceId());
                    tmpObj.put("time" , model.getTime());
                    tmpObj.put("temperature" , model.getTemperature());
                    tmpObj.put("humidity" , model.getHumidity());
                    tmpObj.put("heartRate" , model.getHeartRate());
                    tmpObj.put("breathRate" , model.getBreathRate());
                    tmpObj.put("bodyMotion" , model.getBodyMotion());
                    tmpObj.put("getupFlag" , model.getGetupFlag());
                    tmpObj.put("snore" , model.getSnore());
                    tmpObj.put("breatheStop" , model.getBreatheStop());
                    tmpObj.put("isSyncCloud" , model.isSyncCloud());
                    jsonArray.put(tmpObj);
                }catch (JSONException ex){

                }
                tmpObj = null;
            }
            if (jsonArray.length() > 0) {
                String json = jsonArray.toString();
                uploadCloud(serial, currentDate() , json, deviceId);
            }
        }
    }

    private void updateDataFromDB(String serial, String dateTime, int deviceId) {
        System.out.println("更新数据库里面的数据");
        Realm mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<RecordModel> list = mRealm.where(RecordModel.class).findAll();
                System.out.println("更新数据库里面的数据：" + list.size());
                if (list.size() > 0) {
                    for (RecordModel model: list) {
                        model.setSyncCloud(true);
                    }
                }
            }
        });
    }

    private String currentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String str = simpleDateFormat.format(date);
        return str;
    }

    public long timeStrToLong(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d;
        try {
            d = sdf.parse(time + " 00:00:00");
            long l = d.getTime() / 1000;
            return l;
        }catch (ParseException e) {
            // TODO Auto-generated catch block e.printStackTrace();
        }
        return 0;
    }


    // 下载服务器端数据
    private void downloadSleepDataFromCloud(String serial) {
        System.out.println("从服务器端下载数据");
        if (serial == null) {
            return;
        }
        YMUserInfoManager userInfoManager = new YMUserInfoManager(YMApplication.getContext());
        UserModel model = userInfoManager.loadUserInfo();
        if (model == null) {
            return;
        }
        if (model.getUserInfo() == null) {
            return;
        }
        OkHttpClient okHttpClient  = new OkHttpClient.Builder().addInterceptor(new LogInterceptor())
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        //MediaType  设置Content-Type 标头中包含的媒体类型值
        RequestBody requestBody = new FormBody.Builder().add("userId", model.getUserInfo().getUserId() + "").add("serial", SerialHandler.handleSerial(serial)).build();
        Request request = new Request.Builder()
                .url(YMApplication.getInstance().domain() + "app/sleep/listForUserSleepData")//请求的url
                .addHeader("token", model.getToken())
                .post(requestBody)
                .build();

        //创建/Call
        Call call = okHttpClient.newCall(request);
        //加入队列 异步操作
        call.enqueue(new Callback() {
            //请求错误回调方法
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("网络请求失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    Gson gson = new Gson();
                    SleepDataDownload sleepDataDownload = gson.fromJson(response.body().string(), SleepDataDownload.class);
                    Type type = new TypeToken<List<RecordModel>>() {}.getType();
                    System.out.println("将服务器端数据json转换");
                    if (sleepDataDownload.getData() != null && sleepDataDownload.getData().size() > 0) {
                        List<RecordModel> total = new ArrayList<>();
                        for (SleepDataDownload.SleepData data: sleepDataDownload.getData()) {
                            List<RecordModel> list = gson.fromJson(data.getSleepData(), type);
                            if (list != null && list.size() > 0) {
                                total.addAll(list);
                            }
                        }
                        System.out.println("将服务器端数据插入至本地数据库：" + total.size());
                        if (total.size() > 0) {
                            insertDataToDB(total); // 将服务器拉取到的数据保存至本地数据库
                        }
                    }
                } else {
                    System.out.println("从服务器下载数据失败");
                }
            }
        });
    }

    private void insertDataToDB(List<RecordModel> list) {
        List<RecordModel> newList = new ArrayList<>();
        RecordModel model = null;
        for (int i = 0; i < list.size(); i++) {
            model = list.get(i);
            model.setSyncCloud(true);
            newList.add(model);
        }
        System.out.println("将服务器端数据插入至本地数据库2：" + newList.size());
        if (newList.size() > 0) {
            Realm mRealm = Realm.getDefaultInstance();
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(newList);
                }
            });
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pushNotification();
                }
            }, 2000);//3秒后执行Runnable中的run方法
        }
    }

    Handler handler = new Handler();

    private void pushNotification() {
        SleepAndGetupTimeManager.putHashMapData();
        Intent intentBroadcast = new Intent();   //定义Intent
        intentBroadcast.setAction("Filter");
        intentBroadcast.putExtra("arg0", 17);
        YMApplication.getContext().sendBroadcast(intentBroadcast);
    }

}
