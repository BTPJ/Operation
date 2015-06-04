package com.cyjt.operation.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
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
import com.cyjt.operation.bean.ParkingLot;

/**
 * 这个Fragment用于展现阵列信息，是个纯静态页面
 * 
 * @author kullo<BR>
 *         2014-10-14 上午9:12:10<BR>
 */
public class ParkingLotCheckFragment extends Fragment {
	private LayoutInflater inflater;
	/** 界面根 */
	private View rootView;
	private ListView listView_sensors;
	private ArrayList<String> sensorCodes = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		rootView = inflater.inflate(R.layout.fragment_parkinglot_check,
				container, false);
		listView_sensors = (ListView) rootView
				.findViewById(R.id.listView_sensors);
		return rootView;
	}

	private ParkingLot currentParkingLot;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments().getSerializable("parkingLot") == null) {
			rootView.setVisibility(View.GONE);
			Toast.makeText(getActivity(), "出错了,车位对象不存在", Toast.LENGTH_LONG)
					.show();
			return;
		}
		currentParkingLot = (ParkingLot) getArguments().getSerializable(
				"parkingLot");
		viewEvent();
		fillUI(currentParkingLot);
		if (currentParkingLot.getCodes() != null
				&& currentParkingLot.getCodes().size() > 0) {
			refreshSensorList(currentParkingLot.getCodes());
		}
	}

	private void viewEvent() {
		listView_sensors.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (sensorCodes != null && sensorCodes.size() > 0) {
					listener.onSensorBeClicked(sensorCodes.get(arg2));
				}
			}
		});
	}

	private void fillUI(ParkingLot pl) {
		((TextView) rootView.findViewById(R.id.textView_parkinglot_code_edit))
				.setText("" + pl.getCode());
		((TextView) rootView
				.findViewById(R.id.textView_parkinglot_creat_at_edit))
				.setText("" + pl.getSubmitAtString());
	}

	public void refreshParkingLot(ParkingLot parkingLot) {
		if (parkingLot == null)
			return;
		this.currentParkingLot = parkingLot;
		fillUI(currentParkingLot);
	}

	public void refreshSensorList(ArrayList<String> sensorCodes) {
		if (sensorCodes == null)
			return;
		this.sensorCodes = sensorCodes;
		// 更新地磁列表
		initOrUpAdapterData(sensorCodes);
	}

	private CustomBaseAdapter<RelativeLayout> adapter;

	private void initOrUpAdapterData(ArrayList<String> sensorCodes) {
		if (adapter == null) {
			adapter = new CustomBaseAdapter<RelativeLayout>(
					getSensorList(sensorCodes));
			listView_sensors.setAdapter(adapter);
		} else {
			adapter.upData(getSensorList(sensorCodes));
			adapter.notifyDataSetChanged();
		}
	}

	private List<RelativeLayout> getSensorList(ArrayList<String> sensorCodes) {
		List<RelativeLayout> views = new ArrayList<RelativeLayout>();
		for (String s : sensorCodes) {
			Log.v("demo", "拿到节点编号："+s);
			RelativeLayout rootView = (RelativeLayout) inflater.inflate(
					R.layout.list_view_item_for_sensor, null);
			((TextView) rootView.findViewById(R.id.textView_sensor_code))
					.setText("" + s);
			views.add(rootView);
		}
		return views;
	}

	private ParkingLotCheckFragListener listener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (ParkingLotCheckFragListener) activity;
	}

	public interface ParkingLotCheckFragListener {
		/** 获取所属基站信息 */
		public void onSensorBeClicked(String sensorCodes);

	}
}
