package com.cyjt.operation.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cyjt.operation.R;
import com.cyjt.operation.adapter.CustomBaseAdapter;
import com.cyjt.operation.bean.Area;

/**
 * 这个Fragment用于展示区域列表
 * 
 * @author LTP<BR>
 *         2014-10-23 下午4:47:44<BR>
 */
public class AreaListFragment extends Fragment {

	private ArrayList<Area> areas;
	private LayoutInflater inflater;
	private ListView listView;
	private TextView textView_title;
	private CustomBaseAdapter<RelativeLayout> adapter;
	/** fragment标志位，0标志区域、1表示街道 */
	private int currentFlag = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		View rootView = inflater.inflate(R.layout.fragment_area, container,
				false);
		listView = (ListView) rootView
				.findViewById(R.id.listView_for_dynamic_basestation);
		textView_title = (TextView) rootView.findViewById(R.id.textView_title);
		return rootView;
	}

	/**
	 * @param areas
	 */
	public void refreshArrayList(ArrayList<Area> areas) {
		this.areas = areas;
		initOrUpAdapterData(areas);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		viewEvent();
		currentFlag = getArguments().getInt("currentFlag");
		if (getArguments().getSerializable("areas") == null) {
			return;
		}
		areas = (ArrayList<Area>) getArguments().getSerializable("areas");
		initOrUpAdapterData(areas);
		switch (currentFlag) {
		case 0:
			textView_title.setText("行政区");
			break;
		case 1:
			textView_title.setText("片区");
			break;
		default:
			break;
		}
	}

	/**
	 * 控件事件处理
	 */
	private void viewEvent() {
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				for (int i = 0; i < parent.getChildCount(); i++) {
					((TextView) parent.getChildAt(i).findViewById(
							R.id.textView_area_describe)).setTextColor(Color
							.argb(255, 152, 154, 158));
					((ImageView) parent.getChildAt(i).findViewById(
							R.id.imageView_next)).setVisibility(View.GONE);
				}
				((TextView) view.findViewById(R.id.textView_area_describe))
						.setTextColor(Color.argb(255, 44, 42, 40));
				((ImageView) view.findViewById(R.id.imageView_next))
						.setVisibility(View.VISIBLE);
				if (areas != null && areas.size() > 0) {
					listener.onAreaBeClicked(areas.get(position), currentFlag);
					Log.i("demo", "点击路段");
				}
			}
		});
	}

	/**
	 * 往布局中填充数据
	 * 
	 * @param areas
	 */
	private void initOrUpAdapterData(ArrayList<Area> areas) {
		if (adapter == null) {
			adapter = new CustomBaseAdapter<RelativeLayout>(getRelayouts(areas));
			listView.setAdapter(adapter);
		} else {
			adapter.upData(getRelayouts(areas));
			adapter.notifyDataSetChanged();
		}
	}

	private List<RelativeLayout> layouts;
	private RelativeLayout rootView;
	private Area area;

	/**
	 * 获得布局
	 * 
	 * @param areas
	 *            区域
	 * @return 布局
	 */
	private List<RelativeLayout> getRelayouts(ArrayList<Area> areas) {
		layouts = new ArrayList<RelativeLayout>();
		if (areas == null)
			return layouts;
		for (int i = 0; i < areas.size(); i++) {
			area = areas.get(i);
			rootView = (RelativeLayout) inflater.inflate(
					R.layout.list_view_item_for_area, null);
			((TextView) rootView.findViewById(R.id.textView_area_describe))
					.setText(area.getTitle());
			((ImageView) rootView.findViewById(R.id.imageView_next))
					.setVisibility(View.GONE);
			layouts.add(rootView);
		}
		return layouts;
	}

	private OnAreaClickListener listener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (OnAreaClickListener) activity;
	}

	public interface OnAreaClickListener {

		public void onAreaBeClicked(Area area, int currentFlag);

	}

}
