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
	/** 当前页面标志，0表示查看页面、1标志编辑页面 、2标志添加页面 */
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
				Toast.makeText(NewBaseStationInfoActivity.this, "操作成功",
						Toast.LENGTH_SHORT).show();
				onBackPressed();
				break;
			case HandlerMessageCodes.HTTP_BUILDER_DELETE_BASESTATION_INFO_SUCCEED:
				Toast.makeText(NewBaseStationInfoActivity.this, "删除成功",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(NewBaseStationInfoActivity.this,
						NewPickBaseStationActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				break;
//			case HandlerMessageCodes.DYNAMIC_DELETE_BASESTATION_SUCCEED:
//				Toast.makeText(NewBaseStationInfoActivity.this, "删除成功",
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
						"出错了，没有基站对象也没有其他指令", Toast.LENGTH_LONG).show();
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
		/** 默认为查看 */
		changeActionbar(currentActionFlag);
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
							changeFragment(currentActionFlag);
						} else if (currentActionFlag == 2) {
							// 添加界面下直接返回即可
							onBackPressed();
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
				} else if (currentActionFlag == 1) {
					// 请求网络提基站数据

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
			actionbar_textView_title.setText("查看基站");
			actionbar_refresh_button.setVisibility(View.VISIBLE);
			actionbar_refresh_button.setText("编辑");
			break;
		case 1:
			actionbar_textView_title.setText("编辑基站");
			actionbar_refresh_button.setVisibility(View.GONE);
			actionbar_refresh_button.setText("提交");
			break;
		case 2:
			actionbar_textView_title.setText("添加基站");
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
		// 请求基站心跳信息
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
		// 请求基站心跳信息
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
			// 提交基站信息
			baseStation.setAreaId(-1);
			baseStation.setType(Constants.BASESTATION_TYPE_DYNAMIC_CODE);
			AppContext.getInstance().getVolleyTools()
					.submitBasestationInfo(handler, baseStation);
		} else {
			// 提交基站信息
			baseStation.setType(Constants.BASESTATION_TYPE_STATIC_CODE);
			AppContext.getInstance().getVolleyTools()
					.submitBasestationInfo(handler, baseStation);
		}
	}

	@Override
	public void onDeleteBeClicked(BaseStation baseStation) {
		startProgress();
		if (isDynamic) {
			// 请求删除基站
			AppContext.getInstance().getVolleyTools()
					.dynamicDeleteBasestation(handler, baseStation);
		} else {
			// 请求删除基站
			AppContext.getInstance().getVolleyTools()
					.deleteBasestation(handler, baseStation);
		}
	}

	@Override
	public void onZxingButtonBeClicked() {
		Intent i = new Intent(NewBaseStationInfoActivity.this,
				ZxingCaptureActivity.class);
		i.putExtra("useDescribe", "请扫描基站二维码");
		startActivityForResult(i,
				HandlerMessageCodes.INTENT_ACTION_CODE_ZXING_REQUEST_CODE);
	}

	/** 调用地图 */
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
				// 如果parkingCode中不全是数字则，提示用户重新扫描正确的车位铭牌
				// if (code.matches("^[0-9]*$")) {
				// "^[0-9]*$"表示parkingCode只能是数字
				// "^\d{m,n}$"表示parkingCode只能是从m到n位的数字
				refreashBaseStationCode(code);
				// } else {
				// Toast.makeText(DynamicLaneArrayInfoActivity.this,
				// "请扫描符合协议的二维码", Toast.LENGTH_SHORT).show();
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
