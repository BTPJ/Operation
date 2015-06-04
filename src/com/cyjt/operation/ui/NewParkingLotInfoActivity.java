package com.cyjt.operation.ui;

import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cyjt.operation.R;
import com.cyjt.operation.base.AppContext;
import com.cyjt.operation.base.BaseActivityWithNFC;
import com.cyjt.operation.base.HandlerMessageCodes;
import com.cyjt.operation.bean.BaseStation;
import com.cyjt.operation.bean.LocBaseStation;
import com.cyjt.operation.bean.ParkingLot;
import com.cyjt.operation.fragment.DialogFragmentForSensorHeartBeats;
import com.cyjt.operation.fragment.DynamicPickBasestationDialogFragment;
import com.cyjt.operation.fragment.DynamicPickBasestationDialogFragment.OnBasestationPickedListener;
import com.cyjt.operation.fragment.ParkingLotCheckFragment;
import com.cyjt.operation.fragment.ParkingLotCheckFragment.ParkingLotCheckFragListener;
import com.cyjt.operation.fragment.ParkingLotNewEditFragment;
import com.cyjt.operation.fragment.ParkingLotNewEditFragment.ParkingLotNewEditFragListener;
import com.cyjt.operation.sqlit.DBManager;

/**
 * 车位信息界面<BR>
 * 包含节点以及地磁信息
 * 
 * @author kullo<BR>
 *         2014-10-28 上午9:10:20<BR>
 */
