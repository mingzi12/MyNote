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
    private LayoutInflater mInflater;
    private LinearLayout mLinearlayout1;
    private LinearLayout mLinearlayout2;
    private EditText mNewPassEdit;
    private EditText mNewPassAgainEdit;
    private EditText mOldPassEdit;
    private EditText mModifyPassEdit;
    private EditText mModifyPassAgainEdit;
    private AlertDialog.Builder mNewPassBuilder;
    private AlertDialog.Builder mModifyBuilder;
    private Dialog mDialog1, mDialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        mThemeList = (ListPreference) findPreference("themelist");
        mThemeList.setSummary(PreferenceInfo.themeListValue);
        mThemeList.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // TODO Auto-generated method stub
                String value = (String) newValue;
                mThemeList.setSummary(value);
                PreferenceInfo.setThemeListValue(value);
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
        mLinearlayout1 = (LinearLayout) mInflater.inflate(R.layout.newkey, null);
        mLinearlayout2 = (LinearLayout) mInflater.inflate(R.layout.modifykey, null);

        mNewPassEdit = (EditText) mLinearlayout1.findViewById(R.id.newkeytext);
        mNewPassAgainEdit = (EditText) mLinearlayout1.findViewById(R.id.newkeyagaintext);

        mOldPassEdit = (EditText) mLinearlayout2.findViewById(R.id.oldkeytext);
        mModifyPassEdit = (EditText) mLinearlayout2.findViewById(R.id.modifykeytext);
        mModifyPassAgainEdit = (EditText) mLinearlayout2.findViewById(R.id.modifykeyagaintext);

        mNewPassBuilder = new Builder(OneNotePreferenceActivity.this);
        mNewPassBuilder.setView(mLinearlayout1);
        mNewPassBuilder.setTitle("设置新密码");
        mNewPassBuilder.setIcon(R.drawable.preferences_security_light);
        mNewPassBuilder.setPositiveButton("确定", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String pass = mNewPassEdit.getText().toString();
                String passAgain = mNewPassAgainEdit.getText().toString();

                if (pass.equals("")) {
                    Toast.makeText(OneNotePreferenceActivity.this, "密码不能为空", Toast.LENGTH_LONG).show();
                } else if (pass.equals(passAgain)) {
                    new PreferenceInfo(getApplicationContext()).setUserPassword(pass);
                    mUserSafety.setTitle("修改密码");
                } else {
                    Toast.makeText(OneNotePreferenceActivity.this, "两次输入不正确", Toast.LENGTH_LONG).show();
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
        mModifyBuilder.setView(mLinearlayout2);
        mModifyBuilder.setTitle("修改密码");
        mModifyBuilder.setIcon(R.drawable.preferences_security_light);
        mModifyBuilder.setPositiveButton("确定", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String oldPass = mOldPassEdit.getText().toString();
                String pass = mModifyPassEdit.getText().toString();
                String passAgain = mModifyPassAgainEdit.getText().toString();

                if (!oldPass.equals(PreferenceInfo.userPasswordValue)) {
                    Toast.makeText(OneNotePreferenceActivity.this, "密码错误", Toast.LENGTH_LONG).show();
                } else if (pass.equals("")) {
                    Toast.makeText(OneNotePreferenceActivity.this, "新密码不能为空", Toast.LENGTH_LONG).show();
                } else if (pass.equals(passAgain)) {
                    PreferenceInfo.setUserPassword(pass);
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

        mUserSafety =  findPreference("usersafety");
        if (PreferenceInfo.userPasswordValue.equals("")) {
            mUserSafety.setTitle("设置新密码");
        } else {
            mUserSafety.setTitle("修改密码");
        }
        mUserSafety.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (PreferenceInfo.userPasswordValue.equals("")) {
                    mDialog1.show();
                } else {
                    mDialog2.show();
                }

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
