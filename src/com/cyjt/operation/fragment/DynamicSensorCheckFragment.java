package com.cyjt.operation.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cyjt.operation.R;
import com.cyjt.operation.bean.BaseStation;
import com.cyjt.operation.bean.LaneArray;
import com.cyjt.operation.bean.Sensor;
import com.cyjt.operation.bean.ZValue;

/**
 * 这个Fragment用于展现阵列信息，是个纯静态页面
 * 
 * @author kullo<BR>
 *         2014-10-14 上午9:12:10<BR>
 */
public class DynamicSensorCheckFragment extends Fragment {
	/** 界面根 */
	private View rootView;
	private TextView textView_dynamic_sensor_code_edit;
	private RelativeLayout relativeLayout_dynamic_sensor_belong_to_Lane_array_check_button;
	private RelativeLayout relativeLayout_dynamic_sensor_belong_to_besestation_check_button;
	private TextView textView_dynamic_sensor_belong_to_Lane_array_code;
	private TextView textView_dynamic_sensor_belong_to_Lane_array_describe;
	private TextView textView_dynamic_sensor_belong_to_besestation_code;
	private TextView textView_dynamic_sensor_belong_to_besestation_describe;
	private TextView textView_dynamic_sensor_creat_at_edit;
	private TextView textView_dynamic_sensor_icq_edit;
	private TextView textView_dynamic_sensor_reset_button;
	private TextView textView_dynamic_sensor_node_edit;
	private LinearLayout linearLayout_dynamic_sensor_zvalue;
	private LayoutInflater inflater;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		rootView = inflater.inflate(R.layout.fragment_dynamic_sensor_check,
				container, false);
		linearLayout_dynamic_sensor_zvalue = (LinearLayout) rootView
				.findViewById(R.id.linearLayout_dynamic_sensor_zvalue);
		textView_dynamic_sensor_code_edit = (TextView) rootView
				.findViewById(R.id.textView_dynamic_sensor_code_edit);
		textView_dynamic_sensor_node_edit = (TextView) rootView
				.findViewById(R.id.textView_dynamic_sensor_node_edit);
		textView_dynamic_sensor_reset_button = (TextView) rootView
				.findViewById(R.id.textView_dynamic_sensor_reset_button);

		textView_dynamic_sensor_belong_to_Lane_array_code = (TextView) rootView
				.findViewById(R.id.textView_dynamic_sensor_belong_to_Lane_array_code);

		textView_dynamic_sensor_belong_to_Lane_array_describe = (TextView) rootView
				.findViewById(R.id.textView_dynamic_sensor_belong_to_Lane_array_describe);

		textView_dynamic_sensor_belong_to_besestation_code = (TextView) rootView
				.findViewById(R.id.textView_dynamic_sensor_belong_to_besestation_code);

		textView_dynamic_sensor_belong_to_besestation_describe = (TextView) rootView
				.findViewById(R.id.textView_dynamic_sensor_belong_to_besestation_describe);

		textView_dynamic_sensor_icq_edit = (TextView) rootView
				.findViewById(R.id.textView_dynamic_sensor_icq_edit);
		textView_dynamic_sensor_creat_at_edit = (TextView) rootView
				.findViewById(R.id.textView_dynamic_sensor_creat_at_edit);
		relativeLayout_dynamic_sensor_belong_to_Lane_array_check_button = (RelativeLayout) rootView
				.findViewById(R.id.relativeLayout_dynamic_sensor_belong_to_Lane_array_check_button);
		relativeLayout_dynamic_sensor_belong_to_besestation_check_button = (RelativeLayout) rootView
				.findViewById(R.id.relativeLayout_dynamic_sensor_belong_to_besestation_check_button);
		// 默认俩控件不可见
		// 只有刷新到数据后才可见
		relativeLayout_dynamic_sensor_belong_to_Lane_array_check_button
				.setVisibility(View.GONE);
		relativeLayout_dynamic_sensor_belong_to_besestation_check_button
				.setVisibility(View.GONE);

