package com.cyjt.operation.base;

public class HandlerMessageCodes {
	/** 网络请求出错 */
	public static final int HTTP_VOLLEY_ERROR = 500;
	// =======施工员登录login.json==============
	/** 施工员登录成功 */
	public static final int BUILDER_LOGIN_SUCCEED = 12;
	/** 施工员登录失败 */
	public static final int BUILDER_LOGIN_FAILED = 13;

	// =======获得施工员信息fetch-builder-info==============
	/** 获得施工员信息成功 */
	public static final int FETCH_BUILDER_INFO_SUCCEED = 14;
	/** 获得施工员信息失败 */
	public static final int FETCH_BUILDER_INFO_FAILED = 15;

	// =======根据请求信息终端所在坐标，查询GPS所处范围内的基站信息基站编号、基站地址、基站所绑定的车位号==============
	/** 查询GPS所处范围内的基站信息成功 */
	public static final int FETCH_AREA_BASESTATION_SUCCEED = 16;
	/** 查询GPS所处范围内的基站信息失败 */
	public static final int FETCH_AREA_BASESTATION_FAILED = 17;

	// ===============获取正式基站下已完成绑定的车位=================
	/** 获取正式基站下已完成绑定的车位成功 */
	public static final int FETCH_BASESTATION_BOUND_LOTS_SUCCEED = 18;
	/** 获取正式基站下已完成绑定的车位失败 */
	public static final int FETCH_BASESTATION_BOUND_LOTS_FAILED = 19;
	// ===============获取部署基站下的车位=================
	/** 获取部署基站下的车位成功 */
	public static final int FETCH_BASESTATION_UNBOUND_LOTS_SUCCEED = 20;
	/** 获取部署基站下的车位失败 */
	public static final int FETCH_BASESTATION_UNBOUND_LOTS_FAILED = 21;
	// ===============获取基站下全部的车位（包括激活的和正在激活的）=================
	/** 获取基站下全部的车位（包括激活的和正在激活的）成功 */
	public static final int FETCH_BASESTATION_ALL_LOTS_SUCCEED = 22;
	/** 获取基站下全部的车位（包括激活的和正在激活的）失败 */
	public static final int FETCH_BASESTATION_ALL_LOTS_FAILED = 23;

	// =======提交基站信息，将要新增的基站信息提交给服务器存档submit-basestation-info==============
	/** 提交基站信息成功 */
	public static final int SUBMIT_BASESTATION_INFO_SUCCEED = 24;
	/** 提交基站信息失败 */
	public static final int SUBMIT_BASESTATION_INFO_FAILED = 25;

	// =======提交车位信息submit-lot-infosubmit-basestation-info==============
	/** 提交车位信息成功 */
	public static final int SUBMIT_LOT_INFO_SUCCEED = 26;
	/** 提交车位信息失败 */
	public static final int SUBMIT_LOT_INFO_FAILED = 27;

	// =======downFile==============
	/** downFile成功 */
	public static final int DOWN_FILE_SUCCEED = 30;
	/** downFile失败 */
	public static final int DOWN_FILE_FAILED = 31;

	// =======upFile==============
	/** upFile成功 */
	public static final int UP_FILE_SUCCEED = 32;
	/** upFile失败 */
	public static final int UP_FILE_FAILED = 33;

	// ===========获取通知===================
	/** 获取通知成功 */
	public static final int TOLLER_FETCH_NOTICES_SUCCEED = 38;
	/** 获取通知失败 */
	public static final int TOLLER_FETCH_NOTICES_FAILED = 39;

	// ===========提交坐标===================
	/** 提交坐标成功 */
	public static final int SUBMIT_LBS_LOG_SUCCEED = 40;
	/** 提交坐标失败 */
	public static final int SUBMIT_LBS_LOG_FAILED = 41;

	// =======获取区域列表=============
	/** 获取区域列表信息成功 */
	public static final int BUILDER_FETCH_AREA_INFO_SUCCEED = 60;
	/** 获取区域列表信息失败 */
	public static final int BUILDER_FETCH_AREA_INFO_FAILED = 61;

	// =======获取节点地磁值==============
	/** 获取节点地磁值成功 */
	public static final int HTTP_BUILDER_FETCH_SENSOR_ZVALUE_SUCCEED = 62;
	/** 获取节点地磁值失败 */
	public static final int HTTP_BUILDER_FETCH_SENSOR_ZVALUE_FAILED = 63;
	/** 获取到的节点地磁值为NULL */
	public static final int HTTP_BUILDER_FETCH_SENSOR_ZVALUE_NULL = 64;

