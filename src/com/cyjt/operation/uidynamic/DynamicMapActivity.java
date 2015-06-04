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
	/** �ٶȵ�ͼ */
	private MapView mMapView;
	/** ��ǰҳ���־��0��ʾ�鿴ҳ�桢1��־�༭ҳ�� ��2��־���ҳ�� */
	private int currentActionFlag = -1;
	/** �Ƿ������޸ĵ������� */
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
		/** ��ʼ���ٶȵ�ͼ */
		initBaiduMap();
		/** ��ʼ���ٶȶ�λ */
		initBaiduLocation();
		setResult(Activity.RESULT_CANCELED);
	}

	private void initView() {
		mMapView = (MapView) findViewById(R.id.bmapsView);
		((TextView) findViewById(R.id.textView_code)).setText("��ţ�"
				+ lanLot.getCode());
		((TextView) findViewById(R.id.textView_des)).setText("������"
				+ lanLot.getDescription());
		textView_lan = (TextView) findViewById(R.id.textView_lan);
		textView_lan.setText("γ�ȣ�" + latitude);
		textView_lon = (TextView) findViewById(R.id.textView_lon);
		textView_lon.setText("���ȣ�" + longitude);
		textView_addr = (TextView) findViewById(R.id.textView_addr);
		textView_addr.setText("λ�ã�");
		textView_submit_button = (TextView) findViewById(R.id.textView_submit_button);
		if (currentActionFlag == 0) {
			textView_submit_button.setVisibility(View.GONE);
		}
	}

	private void freshLanLonUI(LatLng point) {
		textView_lan.setText("γ�ȣ�" + latitude);
		textView_lon.setText("���ȣ�" + longitude);
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
						// ��λ�Լ�
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
		// ��Ҫָ����
		zoom.setVisibility(View.GONE);
		UiSettings mUiSettings = mBaiduMap.getUiSettings();
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
					// ����ͼ��ӱ�ע
					addMapOverlay(arg0);
				}
			}
		});
	}

	private LocationClient mLocationClient = null;
	private BDLocationListener myListener = new MyLocationListener();

	private void initBaiduLocation() {
		mLocationClient = new LocationClient(getApplicationContext()); // ����LocationClient��
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
		mLocationClient.stop();
		currentLocation = location;
		// map view ���ٺ��ڴ����½��յ�λ��
		if (location != null && mMapView != null && mBaiduMap != null) {
			mBaiduMap.setMyLocationData(location);
		}
		if (isFirstLoc) {
			isFirstLoc = false;
			switch (currentActionFlag) {
			case 0:
				// �鿴ģʽ������ͼ��׼Ŀ��
				if (lanLot != null) {
					findGoal(new LatLng(lanLot.getLat(), lanLot.getLon()));
				}
				break;
			case 1:
				// �༭ģʽ������ͼ��׼Ŀ��
				if (currentLocation != null) {
					findGoal(new LatLng(lanLot.getLat(), lanLot.getLon()));
				}
				break;
			case 2:
				// ���ģʽ������ͼ��׼��ǰ
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

	/** ��λ����վ */
	private void findGoal(LatLng ll) {
		if (ll != null) {
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
		}
	}

	private void addMapOverlay(LatLng point) {
		// ����Markerͼ��
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_map_mark_smll);
		// ����MarkerOption�������ڵ�ͼ�����Marker
		OverlayOptions option = new MarkerOptions().position(point)
				.icon(bitmap);
		mBaiduMap.clear();
		// �ڵ�ͼ�����Marker������ʾ
		mBaiduMap.addOverlay(option);
		// if (allowUpdataLatLng) {
		// baiduGeoCode();
		// }
		freshLanLon(point);
	}

	// private void clearMapOverlay() {
	// // ���õ�ͼ����
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
