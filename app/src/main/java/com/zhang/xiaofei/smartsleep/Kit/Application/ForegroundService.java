package com.zhang.xiaofei.smartsleep.Kit.Application;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Home.HomeActivity;

public class ForegroundService extends Service {

    private static final String TAG = ForegroundService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");

        String CHANNEL_ONE_ID = "com.zhang.xiaofei.smartsleep";
        String CHANNEL_ONE_NAME= "com.zhang.xiaofei.smartsleep";
        NotificationChannel notificationChannel= null;
//进行8.0的判断
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel= new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.baidu.com"));
            PendingIntent pendingIntent= PendingIntent.getActivity(this, 0, intent, 0);
            Notification notification= new Notification.Builder(this).setChannelId(CHANNEL_ONE_ID)
                    .setTicker("Nature")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("")
                    .setContentIntent(pendingIntent)
                    .build();
            notification.flags|= Notification.FLAG_NO_CLEAR;
            startForeground(1, notification);
        } else {
            Notification.Builder builder = new Notification.Builder
                    (this.getApplicationContext()); //获取一个Notification构造器
            Intent nfIntent = new Intent(this, HomeActivity.class);

            builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                            R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
                    .setContentTitle("") // 设置下拉列表里的标题
                    .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                    .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

            Notification notification = builder.build(); // 获取构建好的Notification
            notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
            startForeground(110, notification);
        }

        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }
}
