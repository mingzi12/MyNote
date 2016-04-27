package com.mingzi.onenote.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mingzi.onenote.R;
import com.mingzi.onenote.vo.PreferenceInfo;


public class OneNotePreferenceActivity extends PreferenceActivity {

    private ListPreference mThemeList;
    private Preference mUserSafety;
    private Preference mAboutApp;
    private Preference mUnLock;
    private LayoutInflater mInflater;
    private LinearLayout mLinearLayout1;
    private LinearLayout mLinearLayout2;
    private LinearLayout mLinearLayout3;
    private EditText mNewPassEdit;
    private EditText mNewPassAgainEdit;
    private EditText mOldPassEdit;
    private EditText mModifyPassEdit;
    private EditText mModifyPassAgainEdit;
    private EditText mUnLockEdit;
    private AlertDialog.Builder mNewPassBuilder;
    private AlertDialog.Builder mModifyBuilder;
    private AlertDialog.Builder mUnLockBuilder;
    private Dialog mDialog1, mDialog2;
    private Dialog mDialog3;
    private PreferenceInfo mPreferenceInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mPreferenceInfo = PreferenceInfo.getPreferenceInfo(this);
        addPreferencesFromResource(R.xml.preference);
        mThemeList = (ListPreference) findPreference("themelist");
        mThemeList.setSummary(mPreferenceInfo.themeListValue);
        mThemeList.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // TODO Auto-generated method stub
                String value = (String) newValue;
                mThemeList.setSummary(value);
                mPreferenceInfo.setThemeListValue(value);
                return true;
            }
        });

        mAboutApp = findPreference("aboutapp");
        mAboutApp.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                // TODO Auto-generated method stub
                AlertDialog ad = new AlertDialog.Builder(OneNotePreferenceActivity.this).create();
                ad.setTitle("OneNote");
                ad.setIcon(R.mipmap.icon);
                ad.setMessage("Author: MingZi      2016.3.14");
                ad.setCanceledOnTouchOutside(true);
                ad.show();
                return false;
            }
        });

        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLinearLayout1 = (LinearLayout) mInflater.inflate(R.layout.newkey, null);
        mLinearLayout2 = (LinearLayout) mInflater.inflate(R.layout.modifykey, null);
        mLinearLayout3 = (LinearLayout) mInflater.inflate(R.layout.unlock, null);
        mNewPassEdit = (EditText) mLinearLayout1.findViewById(R.id.newkeytext);
        mNewPassAgainEdit = (EditText) mLinearLayout1.findViewById(R.id.newkeyagaintext);

        mOldPassEdit = (EditText) mLinearLayout2.findViewById(R.id.oldkeytext);
        mModifyPassEdit = (EditText) mLinearLayout2.findViewById(R.id.modifykeytext);
        mModifyPassAgainEdit = (EditText) mLinearLayout2.findViewById(R.id.modifykeyagaintext);
        mUnLockEdit = (EditText) mLinearLayout3.findViewById(R.id.old_key);
        mNewPassBuilder = new Builder(OneNotePreferenceActivity.this);
        mNewPassBuilder.setView(mLinearLayout1);
        mNewPassBuilder.setTitle("设置新密码");
        mNewPassBuilder.setIcon(R.drawable.preferences_security_light);
        mNewPassBuilder.setPositiveButton("确定", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String pass = mNewPassEdit.getText().toString();
                String passAgain = mNewPassAgainEdit.getText().toString();
                int len = pass.length();
                if (len < 4 || len > 7) {
                    Toast.makeText(OneNotePreferenceActivity.this, "密码长度必须为4~6位", Toast.LENGTH_SHORT).show();
                } else {
                    if (pass.equals("")) {
                        Toast.makeText(OneNotePreferenceActivity.this, "密码不能为空", Toast.LENGTH_LONG).show();
                    } else if (pass.equals(passAgain)) {
                        mPreferenceInfo.setUserPassword(pass);
                        mUserSafety.setTitle("修改密码");
                    } else {
                        Toast.makeText(OneNotePreferenceActivity.this, "两次输入不正确", Toast.LENGTH_LONG).show();
                    }
                }


                dialog.dismiss();
                clearText();
            }
        });
        mNewPassBuilder.setNegativeButton("取消", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                clearText();
            }
        });
        mDialog1 = mNewPassBuilder.create();

        mModifyBuilder = new Builder(OneNotePreferenceActivity.this);
        mModifyBuilder.setView(mLinearLayout2);
        mModifyBuilder.setTitle("修改密码");
        mModifyBuilder.setIcon(R.drawable.preferences_security_light);
        mModifyBuilder.setPositiveButton("确定", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String oldPass = mOldPassEdit.getText().toString();
                String pass = mModifyPassEdit.getText().toString();
                String passAgain = mModifyPassAgainEdit.getText().toString();
                int len = pass.length();
                if (!oldPass.equals(mPreferenceInfo.userPasswordValue)) {
                    Toast.makeText(OneNotePreferenceActivity.this, "旧密码错误", Toast.LENGTH_LONG).show();
                } else if (pass.equals("")) {
                    Toast.makeText(OneNotePreferenceActivity.this, "新密码不能为空", Toast.LENGTH_LONG).show();
                } else if (pass.equals(passAgain)) {
                    if (len < 4 || len > 7) {
                        Toast.makeText(OneNotePreferenceActivity.this, "密码长度必须为4~位", Toast.LENGTH_SHORT).show();
                    } else {
                        mPreferenceInfo.setUserPassword(pass);
                    }


                } else {
                    Toast.makeText(OneNotePreferenceActivity.this, "两次输入不正确", Toast.LENGTH_LONG).show();
                }

                dialog.dismiss();
                clearText();
            }
        });
        mModifyBuilder.setNegativeButton("取消", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                clearText();
            }
        });
        mDialog2 = mModifyBuilder.create();

        mUnLockBuilder= new Builder(OneNotePreferenceActivity.this);
        mUnLockBuilder.setTitle("取消加锁")
                .setView(mLinearLayout3)
                .setPositiveButton("确定", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pass = mUnLockEdit.getText().toString();
                        String oldPass = mPreferenceInfo.userPasswordValue;
                        if (pass.equals(oldPass)) {
                            mPreferenceInfo.unLockApp(false);
                            Toast.makeText(OneNotePreferenceActivity.this,"已取消加锁",Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(OneNotePreferenceActivity.this,"密码错误",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("取消", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        mDialog3 = mUnLockBuilder.create();

        mUserSafety =  findPreference("usersafety");
        if (mPreferenceInfo.userPasswordValue.equals("")) {
            mUserSafety.setTitle("设置新密码");
        } else {
            mUserSafety.setTitle("修改密码");
        }
        mUserSafety.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (mPreferenceInfo.userPasswordValue.equals("")) {
                    mDialog1.show();
                } else {
                    mDialog2.show();
                }

                return false;
            }
        });
    mUnLock = findPreference("unLock");
        mUnLock.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                    mDialog3.show();
                return false;
            }
        });
    }




    /**
     * 清空EditText
     */
    private void clearText() {
        mNewPassEdit.setText("");
        mNewPassAgainEdit.setText("");
        mNewPassEdit.requestFocus();

        mOldPassEdit.setText("");
        mModifyPassEdit.setText("");
        mModifyPassAgainEdit.setText("");
        mOldPassEdit.requestFocus();
    }

}
