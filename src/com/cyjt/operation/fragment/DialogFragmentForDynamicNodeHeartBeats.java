package com.cyjt.operation.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.cyjt.operation.bean.DynamicNodeHeartBeat;
import com.cyjt.operation.bean.Sensor;

public class DialogFragmentForDynamicNodeHeartBeats extends DialogFragment {
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
			case HandlerMessageCodes.FETCH_DYNAMIC_NODE_DATA_SUCCEED:
				// 获取到心跳后将心跳展现出来
				ArrayList<DynamicNodeHeartBeat> nodeHeartBeats = (ArrayList<DynamicNodeHeartBeat>) msg.obj;
				// 填充数据
				initOrUpAdapterData(nodeHeartBeats);
				break;
			case HandlerMessageCodes.FETCH_DYNAMIC_NODE_DATA_FAILED:
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

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments().getString("sensorCode") == null) {
			return;
		}
		sensorCode = getArguments().getString("sensorCode");
		currentSensor = new Sensor();
		currentSensor.setCode(sensorCode);
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

	private void initOrUpAdapterData(ArrayList<DynamicNodeHeartBeat> nodeHeartBeats) {
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
			ArrayList<DynamicNodeHeartBeat> nodeHeartBeats) {
		List<RelativeLayout> views = new ArrayList<RelativeLayout>();
		for (DynamicNodeHeartBeat z : nodeHeartBeats) {
			rootView = (RelativeLayout) inflater.inflate(
					R.layout.list_view_item_for_z_values, null);

			((TextView) rootView.findViewById(R.id.textView_for_sensor_z_value))
					.setText("序列：" + z.getSeq());
			((TextView) rootView
					.findViewById(R.id.textView_for_sensor_z_value_time))
					.setText("上报时间：" + z.getSubmitAtStringFromNow() + "\nTime："
							+ z.getSubmitAtString());
			((TextView) rootView.findViewById(R.id.textView_for_sensor_voltage))
					.setText("占用时长：" + z.getOccupyTime()+ "\n采样时长："
							+ z.getSamplingTime());
			((TextView) rootView.findViewById(R.id.textView_for_sensor_signal))
					.setText("车辆数：" + z.getCarCount() + "\n车头时距："+((int)(((z.getOccupyTime()*1.0f)/(z.getCarCount()*1.0f))*100))*1.0f/100);
			views.add(rootView);
		}
		return views;
	}

	private void getSensorZValue(Sensor sensor) {
			AppContext.getInstance().getVolleyTools()
					.fetchDynamicNodeData(handler, sensor);
	}
}