	// =======删除车位delete-lot-status==============
	/** 删除车位成功 */
	public static final int HTTP_BUILDER_DELETE_LOT_INFO_SUCCEED = 65;
	/** 删除车位失败 */
	public static final int HTTP_BUILDER_DELETE_LOT_INFO_FAILED = 66;
	public static final int BUILDER_FETCH_STREET_INFO_SUCCEED = 67;
	public static final int BUILDER_FETCH_STREET_INFO_FAILED = 68;

	// =======根据区域、路段编号获取区域、路段下的基站列表==============
	/** 根据区域、路段编号获取区域、路段下的基站列表成功 */
	public static final int FETCH_BASESTATION_LIST_SUCCEED = 69;
	/** 根据区域、路段编号获取区域、路段下的基站列表失败 */
	public static final int FETCH_BASESTATION_LIST_FAILED = 70;

	// =======批量提交正式基站、车位、部署基站==============
	/** 批量提交正式基站、车位、部署基站成功 */
	public static final int HTTP_BUILDER_SUBMIT_PARKINGLOTS_INFO_SUCCEED = 71;
	/** 批量提交正式基站、车位、部署基站失败 */
	public static final int HTTP_BUILDER_SUBMIT_PARKINGLOTS_INFO_FAILED = 72;

	// =======删除正式基站==============
	/** 删除正式基站成功 */
	public static final int HTTP_BUILDER_DELETE_BASESTATION_INFO_SUCCEED = 73;
	/** 删除正式基站失败 */
	public static final int HTTP_BUILDER_DELETE_BASESTATION_INFO_FAILED = 74;

	// =======查询指定基站的信息==============
	/** 查询指定基站的信息成功 */
	public static final int HTTP_BUILDER_FETCH_BASESTATION_INFO_SUCCEED = 75;
	/** 查询指定基站的信息失败 */
	public static final int HTTP_BUILDER_FETCH_BASESTATION_INFO_FAILED = 76;

	// =======查询指定车位的信息==============
	/** 查询指定车位的信息成功 */
	public static final int HTTP_BUILDER_FETCH_PARKINGLOT_INFO_SUCCEED = 77;
	/** 查询指定车位的信息失败 */
	public static final int HTTP_BUILDER_FETCH_PARKINGLOT_INFO_FAILED = 78;

	// ======================静态部署接口添加 ==2014-10-24 16:37=====================
	// =======查询指定车位的信息==============
	/** 查询基站下的车位列表成功 */
	public static final int HTTP_BUILDER_FETCH_BASESTATION_LOTS_SUCCEED = 79;
	/** 查询基站下的车位列表失败 */
	public static final int HTTP_BUILDER_FETCH_BASESTATION_LOTS_FAILED = 80;
	// =======提交映射关系==============
	/** 提交映射关系成功 */
	public static final int HTTP_BUILDER_SUBMIT_BASESTATION_MAPPING_SUCCEED = 81;
	/** 提交映射关系失败 */
	public static final int HTTP_BUILDER_SUBMIT_BASESTATION_MAPPING_FAILED = 82;
	// =======获取节点心跳==============
	/** 获取节点心跳成功 */
	public static final int HTTP_BUILDER_FETCH_NODE_HEART_BEAT_SUCCEED = 83;
	/** 获取节点心跳失败 */
	public static final int HTTP_BUILDER_FETCH_NODE_HEART_BEAT_FAILED = 84;
	// =======删除节点==============
	/** 删除节点成功 */
	public static final int HTTP_BUILDER_DELETE_NODE_INFO_SUCCEED = 85;
	/** 删除节点失败 */
	public static final int HTTP_BUILDER_DELETE_NODE_INFO_FAILED = 86;
	// =======查询基站心跳 ==============
	/** 查询基站心跳 成功 */
	public static final int HTTP_BUILDER_FETCH_BASESTATION_HEART_BEAT_SUCCEED = 87;
	/** 查询基站心跳 失败 */
	public static final int HTTP_BUILDER_FETCH_BASESTATION_HEART_BEAT_FAILED = 88;
	// =======获取全部基站==============
	/** 获取全部基站 成功 */
	public static final int HTTP_BUILDER_FETCH_BASESTATIONS_SUCCEED = 89;
	/** 获取全部基站 失败 */
	public static final int HTTP_BUILDER_FETCH_BASESTATIONS_FAILED = 90;
	
