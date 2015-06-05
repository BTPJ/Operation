package com.cyjt.operation.fragment;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cyjt.operation.R;
import com.cyjt.operation.bean.BaseStation;
import com.cyjt.operation.bean.LaneArray;
import com.cyjt.operation.bean.Sensor;

/**
 * ���Fragment������ӽڵ����Ϣ
 * 
 * @author kullo<BR>
 *         2014-10-14 ����9:12:10<BR>
 */
public class DynamicSensorAddFragment extends Fragment {
	private LayoutInflater inflater;
	private int sensorIndex = -1;
	private ArrayList<Sensor> sensors;
	private LaneArray currentLaneArray;

	/** ����� */
	private View rootView;
	private RelativeLayout relativeLayout_dynamic_sensor_add_lane_array_button;
	private TextView textView_dynamic_sensor_add_lane_array_code;
	private TextView textView_dynamic_sensor_add_lane_array_describe;
	private RelativeLayout relativeLayout_dynamic_sensor_add_basestation_button;
	private TextView textView_dynamic_sensor_add_basestation_code;
	private TextView textView_dynamic_sensor_add_basestation_describe;
	private RelativeLayout relativeLayout_dynamic_sensor_add_support_basestation_button;
	private TextView textView_dynamic_sensor_add_support_basestation_code;
	private LinearLayout linearLayout_dynamic_sensor_add_sensor_list;
	private LinearLayout linearLayout_dynamic_sensor_add_sensor_button;
	private TextView textView_dynamic_sensor_add_submit_button;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		rootView = inflater.inflate(R.layout.fragment_dynamic_sensor_add,
				container, false);
		relativeLayout_dynamic_sensor_add_lane_array_button = (RelativeLayout) rootView
				.findViewById(R.id.relativeLayout_dynamic_sensor_add_lane_array_button);
		relativeLayout_dynamic_sensor_add_basestation_button = (RelativeLayout) rootView
				.findViewById(R.id.relativeLayout_dynamic_sensor_add_basestation_button);
		relativeLayout_dynamic_sensor_add_support_basestation_button = (RelativeLayout) rootView
				.findViewById(R.id.relativeLayout_dynamic_sensor_add_support_basestation_button);
		textView_dynamic_sensor_add_lane_array_code = (TextView) rootView
				.findViewById(R.id.textView_dynamic_sensor_add_lane_array_code);
		textView_dynamic_sensor_add_lane_array_describe = (TextView) rootView
				.findViewById(R.id.textView_dynamic_sensor_add_lane_array_describe);
		textView_dynamic_sensor_add_basestation_code = (TextView) rootView
				.findViewById(R.id.textView_dynamic_sensor_add_basestation_code);
		textView_dynamic_sensor_add_basestation_describe = (TextView) rootView
				.findViewById(R.id.textView_dynamic_sensor_add_basestation_describe);
		textView_dynamic_sensor_add_support_basestation_code = (TextView) rootView
				.findViewById(R.id.textView_dynamic_sensor_add_support_basestation_code);
		linearLayout_dynamic_sensor_add_sensor_list = (LinearLayout) rootView
				.findViewById(R.id.linearLayout_dynamic_sensor_add_sensor_list);
		linearLayout_dynamic_sensor_add_sensor_button = (LinearLayout) rootView
				.findViewById(R.id.linearLayout_dynamic_sensor_add_sensor_button);
		textView_dynamic_sensor_add_submit_button = (TextView) rootView
				.findViewById(R.id.textView_dynamic_sensor_add_submit_button);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments().getSerializable("laneArray") == null) {
			rootView.setVisibility(View.GONE);
			Toast.makeText(getActivity(), "������,�ڵ���󲻴���", Toast.LENGTH_LONG)
					.show();
			return;
		}
		currentLaneArray = (LaneArray) getArguments().getSerializable(
				"laneArray");
		viewEvent();
		fillLaneArray(currentLaneArray);
		// Ĭ��addһ��
		addSensor(false);
	}

	private void fillLaneArray(LaneArray laneArray) {
		textView_dynamic_sensor_add_lane_array_code.setText("���룺"
				+ laneArray.getCode());
		textView_dynamic_sensor_add_lane_array_describe.setText("������"
				+ laneArray.getDescription());
	}

	private void fillBaseStation(BaseStation baseStation) {
		textView_dynamic_sensor_add_basestation_code.setText("���룺"
				+ baseStation.getCode());
		textView_dynamic_sensor_add_basestation_describe.setText("������"
				+ baseStation.getDescription());
	}

	private void fillSupportBaseStation(BaseStation supportBaseStation) {
		textView_dynamic_sensor_add_support_basestation_code.setText("���룺"
				+ supportBaseStation.getCode());
	}

	private void viewEvent() {
		relativeLayout_dynamic_sensor_add_lane_array_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						listener.onSensorAddFragmentCheckLaneArrayBeClicked();
					}
				});
		relativeLayout_dynamic_sensor_add_basestation_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						listener.onSensorAddFragmentChangeBasestationBeClicked();
					}
				});
		relativeLayout_dynamic_sensor_add_support_basestation_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						listener.onSensorAddFragmentChangeSupportBasestationBeClicked();
					}
				});
		linearLayout_dynamic_sensor_add_sensor_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						addSensor(false);
					}
				});
		textView_dynamic_sensor_add_submit_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// ������һ��
						addSensor(true);
						listener.onSensorAddFragmentSubmitBeClicked(sensors);
					}
				});
	}

	public void refreshLaneArray(LaneArray laneArray) {
		if (laneArray == null)
			return;
		this.currentLaneArray = laneArray;
		fillLaneArray(currentLaneArray);
	}

	public void refreshBaseStation(BaseStation baseStation) {
		if (baseStation == null)
			return;
		fillBaseStation(baseStation);
	}

	public void refreshSupportBaseStation(BaseStation supportBaseStation) {
		if (supportBaseStation == null)
			return;
		fillSupportBaseStation(supportBaseStation);
	}

	public void addNfcCode(String nfcCode) {
		if (linearLayout_dynamic_sensor_add_sensor_list.getChildCount() > 0) {
			RelativeLayout rL = (RelativeLayout) linearLayout_dynamic_sensor_add_sensor_list
					.getChildAt(sensorIndex);
			((EditText) rL.findViewById(R.id.editText)).setText(nfcCode);
			addSensor(false);
		}
	}

	public void submitSensorsError() {
		sensorIndex--;
	}

	/**
	 * * ��linearLayout_dynamic_sensor_add_sensor_list����ӽڵ�ı༭��<BR>
	 * �ύǰ���ô˷���һ�Σ���֤���һ����ӵĽڵ��ܽ�������
	 * 
	 * @param last
	 *            true��ʾ�������һ�ε��ã�Ĭ��false����
	 */
	private void addSensor(boolean last) {
		sensorIndex++;
		if (sensorIndex != 0) {
			if (linearLayout_dynamic_sensor_add_sensor_list.getChildCount() > 0) {
				RelativeLayout rL = (RelativeLayout) linearLayout_dynamic_sensor_add_sensor_list
						.getChildAt(sensorIndex - 1);
				if (rL == null)
					return;
				String code = "";
				String traffic_number = "";
				String traffic_or = "";
				String editText_build_name = "";
				int icq = -1;
				if (((EditText) rL.findViewById(R.id.editText)).getText() != null) {
					code = ((EditText) rL.findViewById(R.id.editText))
							.getText().toString();
					icq = sensorIndex;
				}
				if (((EditText) rL.findViewById(R.id.editText_traffic_number))
						.getText() != null) {
					traffic_number = ((EditText) rL
							.findViewById(R.id.editText_traffic_number))
							.getText().toString();
				}
				if (((EditText) rL.findViewById(R.id.editText_build_name))
						.getText() != null) {
					editText_build_name = ((EditText) rL
							.findViewById(R.id.editText_build_name)).getText()
							.toString();
				}
				if (((RadioButton) rL
						.findViewById(R.id.orientation_traffic_e2w))
						.isChecked()) {
					traffic_or = "������";
				} else if (((RadioButton) rL
						.findViewById(R.id.orientation_traffic_s2n))
						.isChecked()) {
					traffic_or = "�ϱ���";
				}

				if (!("").equals(code) && !("").equals(traffic_number)
						&& !("").equals(editText_build_name)
						&& !("").equals(traffic_or)) {
					Toast.makeText(getActivity(), "�ڵ�" + code + "�ѱ���",
							Toast.LENGTH_SHORT).show();
					if (sensors == null) {
						sensors = new ArrayList<Sensor>();
					}
					sensors.add(new Sensor(-1, code, code, code, -1, icq,
							new Date(), traffic_or + "-" + editText_build_name,
							Integer.parseInt(traffic_number)));
				} else {
					Toast.makeText(getActivity(), "���нڵ�δ���", Toast.LENGTH_SHORT)
							.show();
					sensorIndex--;
					return;
				}
			}
		}
		if (last) {
			sensorIndex--;
			return;
		}
		RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(
				R.layout.layout_for_dynamic_add_sensor, null);
		relativeLayout.setTag(sensorIndex);
		((TextView) relativeLayout.findViewById(R.id.textView_icq_edit))
				.setText(sensorIndex + 1 + "");
		linearLayout_dynamic_sensor_add_sensor_list.addView(relativeLayout,
				sensorIndex);
	}

	private SensorAddFragListener listener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (SensorAddFragListener) activity;
	}

	public interface SensorAddFragListener {

		/** �����ɰ�ť���ִ�� */
		public void onSensorAddFragmentSubmitBeClicked(ArrayList<Sensor> sensors);

		/** �����ʽ��վ���ֺ��ִ�� */
		public void onSensorAddFragmentChangeBasestationBeClicked();

		/** ��������վ���ֺ��ִ�� */
		public void onSensorAddFragmentChangeSupportBasestationBeClicked();

		/** ����������в��ֺ��ִ�� */
		public void onSensorAddFragmentCheckLaneArrayBeClicked();
	}
}
