package com.zhang.xiaofei.smartsleep.UI.Home;

import com.google.gson.Gson;
import com.zhang.xiaofei.smartsleep.Kit.DB.YMUserInfoManager;
import com.zhang.xiaofei.smartsleep.Model.Device.DeviceModel;
import com.zhang.xiaofei.smartsleep.Model.Login.UserModel;
import com.zhang.xiaofei.smartsleep.Model.Record.RecordModel;
import com.zhang.xiaofei.smartsleep.YMApplication;

import java.io.IOException;
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

    Map<Integer, List<RecordModel>> mMap = new HashMap<Integer, List<RecordModel>>();

    // 向服务器上报睡眠数据
    public void uploadSleepData() {
        System.out.println("将本地数据上报服务器端");
        new Thread(new Runnable() {
            @Override
            public void run() {
                readDataFromDB();
            }
        });
    }

    private class ReportItem {
        String userId;
        String serial;
        String sleepDataTime;
        String data;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getSerial() {
            return serial;
        }

        public void setSerial(String serial) {
            this.serial = serial;
        }

        public String getSleepDataTime() {
            return sleepDataTime;
        }

        public void setSleepDataTime(String sleepDataTime) {
            this.sleepDataTime = sleepDataTime;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

    // 上报至服务器端
    private void uploadCloud(String serial, String dateTime, String data, int deviceId) {
        YMUserInfoManager userInfoManager = new YMUserInfoManager(YMApplication.getContext());
        UserModel model = userInfoManager.loadUserInfo();
        if (model == null) {
            return;
        }
        OkHttpClient okHttpClient  = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        ReportItem item = new ReportItem();
        item.setUserId(item.getUserId());
        item.setSerial(serial);
        item.setSleepDataTime(dateTime);
        item.setData(data);
        Gson gson = new Gson();
        String json = gson.toJson(item);

        //MediaType  设置Content-Type 标头中包含的媒体类型值
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
                , json);
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
                    updateDataFromDB(serial, dateTime, deviceId);
                }
            }
        });
    }

    // 从数据库中读取数据
    private void readDataFromDB() {
        Realm mRealm = Realm.getDefaultInstance();
        YMUserInfoManager userInfoManager = new YMUserInfoManager(YMApplication.getContext());
        UserModel userModel = userInfoManager.loadUserInfo();
        int userId = userModel.getUserInfo().getUserId();
        RealmResults<DeviceModel> deviceList = mRealm.where(DeviceModel.class).equalTo("userId", userId).equalTo("deviceType", 1).findAll();
        if (deviceList.size() <= 0) {
            return;
        }
        int deviceId = deviceList.get(0).getId();
        String serial = deviceList.get(0).getDeviceSerial();
        RealmResults<RecordModel> list = mRealm.where(RecordModel.class)
                .equalTo("isSyncCloud", false)
                .equalTo("deviceId", deviceId)
                .findAll().sort("time", Sort.ASCENDING);
        for (RecordModel model: list) {
            if (mMap.containsKey((Integer) (model.getTime() / 60))) {
                List<RecordModel> temlist = mMap.get((Integer) (model.getTime() / 60));
                temlist.add(model);
                mMap.put((Integer) (model.getTime() / 60), temlist);
            } else {
                List<RecordModel> temlist = new ArrayList<RecordModel>();
                temlist.add(model);
                mMap.put((Integer) (model.getTime() / 60), temlist);
            }
        }
        if (mMap.size() > 0) {
            System.out.println("将本地数据上报服务器端: " + mMap.size());
            for (Map.Entry<Integer, List<RecordModel>> entry: mMap.entrySet()) {
                Gson gson = new Gson();
                String json = gson.toJson(entry.getValue());
                uploadCloud(serial, dateTimeIntToString(entry.getKey()), json, deviceId);
            }
        }
        downloadSleepDataFromCloud(serial); // 将该需要的数据下载下来
    }

    private void updateDataFromDB(String serial, String dateTime, int deviceId) {
        Realm mRealm = Realm.getDefaultInstance();
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<RecordModel> list = mRealm.where(RecordModel.class)
                        .greaterThan("time", timeStrToLong(dateTime))
                        .lessThan("time", timeStrToLong(dateTime) + 24 * 60 * 60)
                        .findAll();
                if (list.size() > 0) {
                    for (RecordModel model: list) {
                        model.setSyncCloud(true);
                    }
                }
            }
        });
    }

    private String dateTimeIntToString(int dateTime)  {
        String date = currentDate((long)dateTime * 60 * 1000);
        if (date.length() == 10) {
            return date;
        }
        return "";
    }

    private String currentDate(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(time);
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

    class SleepItem {
        String userId;
        String serial;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getSerial() {
            return serial;
        }

        public void setSerial(String serial) {
            this.serial = serial;
        }
    }


    // 下载服务器端数据
    private void downloadSleepDataFromCloud(String serial) {
        YMUserInfoManager userInfoManager = new YMUserInfoManager(YMApplication.getContext());
        UserModel model = userInfoManager.loadUserInfo();
        if (model == null) {
            return;
        }
        OkHttpClient okHttpClient  = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        SleepItem item = new SleepItem();
        item.setUserId(item.getUserId());
        item.setSerial(serial);
        Gson gson = new Gson();
        String json = gson.toJson(item);

        //MediaType  设置Content-Type 标头中包含的媒体类型值
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
                , json);
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

                }
            }
        });
    }
}
