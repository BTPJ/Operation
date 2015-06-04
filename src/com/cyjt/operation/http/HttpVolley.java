package com.cyjt.operation.http;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Volley�ĵ���<br>
 * ����ʵ��http�������� </br> * * <B>ע��</B>:
 * ʹ�ô����һ����Ҫ��url��ַд��.volly��url��ַ��д�������ֺ�Ī�����������logcatû���κ���ʾ�����̶߳�ջ����com.android.
 * volley. Request(int method, String url, Response.ErrorListener
 * listener)��mDefaultTrafficStatsTag = TextUtils.isEmpty(url) ? 0:
 * Uri.parse(url).getHost().hashCode();�� ��Ȼ���޷�������ȥ
 * 
 * @author kullo<BR>
 *         2014-4-30 ����5:21:46<BR>
 */
public class HttpVolley {
	private static final String TAG = "MyVolley";
	/** �������� */
	private static HttpVolley instance;
	/** ����������� */
	private static RequestQueue mRequestQueue;

	private HttpVolley(Context context) {
		mRequestQueue = Volley.newRequestQueue(context);
		Log.i(TAG, "MyVolley��ʼ�����");
	}

	/**
	 * ��ʼ��Volley��ض�����ʹ��VolleyǰӦ����ɳ�ʼ��
	 * 
	 * @param context
	 */
	public static void init(Context context) {
		if (instance == null) {
			instance = new HttpVolley(context);
		}
	}

	/**
	 * �õ�������ж���
	 * 
	 * @return
	 */
	public static RequestQueue getRequestQueue() {
		throwIfNotInit();
		Log.v("demo", "�������к�mRequestQueue.getSequenceNumber()��" + mRequestQueue.getSequenceNumber()+"��");
		return mRequestQueue;
	}

	/**
	 * ��������ӵ�������
	 * 
	 * @param request
	 */
	public static void addRequest(Request<?> request) {
		getRequestQueue().add(request);
	}

	/**
	 * ����Ƿ���ɳ�ʼ��
	 */
	private static void throwIfNotInit() {
		if (instance == null) {// ��δ��ʼ��
			throw new IllegalStateException("MyVolley��δ��ʼ������ʹ��ǰӦ��ִ��init()");
		}
	}
}