package com.cyjt.operation.uidynamic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.cyjt.operation.R;
import com.cyjt.operation.bean.LanLot;

public class DynamicMapActivity extends Activity {
	/** 百度地图 */
	private MapView mMapView;
	/** 当前页面标志，0表示查看页面、1标志编辑页面 、2标志添加页面 */
	private int currentActionFlag = -1;
	/** 是否允许修改地理坐标 */
	private boolean allowUpdataLatLng = false;
	private LanLot lanLot;
	private TextView textView_lan;
	private TextView textView_lon;
	private TextView textView_addr;
	private TextView textView_submit_button;

	private double latitude;
	private double longitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dynamic_map);
		if (getIntent().getSerializableExtra("lanLot") == null) {

		} else {
			lanLot = (LanLot) getIntent().getSerializableExtra("lanLot");
			latitude = lanLot.getLat();
			longitude = lanLot.getLon();
		}
		if (getIntent().getIntExtra("currentActionFlag", -1) != -1) {
			currentActionFlag = getIntent()
					.getIntExtra("currentActionFlag", -1);
			if (currentActionFlag != 0)
				allowUpdataLatLng = true;
		}
		initView();
		viewEvent();
		/** 初始化百度地图 */
		initBaiduMap();
		/** 初始化百度定位 */
		initBaiduLocation();
		setResult(Activity.RESULT_CANCELED);
	}

	private void initView() {
		mMapView = (MapView) findViewById(R.id.bmapsView);
		((TextView) findViewById(R.id.textView_code)).setText("编号："
				+ lanLot.getCode());
		((TextView) findViewById(R.id.textView_des)).setText("描述："
				+ lanLot.getDescription());
		textView_lan = (TextView) findViewById(R.id.textView_lan);
		textView_lan.setText("纬度：" + latitude);
		textView_lon = (TextView) findViewById(R.id.textView_lon);
		textView_lon.setText("经度：" + longitude);
		textView_addr = (TextView) findViewById(R.id.textView_addr);
		textView_addr.setText("位置：");
		textView_submit_button = (TextView) findViewById(R.id.textView_submit_button);
		if (currentActionFlag == 0) {
			textView_submit_button.setVisibility(View.GONE);
		}
	}

	private void freshLanLonUI(LatLng point) {
		textView_lan.setText("纬度：" + latitude);
		textView_lon.setText("经度：" + longitude);
		// textView_addr.setText("" + address);
	}

	private void freshLanLon(LatLng point) {
		latitude = point.latitude;
		longitude = point.longitude;
		freshLanLonUI(point);
	}

	private void viewEvent() {
		textView_submit_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mLocationClient.stop();
				lanLot.setLat(latitude);
				lanLot.setLon(longitude);
				Intent resultIntent = new Intent();
				resultIntent.putExtra("lanLot", lanLot);
				setResult(RESULT_OK, resultIntent);
				finish();
			}
		});
		((ImageView) findViewById(R.id.imageView_map_for_back))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mLocationClient.stop();
						onBackPressed();
					}
				});
		((ImageView) findViewById(R.id.imageView_map_find_serlf))
				.setOnClickListener(new OnClickListener() {
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

	private BaiduMap mBaiduMap;

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
		// 不要指南针
		zoom.setVisibility(View.GONE);
		UiSettings mUiSettings = mBaiduMap.getUiSettings();
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
		if (lanLot != null) {
			addMapOverlay(new LatLng(lanLot.getLat(), lanLot.getLon()));
		}
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public boolean onMapPoiClick(MapPoi arg0) {

				return false;
			}

			@Override
			public void onMapClick(LatLng arg0) {
				Log.v("demo", "[" + arg0.latitude + "," + arg0.longitude + "]");
				if (allowUpdataLatLng) {
					// 给地图添加标注
					addMapOverlay(arg0);
				}
			}
		});
	}

	private LocationClient mLocationClient = null;
	private BDLocationListener myListener = new MyLocationListener();

	private void initBaiduLocation() {
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
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
		mLocationClient.stop();
		currentLocation = location;
		// map view 销毁后不在处理新接收的位置
		if (location != null && mMapView != null && mBaiduMap != null) {
			mBaiduMap.setMyLocationData(location);
		}
		if (isFirstLoc) {
			isFirstLoc = false;
			switch (currentActionFlag) {
			case 0:
				// 查看模式，将地图对准目标
				if (lanLot != null) {
					findGoal(new LatLng(lanLot.getLat(), lanLot.getLon()));
				}
				break;
			case 1:
				// 编辑模式，将地图对准目标
				if (currentLocation != null) {
					findGoal(new LatLng(lanLot.getLat(), lanLot.getLon()));
				}
				break;
			case 2:
				// 添加模式，将地图对准当前
				if (currentLocation != null) {
					findGoal(new LatLng(currentLocation.latitude,
							currentLocation.longitude));
				}
				break;

			default:
				break;
			}
		}
	}

	/** 定位到基站 */
	private void findGoal(LatLng ll) {
		if (ll != null) {
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
		}
	}

	private void addMapOverlay(LatLng point) {
		// 构建Marker图标
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_map_mark_smll);
		// 构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions().position(point)
				.icon(bitmap);
		mBaiduMap.clear();
		// 在地图上添加Marker，并显示
		mBaiduMap.addOverlay(option);
		// if (allowUpdataLatLng) {
		// baiduGeoCode();
		// }
		freshLanLon(point);
	}

	// private void clearMapOverlay() {
	// // 重置地图坐标
	// allowUpdataLatLng = true;
	// mBaiduMap.clear();
	// }

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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}
}
