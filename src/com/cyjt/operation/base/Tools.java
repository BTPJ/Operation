package com.cyjt.operation.base;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.format.DateFormat;
import android.util.Log;

import com.cyjt.operation.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 常量类
 * 
 * @author Bert Guo 2013-1-5
 */
public class Tools {

	public static DateFormat dF = new DateFormat();

	public static CharSequence getDF(Date inDate) {
		if (inDate == null)
			return "";
		SimpleDateFormat df = getSimpleDateFormat(Constants.ISO8601DateFormatShort);
		return df.format(inDate);
	}

	/**
	 * 是否启动调试<br>
	 * 在生成Release版时，需要区分Build的类型。如果选择的是自动Build，那么BuildConfig.DEBUG仍然会被设定为true。
	 * 所以在生成Release版时 ，请按照下面这个步骤进行打包，BuildConfig.DEBUG会被修改为false：<br>
	 * 1、取消Build Automatically<br>
	 * 2、Clean（means compiling all java classes）<br>
	 * 3、Build<br>
	 * 4、Export Signed Application Package<br>
	 * 
	 */
	private static boolean isDebug = BuildConfig.DEBUG;

	/**
	 * 调试时使用的标签<br>
	 */
	private static final String TAG = "demos";

	public static boolean isDebug() {
		return isDebug;
	}

	public static void debugInfo(Object tag, Object info) {
		StackTraceElement stack[] = (new Throwable()).getStackTrace();
		int lineNumber = stack[1].getLineNumber();
		String methodName = stack[1].getMethodName();
		if (isDebug) {
			Tools.debugInfo(tag + ":" + methodName + ":" + lineNumber
					+ "====>>" + info);
		}
	}

	public static void debugInfo(Object info) {
		StackTraceElement stack[] = (new Throwable()).getStackTrace();
		StackTraceElement ste = stack[1];
		int lineNumber = ste.getLineNumber();
		String methodName = ste.getMethodName();
		String className = ste.getClassName();
		String comment = className + "-" + methodName + "-" + lineNumber;
		if (isDebug) {
			if (info != null && info.getClass().isArray()) {
				// 如果是数组的话
				Object[] objs = (Object[]) info;
				for (int i = 0; i < objs.length; i++) {
					Log.i(TAG, comment + "  " + String.valueOf(objs[i]));
				}
			} else {
				Log.i(TAG, comment + "  " + String.valueOf(info));
			}
		}
	}

	public static void debugError(String msg, Throwable tr) {
		if (isDebug) {
			Log.w(TAG, msg, tr);
		}
	}

	public static void debugError(Throwable tr) {
		if (isDebug) {
			Log.w(TAG, "错误信息", tr);
		}
	}

	public static String diffTime(Date begin, Date end) {
		long between = (end.getTime() - begin.getTime()) / 1000;// 除以1000是为了转换成秒

		long day = between / (24 * 3600);
		long hour = between % (24 * 3600) / 3600;
		long minute = between % 3600 / 60;
		long second = between % 60 / 60;
		String result = "";
		if (0 != day) {
			result = day + "天";
			return result;
		}
		if (0 != hour) {
			result = hour + "小时";
			return result;
		}
		if (0 != minute) {
			result = minute + "分";
			return result;
		}
		if (0 != second) {
			result = second + "秒";
			return result;
		}
		return result;
	}

	public static Long diffTimeBymReturnMinute(Date begin, Date end) {
		long between = (end.getTime() - begin.getTime()) / 1000;// 除以1000是为了转换成秒

		long day = between / (24 * 3600);
		long hour = between % (24 * 3600) / 3600;
		long minute = between % 3600 / 60;
		long result = 0;
		if (0 != day) {
			result = day * 24 * 60;
		}
		result = result + hour * 60 + minute;
		return result;
	}

	/**
	 * 获得带时间格式与交换时间的Gson对象
	 * 
	 * @param dateFormat
	 *            Constants中的格式常量
	 * @return
	 */
	public static Gson getGson(String dateFormat) {
		if (dateFormat == null || dateFormat == "") {
			return null;
		}
		Gson gson = new GsonBuilder().setDateFormat(dateFormat).create(); // 创建gson对象，并设置日期格式
		return gson;
	}

	/**
	 * 获取时间格式处理工具
	 * 
	 * @param dateFormat
	 *            Constants中的格式常量
	 * @return
	 */
	public static SimpleDateFormat getSimpleDateFormat(String dateFormat) {
		if (dateFormat == null || dateFormat == "") {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf;
	}

	/**
	 * 获取当前客户端版本信息
	 */
	public static int getCurrentVersion() {
		PackageInfo info;
		int curVersionCode = 0;
		try {
			info = AppContext
					.getInstance()
					.getPackageManager()
					.getPackageInfo(AppContext.getInstance().getPackageName(),
							0);
			curVersionCode = info.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return curVersionCode;
	}

}
