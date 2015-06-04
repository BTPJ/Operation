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
 * ��λ��Ϣ����<BR>
 * �����ڵ��Լ��ش���Ϣ
 * 
 * @author kullo<BR>
 *         2014-10-28 ����9:10:20<BR>
 */
public class NewParkingLotInfoActivity extends BaseActivityWithNFC implements
		ParkingLotCheckFragListener, ParkingLotNewEditFragListener,
		OnBasestationPickedListener {
	private FragmentManager fragmentmanager;
	private ParkingLotCheckFragment plCheckFragment;
	// private ParkingLotEditFragment plEditFragment;
	private ParkingLotNewEditFragment plEditFragment;
	/** ��ǰҳ���־��0��ʾ�鿴ҳ�桢1��־�༭ҳ�� */
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
				// ɾ��֮����½���
				if (plEditFragment != null) {
					plEditFragment.refreshParkingLot(currentParkingLot);
				}
				break;
			case HandlerMessageCodes.HTTP_BUILDER_DELETE_NODE_INFO_FAILED:

				break;
			case HandlerMessageCodes.HTTP_BUILDER_DELETE_LOT_INFO_SUCCEED:
				// ɾ����λ�ɹ�
				onBackPressed();
				break;
			case HandlerMessageCodes.HTTP_BUILDER_DELETE_LOT_INFO_FAILED:

				break;
			case HandlerMessageCodes.SUBMIT_LOT_INFO_FAILED:

				break;
			case HandlerMessageCodes.SUBMIT_LOT_INFO_SUCCEED:
				Toast.makeText(NewParkingLotInfoActivity.this, "����ɹ�",
						Toast.LENGTH_SHORT).show();
				// �༭�ɹ�
				onBackPressed();
				break;
			case HandlerMessageCodes.HTTP_BUILDER_SUBMIT_BASESTATION_MAPPING_SUCCEED:
				// �ύ��ϵ�ɹ�
				if (plEditFragment != null) {
					if (("").equals(currentParkingLot.getSensorCode())) {
						Log.i("demo", "nfcCode=" + nfcCode);
						currentParkingLot.setSensorCode(nfcCode);
					} else if (("").equals(currentParkingLot.getSensorCode1())) {
						currentParkingLot.setSensorCode1(nfcCode);
					} else {
						// û�ж���ĵط����
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
			Toast.makeText(NewParkingLotInfoActivity.this, "��λ���󲻴���",
					Toast.LENGTH_LONG).show();
			return;
		} else {
			currentParkingLot = (ParkingLot) getIntent().getSerializableExtra(
					"parkingLot");
			getParkingLotInfo();
			isCurrentLotActivited = currentParkingLot.isHasActivited();
			Log.v("demo", "�ó�λ�Ƿ��Ѽ��" + currentParkingLot.isHasActivited());
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
		/** Ĭ��Ϊ�鿴 */
		changeActionbar(currentActionFlag);
		actionbar_refresh_button.setVisibility(View.GONE);
		((ImageView) findViewById(R.id.actionbar_imageView_back_button))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (currentActionFlag == 0) {
							// �鿴������ֱ�ӷ��ؼ���
							onBackPressed();
						} else if (currentActionFlag == 1) {
							// ����ҳ��Ϊ�鿴
							currentActionFlag = 0;
							// changeFragment(currentActionFlag);
							NewParkingLotInfoActivity.this.finish();

						}
					}
				});

		actionbar_refresh_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ˢ�����ݣ�����������
				if (currentActionFlag == 0) {
					// ����ҳ��Ϊ�༭
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

	/** �ı�actionBar֮ǰҪ�Ƚ�currentActionFlag���иı�ſ��� */
	private void changeActionbar(int currentActionFlag) {
		switch (currentActionFlag) {
		case 0:
			actionbar_textView_title.setText("�鿴��λ");
			actionbar_refresh_button.setVisibility(View.VISIBLE);
			actionbar_refresh_button.setText("�༭");
			break;
		case 1:
			actionbar_textView_title.setText("����");
			actionbar_refresh_button.setVisibility(View.GONE);
			actionbar_refresh_button.setText("�ύ");
			break;
		default:
			break;
		}
	}

	/** �ı�Fragment֮ǰҪ�Ƚ�currentActionFlag���иı�ſ��� */
	private void changeFragment(int currentActionFlag) {
		FragmentTransaction transaction = fragmentmanager.beginTransaction();
		// ������ȫ������
		hideDrawerFragments(transaction);
		// �жϵ�ǰ����Ӧ��Ҫ��ʾ��״ֵ̬��Ȼ����ĳ���
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
	 * ��ȡ��λ�ϵĻ�վ
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
					Toast.makeText(NewParkingLotInfoActivity.this, "������Ӳ����վ",
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
				Toast.makeText(NewParkingLotInfoActivity.this, "������Ӳ����վ",
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
		// Toast.makeText(NewParkingLotInfoActivity.this, "�뽫��Ϣ��������",
		// Toast.LENGTH_SHORT).show();
		// return;
		// }
		if (parkinglot.getSensorCode1().equals("")) {
			Toast.makeText(NewParkingLotInfoActivity.this, "�뽫��Ϣ��������",
					Toast.LENGTH_SHORT).show();
			return;
		}
		// �ύ
		AppContext.getInstance().getVolleyTools()
				.submitBinding(handler, parkinglot);
	}

	@Override
	public void onParkingLotDeleted(ParkingLot parkinglot) {
		startProgress();
		// �ύ
		AppContext.getInstance().getVolleyTools()
				.deleteLot(handler, parkinglot);
	}

	@Override
	public void OnBasestationPicked(LocBaseStation baseStation, int actionFlag) {
		// 0.�����վ���ڴ棬�����ݿ�
		currentSupportBaseStation = new BaseStation(-1, baseStation.getCode(),
				baseStation.getCode(), -1, -1, 0.0, 0.0,
				baseStation.getDescription(), baseStation.getDescription(),
				new Date());
		// if (daManager != null) {
		// daManager.saveOriLocSupportBasestation(baseStation);
		// }
		// // 1.�ڽ�����Ӧ�û�վ��Ϣ
		// upSupportBaseStationUI(0);
		// �ύӳ���ϵ
		if (currentBaseStation != null && currentSupportBaseStation != null) {
			startProgress();
			AppContext
					.getInstance()
					.getVolleyTools()
					.httpSubmitBasestationMapping(handler, currentBaseStation,
							currentSupportBaseStation);
		} else {
			Toast.makeText(NewParkingLotInfoActivity.this, "û�в����վ�����л�վ",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void addBasestation(int actionFlag) {
		// �򿪶�ά��ɨ������
		Intent i = new Intent(NewParkingLotInfoActivity.this,
				ZxingCaptureActivity.class);
		i.putExtra("useDescribe", "��ɨ���վ��ά��");
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
		// Toast.makeText(NewParkingLotInfoActivity.this, "������Ӳ����վ",
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
						"�����վ");
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
