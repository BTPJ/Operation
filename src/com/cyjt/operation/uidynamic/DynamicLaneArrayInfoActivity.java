package com.cyjt.operation.uidynamic;

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
import com.cyjt.operation.bean.LanLot;
import com.cyjt.operation.bean.LaneArray;
import com.cyjt.operation.fragment.DialogFragmentForLaneArrayStatus;
import com.cyjt.operation.fragment.DynamicLaneArrayCheckFragment;
import com.cyjt.operation.fragment.DynamicLaneArrayCheckFragment.LaneArrayCheckFragListener;
import com.cyjt.operation.fragment.DynamicLaneArrayEditFragment;
import com.cyjt.operation.fragment.DynamicLaneArrayEditFragment.LaneArrayEidtFragListener;
import com.cyjt.operation.ui.ZxingCaptureActivity;

/**
 * ������Ϣ����
 * 
 * @author kullo<BR>
 *         2014-10-25 ����9:06:37<BR>
 */
public class DynamicLaneArrayInfoActivity extends Activity implements
		LaneArrayCheckFragListener, LaneArrayEidtFragListener {
	private DynamicLaneArrayCheckFragment dlCheckFrag;
	private DynamicLaneArrayEditFragment dlEditFrag;
	private LaneArray currentLaneArray;
	private ProgressBar actionbar_progressBar;
	private TextView actionbar_textView_title;
	private ImageView actionbar_imageView_back_button;
	private TextView actionbar_refresh_button;
	/** ��ǰҳ���־��0��ʾ�鿴ҳ�桢1��־�༭ҳ�� ��2��־���ҳ�� */
	private int currentActionFlag = 0;
	private FragmentManager fragmentmanager;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerMessageCodes.DYNAMIC_SUBMIT_LANEARRAY_INFO_SUCCEED:
				// ��ӳɹ�
				Toast.makeText(DynamicLaneArrayInfoActivity.this, "�����ɹ�",
						Toast.LENGTH_SHORT).show();
				onBackPressed();
				break;
			case HandlerMessageCodes.DYNAMIC_SUBMIT_LANEARRAY_INFO_FAILED:

				break;
			case HandlerMessageCodes.DYNAMIC_UPDATE_LANEARRAY_INFO_SUCCEED:
				Toast.makeText(DynamicLaneArrayInfoActivity.this, "�����ɹ�",
						Toast.LENGTH_SHORT).show();
				TurnActivityToStart();
				break;
			case HandlerMessageCodes.DYNAMIC_UPDATE_LANEARRAY_INFO_FAILED:

				break;
			case HandlerMessageCodes.DYNAMIC_DELETE_LANEARRAY_SUCCEED:
				Toast.makeText(DynamicLaneArrayInfoActivity.this, "�����ɹ�",
						Toast.LENGTH_SHORT).show();
				// ɾ���ɹ�,Ӧ����ת�������б�������ֱ�ӷ�����һ��
				TurnActivityToStart();
				break;
			case HandlerMessageCodes.DYNAMIC_DELETE_LANEARRAY_FAILED:

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
		fragmentmanager = getFragmentManager();
		setContentView(R.layout.activity_dynamic_lane_array_info);
		initActionBar();
		if (getIntent().getSerializableExtra("laneArray") == null) {
			if (getIntent().getBooleanExtra("actionAddLaneArray", false)) {
				currentLaneArray = new LaneArray();
				currentActionFlag = 2;
			} else {
				Toast.makeText(DynamicLaneArrayInfoActivity.this,
						"�����ˣ�û�����ж���Ҳû������ָ��", Toast.LENGTH_LONG).show();
				return;
			}
		} else {
			currentLaneArray = (LaneArray) getIntent().getSerializableExtra(
					"laneArray");
		}
		initView();
		viewEvent();
		changeFragment(currentActionFlag);
	}

	private void initActionBar() {
		actionbar_progressBar = (ProgressBar) findViewById(R.id.actionbar_progressBar);
		actionbar_textView_title = (TextView) findViewById(R.id.actionbar_textView_title);
		actionbar_imageView_back_button = (ImageView) findViewById(R.id.actionbar_imageView_back_button);
		actionbar_refresh_button = (TextView) findViewById(R.id.actionbar_refresh_button);
		/** Ĭ��Ϊ�鿴 */
		changeActionbar(currentActionFlag);
		actionbar_imageView_back_button
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
					// ���������ύ��������

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
			actionbar_textView_title.setText("�鿴����");
			actionbar_refresh_button.setVisibility(View.VISIBLE);
			actionbar_refresh_button.setText("�༭");
			break;
		case 1:
			actionbar_textView_title.setText("�༭����");
			actionbar_refresh_button.setVisibility(View.GONE);
			actionbar_refresh_button.setText("�ύ");
			break;
		case 2:
			actionbar_textView_title.setText("�������");
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
		if (dlCheckFrag != null) {
			transaction.hide(dlCheckFrag);
		}
		if (dlEditFrag != null) {
			transaction.hide(dlEditFrag);
		}
	}

	private void TurnFragmentToEdit(FragmentTransaction transaction) {
		if (dlEditFrag == null) {
			dlEditFrag = new DynamicLaneArrayEditFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable("laneArray", currentLaneArray);
			dlEditFrag.setArguments(bundle);
			transaction.add(R.id.frameLayout_for_lane_array, dlEditFrag);
		}
		transaction.show(dlEditFrag);
	}

	private void TurnFragmentToCheck(FragmentTransaction transaction) {
		if (dlCheckFrag == null) {
			dlCheckFrag = new DynamicLaneArrayCheckFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable("laneArray", currentLaneArray);
			dlCheckFrag.setArguments(bundle);
			transaction.add(R.id.frameLayout_for_lane_array, dlCheckFrag);
		}
		transaction.show(dlCheckFrag);
	}

	private void refreashLaneArrayCode(String code) {
		currentLaneArray.setCode(code);
		if (dlEditFrag != null) {
			dlEditFrag.refreshLaneArray(currentLaneArray);
		}
	}

	@Override
	public void onMapCheckBeClicked() {
		openMap();
	}

	@Override
	public void onMapEditBeClicked(LaneArray laneArray) {
		currentLaneArray = laneArray;
		openMap();
	}

	@Override
	public void onZxingButtonBeClicked() {
		// ��ת��ȡZxing�ķ���ֵ
		Intent i = new Intent(DynamicLaneArrayInfoActivity.this,
				ZxingCaptureActivity.class);
		i.putExtra("useDescribe", "��ɨ�����ж�ά��");
		startActivityForResult(i,
				HandlerMessageCodes.INTENT_ACTION_CODE_ZXING_REQUEST_CODE);
	}

	@Override
	public void onSubmitBeClicked(LaneArray laneArray) {
		currentLaneArray = laneArray;
		if (("").equals(laneArray.getCode())) {
			Toast.makeText(DynamicLaneArrayInfoActivity.this, "���б�Ų���Ϊ��",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (("").equals(laneArray.getDescription())) {
			Toast.makeText(DynamicLaneArrayInfoActivity.this, "������������Ϊ��",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (laneArray.getLat() == -0.0 || laneArray.getLon() == -0.0) {
			Toast.makeText(DynamicLaneArrayInfoActivity.this, "��������δ��д",
					Toast.LENGTH_SHORT).show();
			return;
		}

		switch (currentActionFlag) {
		case 1:
			startProgress();
			// �༭�ύ
			AppContext.getInstance().getVolleyTools()
					.dynamicUpdateLanearrayInfo(handler, laneArray);
			break;
		case 2:
			startProgress();
			// �½��ύ
			AppContext.getInstance().getVolleyTools()
					.dynamicSubmitLanearrayInfo(handler, laneArray);
			break;
		default:
			break;
		}
	}

	@Override
	public void onDeleteBeClicked(LaneArray laneArray) {
		startProgress();
		// ɾ������
		AppContext.getInstance().getVolleyTools()
				.dynamicDeleteLanearray(handler, laneArray);
	}

	/** ���õ�ͼ */
	private void openMap() {
		Intent i = new Intent(DynamicLaneArrayInfoActivity.this,
				DynamicMapActivity.class);

		i.putExtra("lanLot", new LanLot(currentLaneArray.getLat(),
				currentLaneArray.getLon(), "", currentLaneArray.getCode(),
				currentLaneArray.getDescription()));
		i.putExtra("currentActionFlag", currentActionFlag);
		startActivityForResult(i,
				HandlerMessageCodes.INTENT_REQUEST_CODE_FOR_CHECK_ON_MAP);
	}

	/** ��ת����ͼ���� */
	private void TurnActivityToStart() {
		Intent intent1 = new Intent(DynamicLaneArrayInfoActivity.this,
				DynamicMapAndListActivity.class);
		intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent1);
	}

	/** �򿪳�λ״̬��Dialog */
	@Override
	public void checkLaneArrayStatus() {
		DialogFragmentForLaneArrayStatus fragment = new DialogFragmentForLaneArrayStatus();
		Bundle args = new Bundle();
		args.putInt("type", Constants.BEAN_TYPE_LANEARRAY);
		args.putString("laneArrayCode", currentLaneArray.getCode());
		fragment.setArguments(args);
		fragment.setStyle(DialogFragment.STYLE_NO_TITLE,
				android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth);
		fragment.show(fragmentmanager, "DialogFragmentForLaneArrayStatus");
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
					currentLaneArray.setLat(lanLot.getLat());
					currentLaneArray.setLon(lanLot.getLon());

					if (dlEditFrag != null) {
						dlEditFrag.refreshLaneArray(currentLaneArray);
					}
				}
				break;
			case HandlerMessageCodes.INTENT_ACTION_CODE_ZXING_REQUEST_CODE:
				String code = (String) data.getBundleExtra("Zxing").get(
						"result");
				// ���parkingCode�в�ȫ����������ʾ�û�����ɨ����ȷ�ĳ�λ����
				// if (code.matches("^[A-Za-z0-9]*$")) {
				// "^[0-9]*$"��ʾparkingCodeֻ��������
				// "^\d{m,n}$"��ʾparkingCodeֻ���Ǵ�m��nλ������
				refreashLaneArrayCode(code);
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
