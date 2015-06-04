package com.cyjt.operation.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cyjt.operation.R;
import com.cyjt.operation.bean.BaseStation;

/**
 * ���Fragment���ڱ༭��վ����Ϣ
 * 
 * @author kullo<BR>
 *         2014-10-14 ����9:12:10<BR>
 */
public class BaseStationEditFragment extends Fragment {
	private BaseStation currentBaseStation;
	/** ����� */
	private View rootView;
	private EditText textView_dynamic_lane_array_code_edit;
	private EditText textView_dynamic_lane_array_describe_edit;
	private RelativeLayout relativeLayout_dynamic_lane_array_address_button;
	private TextView textView_dynamic_lane_array_address_longitude;
	private TextView textView_dynamic_lane_array_address_latitude;
	private TextView textView_dynamic_lane_array_address_String;
	private TextView textView_dynamic_lane_array_code_edit_button;
	private TextView textView_dynamic_lane_array_edit_submit_button;
	private TextView textView_dynamic_lane_array_edit_delete_button;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_basestation_edit,
				container, false);
		textView_dynamic_lane_array_code_edit = (EditText) rootView
				.findViewById(R.id.textView_dynamic_lane_array_code_edit);
		textView_dynamic_lane_array_code_edit.setEnabled(false);
		textView_dynamic_lane_array_describe_edit = (EditText) rootView
				.findViewById(R.id.textView_dynamic_lane_array_describe_edit);
		relativeLayout_dynamic_lane_array_address_button = (RelativeLayout) rootView
				.findViewById(R.id.relativeLayout_dynamic_lane_array_address_button);
		textView_dynamic_lane_array_address_longitude = (TextView) rootView
				.findViewById(R.id.textView_dynamic_lane_array_address_longitude);
		textView_dynamic_lane_array_address_latitude = (TextView) rootView
				.findViewById(R.id.textView_dynamic_lane_array_address_latitude);
		textView_dynamic_lane_array_address_String = (TextView) rootView
				.findViewById(R.id.textView_dynamic_lane_array_address_String);
		textView_dynamic_lane_array_code_edit_button = (TextView) rootView
				.findViewById(R.id.textView_dynamic_lane_array_code_edit_button);
		textView_dynamic_lane_array_edit_submit_button = (TextView) rootView
				.findViewById(R.id.textView_dynamic_lane_array_edit_submit_button);
		textView_dynamic_lane_array_edit_delete_button = (TextView) rootView
				.findViewById(R.id.textView_dynamic_lane_array_edit_delete_button);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments().getSerializable("baseStation") == null) {
			rootView.setVisibility(View.GONE);
			Toast.makeText(getActivity(), "������,��վ���󲻴���", Toast.LENGTH_LONG)
					.show();
			return;
		}
		currentBaseStation = (BaseStation) getArguments().getSerializable(
				"baseStation");
		viewEvent();
		if (currentBaseStation.getId() == -1) {
			textView_dynamic_lane_array_edit_delete_button
					.setVisibility(View.GONE);
		} else {
			textView_dynamic_lane_array_code_edit_button
					.setVisibility(View.GONE);
		}
		fillInfo(currentBaseStation);
	}

	private void fillInfo(BaseStation ba) {
		textView_dynamic_lane_array_code_edit.setText("" + ba.getCode());
		textView_dynamic_lane_array_describe_edit.setText(""
				+ ba.getDescription());
		fillLanLon(ba);

	}

	private void fillLanLon(BaseStation ba) {
		textView_dynamic_lane_array_address_longitude.setText("���ȣ�"
				+ ba.getLon());
		textView_dynamic_lane_array_address_latitude.setText("γ�ȣ�"
				+ ba.getLat());
		textView_dynamic_lane_array_address_String.setText("��ַ��");
	}

	private void viewEvent() {
		relativeLayout_dynamic_lane_array_address_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						saveBaseStation();
						// ���õ�ͼ
						listener.onMapEditBeClicked(currentBaseStation);
					}
				});
		textView_dynamic_lane_array_code_edit_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// ���ö�ά��
						listener.onZxingButtonBeClicked();
					}
				});
		textView_dynamic_lane_array_edit_submit_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// ��ȡ�༭��Ļ�վ
						currentBaseStation.setCode(""
								+ textView_dynamic_lane_array_code_edit
										.getText().toString());
						currentBaseStation.setNfcCode(""
								+ textView_dynamic_lane_array_code_edit
										.getText().toString());
						currentBaseStation.setDescription(""
								+ textView_dynamic_lane_array_describe_edit
										.getText().toString());
						// �ύ
						listener.onSubmitBeClicked(currentBaseStation);
					}
				});
		textView_dynamic_lane_array_edit_delete_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// �ύ
						listener.onDeleteBeClicked(currentBaseStation);
					}
				});
		textView_dynamic_lane_array_code_edit
				.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						// TODO Auto-generated method stub

					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
						// TODO Auto-generated method stub

					}

					@Override
					public void afterTextChanged(Editable s) {
						// TODO Auto-generated method stub

					}
				});
		textView_dynamic_lane_array_describe_edit
				.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						// TODO Auto-generated method stub
						currentBaseStation.setDescription(s.toString());
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
						// TODO Auto-generated method stub

					}

					@Override
					public void afterTextChanged(Editable s) {
						// TODO Auto-generated method stub

					}
				});
	}

	public void refreshBaseStation(BaseStation baseStation) {
		if (baseStation == null)
			return;
		this.currentBaseStation = baseStation;
		fillInfo(currentBaseStation);
	}

	private void saveBaseStation() {
		currentBaseStation.setCode(textView_dynamic_lane_array_code_edit
				.getText().toString());
		currentBaseStation
				.setDescription(textView_dynamic_lane_array_describe_edit
						.getText().toString());
	}

	// public void refreshLaneArrayCode(String code) {
	// if (laneArray == null)
	// return;
	// this.currentLaneArray = laneArray;
	// fillUI(currentLaneArray);
	// }
	//
	// public void refreshLaneArrayAddress(LaneArray laneArray) {
	// if (laneArray == null)
	// return;
	// this.currentLaneArray = laneArray;
	// fillUI(currentLaneArray);
	// }

	private BaseStationEidtFragListener listener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (BaseStationEidtFragListener) activity;
	}

	public interface BaseStationEidtFragListener {
		/** �����ͼ���ֺ��ִ�� */
		public void onMapEditBeClicked(BaseStation baseStation);

		/** �����ɰ�ť���ִ�� */
		public void onSubmitBeClicked(BaseStation baseStation);

		/** �����ɰ�ť���ִ�� */
		public void onDeleteBeClicked(BaseStation baseStation);

		/** ���ö�ά��ɨ�� */
		public void onZxingButtonBeClicked();

	}
}
