package com.cyjt.operation.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cyjt.operation.R;
import com.cyjt.operation.adapter.CustomBaseAdapter;
import com.cyjt.operation.bean.LaneArray;

public class DynamicLaneArrayListFragment extends Fragment {

	private OnLaneArrayClickListener listener;
	private LayoutInflater inflater;
	private ListView listView;
	private ArrayList<LaneArray> laneArrays;
	private CustomBaseAdapter<RelativeLayout> adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		View rootView = inflater.inflate(
				R.layout.fragment_dynamic_basestation_list, container, false);
		listView = (ListView) rootView
				.findViewById(R.id.listView_for_dynamic_basestation);
		return rootView;
	}

	public void refreshLaneArrayList(ArrayList<LaneArray> laneArrays) {
		if (isDestroed) {
			return;
		}
		this.laneArrays = laneArrays;
		initOrUpAdapterData(laneArrays);
	}

	private boolean isDestroed = false;

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isDestroed = true;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		viewEvent();
		initOrUpAdapterData(laneArrays);
		listener.refreashLaneArrayList();
	}

	private void viewEvent() {
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					listener.addLaneArray();
					return;
				}
				if (laneArrays != null && laneArrays.size() > position - 1) {
					listener.onLaneArrayBeClicked(laneArrays.get(position - 1));
				}
			}
		});
	}

	private void initOrUpAdapterData(ArrayList<LaneArray> laneArrays) {
		if (adapter == null) {
			adapter = new CustomBaseAdapter<RelativeLayout>(
					getRelayouts(laneArrays));
			listView.setAdapter(adapter);
		} else {
			adapter.upData(getRelayouts(laneArrays));
			adapter.notifyDataSetChanged();
		}
	}

	private List<RelativeLayout> layouts;
	private RelativeLayout rootView;
	private LaneArray la;

	private List<RelativeLayout> getRelayouts(ArrayList<LaneArray> laneArrays) {
		layouts = new ArrayList<RelativeLayout>();
		layouts.add(getAddLaneArrayLayout());
		if (laneArrays == null)
			return layouts;
		for (int i = 0; i < laneArrays.size(); i++) {
			la = laneArrays.get(i);
			rootView = (RelativeLayout) inflater.inflate(
					R.layout.list_view_item_for_dynamic_lane_array_list, null);
			((TextView) rootView.findViewById(R.id.textView_index)).setText(i
					+ 1 + "");
			((TextView) rootView.findViewById(R.id.textView_code)).setText(la
					.getLocation() + " --> " + la.getCode() + "");
			((TextView) rootView.findViewById(R.id.textView_description))
					.setText(la.getDescription() + "");
			layouts.add(rootView);
		}
		return layouts;
	}

	private RelativeLayout getAddLaneArrayLayout() {
		RelativeLayout forAddLayout = (RelativeLayout) inflater.inflate(
				R.layout.layout_for_list_item_add_layout, null);
		TextView tv = (TextView) forAddLayout
				.findViewById(R.id.textView_add_button);
		tv.setText("«·¥•ÃÌº”’Û¡–");
		return forAddLayout;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (OnLaneArrayClickListener) activity;
	}

	public interface OnLaneArrayClickListener {
		public void onLaneArrayBeClicked(LaneArray laneArray);

		public void addLaneArray();

		public void refreashLaneArrayList();
	}
}
