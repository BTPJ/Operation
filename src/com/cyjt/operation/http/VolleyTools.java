package com.cyjt.operation.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.ImageRequest;
import com.cyjt.operation.base.AppContext;
import com.cyjt.operation.base.Constants;
import com.cyjt.operation.base.HandlerMessageCodes;
import com.cyjt.operation.base.Tools;
import com.cyjt.operation.base.URLs;
import com.cyjt.operation.bean.AppVersion;
import com.cyjt.operation.bean.Area;
import com.cyjt.operation.bean.BaseStation;
import com.cyjt.operation.bean.BaseStationHeartBeat;
import com.cyjt.operation.bean.BaseStationUpData;
import com.cyjt.operation.bean.DynamicArray;
import com.cyjt.operation.bean.DynamicNodeHeartBeat;
import com.cyjt.operation.bean.LaneArray;
import com.cyjt.operation.bean.LaneArrayStatus;
import com.cyjt.operation.bean.NewNodeHeartBeat;
import com.cyjt.operation.bean.ParkingLot;
import com.cyjt.operation.bean.RoadsAreas;
import com.cyjt.operation.bean.Sensor;
import com.cyjt.operation.bean.SensorData;
import com.cyjt.operation.bean.SensorSetRequest;
import com.cyjt.operation.bean.SensorSyncService;
import com.cyjt.operation.bean.SetupService;
import com.cyjt.operation.bean.User;
import com.cyjt.operation.bean.ZValue;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class VolleyTools {
	private Gson gson = Tools.getGson(Constants.ISO8601DateFormatShort); // 创建gson对象，并设置日期格式

	public VolleyTools() {
		HttpVolley.init(AppContext.getInstance());
	}

	/**
	 * 1.施工员登陆login.json
	 * 
	 * @param handler
	 * @param nfcCode
	 *            施工员的工作牌ID
	 */
	public void builderLogin(final Handler handler, String nfcCode) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("code", nfcCode);
		param.put("nfcCode", nfcCode);
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.HTTP_BUILDER_LOGIN, param, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------登录\n------response" + response
								+ "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.BUILDER_LOGIN_FAILED);
						try {
							if (response.getBoolean("result")) {
								msg.what = HandlerMessageCodes.BUILDER_LOGIN_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 2.获取登陆人的详情
	 * 
	 * @param handler
	 */
	public void fetchBuilderInfo(final Handler handler) {
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.HTTP_BUILDER_FETCH_BUILDER_INFO, null,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.FETCH_BUILDER_INFO_FAILED);
						try {
							Log.v("demo", "------获取登录者信息：\n------response"
									+ response + "------\n");
							User toller = gson.fromJson(
									response.getString("result"), User.class);
							if (toller != null) {
								msg.what = HandlerMessageCodes.FETCH_BUILDER_INFO_SUCCEED;
								msg.obj = toller;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 获取指定区域下的基站列表
	 * 
	 * @param handler
	 * @param area
	 *            要查询的区域
	 */
	public void fetchBasestationList(final Handler handler, Area area) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		if (area != null) {
			param.put("areaId", area.getId() == -1 ? "" : area.getId() + "");
		}
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.HTTP_BUILDER_FETCH_BASESTATION_LIST, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------获取指定区域下的基站：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.FETCH_BASESTATION_LIST_FAILED);
						try {
							List<BaseStation> basestations = gson.fromJson(
									response.getString("result"),
									new TypeToken<List<BaseStation>>() {
									}.getType());
							if (basestations != null && basestations.size() > 0) {
								msg.what = HandlerMessageCodes.FETCH_BASESTATION_LIST_SUCCEED;
								msg.obj = basestations;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 获取区域下路段列表（用于基站的快速查找）
	 * 
	 * @param handler
	 * @param area
	 */
	public void fetchRoadList(final Handler handler, Area area) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		if (area != null) {
			param.put("code", area.getCode());
		}
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.FETCH_GETROAD_LIST, param, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------获取指定区域下的路段：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.FETCH_GETROAD_LIST_FAILED);
						try {
							List<RoadsAreas> roadsAreas = gson.fromJson(
									response.getString("result"),
									new TypeToken<List<RoadsAreas>>() {
									}.getType());
							if (roadsAreas != null && roadsAreas.size() > 0) {
								msg.what = HandlerMessageCodes.FETCH_GETROAD_LIST_SUCCEED;
								msg.obj = roadsAreas;
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}

				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 获取指定的基站信息（用于基站的快速查找）
	 * 
	 * @param handler
	 * @param zXingCode
	 *            基站箱体上的二维码
	 */
	public void fetchBasestationInfo(final Handler handler, String zXingCode) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("code", zXingCode);
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.HTTP_BUILDER_FETCH_BASESTATION_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------获取指定的基站信息：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATION_INFO_FAILED);
						try {
							BaseStation bs = gson.fromJson(
									response.getString("result"),
									BaseStation.class);
							if (bs != null) {
								msg.what = HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATION_INFO_SUCCEED;
								msg.obj = bs;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 获取指定的车位（用于车位的快速查找）
	 * 
	 * @param handler
	 * @param code
	 *            已使用的车位铭牌上的二维码
	 */
	public void fetchParkingLotInfo(final Handler handler, String zXingCode) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("code", zXingCode);
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.HTTP_BUILDER_FETCH_PARKINGLOT_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------获取指定的车位信息：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.HTTP_BUILDER_FETCH_PARKINGLOT_INFO_FAILED);
						try {
							// 使用gson解析
							ParkingLot parkingLot = gson.fromJson(
									response.getString("result"),
									ParkingLot.class);
							if (parkingLot != null) {
								msg.what = HandlerMessageCodes.HTTP_BUILDER_FETCH_PARKINGLOT_INFO_SUCCEED;
								msg.obj = parkingLot;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/***
	 * 批量新建车位
	 * 
	 * @param handler
	 * @param basestation
	 *            正式基站
	 * @param parkingLots
	 *            车位集合
	 * @param supprotBasestation
	 *            辅助基站
	 */
	public void submitLotsInfo(final Handler handler, BaseStation basestation,
			ArrayList<ParkingLot> parkingLots, BaseStation supprotBasestation) {
		// 准备要提交的数据
		Map<String, String> param = new HashMap<String, String>();
		param.put("basestationId", "" + basestation.getId());
		param.put("basestationNfcCode", "" + basestation.getNfcCode());
		param.put("supprotBasestationNfcCode",
				"" + supprotBasestation.getNfcCode());
		for (int i = 0; i < parkingLots.size(); i++) {
			param.put("parkingLots[" + i + "].code", ""
					+ parkingLots.get(i).getCode());
			param.put("parkingLots[" + i + "].sensorCode", ""
					+ parkingLots.get(i).getSensorCode());
			param.put("parkingLots[" + i + "].sensorCode1", ""
					+ parkingLots.get(i).getSensorCode1());
		}
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.HTTP_BUILDER_SUBMIT_PARKINGLOTS_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------批量新增车位：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.HTTP_BUILDER_SUBMIT_PARKINGLOTS_INFO_FAILED);
						try {
							if (response.getBoolean("result")) {
								msg.what = HandlerMessageCodes.HTTP_BUILDER_SUBMIT_PARKINGLOTS_INFO_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);

	}

	/**
	 * 提交新建或编辑过后的基站信息
	 * 
	 * @param handler
	 * @param basestation
	 *            新建或编辑过的基站
	 */
	public void submitBasestationInfo(final Handler handler,
			BaseStation basestation) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		if (basestation != null) {
			param.put("code", "" + basestation.getCode());
			param.put("id",
					"" + (basestation.getId() == -1 ? "" : basestation.getId()));
			param.put("nfcCode", "" + basestation.getNfcCode());
			param.put("areaId", "" + basestation.getAreaId());
			param.put("description", "" + basestation.getDescription());
			param.put("lon", "" + basestation.getLon());
			param.put("lat", "" + basestation.getLat());
			param.put("type", "" + basestation.getType());
			Log.v("demo", "basestation.getType():" + basestation.getType());
		}
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.HTTP_BUILDER_SUBMIT_BASESTATION_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------提交基站信息：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.SUBMIT_BASESTATION_INFO_FAILED);
						try {
							if (response.getBoolean("result")) {
								msg.what = HandlerMessageCodes.SUBMIT_BASESTATION_INFO_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 提交编辑的车位信息
	 * 
	 * @param handler
	 * @param parkingSpace
	 *            编辑的车位
	 */
	public void submitLotInfo(final Handler handler, ParkingLot parkingSpace) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("id", "" + parkingSpace.getId());
		param.put("sensorCode", "" + parkingSpace.getSensorCode());
		param.put("sensorCode1", "" + parkingSpace.getSensorCode1());
		param.put("code", "" + parkingSpace.getCode());
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.HTTP_BUILDER_SUBMIT_LOT_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------提交车位信息：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.SUBMIT_LOT_INFO_FAILED);
						try {
							if (response.getBoolean("result")) {
								msg.what = HandlerMessageCodes.SUBMIT_LOT_INFO_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 新绑定节点
	 * 
	 * @param handler
	 * @param parkingSpace
	 */
	public void submitBinding(final Handler handler, ParkingLot parkingSpace) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("basestationNfcCode",
				"" + parkingSpace.getBasestationNfcCode());
		param.put("sensorCode", "" + parkingSpace.getSensorCode1());
		param.put("code", "" + parkingSpace.getCode());
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.SUBMIT_NEWBINDING_RESET, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------提交车位信息：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.SUBMIT_LOT_INFO_FAILED);
						try {
							if (response.getBoolean("result")) {
								msg.what = HandlerMessageCodes.SUBMIT_LOT_INFO_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 删除车位
	 * 
	 * @param handler
	 * @param parkingSpace
	 *            要删除的车位
	 */
	public void deleteLot(final Handler handler, ParkingLot parkingSpace) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("code", "" + parkingSpace.getCode());
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.HTTP_BUILDER_DELETE_LOT_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------删除车位操作：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.HTTP_BUILDER_DELETE_LOT_INFO_FAILED);
						try {
							if (response.getBoolean("result")) {
								msg.what = HandlerMessageCodes.HTTP_BUILDER_DELETE_LOT_INFO_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 删除基站
	 * 
	 * @param handler
	 * @param baseStation
	 *            要删除的基站
	 */
	public void deleteBasestation(final Handler handler, BaseStation baseStation) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("id", "" + baseStation.getId());
		param.put("basestationCode", "" + baseStation.getNfcCode());
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.HTTP_BUILDER_DELETE_BASESTATION_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------删除基站操作：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.HTTP_BUILDER_DELETE_BASESTATION_INFO_FAILED);
						try {
							if (response.getBoolean("result")) {
								msg.what = HandlerMessageCodes.HTTP_BUILDER_DELETE_BASESTATION_INFO_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 查询获取区域列表
	 * 
	 * @param handler
	 * @param area
	 */
	public void FetchAreaInfo(final Handler handler, Area area) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("code", "" + area.getCode());
		Log.d("LTP", area.getCode());
		// param.put("id", "" + (area.getId()==-1?"":area.getId()));
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.BUILDER_FETCH_AREA_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------获取查询的区域列表：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.BUILDER_FETCH_AREA_INFO_FAILED);
						try {
							List<Area> areas = gson.fromJson(
									response.getString("result"),
									new TypeToken<ArrayList<Area>>() {
									}.getType());
							if (areas != null && areas.size() > 0) {

								msg.obj = areas;
								msg.what = HandlerMessageCodes.BUILDER_FETCH_AREA_INFO_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 查询获取区域下的街道列表
	 * 
	 * @param handler
	 * @param area
	 */
	public void FetchStreetInfo(final Handler handler, Area area) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("code", "" + area.getCode());
		// param.put("id", "" + (area.getId()==-1?"":area.getId()));
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.BUILDER_FETCH_AREA_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// 这个请求没有返回值
						Log.v("demo", "------获取查询的街道列表：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.BUILDER_FETCH_STREET_INFO_FAILED);
						try {
							List<Area> areas = gson.fromJson(
									response.getString("result"),
									new TypeToken<ArrayList<Area>>() {
									}.getType());
							if (areas != null && areas.size() > 0) {
								msg.obj = areas;
								msg.what = HandlerMessageCodes.BUILDER_FETCH_STREET_INFO_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	// ======================静态部署接口添加 ==2014-10-24 16:37=====================
	/**
	 * 查询基站下的车位列表
	 * 
	 * @param handler
	 * @param action
	 * @param area
	 */
	public void httpFetchBasestationLots(final Handler handler, BaseStation bs,
			final int action) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("basestationCode", "" + bs.getNfcCode());
		// param.put("id", "" + (area.getId()==-1?"":area.getId()));
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.HTTP_BUILDER_FETCH_BASESTATION_LOTS, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// 这个请求没有返回值
						Log.v("demo", "------获取查询的车位列表：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATION_LOTS_FAILED);
						try {
							List<ParkingLot> parkingLots = gson.fromJson(
									response.getString("result"),
									new TypeToken<ArrayList<ParkingLot>>() {
									}.getType());
							// if (parkingLots != null && parkingLots.size() >
							// 0) {
							msg.obj = parkingLots;
							msg.arg1 = action;
							msg.what = HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATION_LOTS_SUCCEED;
							// }
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 查询路段下的车位列表
	 * 
	 * @param handler
	 * @param bs
	 * @param action
	 */
	public void httpFetchRoadAreasLots(final Handler handler, RoadsAreas bs) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("areaId", "" + bs.getId());
		// param.put("id", "" + (area.getId()==-1?"":area.getId()));
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.FETCH_GETSPACES_LIST, param, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// 这个请求没有返回值
						Log.v("demo", "------获取查询的车位列表：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.HTTP_BUILDER_FETCH_ROADARERSPACE_FAILED);
						try {
							List<ParkingLot> parkingLots = gson.fromJson(
									response.getString("result"),
									new TypeToken<ArrayList<ParkingLot>>() {
									}.getType());
							// if (parkingLots != null && parkingLots.size() >
							// 0) {
							msg.obj = parkingLots;
							msg.what = HandlerMessageCodes.HTTP_BUILDER_FETCH_ROADARERSPACE_SUCCESS;
							// }
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 提交映射关系
	 * 
	 * @param handler
	 * @param bs
	 *            正式基站
	 * @param sbs
	 *            部署基站
	 */
	public void httpSubmitBasestationMapping(final Handler handler,
			BaseStation bs, BaseStation sbs) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("basestationNfcCode", "" + bs.getNfcCode());
		param.put("supprotBasestationNfcCode", "" + sbs.getNfcCode());
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.HTTP_BUILDER_SUBMIT_BASESTATION_MAPPING, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// 这个请求没有返回值
						Log.v("demo", "------获取查询的车位列表：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.HTTP_BUILDER_SUBMIT_BASESTATION_MAPPING_FAILED);
						try {
							if (response.getBoolean("result")) {
								msg.what = HandlerMessageCodes.HTTP_BUILDER_SUBMIT_BASESTATION_MAPPING_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 查询节点心跳
	 * 
	 * @param handler
	 * @param sensor
	 *            要查询的节点
	 */
	public void fetchNodeHeartBeat(final Handler handler, Sensor sensor) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("nodeId", "" + sensor.getCode());
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.FETCH_NEWNODE_LIST, param, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// 这个请求没有返回值
						Log.v("demo", "------查询节点心跳：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.HTTP_BUILDER_FETCH_NODE_HEART_BEAT_FAILED);
						try {
							List<NewNodeHeartBeat> nodeHeartBeats = gson.fromJson(
									response.getString("result"),
									new TypeToken<ArrayList<NewNodeHeartBeat>>() {
									}.getType());
							if (nodeHeartBeats != null
									&& nodeHeartBeats.size() > 0) {
								msg.obj = nodeHeartBeats;
								msg.what = HandlerMessageCodes.HTTP_BUILDER_FETCH_NODE_HEART_BEAT_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 删除节点
	 * 
	 * @param handler
	 * @param sensor
	 */
	public void deleteSensor(final Handler handler, String sensorCode,
			final int index) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("nodeCode", "" + sensorCode);
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.HTTP_BUILDER_DELETE_NODE_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// 这个请求没有返回值
						Log.v("demo", "------删除节点：\n------response" + response
								+ "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.HTTP_BUILDER_DELETE_NODE_INFO_FAILED);
						try {
							if (response.getBoolean("result")) {
								msg.what = HandlerMessageCodes.HTTP_BUILDER_DELETE_NODE_INFO_SUCCEED;
								msg.arg1 = index;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 查询基站心跳
	 * 
	 * @param handler
	 * @param basestationCode
	 */
	public void fetchBasestationHeartBeat(final Handler handler,
			String basestationCode) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("basestationCode", "" + basestationCode);
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
//				URLs.FETCH_NEWHEARTBEAT_LIST, param,
				URLs.HTTP_BUILDER_FETCH_BASESTATION_HEART_BEAT,param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// 这个请求没有返回值
						Log.v("demo", "------查询基站心跳：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATION_HEART_BEAT_FAILED);
						try {
							List<BaseStationHeartBeat> BsHearts = gson.fromJson(
									response.getString("result"),
									new TypeToken<ArrayList<BaseStationHeartBeat>>() {
									}.getType());
							if (BsHearts != null && BsHearts.size() > 0) {
								msg.obj = BsHearts;
								msg.what = HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATION_HEART_BEAT_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 查询全部基站
	 * 
	 * @param handler
	 * @param basestationCode
	 */
	public void fetchBasestations(final Handler handler) {
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.HTTP_BUILDER_FETCH_BASESTATIONS, null,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------查询全部基站：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATIONS_FAILED);
						try {
							List<BaseStation> BsHearts = gson.fromJson(
									response.getString("result"),
									new TypeToken<ArrayList<BaseStation>>() {
									}.getType());
							if (BsHearts != null && BsHearts.size() > 0) {
								msg.obj = BsHearts;
								msg.what = HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATIONS_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	// ======================================动态部署接口==================================
	// TODO 动态部署接口快速分割线

	/**
	 * 19.新增动态交通的阵列
	 * 
	 * @param handler
	 * @param LaneArray
	 *            要添加的阵列对象
	 */
	public void dynamicSubmitLanearrayInfo(final Handler handler,
			LaneArray laneArray) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("code", "" + laneArray.getCode());
		param.put("description", "" + laneArray.getDescription());
		param.put("lon", "" + laneArray.getLon());
		param.put("lat", "" + laneArray.getLat());
		param.put("location", "" + laneArray.getLocation());
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_SUBMIT_LANEARRAY_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------新增动态交通的阵列：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.DYNAMIC_SUBMIT_LANEARRAY_INFO_FAILED);
						try {
							if (response.getBoolean("result")) {
								msg.what = HandlerMessageCodes.DYNAMIC_SUBMIT_LANEARRAY_INFO_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 20.修改动态交通的阵列
	 * 
	 * @param handler
	 * @param LaneArray
	 *            要修改的阵列对象
	 */
	public void dynamicUpdateLanearrayInfo(final Handler handler,
			LaneArray laneArray) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("code", "" + laneArray.getCode());
		param.put("description", "" + laneArray.getDescription());
		param.put("lon", "" + laneArray.getLon());
		param.put("lat", "" + laneArray.getLat());
		param.put("id", "" + laneArray.getId());
		param.put("location", "" + laneArray.getLocation());
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_UPDATE_LANEARRAY_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------修改动态交通的阵列：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.DYNAMIC_UPDATE_LANEARRAY_INFO_FAILED);
						try {
							if (response.getBoolean("result")) {
								msg.what = HandlerMessageCodes.DYNAMIC_UPDATE_LANEARRAY_INFO_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 21.获取动态交通的阵列的列表(全部)
	 * 
	 * @param handler
	 * @param screenLocationmap
	 *            要查询的坐标
	 */
	public void dynamicFetchLanearrayList(final Handler handler) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		// param.put("code", "" + screenLocationmap);
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_FETCH_LANEARRAY_LIST, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------获取动态交通的阵列的列表：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.DYNAMIC_FETCH_LANEARRAY_LIST_FAILED);
						try {
							String gsonString = response.getString("result");
							List<LaneArray> laneArrays = gson.fromJson(
									gsonString,
									new TypeToken<ArrayList<LaneArray>>() {
									}.getType());
							if (laneArrays != null && laneArrays.size() > 0) {
								msg.obj = laneArrays;
								msg.what = HandlerMessageCodes.DYNAMIC_FETCH_LANEARRAY_LIST_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 22.获取动态交通的阵列（快速查询单个阵列）
	 * 
	 * @param handler
	 * @param code
	 *            查询所需二维码
	 */
	public void dynamicFetchLanearrayInfo(final Handler handler, String code) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("code", "" + code);
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_FETCH_LANEARRAY_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo",
								"------获取动态交通的阵列（快速查询单个阵列）：\n------response"
										+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.DYNAMIC_FETCH_LANEARRAY_INFO_FAILED);
						try {
							LaneArray laneArry = gson.fromJson(
									response.getString("result"),
									LaneArray.class);
							if (laneArry != null) {
								msg.obj = laneArry;
								msg.what = HandlerMessageCodes.DYNAMIC_FETCH_LANEARRAY_INFO_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 23.删除动态交通的节点阵列
	 * 
	 * @param handler
	 * @param laneArray
	 *            要删除的阵列
	 */
	public void dynamicDeleteLanearray(final Handler handler,
			LaneArray laneArray) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("id", "" + laneArray.getId());
		param.put("code", "" + laneArray.getCode());
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_DELETE_LANEARRAY, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------删除动态交通的节点阵列：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.DYNAMIC_DELETE_LANEARRAY_FAILED);
						try {
							if (response.getBoolean("result")) {
								msg.what = HandlerMessageCodes.DYNAMIC_DELETE_LANEARRAY_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 24.在阵列中添加节点(批量添加节点)
	 * 
	 * @param handler
	 * @param baseStation
	 *            正式基站(运行基站、节点要连接到的基站)
	 * @param supportBaseStation
	 *            部署基站(节点出厂时默认连接的基站)
	 * @param laneArray
	 *            (要添加节点的阵列)
	 * @param sensors
	 *            (要施工的节点集合)
	 */
	public void dynamicSubmitSensors(final Handler handler,
			BaseStation baseStation, BaseStation supportBaseStation,
			LaneArray laneArray, ArrayList<Sensor> sensors) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("basestationId", "" + baseStation.getId());
		param.put("supportBasestationCode", "" + supportBaseStation.getCode());
		param.put("laneArrayId", "" + laneArray.getId());
		ArrayList<SensorData> sd = new ArrayList<SensorData>();
		for (Sensor s : sensors) {
			sd.add(new SensorData(s.getNfcCode(), s.getIcq(), s.getCrossing(),
					s.getDriveway()));
		}
		param.put("dynamicData", gson.toJson(sd));
		// for (int i = 0; i < sensors.size(); i++) {
		// sensor = sensors.get(i);
		// // param.put("nfcCode[" + i + "]", "" + sensor.getNfcCode());
		// // param.put("seq[" + i + "]", "" + sensor.getIcq());
		// // param.put("crossing[" + i + "]", "" + sensor.getCrossing());
		// // param.put("driveway[" + i + "]", "" + sensor.getDriveway());
		//
		// }
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_SUBMIT_SENSORS, param, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------在阵列中添加节点(批量添加节点)：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.DYNAMIC_SUBMIT_SENSORS_FAILED);
						try {
							if (response.getBoolean("result")) {
								msg.what = HandlerMessageCodes.DYNAMIC_SUBMIT_SENSORS_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 25.在阵列中删除动态交通的节点
	 * 
	 * @param handler
	 * @param sensor
	 *            要删除的节点
	 */
	public void dynamicDeleteSensor(final Handler handler, Sensor sensor) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("nfcCode", "" + sensor.getNfcCode());
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_DELETE_SENSOR, param, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------在阵列中删除动态交通的节点：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.DYNAMIC_DELETE_SENSOR_FAILED);
						try {
							if (response.getBoolean("result")) {
								msg.what = HandlerMessageCodes.DYNAMIC_DELETE_SENSOR_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 26.获取节点的地磁值
	 * 
	 * @param handler
	 * @param laneArray
	 *            要查询的阵列
	 */
	public void dynamicFetchSensorZvalues(final Handler handler, Sensor sensor) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("sensorCode", "" + sensor.getNfcCode());
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_FETCH_SENSOR_ZVALUES, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------获取节点的地磁值：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.DYNAMIC_FETCH_SENSOR_ZVALUES_FAILED);
						try {
							List<ZValue> sensors = gson.fromJson(
									response.getString("result"),
									new TypeToken<ArrayList<ZValue>>() {
									}.getType());
							if (sensors != null && sensors.size() > 0) {
								msg.obj = sensors;
								msg.what = HandlerMessageCodes.DYNAMIC_FETCH_SENSOR_ZVALUES_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 27.获取阵列中的节点
	 * 
	 * @param handler
	 * @param laneArray
	 *            要查询的阵列
	 */
	public void dynamicFetchSensorList(final Handler handler,
			LaneArray laneArray) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("code", "" + laneArray.getCode());
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_FETCH_SENSOR_LIST, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------获取阵列中的节点：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.DYNAMIC_FETCH_SENSOR_LIST_FAILED);
						try {
							List<Sensor> sensors = gson.fromJson(
									response.getString("result"),
									new TypeToken<ArrayList<Sensor>>() {
									}.getType());
							if (sensors != null && sensors.size() > 0) {
								msg.obj = sensors;
								msg.what = HandlerMessageCodes.DYNAMIC_FETCH_SENSOR_LIST_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 28.查询节点连接的运行基站
	 * 
	 * @param handler
	 * @param Sensor
	 *            sensor 要查询的节点
	 */
	public void dynamicFetchSensorBoundBasestation(final Handler handler,
			Sensor sensor) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		// param.put("id", "" + sensor.getBasestationId());
		param.put("nfcCode", "" + sensor.getNfcCode());
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_FETCH_SENSOR_BOUND_BASESTATION, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------查询节点连接的运行基站：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.DYNAMIC_FETCH_SENSOR_BOUND_BASESTATION_FAILED);
						try {
							BaseStation baseStation = gson.fromJson(
									response.getString("result"),
									BaseStation.class);
							if (baseStation != null) {
								msg.obj = baseStation;
								msg.what = HandlerMessageCodes.DYNAMIC_FETCH_SENSOR_BOUND_BASESTATION_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 28.查询节点所属的阵列
	 * 
	 * @param handler
	 * @param Sensor
	 *            sensor 要查询的节点
	 */
	public void dynamicFetchSensorBoundLaneArray(final Handler handler,
			Sensor sensor) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		// param.put("id", "" + sensor.getBasestationId());
		param.put("nfcCode", "" + sensor.getNfcCode());
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_FETCH_SENSOR_BOUND_LANEARRAY, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------查询节点所属的阵列：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.DYNAMIC_FETCH_SENSOR_BOUND_LANEARRAY_FAILED);
						try {
							LaneArray la = gson.fromJson(
									response.getString("result"),
									LaneArray.class);
							if (la != null) {
								msg.obj = la;
								msg.what = HandlerMessageCodes.DYNAMIC_FETCH_SENSOR_BOUND_LANEARRAY_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	// ==============================动态部署新增接口2014_10_20_14_15=============================
	// TODO 动态部署新增接口2014_10_20_14_15
	/**
	 * 29.获取动态交通基站下的节点列表
	 * 
	 * @param handler
	 * @param basestation
	 *            基站
	 */
	public void dynamicFetchSensorarrayInfo(final Handler handler,
			BaseStation basestation) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("code", "" + basestation.getCode());
		param.put("id", "" + basestation.getId());
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_FETCH_SENSORARRAY_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------获取动态交通的节点阵列的列表：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.DYNAMIC_FETCH_SENSORARRAY_INFO_FAILED);
						try {
							List<Sensor> sensors = gson.fromJson(
									response.getString("result"),
									new TypeToken<ArrayList<Sensor>>() {
									}.getType());
							if (sensors != null && sensors.size() > 0) {
								msg.obj = sensors;
								msg.what = HandlerMessageCodes.DYNAMIC_FETCH_SENSORARRAY_INFO_SUCCEED;
							}
						} catch (JSONException e) {
							msg.what = HandlerMessageCodes.DYNAMIC_FETCH_SENSORARRAY_INFO_FAILED;
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 30.获取动态交通的阵列的列表
	 * 
	 * @param handler
	 * @param screenLocationmap
	 *            要查询的坐标
	 */
	public void dynamicFetchLanearrayByLanlon(final Handler handler,
			Map<String, Double> screenLocationmap) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		// param.put("startLat", "30.638586");
		// param.put("startLon", "114.210121");
		// param.put("endLat", "30.643871");
		// param.put("endLon", "114.210589");
		double slat = screenLocationmap.get("latLngStartLat");
		double slon = screenLocationmap.get("latLngStartLon");
		double elat = screenLocationmap.get("latLngEndLat");
		double elon = screenLocationmap.get("latLngEndLon");
		param.put("endLat", "" + (slat >= elat ? slat : elat));// 采用小端
		param.put("startLat", "" + (slat < elat ? slat : elat));
		param.put("endLon", "" + (slon >= elon ? slon : elon));
		param.put("startLon", "" + (slon < elon ? slon : elon));
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_FETCH_LANEARRAY_BY_LANLON, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------获取动态交通的阵列的列表：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.DYNAMIC_FETCH_LANEARRAY_BY_LANLON_FAILED);
						try {
							List<LaneArray> laneArrays = gson.fromJson(
									response.getString("result"),
									new TypeToken<ArrayList<LaneArray>>() {
									}.getType());
							if (laneArrays != null && laneArrays.size() > 0) {
								msg.obj = laneArrays;
								msg.what = HandlerMessageCodes.DYNAMIC_FETCH_LANEARRAY_BY_LANLON_SUCCEED;
							}
						} catch (JSONException e) {
							msg.what = HandlerMessageCodes.DYNAMIC_FETCH_LANEARRAY_BY_LANLON_FAILED;
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 31.提交基站关系列表
	 * 
	 * @param handler
	 * @param bs
	 *            正式基站
	 * @param sbs
	 *            部署基站
	 */
	public void dynamicSubmitBasestationsMap(final Handler handler,
			BaseStation bs, BaseStation sbs) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("basestationId", bs.getId() + "");
		param.put("supportBasestationCode", sbs.getCode());
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_SUBMIT_BASESTATIONS_MAP, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------提交基站关系：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.DYNAMIC_SUBMIT_BASESTATIONS_MAP_FAILED);
						try {
							if (response.getBoolean("result")) {
								msg.what = HandlerMessageCodes.DYNAMIC_SUBMIT_BASESTATIONS_MAP_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 32.删除基站
	 * 
	 */
	public void dynamicDeleteBasestation(final Handler handler, BaseStation bs) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("basestationCode", bs.getCode());
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_DELETE_BASESTATION, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------删除基站：\n------response" + response
								+ "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.DYNAMIC_DELETE_BASESTATION_FAILED);
						try {
							if (response.getBoolean("result")) {
								msg.what = HandlerMessageCodes.DYNAMIC_DELETE_BASESTATION_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 33.查询阵列心跳
	 * 
	 * @param handler
	 * @param basestationCode
	 */
	public void fetchLaneArrayStatus(final Handler handler, String laneArrayCode) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("laneArrayCode", "" + laneArrayCode);
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_FETCH_LANEARRAY_STATUS, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// 这个请求没有返回值
						Log.v("demo", "------查询阵列状态：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.DYNAMIC_FETCH_LANEARRAY_STATUS_FAILED);
						try {
							List<LaneArrayStatus> LAStatus = gson.fromJson(
									response.getString("result"),
									new TypeToken<ArrayList<LaneArrayStatus>>() {
									}.getType());
							if (LAStatus != null && LAStatus.size() > 0) {
								msg.obj = LAStatus;
								msg.what = HandlerMessageCodes.DYNAMIC_FETCH_LANEARRAY_STATUS_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 34.获取全部阵列的当前状态
	 * 
	 * @param handler
	 */
	public void dynamicFetchDynamicArrayInfo(final Handler handler) {
		Map<String, String> param = new HashMap<String, String>();
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_FETCH_DYNAMICARRAY_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------获取全部阵列的当前状态：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.DYNAMIC_FETCH_DYNAMICARRAY_INFO_FAILED);
						try {
							List<DynamicArray> laneArrays = gson.fromJson(
									response.getString("result"),
									new TypeToken<ArrayList<DynamicArray>>() {
									}.getType());
							if (laneArrays != null && laneArrays.size() > 0) {
								msg.obj = laneArrays;
								msg.what = HandlerMessageCodes.DYNAMIC_FETCH_DYNAMICARRAY_INFO_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 35提交要改变状态的阵列
	 * 
	 * @param handler
	 */
	public void dynamicUpdateDynamicArrayStatus(final Handler handler,
			DynamicArray dynamicArray) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		// param.put("id", "" + dynamicArray.getId());
		param.put("arrayCode", "" + dynamicArray.getArrayCode());
		param.put("status", "" + dynamicArray.getStatus());
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_UPDATE_DYNAMICARRAY_STATUS, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------提交要改变状态的阵列：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.DYNAMIC_UPDATE_DYNAMICARRAY_STATUS_FAILED);
						try {
							if (response.getBoolean("result")) {
								msg.what = HandlerMessageCodes.DYNAMIC_UPDATE_DYNAMICARRAY_STATUS_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 36.基站同步配置
	 * 
	 */
	public void dynamicSubmitBasestationDataSync(final Handler handler,
			SetupService ss, BaseStation bs) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("basestationCode", bs.getCode() + "");
		param.put("serviceIp", ss.getServiceIp() + "");
		param.put("port", ss.getPort() + "");
		param.put("type", "0x" + ss.getType());
		// 这个基站code是十六进制的,所以得前面加个0x
		param.put("equipmentId", "0x" + ss.getEquipmentId());// 设备Id
		param.put("timing", ss.getTiming() + "");
		param.put("panId", "0x" + ss.getPanId());
		param.put("frequency", ss.getFrequency() + "");
		param.put("rate", "0x" + ss.getRate() + "");
		param.put("superNode", ss.getSuperNode() + "");
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_SUBMIT_BASESTATION_DATA_SYNC, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------提交同步设置：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.DYNAMIC_SUBMIT_BASESTATION_DATA_SYNC_FAILED);
						try {
							if (response.getBoolean("result")) {
								msg.what = HandlerMessageCodes.DYNAMIC_SUBMIT_BASESTATION_DATA_SYNC_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 39.提交节点同步
	 * 
	 */
	public void submitSensorSyncinfo(final Handler handler,
			SensorSyncService ss, BaseStation bs) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("basestationCode", "" + bs.getCode());
		param.put("nodeId", "" + ss.getNodeId());
		param.put("benchmarkX", "" + ss.getBenchmarkX());
		param.put("benchmarkY", "" + ss.getBenchmarkY());
		param.put("forceTrans", "" + ss.getForceTrans());
		param.put("nodeReset", "" + ss.getNodeReset());// 设备Id
		param.put("nodeHeartBeatCommand", "" + ss.getNodeHeartBeatCommand());
		param.put("phase", "" + ss.getPhase());
		param.put("level", "" + ss.getLevel());
		param.put("reserved", "" + ss.getReserved());
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.SUBMIT_SENSOR_SYN_INFO, param, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------提交节点同步设置：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.SUBMIT_SENSOR_SYN_INFO_FAILED);
						try {
							if (response.getBoolean("result")) {
								msg.what = HandlerMessageCodes.SUBMIT_SENSOR_SYN_INFO_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 40.获取基站上报的所有数据
	 * 
	 */
	public void fetchBasestationDatas(final Handler handler, String bsCode) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("basestationCode", "" + bsCode);
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.FETCH_BASESTATION_DATA, param, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------获取基站上报的所有数据：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.FETCH_BASESTATION_DATA_FAILED);
						try {
							List<BaseStationUpData> datas = gson.fromJson(
									response.getString("result"),
									new TypeToken<ArrayList<BaseStationUpData>>() {
									}.getType());
							if (datas != null && datas.size() > 0) {
								msg.obj = datas;
								msg.what = HandlerMessageCodes.FETCH_BASESTATION_DATA_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 41.查询节点的配置请求
	 * 
	 */
	public void fetchSensorSetRequest(final Handler handler, Sensor sensor) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("code", "" + sensor.getCode());
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.FETCH_SENSOR_SET_REQUEST, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------查询节点的配置请求：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.FETCH_SENSOR_SET_REQUEST_FAILED);
						try {
							List<SensorSetRequest> datas = gson.fromJson(
									response.getString("result"),
									new TypeToken<ArrayList<SensorSetRequest>>() {
									}.getType());
							if (datas != null && datas.size() > 0) {
								msg.obj = datas;
								msg.what = HandlerMessageCodes.FETCH_SENSOR_SET_REQUEST_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 42.重置节点
	 * 
	 */
	public void submitSensorReset(final Handler handler, Sensor sensor,
			BaseStation bs) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("code", "" + sensor.getNfcCode());
		param.put("gatewayId", "" + bs.getCode());
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.SUBMIT_SENSOR_RESET, param, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------重置节点：\n------response" + response
								+ "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.SUBMIT_SENSOR_RESET_FAILED);
						try {
							if (response.getBoolean("result")) {
								msg.what = HandlerMessageCodes.SUBMIT_SENSOR_RESET_SUCCEED;
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 43.获取动态节点数据心跳
	 * 
	 */
	public void fetchDynamicNodeData(final Handler handler, Sensor sensor) {
		// 准备要提交的参数
		Map<String, String> param = new HashMap<String, String>();
		param.put("code", "" + sensor.getNfcCode());
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.FETCH_DYNAMIC_NODE_DATA, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------获取动态节点数据心跳：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.FETCH_DYNAMIC_NODE_DATA_FAILED);
						try {
							List<DynamicNodeHeartBeat> datas = gson.fromJson(
									response.getString("result"),
									new TypeToken<ArrayList<DynamicNodeHeartBeat>>() {
									}.getType());
							if (datas != null && datas.size() > 0) {
								msg.obj = datas;
								msg.what = HandlerMessageCodes.FETCH_DYNAMIC_NODE_DATA_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	// =======================================其他接口=============================
	// TODO 其他接口

	/** 37. 获取系统更新信息 */
	public void getNewAppVersion(final Handler handler) {
		// 提交
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.FETCH_NEW_APP_VERSION, null, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------获取系统更新信息：\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.FETCH_NEW_APP_VERSION_FAILED);
						try {
							AppVersion appVersion = gson.fromJson(
									response.getString("result"),
									AppVersion.class);
							if (appVersion != null) {
								msg.obj = appVersion;
								msg.what = HandlerMessageCodes.FETCH_NEW_APP_VERSION_SUCCEED;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							handler.sendMessage(msg);
						}
					}
				}, createReqErrorListener(handler));
		HttpVolley.addRequest(request);
	}

	/**
	 * 下载文件
	 * 
	 * @param handler
	 *            消息处理器
	 * @param context
	 * @param imageUrl
	 *            文件路径
	 * @return Bitmap
	 */
	public void downFile(ImageView mImageView, Context context, String imageUrl) {
		Tools.debugInfo("imageUrl:" + imageUrl);
		// 下载测试图片
		// imageUrl =
		// "http://img4.cache.netease.com/photo/0001/2014-06-06/900x600_9U37NTAU19BR0001.jpg";
		RequestQueue requestQueue = HttpVolley.getRequestQueue();
		final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(
				20);
		ImageCache imageCache = new ImageCache() {
			@Override
			public void putBitmap(String key, Bitmap value) {
				lruCache.put(key, value);
			}

			@Override
			public Bitmap getBitmap(String key) {
				return lruCache.get(key);
			}
		};

		ImageLoader imageLoader = new ImageLoader(requestQueue, imageCache);
		// 要装载图片的控件，默认图片ID，下载出错的图片ID
		ImageListener listener = ImageLoader.getImageListener(mImageView, 0, 0);
		// 这里可设定装载的图片最大尺寸，大于这个尺寸的图片将被压缩
		imageLoader.get(imageUrl, listener, 800, 600);
		Log.v("demo", "------下载文件：\n------" + imageUrl + "成功！------\n");
	}

	/**
	 * 这个下载文件的方式并没有上面那个来的高效
	 * 
	 * @param handler
	 * @param imageView
	 * @param imageUrl
	 */
	public void downFile(Handler handler, final ImageView imageView,
			String imageUrl) {
		ImageRequest imageRequest = new ImageRequest(imageUrl,
				new Response.Listener<Bitmap>() {
					@Override
					public void onResponse(Bitmap response) {
						imageView.setImageBitmap(response);
					}
					// 0,0表示默认原图长宽、填写非0值将会对图片进行压缩
				}, 0, 0, Config.RGB_565, createReqErrorListener(handler));
		HttpVolley.addRequest(imageRequest);
	}

	// -------end-------=========施工员网络==========------end------========施工员网络=======--------end------==========施工员网络========------end---
	/**
	 * 
	 * @return
	 */
	protected Response.ErrorListener createReqErrorListener(
			final Handler handler) {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(
						AppContext.getInstance(),
						VolleyErrorHelper.getMessage(error,
								AppContext.getInstance()), Toast.LENGTH_SHORT)
						.show();
				handler.sendEmptyMessage(HandlerMessageCodes.HTTP_VOLLEY_ERROR);
			}
		};
	}

}
