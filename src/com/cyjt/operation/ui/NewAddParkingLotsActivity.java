package com.cyjt.operation.ui;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cyjt.operation.R;
import com.cyjt.operation.base.AppContext;
import com.cyjt.operation.base.BaseActivityWithNFC;
import com.cyjt.operation.base.HandlerMessageCodes;
import com.cyjt.operation.bean.BaseStation;
import com.cyjt.operation.bean.ParkingLot;
import com.cyjt.operation.sqlit.DBManager;

public class NewAddParkingLotsActivity extends BaseActivityWithNFC {
	private ProgressBar actionbar_progressBar;
	private LayoutInflater inflater;
	private DBManager db = null;
	private TextView actionbar_textView_small_title;
	private TextView actionbar_refresh_button;
	private TextView textView_parkinglot_code_edit_button;
	private TextView textView_add_parkinglot_botton_left;
	private TextView textView_add_parkinglot_botton_right;
	private ViewPager viewpager;
	private CusPagerAdapter<LinearLayout> adapter;
	/** 部署的全部车位集合 */
	private ArrayList<ParkingLot> parkingLots = new ArrayList<ParkingLot>();
	/** 部署时使用的正式基站 */
	private BaseStation basestation;
	/** 吧部署时使用的辅助基站 */
	private BaseStation supprotBasestation;
	/** 添加标志位，此次是否仅创建一个车位 */
	private boolean justAddOne = false;
	private int currentPosition = 0;
	private final int SET_PAGER_TO_LAST = 0;
	private final int SET_SMALL_TITLE = 1;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SET_PAGER_TO_LAST:
				viewpager.setCurrentItem(parkingLots.size() - 1);
				break;
			case SET_SMALL_TITLE:
				upSmallTitle(currentPosition);
				changeButton();
				break;
			case HandlerMessageCodes.HTTP_BUILDER_SUBMIT_PARKINGLOTS_INFO_SUCCEED:
				// 提交批量车位成功
				Log.v("demo", "批量提交成功");
				// 保存Map以及ParkingLots
				if (basestation != null && supprotBasestation != null) {
					db.saveBaseStationMap(basestation, supprotBasestation);
					if (parkingLots != null && parkingLots.size() > 0) {
						db.saveParkingLots(supprotBasestation, parkingLots);
					}
				}
				onBackPressed();
				break;
			case HandlerMessageCodes.HTTP_BUILDER_SUBMIT_PARKINGLOTS_INFO_FAILED:
				// 提交批量车位失败
				Toast.makeText(NewAddParkingLotsActivity.this, "新增失败，请稍后再试",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
			stopProgress();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inflater = getLayoutInflater();
		db = new DBManager(NewAddParkingLotsActivity.this);
		setContentView(R.layout.activity_new_add_parking_lots);
		initActionBar();
		if (getIntent().getSerializableExtra("basestation") == null) {
			return;
		}
		if (getIntent().getSerializableExtra("supportBasestation") == null) {
			return;
		}
		// 取出传递过来的正式基站对象
		basestation = (BaseStation) getIntent().getSerializableExtra(
				"basestation");
		supprotBasestation = (BaseStation) getIntent().getSerializableExtra(
				"supportBasestation");
		initView();
		viewEvent();
		parkingLots.add(new ParkingLot());
		initOrUpdateViewPager(parkingLots);
		handler.sendEmptyMessage(SET_SMALL_TITLE);
	}

