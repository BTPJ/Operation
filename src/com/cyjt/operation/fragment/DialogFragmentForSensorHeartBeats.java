package com.cyjt.operation.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cyjt.operation.R;
import com.cyjt.operation.adapter.CustomBaseAdapter;
import com.cyjt.operation.base.AppConfig;
import com.cyjt.operation.base.AppContext;
import com.cyjt.operation.base.HandlerMessageCodes;
import com.cyjt.operation.bean.NewNodeHeartBeat;
import com.cyjt.operation.bean.Sensor;
import com.cyjt.operation.bean.SensorSetRequest;

/**
 * 获取节点心跳信息的Fragment
 * 
 * @author LTP
 *
 */
public class DialogFragmentForSensorHeartBeats extends DialogFragment {
	/** 图层过滤器 */
	private LayoutInflater inflater;
	private ListView listView_for_z_values;
	private CustomBaseAdapter<RelativeLayout> adapter;
	private Sensor currentSensor;
	private String sensorCode;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerMessageCodes.HTTP_BUILDER_FETCH_NODE_HEART_BEAT_SUCCEED:
				Log.d("DialogFragmentForSensorHeartBeats",
						"HTTP_BUILDER_FETCH_NODE_HEART_BEAT_SUCCEED");
				// 获取到心跳后将心跳展现出来
				ArrayList<NewNodeHeartBeat> nodeHeartBeats = (ArrayList<NewNodeHeartBeat>) msg.obj;
				// 将nodeHeartBeats排序
				// Collections.sort(nodeHeartBeats, new
				// ComparatorNodeHeartBeat());
				// 填充数据
				initOrUpAdapterData(nodeHeartBeats);
				break;
			case HandlerMessageCodes.FETCH_SENSOR_SET_REQUEST_SUCCEED:
				// 获取到心跳后将心跳展现出来
				ArrayList<SensorSetRequest> sensorSetRequests = (ArrayList<SensorSetRequest>) msg.obj;
				// 填充数据
				initOrUpAdapterDataSensorSetRequest(sensorSetRequests);
				break;
			case HandlerMessageCodes.FETCH_SENSOR_SET_REQUEST_FAILED:
			case HandlerMessageCodes.HTTP_BUILDER_FETCH_NODE_HEART_BEAT_FAILED:
				Toast.makeText(getActivity(), "暂无数据", Toast.LENGTH_SHORT)
						.show();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		View rootView = inflater.inflate(R.layout.fragment_dynimac_z_values,
				container, false);
		listView_for_z_values = (ListView) rootView
				.findViewById(R.id.listView_for_z_values);
		return rootView;
	}

	private boolean isCurrentLotActivited = false;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments().getString("sensorCode") == null) {
			return;
		}
		isCurrentLotActivited = getArguments().getBoolean(
				"isCurrentLotActivited");
		sensorCode = getArguments().getString("sensorCode");
		currentSensor = new Sensor();
		currentSensor.setCode(sensorCode);
		// currentSensor = (Sensor) getArguments().getSerializable("sensor");
		viewEvent();

	}

	private void viewEvent() {
		// 请求网络

		if (AppConfig.USING_NETWORK) {
			getSensorZValue(currentSensor);
		} else {
			// ======================以下数据用于测试
		}
		listView_for_z_values.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				dismiss();
			}
		});
	}

	private void initOrUpAdapterData(ArrayList<NewNodeHeartBeat> nodeHeartBeats) {
		if (adapter == null) {
			adapter = new CustomBaseAdapter<RelativeLayout>(
					getZValuesList(nodeHeartBeats));
			listView_for_z_values.setAdapter(adapter);
		} else {
			adapter.upData(getZValuesList(nodeHeartBeats));
			adapter.notifyDataSetChanged();
		}
	}

	private RelativeLayout rootView;

	private List<RelativeLayout> getZValuesList(
			ArrayList<NewNodeHeartBeat> nodeHeartBeats) {
		List<RelativeLayout> views = new ArrayList<RelativeLayout>();
		for (NewNodeHeartBeat z : nodeHeartBeats) {
			rootView = (RelativeLayout) inflater.inflate(
					R.layout.list_view_item_for_z_values, null);

			((TextView) rootView.findViewById(R.id.textView_for_sensor_z_value))
					.setText("序列：" + z.getSeq() + "\n保留字段：" + z.getRetain());
			((TextView) rootView
					.findViewById(R.id.textView_for_sensor_z_value_time))
					.setText("上报时间：" + z.getSubmitAtStringFromNow() + "\nTime："
							+ z.getSubmitAtString());
			((TextView) rootView.findViewById(R.id.textView_for_sensor_voltage))
					.setText("电量：" + "0%");
			((TextView) rootView.findViewById(R.id.textView_for_sensor_signal))
					.setText("信号量：" + z.getSignal());
			views.add(rootView);
		}
		return views;
	}

	private void initOrUpAdapterDataSensorSetRequest(
			ArrayList<SensorSetRequest> nodeHeartBeats) {
		if (adapter == null) {
			adapter = new CustomBaseAdapter<RelativeLayout>(
					getZValuesListSensorSetRequest(nodeHeartBeats));
			listView_for_z_values.setAdapter(adapter);
		} else {
			adapter.upData(getZValuesListSensorSetRequest(nodeHeartBeats));
			adapter.notifyDataSetChanged();
		}
	}

	private List<RelativeLayout> getZValuesListSensorSetRequest(
			ArrayList<SensorSetRequest> nodeHeartBeats) {
		List<RelativeLayout> views = new ArrayList<RelativeLayout>();
		for (SensorSetRequest z : nodeHeartBeats) {
			rootView = (RelativeLayout) inflater.inflate(
					R.layout.list_view_item_for_z_values, null);
			((TextView) rootView.findViewById(R.id.textView_for_sensor_z_value))
					.setText("內容：" + z.getContent());
			((TextView) rootView
					.findViewById(R.id.textView_for_sensor_z_value_time))
					.setText("上报时间：" + z.getSubmitAtStringFromNow() + "\nTime："
							+ z.getSubmitAtString());
			rootView.findViewById(R.id.textView_for_sensor_voltage)
					.setVisibility(View.GONE);
			rootView.findViewById(R.id.textView_for_sensor_signal)
					.setVisibility(View.GONE);
			views.add(rootView);
		}
		return views;
	}

	private void getSensorZValue(Sensor sensor) {
		if (isCurrentLotActivited) {
			AppContext.getInstance().getVolleyTools()
					.fetchNodeHeartBeat(handler, sensor);
			Toast.makeText(getActivity(),
					"正在获取" + currentSensor.getNfcCode() + "节点的地磁值",
					Toast.LENGTH_SHORT).show();
		} else {
			AppContext.getInstance().getVolleyTools()
					.fetchSensorSetRequest(handler, sensor);
			Toast.makeText(getActivity(),
					"正在获取" + currentSensor.getNfcCode() + "节点的配置请求信息",
					Toast.LENGTH_SHORT).show();
		}
	}

}
