package com.cyjt.operation.base;

import android.util.Log;

/**
 * @author LTP 接口类
 */
public class URLs {
	// =================================================
	// ==================接口的URL定义
	// =================================================
	public static String defIp = AppContext.getInstance()
			.getSharedPreferencesTools()
			.readStringPreferences(SharedPreferencesTools.IPS, "defaultIp");
	/** IP地址 */
	public final static String IPADDRESS = defIp;

	/** IP地址 */
	// public final static String IPADDRESS = "221.232.148.67";// 公网IP

//	 public final static String IPADDRESS = "121.40.93.150";// 公网IP2
	// public final static String IPADDRESS = "192.168.1.27";// 陈军的ip
	/** MQTT 使用外网地址 */
	public final static String MQTT_IPADDRESS = "115.29.203.195";
	/** MQTT 使用外网端口 */
	public final static String MQTT_PORT = "1883";
	/** URL拼接 */
	public final static String HTTP_HOST = "http://" + IPADDRESS;
	public final static String HTTP_COLON = ":";
	public final static String HTTP_PORT = HTTP_COLON + "8081";// 公网端口
	// public final static String HTTP_PORT = HTTP_COLON + "8082";// 公网端口
	// public final static String HTTP_PORT = HTTP_COLON + "8080";

	// =========================静态部署2014-11-05
	// 接口整理================================================
	/** 1. 施工员登录 */
	public final static String HTTP_BUILDER_LOGIN = HTTP_HOST + HTTP_PORT
			+ "/parkitf/builder/login.json";
	/** 2. 获取施工员信息 */
	public final static String HTTP_BUILDER_FETCH_BUILDER_INFO = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/fetch-builder-info.json";
	/** 3.查询指定基站的信息 */
	public final static String HTTP_BUILDER_FETCH_BASESTATION_INFO = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/fetch-basestation-info.json";
	/** 4. 查看区域下的基站信息 */
	public final static String HTTP_BUILDER_FETCH_BASESTATION_LIST = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/fetch-area-basestation-list.json";
	/** 5.提交基站信息，用于新建基站 */
	public final static String HTTP_BUILDER_SUBMIT_BASESTATION_INFO = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/submit-basestation-info.json";
	/** 6.提交车位信息，用于新建车位、绑定车位传感器、摄像头等 */
	public final static String HTTP_BUILDER_SUBMIT_LOT_INFO = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/submit-lot-info.json";
	/** 7.删除车位 */
	public final static String HTTP_BUILDER_DELETE_LOT_INFO = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/delete-lot-info.json";
	/** 8.删除基站 */
	public final static String HTTP_BUILDER_DELETE_BASESTATION_INFO = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/delete-basestation-info.json";
	/** 9.提交位置信息 */
	public final static String HTTP_BUILDER_SUBMIT_LBS_LOG = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/submit-lbs-log.json";
	/** 10.查询区域以及街道列表 */
	public final static String BUILDER_FETCH_AREA_INFO = HTTP_HOST + HTTP_PORT
			+ "/parkitf/builder/fetch-area-info.json";
	/** 11.查询指定车位的信息 */
	public final static String HTTP_BUILDER_FETCH_PARKINGLOT_INFO = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/fetch-parkinglot-info.json";
	/** 12. 批量提交车位的信息 */
	public final static String HTTP_BUILDER_SUBMIT_PARKINGLOTS_INFO = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/submit-parkinglots-info.json";
	/** 13.获取岗位路段信息 */
	public final static String HTTP_BUILDER_FETCH_ROADS_INFO = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/fetch-roads-areas.json";
	/** 14.获取车位列表 */
	public final static String HTTP_BUILDER_FETCH_PARKINGSPACES = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/fetch-parkingspaces.json";
	/** 15.绑定节点 */
	public final static String HTTP_BUILDER_BINDING_PARKINGSPACES = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/binding-parkingspace.json";

	// ======================静态部署接口添加 ==2014-10-24 16:37=====================
	/** 13.通过基站的Code来查询基站下的车位列表 */
	public final static String HTTP_BUILDER_FETCH_BASESTATION_LOTS = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/fetch-basestation-lots.json";
	/** 14.提交映射关系 */
	public final static String HTTP_BUILDER_SUBMIT_BASESTATION_MAPPING = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/submit-basestation-mapping.json";
	/** 15.获取节点心跳值 */
	public final static String HTTP_BUILDER_FETCH_NODE_HEART_BEAT = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/fetch-node-heart-beat.json";
	/** 16.删除节点 */
	public final static String HTTP_BUILDER_DELETE_NODE_INFO = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/delete-node-info.json";
	/** 17.查询基站心跳 */
	public final static String HTTP_BUILDER_FETCH_BASESTATION_HEART_BEAT = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/fetch-basestation-heart-beat.json";
	/** 18.查询全部 */
	public final static String HTTP_BUILDER_FETCH_BASESTATIONS = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/fetch-area-basestation.json";

	// ****************************动态交通部署****************************
	// TODO =================动态部署接口===2014-10-17 10:47================

