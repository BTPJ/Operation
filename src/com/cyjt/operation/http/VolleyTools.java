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
	private Gson gson = Tools.getGson(Constants.ISO8601DateFormatShort); // ����gson���󣬲��������ڸ�ʽ

	public VolleyTools() {
		HttpVolley.init(AppContext.getInstance());
	}

	/**
	 * 1.ʩ��Ա��½login.json
	 * 
	 * @param handler
	 * @param nfcCode
	 *            ʩ��Ա�Ĺ�����ID
	 */
	public void builderLogin(final Handler handler, String nfcCode) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("code", nfcCode);
		param.put("nfcCode", nfcCode);
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.HTTP_BUILDER_LOGIN, param, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------��¼\n------response" + response
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
	 * 2.��ȡ��½�˵�����
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
							Log.v("demo", "------��ȡ��¼����Ϣ��\n------response"
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
	 * ��ȡָ�������µĻ�վ�б�
	 * 
	 * @param handler
	 * @param area
	 *            Ҫ��ѯ������
	 */
	public void fetchBasestationList(final Handler handler, Area area) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		if (area != null) {
			param.put("areaId", area.getId() == -1 ? "" : area.getId() + "");
		}
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.HTTP_BUILDER_FETCH_BASESTATION_LIST, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------��ȡָ�������µĻ�վ��\n------response"
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
	 * ��ȡ������·���б����ڻ�վ�Ŀ��ٲ��ң�
	 * 
	 * @param handler
	 * @param area
	 */
	public void fetchRoadList(final Handler handler, Area area) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		if (area != null) {
			param.put("code", area.getCode());
		}
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.FETCH_GETROAD_LIST, param, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------��ȡָ�������µ�·�Σ�\n------response"
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
	 * ��ȡָ���Ļ�վ��Ϣ�����ڻ�վ�Ŀ��ٲ��ң�
	 * 
	 * @param handler
	 * @param zXingCode
	 *            ��վ�����ϵĶ�ά��
	 */
	public void fetchBasestationInfo(final Handler handler, String zXingCode) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("code", zXingCode);
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.HTTP_BUILDER_FETCH_BASESTATION_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------��ȡָ���Ļ�վ��Ϣ��\n------response"
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
	 * ��ȡָ���ĳ�λ�����ڳ�λ�Ŀ��ٲ��ң�
	 * 
	 * @param handler
	 * @param code
	 *            ��ʹ�õĳ�λ�����ϵĶ�ά��
	 */
	public void fetchParkingLotInfo(final Handler handler, String zXingCode) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("code", zXingCode);
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.HTTP_BUILDER_FETCH_PARKINGLOT_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------��ȡָ���ĳ�λ��Ϣ��\n------response"
								+ response + "------\n");
						Message msg = handler
								.obtainMessage(HandlerMessageCodes.HTTP_BUILDER_FETCH_PARKINGLOT_INFO_FAILED);
						try {
							// ʹ��gson����
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
	 * �����½���λ
	 * 
	 * @param handler
	 * @param basestation
	 *            ��ʽ��վ
	 * @param parkingLots
	 *            ��λ����
	 * @param supprotBasestation
	 *            ������վ
	 */
	public void submitLotsInfo(final Handler handler, BaseStation basestation,
			ArrayList<ParkingLot> parkingLots, BaseStation supprotBasestation) {
		// ׼��Ҫ�ύ������
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
						Log.v("demo", "------����������λ��\n------response"
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
	 * �ύ�½���༭����Ļ�վ��Ϣ
	 * 
	 * @param handler
	 * @param basestation
	 *            �½���༭���Ļ�վ
	 */
	public void submitBasestationInfo(final Handler handler,
			BaseStation basestation) {
		// ׼��Ҫ�ύ�Ĳ���
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
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.HTTP_BUILDER_SUBMIT_BASESTATION_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------�ύ��վ��Ϣ��\n------response"
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
	 * �ύ�༭�ĳ�λ��Ϣ
	 * 
	 * @param handler
	 * @param parkingSpace
	 *            �༭�ĳ�λ
	 */
	public void submitLotInfo(final Handler handler, ParkingLot parkingSpace) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("id", "" + parkingSpace.getId());
		param.put("sensorCode", "" + parkingSpace.getSensorCode());
		param.put("sensorCode1", "" + parkingSpace.getSensorCode1());
		param.put("code", "" + parkingSpace.getCode());
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.HTTP_BUILDER_SUBMIT_LOT_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------�ύ��λ��Ϣ��\n------response"
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
	 * �°󶨽ڵ�
	 * 
	 * @param handler
	 * @param parkingSpace
	 */
	public void submitBinding(final Handler handler, ParkingLot parkingSpace) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("basestationNfcCode",
				"" + parkingSpace.getBasestationNfcCode());
		param.put("sensorCode", "" + parkingSpace.getSensorCode1());
		param.put("code", "" + parkingSpace.getCode());
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.SUBMIT_NEWBINDING_RESET, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------�ύ��λ��Ϣ��\n------response"
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
	 * ɾ����λ
	 * 
	 * @param handler
	 * @param parkingSpace
	 *            Ҫɾ���ĳ�λ
	 */
	public void deleteLot(final Handler handler, ParkingLot parkingSpace) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("code", "" + parkingSpace.getCode());
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.HTTP_BUILDER_DELETE_LOT_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------ɾ����λ������\n------response"
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
	 * ɾ����վ
	 * 
	 * @param handler
	 * @param baseStation
	 *            Ҫɾ���Ļ�վ
	 */
	public void deleteBasestation(final Handler handler, BaseStation baseStation) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("id", "" + baseStation.getId());
		param.put("basestationCode", "" + baseStation.getNfcCode());
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.HTTP_BUILDER_DELETE_BASESTATION_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------ɾ����վ������\n------response"
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
	 * ��ѯ��ȡ�����б�
	 * 
	 * @param handler
	 * @param area
	 */
	public void FetchAreaInfo(final Handler handler, Area area) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("code", "" + area.getCode());
		Log.d("LTP", area.getCode());
		// param.put("id", "" + (area.getId()==-1?"":area.getId()));
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.BUILDER_FETCH_AREA_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------��ȡ��ѯ�������б�\n------response"
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
	 * ��ѯ��ȡ�����µĽֵ��б�
	 * 
	 * @param handler
	 * @param area
	 */
	public void FetchStreetInfo(final Handler handler, Area area) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("code", "" + area.getCode());
		// param.put("id", "" + (area.getId()==-1?"":area.getId()));
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.BUILDER_FETCH_AREA_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// �������û�з���ֵ
						Log.v("demo", "------��ȡ��ѯ�Ľֵ��б�\n------response"
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

	// ======================��̬����ӿ���� ==2014-10-24 16:37=====================
	/**
	 * ��ѯ��վ�µĳ�λ�б�
	 * 
	 * @param handler
	 * @param action
	 * @param area
	 */
	public void httpFetchBasestationLots(final Handler handler, BaseStation bs,
			final int action) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("basestationCode", "" + bs.getNfcCode());
		// param.put("id", "" + (area.getId()==-1?"":area.getId()));
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.HTTP_BUILDER_FETCH_BASESTATION_LOTS, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// �������û�з���ֵ
						Log.v("demo", "------��ȡ��ѯ�ĳ�λ�б�\n------response"
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
	 * ��ѯ·���µĳ�λ�б�
	 * 
	 * @param handler
	 * @param bs
	 * @param action
	 */
	public void httpFetchRoadAreasLots(final Handler handler, RoadsAreas bs) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("areaId", "" + bs.getId());
		// param.put("id", "" + (area.getId()==-1?"":area.getId()));
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.FETCH_GETSPACES_LIST, param, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// �������û�з���ֵ
						Log.v("demo", "------��ȡ��ѯ�ĳ�λ�б�\n------response"
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
	 * �ύӳ���ϵ
	 * 
	 * @param handler
	 * @param bs
	 *            ��ʽ��վ
	 * @param sbs
	 *            �����վ
	 */
	public void httpSubmitBasestationMapping(final Handler handler,
			BaseStation bs, BaseStation sbs) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("basestationNfcCode", "" + bs.getNfcCode());
		param.put("supprotBasestationNfcCode", "" + sbs.getNfcCode());
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.HTTP_BUILDER_SUBMIT_BASESTATION_MAPPING, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// �������û�з���ֵ
						Log.v("demo", "------��ȡ��ѯ�ĳ�λ�б�\n------response"
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
	 * ��ѯ�ڵ�����
	 * 
	 * @param handler
	 * @param sensor
	 *            Ҫ��ѯ�Ľڵ�
	 */
	public void fetchNodeHeartBeat(final Handler handler, Sensor sensor) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("nodeId", "" + sensor.getCode());
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.FETCH_NEWNODE_LIST, param, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// �������û�з���ֵ
						Log.v("demo", "------��ѯ�ڵ�������\n------response"
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
	 * ɾ���ڵ�
	 * 
	 * @param handler
	 * @param sensor
	 */
	public void deleteSensor(final Handler handler, String sensorCode,
			final int index) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("nodeCode", "" + sensorCode);
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.HTTP_BUILDER_DELETE_NODE_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// �������û�з���ֵ
						Log.v("demo", "------ɾ���ڵ㣺\n------response" + response
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
	 * ��ѯ��վ����
	 * 
	 * @param handler
	 * @param basestationCode
	 */
	public void fetchBasestationHeartBeat(final Handler handler,
			String basestationCode) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("basestationCode", "" + basestationCode);
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
//				URLs.FETCH_NEWHEARTBEAT_LIST, param,
				URLs.HTTP_BUILDER_FETCH_BASESTATION_HEART_BEAT,param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// �������û�з���ֵ
						Log.v("demo", "------��ѯ��վ������\n------response"
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
	 * ��ѯȫ����վ
	 * 
	 * @param handler
	 * @param basestationCode
	 */
	public void fetchBasestations(final Handler handler) {
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.HTTP_BUILDER_FETCH_BASESTATIONS, null,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------��ѯȫ����վ��\n------response"
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

	// ======================================��̬����ӿ�==================================
	// TODO ��̬����ӿڿ��ٷָ���

	/**
	 * 19.������̬��ͨ������
	 * 
	 * @param handler
	 * @param LaneArray
	 *            Ҫ��ӵ����ж���
	 */
	public void dynamicSubmitLanearrayInfo(final Handler handler,
			LaneArray laneArray) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("code", "" + laneArray.getCode());
		param.put("description", "" + laneArray.getDescription());
		param.put("lon", "" + laneArray.getLon());
		param.put("lat", "" + laneArray.getLat());
		param.put("location", "" + laneArray.getLocation());
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_SUBMIT_LANEARRAY_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------������̬��ͨ�����У�\n------response"
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
	 * 20.�޸Ķ�̬��ͨ������
	 * 
	 * @param handler
	 * @param LaneArray
	 *            Ҫ�޸ĵ����ж���
	 */
	public void dynamicUpdateLanearrayInfo(final Handler handler,
			LaneArray laneArray) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("code", "" + laneArray.getCode());
		param.put("description", "" + laneArray.getDescription());
		param.put("lon", "" + laneArray.getLon());
		param.put("lat", "" + laneArray.getLat());
		param.put("id", "" + laneArray.getId());
		param.put("location", "" + laneArray.getLocation());
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_UPDATE_LANEARRAY_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------�޸Ķ�̬��ͨ�����У�\n------response"
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
	 * 21.��ȡ��̬��ͨ�����е��б�(ȫ��)
	 * 
	 * @param handler
	 * @param screenLocationmap
	 *            Ҫ��ѯ������
	 */
	public void dynamicFetchLanearrayList(final Handler handler) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		// param.put("code", "" + screenLocationmap);
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_FETCH_LANEARRAY_LIST, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------��ȡ��̬��ͨ�����е��б�\n------response"
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
	 * 22.��ȡ��̬��ͨ�����У����ٲ�ѯ�������У�
	 * 
	 * @param handler
	 * @param code
	 *            ��ѯ�����ά��
	 */
	public void dynamicFetchLanearrayInfo(final Handler handler, String code) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("code", "" + code);
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_FETCH_LANEARRAY_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo",
								"------��ȡ��̬��ͨ�����У����ٲ�ѯ�������У���\n------response"
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
	 * 23.ɾ����̬��ͨ�Ľڵ�����
	 * 
	 * @param handler
	 * @param laneArray
	 *            Ҫɾ��������
	 */
	public void dynamicDeleteLanearray(final Handler handler,
			LaneArray laneArray) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("id", "" + laneArray.getId());
		param.put("code", "" + laneArray.getCode());
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_DELETE_LANEARRAY, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------ɾ����̬��ͨ�Ľڵ����У�\n------response"
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
	 * 24.����������ӽڵ�(������ӽڵ�)
	 * 
	 * @param handler
	 * @param baseStation
	 *            ��ʽ��վ(���л�վ���ڵ�Ҫ���ӵ��Ļ�վ)
	 * @param supportBaseStation
	 *            �����վ(�ڵ����ʱĬ�����ӵĻ�վ)
	 * @param laneArray
	 *            (Ҫ��ӽڵ������)
	 * @param sensors
	 *            (Ҫʩ���Ľڵ㼯��)
	 */
	public void dynamicSubmitSensors(final Handler handler,
			BaseStation baseStation, BaseStation supportBaseStation,
			LaneArray laneArray, ArrayList<Sensor> sensors) {
		// ׼��Ҫ�ύ�Ĳ���
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
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_SUBMIT_SENSORS, param, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------����������ӽڵ�(������ӽڵ�)��\n------response"
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
	 * 25.��������ɾ����̬��ͨ�Ľڵ�
	 * 
	 * @param handler
	 * @param sensor
	 *            Ҫɾ���Ľڵ�
	 */
	public void dynamicDeleteSensor(final Handler handler, Sensor sensor) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("nfcCode", "" + sensor.getNfcCode());
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_DELETE_SENSOR, param, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------��������ɾ����̬��ͨ�Ľڵ㣺\n------response"
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
	 * 26.��ȡ�ڵ�ĵش�ֵ
	 * 
	 * @param handler
	 * @param laneArray
	 *            Ҫ��ѯ������
	 */
	public void dynamicFetchSensorZvalues(final Handler handler, Sensor sensor) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("sensorCode", "" + sensor.getNfcCode());
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_FETCH_SENSOR_ZVALUES, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------��ȡ�ڵ�ĵش�ֵ��\n------response"
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
	 * 27.��ȡ�����еĽڵ�
	 * 
	 * @param handler
	 * @param laneArray
	 *            Ҫ��ѯ������
	 */
	public void dynamicFetchSensorList(final Handler handler,
			LaneArray laneArray) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("code", "" + laneArray.getCode());
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_FETCH_SENSOR_LIST, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------��ȡ�����еĽڵ㣺\n------response"
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
	 * 28.��ѯ�ڵ����ӵ����л�վ
	 * 
	 * @param handler
	 * @param Sensor
	 *            sensor Ҫ��ѯ�Ľڵ�
	 */
	public void dynamicFetchSensorBoundBasestation(final Handler handler,
			Sensor sensor) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		// param.put("id", "" + sensor.getBasestationId());
		param.put("nfcCode", "" + sensor.getNfcCode());
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_FETCH_SENSOR_BOUND_BASESTATION, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------��ѯ�ڵ����ӵ����л�վ��\n------response"
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
	 * 28.��ѯ�ڵ�����������
	 * 
	 * @param handler
	 * @param Sensor
	 *            sensor Ҫ��ѯ�Ľڵ�
	 */
	public void dynamicFetchSensorBoundLaneArray(final Handler handler,
			Sensor sensor) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		// param.put("id", "" + sensor.getBasestationId());
		param.put("nfcCode", "" + sensor.getNfcCode());
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_FETCH_SENSOR_BOUND_LANEARRAY, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------��ѯ�ڵ����������У�\n------response"
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

	// ==============================��̬���������ӿ�2014_10_20_14_15=============================
	// TODO ��̬���������ӿ�2014_10_20_14_15
	/**
	 * 29.��ȡ��̬��ͨ��վ�µĽڵ��б�
	 * 
	 * @param handler
	 * @param basestation
	 *            ��վ
	 */
	public void dynamicFetchSensorarrayInfo(final Handler handler,
			BaseStation basestation) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("code", "" + basestation.getCode());
		param.put("id", "" + basestation.getId());
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_FETCH_SENSORARRAY_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------��ȡ��̬��ͨ�Ľڵ����е��б�\n------response"
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
	 * 30.��ȡ��̬��ͨ�����е��б�
	 * 
	 * @param handler
	 * @param screenLocationmap
	 *            Ҫ��ѯ������
	 */
	public void dynamicFetchLanearrayByLanlon(final Handler handler,
			Map<String, Double> screenLocationmap) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		// param.put("startLat", "30.638586");
		// param.put("startLon", "114.210121");
		// param.put("endLat", "30.643871");
		// param.put("endLon", "114.210589");
		double slat = screenLocationmap.get("latLngStartLat");
		double slon = screenLocationmap.get("latLngStartLon");
		double elat = screenLocationmap.get("latLngEndLat");
		double elon = screenLocationmap.get("latLngEndLon");
		param.put("endLat", "" + (slat >= elat ? slat : elat));// ����С��
		param.put("startLat", "" + (slat < elat ? slat : elat));
		param.put("endLon", "" + (slon >= elon ? slon : elon));
		param.put("startLon", "" + (slon < elon ? slon : elon));
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_FETCH_LANEARRAY_BY_LANLON, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------��ȡ��̬��ͨ�����е��б�\n------response"
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
	 * 31.�ύ��վ��ϵ�б�
	 * 
	 * @param handler
	 * @param bs
	 *            ��ʽ��վ
	 * @param sbs
	 *            �����վ
	 */
	public void dynamicSubmitBasestationsMap(final Handler handler,
			BaseStation bs, BaseStation sbs) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("basestationId", bs.getId() + "");
		param.put("supportBasestationCode", sbs.getCode());
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_SUBMIT_BASESTATIONS_MAP, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------�ύ��վ��ϵ��\n------response"
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
	 * 32.ɾ����վ
	 * 
	 */
	public void dynamicDeleteBasestation(final Handler handler, BaseStation bs) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("basestationCode", bs.getCode());
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_DELETE_BASESTATION, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------ɾ����վ��\n------response" + response
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
	 * 33.��ѯ��������
	 * 
	 * @param handler
	 * @param basestationCode
	 */
	public void fetchLaneArrayStatus(final Handler handler, String laneArrayCode) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("laneArrayCode", "" + laneArrayCode);
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_FETCH_LANEARRAY_STATUS, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// �������û�з���ֵ
						Log.v("demo", "------��ѯ����״̬��\n------response"
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
	 * 34.��ȡȫ�����еĵ�ǰ״̬
	 * 
	 * @param handler
	 */
	public void dynamicFetchDynamicArrayInfo(final Handler handler) {
		Map<String, String> param = new HashMap<String, String>();
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_FETCH_DYNAMICARRAY_INFO, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------��ȡȫ�����еĵ�ǰ״̬��\n------response"
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
	 * 35�ύҪ�ı�״̬������
	 * 
	 * @param handler
	 */
	public void dynamicUpdateDynamicArrayStatus(final Handler handler,
			DynamicArray dynamicArray) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		// param.put("id", "" + dynamicArray.getId());
		param.put("arrayCode", "" + dynamicArray.getArrayCode());
		param.put("status", "" + dynamicArray.getStatus());
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_UPDATE_DYNAMICARRAY_STATUS, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------�ύҪ�ı�״̬�����У�\n------response"
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
	 * 36.��վͬ������
	 * 
	 */
	public void dynamicSubmitBasestationDataSync(final Handler handler,
			SetupService ss, BaseStation bs) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("basestationCode", bs.getCode() + "");
		param.put("serviceIp", ss.getServiceIp() + "");
		param.put("port", ss.getPort() + "");
		param.put("type", "0x" + ss.getType());
		// �����վcode��ʮ�����Ƶ�,���Ե�ǰ��Ӹ�0x
		param.put("equipmentId", "0x" + ss.getEquipmentId());// �豸Id
		param.put("timing", ss.getTiming() + "");
		param.put("panId", "0x" + ss.getPanId());
		param.put("frequency", ss.getFrequency() + "");
		param.put("rate", "0x" + ss.getRate() + "");
		param.put("superNode", ss.getSuperNode() + "");
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.DYNAMIC_SUBMIT_BASESTATION_DATA_SYNC, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------�ύͬ�����ã�\n------response"
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
	 * 39.�ύ�ڵ�ͬ��
	 * 
	 */
	public void submitSensorSyncinfo(final Handler handler,
			SensorSyncService ss, BaseStation bs) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("basestationCode", "" + bs.getCode());
		param.put("nodeId", "" + ss.getNodeId());
		param.put("benchmarkX", "" + ss.getBenchmarkX());
		param.put("benchmarkY", "" + ss.getBenchmarkY());
		param.put("forceTrans", "" + ss.getForceTrans());
		param.put("nodeReset", "" + ss.getNodeReset());// �豸Id
		param.put("nodeHeartBeatCommand", "" + ss.getNodeHeartBeatCommand());
		param.put("phase", "" + ss.getPhase());
		param.put("level", "" + ss.getLevel());
		param.put("reserved", "" + ss.getReserved());
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.SUBMIT_SENSOR_SYN_INFO, param, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------�ύ�ڵ�ͬ�����ã�\n------response"
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
	 * 40.��ȡ��վ�ϱ�����������
	 * 
	 */
	public void fetchBasestationDatas(final Handler handler, String bsCode) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("basestationCode", "" + bsCode);
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.FETCH_BASESTATION_DATA, param, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------��ȡ��վ�ϱ����������ݣ�\n------response"
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
	 * 41.��ѯ�ڵ����������
	 * 
	 */
	public void fetchSensorSetRequest(final Handler handler, Sensor sensor) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("code", "" + sensor.getCode());
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.FETCH_SENSOR_SET_REQUEST, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------��ѯ�ڵ����������\n------response"
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
	 * 42.���ýڵ�
	 * 
	 */
	public void submitSensorReset(final Handler handler, Sensor sensor,
			BaseStation bs) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("code", "" + sensor.getNfcCode());
		param.put("gatewayId", "" + bs.getCode());
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.SUBMIT_SENSOR_RESET, param, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------���ýڵ㣺\n------response" + response
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
	 * 43.��ȡ��̬�ڵ���������
	 * 
	 */
	public void fetchDynamicNodeData(final Handler handler, Sensor sensor) {
		// ׼��Ҫ�ύ�Ĳ���
		Map<String, String> param = new HashMap<String, String>();
		param.put("code", "" + sensor.getNfcCode());
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.FETCH_DYNAMIC_NODE_DATA, param,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------��ȡ��̬�ڵ�����������\n------response"
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

	// =======================================�����ӿ�=============================
	// TODO �����ӿ�

	/** 37. ��ȡϵͳ������Ϣ */
	public void getNewAppVersion(final Handler handler) {
		// �ύ
		Request<JSONObject> request = new JsonFormRequest(Method.POST,
				URLs.FETCH_NEW_APP_VERSION, null, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("demo", "------��ȡϵͳ������Ϣ��\n------response"
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
	 * �����ļ�
	 * 
	 * @param handler
	 *            ��Ϣ������
	 * @param context
	 * @param imageUrl
	 *            �ļ�·��
	 * @return Bitmap
	 */
	public void downFile(ImageView mImageView, Context context, String imageUrl) {
		Tools.debugInfo("imageUrl:" + imageUrl);
		// ���ز���ͼƬ
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
		// Ҫװ��ͼƬ�Ŀؼ���Ĭ��ͼƬID�����س����ͼƬID
		ImageListener listener = ImageLoader.getImageListener(mImageView, 0, 0);
		// ������趨װ�ص�ͼƬ���ߴ磬��������ߴ��ͼƬ����ѹ��
		imageLoader.get(imageUrl, listener, 800, 600);
		Log.v("demo", "------�����ļ���\n------" + imageUrl + "�ɹ���------\n");
	}

	/**
	 * ��������ļ��ķ�ʽ��û�������Ǹ����ĸ�Ч
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
					// 0,0��ʾĬ��ԭͼ������д��0ֵ�����ͼƬ����ѹ��
				}, 0, 0, Config.RGB_565, createReqErrorListener(handler));
		HttpVolley.addRequest(imageRequest);
	}

	// -------end-------=========ʩ��Ա����==========------end------========ʩ��Ա����=======--------end------==========ʩ��Ա����========------end---
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
