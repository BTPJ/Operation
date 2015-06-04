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
import com.cyjt.operation.bean.BaseStation;

public class DynamicBasestationListFragment extends Fragment {

	private OnBasestationClickListener listener;
	private ArrayList<BaseStation> baseStations;
	private LayoutInflater inflater;
	private ListView listView;
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

	public void refreshBasestationList(ArrayList<BaseStation> baseStations) {
		this.baseStations = baseStations;
		initOrUpAdapterData(baseStations);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		viewEvent();
		initOrUpAdapterData(baseStations);
		listener.refreashBasestationList();
	}

	private void viewEvent() {
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					listener.addBasestation();
					return;
				}
				if (baseStations != null && baseStations.size() > position - 1) {
					listener.onBasestationBeClicked(baseStations
							.get(position - 1));
				}
			}
		});
	}

	private void initOrUpAdapterData(ArrayList<BaseStation> baseStations) {
		if (adapter == null) {
			adapter = new CustomBaseAdapter<RelativeLayout>(
					getRelayouts(baseStations));
			listView.setAdapter(adapter);
		} else {
			adapter.upData(getRelayouts(baseStations));
			adapter.notifyDataSetChanged();
		}
	}

	private List<RelativeLayout> layouts;
	private RelativeLayout rootView;
	private TextView textView_index;
	private TextView textView_code;
	private TextView textView_description;
	private BaseStation bs;

	private List<RelativeLayout> getRelayouts(
			ArrayList<BaseStation> baseStations) {
		layouts = new ArrayList<RelativeLayout>();
		layouts.add(getAddBasestationLayout());
		if (baseStations == null)
			return layouts;
		for (int i = 0; i < baseStations.size(); i++) {
			bs = baseStations.get(i);
			rootView = (RelativeLayout) inflater.inflate(
					R.layout.list_view_item_for_dynamic_basestation_list, null);
			textView_index = (TextView) rootView
					.findViewById(R.id.textView_index);
			textView_code = (TextView) rootView
					.findViewById(R.id.textView_code);
			textView_description = (TextView) rootView
					.findViewById(R.id.textView_description);
			switch (bs.getStatus()) {
			case 1:
				textView_index
						.setBackgroundResource(R.drawable.fillet_shape_background_for_list_item_circle_little_gray);
				break;
			case 2:

				break;

			default:
				break;
			}
			textView_index.setText(i + 1 + "");
			textView_code.setText(bs.getCode() + "");
			textView_description.setText(bs.getDescription() + "");
			layouts.add(rootView);
		}
		return layouts;
	}

	/** 返回一个用于添加基站的层 */
	private RelativeLayout getAddBasestationLayout() {
		RelativeLayout forAddLayout = (RelativeLayout) inflater.inflate(
				R.layout.layout_for_list_item_add_layout, null);
		TextView tv = (TextView) forAddLayout
				.findViewById(R.id.textView_add_button);
		tv.setText("轻触添加基站");
		return forAddLayout;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (OnBasestationClickListener) activity;
	}

	public interface OnBasestationClickListener {
		/** 选中基站后会执行 */
		public void onBasestationBeClicked(BaseStation baseStation);

		/** 选中添加基站后会执行 */
		public void addBasestation();

		public void refreashBasestationList();
	}

}
