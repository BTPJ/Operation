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
import com.cyjt.operation.bean.LaneArray;

/**
 * ���Fragment����չ��������Ϣ���Ǹ�����̬ҳ��
 * 
 * @author kullo<BR>
 *         2014-10-14 ����9:12:10<BR>
 */
public class DynamicLaneArrayCheckFragment extends Fragment {
	/** ����� */
	private View rootView;
	private TextView textView_dynamic_lane_array_code_edit;
	private TextView textView_dynamic_lane_array_describe_edit;
	private RelativeLayout relativeLayout_dynamic_lane_array_address_button;
	private TextView textView_dynamic_lane_array_address_longitude;
	private TextView textView_dynamic_lane_array_address_latitude;
	private TextView textView_dynamic_lane_array_address_String;
	private TextView textView_dynamic_lane_array_creat_at_edit;
	private TextView textView_dynamic_lane_array_location_flag_edit;
	private TextView textView_dynamic_lane_array_status_edit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_dynamic_lane_array_check,
				container, false);
		textView_dynamic_lane_array_status_edit = (TextView) rootView
				.findViewById(R.id.textView_dynamic_lane_array_status_edit);
		textView_dynamic_lane_array_location_flag_edit = (TextView) rootView
				.findViewById(R.id.textView_dynamic_lane_array_location_flag_edit);
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
		return rootView;
	}

	private LaneArray currentLaneArray;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments().getSerializable("laneArray") == null) {
			rootView.setVisibility(View.GONE);
			Toast.makeText(getActivity(), "������,���ж��󲻴���", Toast.LENGTH_LONG)
					.show();
			return;
		}
		currentLaneArray = (LaneArray) getArguments().getSerializable(
				"laneArray");
		viewEvent();
		fillUI(currentLaneArray);
	}

	private void viewEvent() {
		relativeLayout_dynamic_lane_array_address_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						listener.onMapCheckBeClicked();
					}
				});
		textView_dynamic_lane_array_status_edit
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						listener.checkLaneArrayStatus();
					}
				});
	}

	private void fillUI(LaneArray la) {
		textView_dynamic_lane_array_code_edit.setText("" + la.getCode());
		textView_dynamic_lane_array_describe_edit.setText(""
				+ la.getDescription());
		textView_dynamic_lane_array_address_longitude.setText("���ȣ�"
				+ la.getLon());
		textView_dynamic_lane_array_address_latitude.setText("γ�ȣ�"
				+ la.getLat());
		textView_dynamic_lane_array_address_String.setText("λ�ã�");
		textView_dynamic_lane_array_location_flag_edit.setText(""
				+ la.getLocation());
		textView_dynamic_lane_array_creat_at_edit.setText(""
				+ la.getCreatAtString());
	}

	public void refreshLaneArray(LaneArray laneArray) {
		if (laneArray == null)
			return;
		this.currentLaneArray = laneArray;
		fillUI(currentLaneArray);
	}

	private LaneArrayCheckFragListener listener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (LaneArrayCheckFragListener) activity;
	}

	public interface LaneArrayCheckFragListener {
		/** �����ͼ���ֺ��ִ�� */
		public void onMapCheckBeClicked();

		public void checkLaneArrayStatus();

	}
}
