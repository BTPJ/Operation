package com.cyjt.operation.base;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesTools {
	/** Http的sessionId关键字 */
	public static String SESSION = "session";
	public static String LOGIN_SUCCEED_USER = "LOGIN_SUCCEED_USER";
	public static String IPS = "IP_LISTS";
	private Context context;

	public SharedPreferencesTools(Context context) {
		this.context = context;
	}

	/**
	 * 读取自定义的整型参数
	 * 
	 * @param perferencesName表名
	 * <BR>
	 *            使用SharedPreferencesTools提供的内表面
	 * @param key键
	 * @return返回值不存在返回-1
	 */
	public int readIntPreferences(String perferencesName, String key) {
		SharedPreferences preferences = context.getSharedPreferences(
				perferencesName, Context.MODE_PRIVATE);
		int result = preferences.getInt(key, -1);
		return result;
	}

	/**
	 * 读取自定义的String参数
	 * 
	 * @param perferencesName表名
	 * <BR>
	 *            使用SharedPreferencesTools提供的内表面
	 * @param key键
	 * @return返回值不存在返回""
	 */
	public String readStringPreferences(String perferencesName, String key) {
		SharedPreferences preferences = context.getSharedPreferences(
				perferencesName, Context.MODE_PRIVATE);
		String result = preferences.getString(key, "");
		return result;
	}

	/**
	 * 读取自定义的Longg参数
	 * 
	 * @param perferencesName表名
	 * <BR>
	 *            使用SharedPreferencesTools提供的内表面
	 * @param key键
	 * @return返回值不存在返回""
	 */
	public Long readLongPreferences(String perferencesName, String key) {
		SharedPreferences preferences = context.getSharedPreferences(
				perferencesName, Context.MODE_PRIVATE);
		Long result = preferences.getLong(key, -1l);
		return result;
	}

	/**
	 * 保存整型参数
	 * 
	 * @param perferencesName表名
	 * <BR>
	 *            使用SharedPreferencesTools提供的内表面
	 * @param key键
	 * @param value值
	 */
	public void writeIntPreferences(String perferencesName, String key,
			int value) {
		SharedPreferences preferences = context.getSharedPreferences(
				perferencesName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	/**
	 * 保存String参数
	 * 
	 * @param perferencesName表名
	 * <BR>
	 *            使用SharedPreferencesTools提供的内表面
	 * @param key键
	 * @param value值
	 */
	public void writeStringPreferences(String perferencesName, String key,
			String value) {
		SharedPreferences preferences = context.getSharedPreferences(
				perferencesName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * 保存Long参数
	 * 
	 * @param perferencesName表名
	 * <BR>
	 *            使用SharedPreferencesTools提供的内表面
	 * @param key键
	 * @param value值
	 */
	public void writeLongPreferences(String perferencesName, String key,
			Long value) {
		SharedPreferences preferences = context.getSharedPreferences(
				perferencesName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	/**
	 * 清除保存的 值
	 * 
	 * @param perferencesName表名
	 * <BR>
	 *            使用SharedPreferencesTools提供的内表面
	 * @param key键
	 */
	public void clearPreferences(String perferencesName, String key) {
		SharedPreferences preferences = context.getSharedPreferences(
				perferencesName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.remove(key);
		editor.commit();
	}
}