public class NewParkingLotInfoActivity extends BaseActivityWithNFC implements
		ParkingLotCheckFragListener, ParkingLotNewEditFragListener,
		OnBasestationPickedListener {
	private FragmentManager fragmentmanager;
	private ParkingLotCheckFragment plCheckFragment;
	// private ParkingLotEditFragment plEditFragment;
	private ParkingLotNewEditFragment plEditFragment;
	/** 当前页面标志，0表示查看页面、1标志编辑页面 */
	private int currentActionFlag = 1;
	private TextView actionbar_textView_title;
	private TextView actionbar_refresh_button;
	private Button jiedian_heart, jizhan_heart;
	private ParkingLot currentParkingLot;
	private BaseStation currentBaseStation;
	private BaseStation currentSupportBaseStation;
	private ProgressBar actionbar_progressBar;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerMessageCodes.HTTP_BUILDER_FETCH_PARKINGLOT_INFO_SUCCEED:
				if ((ParkingLot) msg.obj != null && !hasDismissed) {
					currentParkingLot = (ParkingLot) msg.obj;
					changeFragment(currentActionFlag);
					getBaseStationInfo();
				}
				break;
			case HandlerMessageCodes.HTTP_BUILDER_FETCH_PARKINGLOT_INFO_FAILED:

				break;
			case HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATION_INFO_SUCCEED:
				currentBaseStation = (BaseStation) msg.obj;
				break;
			case HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATION_INFO_FAILED:

				break;
			case HandlerMessageCodes.HTTP_BUILDER_DELETE_NODE_INFO_SUCCEED:
				switch (msg.arg1) {
				case 0:
					currentParkingLot.setSensorCode("");

					break;
				case 1:
					currentParkingLot.setSensorCode1("");

					break;

				default:
					break;
				}
				// 删掉之后更新界面
				if (plEditFragment != null) {
					plEditFragment.refreshParkingLot(currentParkingLot);
				}
				break;
			case HandlerMessageCodes.HTTP_BUILDER_DELETE_NODE_INFO_FAILED:

				break;
			case HandlerMessageCodes.HTTP_BUILDER_DELETE_LOT_INFO_SUCCEED:
				// 删除车位成功
				onBackPressed();
				break;
			case HandlerMessageCodes.HTTP_BUILDER_DELETE_LOT_INFO_FAILED:

				break;
			case HandlerMessageCodes.SUBMIT_LOT_INFO_FAILED:

				break;
			case HandlerMessageCodes.SUBMIT_LOT_INFO_SUCCEED:
				Toast.makeText(NewParkingLotInfoActivity.this, "部署成功",
						Toast.LENGTH_SHORT).show();
				// 编辑成功
				onBackPressed();
				break;
			case HandlerMessageCodes.HTTP_BUILDER_SUBMIT_BASESTATION_MAPPING_SUCCEED:
				// 提交关系成功
				if (plEditFragment != null) {
					if (("").equals(currentParkingLot.getSensorCode())) {
						Log.i("demo", "nfcCode=" + nfcCode);
						currentParkingLot.setSensorCode(nfcCode);
					} else if (("").equals(currentParkingLot.getSensorCode1())) {
						currentParkingLot.setSensorCode1(nfcCode);
					} else {
						// 没有多余的地方添加
						return;
					}
					plEditFragment.refreshParkingLot(currentParkingLot);
				}
				break;
			default:
				break;
			}
			stopProgress();
		}
	};
	private boolean isCurrentLotActivited = false;
	private boolean hasDismissed = false;

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		hasDismissed = true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fragmentmanager = getFragmentManager();
		daManager = new DBManager(NewParkingLotInfoActivity.this);
		setContentView(R.layout.activity_parking_lot_info);
		initActionBar();
		if (getIntent().getSerializableExtra("parkingLot") == null) {
			Toast.makeText(NewParkingLotInfoActivity.this, "车位对象不存在",
					Toast.LENGTH_LONG).show();
			return;
		} else {
			currentParkingLot = (ParkingLot) getIntent().getSerializableExtra(
					"parkingLot");
			getParkingLotInfo();
			isCurrentLotActivited = currentParkingLot.isHasActivited();
			Log.v("demo", "该车位是否已激活？" + currentParkingLot.isHasActivited());
		}
		Log.i("demo", "getSensorCode1=" + currentParkingLot.getSensorCode1());
		initView();
		viewEvent();
	}

	private void initActionBar() {
		actionbar_progressBar = (ProgressBar) findViewById(R.id.actionbar_progressBar);
		actionbar_textView_title = (TextView) findViewById(R.id.actionbar_textView_title);
		actionbar_refresh_button = (TextView) findViewById(R.id.actionbar_refresh_button);
		jiedian_heart = (Button) findViewById(R.id.jiedian_heart);
		jizhan_heart = (Button) findViewById(R.id.jizhan_heart);
		jiedian_heart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragmentForSensorHeartBeats fragment = new DialogFragmentForSensorHeartBeats();
				Bundle args = new Bundle();
				args.putString("sensorCode", currentParkingLot.getSensorCode1());
				args.putBoolean("isCurrentLotActivited", isCurrentLotActivited);
				fragment.setArguments(args);
				fragment.setStyle(DialogFragment.STYLE_NO_TITLE,
						android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth);
				fragment.show(getFragmentManager(),
						"SensorHeartBeatsListDialogFragment");
			}
		});

		jizhan_heart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle args = new Bundle();
				args.putInt("actionFlag", 1);
				fragment = new DynamicPickBasestationDialogFragment();
				fragment.setArguments(args);
				fragment.setStyle(DialogFragment.STYLE_NO_TITLE,
						android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth);
				fragment.show(fragmentmanager,
						"DynamicPickBasestationDialogFragment");
			}
		});
		/** 默认为查看 */
		changeActionbar(currentActionFlag);
		actionbar_refresh_button.setVisibility(View.GONE);
		((ImageView) findViewById(R.id.actionbar_imageView_back_button))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (currentActionFlag == 0) {
							// 查看界面下直接返回即可
							onBackPressed();
						} else if (currentActionFlag == 1) {
							// 交换页面为查看
							currentActionFlag = 0;
							// changeFragment(currentActionFlag);
							NewParkingLotInfoActivity.this.finish();

						}
					}
				});

		actionbar_refresh_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 刷新数据，或其他操作
				if (currentActionFlag == 0) {
					// 交换页面为编辑
					currentActionFlag = 1;
					changeFragment(currentActionFlag);
				}
			}
		});
		stopProgress();
	}

	private void initView() {

	}

	private void viewEvent() {

	}

	/** 改变actionBar之前要先将currentActionFlag进行改变才可以 */
	private void changeActionbar(int currentActionFlag) {
		switch (currentActionFlag) {
		case 0:
			actionbar_textView_title.setText("查看车位");
			actionbar_refresh_button.setVisibility(View.VISIBLE);
			actionbar_refresh_button.setText("编辑");
			break;
		case 1:
			actionbar_textView_title.setText("部署");
			actionbar_refresh_button.setVisibility(View.GONE);
			actionbar_refresh_button.setText("提交");
			break;
		default:
			break;
		}
	}

	/** 改变Fragment之前要先将currentActionFlag进行改变才可以 */
	private void changeFragment(int currentActionFlag) {
		FragmentTransaction transaction = fragmentmanager.beginTransaction();
		// 先隐藏全部内容
		hideDrawerFragments(transaction);
		// 判断当前抽屉应该要显示的状态值，然后更改抽屉
		if (currentActionFlag == 0) {
			TurnFragmentToCheck(transaction);
		} else if (currentActionFlag == 1) {
			TurnFragmentToEdit(transaction);
		}
		if (transaction == null)
			return;

		transaction.commit();
		changeActionbar(currentActionFlag);
	}

	private void hideDrawerFragments(FragmentTransaction transaction) {
		if (plCheckFragment != null) {
			transaction.hide(plCheckFragment);
		}
		if (plEditFragment != null) {
			transaction.hide(plEditFragment);
		}
	}

	private void TurnFragmentToEdit(FragmentTransaction transaction) {
		if (plEditFragment == null) {
			// plEditFragment = new ParkingLotEditFragment();
			plEditFragment = new ParkingLotNewEditFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable("parkingLot", currentParkingLot);
			plEditFragment.setArguments(bundle);
			transaction.add(R.id.frameLayout_for_parkingLot, plEditFragment);
		}
		transaction.show(plEditFragment);
	}

	private void TurnFragmentToCheck(FragmentTransaction transaction) {
		if (plCheckFragment == null) {
			plCheckFragment = new ParkingLotCheckFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable("parkingLot", currentParkingLot);
			plCheckFragment.setArguments(bundle);
			transaction.add(R.id.frameLayout_for_parkingLot, plCheckFragment);
		}
		transaction.show(plCheckFragment);
	}

	private void getParkingLotInfo() {
		startProgress();
		AppContext.getInstance().getVolleyTools()
				.fetchParkingLotInfo(handler, currentParkingLot.getCode());
	}

	/**
	 * 获取车位上的基站
	 */
	private void getBaseStationInfo() {
		if (currentParkingLot.isHasActivited()) {
			startProgress();
			AppContext
					.getInstance()
					.getVolleyTools()
					.fetchBasestationInfo(handler,
							currentParkingLot.getBasestationNfcCode());
		}
	}

	private DynamicPickBasestationDialogFragment fragment;
	private DBManager daManager;

	private void openBaseStationPickFragment(int actionCode) {
		Bundle args = new Bundle();
		args.putInt("actionFlag", actionCode);
		switch (actionCode) {
		case 1:
			args.putSerializable("baseStations",
					daManager.getOriLocSupportBasestations(0, 5));

			break;

		default:
			break;
		}
		if (fragment != null && fragment.isVisible())
			return;
		fragment = new DynamicPickBasestationDialogFragment();
		fragment.setArguments(args);
		fragment.setStyle(DialogFragment.STYLE_NO_TITLE,
				android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth);
		fragment.show(fragmentmanager, "DynamicPickBasestationDialogFragment");
	}

	@Override
	public void onSensorBeClicked(String sensorCode) {
		DialogFragmentForSensorHeartBeats fragment = new DialogFragmentForSensorHeartBeats();
		Bundle args = new Bundle();
		args.putString("sensorCode", sensorCode);
		args.putBoolean("isCurrentLotActivited", isCurrentLotActivited);
		fragment.setArguments(args);
		fragment.setStyle(DialogFragment.STYLE_NO_TITLE,
				android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth);
		fragment.show(getFragmentManager(),
				"SensorHeartBeatsListDialogFragment");
	}

	@Override
	public void onSensorCodeBeClicked(int index) {
		switch (index) {
		case 0:
			if (("").equals(currentParkingLot.getSensorCode())) {
				if (currentSupportBaseStation == null) {
					Toast.makeText(NewParkingLotInfoActivity.this, "请先添加部署基站",
							Toast.LENGTH_LONG).show();
					openBaseStationPickFragment(1);
				} else {

				}
				return;
			} else {
				handler.sendMessage(handler
						.obtainMessage(
								HandlerMessageCodes.HTTP_BUILDER_DELETE_NODE_INFO_SUCCEED,
								0, 1));

			}
			break;
		case 1:
			if (("").equals(currentParkingLot.getSensorCode1())) {
				Toast.makeText(NewParkingLotInfoActivity.this, "请先添加部署基站",
						Toast.LENGTH_LONG).show();
				openBaseStationPickFragment(1);
				return;
			} else {
				handler.sendMessage(handler
						.obtainMessage(
								HandlerMessageCodes.HTTP_BUILDER_DELETE_NODE_INFO_SUCCEED,
								1, 1));
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onParkingLotEdited(ParkingLot parkinglot) {
		startProgress();
		// if (parkinglot.getSensorCode().equals("")
		// || parkinglot.getSensorCode1().equals("")) {
		// Toast.makeText(NewParkingLotInfoActivity.this, "请将信息补充完整",
		// Toast.LENGTH_SHORT).show();
		// return;
		// }
		if (parkinglot.getSensorCode1().equals("")) {
			Toast.makeText(NewParkingLotInfoActivity.this, "请将信息补充完整",
					Toast.LENGTH_SHORT).show();
			return;
		}
		// 提交
		AppContext.getInstance().getVolleyTools()
				.submitBinding(handler, parkinglot);
	}

	@Override
	public void onParkingLotDeleted(ParkingLot parkinglot) {
		startProgress();
		// 提交
		AppContext.getInstance().getVolleyTools()
				.deleteLot(handler, parkinglot);
	}

	@Override
	public void OnBasestationPicked(LocBaseStation baseStation, int actionFlag) {
		// 0.保存基站到内存，到数据库
		currentSupportBaseStation = new BaseStation(-1, baseStation.getCode(),
				baseStation.getCode(), -1, -1, 0.0, 0.0,
				baseStation.getDescription(), baseStation.getDescription(),
				new Date());
		// if (daManager != null) {
		// daManager.saveOriLocSupportBasestation(baseStation);
		// }
		// // 1.在界面中应用基站信息
		// upSupportBaseStationUI(0);
		// 提交映射关系
		if (currentBaseStation != null && currentSupportBaseStation != null) {
			startProgress();
			AppContext
					.getInstance()
					.getVolleyTools()
					.httpSubmitBasestationMapping(handler, currentBaseStation,
							currentSupportBaseStation);
		} else {
			Toast.makeText(NewParkingLotInfoActivity.this, "没有部署基站或运行基站",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void addBasestation(int actionFlag) {
		// 打开二维码扫描请求
		Intent i = new Intent(NewParkingLotInfoActivity.this,
				ZxingCaptureActivity.class);
		i.putExtra("useDescribe", "请扫描基站二维码");
		startActivityForResult(i,
				HandlerMessageCodes.INTENT_ACTION_CODE_ZXING_REQUEST_CODE);

	}

	private String nfcCode = "";

	@Override
	protected void readedCardNeededData(String NFC_CODE) {
		nfcCode = NFC_CODE;
		changeFragment(1);
		currentParkingLot.setSensorCode1(nfcCode);
		plEditFragment.refreshParkingLot(currentParkingLot);
		// if (currentSupportBaseStation == null) {
		// Toast.makeText(NewParkingLotInfoActivity.this, "请先添加部署基站",
		// Toast.LENGTH_LONG).show();
		// openBaseStationPickFragment(1);
		// } else {
		// handler.sendEmptyMessage(HandlerMessageCodes.HTTP_BUILDER_SUBMIT_BASESTATION_MAPPING_SUCCEED);
		// }
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
