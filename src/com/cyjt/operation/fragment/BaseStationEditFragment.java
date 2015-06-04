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
 * 这个Fragment用于编辑基站的信息
 * 
 * @author kullo<BR>
 *         2014-10-14 上午9:12:10<BR>
 */
public class BaseStationEditFragment extends Fragment {
	private BaseStation currentBaseStation;
	/** 界面根 */
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
			Toast.makeText(getActivity(), "出错了,基站对象不存在", Toast.LENGTH_LONG)
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
		textView_dynamic_lane_array_address_longitude.setText("经度："
				+ ba.getLon());
		textView_dynamic_lane_array_address_latitude.setText("纬度："
				+ ba.getLat());
		textView_dynamic_lane_array_address_String.setText("地址：");
	}

	private void viewEvent() {
		relativeLayout_dynamic_lane_array_address_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						saveBaseStation();
						// 调用地图
						listener.onMapEditBeClicked(currentBaseStation);
					}
				});
		textView_dynamic_lane_array_code_edit_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 调用二维码
						listener.onZxingButtonBeClicked();
					}
				});
		textView_dynamic_lane_array_edit_submit_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 获取编辑后的基站
						currentBaseStation.setCode(""
								+ textView_dynamic_lane_array_code_edit
										.getText().toString());
						currentBaseStation.setNfcCode(""
								+ textView_dynamic_lane_array_code_edit
										.getText().toString());
						currentBaseStation.setDescription(""
								+ textView_dynamic_lane_array_describe_edit
										.getText().toString());
						// 提交
						listener.onSubmitBeClicked(currentBaseStation);
					}
				});
		textView_dynamic_lane_array_edit_delete_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 提交
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
		/** 点击地图部分后会执行 */
		public void onMapEditBeClicked(BaseStation baseStation);

		/** 点击完成按钮后会执行 */
		public void onSubmitBeClicked(BaseStation baseStation);

		/** 点击完成按钮后会执行 */
		public void onDeleteBeClicked(BaseStation baseStation);

		/** 调用二维码扫描 */
		public void onZxingButtonBeClicked();

	}
}
