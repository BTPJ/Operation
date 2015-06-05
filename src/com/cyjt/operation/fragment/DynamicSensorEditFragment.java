package com.cyjt.operation.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cyjt.operation.R;
import com.cyjt.operation.bean.Sensor;

/**
 * 这个Fragment用于编辑基站的信息
 * 
 * @author kullo<BR>
 *         2014-10-14 上午9:12:10<BR>
 */
public class DynamicSensorEditFragment extends Fragment {
	private Sensor currentSensor;
	/** 界面根 */
	private View rootView;
	private TextView textView_dynamic_sensor_code_edit;
	private TextView textView_dynamic_sensor_edit_delete_button;
	private TextView textView_dynamic_sensor_icq_edit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_dynamic_sensor_edit,
				container, false);
		textView_dynamic_sensor_code_edit = (TextView) rootView
				.findViewById(R.id.textView_dynamic_sensor_code_edit);
		textView_dynamic_sensor_edit_delete_button = (TextView) rootView
				.findViewById(R.id.textView_dynamic_sensor_edit_delete_button);
		textView_dynamic_sensor_icq_edit = (TextView) rootView
				.findViewById(R.id.textView_dynamic_sensor_icq_edit);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments().getSerializable("sensor") == null) {
			rootView.setVisibility(View.GONE);
			Toast.makeText(getActivity(), "出错了,节点对象不存在", Toast.LENGTH_LONG)
					.show();
			return;
		}
		currentSensor = (Sensor) getArguments().getSerializable("sensor");
		viewEvent();
		fillUI(currentSensor);
	}

	private void fillUI(Sensor sensor) {
		textView_dynamic_sensor_code_edit.setText("" + sensor.getNfcCode());
		textView_dynamic_sensor_icq_edit.setText("" + sensor.getIcq());
	}

	private void viewEvent() {
		textView_dynamic_sensor_edit_delete_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 提交
						listener.onSensorEditFragmentDeleteBeClicked(currentSensor);
					}
				});
	}

	public void refreshLaneArray(Sensor sensor) {
		if (sensor == null)
			return;
		this.currentSensor = sensor;
		fillUI(currentSensor);
	}

	private SensorEditFragListener listener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (SensorEditFragListener) activity;
	}

	public interface SensorEditFragListener {

		/** 点击删除按钮后会执行 */
		public void onSensorEditFragmentDeleteBeClicked(Sensor sensor);
	}
}
