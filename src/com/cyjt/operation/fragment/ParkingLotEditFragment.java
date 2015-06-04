package com.cyjt.operation.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cyjt.operation.R;
import com.cyjt.operation.bean.ParkingLot;

/**
 * 这个Fragment用于编辑基站的信息
 * 
 * @author kullo<BR>
 *         2014-10-14 上午9:12:10<BR>
 */
public class ParkingLotEditFragment extends Fragment {
	private ParkingLot currentParkingLot;
	/** 界面根 */
	private View rootView;
	private TextView textView_parkinglot_code_edit;
	private TextView textView_sensor_code_status;
	private TextView textView_sensor1_code_status;
	private EditText editText_sensor_code;
	private EditText editText_sensor1_code; 
	private TextView textView_parkinglot_edit_submit_button;
	private TextView textView_parkinglot_edit_delete_button;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_parkinglot_edit,
				container, false);
		textView_parkinglot_code_edit = (TextView) rootView
				.findViewById(R.id.textView_parkinglot_code_edit);
		editText_sensor_code = (EditText) rootView
				.findViewById(R.id.editText_sensor_code);
		editText_sensor1_code = (EditText) rootView
				.findViewById(R.id.editText_sensor1_code);
		textView_sensor_code_status = (TextView) rootView
				.findViewById(R.id.textView_sensor_code_status);
		textView_sensor1_code_status = (TextView) rootView
				.findViewById(R.id.textView_sensor1_code_status);
		textView_parkinglot_edit_submit_button = (TextView) rootView
				.findViewById(R.id.textView_parkinglot_edit_submit_button);
		textView_parkinglot_edit_delete_button = (TextView) rootView
				.findViewById(R.id.textView_parkinglot_edit_delete_button);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments().getSerializable("parkingLot") == null) {
			rootView.setVisibility(View.GONE);
			Toast.makeText(getActivity(), "出错了,基站对象不存在", Toast.LENGTH_LONG)
					.show();
			return;
		}
		currentParkingLot = (ParkingLot) getArguments().getSerializable(
				"parkingLot");
		viewEvent();
		fillInfo(currentParkingLot);
	}

	private void viewEvent() {
		textView_parkinglot_edit_submit_button
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						listener.onParkingLotEdited(getCurrentParkingLot());
					}

				});
		textView_parkinglot_edit_delete_button
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						listener.onParkingLotDeleted(getCurrentParkingLot());

					}
				});
		textView_sensor_code_status.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.onSensorCodeBeClicked(0);
			}
		});
		textView_sensor1_code_status.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.onSensorCodeBeClicked(1);
			}
		});
	}

	private void fillInfo(ParkingLot pl) {
		textView_parkinglot_code_edit.setText("" + currentParkingLot.getCode());
		editText_sensor_code.setText("" + currentParkingLot.getSensorCode());
		editText_sensor1_code.setText("" + currentParkingLot.getSensorCode1());
		if (("").equals(currentParkingLot.getSensorCode())) {
			textView_sensor_code_status.setText("+");
			textView_sensor_code_status
					.setBackgroundResource(R.drawable.fillet_shape_background_for_list_item_circle_green);
		} else {
			textView_sensor_code_status.setText("一");
			textView_sensor_code_status
					.setBackgroundResource(R.drawable.fillet_shape_background_for_list_item_circle_orange);

		}
		if (("").equals(currentParkingLot.getSensorCode1())) {
			textView_sensor1_code_status.setText("+");
			textView_sensor1_code_status
					.setBackgroundResource(R.drawable.fillet_shape_background_for_list_item_circle_green);

		} else {
			textView_sensor1_code_status.setText("一");
			textView_sensor1_code_status
					.setBackgroundResource(R.drawable.fillet_shape_background_for_list_item_circle_orange);
		}

	}

	private ParkingLot getCurrentParkingLot() {
		currentParkingLot.setCode(textView_parkinglot_code_edit.getText()
				.toString());
		currentParkingLot.setSensorCode(editText_sensor_code.getText()
				.toString());
		currentParkingLot.setSensorCode1(editText_sensor1_code.getText()
				.toString());
		return currentParkingLot;
	}

	public void refreshParkingLot(ParkingLot parkingLot) {
		if (parkingLot == null)
			return;
		this.currentParkingLot = parkingLot;
		fillInfo(currentParkingLot);
	}

	private ParkingLotEditFragListener listener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (ParkingLotEditFragListener) activity;
	}

	public interface ParkingLotEditFragListener {
		/** 获取所属基站信息 */
		public void onSensorCodeBeClicked(int index);

		public void onParkingLotEdited(ParkingLot parkinglot);

		public void onParkingLotDeleted(ParkingLot parkinglot);

	}
}