	// =======根据区域、路段编号获取区域的路段==============
	/** 根据区域、路段编号获取区域路段列表成功 */
	public static final int FETCH_GETROAD_LIST_SUCCEED = 91;
	/** 根据区域、路段编号获取区域路段列表失败 */
	public static final int FETCH_GETROAD_LIST_FAILED = 92;
	
	/** 获取路段下车位列表成功*/
	public static final int HTTP_BUILDER_FETCH_ROADARERSPACE_SUCCESS = 93;
	/** 获取路段下车位列表失败*/
	public static final int HTTP_BUILDER_FETCH_ROADARERSPACE_FAILED = 94;

	// *************************************动态交通部署**********************************
	// TODO 动态部署分割线===========2014-10-17 10:47 添加============================
	// =============1.用户登录==============
	/** 用户登录成功 */
	public static final int DYNAMIC_BUILDER_LOGIN_SUCCEED = 501;
	/** 用户登录失败 */
	public static final int DYNAMIC_BUILDER_LOGIN_FAILED = 502;
	// =============2.获取用户信息成功==============
	/** 用户登录成功 */
	public static final int DYNAMIC_FETCH_BUILDER_INFO_SUCCEED = 503;
	/** 用户登录失败 */
	public static final int DYNAMIC_FETCH_BUILDER_INFO_FAILED = 504;

	// =============19.新增动态交通的阵列==============
	/** 新增动态交通的阵列成功 */
	public static final int DYNAMIC_SUBMIT_LANEARRAY_INFO_SUCCEED = 537;
	/** 新增动态交通的阵列失败 */
	public static final int DYNAMIC_SUBMIT_LANEARRAY_INFO_FAILED = 538;

	// =============20.修改动态交通的阵列==============
	/** 修改动态交通的阵列成功 */
	public static final int DYNAMIC_UPDATE_LANEARRAY_INFO_SUCCEED = 539;
	/** 修改动态交通的阵列失败 */
	public static final int DYNAMIC_UPDATE_LANEARRAY_INFO_FAILED = 540;

	// =============21.获取动态交通的节点阵列的列表==============
	/** 获取动态交通的节点阵列的列表成功 */
	public static final int DYNAMIC_FETCH_LANEARRAY_LIST_SUCCEED = 541;
	/** 获取动态交通的节点阵列的列表失败 */
	public static final int DYNAMIC_FETCH_LANEARRAY_LIST_FAILED = 542;

	// =============22.获取动态交通的阵列（快速查询单个阵列）==============
	/** 获取动态交通的阵列（快速查询单个阵列）成功 */
	public static final int DYNAMIC_FETCH_LANEARRAY_INFO_SUCCEED = 543;
	/** 获取动态交通的阵列（快速查询单个阵列）失败 */
	public static final int DYNAMIC_FETCH_LANEARRAY_INFO_FAILED = 544;

	// =============23.删除动态交通的节点阵列==============
	/** 删除动态交通的节点阵列成功 */
	public static final int DYNAMIC_DELETE_LANEARRAY_SUCCEED = 545;
	/** 删除动态交通的节点阵列失败 */
	public static final int DYNAMIC_DELETE_LANEARRAY_FAILED = 546;

	// =============24.在阵列中添加节点==============
	/** 在阵列中添加节点成功 */
	public static final int DYNAMIC_SUBMIT_SENSORS_SUCCEED = 547;
	/** 在阵列中添加节点失败 */
	public static final int DYNAMIC_SUBMIT_SENSORS_FAILED = 548;

	// =============25.在阵列中删除动态交通的节点==============
	/** 在阵列中删除动态交通的节点成功 */
	public static final int DYNAMIC_DELETE_SENSOR_SUCCEED = 549;
	/** 在阵列中删除动态交通的节点失败 */
	public static final int DYNAMIC_DELETE_SENSOR_FAILED = 550;

	// =============26.获取节点的地磁值==============
	/** 获取节点的地磁值成功 */
	public static final int DYNAMIC_FETCH_SENSOR_ZVALUES_SUCCEED = 551;
	/** 获取节点的地磁值失败 */
	public static final int DYNAMIC_FETCH_SENSOR_ZVALUES_FAILED = 552;

