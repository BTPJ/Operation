package com.cyjt.operation.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cyjt.operation.R;
import com.cyjt.operation.adapter.CustomBaseAdapter;
import com.cyjt.operation.bean.LocBaseStation;

/**
 * 
 * @author LTP
 *
 */
public class DynamicPickBasestationDialogFragment extends DialogFragment {
	/** 图层过滤器 */
	private LayoutInflater inflater;
	private ListView listView_dynamic_basestation;
	private RelativeLayout include_header;
	private CustomBaseAdapter<RelativeLayout> adapter;
	private TextView actionbar_textView_title;
	private ImageView actionbar_imageView_back_button;
	/** 当前界面功能标志。0表示当前为选取正式基站、1表示当前为选取部署基站 */
	private int currentActionFlag = 0;
	private ArrayList<LocBaseStation> baseStations;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		View rootView = inflater.inflate(
				R.layout.fragment_dynamic_pick_basestation, container, false);
		include_header = (RelativeLayout) rootView
				.findViewById(R.id.include_header);
		listView_dynamic_basestation = (ListView) rootView
				.findViewById(R.id.listView_dynamic_basestation);
		actionbar_textView_title = (TextView) rootView
				.findViewById(R.id.actionbar_textView_title);
		actionbar_imageView_back_button = (ImageView) rootView
				.findViewById(R.id.actionbar_imageView_back_button);
		((TextView) rootView.findViewById(R.id.actionbar_refresh_button))
				.setVisibility(View.GONE);
		((ProgressBar) rootView.findViewById(R.id.actionbar_progressBar))
				.setVisibility(View.GONE);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		currentActionFlag = getArguments().getInt("actionFlag");
		if (getArguments().getSerializable("baseStations") == null) {
			dismiss();
			return;
		}
		baseStations = (ArrayList<LocBaseStation>) getArguments()
				.getSerializable("baseStations");
		viewEvent();
		initOrUpAdapterData(baseStations);
	}

	private String title = "";

	private void viewEvent() {
		switch (currentActionFlag) {
		case 0:
			include_header
					.setBackgroundResource(R.drawable.fillet_shape_background_for_actionbar_with_vivid_green_and_bottom_shadow);
			actionbar_textView_title.setText("选取要使用的正式基站");
			title = "使用新的正式基站";
			break;
		case 1:
			include_header
					.setBackgroundResource(R.drawable.fillet_shape_background_for_actionbar_with_orange_and_bottom_shadow);
			actionbar_textView_title.setText("选取要使用的部署基站");
			title = "使用新的部署基站";
			break;

		default:
			break;
		}
		actionbar_imageView_back_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dismiss();
					}
				});
		listView_dynamic_basestation
				.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						if (position == 0) {
							listener.addBasestation(currentActionFlag);
							dismiss();
							return;
						}
						listener.OnBasestationPicked(
								baseStations.get(position - 1),
								currentActionFlag);
						dismiss();
					}
				});
	}

	private void initOrUpAdapterData(ArrayList<LocBaseStation> baseStations) {
		if (adapter == null) {
			adapter = new CustomBaseAdapter<RelativeLayout>(
					getBaseStationsList(baseStations));
			listView_dynamic_basestation.setAdapter(adapter);
		} else {
			adapter.upData(getBaseStationsList(baseStations));
			adapter.notifyDataSetChanged();
		}
	}

	private RelativeLayout rootView;
	private List<RelativeLayout> views;

	private List<RelativeLayout> getBaseStationsList(
			ArrayList<LocBaseStation> baseStations) {
		views = new ArrayList<RelativeLayout>();
		views.add(getAddLaneArrayLayout());
		if (baseStations == null)
			return views;
		for (LocBaseStation baseStation : baseStations) {
			rootView = (RelativeLayout) inflater.inflate(
					R.layout.list_view_item_for_quick_search_lane_array, null);
			((TextView) rootView.findViewById(R.id.textView_lane_array))
					.setVisibility(View.GONE);
			((ImageView) rootView.findViewById(R.id.imageView))
					.setVisibility(View.GONE);
			((TextView) rootView.findViewById(R.id.textView_lane_array_code))
					.setText("编号：" + baseStation.getCode());
			switch (currentActionFlag) {
			case 0:
				((TextView) rootView
						.findViewById(R.id.textView_lane_array_describe))
						.setText("描述：" + baseStation.getDescription());
				break;
			case 1:
				((TextView) rootView
						.findViewById(R.id.textView_lane_array_describe))
						.setVisibility(View.GONE);
				break;

			default:
				break;
			}
			views.add(rootView);
		}
		return views;
	}

	private RelativeLayout getAddLaneArrayLayout() {
		RelativeLayout forAddLayout = (RelativeLayout) inflater.inflate(
				R.layout.layout_for_list_item_add_layout, null);
		((TextView) forAddLayout.findViewById(R.id.textView_add_button))
				.setText(title);
		return forAddLayout;
	}

	private OnBasestationPickedListener listener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity != null)
			listener = (OnBasestationPickedListener) activity;
	}

	public interface OnBasestationPickedListener {
		/**
		 * 选取基站后执行
		 * 
		 * @param position
		 *            选取的位置
		 * @param actionFlag
		 *            当前标志
		 */
		public void OnBasestationPicked(LocBaseStation baseStation,
				int actionFlag);

		/**
		 * 添加基站后执行
		 * 
		 * @param currentActionFlag
		 *            当前标志
		 */
		public void addBasestation(int actionFlag);
	}
}
