package com.cyjt.operation.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
import com.cyjt.operation.bean.BaseStationHeartBeat;
import com.cyjt.operation.bean.LocBaseStation;
import com.cyjt.operation.bean.ParkingLot;
import com.cyjt.operation.bean.RoadsAreas;
import com.cyjt.operation.fragment.DynamicPickBasestationDialogFragment;
import com.cyjt.operation.fragment.ParkingLotListFragment;
import com.cyjt.operation.fragment.ParkingLotListFragment.ParkingLotFragmentListener;
import com.cyjt.operation.fragment.RoadAreasListFragment.OnRoadAreasClickListener;
import com.cyjt.operation.fragment.YesOrNoDialogFragment;
import com.cyjt.operation.fragment.YesOrNoDialogFragment.YesOrNoFragmentActionListener;
import com.cyjt.operation.sqlit.DBManager;

public class NewRoadPickParkingLotActivity extends Activity implements
		ParkingLotFragmentListener, OnRoadAreasClickListener,
		YesOrNoFragmentActionListener {
	private DBManager daManager;
	private FragmentManager fragmentmanager;
	private ParkingLotListFragment parkingLotsFragment;
	private DynamicPickBasestationDialogFragment fragment;
	private RoadsAreas currentBaseStation = null;
	private RoadsAreas currentSupportBaseStation = null;
	private final int INTENT_REQUEST_CODE_FOR_ADD_PARKING_LOT = 0;
	private TextView textView_basestation_parkinglot_count_edit;
	private TextView textView_support_basestation_code_edit;
	private TextView textView_basestation_support_parkinglot_count_edit,
			actionbar_textView_small_title;
	private RelativeLayout relativeLayout_for_basestation;
	private RelativeLayout relativeLayout_for_support_basestation;
	private String smallTitle;
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
			case HandlerMessageCodes.HTTP_BUILDER_FETCH_ROADARERSPACE_SUCCESS:
				Log.i("demo", "����·���³�λ�ɹ�");
				parkinglots = (ArrayList<ParkingLot>) msg.obj;
				if (currentSupportBaseStation != null) {
					getParkingLots(currentSupportBaseStation);
				} else {
					// ��ʾ���ڵ�����
					showParkingLots();
				}
				// // ��ȡ���ǲ����վ�µ�����
				// ArrayList<ParkingLot> parkinglotss = (ArrayList<ParkingLot>)
				// msg.obj;
				// for (ParkingLot parkinglot : parkinglotss) {
				// parkinglot.setHasActivited(false);
				// }
				// if (parkinglotss != null && parkinglotss.size() > 0) {
				// parkinglots.addAll(parkinglotss);
				// upSupportBaseStationUI(parkinglotss.size());
				// } else {
				//
				// }
				// showParkingLots();
				break;
			case HandlerMessageCodes.HTTP_BUILDER_FETCH_ROADARERSPACE_FAILED:
				break;
			case HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATION_LOTS_FAILED:

				break;
			case HandlerMessageCodes.HTTP_BUILDER_SUBMIT_BASESTATION_MAPPING_SUCCEED:
				Toast.makeText(NewRoadPickParkingLotActivity.this, "��վ�����ύ�ɹ�",
						Toast.LENGTH_SHORT).show();
				getSupportBsHeart(currentSupportBaseStation.getCode());
				break;
			case HandlerMessageCodes.HTTP_BUILDER_DELETE_LOT_INFO_SUCCEED:
				Toast.makeText(NewRoadPickParkingLotActivity.this, "ɾ���ɹ�",
						Toast.LENGTH_SHORT).show();
				// ˢ�½���
				getParkingLots(currentBaseStation);
				break;
			case HandlerMessageCodes.HTTP_BUILDER_DELETE_LOT_INFO_FAILED:
				break;
			case HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATION_HEART_BEAT_SUCCEED:
				List<BaseStationHeartBeat> sbsh = (List<BaseStationHeartBeat>) msg.obj;
				basestation_status.setVisibility(View.VISIBLE);
				if (sbsh.size() < 2) {
					// 1���Ӻ���ˢ��
					handler.sendEmptyMessageDelayed(
							HandlerMessageCodes.HANDLER_CODE_REFREASH_SUPPORTBS_HEART,
							1000);

					return;
				}
				if ((sbsh.get(0).getReceiveAt().getTime() - sbsh.get(1)
						.getReceiveAt().getTime()) > 60 * 1000) {
					// ����
					basestation_status
							.setBackgroundResource(R.drawable.fillet_shape_background_for_list_item_circle_gray);
				} else {
					// ����
					basestation_status
							.setBackgroundResource(R.drawable.fillet_shape_background_for_list_item_circle_green);
					openAddParkingLots();
				}
				break;
			case HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATION_HEART_BEAT_FAILED:
				Toast.makeText(NewRoadPickParkingLotActivity.this, "�����վ������",
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
		daManager = new DBManager(NewRoadPickParkingLotActivity.this);
		setContentView(R.layout.activity_new_parking_lots);
		initActionBar();
		if (getIntent().getSerializableExtra("roadsAreas") == null) {
			return;
		}
		currentBaseStation = (RoadsAreas) getIntent().getSerializableExtra(
				"roadsAreas");
		smallTitle = getIntent().getStringExtra("smallTitle");
		Log.i("demo", "smallTitle=" + smallTitle);
		setFragment();
		initView();
		viewEvent();
		// Ĭ�Ͽ�����Ҫ������ʽ��վ�µĳ�λ����
		getParkingLots(currentBaseStation);
		// ��ȡ��Ч�Ĳ����վ��Ϣ
		// ȡ���ݴ��ڱ��صĲ����վ�µĳ�λ
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
				.setText("" + currentBaseStation.getCode());
		textView_basestation_parkinglot_count_edit = (TextView) findViewById(R.id.textView_basestation_parkinglot_count_edit);
		textView_support_basestation_code_edit = (TextView) findViewById(R.id.textView_support_basestation_code_edit);
		textView_basestation_support_parkinglot_count_edit = (TextView) findViewById(R.id.textView_basestation_support_parkinglot_count_edit);
		basestation_status = (TextView) findViewById(R.id.basestation_status);
		relativeLayout_for_basestation = (RelativeLayout) findViewById(R.id.relativeLayout_for_basestation);
		relativeLayout_for_support_basestation = (RelativeLayout) findViewById(R.id.relativeLayout_for_support_basestation);
		actionbar_textView_small_title = (TextView) findViewById(R.id.actionbar_textView_small_title);
		Log.i("demo", "title=" + currentBaseStation.getTitle());
		String a = smallTitle + "," + currentBaseStation.getTitle();
		actionbar_textView_small_title.setText(a);
	}

	private void viewEvent() {
		relativeLayout_for_basestation
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// �鿴��վ
						Intent intent = new Intent(
								NewRoadPickParkingLotActivity.this,
								NewBaseStationInfoActivity.class);
						intent.putExtra("baseStation", currentBaseStation);
						startActivity(intent);
					}
				});
		relativeLayout_for_support_basestation
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Toast.makeText(NewRoadPickParkingLotActivity.this,
								"ѡȡ�����վ", Toast.LENGTH_SHORT).show();
						openBaseStationPickFragment(1);
					}
				});
	}

	private void initActionBar() {
		actionbar_progressBar = (ProgressBar) findViewById(R.id.actionbar_progressBar);
		actionbar_progressBar.setVisibility(View.GONE);
		((TextView) findViewById(R.id.actionbar_textView_title)).setText("��λ");
		((ImageView) findViewById(R.id.actionbar_imageView_back_button))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						onBackPressed();
					}
				});
		actionbar_refresh_button = (TextView) findViewById(R.id.actionbar_refresh_button);
		// actionbar_refresh_button.setText("����");
		actionbar_refresh_button.setText("");
		actionbar_refresh_button.setVisibility(View.GONE);
		actionbar_refresh_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (currentSupportBaseStation != null) {
					Toast.makeText(NewRoadPickParkingLotActivity.this, "���ӳ�λ",
							Toast.LENGTH_SHORT).show();
					// ��ת���ӳ�λ
					// openAddParkingLots();
				} else {
					Toast.makeText(NewRoadPickParkingLotActivity.this,
							"ѡȡ�����վ", Toast.LENGTH_SHORT).show();
					// openBaseStationPickFragment(1);
				}
			}
		});
		stopProgress();
	}

	private void showParkingLots() {
		// ��parkinglots��������
		Collections.sort(parkinglots, new ComparatorParkingLot());
		// �жϱ�����������
		if (parkingLotsFragment != null) {
			Log.i("demo", "parkinglots=" + parkinglots.size());
			parkingLotsFragment.freshParkingLotsData(parkinglots);
		}
	}

	private void upCount(int count) {
		textView_basestation_parkinglot_count_edit.setText("" + count);
	}

	/**
	 * ��䲿���վ����Ϣ
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
	 *            0��ʽ��վ��1 �����վ
	 */
	// private void getParkingLots(BaseStation basestation, int flag) {
	// if (currentBaseStation != null) {
	// AppContext.getInstance().getVolleyTools()
	// .httpFetchBasestationLots(handler, basestation, flag);
	// startProgress();
	// }
	// }
	private void getParkingLots(RoadsAreas ra) {
		if (currentBaseStation != null) {
			AppContext.getInstance().getVolleyTools()
					.httpFetchRoadAreasLots(handler, ra);
			startProgress();
		}
	}

	/**
	 * �����ӳ�λ����
	 */
	private void openAddParkingLots() {
		Intent intent = new Intent(NewRoadPickParkingLotActivity.this,
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
						"�����վ");
				// OnBasestationPicked(baseStation, 1);
				break;
			case INTENT_REQUEST_CODE_FOR_ADD_PARKING_LOT:
				// ����ˢ��
				getParkingLots(currentBaseStation);
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
		Toast.makeText(NewRoadPickParkingLotActivity.this,
				"�鿴��λ��" + parkingLot.getCode(), Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(NewRoadPickParkingLotActivity.this,
				NewParkingLotInfoActivity.class);
		intent.putExtra("parkingLot", parkingLot);
		startActivity(intent);
		// } else {

		// prepareToDeletePl = parkingLot;
		// // ������ʾ���Ƿ�Ҫɾ���ó�λ
		// YesOrNoDialogFragment yesOrNoFrag = new YesOrNoDialogFragment();
		// Bundle b = new Bundle();
		// b.putStringArray("stringArray", new String[] {
		// "�����λ���ڼ����У�ȷ��Ҫɾ�������²��������λ��", "ȡ��", "ɾ��" });
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
		// ������ʾ���Ƿ�Ҫɾ���ó�λ
		YesOrNoDialogFragment yesOrNoFrag = new YesOrNoDialogFragment();
		Bundle b = new Bundle();
		b.putStringArray("stringArray", new String[] {
				"�����λ���ڼ����У�ȷ��Ҫɾ�������²��������λ��", "ȡ��", "ɾ��" });
		yesOrNoFrag.setArguments(b);
		yesOrNoFrag.setStyle(DialogFragment.STYLE_NO_TITLE,
				android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth);
		// android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth);
		yesOrNoFrag.show(getFragmentManager(), "YesOrNoDialogFragment");
	}

	@Override
	public void OnParkingLotsRefreashed() {
		// ˢ��
		getParkingLots(currentBaseStation);
	}

	@Override
	public void onSureClicked() {
		// ɾ��prepareToDeletePl
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

	/** ��ȡ�����վ������ */
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

	@Override
	public void onRoadAreasBeClicked(RoadsAreas roadsAreas) {
		// 0.�����վ���ڴ棬�����ݿ�
		// currentSupportBaseStation = new BaseStation(-1, roadsAreas.getCode(),
		// baseStation.getCode(), -1, -1, 0.0, 0.0,
		// baseStation.getDescription(), baseStation.getDescription(),
		// new Date());
		// if (daManager != null) {
		// daManager.saveOriLocSupportBasestation(baseStation);
		// }
		// 1.�ڽ�����Ӧ�û�վ��Ϣ
		// upSupportBaseStationUI(0);
		// �ύӳ���ϵ
		AppContext.getInstance().getVolleyTools()
				.httpFetchRoadAreasLots(handler, roadsAreas);
	}

	@Override
	public void addRoadAreas() {
		// �򿪶�ά��ɨ������
		Intent i = new Intent(NewRoadPickParkingLotActivity.this,
				ZxingCaptureActivity.class);
		i.putExtra("useDescribe", "��ɨ���վ��ά��");
		startActivityForResult(i,
				HandlerMessageCodes.INTENT_ACTION_CODE_ZXING_REQUEST_CODE);
	}

	@Override
	public void OnRoadAreasRefreashed() {
		// ˢ��
		getParkingLots(currentBaseStation);
	}

}