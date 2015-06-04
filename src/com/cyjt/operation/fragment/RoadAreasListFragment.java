package com.cyjt.operation.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.cyjt.operation.base.Constants;
import com.cyjt.operation.bean.RoadsAreas;

/**
 * ���Fragment����չʾ·���б�
 * 
 * @author kullo<BR>
 *         2014-10-23 ����4:47:44<BR>
 */
public class RoadAreasListFragment extends Fragment {

	private ArrayList<RoadsAreas> roadsAreas;
	private LayoutInflater inflater;
	private ListView listView;
	private SwipeRefreshLayout refreshableView;
	private CustomBaseAdapter<RelativeLayout> adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		View rootView = inflater.inflate(R.layout.fragment_basestation,
				container, false);
		listView = (ListView) rootView
				.findViewById(R.id.listView_for_basestation);
		refreshableView = (SwipeRefreshLayout) rootView
				.findViewById(R.id.refreshable_view);
		refreshableView.setColorSchemeResources(Constants.SWIPE_REFRESH_COLORS);
		return rootView;
	}

	public void refreshRoadAreasList(ArrayList<RoadsAreas> roadsAreas) {
		this.roadsAreas = roadsAreas;
		initOrUpAdapterData(roadsAreas);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		roadsAreas = (ArrayList<RoadsAreas>) getArguments().getSerializable(
				"roadsAreas");
		viewEvent();
		initOrUpAdapterData(roadsAreas);
	}

	public void stopFreashView() {
		refreshableView.setRefreshing(false);
	}

	private void viewEvent() {
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (roadsAreas != null && roadsAreas.size() > 0) {
					listener.onRoadAreasBeClicked(roadsAreas.get(position));
				}
			}
		});
		refreshableView
				.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
					@Override
					public void onRefresh() {
						// ˢ��
						listener.OnRoadAreasRefreashed();
					}
				});
	}

	private void initOrUpAdapterData(ArrayList<RoadsAreas> roadsAreas) {
		if (adapter == null) {
			adapter = new CustomBaseAdapter<RelativeLayout>(
					getRelayouts(roadsAreas));
			listView.setAdapter(adapter);
		} else {
			adapter.upData(getRelayouts(roadsAreas));
			adapter.notifyDataSetChanged();
		}
	}

	private List<RelativeLayout> layouts;
	private RelativeLayout rootView;
	private RoadsAreas bs;

	private List<RelativeLayout> getRelayouts(ArrayList<RoadsAreas> roadsAreas) {
		layouts = new ArrayList<RelativeLayout>();
		if (roadsAreas == null)
			return layouts;
		for (int i = 0; i < roadsAreas.size(); i++) {
			bs = roadsAreas.get(i);
			rootView = (RelativeLayout) inflater.inflate(
					R.layout.list_view_item_for_roadareas_list, null);
			((TextView) rootView.findViewById(R.id.textView_code)).setText(bs
					.getTitle());
			((TextView) rootView.findViewById(R.id.textView_index)).setText(i
					+ 1 + "");
			layouts.add(rootView);
		}
		return layouts;
	}

	/** ����һ��������ӻ�վ�Ĳ� */
	private RelativeLayout getAddBasestationLayout() {
		RelativeLayout forAddLayout = (RelativeLayout) inflater.inflate(
				R.layout.layout_for_list_item_add_layout, null);
		TextView tv = (TextView) forAddLayout
				.findViewById(R.id.textView_add_button);
		tv.setText("�ᴥ��ӻ�վ");
		return forAddLayout;
	}

	private OnRoadAreasClickListener listener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (OnRoadAreasClickListener) activity;
	}

	public interface OnRoadAreasClickListener {
		/** ѡ��·�κ��ִ�� */
		public void onRoadAreasBeClicked(RoadsAreas roadsAreas);

		/** ѡ�����·�κ��ִ�� */
		public void addRoadAreas();

		/** ˢ��·�� */
		public void OnRoadAreasRefreashed();

	}

}
