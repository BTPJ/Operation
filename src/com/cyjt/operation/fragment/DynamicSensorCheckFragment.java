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
 * ���Fragment����չ��������Ϣ���Ǹ�����̬ҳ��
 * 
 * @author kullo<BR>
 *         2014-10-14 ����9:12:10<BR>
 */
public class DynamicSensorCheckFragment extends Fragment {
	/** ����� */
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
		// Ĭ�����ؼ����ɼ�
		// ֻ��ˢ�µ����ݺ�ſɼ�
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
			Toast.makeText(getActivity(), "������,�ڵ���󲻴���", Toast.LENGTH_LONG)
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
						// �鿴��������
						listener.onSensorCheckFragmentBelongLaneArrayBeClicked();
					}
				});
		textView_dynamic_sensor_reset_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// �鿴��������
						listener.onSensorNeedReset();
					}
				});
		relativeLayout_dynamic_sensor_belong_to_besestation_check_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// �鿴������վ
						listener.onSensorCheckFragmentBelongBasestationBeClicked();
					}
				});
		textView_dynamic_sensor_node_edit
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// ��Fragment������ӿ�
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
		textView_dynamic_sensor_belong_to_Lane_array_code.setText("��ţ�"
				+ laneArry.getCode());
		textView_dynamic_sensor_belong_to_Lane_array_describe.setText("������"
				+ laneArry.getDescription());
	}

	private void fillBelongBeseStation(BaseStation beseStation) {
		relativeLayout_dynamic_sensor_belong_to_besestation_check_button
				.setVisibility(View.VISIBLE);
		textView_dynamic_sensor_belong_to_besestation_code.setText("��ţ�"
				+ beseStation.getCode());
		textView_dynamic_sensor_belong_to_besestation_describe.setText("������"
				+ beseStation.getDescription());
	}

	/** ����������Ϣ */
	public void refreshSensor(Sensor sensor) {
		if (sensor == null)
			return;
		this.currentSensor = sensor;
		fillUI(currentSensor);
	}

	/** ���½ڵ�������������Ϣ */
	public void refreshBelongLaneArray(LaneArray laneArry) {
		if (laneArry == null)
			return;
		fillBelongLaneArray(laneArry);
	}

	/** ���½ڵ������Ļ�վ��Ϣ */
	public void refreshBelongBaseStation(BaseStation beseStation) {
		if (beseStation == null)
			return;
		fillBelongBeseStation(beseStation);
	}

	/** ���½ڵ�ĵش�ֵ��Ϣ */
	public void refreshZvalueList(ArrayList<ZValue> zValues) {
		Toast.makeText(getActivity(), "��ˢ�µش��б�", Toast.LENGTH_SHORT).show();
		// ���µش��б�
		initOrUpAdapterData(zValues);
	}

	private void initOrUpAdapterData(ArrayList<ZValue> zValues) {
		for (ZValue z : zValues) {
			RelativeLayout rootView = (RelativeLayout) inflater.inflate(
					R.layout.list_view_item_for_z_values, null);
			((TextView) rootView.findViewById(R.id.textView_for_sensor_z_value))
					.setText("�ش�ֵ��" + z.getEmValueZ());
			((TextView) rootView
					.findViewById(R.id.textView_for_sensor_z_value_time))
					.setText("�ϱ�ʱ�䣺" + z.getzValueTimeFromNow() + "\n"
							+ z.getzValueTimeString());
			((TextView) rootView.findViewById(R.id.textView_for_sensor_signal))
					.setText("�ź�����" + z.getSignal());
			((TextView) rootView.findViewById(R.id.textView_for_sensor_voltage))
					.setText("������" + z.getVoltagePercentage());
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
		/** ��ȡ������վ��Ϣ */
		public void onSensorCheckFragmentCreated();

		public void onSensorNodeChecked();

		public void onSensorNeedReset();

		/** ��ȡ�ڵ�ش�ֵ */
		public void onSensorCheckFragmentCreatedRefreashZvalues();

		/** �������в��ֱ���������� */
		public void onSensorCheckFragmentBelongLaneArrayBeClicked();

		/** ������վ���ֱ���������� */
		public void onSensorCheckFragmentBelongBasestationBeClicked();

	}
}
