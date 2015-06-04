package com.cyjt.operation.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cyjt.operation.base.AppContext;

/**
 * ���ύ����,ȡ��������json��Ӧ
 * 
 * @author jacarri at 2014��5��1��
 */
public class JsonFormRequest extends JsonObjectRequest {

	private final Map<String, String> _params;

	/*
	 * 
	 * ���߷�������Ҫ�ӱ�body��ȡ���ύ������<br>
	 */
	public String getBodyContentType() {
		return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
	}

	/**
	 * @param method
	 * @param url
	 * @param params
	 *            ��������Ϊnull
	 * @param jsonRequest
	 * @param listener
	 * @param errorListener
	 */
	public JsonFormRequest(int method, String url, Map<String, String> params, Listener<JSONObject> listener,
			ErrorListener errorListener) {
		super(method, url, null, listener, errorListener);
		Log.v("volley", "�����ַ��"+url+"\n");
		if(params!=null){
			Log.v("volley","���������"+params.toString());
			
		}
		_params = params;
	}

	/**
	 * Converts <code>params</code> into an application/x-www-form-urlencoded
	 * encoded string.
	 */
	private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
		StringBuilder encodedParams = new StringBuilder();
		try {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
				encodedParams.append('=');
				encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
				encodedParams.append('&');
			}
			return encodedParams.toString().getBytes(paramsEncoding);
		} catch (UnsupportedEncodingException uee) {
			throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
		}
	}

	/**
	 * Returns the raw POST or PUT body to be sent.
	 */
	public byte[] getBody() {
		Map<String, String> params = getParams();
		if (params != null && params.size() > 0) {
			return encodeParameters(params, getParamsEncoding());
		}
		return null;
	}

	@Override
	protected Map<String, String> getParams() {
		return _params;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.android.volley.toolbox.StringRequest#parseNetworkResponse(com.android
	 * .volley.NetworkResponse)
	 */
	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		// since we don't know which of the two underlying network vehicles
		// will Volley use, we have to handle and store session cookies manually
		AppContext.getInstance().checkSessionCookie(response.headers);
		return super.parseNetworkResponse(response);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.android.volley.Request#getHeaders()
	 */
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		//����response.statusCodeΪ��ֵ������������ᱻִ�У�
		Map<String, String> headers = super.getHeaders();
		if (headers == null || headers.equals(Collections.emptyMap())) {
			headers = new HashMap<String, String>();
		}
		AppContext.getInstance().addSessionCookie(headers);
		return headers;
	}
}