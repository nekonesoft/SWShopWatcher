package com.sw.nekonesoft.swshopwatcher;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by kishimoto on 2016/12/06.
 */

public class NotifBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        switch (action) {
            //case "click_notification":
            case "delete_notification":
                // 通知削除時のイベント
                // アラームスタート
                startAlarm(context);
                // サマナーズウォー起動
                PackageManager pManager = context.getPackageManager();
                Intent intent2 = pManager.getLaunchIntentForPackage(DEF.PACKAGE_NAME_SW);
                context.startActivity(intent2);

                //Toast.makeText(context, "Click or Delete Notification !", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
    }

    // アラームスタート
    private void startAlarm(Context context) {
        // インターバル値をロード
        int intervalMinutes = DEF.loadPreferenceInt(context, "IntervalMinutes", 60);
        int intervalSeconds = DEF.loadPreferenceInt(context, "IntervalSeconds", 0);
        // インターバル値(秒)
        int intervalTime = intervalMinutes * 60 + intervalSeconds;

        // 時間をセットする
        Calendar calendar = Calendar.getInstance();
        // Calendarを使って現在の時間をミリ秒で取得
        long startTimeMillis = System.currentTimeMillis();
        calendar.setTimeInMillis(startTimeMillis);
        // mIntervalTime秒後に設定
        calendar.add(Calendar.SECOND, intervalTime);

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent, 0);

        // アラームをセットする
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending);

        DEF.savePreferenceLong(context, "StartTimeMillis", startTimeMillis);
        DEF.savePreferenceBoolean(context, "AlarmWaiting", true);

        String str = context.getString(R.string.toast_notif_nextalarm);
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
}
