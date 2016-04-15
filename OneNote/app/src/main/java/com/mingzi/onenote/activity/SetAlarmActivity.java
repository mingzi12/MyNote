package com.mingzi.onenote.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.mingzi.onenote.R;
import com.mingzi.onenote.receiver.AlarmReceiver;
import com.mingzi.onenote.vo.Note;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SetAlarmActivity extends Activity {

    private static final String TAG = "SetAlarm";
    public static final String SETTING_ALARM = "settingNote";
    private  String ALARMtIME = "alarmTime";

    private Button mSetAlarmBtn;                                    // 申明设置时钟按钮
    private ToggleButton mEnableAlarmBtn;                        // 申明开启\关闭按钮
    private ToggleButton mAlarmStyleBtn;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private static boolean sAlarmStyle = true;            // 闹钟提示方式 (true:铃声;false:振动)
    Calendar mCalendar = Calendar.getInstance();
    final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
    static SetAlarmActivity sInstance;
    private Note mNote;

    public static void setAlarmStyle(boolean style) {
        sAlarmStyle = style;
    }

    public static boolean getAlarmStyle() {
        return sAlarmStyle;
    }

    private void loadData() {
        mPreferences = getSharedPreferences("oneNote", MODE_PRIVATE);
        mEditor = mPreferences.edit();
        mSetAlarmBtn.setText(mPreferences.getString(ALARMtIME, mDateFormat.format(new Date(mCalendar.getTimeInMillis()))));
        Log.d(TAG, "loadData: "+ALARMtIME);
        mEnableAlarmBtn.setChecked(mPreferences.getBoolean("on_off", false));
    }

   /* private void saveData() {
        mEditor.putString(ALARMtIME, mSetAlarmBtn.getText().toString());
        mEditor.putBoolean("on_off", mEnableAlarmBtn.isChecked());
        mEditor.commit();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getBundleExtra(SETTING_ALARM);
        mNote = bundle.getParcelable(SETTING_ALARM);
        ALARMtIME = ALARMtIME +mNote.getNoteId();
        sInstance = this;                                        // 用于在ShakeAlarm窗口中关闭此activity
        MyOnClickListener myOnClickListener = new MyOnClickListener();    // 注册设置时间按钮监听事件
        mSetAlarmBtn = (Button) findViewById(R.id.btn_setClock);
        mSetAlarmBtn.setOnClickListener(myOnClickListener);
        mEnableAlarmBtn = (ToggleButton) findViewById(R.id.btn_enClk); // 注册开启关闭按钮监听事件
        mEnableAlarmBtn.setOnClickListener(myOnClickListener);
        loadData();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    class MyOnClickListener implements OnClickListener {
        private TimePicker timePicker;            // 申明时间控件
        private DatePicker mDatePicker;
        private PendingIntent mPendingIntent;
        private Intent mIntent;
        AlarmManager mAlarmManager;
        LayoutInflater mInflater;
        LinearLayout mSetAlarmLayout;

        /**
         * 在ButtonListener构造方法中加载对话框的布局
         */
        public MyOnClickListener() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);        // 用于加载alertdialog布局
            mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        }

        private void enableAlarm(Calendar calendar) {
            mSetAlarmBtn.setText(mDateFormat.format(new Date(calendar.getTimeInMillis())));
            mEditor.putString(ALARMtIME, mDateFormat.format(new Date(calendar.getTimeInMillis())));
            mEditor.commit();
            Bundle lBundle = new Bundle();
            lBundle.putParcelable(AlarmReceiver.RECEIVE_BUNDLE, mNote);
            mIntent = new Intent();    // 创建Intent对象
            mIntent.setClass(SetAlarmActivity.this, AlarmReceiver.class);
            mIntent.setAction(mDateFormat.format(new Date(calendar.getTimeInMillis())));
            mIntent.putExtra(AlarmReceiver.RECEIVE_BUNDLE, lBundle);
            mPendingIntent = PendingIntent.getBroadcast(SetAlarmActivity.this, 0, mIntent, 0);    // 创建PendingIntent
            mAlarmManager.set(AlarmManager.RTC_WAKEUP,    // 设置闹钟，当前时间就唤醒
                    calendar.getTimeInMillis(), mPendingIntent);

        }

        private void disableAlarm() {
            mIntent = new Intent(SetAlarmActivity.this, AlarmReceiver.class);
            mIntent.setAction(mPreferences.getString(ALARMtIME, null));
            mPendingIntent = PendingIntent.getBroadcast(SetAlarmActivity.this, 0, mIntent, 0);
            mAlarmManager.cancel(mPendingIntent);
            Toast.makeText(SetAlarmActivity.this, "闹钟取消成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_setClock:
                    mSetAlarmLayout = (LinearLayout) mInflater.inflate(R.layout.alarm_dialog, null);
                    mDatePicker = (DatePicker) mSetAlarmLayout.findViewById(R.id.datepicker);
                    timePicker = (TimePicker) mSetAlarmLayout.findViewById(R.id.timepicker);
                    timePicker.setIs24HourView(true);
                    mAlarmStyleBtn = (ToggleButton) mSetAlarmLayout.findViewById(R.id.togbtn_alarm_style);
                    mAlarmStyleBtn.setChecked(mPreferences.getBoolean("style", false));
                    new AlertDialog.Builder(SetAlarmActivity.this)
                            .setView(mSetAlarmLayout)
                            .setTitle("设置闹钟时间")
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Calendar lCalendar = Calendar.getInstance();
                                            lCalendar.set(Calendar.YEAR, mDatePicker.getYear());
                                            lCalendar.set(Calendar.MONTH, mDatePicker.getMonth());
                                            lCalendar.set(Calendar.DAY_OF_MONTH, mDatePicker.getDayOfMonth());
                                            lCalendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());        // 设置闹钟小时数
                                            lCalendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());            // 设置闹钟的分钟数
                                            lCalendar.set(Calendar.SECOND, 0); // 设置闹钟的秒数
                                            lCalendar.set(Calendar.MILLISECOND, 0); // 设置闹钟的毫秒数
                                            enableAlarm(lCalendar);
                                            if (mAlarmStyleBtn.isChecked()) {
                                                SetAlarmActivity.setAlarmStyle(true);
                                            } else {
                                                SetAlarmActivity.setAlarmStyle(false);
                                            }

                                            mEditor.putBoolean("style", mAlarmStyleBtn.isChecked());
                                            mEnableAlarmBtn.setChecked(true);
                                            Toast.makeText(SetAlarmActivity.this, "闹钟设置成功", Toast.LENGTH_LONG)
                                                    .show();// 提示用户
                                        }
                                    }).setNegativeButton("取消", null).show();
                    break;

                case R.id.btn_enClk:
                    if (!mEnableAlarmBtn.isChecked()) {
                        Log.d(TAG, "onClick: " + mEnableAlarmBtn.isChecked());
                        disableAlarm();
                    } else {
                            if (compareDate()) {
                                try {
                                    mCalendar.setTime(mDateFormat.parse(mPreferences.getString(ALARMtIME,null)));
                                    Log.d(TAG, "onClick: "+mPreferences.getString(ALARMtIME,null));
                                    enableAlarm(mCalendar);
                                    Toast.makeText(SetAlarmActivity.this,"重新开启闹钟成功！",Toast.LENGTH_LONG).show();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                mEnableAlarmBtn.setChecked(false);
                                Toast.makeText(SetAlarmActivity.this, "闹钟已过期，请重新设置！", Toast.LENGTH_LONG).show();
                            }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private boolean compareDate(){
        String dateStr = mPreferences.getString(ALARMtIME,null);
        if (dateStr !=null) {
            try {
                return mDateFormat.parse(dateStr).after(new Date());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_alarm_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_alarm:

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}