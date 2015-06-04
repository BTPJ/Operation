package com.cyjt.operation.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cyjt.operation.R;
import com.cyjt.operation.base.AppContext;
import com.cyjt.operation.base.Constants;
import com.cyjt.operation.base.HandlerMessageCodes;
import com.cyjt.operation.bean.BaseStation;
import com.cyjt.operation.bean.LanLot;
import com.cyjt.operation.fragment.BaseStationCheckFragment;
import com.cyjt.operation.fragment.BaseStationCheckFragment.BaseStationCheckFragListener;
import com.cyjt.operation.fragment.BaseStationEditFragment;
import com.cyjt.operation.fragment.BaseStationEditFragment.BaseStationEidtFragListener;
import com.cyjt.operation.fragment.DialogFragmentForBaseStationHeartBeats;
import com.cyjt.operation.fragment.DialogFragmentForBaseStationUpDatas;
import com.cyjt.operation.uidynamic.DynamicMapActivity;

public class NewBaseStationInfoActivity extends Activity implements
		BaseStationCheckFragListener, BaseStationEidtFragListener {
	/** ��ǰҳ���־��0��ʾ�鿴ҳ�桢1��־�༭ҳ�� ��2��־���ҳ�� */
	private int currentActionFlag = 0;
	private FragmentManager fragmentmanager;
	private BaseStationEditFragment bsEditFragment;
	private BaseStationCheckFragment bsCheckFragment;
	private TextView actionbar_textView_title;
	private TextView actionbar_refresh_button;
	private ProgressBar actionbar_progressBar;
	private BaseStation currentBaseStation;
	private boolean isDynamic = false;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerMessageCodes.SUBMIT_BASESTATION_INFO_SUCCEED:
				Toast.makeText(NewBaseStationInfoActivity.this, "�����ɹ�",
						Toast.LENGTH_SHORT).show();
				onBackPressed();
				break;
			case HandlerMessageCodes.HTTP_BUILDER_DELETE_BASESTATION_INFO_SUCCEED:
				Toast.makeText(NewBaseStationInfoActivity.this, "ɾ���ɹ�",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(NewBaseStationInfoActivity.this,
						NewPickBaseStationActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				break;
//			case HandlerMessageCodes.DYNAMIC_DELETE_BASESTATION_SUCCEED:
//				Toast.makeText(NewBaseStationInfoActivity.this, "ɾ���ɹ�",
//						Toast.LENGTH_SHORT).show();
//				Intent intent2 = new Intent(NewBaseStationInfoActivity.this,
//						DynamicMapAndListActivity.class);
//				intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				intent2.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//				startActivity(intent2);
//				break;

			default:
				break;
			}
			stopProgress();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fragmentmanager = getFragmentManager();
		setContentView(R.layout.activity_base_station_info);
		initActionBar();
		if (getIntent().getBooleanExtra("isDynamic", false)) {
			isDynamic = true;
		} else {
			isDynamic = false;
		}
		if (getIntent().getSerializableExtra("baseStation") == null) {
			if (getIntent().getBooleanExtra("actionAddBaseStation", false)) {
				currentBaseStation = new BaseStation();
				currentBaseStation.setAreaId(getIntent().getLongExtra("areaId",
						-1));
				currentActionFlag = 2;
			} else {
				Toast.makeText(NewBaseStationInfoActivity.this,
						"�����ˣ�û�л�վ����Ҳû������ָ��", Toast.LENGTH_LONG).show();
				return;
			}
		} else {
			currentBaseStation = (BaseStation) getIntent()
					.getSerializableExtra("baseStation");
		}
		initView();
		viewEvent();
		changeFragment(currentActionFlag);
	}

	private void initActionBar() {
		actionbar_progressBar = (ProgressBar) findViewById(R.id.actionbar_progressBar);
		actionbar_textView_title = (TextView) findViewById(R.id.actionbar_textView_title);
		actionbar_refresh_button = (TextView) findViewById(R.id.actionbar_refresh_button);
		/** Ĭ��Ϊ�鿴 */
		changeActionbar(currentActionFlag);
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
							changeFragment(currentActionFlag);
						} else if (currentActionFlag == 2) {
							// ��ӽ�����ֱ�ӷ��ؼ���
							onBackPressed();
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
				} else if (currentActionFlag == 1) {
					// �����������վ����

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
			actionbar_textView_title.setText("�鿴��վ");
			actionbar_refresh_button.setVisibility(View.VISIBLE);
			actionbar_refresh_button.setText("�༭");
			break;
		case 1:
			actionbar_textView_title.setText("�༭��վ");
			actionbar_refresh_button.setVisibility(View.GONE);
			actionbar_refresh_button.setText("�ύ");
			break;
		case 2:
			actionbar_textView_title.setText("��ӻ�վ");
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
		} else if (currentActionFlag == 2) {
			TurnFragmentToEdit(transaction);
		}
		transaction.commit();
		changeActionbar(currentActionFlag);
	}

	private void hideDrawerFragments(FragmentTransaction transaction) {
		if (bsCheckFragment != null) {
			transaction.hide(bsCheckFragment);
		}
		if (bsEditFragment != null) {
			transaction.hide(bsEditFragment);
		}
	}

	private void TurnFragmentToEdit(FragmentTransaction transaction) {
		if (bsEditFragment == null) {
			bsEditFragment = new BaseStationEditFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable("baseStation", currentBaseStation);
			bsEditFragment.setArguments(bundle);
			transaction.add(R.id.frameLayout_for_basestation, bsEditFragment);
		}
		transaction.show(bsEditFragment);
	}

	private void TurnFragmentToCheck(FragmentTransaction transaction) {
		if (bsCheckFragment == null) {
			bsCheckFragment = new BaseStationCheckFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable("baseStation", currentBaseStation);
			bsCheckFragment.setArguments(bundle);
			transaction.add(R.id.frameLayout_for_basestation, bsCheckFragment);
		}
		transaction.show(bsCheckFragment);
	}

	private void refreashBaseStationCode(String code) {
		currentBaseStation.setCode(code);
		if (bsEditFragment != null) {
			bsEditFragment.refreshBaseStation(currentBaseStation);
		}
	}

	@Override
	public void onMapCheckBeClicked() {
		openMap(currentActionFlag);
	}

	@Override
	public void CheckBaseStationHeartBeClicked() {
		// �����վ������Ϣ
		DialogFragmentForBaseStationHeartBeats fragment = new DialogFragmentForBaseStationHeartBeats();
		Bundle args = new Bundle();
		args.putString("baseStationCode", currentBaseStation.getNfcCode());
		fragment.setArguments(args);
		fragment.setStyle(DialogFragment.STYLE_NO_TITLE,
				android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth);
		fragment.show(fragmentmanager,
				"BaseStationHeartBeatsListDialogFragment");
	}

	@Override
	public void CheckBaseStationUpDataBeClicked() {
		// �����վ������Ϣ
		DialogFragmentForBaseStationUpDatas fragment = new DialogFragmentForBaseStationUpDatas();
		Bundle args = new Bundle();
		args.putString("baseStationCode", currentBaseStation.getNfcCode());
		fragment.setArguments(args);
		fragment.setStyle(DialogFragment.STYLE_NO_TITLE,
				android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth);
		fragment.show(fragmentmanager, "DialogFragmentForBaseStationUpDatas");
	}

	@Override
	public void onMapEditBeClicked(BaseStation baseStation) {
		openMap(currentActionFlag);
	}

	@Override
	public void onSubmitBeClicked(BaseStation baseStation) {
		startProgress();
		if (isDynamic) {
			// �ύ��վ��Ϣ
			baseStation.setAreaId(-1);
			baseStation.setType(Constants.BASESTATION_TYPE_DYNAMIC_CODE);
			AppContext.getInstance().getVolleyTools()
					.submitBasestationInfo(handler, baseStation);
		} else {
			// �ύ��վ��Ϣ
			baseStation.setType(Constants.BASESTATION_TYPE_STATIC_CODE);
			AppContext.getInstance().getVolleyTools()
					.submitBasestationInfo(handler, baseStation);
		}
	}

	@Override
	public void onDeleteBeClicked(BaseStation baseStation) {
		startProgress();
		if (isDynamic) {
			// ����ɾ����վ
			AppContext.getInstance().getVolleyTools()
					.dynamicDeleteBasestation(handler, baseStation);
		} else {
			// ����ɾ����վ
			AppContext.getInstance().getVolleyTools()
					.deleteBasestation(handler, baseStation);
		}
	}

	@Override
	public void onZxingButtonBeClicked() {
		Intent i = new Intent(NewBaseStationInfoActivity.this,
				ZxingCaptureActivity.class);
		i.putExtra("useDescribe", "��ɨ���վ��ά��");
		startActivityForResult(i,
				HandlerMessageCodes.INTENT_ACTION_CODE_ZXING_REQUEST_CODE);
	}

	/** ���õ�ͼ */
	private void openMap(int currentActionFlag) {
		Intent i = new Intent(NewBaseStationInfoActivity.this,
				DynamicMapActivity.class);

		i.putExtra("lanLot", new LanLot(currentBaseStation.getLat(),
				currentBaseStation.getLon(), "", currentBaseStation.getCode(),
				currentBaseStation.getDescription()));
		i.putExtra("currentActionFlag", currentActionFlag);
		startActivityForResult(i,
				HandlerMessageCodes.INTENT_REQUEST_CODE_FOR_CHECK_ON_MAP);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case HandlerMessageCodes.INTENT_REQUEST_CODE_FOR_CHECK_ON_MAP:
				if (data.getSerializableExtra("lanLot") != null) {
					LanLot lanLot = (LanLot) data
							.getSerializableExtra("lanLot");
					currentBaseStation.setLat(lanLot.getLat());
					currentBaseStation.setLon(lanLot.getLon());
					if (bsEditFragment != null) {
						bsEditFragment.refreshBaseStation(currentBaseStation);
					}
				}
				break;
			case HandlerMessageCodes.INTENT_ACTION_CODE_ZXING_REQUEST_CODE:
				String code = (String) data.getBundleExtra("Zxing").get(
						"result");
				// ���parkingCode�в�ȫ����������ʾ�û�����ɨ����ȷ�ĳ�λ����
				// if (code.matches("^[0-9]*$")) {
				// "^[0-9]*$"��ʾparkingCodeֻ��������
				// "^\d{m,n}$"��ʾparkingCodeֻ���Ǵ�m��nλ������
				refreashBaseStationCode(code);
				// } else {
				// Toast.makeText(DynamicLaneArrayInfoActivity.this,
				// "��ɨ�����Э��Ķ�ά��", Toast.LENGTH_SHORT).show();
				// }
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
