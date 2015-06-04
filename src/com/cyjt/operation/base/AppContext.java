package com.cyjt.operation.base;

import java.util.Map;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.cyjt.operation.bean.User;
import com.cyjt.operation.http.VolleyTools;
import com.cyjt.operation.ui.LoginActivity;

/**
 * 应用基类
 * 
 * @author kullo<BR>
 *         2014-5-5 上午10:38:25<BR>
 */
public class AppContext extends Application {
	/** AppContext的单例对象 */
	private static AppContext instance;
	/** 本地存储 */
	private SharedPreferencesTools sharedPreferencesTools;
	/** 全局单例的volley工具 */
	private VolleyTools volleyTools;
	/** 全局单例的施工员 */
	private User user;

	/*
	 * 返回AppContext的单例对象
	 */
	public static AppContext getInstance() {
		return instance;
	}

	/** 返回SharedPreferencesTools实例 */
	public SharedPreferencesTools getSharedPreferencesTools() {
		return sharedPreferencesTools;
	}

	/** 返回网络通信工具的单例 */
	public VolleyTools getVolleyTools() {
		return volleyTools;
	}

	/** 获得登录者 */
	public User getUser() {
		return user;
	}

	/** 创建登录者 */
	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		// 1.在这里对地图进行初始化
		SDKInitializer.initialize(instance.getApplicationContext());
		sharedPreferencesTools = new SharedPreferencesTools(instance);
		volleyTools = new VolleyTools();

	}

	private static final String SET_COOKIE_KEY = "Set-Cookie";
	private static final String COOKIE_KEY = "Cookie";
	private static final String SESSION_COOKIE = "sessionid";

	public final void checkResponseStatusCode(int statusCode) {
		// 如果ResponseStatus为401则表示cookie信息无效了（session超时或者服务器重启了），需要重新登录
		if (statusCode != 401)
			return;
		Intent intent = new Intent(instance, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 先移除页面全部的activity
		// AppManager.getAppManager().finishAllActivity();
		// 启用登录页面
		instance.startActivity(intent);
		Toast.makeText(instance, "请重新登录", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 如果cookie存在就检查并保存cookie
	 * 
	 * @param headers
	 *            Response Headers.
	 */
	public final void checkSessionCookie(Map<String, String> headers) {
		// Set-Cookie: JSESSIONID=gt9s1imtguco40cp68yiyj9g;Path=/parkitf
		if (headers.containsKey(SET_COOKIE_KEY)) {
			String cookies = headers.get(SET_COOKIE_KEY);// JSESSIONID=gt9s1imtguco40cp68yiyj9g;Path=/parkitf
			if (cookies.length() > 0) {
				sharedPreferencesTools
						.writeStringPreferences(SharedPreferencesTools.SESSION,
								SESSION_COOKIE, cookies);
			}
		}
	}

	/**
	 * 登录前一定要把以前的cookie给清理掉
	 */
	public final void clearSessionCookie() {
		sharedPreferencesTools.clearPreferences(SharedPreferencesTools.SESSION,
				SET_COOKIE_KEY);
	}

	/**
	 * Adds session cookie to headers if exists.<br>
	 * 如果带上已经挂掉的服务器发来的cooike,是没有用的.所以在登录前一定要清掉已经存在的cookie
	 * 
	 * @param headers
	 */
	public final void addSessionCookie(Map<String, String> headers) {
		String sessionId = sharedPreferencesTools.readStringPreferences(
				SharedPreferencesTools.SESSION, SESSION_COOKIE);
		if (sessionId.length() > 0) {
			StringBuilder builder = new StringBuilder();
			builder.append(sessionId);
			if (headers.containsKey(COOKIE_KEY)) {
				builder.append("; ");
				builder.append(headers.get(COOKIE_KEY));
			}
			headers.put(COOKIE_KEY, builder.toString());
		} else {
		}
	}
}
