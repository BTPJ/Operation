package com.cyjt.operation.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cyjt.operation.R;
import com.cyjt.operation.bean.BaseStation;

/**
 * 这个Fragment用于展现阵列信息，是个纯静态页面
 * 
 * @author kullo<BR>
 *         2014-10-14 上午9:12:10<BR>
 */
public class BaseStationCheckFragment extends Fragment {
	/** 界面根 */
	private View rootView;
	private TextView textView_dynamic_lane_array_code_edit;
	private TextView textView_dynamic_lane_array_describe_edit;
	private RelativeLayout relativeLayout_dynamic_lane_array_address_button;
	private RelativeLayout relativeLayout_basestation_heart_check_button;
	private RelativeLayout relativeLayout_basestation_updata_check_button;
	private TextView textView_dynamic_lane_array_address_longitude;
	private TextView textView_dynamic_lane_array_address_latitude;
	private TextView textView_dynamic_lane_array_address_String;
	private TextView textView_dynamic_lane_array_creat_at_edit;
	private TextView textView_dynamic_BaseStation_status_edit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_basestation_check,
				container, false);
		textView_dynamic_BaseStation_status_edit = (TextView) rootView
				.findViewById(R.id.textView_dynamic_BaseStation_status_edit);
		textView_dynamic_lane_array_code_edit = (TextView) rootView
				.findViewById(R.id.textView_dynamic_lane_array_code_edit);
		textView_dynamic_lane_array_describe_edit = (TextView) rootView
				.findViewById(R.id.textView_dynamic_lane_array_describe_edit);
		textView_dynamic_lane_array_address_longitude = (TextView) rootView
				.findViewById(R.id.textView_dynamic_lane_array_address_longitude);
		textView_dynamic_lane_array_address_latitude = (TextView) rootView
				.findViewById(R.id.textView_dynamic_lane_array_address_latitude);
		textView_dynamic_lane_array_address_String = (TextView) rootView
				.findViewById(R.id.textView_dynamic_lane_array_address_String);
		textView_dynamic_lane_array_creat_at_edit = (TextView) rootView
				.findViewById(R.id.textView_dynamic_lane_array_creat_at_edit);
		relativeLayout_dynamic_lane_array_address_button = (RelativeLayout) rootView
				.findViewById(R.id.relativeLayout_dynamic_lane_array_address_button);
		relativeLayout_basestation_heart_check_button = (RelativeLayout) rootView
				.findViewById(R.id.relativeLayout_basestation_heart_check_button);
		relativeLayout_basestation_updata_check_button = (RelativeLayout) rootView
				.findViewById(R.id.relativeLayout_basestation_updata_check_button);
		return rootView;
	}

	private BaseStation currentBaseStation;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments().getSerializable("baseStation") == null) {
			rootView.setVisibility(View.GONE);
			Toast.makeText(getActivity(), "出错了,阵列对象不存在", Toast.LENGTH_LONG)
					.show();
			return;
		}
		currentBaseStation = (BaseStation) getArguments().getSerializable(
				"baseStation");
		viewEvent();
		fillUI(currentBaseStation);
	}

	private void viewEvent() {
		relativeLayout_dynamic_lane_array_address_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						listener.onMapCheckBeClicked();
					}
				});
		relativeLayout_basestation_heart_check_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						listener.CheckBaseStationHeartBeClicked();
					}
				});
		relativeLayout_basestation_updata_check_button
		.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				listener.CheckBaseStationUpDataBeClicked();
			}
		});
	}

	private void fillUI(BaseStation bs) {
		switch (bs.getStatus()) {
		case 1:
			textView_dynamic_BaseStation_status_edit.setText("离线时间："
					+ bs.getLastAtString());
			break;
		case 2:
			textView_dynamic_BaseStation_status_edit.setText("在线时间："
					+ bs.getLastAtString());
			break;
		default:
			break;
		}
		textView_dynamic_lane_array_code_edit.setText("" + bs.getCode());
		textView_dynamic_lane_array_describe_edit.setText(""
				+ bs.getDescription());
		textView_dynamic_lane_array_address_longitude.setText("经度："
				+ bs.getLon());
		textView_dynamic_lane_array_address_latitude.setText("纬度："
				+ bs.getLat());
		textView_dynamic_lane_array_address_String.setText("地址：");
		textView_dynamic_lane_array_creat_at_edit.setText(""
				+ bs.getCreateAtString());
	}

	public void refreshBaseStation(BaseStation baseStation) {
		if (baseStation == null)
			return;
		this.currentBaseStation = baseStation;
		fillUI(currentBaseStation);
	}

	private BaseStationCheckFragListener listener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (BaseStationCheckFragListener) activity;
	}

	public interface BaseStationCheckFragListener {
		/** 点击地图部分后会执行 */
		public void onMapCheckBeClicked();
		/** 查询基站心跳 */
		public void CheckBaseStationHeartBeClicked();
		/** 查询基站上报的所有数据 */
		public void CheckBaseStationUpDataBeClicked();
	}
}
