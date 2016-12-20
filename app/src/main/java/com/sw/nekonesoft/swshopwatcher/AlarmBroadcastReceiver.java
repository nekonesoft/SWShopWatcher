package com.sw.nekonesoft.swshopwatcher;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by kishimoto on 2016/11/29.
 */

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    // バイブレーションの時間と間隔
    public static final long VIB_TIME = 500;
    public static final long NON_VIB_TIME = 400;

    @Override
    public void onReceive(Context context, Intent intent) {
        // スクリーンオン
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                                            | PowerManager.ACQUIRE_CAUSES_WAKEUP, "My Tag");
        wl.acquire(20000);

        // アラーム作動中フラグオフ
        DEF.savePreferenceBoolean(context, "AlarmWaiting", false);
        // ファーストフラグオフ
        DEF.savePreferenceBoolean(context, "IsFirstTime", false);

        // RecieverからMainActivityを起動させる
        Intent intent2 = new Intent(context, MainActivity.class);
        //intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent2, 0);

        // 通知タップ時のPendingIntent
        //PendingIntent piClick = PendingIntent.getBroadcast(context, 0 , new Intent("click_notification"), 0);
        // 通知削除時のPendingIntent
        PendingIntent piDelete = PendingIntent.getBroadcast(context, 0 , new Intent("delete_notification"), 0);

        NotificationManager notificationManager
                = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(context.getString(R.string.notif_ticker))
                .setWhen(System.currentTimeMillis())
                .setContentTitle(context.getString(R.string.notif_title))
                .setContentText(context.getString(R.string.notif_text))
                // バイブレート、LEDで通知
                //.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                // タップするとキャンセル(消える)
                .setAutoCancel(true)
                // 通知をタップした時にMainActivityを立ち上げる
                .setContentIntent(pendingIntent)
                //.setContentIntent(piClick)
                // 通知を削除した時はブロードキャストするよう設定する
                .setDeleteIntent(piDelete)
                .build();

        // バイブレーション回数をセット
        int vibration = Integer.parseInt(DEF.loadPreferenceString(context, "SettingsVibration", "1"));
        //Toast.makeText(context, "SettingsVibration == " + String.valueOf(vibration), Toast.LENGTH_LONG).show();
        notification.vibrate = getVibratePattern(vibration);

        // 古い通知を削除
        notificationManager.cancelAll();
        // 通知
        notificationManager.notify(DEF.NOTIFICATION_ID, notification);

        // toast で受け取りを確認
        //Toast.makeText(context, "Received ", Toast.LENGTH_LONG).show();
    }

    private long[] getVibratePattern(int vibTimes) {
        switch (vibTimes) {
            case 1: {
                return new long[]{NON_VIB_TIME, VIB_TIME};
            }
            case 2: {
                return new long[]{NON_VIB_TIME, VIB_TIME, NON_VIB_TIME, VIB_TIME};
            }
            case 3: {
                return new long[]{NON_VIB_TIME, VIB_TIME, NON_VIB_TIME, VIB_TIME
                        , NON_VIB_TIME, VIB_TIME};
           }
            case 4: {
                return new long[]{NON_VIB_TIME, VIB_TIME, NON_VIB_TIME, VIB_TIME
                        , NON_VIB_TIME, VIB_TIME, NON_VIB_TIME, VIB_TIME};
            }
            default: {
                return new long[]{0, 0};
            }
        }
    }
}
