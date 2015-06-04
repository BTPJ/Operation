package com.cyjt.operation.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.cyjt.operation.R;
import com.cyjt.operation.bean.BaseStation;
import com.cyjt.operation.bean.LaneArray;

public class DynamicBasestationMapFragment extends Fragment {
	/** 百度地图 */
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private LayoutInflater inflater;
	private ArrayList<Marker> baseStationMarkers;
	private ArrayList<Marker> laneArrayMarkers;
	private ImageView imageView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		View rootView = inflater.inflate(
				R.layout.fragment_dynamic_basestation_map, container, false);
		mMapView = (MapView) rootView.findViewById(R.id.bmapsView);
		imageView = (ImageView) rootView
				.findViewById(R.id.imageView_map_find_serlf);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		viewEvent();
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 定位自己
				if (currentLocation != null) {
					findGoal(new LatLng(currentLocation.latitude,
							currentLocation.longitude));
				}
			}
		});
	}

	private void viewEvent() {
		// 初始化百度地图
		initBaiduMap();
		// 初始化百度定位
		initBaiduLocation();
	}

	// private void initMapOptions() {
	// BaiduMapOptions mapOptions;
	// mapOptions = new BaiduMapOptions();
	// // 禁止俯视图
	// mapOptions.overlookingGesturesEnabled(false);
	// // 地图类型正常，可设置卫星图
	// mapOptions.mapType(BaiduMap.MAP_TYPE_NORMAL);
	// // 去指南针
	// mapOptions.compassEnabled(false);
	// // 设置是否显示比例尺控件
	// mapOptions.scaleControlEnabled(true);
	// //设置是否允许拖拽手势，默认允许
	// mapOptions.scrollGesturesEnabled(true);
	// //设置是否显示缩放控件
	// mapOptions.zoomControlsEnabled(true);
	// //设置是否允许缩放手势
	// mapOptions.zoomGesturesEnabled(true);
	// }

	private void initBaiduMap() {
		mBaiduMap = mMapView.getMap();
		// 隐藏缩放控件
		int childCount = mMapView.getChildCount();
		View zoom = null;
		for (int i = 0; i < childCount; i++) {
			View child = mMapView.getChildAt(i);
			if (child instanceof ZoomControls) {
				zoom = child;
				break;
			}
		}
		zoom.setVisibility(View.GONE);
		UiSettings mUiSettings = mBaiduMap.getUiSettings();
		// 禁止俯视图
		mUiSettings.setOverlookingGesturesEnabled(false);
		// 不要指南针
		mUiSettings.setCompassEnabled(false);
		// 设置地图类型
		// 普通地图
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		// 卫星地图
		// mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
		// 设置缩放级别
		MapStatusUpdate ms = MapStatusUpdateFactory.zoomTo(18);
		mBaiduMap.setMapStatus(ms);
		// 开启定位
		mBaiduMap.setMyLocationEnabled(true);
	}

	private Map<String, Double> screenLocationMap = null;
	private Point point;
	private LatLng latLng;

	/**
	 * 获取当前屏幕对应的地图坐标
	 * 
	 * @return Map<String, Double> <BR>
	 *         latLngStartLat屏幕左上角纬度，latLngStartLon屏幕左上角经度<BR>
	 *         latLngEndLat屏幕右下角纬度，latLngEndLon屏幕右上角经度
	 */
	public Map<String, Double> getScreenLocation() {
		if(mMapView==null){
			return null;
		}
		screenLocationMap = new HashMap<String, Double>();
		
		point = new Point((int) mMapView.getX(), (int) mMapView.getY());
		latLng = mBaiduMap.getProjection().fromScreenLocation(point);
		screenLocationMap.put("latLngStartLat", latLng.latitude);
		screenLocationMap.put("latLngStartLon", latLng.longitude);
		point = new Point(mMapView.getHeight(), mMapView.getWidth());
		latLng = mBaiduMap.getProjection().fromScreenLocation(point);
		screenLocationMap.put("latLngEndLat", latLng.latitude);
		screenLocationMap.put("latLngEndLon", latLng.longitude);
		return screenLocationMap;
	}

	/**
	 * 刷新地图上的基站信息
	 * 
	 * @param baseStations
	 *            要显示的基站集合
	 */
	public void freshBaseStations(ArrayList<BaseStation> baseStations) {
		if(isDestroed) return;
		OverlayOptions option = null;
		BaseStation baseStation;
		LatLng point = null;
		if (mBaiduMap != null) {
			removeBaseStationMarkers();
		}
		for (int i = 0; i < baseStations.size(); i++) {
			baseStation = baseStations.get(i);
			point = new LatLng(baseStation.getLat(), baseStation.getLon());
			// 构建Marker图标
			// 构建MarkerOption，用于在地图上添加Marker
			option = new MarkerOptions().position(point).icon(
					BitmapDescriptorFactory
							.fromResource(R.drawable.icon_point_orange));
			if (mBaiduMap != null && baseStationMarkers != null) {
				baseStationMarkers.add((Marker) mBaiduMap.addOverlay(option));
			}
			// 在地图上添加Marker，并显示
			// Marker marker = (Marker) mBaiduMap.addOverlay(option);
			// marker.setTitle(parking.getName());
			// marker.setZIndex(i);
			// 使用自定义的地图覆盖物控件
		}

	}

	/**
	 * 刷新地图上的基站信息
	 * 
	 * @param baseStations
	 *            要显示的基站集合
	 */
	public void freshLaneArrays(ArrayList<LaneArray> laneArrays) {
		if(isDestroed) return;
		OverlayOptions option = null;
		LaneArray laneArray;
		View rootView = null;
		LatLng point = null;
		if (mBaiduMap != null) {
			removeLaneArrayMarkers();
		}
		for (int i = 0; i < laneArrays.size(); i++) {
			laneArray = laneArrays.get(i);
			point = new LatLng(laneArray.getLat(), laneArray.getLon());
			// 构建Marker图标
			// 构建MarkerOption，用于在地图上添加Marker
			if (inflater != null) {
				rootView = inflater.inflate(
						R.layout.layout_for_baidumarker_lanearry, null);
				((TextView) rootView.findViewById(R.id.textView_location))
						.setText("" + laneArray.getLocation());
				((ImageView) rootView.findViewById(R.id.imageView_icon))
						.setBackgroundResource(R.drawable.icon_point_green);
				// option = new MarkerOptions().position(point).icon(
				// BitmapDescriptorFactory.fromView(rootView));
				Log.v("demo",
						""
								+ ((TextView) rootView
										.findViewById(R.id.textView_location))
										.getText().toString());
			}
			option = new MarkerOptions().position(point).icon(
					BitmapDescriptorFactory
							.fromResource(R.drawable.icon_point_green));
			if (mBaiduMap != null && laneArrayMarkers != null) {
				laneArrayMarkers.add((Marker) mBaiduMap.addOverlay(option));
			}
		}
	}

	private void removeBaseStationMarkers() {
		if (baseStationMarkers == null) {
			baseStationMarkers = new ArrayList<Marker>();
			return;
		}
		removeMarkers(baseStationMarkers);
	}

	private void removeLaneArrayMarkers() {
		if (laneArrayMarkers == null) {
			laneArrayMarkers = new ArrayList<Marker>();
			return;
		}
		removeMarkers(laneArrayMarkers);
	}

	private void removeMarkers(ArrayList<Marker> markers) {
		if (markers != null && markers.size() > 0) {
			for (Marker marker : markers) {
				marker.remove();
			}
		}
	}

	private LocationClient mLocationClient = null;
	private BDLocationListener myListener = new MyLocationListener();

	private void initBaiduLocation() {
		mLocationClient = new LocationClient(getActivity()
				.getApplicationContext()); // 声明LocationClient类
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
		mLocationClient.setLocOption(option);
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		mLocationClient.start();
		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.requestLocation();// 立即定位
		}
	}

	private boolean isFirstLoc = true;
	private MyLocationData currentLocation;

	private void freshSelf(MyLocationData location) {
		// mLocationClient.stop();
		currentLocation = location;
		// map view 销毁后不在处理新接收的位置
		if (location != null && mMapView != null && mBaiduMap != null) {
			mBaiduMap.setMyLocationData(location);
		}
		if (isFirstLoc) {
			isFirstLoc = false;
			if (currentLocation != null) {
				findGoal(new LatLng(currentLocation.latitude,
						currentLocation.longitude));
			}
			// 第一次定位到自己
			// 通知下层获取数据
			listener.firstLocationSelf();
		}
	}

	/** 定位到基站 */
	private void findGoal(LatLng ll) {
		if (ll != null) {
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
		}
	}

	private class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					.direction(location.getDirection())
					.latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			freshSelf(locData);
		}

		@Override
		public void onReceivePoi(BDLocation poiLocation) {
		}
	}
private boolean isDestroed = false;
	@Override
	public void onDestroy() {
		super.onDestroy();
		isDestroed = true;
		if(mLocationClient!=null){
			mLocationClient.stop();
		}
		mBaiduMap= null;
		mMapView.onDestroy();
	
		
	}

	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	private MapEnventListener listener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (MapEnventListener) activity;
	}

	public interface MapEnventListener {
		public void firstLocationSelf();
	}
}
