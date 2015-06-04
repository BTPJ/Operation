package com.cyjt.operation.base;

import android.graphics.Color;

public class Constants {
	// 使用ISO-8601 时间格式与接口交换时间
	public final static String ISO8601DateFormatShort = "yyyy-MM-dd HH:mm:ss";
	/**
	 * 界面上只显示小时和分钟
	 */
	public final static String ThumbDateFormatShort = "HH:mm";
	public final static String[] LOCATION_ARRAY = { "点击选取标识", "A", "B", "C",
			"D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
			"Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
	public final static String[] TYPE_ARRAY = { "不修改", "动态交通基站", "静态交通基站" };
	public final static String[] SRC_ARRAY = { "不修改", "基站配时", "区域服务器配时",
			"中央监控服务器配时" };
	public final static String[] SENSOR_SHPEAR = { "不修改", "1","2","3","4","5","6","7","8"};
	public final static String[] SENSOR_SHPEAR_LEVEL = { "不修改", "初级","次级"};
	public final static String[] SENSOR_TIME_BEATCOMMAND = { "不修改", "10分钟","30分钟","1小时"};
	/** javaBean的类型代码，用于数据传递时做分辨用 */
	public final static int BEAN_TYPE_BASESTATION = 1;
	public final static int BEAN_TYPE_PARKINGLOT = 2;
	public final static int BEAN_TYPE_SENSOR = 3;
	public final static int BEAN_TYPE_LANEARRAY = 4;
	// ===============
	// ===============
	/** 默认 */
	public final static int LANEARRAY_STATUS_DEF = 0;
	public final static String LANEARRAY_STATUS_DEF_STRING = "未知";
	public final static int LANEARRAY_STATUS_DEF_COLOR = Color.argb(255, 188,
			183, 181);
	/** 畅通 */
	public final static int LANEARRAY_STATUS_GREEN = 1;
	public final static String LANEARRAY_STATUS_GREEN_STRING = "畅通";
	public final static int LANEARRAY_STATUS_GREEN_COLOR = Color.argb(255, 73,
			113, 69);
	/** 缓行 */
	public final static int LANEARRAY_STATUS_YELLOW = 3;
	public final static String LANEARRAY_STATUS_YELLOW_STRING = "缓行";
	public final static int LANEARRAY_STATUS_YELLOW_COLOR = Color.argb(255,
			196, 120, 13);
	/** 阻塞 */
	public final static int LANEARRAY_STATUS_RED = 5;
	public final static String LANEARRAY_STATUS_RED_STRING = "阻塞";
	public final static int LANEARRAY_STATUS_RED_COLOR = Color.argb(255, 200,
			37, 54);

	public final static int[] SWIPE_REFRESH_COLORS = {
			android.R.color.holo_blue_bright, android.R.color.holo_green_light,
			android.R.color.holo_orange_light, android.R.color.holo_red_light };

	/** 基站类型----静态部署基站 */
	public final static int BASESTATION_TYPE_STATIC_CODE = 1;
	/** 基站类型----动态部署基站 */
	public final static int BASESTATION_TYPE_DYNAMIC_CODE = 2;

}
