package com.cyjt.operation.uidynamic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cyjt.operation.R;
import com.cyjt.operation.adapter.CustomBaseAdapter;
import com.cyjt.operation.base.AppConfig;
import com.cyjt.operation.base.AppContext;
import com.cyjt.operation.base.HandlerMessageCodes;
import com.cyjt.operation.bean.LaneArray;
import com.cyjt.operation.bean.Sensor;

public class DynamicLaneArrayActivity extends Activity {
	private LayoutInflater inflater;
	private LaneArray currentLaneArray;
	private ListView listView_dynamic_sensor_list;
	private ArrayList<Sensor> sensors;
	private ProgressBar actionbar_progressBar;
	private CustomBaseAdapter<RelativeLayout> adapter;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerMessageCodes.DYNAMIC_FETCH_SENSOR_LIST_SUCCEED:
				if (msg.obj != null) {
					sensors = (ArrayList<Sensor>) msg.obj;
					initOrUpAdapterData(sensors);
				}
				break;
			case HandlerMessageCodes.DYNAMIC_FETCH_SENSOR_LIST_FAILED:
				initOrUpAdapterData(null);
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
		this.inflater = getLayoutInflater();
		setContentView(R.layout.activity_dynamic_lane_array);
		initActionBar();
		if (getIntent().getSerializableExtra("laneArray") == null) {
			Toast.makeText(DynamicLaneArrayActivity.this, "出错了,阵列不存在",
					Toast.LENGTH_LONG).show();
			return;
		}
		currentLaneArray = (LaneArray) getIntent().getSerializableExtra(
				"laneArray");
		initView();
		viewEvent();
		if (AppConfig.USING_NETWORK) {
			getSensors(currentLaneArray);
		} else {
			getTestData(currentLaneArray);
		}
	}

	private void initActionBar() {
		actionbar_progressBar = (ProgressBar) findViewById(R.id.actionbar_progressBar);
		((TextView) findViewById(R.id.actionbar_textView_title)).setText("阵列");
		((ImageView) findViewById(R.id.actionbar_imageView_back_button))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						onBackPressed();
					}
				});
		((TextView) findViewById(R.id.actionbar_refresh_button))
				.setVisibility(View.GONE);
		stopProgress();
	}

	private void initView() {
		((TextView) findViewById(R.id.textView_lane_array_code))
				.setText("阵列编号：" + currentLaneArray.getCode());
		((TextView) findViewById(R.id.textView_lane_array_description))
				.setText("阵列描述：" + currentLaneArray.getDescription());
		listView_dynamic_sensor_list = (ListView) findViewById(R.id.listView_dynamic_sensor_list);
	}

	private void viewEvent() {
		((RelativeLayout) findViewById(R.id.relativeLayout_dynamic_lane_array_info))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 跳转到阵列信息界面
						Intent intent = new Intent(
								DynamicLaneArrayActivity.this,
								DynamicLaneArrayInfoActivity.class);
						intent.putExtra("laneArray", currentLaneArray);
						startActivity(intent);
					}
				});
		listView_dynamic_sensor_list
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Intent intent = new Intent(
								DynamicLaneArrayActivity.this,
								DynamicSensorInfoActivity.class);
						if (position == 0) {
							// 跳转到添加sensor的界面
							intent.putExtra("actionAddSensor", true);
							intent.putExtra("laneArray", currentLaneArray);
							startActivity(intent);
							return;
						}
						if (sensors != null && sensors.size() >= position) {
							// 跳转到sensor的信息界面
							intent.putExtra("sensor", sensors.get(position - 1));
							intent.putExtra("laneArray", currentLaneArray);
							startActivity(intent);
						}
					}
				});
	}

	private void initOrUpAdapterData(ArrayList<Sensor> sensors) {
		if (adapter == null) {
			adapter = new CustomBaseAdapter<RelativeLayout>(
					getRelayouts(sensors));
			listView_dynamic_sensor_list.setAdapter(adapter);
		} else {
			adapter.upData(getRelayouts(sensors));
			adapter.notifyDataSetChanged();
		}
	}

	private List<RelativeLayout> getRelayouts(ArrayList<Sensor> sensors) {
		List<RelativeLayout> layouts = new ArrayList<RelativeLayout>();
		layouts.add(getAddLaneArrayLayout());
		if (sensors == null)
			return layouts;
		Sensor sensor = null;
		RelativeLayout rootView = null;
		for (int i = 0; i < sensors.size(); i++) {
			sensor = sensors.get(i);
			rootView = (RelativeLayout) inflater.inflate(
					R.layout.list_view_item_for_dynamic_sensor_list, null);
			((TextView) rootView.findViewById(R.id.textView_index)).setText(i
					+ 1 + "");
			((TextView) rootView.findViewById(R.id.textView_code)).setText(""
					+ sensor.getNfcCode());
			((TextView) rootView.findViewById(R.id.textView_description))
					.setVisibility(View.GONE);
			layouts.add(rootView);
		}
		return layouts;
	}

	/** 返回一个用于添加节点的层 */
	private RelativeLayout getAddLaneArrayLayout() {
		RelativeLayout forAddLayout = (RelativeLayout) inflater.inflate(
				R.layout.layout_for_list_item_add_layout, null);
		TextView tv = (TextView) forAddLayout
				.findViewById(R.id.textView_add_button);
		tv.setText("轻触添加节点");
		return forAddLayout;
	}

	private void getSensors(LaneArray currentLaneArray) {
		startProgress();
		AppContext.getInstance().getVolleyTools()
				.dynamicFetchSensorList(handler, currentLaneArray);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (currentLaneArray != null) {
			getSensors(currentLaneArray);
		}
	}

	/**
	 * 这是一个数据测试方法,用于模拟请求
	 * 
	 * @param currentLaneArray
	 */
	private void getTestData(LaneArray currentLaneArray) {
		sensors = new ArrayList<Sensor>();
		Sensor sensor = null;
		for (int i = 0; i < 23; i++) {
			sensor = new Sensor(23 + i, "33209323000023" + i, "33209323000023"
					+ i, "33209323000023" + i, 2342, -1, new Date());
			sensors.add(sensor);
		}
		Message msg = new Message();
		msg.obj = sensors;
		msg.what = HandlerMessageCodes.DYNAMIC_FETCH_SENSOR_LIST_SUCCEED;
		handler.sendMessage(msg);
	}

	private void startProgress() {
		actionbar_progressBar.setIndeterminate(false);
		actionbar_progressBar.setVisibility(View.VISIBLE);
	}

	private void stopProgress() {
		actionbar_progressBar.setIndeterminate(true);
		actionbar_progressBar.setVisibility(View.GONE);
	}
}
