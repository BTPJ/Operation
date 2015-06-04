package com.cyjt.operation.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cyjt.operation.R;
import com.cyjt.operation.base.AppContext;
import com.cyjt.operation.base.ComparatorParkingLot;
import com.cyjt.operation.base.HandlerMessageCodes;
import com.cyjt.operation.bean.BaseStation;
import com.cyjt.operation.bean.BaseStationHeartBeat;
import com.cyjt.operation.bean.LocBaseStation;
import com.cyjt.operation.bean.ParkingLot;
import com.cyjt.operation.fragment.DynamicPickBasestationDialogFragment;
import com.cyjt.operation.fragment.DynamicPickBasestationDialogFragment.OnBasestationPickedListener;
import com.cyjt.operation.fragment.ParkingLotListFragment;
import com.cyjt.operation.fragment.ParkingLotListFragment.ParkingLotFragmentListener;
import com.cyjt.operation.fragment.YesOrNoDialogFragment;
import com.cyjt.operation.fragment.YesOrNoDialogFragment.YesOrNoFragmentActionListener;
import com.cyjt.operation.sqlit.DBManager;

public class NewPickParkingLotActivity extends Activity implements
		ParkingLotFragmentListener, OnBasestationPickedListener,
		YesOrNoFragmentActionListener {
	private DBManager daManager;
	private FragmentManager fragmentmanager;
	private ParkingLotListFragment parkingLotsFragment;
	private DynamicPickBasestationDialogFragment fragment;
	private BaseStation currentBaseStation = null;
	private BaseStation currentSupportBaseStation = null;
	private final int INTENT_REQUEST_CODE_FOR_ADD_PARKING_LOT = 0;
	private TextView textView_basestation_parkinglot_count_edit;
	private TextView textView_support_basestation_code_edit;
	private TextView textView_basestation_support_parkinglot_count_edit;
	private RelativeLayout relativeLayout_for_basestation;
	private RelativeLayout relativeLayout_for_support_basestation;
	private ProgressBar actionbar_progressBar;
	private TextView actionbar_refresh_button;
	private TextView basestation_status;
	private ArrayList<ParkingLot> parkinglots = new ArrayList<ParkingLot>();
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATION_LOTS_SUCCEED:
				switch (msg.arg1) {
				case 0:
					// 获取正式基站下的车位数据
					parkinglots = (ArrayList<ParkingLot>) msg.obj;
					if (parkinglots != null) {
						upCount(parkinglots.size());
					}
					// 获取的是正式基站下的数据
					String supportBSCode = daManager
							.getBaseStationMap(currentBaseStation);
					// 判断这个关系表是否存在
					if (supportBSCode != null && !supportBSCode.equals("")) {
						if (currentSupportBaseStation == null) {
							currentSupportBaseStation = new BaseStation();
						}
						currentSupportBaseStation.setCode(supportBSCode);
						currentSupportBaseStation.setNfcCode(supportBSCode);
						// 请求网络，获取部署基站下未部署成功的车位
						getParkingLots(currentSupportBaseStation, 1);
					} else {
						if (currentSupportBaseStation != null) {
							getParkingLots(currentSupportBaseStation, 1);
						} else {
							// 显示存在的数据
							showParkingLots();
						}
					}
					break;
				case 1:
					// 获取的是部署基站下的数据
					ArrayList<ParkingLot> parkinglotss = (ArrayList<ParkingLot>) msg.obj;
					for (ParkingLot parkinglot : parkinglotss) {
						parkinglot.setHasActivited(false);
					}
					if (parkinglotss != null && parkinglotss.size() > 0) {
						parkinglots.addAll(parkinglotss);
						upSupportBaseStationUI(parkinglotss.size());
					} else {
						// 如果获取到部署基站下的数据为空，说明部署完成，删除这个关系表
						daManager
								.deleteBaseStationMap(currentSupportBaseStation);
					}
					showParkingLots();
					break;
				default:
					break;
				}
			case HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATION_LOTS_FAILED:

				break;
			case HandlerMessageCodes.HTTP_BUILDER_SUBMIT_BASESTATION_MAPPING_SUCCEED:
				Toast.makeText(NewPickParkingLotActivity.this, "基站关联提交成功",
						Toast.LENGTH_SHORT).show();
				getSupportBsHeart(currentSupportBaseStation.getCode());
				break;
			case HandlerMessageCodes.HTTP_BUILDER_DELETE_LOT_INFO_SUCCEED:
				Toast.makeText(NewPickParkingLotActivity.this, "删除成功",
						Toast.LENGTH_SHORT).show();
				// 刷新界面
				getParkingLots(currentBaseStation, 0);
				break;
			case HandlerMessageCodes.HTTP_BUILDER_DELETE_LOT_INFO_FAILED:
				break;
			case HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATION_HEART_BEAT_SUCCEED:
				List<BaseStationHeartBeat> sbsh = (List<BaseStationHeartBeat>) msg.obj;
				basestation_status.setVisibility(View.VISIBLE);
				if (sbsh.size() < 2) {
					// 1分钟后再刷新
					handler.sendEmptyMessageDelayed(
							HandlerMessageCodes.HANDLER_CODE_REFREASH_SUPPORTBS_HEART,
							1000);

					return;
				}
				if ((sbsh.get(0).getReceiveAt().getTime() - sbsh.get(1)
						.getReceiveAt().getTime()) > 60 * 1000) {
					// 离线
					basestation_status
							.setBackgroundResource(R.drawable.fillet_shape_background_for_list_item_circle_gray);
				} else {
					// 在线
					basestation_status
							.setBackgroundResource(R.drawable.fillet_shape_background_for_list_item_circle_green);
					openAddParkingLots();
				}
				break;
			case HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATION_HEART_BEAT_FAILED:
				Toast.makeText(NewPickParkingLotActivity.this, "部署基站不在线",
						Toast.LENGTH_SHORT).show();
				basestation_status.setVisibility(View.VISIBLE);
				basestation_status
						.setBackgroundResource(R.drawable.fillet_shape_background_for_list_item_circle_gray);
				break;
			case HandlerMessageCodes.HANDLER_CODE_REFREASH_SUPPORTBS_HEART:
				getSupportBsHeart(currentSupportBaseStation.getCode());
				break;
			default:
				break;
			}
			stopProgress();
			if (parkingLotsFragment != null) {
				parkingLotsFragment.stopFreashView();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fragmentmanager = getFragmentManager();
		daManager = new DBManager(NewPickParkingLotActivity.this);
		setContentView(R.layout.activity_new_parking_lots);
		initActionBar();
		if (getIntent().getSerializableExtra("baseStation") == null) {
			return;
		}
		currentBaseStation = (BaseStation) getIntent().getSerializableExtra(
				"baseStation");
		setFragment();
		initView();
		viewEvent();
		// 默认开启后即要请求正式基站下的车位数据
		getParkingLots(currentBaseStation, 0);
		// 获取有效的部署基站信息
		// 取出暂存在本地的部署基站下的车位
	}

	private void setFragment() {
		if (parkingLotsFragment == null)
			parkingLotsFragment = new ParkingLotListFragment();
		fragmentmanager.beginTransaction()
				.add(R.id.frameLayout_for_parkingLots, parkingLotsFragment)
				.commit();
	}

	private void initView() {
		((TextView) findViewById(R.id.textView_basestation_code_edit))
				.setText("" + currentBaseStation.getNfcCode());
		textView_basestation_parkinglot_count_edit = (TextView) findViewById(R.id.textView_basestation_parkinglot_count_edit);
		textView_support_basestation_code_edit = (TextView) findViewById(R.id.textView_support_basestation_code_edit);
		textView_basestation_support_parkinglot_count_edit = (TextView) findViewById(R.id.textView_basestation_support_parkinglot_count_edit);
		basestation_status = (TextView) findViewById(R.id.basestation_status);
		relativeLayout_for_basestation = (RelativeLayout) findViewById(R.id.relativeLayout_for_basestation);
		relativeLayout_for_support_basestation = (RelativeLayout) findViewById(R.id.relativeLayout_for_support_basestation);
	}

	private void viewEvent() {
		relativeLayout_for_basestation
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 查看基站
						Intent intent = new Intent(
								NewPickParkingLotActivity.this,
								NewBaseStationInfoActivity.class);
						intent.putExtra("baseStation", currentBaseStation);
						startActivity(intent);
					}
				});
		relativeLayout_for_support_basestation
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Toast.makeText(NewPickParkingLotActivity.this,
								"选取部署基站", Toast.LENGTH_SHORT).show();
						openBaseStationPickFragment(1);
					}
				});
	}

	private void initActionBar() {
		actionbar_progressBar = (ProgressBar) findViewById(R.id.actionbar_progressBar);
		((TextView) findViewById(R.id.actionbar_textView_title)).setText("车位");
		((ImageView) findViewById(R.id.actionbar_imageView_back_button))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						onBackPressed();
					}
				});
		actionbar_refresh_button = (TextView) findViewById(R.id.actionbar_refresh_button);
		actionbar_refresh_button.setText("添加");
		actionbar_refresh_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (currentSupportBaseStation != null) {
					Toast.makeText(NewPickParkingLotActivity.this, "添加车位",
							Toast.LENGTH_SHORT).show();
					// 跳转添加车位
					openAddParkingLots();
				} else {
					Toast.makeText(NewPickParkingLotActivity.this, "选取部署基站",
							Toast.LENGTH_SHORT).show();
					openBaseStationPickFragment(1);
				}
			}
		});
		stopProgress();
	}

	private void showParkingLots() {
		// 对parkinglots进行排序
		Collections.sort(parkinglots, new ComparatorParkingLot());
		// 判断本地有无数据
		if (parkingLotsFragment != null) {
			parkingLotsFragment.freshParkingLotsData(parkinglots);
		}
	}

	private void upCount(int count) {
		textView_basestation_parkinglot_count_edit.setText("" + count);
	}

	/**
	 * 填充部署基站的信息
	 * 
	 * @param count
	 */
	private void upSupportBaseStationUI(int count) {
		textView_basestation_support_parkinglot_count_edit.setText("" + count);
		textView_support_basestation_code_edit.setText(""
				+ currentSupportBaseStation.getCode());

	}

	private void openBaseStationPickFragment(int actionCode) {
		Bundle args = new Bundle();
		args.putInt("actionFlag", actionCode);
		switch (actionCode) {
		case 0:
			args.putSerializable("baseStations",
					daManager.getOriLocBasestations(0, 5));

			break;
		case 1:
			args.putSerializable("baseStations",
					daManager.getOriLocSupportBasestations(0, 5));

			break;

		default:
			break;
		}
		fragment = new DynamicPickBasestationDialogFragment();
		fragment.setArguments(args);
		fragment.setStyle(DialogFragment.STYLE_NO_TITLE,
				android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth);
		// android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth);
		fragment.show(fragmentmanager, "DynamicPickBasestationDialogFragment");
	}

	/**
	 * 
	 * @param basestation
	 * @param flag
	 *            0正式基站，1 部署基站
	 */
	private void getParkingLots(BaseStation basestation, int flag) {
		if (currentBaseStation != null) {
			AppContext.getInstance().getVolleyTools()
					.httpFetchBasestationLots(handler, basestation, flag);
			startProgress();
		}
	}

	/**
	 * 打开添加车位界面
	 */
	private void openAddParkingLots() {
		Intent intent = new Intent(NewPickParkingLotActivity.this,
				NewAddParkingLotsActivity.class);
		intent.putExtra("basestation", currentBaseStation);
		intent.putExtra("supportBasestation", currentSupportBaseStation);
		startActivityForResult(intent, INTENT_REQUEST_CODE_FOR_ADD_PARKING_LOT);
		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case HandlerMessageCodes.INTENT_ACTION_CODE_ZXING_REQUEST_CODE:
				String code = (String) data.getBundleExtra("Zxing").get(
						"result");
				LocBaseStation baseStation = new LocBaseStation(-1, code,
						"部署基站");
				OnBasestationPicked(baseStation, 1);
				break;
			case INTENT_REQUEST_CODE_FOR_ADD_PARKING_LOT:
				// 请求刷新
				getParkingLots(currentBaseStation, 0);
				break;
			default:
				break;
			}
		}
	}

	private ParkingLot prepareToDeletePl;

	@Override
	public void OnParkingLotBeCilcked(ParkingLot parkingLot) {
		// if (parkingLot.isHasActivited()) {
		Toast.makeText(NewPickParkingLotActivity.this,
				"查看车位：" + parkingLot.getCode(), Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(NewPickParkingLotActivity.this,
				NewParkingLotInfoActivity.class);
		intent.putExtra("parkingLot", parkingLot);
		startActivity(intent);
		// } else {

		// prepareToDeletePl = parkingLot;
		// // 弹出提示，是否要删除该车位
		// YesOrNoDialogFragment yesOrNoFrag = new YesOrNoDialogFragment();
		// Bundle b = new Bundle();
		// b.putStringArray("stringArray", new String[] {
		// "这个车位正在激活中，确定要删除或重新部署这个车位吗？", "取消", "删除" });
		// yesOrNoFrag.setArguments(b);
		// yesOrNoFrag.setStyle(DialogFragment.STYLE_NO_TITLE,
		// android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth);
		// // android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth);
		// yesOrNoFrag.show(getFragmentManager(), "YesOrNoDialogFragment");
		// }
	}

	@Override
	public void OnParkingLotBeLongCilcked(ParkingLot parkingLot) {
		prepareToDeletePl = parkingLot;
		// 弹出提示，是否要删除该车位
		YesOrNoDialogFragment yesOrNoFrag = new YesOrNoDialogFragment();
		Bundle b = new Bundle();
		b.putStringArray("stringArray", new String[] {
				"这个车位正在激活中，确定要删除或重新部署这个车位吗？", "取消", "删除" });
		yesOrNoFrag.setArguments(b);
		yesOrNoFrag.setStyle(DialogFragment.STYLE_NO_TITLE,
				android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth);
		// android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth);
		yesOrNoFrag.show(getFragmentManager(), "YesOrNoDialogFragment");
	}

	@Override
	public void OnBasestationPicked(LocBaseStation baseStation, int actionFlag) {
		// 0.保存基站到内存，到数据库
		currentSupportBaseStation = new BaseStation(-1, baseStation.getCode(),
				baseStation.getCode(), -1, -1, 0.0, 0.0,
				baseStation.getDescription(), baseStation.getDescription(),
				new Date());
		if (daManager != null) {
			daManager.saveOriLocSupportBasestation(baseStation);
		}
		// 1.在界面中应用基站信息
		upSupportBaseStationUI(0);
		// 提交映射关系
		AppContext
				.getInstance()
				.getVolleyTools()
				.httpSubmitBasestationMapping(handler, currentBaseStation,
						currentSupportBaseStation);
	}

	@Override
	public void addBasestation(int actionFlag) {
		// 打开二维码扫描请求
		Intent i = new Intent(NewPickParkingLotActivity.this,
				ZxingCaptureActivity.class);
		i.putExtra("useDescribe", "请扫描基站二维码");
		startActivityForResult(i,
				HandlerMessageCodes.INTENT_ACTION_CODE_ZXING_REQUEST_CODE);
	}

	@Override
	public void OnParkingLotsRefreashed() {
		// 刷新
		getParkingLots(currentBaseStation, 0);
	}

	@Override
	public void onSureClicked() {
		// 删除prepareToDeletePl
		// if (prepareToDeletePl != null) {
		// ArrayList<ParkingLot> pls = new ArrayList<ParkingLot>();
		// pls.add(prepareToDeletePl);
		// daManager.deleteParkingLots(pls);
		// OnParkingLotsRefreashed();
		// }
		AppContext.getInstance().getVolleyTools()
				.deleteLot(handler, prepareToDeletePl);
	}

	private void getsupprotBasestationInfo(String sbsCode) {
		AppContext.getInstance().getVolleyTools()
				.fetchBasestationInfo(handler, sbsCode);
	}

	/** 获取部署基站的心跳 */
	private void getSupportBsHeart(String sbsCode) {
		AppContext.getInstance().getVolleyTools()
				.fetchBasestationHeartBeat(handler, sbsCode);
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
