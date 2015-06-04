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
import com.cyjt.operation.bean.BaseStation;

/**
 * ���Fragment����չʾ��վ�б�
 * 
 * @author kullo<BR>
 *         2014-10-23 ����4:47:44<BR>
 */
public class BasestationListFragment extends Fragment {

	private ArrayList<BaseStation> baseStations;
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

	public void refreshBasestationList(ArrayList<BaseStation> baseStations) {
		this.baseStations = baseStations;
		initOrUpAdapterData(baseStations);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		baseStations = (ArrayList<BaseStation>) getArguments().getSerializable(
				"baseStation");
		viewEvent();
		initOrUpAdapterData(baseStations);
	}

	public void stopFreashView() {
		refreshableView.setRefreshing(false);
	}

	private void viewEvent() {
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (baseStations != null && baseStations.size() > 0) {
					listener.onBasestationBeClicked(baseStations.get(position));
				}
			}
		});
		refreshableView
				.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
					@Override
					public void onRefresh() {
						// ˢ��
						listener.OnBaseStationsRefreashed();
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
	private BaseStation bs;

	private List<RelativeLayout> getRelayouts(
			ArrayList<BaseStation> baseStations) {
		layouts = new ArrayList<RelativeLayout>();
		if (baseStations == null)
			return layouts;
		for (int i = 0; i < baseStations.size(); i++) {
			bs = baseStations.get(i);
			rootView = (RelativeLayout) inflater.inflate(
					R.layout.list_view_item_for_basestation_list, null);
			((TextView) rootView.findViewById(R.id.textView_index)).setText(i
					+ 1 + "");
			((TextView) rootView.findViewById(R.id.textView_code)).setText(bs
					.getNfcCode() + "");
			((TextView) rootView.findViewById(R.id.textView_description))
					.setText(bs.getDescription() + "");
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

	private OnBasestationClickListener listener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (OnBasestationClickListener) activity;
	}

	public interface OnBasestationClickListener {
		/** ѡ�л�վ���ִ�� */
		public void onBasestationBeClicked(BaseStation baseStation);

		/** ѡ����ӻ�վ���ִ�� */
		public void addBasestation();

		/** ˢ�»�վ */
		public void OnBaseStationsRefreashed();

	}

}
