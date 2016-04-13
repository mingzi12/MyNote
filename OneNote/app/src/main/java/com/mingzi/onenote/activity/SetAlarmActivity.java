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
import android.view.SubMenu;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SetAlarmActivity extends Activity
{
    private static final String TAG = "SetAlarm";
    public static final String SETTING_ALARM = "settingNote";

    private Button mSetAlarmBtn;									// 申明设置时钟按钮
    private ToggleButton mEnableAlarmBtn;						// 申明开启\关闭按钮
    private ToggleButton mAlarmStyleBtn;
    private SharedPreferences mPreferences;
    SharedPreferences.Editor mEditor;
    private static boolean alarmStyle = true;			// 闹钟提示方式 (true:铃声;false:振动)
    Calendar c = Calendar.getInstance();
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
    static SetAlarmActivity instance;
    static String shakeSenseValue;

    private Note mNote;
    public static void setAlarmStyle(boolean style)
    {
        alarmStyle = style;
    }

    public static boolean getAlarmStyle()
    {
        return alarmStyle;
    }

    private void loadData()
    {
        mPreferences = getSharedPreferences("oneNote", MODE_PRIVATE);
        mEditor = mPreferences.edit();
        mSetAlarmBtn.setText(mPreferences.getString("time",
                sdf.format(new Date(c.getTimeInMillis()))));
        mEnableAlarmBtn.setChecked(mPreferences.getBoolean("on_off", false));
    }

    private void saveData()
    {
        mEditor.putString("time", mSetAlarmBtn.getText().toString());
        mEditor.putBoolean("on_off", mEnableAlarmBtn.isChecked());
        mEditor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getBundleExtra(SETTING_ALARM);
        mNote = bundle.getParcelable(SETTING_ALARM);
        instance = this;										// 用于在ShakeAlarm窗口中关闭此activity
        shakeSenseValue = getResources().getString(R.string.shakeSenseValue_2);
        ButtonListener buttonListener = new ButtonListener();	// 注册设置时间按钮监听事件
        mSetAlarmBtn = (Button) findViewById(R.id.btn_setClock);
        mSetAlarmBtn.setOnClickListener(buttonListener);
        mEnableAlarmBtn = (ToggleButton) findViewById(R.id.btn_enClk); // 注册开启关闭按钮监听事件
        mEnableAlarmBtn.setOnClickListener(buttonListener);
        loadData();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        saveData();
    }

    class ButtonListener implements OnClickListener
    {
        private TimePicker timePicker;			// 申明时间控件
        private DatePicker mDatePicker;
        private PendingIntent mPendingIntent;
        private Intent intent;
        AlarmManager alarmManager;
        LayoutInflater inflater;
        LinearLayout setAlarmLayout;

        /**
         * 在ButtonListener构造方法中加载对话框的布局
         */
        public ButtonListener()
        {
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);		// 用于加载alertdialog布局
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            setAlarmLayout = (LinearLayout) inflater.inflate(R.layout.alarm_dialog, null);
        }

        private void enableClk()
        {
            timePicker = (TimePicker) setAlarmLayout.findViewById(R.id.timepicker);
            mDatePicker = (DatePicker) setAlarmLayout.findViewById(R.id.datepicker);
            c.set(Calendar.YEAR,mDatePicker.getYear());
            c.set(Calendar.MONTH,mDatePicker.getMonth());
            c.set(Calendar.DAY_OF_MONTH,mDatePicker.getDayOfMonth());
            c.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());        // 设置闹钟小时数
            c.set(Calendar.MINUTE, timePicker.getCurrentMinute());            // 设置闹钟的分钟数
            c.set(Calendar.SECOND, 0); // 设置闹钟的秒数
            c.set(Calendar.MILLISECOND, 0); // 设置闹钟的毫秒数
            mSetAlarmBtn.setText(sdf.format(new Date(c.getTimeInMillis())));
            mEditor.putString("time", sdf.format(new Date(c.getTimeInMillis())));
            mEditor.commit();
            Bundle lBundle = new Bundle();
            lBundle.putParcelable(AlarmReceiver.RECEIVE_BUNDLE, mNote);
            intent = new Intent();    // 创建Intent对象
            intent.setClass(SetAlarmActivity.this,AlarmReceiver.class);
            intent.setAction(sdf.format(new Date(c.getTimeInMillis())));
            intent.putExtra(AlarmReceiver.RECEIVE_BUNDLE,lBundle);
            mPendingIntent = PendingIntent.getBroadcast(SetAlarmActivity.this, 0, intent, 0);    // 创建PendingIntent
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,    // 设置闹钟，当前时间就唤醒
                    c.getTimeInMillis(), 24 * 60 * 60 * 1000, mPendingIntent);

        }

        private void disableClk()
        {
            intent = new Intent(SetAlarmActivity.this,AlarmReceiver.class);
            intent.setAction(mPreferences.getString("time", null));
            Log.d(TAG, "disableClk: " + mPreferences.getString("time", null));
            mPendingIntent = PendingIntent.getBroadcast(SetAlarmActivity.this, 0, intent, 0);
            alarmManager.cancel(mPendingIntent);
            Toast.makeText(SetAlarmActivity.this,"闹钟取消成功",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onClick(View v)
        {

            switch (v.getId())
            {
                case R.id.btn_setClock:

                    setAlarmLayout = (LinearLayout) inflater.inflate(R.layout.alarm_dialog, null);
                    mAlarmStyleBtn = (ToggleButton) setAlarmLayout.findViewById(R.id.togbtn_alarm_style);
                    mAlarmStyleBtn.setChecked(mPreferences.getBoolean("style", false));
                    timePicker = (TimePicker) setAlarmLayout.findViewById(R.id.timepicker);
                    timePicker.setIs24HourView(true);
                    new AlertDialog.Builder(SetAlarmActivity.this)
                            .setView(setAlarmLayout)
                            .setTitle("设置闹钟时间")
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            enableClk();
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
                        Log.d(TAG, "onClick: "+mEnableAlarmBtn.isChecked());
                        disableClk();
                    }
                       else {
                        mEnableAlarmBtn.setChecked(false);
                        Toast.makeText(SetAlarmActivity.this,"闹钟已过期，请重新设置！",Toast.LENGTH_LONG).show();
                    }
                        break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.setting_alarm_menu, menu);
        SubMenu subMenu = menu.addSubMenu("摇晃灵敏度");
        subMenu.add(1, 1, 1, "温柔甩");
        subMenu.add(1, 2, 2, "正常甩");
        subMenu.add(1, 3, 3, "暴力甩");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case 1:
                shakeSenseValue = getResources().getString(
                        R.string.shakeSenseValue_1);
                Toast.makeText(this, "温柔甩设置成功", Toast.LENGTH_SHORT).show();
                break;

            case 2:
                shakeSenseValue = getResources().getString(
                        R.string.shakeSenseValue_2);
                Toast.makeText(this, "正常甩设置成功", Toast.LENGTH_SHORT).show();
                break;

            case 3:
                shakeSenseValue = getResources().getString(
                        R.string.shakeSenseValue_3);
                Toast.makeText(this, "暴力甩设置成功", Toast.LENGTH_SHORT).show();
                break;

            case R.id.about_alarm:
                new AlertDialog.Builder(this).setTitle("关于").setMessage("摇摇乐v1.5")
                        .setNegativeButton("确定", null).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}