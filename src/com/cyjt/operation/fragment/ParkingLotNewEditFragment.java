package com.cyjt.operation.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
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
public class ParkingLotNewEditFragment extends Fragment {
	private ParkingLot currentParkingLot;
	/** 界面根 */
	private View rootView;
	private TextView textView_parkinglot_code_edit;
	private TextView textView_parkinglot_basestation_edit;
	private EditText editText_sensor1_code;
	private TextView textView_parkinglot_edit_submit_button;
	private String nfcCode;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_newparkinglot_edit,
				container, false);
		textView_parkinglot_code_edit = (TextView) rootView
				.findViewById(R.id.textView_parkinglot_code_edit);
		editText_sensor1_code = (EditText) rootView
				.findViewById(R.id.editText_sensor1_code);
		textView_parkinglot_edit_submit_button = (TextView) rootView
				.findViewById(R.id.textView_parkinglot_edit_submit_button);
		textView_parkinglot_basestation_edit = (TextView) rootView
				.findViewById(R.id.textView_parkinglot_basestation_edit);
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
		nfcCode = getArguments().getString("nfcCode");
		Log.i("demo", "nfcCode=" + nfcCode);
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
	}

	private void fillInfo(ParkingLot pl) {
		textView_parkinglot_code_edit.setText("" + pl.getCode());
		editText_sensor1_code.setText("" + pl.getSensorCode1());
		textView_parkinglot_basestation_edit.setText(""
				+ pl.getBasestationNfcCode());
	}

	private ParkingLot getCurrentParkingLot() {
		currentParkingLot.setCode(textView_parkinglot_code_edit.getText()
				.toString());
		currentParkingLot.setSensorCode1(editText_sensor1_code.getText()
				.toString());
		currentParkingLot
				.setBasestationNfcCode(textView_parkinglot_basestation_edit
						.getText().toString());
		return currentParkingLot;
	}

	public void refreshParkingLot(ParkingLot parkingLot) {
		if (parkingLot == null)
			return;
		this.currentParkingLot = parkingLot;
		fillInfo(currentParkingLot);
	}

	private ParkingLotNewEditFragListener listener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (ParkingLotNewEditFragListener) activity;
	}

	public interface ParkingLotNewEditFragListener {
		/** 获取所属基站信息 */
		public void onSensorCodeBeClicked(int index);

		public void onParkingLotEdited(ParkingLot parkinglot);

		public void onParkingLotDeleted(ParkingLot parkinglot);

	}
}