		return rootView;
	}

	private Sensor currentSensor;

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
		if ((LaneArray) getArguments().getSerializable("laneArray") != null) {
			refreshBelongLaneArray((LaneArray) getArguments().getSerializable(
					"laneArray"));
		}
		listener.onSensorCheckFragmentCreated();
		listener.onSensorCheckFragmentCreatedRefreashZvalues();
	}

	private void viewEvent() {
		relativeLayout_dynamic_sensor_belong_to_Lane_array_check_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 查看所属阵列
						listener.onSensorCheckFragmentBelongLaneArrayBeClicked();
					}
				});
		textView_dynamic_sensor_reset_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 查看所属阵列
						listener.onSensorNeedReset();
					}
				});
		relativeLayout_dynamic_sensor_belong_to_besestation_check_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 查看所属基站
						listener.onSensorCheckFragmentBelongBasestationBeClicked();
					}
				});
		textView_dynamic_sensor_node_edit
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// 打开Fragment，请求接口
						listener.onSensorNodeChecked();
					}
				});
	}

	private void fillUI(Sensor sensor) {
		textView_dynamic_sensor_code_edit.setText("" + sensor.getNfcCode());
		textView_dynamic_sensor_creat_at_edit.setText(""
				+ sensor.getCreatAtString());
		textView_dynamic_sensor_icq_edit.setText("" + sensor.getIcq());
	}

	private void fillBelongLaneArray(LaneArray laneArry) {
		relativeLayout_dynamic_sensor_belong_to_Lane_array_check_button
				.setVisibility(View.VISIBLE);
		textView_dynamic_sensor_belong_to_Lane_array_code.setText("编号："
				+ laneArry.getCode());
		textView_dynamic_sensor_belong_to_Lane_array_describe.setText("描述："
				+ laneArry.getDescription());
	}

	private void fillBelongBeseStation(BaseStation beseStation) {
		relativeLayout_dynamic_sensor_belong_to_besestation_check_button
				.setVisibility(View.VISIBLE);
		textView_dynamic_sensor_belong_to_besestation_code.setText("编号："
				+ beseStation.getCode());
		textView_dynamic_sensor_belong_to_besestation_describe.setText("描述："
				+ beseStation.getDescription());
	}

	/** 更新阵列信息 */
	public void refreshSensor(Sensor sensor) {
		if (sensor == null)
			return;
		this.currentSensor = sensor;
		fillUI(currentSensor);
	}

	/** 更新节点所属的阵列信息 */
	public void refreshBelongLaneArray(LaneArray laneArry) {
		if (laneArry == null)
			return;
		fillBelongLaneArray(laneArry);
	}

	/** 更新节点所属的基站信息 */
	public void refreshBelongBaseStation(BaseStation beseStation) {
		if (beseStation == null)
			return;
		fillBelongBeseStation(beseStation);
	}

	/** 更新节点的地磁值信息 */
	public void refreshZvalueList(ArrayList<ZValue> zValues) {
		Toast.makeText(getActivity(), "正刷新地磁列表", Toast.LENGTH_SHORT).show();
		// 更新地磁列表
		initOrUpAdapterData(zValues);
	}

	private void initOrUpAdapterData(ArrayList<ZValue> zValues) {
		for (ZValue z : zValues) {
			RelativeLayout rootView = (RelativeLayout) inflater.inflate(
					R.layout.list_view_item_for_z_values, null);
			((TextView) rootView.findViewById(R.id.textView_for_sensor_z_value))
					.setText("地磁值：" + z.getEmValueZ());
			((TextView) rootView
					.findViewById(R.id.textView_for_sensor_z_value_time))
					.setText("上报时间：" + z.getzValueTimeFromNow() + "\n"
							+ z.getzValueTimeString());
			((TextView) rootView.findViewById(R.id.textView_for_sensor_signal))
					.setText("信号量：" + z.getSignal());
			((TextView) rootView.findViewById(R.id.textView_for_sensor_voltage))
					.setText("电量：" + z.getVoltagePercentage());
			linearLayout_dynamic_sensor_zvalue.addView(rootView);
		}
	}

	private SensorCheckFragListener listener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (SensorCheckFragListener) activity;
	}

	public interface SensorCheckFragListener {
		/** 获取所属基站信息 */
		public void onSensorCheckFragmentCreated();

		public void onSensorNodeChecked();

		public void onSensorNeedReset();

		/** 获取节点地磁值 */
		public void onSensorCheckFragmentCreatedRefreashZvalues();

		/** 所属阵列部分被点击后会调用 */
		public void onSensorCheckFragmentBelongLaneArrayBeClicked();

		/** 所属基站部分被点击后会调用 */
		public void onSensorCheckFragmentBelongBasestationBeClicked();

	}
}
