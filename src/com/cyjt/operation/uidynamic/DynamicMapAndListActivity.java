package com.cyjt.operation.uidynamic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cyjt.operation.R;
import com.cyjt.operation.base.AppConfig;
import com.cyjt.operation.base.AppContext;
import com.cyjt.operation.base.BaseActivity;
import com.cyjt.operation.base.ComparatorBaseStation;
import com.cyjt.operation.base.HandlerMessageCodes;
import com.cyjt.operation.bean.BaseStation;
import com.cyjt.operation.bean.LaneArray;
import com.cyjt.operation.fragment.DynamicBasestationListFragment;
import com.cyjt.operation.fragment.DynamicBasestationListFragment.OnBasestationClickListener;
import com.cyjt.operation.fragment.DynamicBasestationMapFragment;
import com.cyjt.operation.fragment.DynamicBasestationMapFragment.MapEnventListener;
import com.cyjt.operation.fragment.DynamicLaneArrayListFragment;
import com.cyjt.operation.fragment.DynamicLaneArrayListFragment.OnLaneArrayClickListener;
import com.cyjt.operation.ui.NewBaseStationInfoActivity;

/**
 * 动态部署功能模块的主界面<BR>
 * 使用DynamicBasestationListFragment、
 * DynamicLaneArrayListFragment和DynamicBasestationMapFragment这3个Fragment来拼凑<BR>
 * 
 * @author kullo<BR>
 *         2014-10-23 下午3:34:04<BR>
 */
