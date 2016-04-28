package com.mingzi.onenote.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.mingzi.onenote.R;
import com.mingzi.onenote.receiver.AlarmReceiver;
import com.mingzi.onenote.vo.Note;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SetAlarmActivity extends Activity {

    private static final String TAG = "SetAlarmActivity";
    public static final String SETTING_ALARM = "settingNote";
    private  String ALARMtIME = "alarmTime";

    private Button mSetAlarmBtn;                                    // 申明设置时钟按钮
    private ToggleButton mEnableAlarmBtn;                        // 申明开启\关闭按钮
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    Calendar mCalendar = Calendar.getInstance();
    final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
    static SetAlarmActivity sInstance;
    private Note mNote;
    //用于选择铃声后作相应的判断标记
    private static final int REQUEST_CODE_PICK_RINGTONE = 1;
    //保存铃声的Uri的字符串形式
    private String mRingtoneUri = null;

    private void loadData() {
        mPreferences = getSharedPreferences("oneNote", MODE_PRIVATE);
        mEditor = mPreferences.edit();
        mSetAlarmBtn.setText(mPreferences.getString(ALARMtIME, mDateFormat.format(new Date(mCalendar.getTimeInMillis()))));
        Log.d(TAG, "loadData: "+ALARMtIME);
        mEnableAlarmBtn.setChecked(mPreferences.getBoolean("on_off", false));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getBundleExtra(SETTING_ALARM);
        mNote = bundle.getParcelable(SETTING_ALARM);
        Log.d(TAG, "onCreate: "+mNote.getNoteId());
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
            mEditor.putBoolean("on_off",true);
            mEditor.commit();
            Log.d(TAG, "enableAlarm: " + mPreferences.getBoolean("style", false));
            Bundle lBundle = new Bundle();
            lBundle.putParcelable(AlarmReceiver.RECEIVE_BUNDLE, mNote);
            mIntent = new Intent();    // 创建Intent对象
            mIntent.setClass(SetAlarmActivity.this, AlarmReceiver.class);
            mIntent.setAction(mDateFormat.format(new Date(calendar.getTimeInMillis())));
            mIntent.putExtra(AlarmReceiver.RECEIVE_BUNDLE, lBundle);
            mPendingIntent = PendingIntent.getBroadcast(SetAlarmActivity.this, 0, mIntent, 0);    // 创建PendingIntent
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), mPendingIntent);// 设置闹钟，当前时间就唤醒

        }

        private void disableAlarm() {
            mIntent = new Intent(SetAlarmActivity.this, AlarmReceiver.class);
            mIntent.setAction(mPreferences.getString(ALARMtIME, null));
            mPendingIntent = PendingIntent.getBroadcast(SetAlarmActivity.this, 0, mIntent, 0);
            mAlarmManager.cancel(mPendingIntent);
            mEditor.putBoolean("on_off",false);
            mEditor.commit();
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
                    Log.d(TAG, "onClick:origin : "+mPreferences.getBoolean("style", false));
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
                                            mEnableAlarmBtn.setChecked(true);
                                            Toast.makeText(SetAlarmActivity.this, "闹钟设置成功", Toast.LENGTH_LONG).show();// 提示用户
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
        SubMenu subMenu = menu.addSubMenu("  提醒方式");
        subMenu.setIcon(R.drawable.ic_menu_way_light);
        subMenu.addSubMenu(1,1,1,"震动");
        subMenu.addSubMenu(1,2,2,"铃声");
        subMenu.addSubMenu(1,3,3,"铃声和震动");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                mEditor.putString("styleToRemain",getResources().getString(R.string.vibration));
                mEditor.commit();
                break;
            case 2:
                mEditor.putString("styleToRemain",getResources().getString(R.string.ringTone));
                mEditor.commit();
                break;
            case 3:
                mEditor.putString("styleToRemain",getResources().getString(R.string.ringToneAndVibration));
                mEditor.commit();
                break;
            case R.id.setting_ringTone:
                doPickRingtone();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 显示ActionBar中每个子项的图标
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }


    private void doPickRingtone() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        // Allow user to pick 'Default'
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        // Show only ringtones
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
        // Don't show 'Silent'
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);

        Uri ringtoneUri;
        if (mRingtoneUri != null) {
            ringtoneUri = Uri.parse(mRingtoneUri);
        } else {
            // Otherwise pick default ringtone Uri so that something is
            // selected.
            ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }

        // Put checkmark next to the current ringtone for this contact
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtoneUri);

        // Launch!
        // startActivityForResult(intent, REQUEST_CODE_PICK_RINGTONE);
        startActivityForResult(intent, REQUEST_CODE_PICK_RINGTONE);
    }

    private void handleRingtonePicked(Uri pickedUri) {
        if (pickedUri != null) {
            mRingtoneUri = pickedUri.toString();
        } else {
            mRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(SetAlarmActivity.this,
                    RingtoneManager.TYPE_RINGTONE).toString();
        }
        // get ringtone name and you can save mRingtoneUri for database.
        if (mRingtoneUri != null) {
            mEditor.putString("ringTone", mRingtoneUri);
            Log.d(TAG, "handleRingtonePicked: "+mRingtoneUri);
            mEditor.commit();
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_PICK_RINGTONE: {
                Uri pickedUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                handleRingtonePicked(pickedUri);
                break;
            }
        }
    }
}