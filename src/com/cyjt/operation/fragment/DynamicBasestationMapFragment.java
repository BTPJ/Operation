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
	/** �ٶȵ�ͼ */
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
				// ��λ�Լ�
				if (currentLocation != null) {
					findGoal(new LatLng(currentLocation.latitude,
							currentLocation.longitude));
				}
			}
		});
	}

	private void viewEvent() {
		// ��ʼ���ٶȵ�ͼ
		initBaiduMap();
		// ��ʼ���ٶȶ�λ
		initBaiduLocation();
	}

	// private void initMapOptions() {
	// BaiduMapOptions mapOptions;
	// mapOptions = new BaiduMapOptions();
	// // ��ֹ����ͼ
	// mapOptions.overlookingGesturesEnabled(false);
	// // ��ͼ��������������������ͼ
	// mapOptions.mapType(BaiduMap.MAP_TYPE_NORMAL);
	// // ȥָ����
	// mapOptions.compassEnabled(false);
	// // �����Ƿ���ʾ�����߿ؼ�
	// mapOptions.scaleControlEnabled(true);
	// //�����Ƿ�������ק���ƣ�Ĭ������
	// mapOptions.scrollGesturesEnabled(true);
	// //�����Ƿ���ʾ���ſؼ�
	// mapOptions.zoomControlsEnabled(true);
	// //�����Ƿ�������������
	// mapOptions.zoomGesturesEnabled(true);
	// }

	private void initBaiduMap() {
		mBaiduMap = mMapView.getMap();
		// �������ſؼ�
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
		// ��ֹ����ͼ
		mUiSettings.setOverlookingGesturesEnabled(false);
		// ��Ҫָ����
		mUiSettings.setCompassEnabled(false);
		// ���õ�ͼ����
		// ��ͨ��ͼ
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		// ���ǵ�ͼ
		// mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
		// �������ż���
		MapStatusUpdate ms = MapStatusUpdateFactory.zoomTo(18);
		mBaiduMap.setMapStatus(ms);
		// ������λ
		mBaiduMap.setMyLocationEnabled(true);
	}

	private Map<String, Double> screenLocationMap = null;
	private Point point;
	private LatLng latLng;

	/**
	 * ��ȡ��ǰ��Ļ��Ӧ�ĵ�ͼ����
	 * 
	 * @return Map<String, Double> <BR>
	 *         latLngStartLat��Ļ���Ͻ�γ�ȣ�latLngStartLon��Ļ���ϽǾ���<BR>
	 *         latLngEndLat��Ļ���½�γ�ȣ�latLngEndLon��Ļ���ϽǾ���
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
	 * ˢ�µ�ͼ�ϵĻ�վ��Ϣ
	 * 
	 * @param baseStations
	 *            Ҫ��ʾ�Ļ�վ����
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
			// ����Markerͼ��
			// ����MarkerOption�������ڵ�ͼ�����Marker
			option = new MarkerOptions().position(point).icon(
					BitmapDescriptorFactory
							.fromResource(R.drawable.icon_point_orange));
			if (mBaiduMap != null && baseStationMarkers != null) {
				baseStationMarkers.add((Marker) mBaiduMap.addOverlay(option));
			}
			// �ڵ�ͼ�����Marker������ʾ
			// Marker marker = (Marker) mBaiduMap.addOverlay(option);
			// marker.setTitle(parking.getName());
			// marker.setZIndex(i);
			// ʹ���Զ���ĵ�ͼ������ؼ�
		}

	}

	/**
	 * ˢ�µ�ͼ�ϵĻ�վ��Ϣ
	 * 
	 * @param baseStations
	 *            Ҫ��ʾ�Ļ�վ����
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
			// ����Markerͼ��
			// ����MarkerOption�������ڵ�ͼ�����Marker
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
				.getApplicationContext()); // ����LocationClient��
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// ���ö�λģʽ
		option.setCoorType("bd09ll");// ���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
		option.setScanSpan(5000);// ���÷���λ����ļ��ʱ��Ϊ5000ms
		option.setIsNeedAddress(true);// ���صĶ�λ���������ַ��Ϣ
		option.setNeedDeviceDirect(true);// ���صĶ�λ��������ֻ���ͷ�ķ���
		mLocationClient.setLocOption(option);
		mLocationClient.registerLocationListener(myListener); // ע���������
		mLocationClient.start();
		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.requestLocation();// ������λ
		}
	}

	private boolean isFirstLoc = true;
	private MyLocationData currentLocation;

	private void freshSelf(MyLocationData location) {
		// mLocationClient.stop();
		currentLocation = location;
		// map view ���ٺ��ڴ����½��յ�λ��
		if (location != null && mMapView != null && mBaiduMap != null) {
			mBaiduMap.setMyLocationData(location);
		}
		if (isFirstLoc) {
			isFirstLoc = false;
			if (currentLocation != null) {
				findGoal(new LatLng(currentLocation.latitude,
						currentLocation.longitude));
			}
			// ��һ�ζ�λ���Լ�
			// ֪ͨ�²��ȡ����
			listener.firstLocationSelf();
		}
	}

	/** ��λ����վ */
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
