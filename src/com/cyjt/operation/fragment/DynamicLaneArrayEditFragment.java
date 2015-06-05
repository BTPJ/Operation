package com.cyjt.operation.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cyjt.operation.R;
import com.cyjt.operation.base.Constants;
import com.cyjt.operation.bean.LaneArray;

/**
 * 这个Fragment用于编辑基站的信息
 * 
 * @author kullo<BR>
 *         2014-10-14 上午9:12:10<BR>
 */
public class DynamicLaneArrayEditFragment extends Fragment {
	private LaneArray currentLaneArray;
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
	private Spinner textView_dynamic_lane_array_location_flag_edit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_dynamic_lane_array_edit,
				container, false);
		textView_dynamic_lane_array_code_edit = (EditText) rootView
				.findViewById(R.id.textView_dynamic_lane_array_code_edit);
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
		textView_dynamic_lane_array_location_flag_edit = (Spinner) rootView
				.findViewById(R.id.textView_dynamic_lane_array_location_flag_edit);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments().getSerializable("laneArray") == null) {
			rootView.setVisibility(View.GONE);
			Toast.makeText(getActivity(), "出错了,阵列对象不存在", Toast.LENGTH_LONG)
					.show();
			return;
		}
		currentLaneArray = (LaneArray) getArguments().getSerializable(
				"laneArray");
		viewEvent();
		if (currentLaneArray.getId() == -1) {
			textView_dynamic_lane_array_edit_delete_button
					.setVisibility(View.GONE);
		}
		fillInfo(currentLaneArray);
	}

	private void fillInfo(LaneArray la) {
		textView_dynamic_lane_array_code_edit.setText("" + la.getCode());
		textView_dynamic_lane_array_describe_edit.setText(""
				+ la.getDescription());
		fillLanLon(la);
		fillLocation(la);
	}

	private ArrayAdapter<String> spinnerAdapter = null;

	private void initLocationAdapter() {
		if (spinnerAdapter == null) {
			spinnerAdapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_spinner_item,
					Constants.LOCATION_ARRAY);
			spinnerAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			textView_dynamic_lane_array_location_flag_edit
					.setAdapter(spinnerAdapter);
		}
	}

	private void fillLocation(LaneArray la) {
		initLocationAdapter();
		int position = 0;
		if (!la.getLocation().equals("")) {
			position = (int) la.getLocation().charAt(0) - 64;
		} else {

		}
		textView_dynamic_lane_array_location_flag_edit.setSelection(position);
	}

	private void fillLanLon(LaneArray la) {
		textView_dynamic_lane_array_address_longitude.setText("经度："
				+ la.getLon());
		textView_dynamic_lane_array_address_latitude.setText("纬度："
				+ la.getLat());
		textView_dynamic_lane_array_address_String.setText("位置：");
	}

	private void viewEvent() {
		textView_dynamic_lane_array_location_flag_edit
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View view,
							int position, long id) {

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});
		relativeLayout_dynamic_lane_array_address_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						saveLaneArray();
						// 调用地图
						listener.onMapEditBeClicked(currentLaneArray);
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
						// 获取编辑后的阵列
						int position = textView_dynamic_lane_array_location_flag_edit
								.getSelectedItemPosition();
						saveLaneArray();
						if (position == 0) {
							Toast.makeText(getActivity(), "请设置对应标识",
									Toast.LENGTH_SHORT).show();
							return;
						}

						// 提交
						listener.onSubmitBeClicked(currentLaneArray);
						Log.v("demo", "" + currentLaneArray.getLocation());
					}
				});
		textView_dynamic_lane_array_edit_delete_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 提交
						listener.onDeleteBeClicked(currentLaneArray);
					}
				});
		textView_dynamic_lane_array_code_edit
				.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {

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

	public void refreshLaneArray(LaneArray laneArray) {
		if (laneArray == null)
			return;
		this.currentLaneArray = laneArray;
		fillInfo(currentLaneArray);
	}

	private void saveLaneArray() {
		currentLaneArray.setCode(textView_dynamic_lane_array_code_edit
				.getText().toString());
		currentLaneArray
				.setDescription(textView_dynamic_lane_array_describe_edit
						.getText().toString());
		int position = textView_dynamic_lane_array_location_flag_edit
				.getSelectedItemPosition();
		if (position != 0) {
			currentLaneArray.setLocation(Constants.LOCATION_ARRAY[position]);
		}
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

	private LaneArrayEidtFragListener listener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (LaneArrayEidtFragListener) activity;
	}

	public interface LaneArrayEidtFragListener {
		/** 点击地图部分后会执行 */
		public void onMapEditBeClicked(LaneArray laneArray);

		/** 点击完成按钮后会执行 */
		public void onSubmitBeClicked(LaneArray laneArray);

		/** 点击完成按钮后会执行 */
		public void onDeleteBeClicked(LaneArray laneArray);

		/** 调用二维码扫描 */
		public void onZxingButtonBeClicked();

	}
}