	// =============27.获取阵列中的节点==============
	/** 获取阵列中的节点成功 */
	public static final int DYNAMIC_FETCH_SENSOR_LIST_SUCCEED = 553;
	/** 获取阵列中的节点失败 */
	public static final int DYNAMIC_FETCH_SENSOR_LIST_FAILED = 554;

	// =============28.查询节点连接的运行基站==============
	/** 查询节点连接的运行基站成功 */
	public static final int DYNAMIC_FETCH_SENSOR_BOUND_BASESTATION_SUCCEED = 555;
	/** 查询节点连接的运行基站失败 */
	public static final int DYNAMIC_FETCH_SENSOR_BOUND_BASESTATION_FAILED = 556;

	// TODO 动态部署分割线===========2014-10-20 14:18 添加============================
	// =========================获取动态交通的节点阵列的列表=========================
	/** 获取动态交通的节点阵列的列表成功 */
	public static final int DYNAMIC_FETCH_SENSORARRAY_INFO_SUCCEED = 557;
	/** 获取动态交通的节点阵列的列表失败 */
	public static final int DYNAMIC_FETCH_SENSORARRAY_INFO_FAILED = 558;
	// =========================30.使用地图坐标查询节点阵列列表=========================
	/** 使用地图坐标查询节点阵列列表 */
	public static final int DYNAMIC_FETCH_LANEARRAY_BY_LANLON_SUCCEED = 559;
	/** 使用地图坐标查询节点阵列列表失败 */
	public static final int DYNAMIC_FETCH_LANEARRAY_BY_LANLON_FAILED = 560;
	// =========================31.添加基站关系表=========================
	/** 添加基站关系表 */
	public static final int DYNAMIC_SUBMIT_BASESTATIONS_MAP_SUCCEED = 561;
	/** 添加基站关系表失败 */
	public static final int DYNAMIC_SUBMIT_BASESTATIONS_MAP_FAILED = 562;
	// =========================31.删除动态交通中的基站=========================
	/** 删除成功 */
	public static final int DYNAMIC_DELETE_BASESTATION_SUCCEED = 563;
	/** 删除失败 */
	public static final int DYNAMIC_DELETE_BASESTATION_FAILED = 564;
	// =========================32.获取阵列状态=========================
	/** 获取阵列状态成功 */
	public static final int DYNAMIC_FETCH_LANEARRAY_STATUS_SUCCEED = 565;
	/** 获取阵列状态失败 */
	public static final int DYNAMIC_FETCH_LANEARRAY_STATUS_FAILED = 566;
	// =========================33.基站配置同步=========================
	/** 获取阵列状态成功 */
	public static final int DYNAMIC_SUBMIT_BASESTATION_DATA_SYNC_SUCCEED = 567;
	/** 获取阵列状态失败 */
	public static final int DYNAMIC_SUBMIT_BASESTATION_DATA_SYNC_FAILED = 568;
	// =============34.查询节点所属阵列==============
	/** 查询节点所属阵列成功 */
	public static final int DYNAMIC_FETCH_SENSOR_BOUND_LANEARRAY_SUCCEED = 569;
	/** 查询节点所属阵列失败 */
	public static final int DYNAMIC_FETCH_SENSOR_BOUND_LANEARRAY_FAILED = 570;