	/** 19.新增动态交通的阵列 */
	public final static String DYNAMIC_SUBMIT_LANEARRAY_INFO = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/submit-dynamic-lanearray-info.json";
	/** 20.修改动态交通的阵列 */
	public final static String DYNAMIC_UPDATE_LANEARRAY_INFO = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/update-dynamic-lanearray-info.json";
	/** 21.获取动态交通的节点阵列的列表 */
	public final static String DYNAMIC_FETCH_LANEARRAY_LIST = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/fetch-dynamic-lanearray-list.json";
	/** 22.获取动态交通的阵列(快速查询单个阵列) */
	public final static String DYNAMIC_FETCH_LANEARRAY_INFO = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/fetch-dynamic-lanearray-info.json";
	/** 23.删除动态交通的节点阵列 */
	public final static String DYNAMIC_DELETE_LANEARRAY = HTTP_HOST + HTTP_PORT
			+ "/parkitf/builder/delete-dynamic-lanearray.json";
	/** 24.在阵列中添加节点 */
	public final static String DYNAMIC_SUBMIT_SENSORS = HTTP_HOST + HTTP_PORT
			+ "/parkitf/builder/submit-dynamic-sensors.json";
	/** 25.在阵列中删除动态交通的节点 */
	public final static String DYNAMIC_DELETE_SENSOR = HTTP_HOST + HTTP_PORT
			+ "/parkitf/builder/delete-dynamic-sensor.json";
	/** 26.获取节点的地磁值 */
	public final static String DYNAMIC_FETCH_SENSOR_ZVALUES = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/fetch-dynamic-sensor-zvalues.json";
	/** 27.获取阵列中的节点 */
	public final static String DYNAMIC_FETCH_SENSOR_LIST = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/fetch-dynamic-sensor-list.json";
	/** 28.查询节点连接的运行基站 */
	public final static String DYNAMIC_FETCH_SENSOR_BOUND_BASESTATION = HTTP_HOST
			+ HTTP_PORT
			+ "/parkitf/builder/fetch-sensor-bound-basestation.json";
	/** 29.查询节点所属的阵列 */
	public final static String DYNAMIC_FETCH_SENSOR_BOUND_LANEARRAY = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/fetch-sensor-bound-array.json";
	// TODO =============动态部署新添接口===添加时间：2014-10-20 15:38=============
	/** 30.获取动态交通的节点列表信息 */
	public final static String DYNAMIC_FETCH_SENSORARRAY_INFO = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/fetch-basestation-list-sensor.json";
	/** 31.使用地图坐标查询节点阵列列表 */
	public final static String DYNAMIC_FETCH_LANEARRAY_BY_LANLON = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/fetch-lanlon-bound-array.json";
	/** 32.提交基站关系表 */
	public final static String DYNAMIC_SUBMIT_BASESTATIONS_MAP = HTTP_HOST
			+ HTTP_PORT
			+ "/parkitf/builder/submit-supportBasestation-basestation.json";
	/** 33.删除动态交通中的基站 */
	public final static String DYNAMIC_DELETE_BASESTATION = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/delete-dynamic-basestation.json";
	/** 34.获取阵列状态列表 */
	public final static String DYNAMIC_FETCH_LANEARRAY_STATUS = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/fetch-lanearray-status.json";
	/** 35.配置基站同步信息 */
	public final static String DYNAMIC_SUBMIT_BASESTATION_DATA_SYNC = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/basestation-data-sync.json";

	/** 36.获取所有当前最新上报的交通状态数据 */
	public final static String DYNAMIC_FETCH_DYNAMICARRAY_INFO = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/fetch-dynamicarray-info.json";

	/** 37.手动更改当前交通状态数据 */
	public final static String DYNAMIC_UPDATE_DYNAMICARRAY_STATUS = HTTP_HOST
			+ HTTP_PORT + "/parkitf/builder/update-dynamicarray-status.json";

	/** 38.获取最新应用信息 */
	public final static String FETCH_NEW_APP_VERSION = HTTP_HOST + HTTP_PORT
			+ "/parkitf/builder/fetch-new-app-version";

	// ================新增接口2014_12_05 14:27======
	/** 39.提交节点配置信息 */

	public final static String SUBMIT_SENSOR_SYN_INFO = HTTP_HOST + HTTP_PORT
			+ "/parkitf/builder/submit-sensor-syn-info";
	/** 40.获取基站上报的所有数据 */
	public final static String FETCH_BASESTATION_DATA = HTTP_HOST + HTTP_PORT
			+ "/parkitf/builder/fetch-basestation-data";
	// ========================新增接口2015_01_16 17:36===============
	/** 41.查询节点的配置请求 */
	public final static String FETCH_SENSOR_SET_REQUEST = HTTP_HOST + HTTP_PORT
			+ "/parkitf/builder/fetch-node-data";

	// =============================新增接口2015_02_04 16:39==============
	/** 42.节点重置请求 */
	public final static String SUBMIT_SENSOR_RESET = HTTP_HOST + HTTP_PORT
			+ "/parkitf/builder/reset-node-code";
	/** 43.获取动态节点数据心跳 */
	public final static String FETCH_DYNAMIC_NODE_DATA = HTTP_HOST + HTTP_PORT
			+ "/parkitf/builder/fetch-dynamic-node-data";
	/** 44.获取岗位路段信息 */
	public final static String FETCH_GETROAD_LIST = HTTP_HOST + HTTP_PORT
			+ "/parkitf/builder/fetch-roads-areas.json";
	/** 45.获取车位列表 */
	public final static String FETCH_GETSPACES_LIST = HTTP_HOST + HTTP_PORT
			+ "/parkitf/builder/fetch-parkingspaces.json";
	/** 46.新接口查询节点 */
	public final static String FETCH_NEWNODE_LIST = HTTP_HOST + HTTP_PORT
			+ "/parkitf/builder/fetch-newnode-data";
	/** 47.绑定节点 */
	public final static String SUBMIT_NEWBINDING_RESET = HTTP_HOST + HTTP_PORT
			+ "/parkitf/builder/binding-parkingspace.json";
	/** 48.新接口查询基站心跳 */
	public final static String FETCH_NEWHEARTBEAT_LIST = HTTP_HOST + HTTP_PORT
			+ "/parkitf/builder/fetch-basestation-newheartbeat";

	// ==============================================
	public static void changeIp(String ip) {
		defIp = ip;
		Log.v("demo", "defIp:" + defIp);
	}
}
