package com.cyjt.operation.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.cyjt.operation.base.AppConfig;
import com.cyjt.operation.base.AppContext;
import com.cyjt.operation.base.HandlerMessageCodes;
import com.cyjt.operation.bean.BaseStationHeartBeat;

public class DialogFragmentForBaseStationHeartBeats extends DialogFragment {
	private static final String TAG = "DialogFragmentForBaseStationHeartBeats";
	/** 图层过滤器 */
	private LayoutInflater inflater;
	private ListView listView_for_z_values;
	private CustomBaseAdapter<RelativeLayout> adapter;
	private String baseStationCode;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATION_HEART_BEAT_SUCCEED:
				// 填充数据
				Log.d(TAG, "HTTP_BUILDER_FETCH_BASESTATION_HEART_BEAT_SUCCEED");
				initOrUpAdapterData((ArrayList<BaseStationHeartBeat>) msg.obj);
				break;
			case HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATION_HEART_BEAT_FAILED:
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
		if (getArguments().getString("baseStationCode") == null) {
			return;
		}
		baseStationCode = getArguments().getString("baseStationCode");
		Log.d(TAG, baseStationCode);
		viewEvent();

	}

	private void viewEvent() {
		// 请求网络
		Toast.makeText(getActivity(), "正在获取" + baseStationCode + "基站的心跳信息",
				Toast.LENGTH_SHORT).show();
		if (AppConfig.USING_NETWORK) {
			Log.d(TAG, "getBsHeart");
			getBsHeart(baseStationCode);
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

	private void initOrUpAdapterData(
			ArrayList<BaseStationHeartBeat> bsHeartBeats) {
		if (adapter == null) {
			adapter = new CustomBaseAdapter<RelativeLayout>(
					getZValuesList(bsHeartBeats));
			listView_for_z_values.setAdapter(adapter);
		} else {
			adapter.upData(getZValuesList(bsHeartBeats));
			adapter.notifyDataSetChanged();
		}
	}

	private RelativeLayout rootView;

	private List<RelativeLayout> getZValuesList(
			ArrayList<BaseStationHeartBeat> bsHeartBeats) {
		List<RelativeLayout> views = new ArrayList<RelativeLayout>();
		for (BaseStationHeartBeat z : bsHeartBeats) {
			Log.d("LTP", z.getGatewayId());
			rootView = (RelativeLayout) inflater.inflate(
					R.layout.list_view_item_for_z_values, null);
			((TextView) rootView.findViewById(R.id.textView_for_sensor_z_value))
					.setText("基站网关：" + z.getGatewayId());
			((TextView) rootView.findViewById(R.id.textView_for_sensor_voltage))
					.setText("上报时间:" + z.getReceiveAtFromNow());
			((TextView) rootView
					.findViewById(R.id.textView_for_sensor_z_value_time))
					.setText("Time:" + z.getReceiveAtString());
			((TextView) rootView.findViewById(R.id.textView_for_sensor_signal))
					.setVisibility(View.GONE);
			views.add(rootView);
		}
		return views;
	}

	private void getBsHeart(String code) {
		AppContext.getInstance().getVolleyTools()
				.fetchBasestationHeartBeat(handler, code);
	}
}
