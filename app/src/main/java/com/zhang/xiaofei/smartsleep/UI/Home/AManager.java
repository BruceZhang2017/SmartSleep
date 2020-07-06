package com.zhang.xiaofei.smartsleep.UI.Home;

import android.app.Notification;
import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.zhang.xiaofei.smartsleep.R;

import static com.zhang.xiaofei.smartsleep.YMApplication.CHANNEL_ID_1;
import static com.zhang.xiaofei.smartsleep.YMApplication.VIBRATION_PATTERN;

class AManager {
    public static void alarmGetup(Context context, int value) {
        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID_1)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getResources().getString(R.string.warning))
                .setContentText(context.getResources().getString(value == 0 ? R.string.reminder : R.string.heart_rate_100))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVibrate(VIBRATION_PATTERN)
                .setDefaults(Notification.DEFAULT_SOUND)
                .build();

        notificationManager.notify(1, notification);

    }
}