public class DynamicMapAndListActivity extends BaseActivity implements
		OnBasestationClickListener, OnLaneArrayClickListener, MapEnventListener {
	private FragmentManager fragmentmanager;
	private DynamicBasestationListFragment dbListFrag;
	private DynamicLaneArrayListFragment dlListFrag;
	private DynamicBasestationMapFragment dbMapFrag;
	private ImageView actionbar_imageView_back_button;
	private TextView actionbar_refresh_button;
	private TextView actionbar_textView_title;
	private ProgressBar actionbar_progressBar;
	private RelativeLayout relativeLayout_dynamic_basestation_list_button;
	private RelativeLayout relativeLayout_dynamic_lane_sensor_list_button;
	/** 准备抽屉 */
	private DrawerLayout drawer_layout;
	/** 屏幕密度 */
	private float scale;
	/***/
	private int widthPixels;
	/** 这个标志，用于标明当前的抽屉中显示的是哪类数据，0表示显示的是基站、1表示显示的是阵列，默认为0 */
	private int currentDrawerFlag = 0;
	/** 抽屉打开与否的标志位，true表示抽屉当前处于打开状态、false即关闭状态 */
	private boolean isOpen = false;
	/** 屏幕坐标 */
	private Map<String, Double> screenLocationmap = null;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (isDestroed)
				return;

			switch (msg.what) {
			// case HandlerMessageCodes.FETCH_BASESTATION_LIST_SUCCEED:
			// @SuppressWarnings("unchecked")
			// ArrayList<BaseStation> baseStations = (ArrayList<BaseStation>)
			// msg.obj;
			// Collections.sort(baseStations, new ComparatorBaseStation());
			// if (dbListFrag != null && baseStations != null) {
			// // 更新列表
			// dbListFrag.refreshBasestationList(baseStations);
			// // 更新地图
			// }
			// if (dbMapFrag != null && baseStations != null) {
			// // 更新地图
			// dbMapFrag.freshBaseStations(baseStations);
			// }
			// break;
			// case HandlerMessageCodes.FETCH_BASESTATION_LIST_FAILED:
			//
			// break;
			case HandlerMessageCodes.DYNAMIC_FETCH_LANEARRAY_LIST_SUCCEED:
			case HandlerMessageCodes.DYNAMIC_FETCH_LANEARRAY_BY_LANLON_SUCCEED:
				@SuppressWarnings("unchecked")
				ArrayList<LaneArray> laneArrays = (ArrayList<LaneArray>) msg.obj;
				if (dlListFrag != null && laneArrays != null) {
					// 更新列表
					dlListFrag.refreshLaneArrayList(laneArrays);
				}
				if (dbMapFrag != null && laneArrays != null) {
					// 更新地图
					dbMapFrag.freshLaneArrays(laneArrays);
				}
				break;

			case HandlerMessageCodes.DYNAMIC_FETCH_LANEARRAY_LIST_FAILED:
			case HandlerMessageCodes.DYNAMIC_FETCH_LANEARRAY_BY_LANLON_FAILED:

				break;
			case HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATIONS_SUCCEED:
				@SuppressWarnings("unchecked")
				ArrayList<BaseStation> baseStations2 = (ArrayList<BaseStation>) msg.obj;
				Collections.sort(baseStations2, new ComparatorBaseStation());
				if (dbListFrag != null && baseStations2 != null) {
					// 更新列表
					dbListFrag.refreshBasestationList(baseStations2);
				}
				if (dbMapFrag != null && baseStations2 != null) {
					// 更新地图
					dbMapFrag.freshBaseStations(baseStations2);
				}
				refreashLaneArrayList();
				break;
			case HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATIONS_FAILED:

				break;
			case HandlerMessageCodes.HANDLER_CODE_ON_FRIST_LOCATION_SERF:
				// 刷新数据
				if (getScreenLocation())
					refreashBasestationList();
				break;

			default:
				break;
			}
			stopProgress();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		scale = getResources().getDisplayMetrics().density;
		widthPixels = getResources().getDisplayMetrics().widthPixels;
		fragmentmanager = getFragmentManager();
		setContentView(R.layout.activity_dynamic_basestation_list_and_map);
		initView();
		viewEvent();
		// dbListFrag = new DynamicBasestationListFragment();
		// dlListFrag = new DynamicLaneArrayListFragment();
		dbMapFrag = new DynamicBasestationMapFragment();
		if (savedInstanceState == null) {
			changeDrawer();
			fragmentmanager.beginTransaction()
					.add(R.id.main_fragment, dbMapFrag).commit();
		} else {

		}
	}

	private void initView() {
		relativeLayout_dynamic_lane_sensor_list_button = (RelativeLayout) findViewById(R.id.relativeLayout_dynamic_lane_sensor_list_button);
		relativeLayout_dynamic_basestation_list_button = (RelativeLayout) findViewById(R.id.relativeLayout_dynamic_basestation_list_button);
		drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
		actionbar_imageView_back_button = (ImageView) findViewById(R.id.actionbar_imageView_back_button);
		actionbar_refresh_button = (TextView) findViewById(R.id.actionbar_refresh_button);
		actionbar_textView_title = (TextView) findViewById(R.id.actionbar_textView_title);
		actionbar_textView_title.setText("地图查找");
		actionbar_progressBar = (ProgressBar) findViewById(R.id.actionbar_progressBar);
		stopProgress();
	}

	private boolean getScreenLocation() {
		if (dbMapFrag != null) {
			screenLocationmap = dbMapFrag.getScreenLocation();
			if (screenLocationmap != null) {
				Log.v("demo", "[" + screenLocationmap.get("latLngStartLat")
						+ "," + screenLocationmap.get("latLngStartLon")
						+ "]----->[" + screenLocationmap.get("latLngEndLat")
						+ "," + screenLocationmap.get("latLngEndLon") + "]");
			}
			return true;
		}
		return false;
	}

	private void viewEvent() {
		actionbar_refresh_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (getScreenLocation())
					refreashBasestationList();
				// refreashLaneArrayList();
			}
		});
		actionbar_imageView_back_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						onBackPressed();
					}
				});
		relativeLayout_dynamic_basestation_list_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (currentDrawerFlag == 0) {
							// 打开或关闭抽屉
							controlDrawer();
						} else {
							currentDrawerFlag = 0;
							changeDrawer();
							if (!isOpen) {
								controlDrawer();
							}
						}
					}
				});
		relativeLayout_dynamic_lane_sensor_list_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (currentDrawerFlag == 1) {
							// 打开或关闭抽屉
							controlDrawer();
						} else {
							currentDrawerFlag = 1;
							changeDrawer();
							if (!isOpen) {
								controlDrawer();
							}
						}
					}
				});
		drawer_layout.setDrawerListener(new DrawerListener() {
			@Override
			public void onDrawerStateChanged(int arg0) {

			}

			@Override
			public void onDrawerSlide(View arg0, float arg1) {
				// Convert the dps to pixels, based on density scale
				moveTheListButton(widthPixels - arg1 * 300 * scale - 40 * scale);
			}

			@Override
			public void onDrawerOpened(View arg0) {

			}

			@Override
			public void onDrawerClosed(View arg0) {

			}
		});
	}

	/** 根据给值横向移动菜单按钮 */
	private void moveTheListButton(Float obj) {
		relativeLayout_dynamic_basestation_list_button.setX(obj);
		relativeLayout_dynamic_lane_sensor_list_button.setX(obj);
	}

	/** 打开或关闭抽屉层 */
	private void controlDrawer() {
		// 打开drawer_layout
		if (!drawer_layout.isDrawerOpen(drawer_layout.getChildAt(1))) {
			drawer_layout.openDrawer(Gravity.RIGHT);
			isOpen = true;
		} else {
			drawer_layout.closeDrawer(Gravity.RIGHT);
			isOpen = false;
		}
	}

	/** 改变抽屉内容 ，要先改变currentDrawerFlag这个标志位，才可以调用这个方法更改内容 */
	private void changeDrawer() {
		FragmentTransaction transaction = fragmentmanager.beginTransaction();
		// 先隐藏全部内容
		hideDrawerFragments(transaction);
		// 判断当前抽屉应该要显示的状态值，然后更改抽屉
		if (currentDrawerFlag == 0) {
			turnDrawerToBasestationListFragment(transaction);
		} else if (currentDrawerFlag == 1) {
			turnDrawerToLaneArrayListFragment(transaction);
		}
		transaction.commit();
	}

	/** 隐藏抽屉中内容 */
	private void hideDrawerFragments(FragmentTransaction transaction) {
		if (dbListFrag != null) {
			transaction.hide(dbListFrag);
		}
		if (dlListFrag != null) {
			transaction.hide(dlListFrag);
		}
	}

	/** 将抽屉内容切换为基站列表 */
	private void turnDrawerToBasestationListFragment(
			FragmentTransaction transaction) {
		if (dbListFrag == null) {
			dbListFrag = new DynamicBasestationListFragment();
			transaction.add(R.id.include_right, dbListFrag);
		}
		transaction.show(dbListFrag);
	}

	/** 将抽屉内容切换为阵列列表 */
	private void turnDrawerToLaneArrayListFragment(
			FragmentTransaction transaction) {
		if (dlListFrag == null) {
			dlListFrag = new DynamicLaneArrayListFragment();
			transaction.add(R.id.include_right, dlListFrag);
		}
		transaction.show(dlListFrag);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("framentHasUsed",
				R.id.frameLayout_for_basestaion_list_fragment);
		outState.putBoolean("drawer_layout", isOpen);
	}

	@Override
	public void onBasestationBeClicked(BaseStation baseStation) {
		// 传递数据，并等待返回
		if (baseStation == null)
			return;
		Intent intent = new Intent(DynamicMapAndListActivity.this,
				DynamicBasestationActivity.class);
		intent.putExtra("baseStation", baseStation);
		startActivity(intent);
	}

	@Override
	public void addBasestation() {
		Toast.makeText(DynamicMapAndListActivity.this, "添加基站",
				Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(DynamicMapAndListActivity.this,
				NewBaseStationInfoActivity.class);
		// TODO 这儿需要修改
		intent.putExtra("areaId", "769");
		intent.putExtra("isDynamic", true);
		intent.putExtra("actionAddBaseStation", true);
		startActivity(intent);
	}

	@Override
	public void onLaneArrayBeClicked(LaneArray laneArray) {
		if (laneArray == null)
			return;
		Intent intent = new Intent(DynamicMapAndListActivity.this,
				DynamicLaneArrayActivity.class);
		intent.putExtra("laneArray", laneArray);
		startActivity(intent);
	}

	@Override
	public void addLaneArray() {
		Toast.makeText(DynamicMapAndListActivity.this, "添加阵列",
				Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(DynamicMapAndListActivity.this,
				DynamicLaneArrayInfoActivity.class);
		intent.putExtra("actionAddLaneArray", true);
		startActivity(intent);
	}

	@Override
	public void refreashLaneArrayList() {
		if (AppConfig.USING_NETWORK) {
			if (screenLocationmap != null) {
				startProgress();
				AppContext
						.getInstance()
						.getVolleyTools()
						.dynamicFetchLanearrayByLanlon(handler,
								screenLocationmap);
			}
		} else {
			startLaneArrayData();
		}
	}

	@Override
	public void refreashBasestationList() {
		Log.v("demo", "获取基站");
		if (AppConfig.USING_NETWORK) {
			if (screenLocationmap != null) {
				startProgress();
				AppContext.getInstance().getVolleyTools()
						.fetchBasestations(handler);
			}
		} else {
			startBasestationData();
		}
	}

	@Override
	public void firstLocationSelf() {
		// 延时一段时间
		handler.sendEmptyMessageDelayed(
				HandlerMessageCodes.HANDLER_CODE_ON_FRIST_LOCATION_SERF, 1000);
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreashBasestationList();
		// refreashLaneArrayList();//放在这里的话，当引起401的时候，会重复一次重新登录的逻辑，虽然在堆栈内仍然只会有一个应用实体，但两次TAG总觉得不妥
	}

	private boolean isDestroed = false;

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isDestroed = true;
	}

	private void startBasestationData() {
		new Thread() {

			@Override
			public void run() {
				super.run();
				try {
					sleep(2000);
					testDataForBasestation();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}.start();
	}

	private void startLaneArrayData() {
		new Thread() {

			@Override
			public void run() {
				super.run();
				try {
					sleep(2000);
					testDataForLaneArray();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void testDataForBasestation() {
		ArrayList<BaseStation> baseStations = new ArrayList<BaseStation>();
		BaseStation bs;
		for (int i = 0; i < 100; i++) {
			bs = new BaseStation(i + 12900, "B170100" + i, "B170100" + i, 354,
					2, 23.4545, 23.34343, "就是一个基站而已，如果要加上个编号的话，就用这个吧，第" + i
							+ "号基站", "无具体型号", new Date());
			baseStations.add(bs);
		}
		Message msg = new Message();
		msg.obj = baseStations;
		msg.what = HandlerMessageCodes.FETCH_BASESTATION_LIST_SUCCEED;
		handler.sendMessage(msg);
	}

	private void testDataForLaneArray() {
		ArrayList<LaneArray> laneArrays = new ArrayList<LaneArray>();
		LaneArray la;
		for (int i = 0; i < 100; i++) {
			la = new LaneArray(232, "17120032", new Date(),
					"如果没有什么好描述的话，描述下功能也行。这是第" + i + "条功能");
			laneArrays.add(la);
		}
		Message msg = new Message();
		msg.obj = laneArrays;
		msg.what = HandlerMessageCodes.DYNAMIC_FETCH_LANEARRAY_LIST_SUCCEED;
		handler.sendMessage(msg);
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
