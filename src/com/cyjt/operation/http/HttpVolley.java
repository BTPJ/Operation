package com.cyjt.operation.http;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Volley的单例<br>
 * 用于实现http请求的类库 </br> * * <B>注意</B>:
 * 使用此类库一定不要把url地址写错.volly的url地址填写错误会出现很莫名其妙的现象：logcat没有任何提示，且线程堆栈经过com.android.
 * volley. Request(int method, String url, Response.ErrorListener
 * listener)的mDefaultTrafficStatsTag = TextUtils.isEmpty(url) ? 0:
 * Uri.parse(url).getHost().hashCode();后 。然后无法跟踪下去
 * 
 * @author kullo<BR>
 *         2014-4-30 下午5:21:46<BR>
 */
public class HttpVolley {
	private static final String TAG = "MyVolley";
	/** 单例对象 */
	private static HttpVolley instance;
	/** 网络请求队列 */
	private static RequestQueue mRequestQueue;

	private HttpVolley(Context context) {
		mRequestQueue = Volley.newRequestQueue(context);
		Log.i(TAG, "MyVolley初始化完成");
	}

	/**
	 * 初始化Volley相关对象，在使用Volley前应该完成初始化
	 * 
	 * @param context
	 */
	public static void init(Context context) {
		if (instance == null) {
			instance = new HttpVolley(context);
		}
	}

	/**
	 * 得到请求队列对象
	 * 
	 * @return
	 */
	public static RequestQueue getRequestQueue() {
		throwIfNotInit();
		Log.v("demo", "网络序列号mRequestQueue.getSequenceNumber()：" + mRequestQueue.getSequenceNumber()+"个");
		return mRequestQueue;
	}

	/**
	 * 将请求添加到队列中
	 * 
	 * @param request
	 */
	public static void addRequest(Request<?> request) {
		getRequestQueue().add(request);
	}

	/**
	 * 检查是否完成初始化
	 */
	private static void throwIfNotInit() {
		if (instance == null) {// 尚未初始化
			throw new IllegalStateException("MyVolley尚未初始化，在使用前应该执行init()");
		}
	}
}