	private void initActionBar() {
		actionbar_progressBar = (ProgressBar) findViewById(R.id.actionbar_progressBar);
		((TextView) findViewById(R.id.actionbar_textView_small_title_icon))
				.setVisibility(View.GONE);
		findViewById(R.id.actionbar_imageView_back_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						onBackPressed();
					}
				});
		((TextView) findViewById(R.id.actionbar_textView_title))
				.setText("添加车位");

		actionbar_textView_small_title = (TextView) findViewById(R.id.actionbar_textView_small_title);
		actionbar_refresh_button = (TextView) findViewById(R.id.actionbar_refresh_button);
		actionbar_refresh_button.setText("提交");
		actionbar_refresh_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 将views中的数据转换出来
				getparkingLotData();
				// 提示用户parkingLots中有空数据
				ParkingLot pl = parkingLots.get(parkingLots.size() - 1);
				if (isParkingLotNull(pl)) {
					Toast.makeText(NewAddParkingLotsActivity.this, "请将信息填充完",
							Toast.LENGTH_LONG).show();
					return;
					// parkingLots.remove(pl);
				}
				if (justAddOne) {
					// 提交单个的车位数据
					// TODO 具体接口未知
					AppContext
							.getInstance()
							.getVolleyTools()
							.submitLotsInfo(handler, basestation, parkingLots,
									supprotBasestation);
				} else {
					// 提交批量的车位数据
					AppContext
							.getInstance()
							.getVolleyTools()
							.submitLotsInfo(handler, basestation, parkingLots,
									supprotBasestation);
				}
			}

		});
		textView_parkinglot_code_edit_button = (TextView) findViewById(R.id.textView_parkinglot_code_edit_button);
		textView_parkinglot_code_edit_button.setVisibility(View.GONE);
		textView_add_parkinglot_botton_left = (TextView) findViewById(R.id.textView_add_parkinglot_botton_left);
		textView_add_parkinglot_botton_right = (TextView) findViewById(R.id.textView_add_parkinglot_botton_right);

		textView_add_parkinglot_botton_left.setVisibility(View.GONE);
		textView_add_parkinglot_botton_right.setVisibility(View.GONE);

		stopProgress();
	}

	/** 改变按钮状态 */
	private void changeButton() {
		if (justAddOne) {
			return;
		}
		textView_add_parkinglot_botton_right.setVisibility(View.VISIBLE);
		if (currentPosition == 0) {
			textView_add_parkinglot_botton_left.setVisibility(View.GONE);
		} else {
			textView_add_parkinglot_botton_left.setVisibility(View.VISIBLE);
		}
		if (views.size() == currentPosition + 1) {
			textView_add_parkinglot_botton_right
					.setBackgroundResource(R.drawable.icon_add_button);
		} else {
			textView_add_parkinglot_botton_right
					.setBackgroundResource(R.drawable.icon_right_button);

		}
	}

	private void initView() {
		viewpager = (ViewPager) findViewById(R.id.viewpager);
		textView_parkinglot_code_edit_button.setVisibility(View.VISIBLE);
		changeButton();
	}

	private void viewEvent() {
		textView_parkinglot_code_edit_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 打开二维码扫描
						Intent i = new Intent();
						i.setClass(NewAddParkingLotsActivity.this,
								ZxingCaptureActivity.class);
						i.putExtra("useDescribe", "请扫描车位二维码");
						startActivityForResult(
								i,
								HandlerMessageCodes.INTENT_ACTION_CODE_ZXING_REQUEST_CODE);
					}
				});
		textView_add_parkinglot_botton_left
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (0 == currentPosition) {
						} else {
							viewpager.setCurrentItem(currentPosition - 1);
						}
					}

				});
		textView_add_parkinglot_botton_right
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Log.v("demo", "views.size():" + views.size()
								+ "_______currentPosition:" + currentPosition);
						if (views.size() == currentPosition + 1) {
							addView();
						} else {
							viewpager.setCurrentItem(currentPosition + 1);
						}
					}

				});
		viewpager.setOnPageChangeListener(new SimpleOnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				currentPosition = position;
				handler.sendEmptyMessage(SET_SMALL_TITLE);

			}
		});
	}

	private void upSmallTitle(int currentPosition) {
		actionbar_textView_small_title.setText((currentPosition + 1) + "/"
				+ views.size());

	}

	private void initOrUpdateViewPager(ArrayList<ParkingLot> parkingLots) {
		if (parkingLots == null)
			return;
		views = getViews(parkingLots.get(parkingLots.size() - 1));
		if (adapter == null) {
			adapter = new CusPagerAdapter<LinearLayout>();
			viewpager.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
	}

	private ArrayList<LinearLayout> views = new ArrayList<LinearLayout>();
	private LinearLayout rootView;

	private ArrayList<LinearLayout> getViews(ParkingLot pl) {
		rootView = (LinearLayout) inflater.inflate(
				R.layout.view_pager_item_for_add_parking_lots, null);
		((EditText) rootView.findViewById(R.id.textView_parkinglot_code_edit))
				.setText(pl.getCode());
		((EditText) rootView.findViewById(R.id.textView_sensor1_code_edit))
				.setText(pl.getSensorCode());
		((EditText) rootView.findViewById(R.id.textView_sensor2_code_edit))
				.setText(pl.getSensorCode1());
		views.add(rootView);
		return views;
	}

	private ArrayList<String> getTitles(ArrayList<ParkingLot> parkingLots) {
		ArrayList<String> titles = new ArrayList<String>();
		for (ParkingLot pl : parkingLots) {
			titles.add(pl.getCode());
		}
		return titles;
	}

	private void addView() {
		getparkingLotData();
		if (parkingLots.size() > 0) {
			if (isParkingLotNull(parkingLots.get(parkingLots.size() - 1))) {
				Toast.makeText(NewAddParkingLotsActivity.this, "请将信息填充完",
						Toast.LENGTH_LONG).show();
				return;
			}
		}
		parkingLots.add(new ParkingLot());
		initOrUpdateViewPager(parkingLots);
		handler.sendEmptyMessage(SET_PAGER_TO_LAST);

	}

	private boolean isParkingLotNull(ParkingLot pl) {
		return ("").equals(pl.getCode()) || ("").equals(pl.getSensorCode())
				|| ("").equals(pl.getSensorCode1());
	}

	/**
	 * 将控件中的数据取出来<BR>
	 * 执行耗时1ms（效率比较高）
	 * 
	 */
	private void getparkingLotData() {
		LinearLayout view = null;
		ParkingLot pl = null;
		for (int index = 0; index < views.size(); index++) {
			view = views.get(index);
			pl = parkingLots.get(index);
			pl.setCode(((EditText) view
					.findViewById(R.id.textView_parkinglot_code_edit))
					.getText().toString());
			pl.setSensorCode(((EditText) view
					.findViewById(R.id.textView_sensor1_code_edit)).getText()
					.toString());
			pl.setSensorCode1(((EditText) view
					.findViewById(R.id.textView_sensor2_code_edit)).getText()
					.toString());
		}
	}

	private void saveNfcCode(String nFC_CODE) {
		LinearLayout lY = views.get(currentPosition);
		TextView sensor1_coden = (TextView) lY
				.findViewById(R.id.textView_sensor1_code_edit);
		TextView sensor2_coden = (TextView) lY
				.findViewById(R.id.textView_sensor2_code_edit);
		if (("").equals(sensor1_coden.getText().toString())) {
			sensor1_coden.setText(nFC_CODE);
		} else if (("").equals(sensor2_coden.getText().toString())) {
			sensor2_coden.setText(nFC_CODE);
		} else {
			// 两个都满了则尝试跳转到下一条
			if (justAddOne)
				return;
			addView();

		}
	}

	private void saveParkingLotTDC(String s) {
		((TextView) (views.get(currentPosition)
				.findViewById(R.id.textView_parkinglot_code_edit))).setText(s);
		// addView();
	}

	private class CusPagerAdapter<T> extends PagerAdapter {

		@Override
		public void destroyItem(View container, int position, Object object) {
			if (views.size() > position) {
				((ViewPager) container).removeView((View) views.get(position));
			}
		}

		@Override
		public Object instantiateItem(View container, int position) {
			// TODO Auto-generated method stub
			((ViewPager) container).addView((View) views.get(position));
			return views.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			ArrayList<String> titles = new ArrayList<String>();
			if (parkingLots != null && parkingLots.size() > 0) {
				titles = getTitles(parkingLots);
			}
			if (titles.size() > position) {
				return titles.get(position);
			}
			return "";
		}
	}

	@Override
	protected void readedCardNeededData(String NFC_CODE) {
		saveNfcCode(NFC_CODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case HandlerMessageCodes.INTENT_ACTION_CODE_ZXING_REQUEST_CODE:
				String s = (String) data.getBundleExtra("Zxing").get("result");
				// 如果parkingCode中不全是数字则，提示用户重新扫描正确的车位铭牌
				if (s.matches("^[0-9]*$")) {
					// "^[0-9]*$"表示parkingCode只能是数字
					// "^\d{m,n}$"表示parkingCode只能是从m到n位的数字
					saveParkingLotTDC(s);
				} else {
					Toast.makeText(NewAddParkingLotsActivity.this,
							"请扫描车位铭牌二维码!", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
		}
	}

	private void startProgress() {
		actionbar_progressBar.setIndeterminate(false);
		actionbar_progressBar.setVisibility(View.VISIBLE);
		actionbar_refresh_button.setVisibility(View.GONE);
	}

	private void stopProgress() {
		actionbar_progressBar.setIndeterminate(true);
		actionbar_progressBar.setVisibility(View.GONE);
		actionbar_refresh_button.setVisibility(View.VISIBLE);
	}
}
