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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cyjt.operation.R;
import com.cyjt.operation.adapter.CustomBaseAdapter;
import com.cyjt.operation.base.Constants;
import com.cyjt.operation.bean.ParkingLot;

public class ParkingLotListFragment extends Fragment {
	private LayoutInflater inflater;
	private ListView listView_parking_lots;
	private CustomBaseAdapter<RelativeLayout> adapter;
	private ArrayList<RelativeLayout> parkingLotsList;
	private ArrayList<ParkingLot> parkingLots;
	private SwipeRefreshLayout refreshableView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		View rootView = inflater.inflate(R.layout.fragment_parking_lots,
				container, false);
		listView_parking_lots = (ListView) rootView
				.findViewById(R.id.listView_parking_lots);
		refreshableView = (SwipeRefreshLayout) rootView
				.findViewById(R.id.refreshable_view);
		refreshableView.setColorSchemeResources(Constants.SWIPE_REFRESH_COLORS);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		viewEvent();
		// compeletedLots = (ArrayList<ParkingLot>)
		// getArguments().getSerializable("compeletedLots");
		// 请求网络，获取已绑定的车位
		if (parkingLots != null) {
			initOrUpdataCompletedLots(parkingLots);
		}
	}

	public void stopFreashView() {
		refreshableView.setRefreshing(false);
	}

	private void viewEvent() {
		listView_parking_lots.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (position != parkingLots.size()) {
					listener.OnParkingLotBeCilcked(parkingLots.get(position));
				}
			}
		});
		listView_parking_lots
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int position, long arg3) {
						if (position != parkingLots.size()
								&& !parkingLots.get(position).isHasActivited()) {
							listener.OnParkingLotBeLongCilcked(parkingLots
									.get(position));
						}
						return false;
					}
				});
		refreshableView
				.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
					@Override
					public void onRefresh() {
						// 请求数据
						listener.OnParkingLotsRefreashed();
					}
				});
	}

	public void freshParkingLotsData(ArrayList<ParkingLot> parkingLots) {
		this.parkingLots = parkingLots;
		initOrUpdataCompletedLots(parkingLots);

	}

	/** 初始化firstAdapter */
	private void initOrUpdataCompletedLots(ArrayList<ParkingLot> parkingLots) {
		if (adapter == null) {
			// 初始化adapter
			adapter = new CustomBaseAdapter<RelativeLayout>(
					getParkingLotsDataInfo(parkingLots));
			listView_parking_lots.setAdapter(adapter);
		} else {
			adapter.upData(getParkingLotsDataInfo(parkingLots));
			adapter.notifyDataSetChanged();
		}
	}

	private List<RelativeLayout> getParkingLotsDataInfo(
			final ArrayList<ParkingLot> parkingLots) {
		parkingLotsList = new ArrayList<RelativeLayout>();
		if (parkingLots.size() > 0) {
			for (ParkingLot fb : parkingLots) {
				RelativeLayout rootView = (RelativeLayout) inflater
						.inflate(
								R.layout.list_view_item_for_parkinglot_new_circle,
								null);
				if (fb.isHasActivited()) {
					((TextView) rootView
							.findViewById(R.id.textView_for_comlot_code))
							.setText("" + fb.getCode());
					((TextView) rootView
							.findViewById(R.id.textView_has_not_activity))
							.setVisibility(View.VISIBLE);

				} else {
					((TextView) rootView
							.findViewById(R.id.textView_has_activity))
							.setVisibility(View.VISIBLE);
					((TextView) rootView
							.findViewById(R.id.textView_for_incomlot_code))
							.setText("" + fb.getCode());
				}
				switch (fb.getSpaceStatus()) {
				case 1:
					((TextView) rootView
							.findViewById(R.id.textView_has_not_activity))
							.setText("无节点");
					break;
				case 2:
					((TextView) rootView
							.findViewById(R.id.textView_has_not_activity))
							.setText("有节点,心跳有问题");
					break;
				case 3:
					((TextView) rootView
							.findViewById(R.id.textView_has_not_activity))
							.setText("节点正常");
					break;
				default:
					break;
				}
				switch (fb.getStatus()) {
				case 1:
					((TextView) rootView
							.findViewById(R.id.textView_has_activity))
							.setText("空闲");
					break;
				case 2:
					((TextView) rootView
							.findViewById(R.id.textView_has_activity))
							.setText("占用");
					break;
				case 3:
					((TextView) rootView
							.findViewById(R.id.textView_has_activity))
							.setText("停用");
					break;
				default:
					break;
				}
				parkingLotsList.add(rootView);
			}
			// 添加尾巴
			parkingLotsList.add((RelativeLayout) inflater.inflate(
					R.layout.list_view_item_for_parkinglot_new_end, null));
		}
		return parkingLotsList;
	}

	private ParkingLotFragmentListener listener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (ParkingLotFragmentListener) activity;
	}

	public interface ParkingLotFragmentListener {
		/** 车位被点击 */
		public void OnParkingLotBeCilcked(ParkingLot parkingLot);

		public void OnParkingLotBeLongCilcked(ParkingLot parkingLot);

		/** 下拉刷新 */
		public void OnParkingLotsRefreashed();
	}
}
