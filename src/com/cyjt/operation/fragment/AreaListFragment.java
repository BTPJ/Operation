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
 * ���Fragment����չʾ�����б�
 * 
 * @author LTP<BR>
 *         2014-10-23 ����4:47:44<BR>
 */
public class AreaListFragment extends Fragment {

	private ArrayList<Area> areas;
	private LayoutInflater inflater;
	private ListView listView;
	private TextView textView_title;
	private CustomBaseAdapter<RelativeLayout> adapter;
	/** fragment��־λ��0��־����1��ʾ�ֵ� */
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
			textView_title.setText("������");
			break;
		case 1:
			textView_title.setText("Ƭ��");
			break;
		default:
			break;
		}
	}

	/**
	 * �ؼ��¼�����
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
					Log.i("demo", "���·��");
				}
			}
		});
	}

	/**
	 * ���������������
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
	 * ��ò���
	 * 
	 * @param areas
	 *            ����
	 * @return ����
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
