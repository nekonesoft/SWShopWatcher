package com.sw.nekonesoft.swshopwatcher;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ButtonRectangle mStartButton, mStopButton, mStartSWButton, mGotoSWButton;
    private ButtonRectangle mSetButton, mFirstSetButton;
    private TextView mTimerText;
    private EditText mIntervalMinutesText, mIntervalSecondsText;
    private EditText mFirstMinutesText, mFirstSecondsText;

    private Timer mTimer;
    private CountDownTimerTask mTimerTask = null;
    private Handler mHandler = new Handler();
    private long mIntervalTime = 60 * 60;
    private long mFirstTime = 60 * 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 画面の向きを縦に固定
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // DrawerToggle
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // NavigationView Listener
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        int color = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);

        mStartButton = (ButtonRectangle) findViewById(R.id.start_button);
        mStopButton = (ButtonRectangle) findViewById(R.id.stop_button);
        mStartSWButton = (ButtonRectangle) findViewById(R.id.start_sw_button);
        mGotoSWButton = (ButtonRectangle) findViewById(R.id.goto_sw_button);
        mSetButton = (ButtonRectangle) findViewById(R.id.set_button);
        mFirstSetButton = (ButtonRectangle) findViewById(R.id.first_set_button);
        mStartButton.setBackgroundColor(color);
        mStopButton.setBackgroundColor(color);
        mStartSWButton.setBackgroundColor(color);
        mGotoSWButton.setBackgroundColor(color);
        mSetButton.setBackgroundColor(color);
        mFirstSetButton.setBackgroundColor(color);

        mTimerText = (TextView) findViewById(R.id.timer);
        mTimerText.setTextColor(color);

        mIntervalMinutesText = (EditText) findViewById(R.id.interval_minutes);
        mIntervalSecondsText = (EditText) findViewById(R.id.interval_seconds);
        mFirstMinutesText = (EditText) findViewById(R.id.first_minutes);
        mFirstSecondsText = (EditText) findViewById(R.id.first_seconds);

        // インターバル値をロード
        mIntervalTime = loadIntervalTime();
        // ファースト値をロード
        mFirstTime = loadFirstTime();
        // カウンターリセット
        resetTimerText();

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditTextからフォーカスを外す
                mTimerText.requestFocus();
                // カウンターリセット
                resetTimerText();
                // アラームスタート
                startAlarm();
                // タイマースタート
                startTimer();
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditTextからフォーカスを外す
                mTimerText.requestFocus();
                // カウンターリセット
                resetTimerText();
                // アラーム破棄
                cancelAlarm();
                // タイマー破棄
                cancelTimer();
            }
        });

        mStartSWButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditTextからフォーカスを外す
                mTimerText.requestFocus();
                // カウンターリセット
                resetTimerText();
                // アラームスタート
                startAlarm();
                // タイマースタート
                startTimer();

                // サマナーズウォー起動
                PackageManager pManager = getPackageManager();
                Intent intent = pManager.getLaunchIntentForPackage(DEF.PACKAGE_NAME_SW);
                startActivity(intent);
            }
        });

        mGotoSWButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditTextからフォーカスを外す
                mTimerText.requestFocus();

                // サマナーズウォー起動
                PackageManager pManager = getPackageManager();
                Intent intent = pManager.getLaunchIntentForPackage(DEF.PACKAGE_NAME_SW);
                startActivity(intent);
            }
        });

        mSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditTextからフォーカスを外す
                mTimerText.requestFocus();
                // アラーム破棄
                cancelAlarm();
                // タイマー破棄
                cancelTimer();
                // インターバル値をセット
                int intervalMinutes = Integer.parseInt(mIntervalMinutesText.getText().toString());
                int intervalSeconds = Integer.parseInt(mIntervalSecondsText.getText().toString());
                DEF.savePreferenceInt(getApplicationContext(), "IntervalMinutes", intervalMinutes);
                DEF.savePreferenceInt(getApplicationContext(), "IntervalSeconds", intervalSeconds);
                // ファーストフラグオフ
                DEF.savePreferenceBoolean(getApplicationContext(), "IsFirstTime", false);
                // インターバル値をロード
                mIntervalTime = loadIntervalTime();
                // カウンターリセット
                resetTimerText();
            }
        });

        mFirstSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditTextからフォーカスを外す
                mTimerText.requestFocus();
                // アラーム破棄
                cancelAlarm();
                // タイマー破棄
                cancelTimer();
                // ファースト値をセット
                int FirstMinutes = Integer.parseInt(mFirstMinutesText.getText().toString());
                int FirstSeconds = Integer.parseInt(mFirstSecondsText.getText().toString());
                DEF.savePreferenceInt(getApplicationContext(), "FirstMinutes", FirstMinutes);
                DEF.savePreferenceInt(getApplicationContext(), "FirstSeconds", FirstSeconds);
                // ファーストフラグオン
                DEF.savePreferenceBoolean(getApplicationContext(), "IsFirstTime", true);
                // ファースト値をロード
                mFirstTime = loadFirstTime();
                // カウンターリセット
                resetTimerText();
            }
        });
    }

    private long loadIntervalTime() {
        // インターバル値をロード
        int intervalMinutes = DEF.loadPreferenceInt(getApplicationContext(), "IntervalMinutes", 60);
        int intervalSeconds = DEF.loadPreferenceInt(getApplicationContext(), "IntervalSeconds", 0);
        mIntervalMinutesText.setText(String.format("%02d", intervalMinutes));
        mIntervalSecondsText.setText(String.format("%02d", intervalSeconds));
        mIntervalMinutesText.setSelection(2);
        mIntervalSecondsText.setSelection(2);
        // インターバル値(秒)
        return (intervalMinutes * 60 + intervalSeconds);
    }

    private long loadFirstTime() {
        // ファースト値をロード
        int firstMinutes = DEF.loadPreferenceInt(getApplicationContext(), "FirstMinutes", 60);
        int firstSeconds = DEF.loadPreferenceInt(getApplicationContext(), "FirstSeconds", 0);
        mFirstMinutesText.setText(String.format("%02d", firstMinutes));
        mFirstSecondsText.setText(String.format("%02d", firstSeconds));
        mFirstMinutesText.setSelection(2);
        mFirstSecondsText.setSelection(2);
        // ファースト値(秒)
        return (firstMinutes * 60 + firstSeconds);
    }

    private void resetTimerText() {
        // カウンターリセット
        if (DEF.loadPreferenceBoolean(getApplicationContext(), "IsFirstTime", false) == true)
            setTimerText(mFirstTime);
        else
            setTimerText(mIntervalTime);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (DEF.loadPreferenceBoolean(getApplicationContext(), "AlarmWaiting", false) == false) {
            // カウンターリセット
            if (DEF.loadPreferenceBoolean(getApplicationContext(), "IsFirstTime", false) == true)
                setTimerText(mFirstTime);
            else
                setTimerText(mIntervalTime);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (DEF.loadPreferenceBoolean(getApplicationContext(), "AlarmWaiting", false) == true) {
            // タイマースタート
            startTimer();
        } else {
            // タイマー破棄
            cancelTimer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // タイマー破棄
        cancelTimer();
    }

    // アラームスタート
    private void startAlarm() {
        // 時間をセットする
        Calendar calendar = Calendar.getInstance();
        // Calendarを使って現在の時間をミリ秒で取得
        long startTimeMillis = System.currentTimeMillis();
        calendar.setTimeInMillis(startTimeMillis);
        // アラーム時間
        if (DEF.loadPreferenceBoolean(getApplicationContext(), "IsFirstTime", false) == true)
            // mFirstTime秒後に設定
            calendar.add(Calendar.SECOND, (int) mFirstTime);
        else
            // mIntervalTime秒後に設定
            calendar.add(Calendar.SECOND, (int) mIntervalTime);

        Intent intent = new Intent(getApplicationContext(), AlarmBroadcastReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

        // アラームをセットする
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending);

        DEF.savePreferenceLong(getApplicationContext(), "StartTimeMillis", startTimeMillis);
        DEF.savePreferenceBoolean(getApplicationContext(), "AlarmWaiting", true);

        //Toast.makeText(getApplicationContext(), "Set Alarm", Toast.LENGTH_SHORT).show();
    }

    // アラーム破棄
    private void cancelAlarm() {
        if (DEF.loadPreferenceBoolean(getApplicationContext(), "AlarmWaiting", false) == true) {

            Intent indent = new Intent(getApplicationContext(), AlarmBroadcastReceiver.class);
            PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, indent, 0);

            // アラームを解除する
            AlarmManager am = (AlarmManager) MainActivity.this.getSystemService(ALARM_SERVICE);
            am.cancel(pending);

            DEF.savePreferenceBoolean(getApplicationContext(), "AlarmWaiting", false);

            //Toast.makeText(getApplicationContext(), "Cancel Alarm", Toast.LENGTH_SHORT).show();
        }
    }

    // タイマースタート
    private void startTimer() {
        // タイマー破棄
        cancelTimer();

        if (DEF.loadPreferenceBoolean(getApplicationContext(), "AlarmWaiting", false) == true) {
            // Timer インスタンスを生成
            mTimer = new Timer();
            // TimerTask インスタンスを生成
            mTimerTask = new CountDownTimerTask();

            long startTimeMillis = DEF.loadPreferenceLong(getApplicationContext(), "StartTimeMillis", 0);
            long offsetTime = (1500 - ((System.currentTimeMillis() - startTimeMillis) % 1000)) % 1000;

            // スケジュールを設定 1sec = 1000msec
            // public void scheduleAtFixedRate (TimerTask task, long delay, long period)
            mTimer.scheduleAtFixedRate(mTimerTask, offsetTime, 1000);

            //Log.d(DEF.TAG, "startTimer : " + String.valueOf(offsetTime));
        }
    }

    // タイマー破棄
    private void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    // タイマー表示セット
    private void setTimerText(long count) {
        long mm = count / 60;
        long ss = count % 60;
        // 桁数を合わせるために02d(2桁)を設定
        mTimerText.setText(String.format("%1$02d : %2$02d", mm, ss));
    }

    class CountDownTimerTask extends TimerTask {
        @Override
        public void run() {
            // handlerを使って処理をキューイングする
            mHandler.post(new Runnable() {
                public void run() {
                    // カウントダウン
                    if (DEF.loadPreferenceBoolean(getApplicationContext(), "AlarmWaiting", false) == true) {
                        long startTimeMillis = DEF.loadPreferenceLong(getApplicationContext(), "StartTimeMillis", 0);
                        // ファーストか？
                        long count;
                        if (DEF.loadPreferenceBoolean(getApplicationContext(), "IsFirstTime", false) == true)
                            count = mFirstTime - ((System.currentTimeMillis() - startTimeMillis) / 1000);
                        else
                            count = mIntervalTime - ((System.currentTimeMillis() - startTimeMillis) / 1000);
                        //Log.d(DEF.TAG, "CountDownTimerTask : " + String.valueOf(count));
                        setTimerText(count);
                        if (count <= 0) {
                            // タイマー破棄
                            cancelTimer();
                            // カウンター表示を 00:00 にする
                            setTimerText(0);

                            //String str = "Count End";
                            //Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.menu_drawer_settings:
                // 設定
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, DEF.REQUEST_SETTING);
                //Log.d(DEF.TAG, "onNavigationItemSelected : " + "Settings Selected!");
                break;
            case R.id.menu_drawer_finish:
                // 終了
                Snackbar.make(findViewById(R.id.activity_main), getString(R.string.menu_finish_confirm), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.menu_finish_yes), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        })
                        //.setActionTextColor(getResources().getColor(R.color.corporate_identity))
                        .setDuration(Snackbar.LENGTH_LONG)
                        .show();
                //Log.d(DEF.TAG, "onNavigationItemSelected : " + "Finish Selected!");
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: {
                // 設定
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent, DEF.REQUEST_SETTING);
                return true;
            }
            case R.id.action_finish: {
                // 終了
                Snackbar.make(findViewById(R.id.activity_main), getString(R.string.menu_finish_confirm), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.menu_finish_yes), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        })
                        //.setActionTextColor(getResources().getColor(R.color.corporate_identity))
                        .setDuration(Snackbar.LENGTH_LONG)
                        .show();
                return true;
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        requestCode &= 0xffff;
        if (requestCode == DEF.REQUEST_SETTING) {
            //if (resultCode == RESULT_OK) {
            //}
        }
    }

}