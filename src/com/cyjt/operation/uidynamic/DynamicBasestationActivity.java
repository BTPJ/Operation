package com.cyjt.operation.uidynamic;

import java.util.ArrayList;
import java.util.List;

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
import com.cyjt.operation.base.AppContext;
import com.cyjt.operation.base.HandlerMessageCodes;
import com.cyjt.operation.bean.BaseStation;
import com.cyjt.operation.bean.Sensor;
import com.cyjt.operation.ui.NewBaseStationInfoActivity;
import com.cyjt.operation.uitools.ToolsSetupDynamicBasestationActivity;

/**
 * 这个界面用于展示基站下的节点阵列
 * 
 * @author kullo<BR>
 *         2014-10-8 上午10:51:57<BR>
 */
public class DynamicBasestationActivity extends Activity {
	private LayoutInflater inflater;
	private ListView listView_dynamic_sensorArray_list;
	private CustomBaseAdapter<RelativeLayout> adapter;
	private ProgressBar actionbar_progressBar;
	private BaseStation currentBasestation = null;
	private ArrayList<Sensor> sensors = new ArrayList<Sensor>();
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerMessageCodes.DYNAMIC_FETCH_SENSORARRAY_INFO_SUCCEED:
				sensors = (ArrayList<Sensor>) msg.obj;
				initOrUpAdapterData(sensors);
				break;
			case HandlerMessageCodes.DYNAMIC_FETCH_SENSORARRAY_INFO_FAILED:

				break;
			case HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATION_INFO_SUCCEED:
				currentBasestation = (BaseStation) msg.obj;
				fillBaseStationUI();
				break;
			case HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATION_INFO_FAILED:

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
		setContentView(R.layout.activity_dynamic_sensor_array);
		initActionBar();
		if (getIntent().getSerializableExtra("baseStation") == null) {
			Toast.makeText(DynamicBasestationActivity.this, "出错了，基站对象不存在！",
					Toast.LENGTH_LONG).show();
			return;
		}
		currentBasestation = (BaseStation) getIntent().getSerializableExtra(
				"baseStation");
		initView();
		viewEvent();
		// 获取基站下的节点
		fetchSensorarrayList(currentBasestation);
	}

	private void initActionBar() {
		actionbar_progressBar = (ProgressBar) findViewById(R.id.actionbar_progressBar);
		((TextView) findViewById(R.id.actionbar_textView_title)).setText("基站");
		((ImageView) findViewById(R.id.actionbar_imageView_back_button))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						onBackPressed();
					}
				});
		((TextView) findViewById(R.id.actionbar_refresh_button)).setText("配时");
		((TextView) findViewById(R.id.actionbar_refresh_button))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (currentBasestation == null) {
							Toast.makeText(DynamicBasestationActivity.this,
									"当前基站不存在", Toast.LENGTH_SHORT).show();
							return;
						}
						// 跳转到自动配时
						Intent intent = new Intent(
								DynamicBasestationActivity.this,
								ToolsSetupDynamicBasestationActivity.class);
						intent.putExtra("baseStation", currentBasestation);
						startActivity(intent);
					}
				});
	}

	private void initView() {
		listView_dynamic_sensorArray_list = (ListView) findViewById(R.id.listView_dynamic_sensorArray_list);
	}

	private void fillBaseStationUI() {
		((TextView) findViewById(R.id.textView_basestation_code))
				.setText("基站编号：" + currentBasestation.getCode());
		((TextView) findViewById(R.id.textView_basestation_description))
				.setText("基站描述：" + currentBasestation.getDescription());
	}

	private void viewEvent() {
		fillBaseStationUI();
		((RelativeLayout) findViewById(R.id.relativeLayout_dynamic_basestation_info))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 跳转到基站
						Intent intent = new Intent(
								DynamicBasestationActivity.this,
								NewBaseStationInfoActivity.class);
						intent.putExtra("baseStation", currentBasestation);
						intent.putExtra("actionAddBaseStation", false);
						intent.putExtra("isDynamic", true);
						startActivity(intent);
					}
				});
		listView_dynamic_sensorArray_list
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View view,
							int position, long id) {
						// 跳转到节点信息
						Intent intent = new Intent(
								DynamicBasestationActivity.this,
								DynamicSensorInfoActivity.class);
						if (sensors != null && sensors.size() >= position) {
							// 跳转到sensor的信息界面
							intent.putExtra("sensor", sensors.get(position));
							startActivity(intent);
						}
					}
				});
	}

	private void initOrUpAdapterData(ArrayList<Sensor> sensors) {
		if (adapter == null) {
			adapter = new CustomBaseAdapter<RelativeLayout>(
					getRelayouts(sensors));
			listView_dynamic_sensorArray_list.setAdapter(adapter);
		} else {
			adapter.upData(getRelayouts(sensors));
			adapter.notifyDataSetChanged();
		}
	}

	private List<RelativeLayout> getRelayouts(ArrayList<Sensor> sensors) {
		List<RelativeLayout> layouts = new ArrayList<RelativeLayout>();
		Sensor sensor = null;
		RelativeLayout rootView = null;
		for (int i = 0; i < sensors.size(); i++) {
			sensor = sensors.get(i);
			rootView = (RelativeLayout) inflater.inflate(
					R.layout.list_view_item_for_dynamic_sensor_list, null);
			((TextView) rootView.findViewById(R.id.textView_index))
					.setText(sensor.getIcq() + "");
			((TextView) rootView.findViewById(R.id.textView_code))
					.setText(sensor.getIcq() + " --> " + sensor.getNfcCode());
			((TextView) rootView.findViewById(R.id.textView_description))
					.setVisibility(View.GONE);
			layouts.add(rootView);
		}
		return layouts;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 请求刷新
		fetchThisBasestation();
	}

	/**
	 * 请求网络，获取基站下的节点阵列列表
	 */
	private void fetchSensorarrayList(BaseStation basestation) {
		startProgress();
		AppContext.getInstance().getVolleyTools()
				.dynamicFetchSensorarrayInfo(handler, basestation);
	}

	private void fetchThisBasestation() {
		if (currentBasestation != null) {
			startProgress();
			AppContext
					.getInstance()
					.getVolleyTools()
					.fetchBasestationInfo(handler, currentBasestation.getCode());
		}
	}

	private void startProgress() {
		actionbar_progressBar.setIndeterminate(false);
		actionbar_progressBar.setVisibility(View.VISIBLE);
		findViewById(R.id.actionbar_refresh_button).setVisibility(View.GONE);
	}

	private void stopProgress() {
		actionbar_progressBar.setIndeterminate(true);
		actionbar_progressBar.setVisibility(View.GONE);
		findViewById(R.id.actionbar_refresh_button).setVisibility(View.VISIBLE);
	}
}
