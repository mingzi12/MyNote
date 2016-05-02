package com.mingzi.onenote.vo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;

import com.mingzi.onenote.values.ConstantValue;

public class PreferenceInfo {
	private static final String TAG = "PreferenceInfo";
	public static String themeListValue;
	public static int themeColorValue;
	public static String userPasswordValue;
	public static boolean ifLocked;

    private static PreferenceInfo mPreferenceInfo;
	private static Context context;
	private static SharedPreferences share;
	private static Editor editor;
	
	private PreferenceInfo(Context context) {
		super();
		PreferenceInfo.context = context.getApplicationContext();
		PreferenceInfo.share = context.getSharedPreferences("OneNote", Context.MODE_PRIVATE);
		PreferenceInfo.editor = share.edit();
		
		getThemeListValue();
		getUserPassword();
	}

    public static PreferenceInfo getPreferenceInfo(Context context) {
                if (mPreferenceInfo == null) {
                    synchronized (PreferenceInfo.class) {
                        if (mPreferenceInfo == null) {
                            mPreferenceInfo = new PreferenceInfo(context);
                        }
                    }
                }
        return mPreferenceInfo;
    }
	/**
	 * 主题颜色读写
	 */
	public  void getThemeListValue() {
		themeListValue = share.getString("themeList", "默认");
		
		if (themeListValue.equals("默认")) {
			themeColorValue = ConstantValue.THEME_WHITE;
		}
		else if (themeListValue.equals("椰壳绿")) {
			themeColorValue = ConstantValue.THEME_GREEN;
		}
		else if (themeListValue.equals("芒果")) {
			themeColorValue = ConstantValue.THEME_YELLOW;
		}
		else if (themeListValue.equals("胭脂红")) {
			themeColorValue = ConstantValue.THEME_RED;
		}
		else if (themeListValue.equals("驼绒")) {
			themeColorValue = ConstantValue.THEME_BROWN;
		}
		else if (themeListValue.equals("鲜橙")) {
			themeColorValue = ConstantValue.THEME_ORANGE;
		}
		else if (themeListValue.equals("紫丁香")) {
			themeColorValue = ConstantValue.THEME_PURPLE;
		}
	}
	
	public  void setThemeListValue(String value) {
		editor.putString("themeList", value);
		editor.commit();
		
		getThemeListValue();
	}
	
	/**
	 * 用户密码读写
	 */
	public  void getUserPassword() {
		userPasswordValue = share.getString("userPassword", "");
		ifLocked = share.getBoolean("isLock", false);
	}
	
	public  void setUserPassword(String value) {
		editor.putString("userPassword", value);
		editor.putBoolean("isLock", true);
		editor.commit();
		Toast.makeText(context, "已设置新密码", Toast.LENGTH_LONG).show();
		
		getUserPassword();
	}
	
	/**
	 * 界面刷新
	 */
	public  void dataFlush() {
		getThemeListValue();
	}

    public  void unLockApp(boolean unLock) {
        editor.putBoolean("isLock",unLock);
        editor.putString("userPassword", "");
        editor.apply();
    }
    /**
     * item显示方式
     * 0 代表 listview显示
     * 1 代表gridview显示
     * */
    public int getViewForm() {
       return share.getInt("getViewForm",0);
    }

    public void setViewForm(int form) {
        editor.putInt("getViewForm",form);
        Log.d(TAG, "setViewForm: "+form);
        editor.apply();
    }

    /**
     * 设置排序方式
     * 0 代表按更新时间升序
     * 1 代表按更新时间降序
     * 2 代表按创建时间升序
     * 3 代表按创建时间降序
     * 4 代表按内容大小升序
     * 5 代表按内容大小降序
     * */

    public void setSortForm(int sort) {
        editor.putInt("setSortForm",sort);
        editor.apply();
    }

    public int getSortForm() {
        return share.getInt("setSortForm",-1);
    }
}
