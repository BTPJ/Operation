package com.cyjt.operation.http;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.cyjt.operation.R;
import com.cyjt.operation.base.AppContext;

public class VolleyErrorHelper {
	/**
	 * Returns appropriate message which is to be displayed to the user against
	 * the specified error object.
	 * 
	 * @param error
	 * @param context
	 * @return
	 */
	public static String getMessage(VolleyError error, Context context) {
		String response = "";
		if (null != error && null != error.networkResponse) {
			if (error.networkResponse.statusCode == HttpStatus.SC_BAD_REQUEST) {
				try {
					response = new String(error.networkResponse.data,
							HTTP.UTF_8);
					JSONObject oo = new JSONObject(response);
					String msg = oo.getString("msg");
					// 客户端目前不显示超过20个字符的错误信息
					if (null != msg && msg.length() <= 50) {
						return msg;
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		if (error instanceof TimeoutError) {
			return context.getResources().getString(
					R.string.generic_server_down);
		} else if (isServerProblem(error)) {
			return handleServerError(error, context);
		} else if (isNetworkProblem(error)) {
			return context.getResources().getString(R.string.no_internet);
		} else if (error instanceof AuthFailureError) {
			return context.getResources().getString(R.string.auth_failure);
		}
		return context.getResources().getString(R.string.generic_error);
	}

	/**
	 * Determines whether the error is related to network
	 * 
	 * @param error
	 * @return
	 */
	private static boolean isNetworkProblem(Object error) {
		return (error instanceof NetworkError)
				|| (error instanceof NoConnectionError);
	}

	/**
	 * Determines whether the error is related to server
	 * 
	 * @param error
	 * @return
	 */
	private static boolean isServerProblem(Object error) {
		return (error instanceof ServerError)
				|| (error instanceof AuthFailureError);
	}

	/**
	 * Handles the server error, tries to determine whether to show a stock
	 * message or to show a message retrieved from the server.
	 * 
	 * @param err
	 * @param context
	 * @return
	 */
	private static String handleServerError(Object err, Context context) {
		VolleyError error = (VolleyError) err;

		NetworkResponse response = error.networkResponse;
		if (response != null) {
			switch (response.statusCode) {
			case 404:
				return context.getResources().getString(R.string.http_404);
			case 422:
			case 401:
				// 如果401则应用要重新跳回到登录界面
				AppContext.getInstance().checkResponseStatusCode(
						response.statusCode);
			case 500:
				byte[] htmlBodyBytes = error.networkResponse.data;
				Log.e("volley", new String(htmlBodyBytes), error);
			default:
				return context.getResources().getString(
						R.string.generic_server_down);
			}
		}
		return context.getResources().getString(R.string.generic_error);
	}
}
