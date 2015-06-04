package com.cyjt.operation.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cyjt.operation.R;
import com.cyjt.operation.base.AppContext;
import com.cyjt.operation.base.HandlerMessageCodes;
import com.cyjt.operation.bean.Area;
import com.cyjt.operation.bean.RoadsAreas;
import com.cyjt.operation.fragment.AreaListFragment;
import com.cyjt.operation.fragment.AreaListFragment.OnAreaClickListener;
import com.cyjt.operation.fragment.RoadAreasListFragment;
import com.cyjt.operation.fragment.RoadAreasListFragment.OnRoadAreasClickListener;

/**
 * ����ѡ�����<BR>
 * �������������Fragment���ɣ��������������������������չʾ�������ұ�չʾ�����µĽֵ�<BR>
 * ��Fragment����չʾ·���б�
 * 
 * @author LTP<BR>
 *         2014-10-23 ����3:50:55<BR>
 */
public class NewPickRoadAreasActivity extends Activity implements
		OnRoadAreasClickListener, OnAreaClickListener {
	/***/
	private FragmentManager fragmentmanager;
	private AreaListFragment areaFragment = null;
	private AreaListFragment streetFragment = null;
	private RoadAreasListFragment roadAreasFragment = null;
	private FrameLayout main_fragment;
	private FrameLayout framelayout_street;
	private ProgressBar actionbar_progressBar;
	private Area currentStreet;

	/** ׼������ */
	private DrawerLayout drawer_layout;
	private boolean isDrawerClosed = false;
	/** ��Ļ�ܶ� */
	private float scale;
	private ArrayList<Area> areas;
	private TextView actionbar_textView_small_title;
	private TextView actionbar_refresh_button;
	private ImageView actionbar_imageView_back_button;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			stopProgress();
			switch (msg.what) {
			case HandlerMessageCodes.BUILDER_FETCH_AREA_INFO_SUCCEED:
				areas = (ArrayList<Area>) msg.obj;
				if (areas != null && areas.size() > 0) {
					// �������
					controlDrawer();
					// ��ʼ��Areafragment
					// ����ֵ���ݹ�ȥ
					creatOrRefreshAreaFragment(areas);
					// Ĭ��ֱ�������һ��
					// getStreet(areas.get(0));
				}
				break;
			case HandlerMessageCodes.BUILDER_FETCH_STREET_INFO_SUCCEED:
				ArrayList<Area> streets = (ArrayList<Area>) msg.obj;
				if (streets != null && streets.size() > 0) {
					// ��ʼ���ڶ���Areafragment
					// ����ֵ���ݹ�ȥ
					creatOrRefreshStreetFragment(streets);
				}
				break;
			case HandlerMessageCodes.FETCH_GETROAD_LIST_SUCCEED:
				ArrayList<RoadsAreas> roads = (ArrayList<RoadsAreas>) msg.obj;
				if (roads != null && roads.size() > 0) {
					// ��ʼ���ڶ���Areafragment
					// ����ֵ���ݹ�ȥ
					creatOrRefreshRoadsFragment(roads);
					Log.i("demo", "·���б��ȡ�ɹ�");
				}
				break;
			}

			if (roadAreasFragment != null) {
				roadAreasFragment.stopFreashView();
			}
			stopProgress();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		scale = getResources().getDisplayMetrics().density;
		fragmentmanager = getFragmentManager();
		setContentView(R.layout.activity_pick_basestation_new);
		initActionBar();
		initView();
		viewEvent();
		// �������磬��ȡ������Ϣ
		getArea();
	}

	private void initActionBar() {
		actionbar_progressBar = (ProgressBar) findViewById(R.id.actionbar_progressBar);
		((TextView) findViewById(R.id.actionbar_textView_title)).setText("·��");
		actionbar_textView_small_title = (TextView) findViewById(R.id.actionbar_textView_small_title);
		actionbar_textView_small_title.setText("ѡȡ����");
		actionbar_imageView_back_button = (ImageView) findViewById(R.id.actionbar_imageView_back_button);
		actionbar_imageView_back_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// if (isDrawerClosed) {
						// controlDrawer();
						// } else {
						onBackPressed();
						// }
					}
				});

		actionbar_refresh_button = (TextView) findViewById(R.id.actionbar_refresh_button);
		actionbar_refresh_button.setText("���");
		actionbar_refresh_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (currentStreet == null) {
					if (isDrawerClosed) {
						controlDrawer();
					}
					Toast.makeText(NewPickRoadAreasActivity.this, "��ѡ��Ҫ��ӵ�����",
							Toast.LENGTH_SHORT).show();
				} else {

					Toast.makeText(NewPickRoadAreasActivity.this, "��ӻ�վ",
							Toast.LENGTH_SHORT).show();
					addRoadAreas();
				}
			}
		});
		// actionbar_refresh_button.setVisibility(View.GONE);
		stopProgress();
	}

	private void initView() {
		drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
		main_fragment = (FrameLayout) findViewById(R.id.main_fragment);
		framelayout_street = (FrameLayout) findViewById(R.id.framelayout_street);
		framelayout_street = (FrameLayout) findViewById(R.id.framelayout_street);
		// textView_area_menu.setVisibility(View.GONE);
	}

	private void viewEvent() {
		((LinearLayout) findViewById(R.id.linearLayout_title))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						controlDrawer();
					}
				});

		drawer_layout.setDrawerListener(new DrawerListener() {
			@Override
			public void onDrawerStateChanged(int arg0) {

			}

			@Override
			public void onDrawerSlide(View arg0, float arg1) {
				moveTheMenuButton(arg1 * 300 * scale);
			}

			@Override
			public void onDrawerOpened(View arg0) {
				isDrawerClosed = false;
				// actionbar_imageView_back_button
				// .setImageResource(R.drawable.icon_back_white);
			}

			@Override
			public void onDrawerClosed(View arg0) {
				isDrawerClosed = true;
				// actionbar_imageView_back_button
				// .setImageResource(R.drawable.icon_list);
			}
		});
	}

	/** ���ݸ�ֵ�����ƶ��˵���ť */
	private void moveTheMenuButton(Float obj) {
		main_fragment.setX(obj);
	}

	/** �򿪻�رճ���� */
	private void controlDrawer() {
		// ��drawer_layout
		if (!drawer_layout.isDrawerOpen(drawer_layout.getChildAt(1))) {
			drawer_layout.openDrawer(Gravity.LEFT);
		} else {
			drawer_layout.closeDrawer(Gravity.LEFT);
		}
	}

	private void getArea() {
		startProgress();
		AppContext.getInstance().getVolleyTools()
				.FetchAreaInfo(handler, new Area());
	}

	private void getStreet(Area area) {
		startProgress();
		AppContext.getInstance().getVolleyTools()
				.FetchStreetInfo(handler, area);
	}

	private void getBaseStation(Area area) {
		startProgress();
		AppContext.getInstance().getVolleyTools()
				.fetchBasestationList(handler, area);
	}

	private void getRoadsAreas(Area area) {
		startProgress();
		AppContext.getInstance().getVolleyTools().fetchRoadList(handler, area);
	}

	private void creatOrRefreshAreaFragment(ArrayList<Area> areas) {
		if (areaFragment == null) {
			FragmentTransaction transaction = fragmentmanager
					.beginTransaction();
			Bundle bundle = new Bundle();
			bundle.putSerializable("areas", areas);
			bundle.putInt("currentFlag", 0);
			areaFragment = new AreaListFragment();
			areaFragment.setArguments(bundle);
			transaction.add(R.id.framelayout_area, areaFragment);
			transaction.show(areaFragment);
			transaction.commit();
		} else {
			areaFragment.refreshArrayList(areas);
		}
	}

	private void creatOrRefreshStreetFragment(ArrayList<Area> areas) {
		if (streetFragment == null) {
			FragmentTransaction transaction = fragmentmanager
					.beginTransaction();
			Bundle bundle = new Bundle();
			bundle.putSerializable("areas", areas);
			bundle.putInt("currentFlag", 1);
			streetFragment = new AreaListFragment();
			streetFragment.setArguments(bundle);
			transaction.add(R.id.framelayout_street, streetFragment);
			transaction.show(streetFragment);
			transaction.commit();
		} else {
			streetFragment.refreshArrayList(areas);
		}
	}

	private void creatOrRefreshRoadsFragment(ArrayList<RoadsAreas> roadsAreas) {
		if (roadAreasFragment == null) {
			FragmentTransaction transaction = fragmentmanager
					.beginTransaction();
			Bundle bundle = new Bundle();
			bundle.putSerializable("roadsAreas", roadsAreas);
			bundle.putInt("currentFlag", 1);
			roadAreasFragment = new RoadAreasListFragment();
			roadAreasFragment.setArguments(bundle);
			transaction.add(R.id.main_fragment, roadAreasFragment);
			transaction.show(roadAreasFragment);
			transaction.commit();
			Log.i("demo", "����roadsFragment");
		} else {
			roadAreasFragment.refreshRoadAreasList(roadsAreas);
		}
	}

	// ��ת��·��
	@Override
	public void onRoadAreasBeClicked(RoadsAreas roadsAreas) {
		Toast.makeText(NewPickRoadAreasActivity.this, "�鿴·��",
				Toast.LENGTH_SHORT).show();
		if (roadsAreas == null)
			return;
		Intent intent = new Intent(NewPickRoadAreasActivity.this,
				NewRoadPickParkingLotActivity.class);
		// Intent intent = new Intent(NewPickBaseStationActivity.this,
		// NewPickParkingLotActivity.class);
		intent.putExtra("roadsAreas", roadsAreas);
		intent.putExtra("smallTitle", actionbar_textView_small_title.getText()
				.toString());
		startActivity(intent);
	}

	@Override
	public void addRoadAreas() {
		// TODO ������ӻ�վ
		Toast.makeText(NewPickRoadAreasActivity.this, "��ӻ�վ",
				Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(NewPickRoadAreasActivity.this,
				NewBaseStationInfoActivity.class);
		intent.putExtra("areaId", streetStringid);
		intent.putExtra("actionAddBaseStation", true);
		startActivity(intent);
	}

	@Override
	public void OnRoadAreasRefreashed() {
		if (currentStreet != null) {
			getRoadsAreas(currentStreet);
		}
	}

	private String areaStringTitle = "";
	private String streetStringTitle = "";
	private long streetStringid = -1;

	@Override
	public void onAreaBeClicked(Area area, int currentFlag) {
		// �ж�currentFlag��ֵ
		switch (currentFlag) {
		case 0:
			// �޸ĸ�����
			areaStringTitle = area.getTitle();
			streetStringTitle = "";
			currentStreet = null;
			// ��������ֵ��б�
			getStreet(area);
			break;
		case 1:
			// ���¸�����
			streetStringTitle = "," + area.getTitle();
			streetStringid = area.getId();
			// �رճ���
			controlDrawer();
			// ����ջ�վFragment������
			creatOrRefreshRoadsFragment(null);
			// �������վ�б�
			// getBaseStation(area);
			getRoadsAreas(area);
			currentStreet = area;
			break;

		default:
			break;
		}
		actionbar_textView_small_title.setText(areaStringTitle
				+ streetStringTitle);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// ����ӿڣ�ˢ�½���
		OnRoadAreasRefreashed();
	}

	private void startProgress() {
		actionbar_progressBar.setIndeterminate(false);
		actionbar_progressBar.setVisibility(View.VISIBLE);
		actionbar_refresh_button.setVisibility(View.GONE);
	}

	private void stopProgress() {
		actionbar_progressBar.setIndeterminate(true);
		actionbar_progressBar.setVisibility(View.GONE);
		actionbar_refresh_button.setVisibility(View.VISIBLE);
	}

}