	// =============35.获取所有当前最新上报的交通状态数据==============
	/** 获取所有当前最新上报的交通状态数据成功 */
	public static final int DYNAMIC_FETCH_DYNAMICARRAY_INFO_SUCCEED = 571;
	/** 获取所有当前最新上报的交通状态数据失败 */
	public static final int DYNAMIC_FETCH_DYNAMICARRAY_INFO_FAILED = 572;
	// =============36.手动更改当前交通状态数据==============
	/** 手动更改当前交通状态数据成功 */
	public static final int DYNAMIC_UPDATE_DYNAMICARRAY_STATUS_SUCCEED = 573;
	/** 手动更改当前交通状态数据失败 */
	public static final int DYNAMIC_UPDATE_DYNAMICARRAY_STATUS_FAILED = 574;
	// =============37.获取最新应用信息==============
	/** 手动更改当前交通状态数据成功 */
	public static final int FETCH_NEW_APP_VERSION_SUCCEED = 575;
	/** 手动更改当前交通状态数据失败 */
	public static final int FETCH_NEW_APP_VERSION_FAILED = 576;
	// =============39.提交节点同步信息==============
	/** 查單個節點信息成功 */
	public static final int SUBMIT_SENSOR_SYN_INFO_SUCCEED = 579;
	/** 查單個節點信息失败 */
	public static final int SUBMIT_SENSOR_SYN_INFO_FAILED = 580;
	// =============40.获取基站上报的所有数据==============
	/** 获取基站上报的所有数据信息成功 */
	public static final int FETCH_BASESTATION_DATA_SUCCEED = 581;
	/** 获取基站上报的所有数据失败 */
	public static final int FETCH_BASESTATION_DATA_FAILED = 582;
	// =============41.查询节点的配置请求==============
	/** 查询节点的配置请求信息成功 */
	public static final int FETCH_SENSOR_SET_REQUEST_SUCCEED = 583;
	/** 查询节点的配置请求失败 */
	public static final int FETCH_SENSOR_SET_REQUEST_FAILED = 584;
	// =============42.重置节点==============
	/** 获取基站上报的所有数据信息成功 */
	public static final int SUBMIT_SENSOR_RESET_SUCCEED = 585;
	/** 获取基站上报的所有数据失败 */
	public static final int SUBMIT_SENSOR_RESET_FAILED = 586;
	// =============43.获取动态节点数据心跳==============
	/** 获取动态节点数据心跳成功 */
	public static final int FETCH_DYNAMIC_NODE_DATA_SUCCEED = 587;
	/**获取动态节点数据心跳失败 */
	public static final int FETCH_DYNAMIC_NODE_DATA_FAILED = 588;

	// ******************************动态部署接口结尾分割线**********************************

	// ===================================动画相关========================
	/** 动画消息 */
	public static final int HAS_OUT_OF_LEFT = 3000;
	public static final int HAS_OUT_OF_RIGHT = 3001;
	/** 车位回到基站的车位部分动画结束的标志位 */
	public static final int PARKINGSPACE_2_BASESTATION = 3002;
	/** 基站到车位的基站部分动画结束的标志位 */
	public static final int BASESTATION_2_PARKINGSPACE_BASESTATION_ENDED = 3003;
	public static final int BASESTATION_2_PARKINGSPACE_PARKINGSPACE_STARTED = 3004;

	// ==============================Intent相关====================================
	/** Intent请求中的ActionCode，用于辨识操作 */
	/** 用于基站部分 */
	public static final int INTENT_ACTION_CODE_BASESTATION_EDIT = 7000;
	public static final int INTENT_ACTION_CODE_BASESTATION_ADD = 7001;
	public static final int INTENT_ACTION_CODE_BASESTATION_DELETE = 7002;

	/** 用于车位部分 */
	public static final int INTENT_ACTION_CODE_PARKINGLOT_DELETE = 7003;
	public static final int INTENT_ACTION_CODE_PARKINGLOT_EDIT = 7004;
	public static final int INTENT_ACTION_CODE_PARKINGLOT_ADD = 7005;
	/** 二维码请求 */
	public static final int INTENT_ACTION_CODE_ZXING_REQUEST_CODE = 7006;

	/** 添加辅助基站的请求 */
	public static final int INTENT_ACTION_CODE_ADD_SUPPORT_BASESTATION = 7007;

	/** 动态交通部署添加节点请求 */
	public static final int INTENT_ACTION_CODE_DYNIMAC_ADD_SENSOR = 7008;
	/** 动态交通部署编辑节点请求 */
	public static final int INTENT_ACTION_CODE_DYNIMAC_EDIT_SENSOR = 7009;
	/** 动态交通、编辑基站请求 */
	public static final int INTENT_REQUEST_CODE_FOR_EDIT_BASESTATION = 7010;

	/** 查看地图请求 */
	public static final int INTENT_REQUEST_CODE_FOR_CHECK_ON_MAP = 7011;

	// ===========================Handler的其他操作==========================
	public static final int HANDLER_CODE_ON_FRIST_LOCATION_SERF = 9000;
	
	
	public static final int HANDLER_CODE_UP_APP_DOWN_UPDATE = 9001;
	public static final int HANDLER_CODE_UP_APP_DOWN_DOWN_OVER = 9002;
	public static final int HANDLER_CODE_UP_APP_DOWN_ERROR = 9003;
	
	
	/**刷新部署基站的心跳状态*/
	public static final int HANDLER_CODE_REFREASH_SUPPORTBS_HEART = 9004;

}
