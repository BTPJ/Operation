package com.cyjt.operation.uitools;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cyjt.operation.R;
import com.cyjt.operation.adapter.CustomBaseAdapter;
import com.cyjt.operation.base.AppContext;
import com.cyjt.operation.base.ComparatorDynamicArray;
import com.cyjt.operation.base.Constants;
import com.cyjt.operation.base.HandlerMessageCodes;
import com.cyjt.operation.bean.DynamicArray;
import com.cyjt.operation.fragment.DialogFragmentForLaneArrayStatusChange;
import com.cyjt.operation.fragment.DialogFragmentForLaneArrayStatusChange.ArrayDialogEnventListener;

public class ToolsChangeArrayStatusActivity extends Activity implements
		ArrayDialogEnventListener {
	private LayoutInflater inflater;
	private SwipeRefreshLayout swiprrefreshLayout_forArrays;
	private ArrayList<DynamicArray> arrays;
	private ListView listView;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerMessageCodes.DYNAMIC_FETCH_DYNAMICARRAY_INFO_SUCCEED:
				arrays = (ArrayList<DynamicArray>) msg.obj;
				Collections.sort(arrays, new ComparatorDynamicArray());
				initOrUpdata(arrays);
				break;
			case HandlerMessageCodes.DYNAMIC_FETCH_DYNAMICARRAY_INFO_FAILED:
				break;
			case HandlerMessageCodes.DYNAMIC_UPDATE_DYNAMICARRAY_STATUS_SUCCEED:
				Toast.makeText(ToolsChangeArrayStatusActivity.this, "修改成功",
						Toast.LENGTH_SHORT).show();
				// 提交刷新，重新获取
				getDynamicArrays();
				break;
			case HandlerMessageCodes.DYNAMIC_UPDATE_DYNAMICARRAY_STATUS_FAILED:
				// 请求失败
				Toast.makeText(ToolsChangeArrayStatusActivity.this, "修改失败，请重试",
						Toast.LENGTH_SHORT).show();
				break;
			case HandlerMessageCodes.HTTP_VOLLEY_ERROR:
				break;
			}
			swiprrefreshLayout_forArrays.setRefreshing(false);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inflater = getLayoutInflater();
		setContentView(R.layout.activity_tools_change_array_status);
		initActionBar();
		initView();
		viewEvent();
		// 请求网络
		getDynamicArrays();
	}

	private void initActionBar() {
		findViewById(R.id.actionbar_imageView_back_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						onBackPressed();

					}
				});
		findViewById(R.id.actionbar_refresh_button).setVisibility(View.GONE);
		((TextView) findViewById(R.id.actionbar_textView_title))
				.setText("阵列状态查询");
		findViewById(R.id.actionbar_progressBar).setVisibility(View.GONE);
	}

	private void initView() {
		listView = (ListView) findViewById(R.id.listView);
		swiprrefreshLayout_forArrays = (SwipeRefreshLayout) findViewById(R.id.swiprrefreshLayout_forArrays);
		swiprrefreshLayout_forArrays
				.setColorSchemeResources(Constants.SWIPE_REFRESH_COLORS);
	}

	private void viewEvent() {
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int i, long l) {
				DynamicArray array = arrays.get(i);
				// 打开Dialog
				openDynamicArrayInfoLog(array);
			}
		});
		swiprrefreshLayout_forArrays
				.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
					@Override
					public void onRefresh() {
						// 请求数据
						getDynamicArrays();
					}
				});
	}

	private DialogFragmentForLaneArrayStatusChange yesOrNoFrag;

	private void openDynamicArrayInfoLog(DynamicArray array) {
		yesOrNoFrag = new DialogFragmentForLaneArrayStatusChange();
		Bundle b = new Bundle();
		b.putSerializable("dynamicArray", array);
		yesOrNoFrag.setArguments(b);
		yesOrNoFrag.setStyle(
				DialogFragmentForLaneArrayStatusChange.STYLE_NO_TITLE,
				android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth);
		yesOrNoFrag.show(getFragmentManager(), "ArrayDialog");
	}

	private CustomBaseAdapter<RelativeLayout> adapter;

	private void initOrUpdata(ArrayList<DynamicArray> arrays) {
		if (adapter == null) {
			adapter = new CustomBaseAdapter<RelativeLayout>(getViews(arrays));
			listView.setAdapter(adapter);
		} else {
			adapter.upData(getViews(arrays));
			adapter.notifyDataSetChanged();
		}
	}

	private ArrayList<RelativeLayout> getViews(ArrayList<DynamicArray> arrays) {
		ArrayList<RelativeLayout> views = new ArrayList<RelativeLayout>();
		for (DynamicArray array : arrays) {
			views.add(getview(array));
		}
		return views;
	}

	private RelativeLayout rootView;
	private int color;
	private String statusString = "";

	private RelativeLayout getview(DynamicArray array) {
		rootView = (RelativeLayout) inflater.inflate(
				R.layout.list_view_item_for_dynamic_array_new, null);

		switch (array.getStatus()) {

		case Constants.LANEARRAY_STATUS_GREEN:
			color = Constants.LANEARRAY_STATUS_GREEN_COLOR;
			statusString = Constants.LANEARRAY_STATUS_GREEN_STRING;
			break;
		case Constants.LANEARRAY_STATUS_YELLOW:
			color = Constants.LANEARRAY_STATUS_YELLOW_COLOR;
			statusString = Constants.LANEARRAY_STATUS_YELLOW_STRING;
			break;
		case Constants.LANEARRAY_STATUS_RED:
			color = Constants.LANEARRAY_STATUS_RED_COLOR;
			statusString = Constants.LANEARRAY_STATUS_RED_STRING;
			break;
		default:
		case Constants.LANEARRAY_STATUS_DEF:
			color = Constants.LANEARRAY_STATUS_DEF_COLOR;
			statusString = Constants.LANEARRAY_STATUS_DEF_STRING;
			break;
		}
		((TextView) rootView.findViewById(R.id.textView_status))
				.setText(statusString);
		((TextView) rootView.findViewById(R.id.textView_status))
				.setTextColor(color);
		((TextView) rootView.findViewById(R.id.textView_array_code)).setText(""
				+ array.getArrayCode());
		((TextView) rootView.findViewById(R.id.textView_submit_time))
				.setText("" + array.getSubmitAtString());
		((TextView) rootView.findViewById(R.id.textView_submit_time_from_now))
				.setText("" + array.getSubmitAtFromNow());
		return rootView;
	}

	private void getDynamicArrays() {
		AppContext.getInstance().getVolleyTools()
				.dynamicFetchDynamicArrayInfo(handler);
	}

	@Override
	public void onSubmitClicked(DynamicArray array) {
		AppContext.getInstance().getVolleyTools()
				.dynamicUpdateDynamicArrayStatus(handler, array);
	}
}