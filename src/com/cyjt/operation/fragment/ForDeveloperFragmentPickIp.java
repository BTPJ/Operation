package com.cyjt.operation.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cyjt.operation.R;
import com.cyjt.operation.adapter.CustomBaseAdapter;
import com.cyjt.operation.base.AppContext;
import com.cyjt.operation.base.Constants;
import com.cyjt.operation.base.IpAddress;
import com.cyjt.operation.base.SharedPreferencesTools;
import com.cyjt.operation.base.Tools;
import com.cyjt.operation.base.URLs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 添加与选择ip的fragment
 * 
 * @author LTP
 *
 */
public class ForDeveloperFragmentPickIp extends DialogFragment {
	private Gson gson = Tools.getGson(Constants.ISO8601DateFormatShort); // 创建gson对象，并设置日期格式
	private final int LOCAL_WHAT = 0;
	private final int SET_CLICKABLE_TRUE = 1;
	private String defaultIp = "";
	private LayoutInflater inflater;
	private ArrayList<IpAddress> ips = new ArrayList<IpAddress>();
	private TextView textView_for_developer_pick_ip_current_ip;
	private TextView textView_for_developer_pick_ip_cancel;
	private TextView textView_for_developer_pick_ip_add;
	private ListView listView_for_developer_pick_ip;
	private CustomBaseAdapter<RelativeLayout> adapter;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOCAL_WHAT:
				textView_for_developer_pick_ip_current_ip.setText(""
						+ defaultIp);
				initOrUpdataListView(ips);
				break;
			case SET_CLICKABLE_TRUE:
				listView_for_developer_pick_ip.setEnabled(true);
				break;
			}
		}
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		readerList();
		uiHandler();
		// 延时控制
		handler.sendEmptyMessageDelayed(SET_CLICKABLE_TRUE, 2000);
	}

	private void uiHandler() {
		// 从数据库中获取到数据并封装为ArrayList<String>
		handler.sendEmptyMessage(LOCAL_WHAT);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		View rootView = inflater.inflate(
				R.layout.fragment_for_developer_pick_ip, container, false);
		textView_for_developer_pick_ip_current_ip = (TextView) rootView
				.findViewById(R.id.textView_for_developer_pick_ip_current_ip);
		textView_for_developer_pick_ip_add = (TextView) rootView
				.findViewById(R.id.textView_for_developer_pick_ip_add);
		textView_for_developer_pick_ip_cancel = (TextView) rootView
				.findViewById(R.id.textView_for_developer_pick_ip_cancel);
		listView_for_developer_pick_ip = (ListView) rootView
				.findViewById(R.id.listView_for_developer_pick_ip);
		listView_for_developer_pick_ip.setEnabled(false);
		textView_for_developer_pick_ip_cancel
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dismiss();
					}
				});
		textView_for_developer_pick_ip_add
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 弹出添加窗口
						// 弹出IP地址选择
						ForDeveloperFragmentAddIp fdF = new ForDeveloperFragmentAddIp();
						fdF.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
						fdF.show(getFragmentManager(),
								"ForDeveloperFragmentAddIp");
					}
				});
		listView_for_developer_pick_ip
				.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// 选择后返回
						Toast.makeText(getActivity(),
								"已切换Ip为：" + ips.get(position).getIp(),
								Toast.LENGTH_SHORT).show();
						URLs.changeIp(ips.get(position).getIp());
						setDefIp(ips.get(position).getIp());
						dismiss();
					}
				});
		return rootView;
	}

	/** 初始化thirdAdapter */
	private void initOrUpdataListView(ArrayList<IpAddress> ips) {
		if (ips == null) {
			return;
		}
		if (adapter == null) {
			// 初始化adapter
			adapter = new CustomBaseAdapter<RelativeLayout>(getDataInfo(ips));
			listView_for_developer_pick_ip.setAdapter(adapter);
		} else {
			adapter.upData(getDataInfo(ips));
			adapter.notifyDataSetChanged();
		}
	}

	private TextView ipString;
	private TextView deleteButton;
	private RelativeLayout rootView;
	private int i = 0;

	private List<RelativeLayout> getDataInfo(ArrayList<IpAddress> ips) {
		List<RelativeLayout> relativeLayouts_dataInfo = new ArrayList<RelativeLayout>();
		i = 0;
		for (IpAddress ip : ips) {
			i++;
			rootView = (RelativeLayout) inflater.inflate(
					R.layout.list_view_item_for_pick_ip, null);
			ipString = (TextView) rootView
					.findViewById(R.id.textView_for_developer_pick_ip_list_item_ip);
			deleteButton = (TextView) rootView
					.findViewById(R.id.textView_for_developer_pick_ip_list_item_delete_button);
			deleteButton.setTag(i - 1);
			ipString.setText("" + ip.getIp());
			deleteButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					deleteList((Integer) v.getTag());
				}

			});
			relativeLayouts_dataInfo.add(rootView);
		}
		return relativeLayouts_dataInfo;
	}

	private void setDefIp(String string) {
		defaultIp = string;
		uiHandler();
		AppContext
				.getInstance()
				.getSharedPreferencesTools()
				.writeStringPreferences(SharedPreferencesTools.IPS,
						"defaultIp", defaultIp);
	}

	/** 删除本地和内存数据,并更新数据 */
	private void deleteList(Integer tag) {
		ips.remove(tag);
		saveList();
	}

	private void addList(IpAddress ip) {
		if (ips == null) {
			ips = new ArrayList<IpAddress>();
		}
		ips.add(ip);
		saveList();
		Toast.makeText(getActivity(), "添加成功", Toast.LENGTH_SHORT).show();
	}

	private void readerList() {
		// 数据库中读取gson
		String gString = AppContext.getInstance().getSharedPreferencesTools()
				.readStringPreferences(SharedPreferencesTools.IPS, "IPS");
		// 以及拿出保存的默认Ip
		defaultIp = AppContext.getInstance().getSharedPreferencesTools()
				.readStringPreferences(SharedPreferencesTools.IPS, "defaultIp");
		ips = gson.fromJson(gString, new TypeToken<ArrayList<IpAddress>>() {
		}.getType());
	}

	private void saveList() {
		String gString = gson.toJson(ips);
		// 保存和更新
		AppContext
				.getInstance()
				.getSharedPreferencesTools()
				.writeStringPreferences(SharedPreferencesTools.IPS,
						"defaultIp", defaultIp);
		AppContext
				.getInstance()
				.getSharedPreferencesTools()
				.writeStringPreferences(SharedPreferencesTools.IPS, "IPS",
						gString);
		uiHandler();
	}

	public void OnAddSucceed(IpAddress ip) {
		addList(ip);
	}
}
