package com.cyjt.operation.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.cyjt.operation.base.AppConfig;
import com.cyjt.operation.base.AppContext;
import com.cyjt.operation.base.Constants;
import com.cyjt.operation.base.HandlerMessageCodes;
import com.cyjt.operation.bean.LaneArrayStatus;

public class DialogFragmentForLaneArrayStatus extends DialogFragment {
	/** 图层过滤器 */
	private LayoutInflater inflater;
	private ListView listView_for_z_values;
	private CustomBaseAdapter<RelativeLayout> adapter;
	private String laneArrayCode;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerMessageCodes.DYNAMIC_FETCH_LANEARRAY_STATUS_SUCCEED:
				// 获取到心跳后将心跳展现出来
				ArrayList<LaneArrayStatus> laneArrayStatus = (ArrayList<LaneArrayStatus>) msg.obj;
				// 填充数据
				initOrUpAdapterData(laneArrayStatus);
				break;
			case HandlerMessageCodes.DYNAMIC_FETCH_LANEARRAY_STATUS_FAILED:
				Toast.makeText(getActivity(), "暂无数据", Toast.LENGTH_SHORT)
						.show();

				break;

			default:
				break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		View rootView = inflater.inflate(R.layout.fragment_dynimac_z_values,
				container, false);
		listView_for_z_values = (ListView) rootView
				.findViewById(R.id.listView_for_z_values);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments().getString("laneArrayCode") == null) {
			return;
		}
		laneArrayCode = getArguments().getString("laneArrayCode");
		viewEvent();

	}

	private void viewEvent() {
		// 请求网络
		if (AppConfig.USING_NETWORK) {
			getLAStatus(laneArrayCode);
		} else {
			// ======================以下数据用于测试
		}
		listView_for_z_values.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				dismiss();
			}
		});
	}

	private void initOrUpAdapterData(ArrayList<LaneArrayStatus> laneArrayStatus) {
		if (adapter == null) {
			adapter = new CustomBaseAdapter<RelativeLayout>(
					getZValuesList(laneArrayStatus));
			listView_for_z_values.setAdapter(adapter);
		} else {
			adapter.upData(getZValuesList(laneArrayStatus));
			adapter.notifyDataSetChanged();
		}
	}

	private RelativeLayout rootView;

	private List<RelativeLayout> getZValuesList(
			ArrayList<LaneArrayStatus> lneArrayStatus) {
		List<RelativeLayout> views = new ArrayList<RelativeLayout>();
		for (LaneArrayStatus z : lneArrayStatus) {
			rootView = (RelativeLayout) inflater.inflate(
					R.layout.list_view_item_for_z_values, null);
			switch (z.getStatus()) {
			case Constants.LANEARRAY_STATUS_GREEN:

				((TextView) rootView
						.findViewById(R.id.textView_for_sensor_z_value))
						.setTextColor(Color.rgb(113, 163, 32));

				break;
			case Constants.LANEARRAY_STATUS_YELLOW:
				((TextView) rootView
						.findViewById(R.id.textView_for_sensor_z_value))
						.setTextColor(Color.rgb(255, 143, 0));
				break;
			case Constants.LANEARRAY_STATUS_RED:
				((TextView) rootView
						.findViewById(R.id.textView_for_sensor_z_value))
						.setTextColor(Color.rgb(170, 34, 42));
				break;

			default:
				break;
			}
			((TextView) rootView.findViewById(R.id.textView_for_sensor_z_value))
					.setText("阵列状态：" + z.getStatusString());
			((TextView) rootView.findViewById(R.id.textView_for_sensor_voltage))
					.setText("上报时间：" + z.getSubmitAtFromNow());
			((TextView) rootView
					.findViewById(R.id.textView_for_sensor_z_value_time))
					.setText("Time：" + z.getSubmitAtString());
			((TextView) rootView.findViewById(R.id.textView_for_sensor_signal))
					.setVisibility(View.GONE);
			views.add(rootView);
		}
		return views;
	}

	private void getLAStatus(String code) {
		AppContext.getInstance().getVolleyTools()
				.fetchLaneArrayStatus(handler, code);
	}
}
