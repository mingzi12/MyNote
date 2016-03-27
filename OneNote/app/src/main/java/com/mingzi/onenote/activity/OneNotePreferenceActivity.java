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
    private ListPreference themeList;
    private Preference usersafety, aboutapp;
    private LayoutInflater inflater;
    private LinearLayout linearlayout_1, linearlayout_2;
    private EditText newkeytext, newkeyagaintext;
    private EditText oldkeytext, modifykeytext, modifykeyagaintext;
    private AlertDialog.Builder builder_1, builder_2;
    private Dialog dialog_1, dialog_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        themeList = (ListPreference) findPreference("themelist");
        themeList.setSummary(PreferenceInfo.themeListValue);
        themeList.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // TODO Auto-generated method stub
                String value = (String) newValue;
                themeList.setSummary(value);
                PreferenceInfo.setThemeListValue(value);
                return true;
            }
        });

        aboutapp = findPreference("aboutapp");
        aboutapp.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                // TODO Auto-generated method stub
                AlertDialog ad = new AlertDialog.Builder(OneNotePreferenceActivity.this).create();
                ad.setTitle("NotePad");
                ad.setIcon(R.mipmap.icon);
                ad.setMessage("Author: Mingzi      2016.3.14");
                ad.setCanceledOnTouchOutside(true);
                ad.show();
                return false;
            }
        });

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        linearlayout_1 = (LinearLayout) inflater.inflate(R.layout.newkey, null);
        linearlayout_2 = (LinearLayout) inflater.inflate(R.layout.modifykey, null);

        newkeytext = (EditText) linearlayout_1.findViewById(R.id.newkeytext);
        newkeyagaintext = (EditText) linearlayout_1.findViewById(R.id.newkeyagaintext);

        oldkeytext = (EditText) linearlayout_2.findViewById(R.id.oldkeytext);
        modifykeytext = (EditText) linearlayout_2.findViewById(R.id.modifykeytext);
        modifykeyagaintext = (EditText) linearlayout_2.findViewById(R.id.modifykeyagaintext);

        builder_1 = new Builder(OneNotePreferenceActivity.this);
        builder_1.setView(linearlayout_1);
        builder_1.setTitle("设置新密码");
        builder_1.setIcon(R.drawable.lock_light);
        builder_1.setPositiveButton("确定", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String key = newkeytext.getText().toString();
                String keyagain = newkeyagaintext.getText().toString();

                if (key.equals("")) {
                    Toast.makeText(OneNotePreferenceActivity.this, "密码不能为空", Toast.LENGTH_LONG).show();
                } else if (key.equals(keyagain)) {
                    PreferenceInfo.setUserPassword(key);
                    usersafety.setTitle("修改密码");
                } else {
                    Toast.makeText(OneNotePreferenceActivity.this, "两次输入不正确", Toast.LENGTH_LONG).show();
                }

                dialog.dismiss();
                clearText();
            }
        });
        builder_1.setNegativeButton("取消", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                clearText();
            }
        });
        dialog_1 = builder_1.create();

        builder_2 = new Builder(OneNotePreferenceActivity.this);
        builder_2.setView(linearlayout_2);
        builder_2.setTitle("修改密码");
        builder_2.setIcon(R.drawable.lock_light);
        builder_2.setPositiveButton("确定", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String oldkey = oldkeytext.getText().toString();
                String key = modifykeytext.getText().toString();
                String keyagain = modifykeyagaintext.getText().toString();

                if (!oldkey.equals(PreferenceInfo.userPasswordValue)) {
                    Toast.makeText(OneNotePreferenceActivity.this, "密码错误", Toast.LENGTH_LONG).show();
                } else if (key.equals("")) {
                    Toast.makeText(OneNotePreferenceActivity.this, "新密码不能为空", Toast.LENGTH_LONG).show();
                } else if (key.equals(keyagain)) {
                    PreferenceInfo.setUserPassword(key);
                } else {
                    Toast.makeText(OneNotePreferenceActivity.this, "两次输入不正确", Toast.LENGTH_LONG).show();
                }

                dialog.dismiss();
                clearText();
            }
        });
        builder_2.setNegativeButton("取消", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                clearText();
            }
        });
        dialog_2 = builder_2.create();

        usersafety =  findPreference("usersafety");
        if (PreferenceInfo.userPasswordValue.equals("")) {
            usersafety.setTitle("设置新密码");
        } else {
            usersafety.setTitle("修改密码");
        }
        usersafety.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (PreferenceInfo.userPasswordValue.equals("")) {
                    dialog_1.show();
                }
                else {
                    dialog_2.show();
                }

                return false;
            }
        });

    }

    /**
     * 清空EditText
     */
    private void clearText() {
        newkeytext.setText("");
        newkeyagaintext.setText("");
        newkeytext.requestFocus();

        oldkeytext.setText("");
        modifykeytext.setText("");
        modifykeyagaintext.setText("");
        oldkeytext.requestFocus();
    }

}
