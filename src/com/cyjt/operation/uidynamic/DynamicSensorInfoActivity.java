package com.cyjt.operation.uidynamic;

import java.util.ArrayList;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cyjt.operation.R;
import com.cyjt.operation.base.AppConfig;
import com.cyjt.operation.base.AppContext;
import com.cyjt.operation.base.BaseActivityWithNFC;
import com.cyjt.operation.base.HandlerMessageCodes;
import com.cyjt.operation.bean.BaseStation;
import com.cyjt.operation.bean.LaneArray;
import com.cyjt.operation.bean.LocBaseStation;
import com.cyjt.operation.bean.Sensor;
import com.cyjt.operation.bean.ZValue;
import com.cyjt.operation.fragment.DialogFragmentForDynamicNodeHeartBeats;
import com.cyjt.operation.fragment.DynamicPickBasestationDialogFragment;
import com.cyjt.operation.fragment.DynamicPickBasestationDialogFragment.OnBasestationPickedListener;
import com.cyjt.operation.fragment.DynamicSensorAddFragment;
import com.cyjt.operation.fragment.DynamicSensorAddFragment.SensorAddFragListener;
import com.cyjt.operation.fragment.DynamicSensorCheckFragment;
import com.cyjt.operation.fragment.DynamicSensorCheckFragment.SensorCheckFragListener;
import com.cyjt.operation.fragment.DynamicSensorEditFragment;
import com.cyjt.operation.fragment.DynamicSensorEditFragment.SensorEditFragListener;
import com.cyjt.operation.sqlit.DBManager;
import com.cyjt.operation.ui.NewBaseStationInfoActivity;
import com.cyjt.operation.ui.ZxingCaptureActivity;

public class DynamicSensorInfoActivity extends BaseActivityWithNFC implements
		SensorCheckFragListener, SensorEditFragListener, SensorAddFragListener,
		OnBasestationPickedListener {
	private ProgressBar actionbar_progressBar;
	private DBManager daManager;
	private FragmentManager fragmentmanager;
	private DynamicPickBasestationDialogFragment fragment;
	private DynamicSensorCheckFragment dsCheckFrag;
	private DynamicSensorEditFragment dsEditFrag;
	private boolean SensorAddResultFlag = false;
	private DynamicSensorAddFragment dsAddFrag;
	private TextView actionbar_textView_title;
	private ImageView actionbar_imageView_back_button;
	private TextView actionbar_refresh_button;
	/** 当前页面标志，0表示查看页面、1标志编辑页面 、2标志添加页面 */
	private int currentActionFlag = 0;
	private Sensor currentSensor;
	private LaneArray belongLaneArray;
	private BaseStation belongBaseStation;
	private BaseStation supportBaseStation;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerMessageCodes.DYNAMIC_FETCH_SENSOR_ZVALUES_SUCCEED:
				@SuppressWarnings("unchecked")
				ArrayList<ZValue> zValues = (ArrayList<ZValue>) msg.obj;
				if (dsCheckFrag != null) {
					if (zValues != null && zValues.size() > 0) {
						dsCheckFrag.refreshZvalueList(zValues);
					} else {
						Toast.makeText(DynamicSensorInfoActivity.this, "暂无地磁值",
								Toast.LENGTH_SHORT).show();
					}
				}
				break;
			case HandlerMessageCodes.DYNAMIC_FETCH_SENSOR_ZVALUES_FAILED:
				Toast.makeText(DynamicSensorInfoActivity.this, "获取地磁值失败",
						Toast.LENGTH_SHORT).show();
				break;
			case HandlerMessageCodes.DYNAMIC_DELETE_SENSOR_SUCCEED:
				Toast.makeText(DynamicSensorInfoActivity.this, "操作成功",
						Toast.LENGTH_SHORT).show();
				onBackPressed();

				break;
			case HandlerMessageCodes.DYNAMIC_DELETE_SENSOR_FAILED:
				Toast.makeText(DynamicSensorInfoActivity.this, "删除失败，请稍后再试",
						Toast.LENGTH_SHORT).show();

				break;
			case HandlerMessageCodes.DYNAMIC_FETCH_SENSOR_BOUND_BASESTATION_SUCCEED:
				belongBaseStation = (BaseStation) msg.obj;
				if (dsCheckFrag != null && belongBaseStation != null
						&& !belongBaseStation.getCode().equals("")) {
					dsCheckFrag.refreshBelongBaseStation(belongBaseStation);
				}
				break;
			case HandlerMessageCodes.DYNAMIC_FETCH_SENSOR_BOUND_BASESTATION_FAILED:
				Toast.makeText(DynamicSensorInfoActivity.this, "未能成功获取所属基站",
						Toast.LENGTH_SHORT).show();

				break;
			case HandlerMessageCodes.DYNAMIC_FETCH_SENSOR_BOUND_LANEARRAY_SUCCEED:
				belongLaneArray = (LaneArray) msg.obj;
				if (dsCheckFrag != null && belongLaneArray != null
						&& !belongLaneArray.getCode().equals("")) {
					dsCheckFrag.refreshBelongLaneArray(belongLaneArray);
				}
				break;
			case HandlerMessageCodes.DYNAMIC_FETCH_SENSOR_BOUND_LANEARRAY_FAILED:
				Toast.makeText(DynamicSensorInfoActivity.this, "未能成功获取所属阵列",
						Toast.LENGTH_SHORT).show();

				break;
			case HandlerMessageCodes.DYNAMIC_SUBMIT_SENSORS_SUCCEED:
				Toast.makeText(DynamicSensorInfoActivity.this, "操作成功",
						Toast.LENGTH_SHORT).show();
				// 添加成功
				onBackPressed();
				break;
			case HandlerMessageCodes.DYNAMIC_SUBMIT_SENSORS_FAILED:

				break;
			case HandlerMessageCodes.DYNAMIC_SUBMIT_BASESTATIONS_MAP_SUCCEED:
				Log.v("demo", "基站关系提交成功");
				break;
			case HandlerMessageCodes.DYNAMIC_SUBMIT_BASESTATIONS_MAP_FAILED:

				break;
			case HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATION_INFO_SUCCEED:
				if ((BaseStation) msg.obj == null) {
					Toast.makeText(DynamicSensorInfoActivity.this, "该基站不存在",
							Toast.LENGTH_SHORT).show();
					return;
				}
				// 获取正式基站成功
				BaseStation belongBaseStation = (BaseStation) msg.obj;
				LocBaseStation baseStation = new LocBaseStation(
						belongBaseStation.getId(), belongBaseStation.getCode(),
						belongBaseStation.getDescription());
				OnBasestationPicked(baseStation, 0);
				break;
			case HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATION_INFO_FAILED:

				break;
			case HandlerMessageCodes.SUBMIT_SENSOR_RESET_SUCCEED:
				Toast.makeText(DynamicSensorInfoActivity.this, "重置成功",
						Toast.LENGTH_SHORT).show();
				break;
			case HandlerMessageCodes.SUBMIT_SENSOR_RESET_FAILED:
				Toast.makeText(DynamicSensorInfoActivity.this, "重置失败",
						Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
			stopProgress();
			if (SensorAddResultFlag && dsAddFrag != null) {
				SensorAddResultFlag = false;
				dsAddFrag.submitSensorsError();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fragmentmanager = getFragmentManager();
		daManager = new DBManager(DynamicSensorInfoActivity.this);
		setContentView(R.layout.activity_dynamic_sensor_info);
		initActionBar();
		if (getIntent().getSerializableExtra("sensor") == null) {
			if (getIntent().getBooleanExtra("actionAddSensor", false)) {
				currentSensor = new Sensor();
				currentActionFlag = 2;
			} else {
				Toast.makeText(DynamicSensorInfoActivity.this,
						"出错了，没有节点对象也没有其他指令", Toast.LENGTH_LONG).show();
				return;
			}
		} else {
			currentSensor = (Sensor) getIntent().getSerializableExtra("sensor");
		}
		if (getIntent().getSerializableExtra("laneArray") == null) {
			actionbar_refresh_button.setVisibility(View.GONE);
		} else {
			belongLaneArray = (LaneArray) getIntent().getSerializableExtra(
					"laneArray");
		}
		initView();
		viewEvent();
		changeFragment(currentActionFlag);
		// 请求获取节点的信息
		// AppContext.getInstance().getVolleyTools().d
	}

	private void initView() {

	}

	private void viewEvent() {

	}

	private void initActionBar() {
		actionbar_progressBar = (ProgressBar) findViewById(R.id.actionbar_progressBar);
		actionbar_textView_title = (TextView) findViewById(R.id.actionbar_textView_title);
		actionbar_imageView_back_button = (ImageView) findViewById(R.id.actionbar_imageView_back_button);
		actionbar_refresh_button = (TextView) findViewById(R.id.actionbar_refresh_button);
		/** 默认为查看 */
		changeActionbar(currentActionFlag);
		actionbar_imageView_back_button
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
					// 请求网络提交阵列数据

				}
			}
		});
		stopProgress();
	}

	/** 改变actionBar之前要先将currentActionFlag进行改变才可以 */
	private void changeActionbar(int currentActionFlag) {
		switch (currentActionFlag) {
		case 0:
			actionbar_textView_title.setText("查看节点");
			actionbar_refresh_button.setVisibility(View.VISIBLE);
			actionbar_refresh_button.setText("编辑");
			break;
		case 1:
			actionbar_textView_title.setText("编辑节点");
			actionbar_refresh_button.setVisibility(View.GONE);
			actionbar_refresh_button.setText("提交");
			break;
		case 2:
			actionbar_textView_title.setText("添加节点");
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
		switch (currentActionFlag) {
		case 0:

			TurnFragmentToCheck(transaction);
			break;
		case 1:

			TurnFragmentToEdit(transaction);
			break;
		case 2:
			TurnFragmentToAdd(transaction);

			break;

		default:
			break;
		}
		transaction.commit();
		changeActionbar(currentActionFlag);
	}

	private void hideDrawerFragments(FragmentTransaction transaction) {
		if (dsCheckFrag != null) {
			transaction.hide(dsCheckFrag);
		}
		if (dsEditFrag != null) {
			transaction.hide(dsEditFrag);
		}
		if (dsAddFrag != null) {
			transaction.hide(dsAddFrag);
		}
	}

	private void TurnFragmentToEdit(FragmentTransaction transaction) {
		if (dsEditFrag == null) {
			dsEditFrag = new DynamicSensorEditFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable("laneArray", belongLaneArray);
			bundle.putSerializable("sensor", currentSensor);
			dsEditFrag.setArguments(bundle);
			transaction.add(R.id.frameLayout_for_sensor, dsEditFrag);
		}
		transaction.show(dsEditFrag);
	}

	private void TurnFragmentToCheck(FragmentTransaction transaction) {
		if (dsCheckFrag == null) {
			dsCheckFrag = new DynamicSensorCheckFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable("laneArray", belongLaneArray);
			bundle.putSerializable("sensor", currentSensor);
			dsCheckFrag.setArguments(bundle);
			transaction.add(R.id.frameLayout_for_sensor, dsCheckFrag);
		}
		transaction.show(dsCheckFrag);
	}

	private void TurnFragmentToAdd(FragmentTransaction transaction) {
		if (dsAddFrag == null) {
			dsAddFrag = new DynamicSensorAddFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable("laneArray", belongLaneArray);
			dsAddFrag.setArguments(bundle);
			transaction.add(R.id.frameLayout_for_sensor, dsAddFrag);
		}
		transaction.show(dsAddFrag);
	}

	@Override
	public void onSensorCheckFragmentCreated() {
		// 查询所属基站
		if (AppConfig.USING_NETWORK) {
			startProgress();
			// 请求所属基站
			AppContext.getInstance().getVolleyTools()
					.dynamicFetchSensorBoundBasestation(handler, currentSensor);
			// 请求所属阵列
			AppContext.getInstance().getVolleyTools()
					.dynamicFetchSensorBoundLaneArray(handler, currentSensor);

		} else {

		}
	}

	@Override
	public void onSensorCheckFragmentCreatedRefreashZvalues() {
		if (AppConfig.USING_NETWORK) {
			startProgress();
			AppContext.getInstance().getVolleyTools()
					.dynamicFetchSensorZvalues(handler, currentSensor);
		} else {
			startGetZV();
		}
	}

	private void startGetZV() {
		new Thread() {
			@Override
			public void run() {
				super.run();
				try {
					sleep(1000);
					getTestZValues();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void getTestZValues() {
		ArrayList<ZValue> zValues = new ArrayList<ZValue>();
		ZValue zv = null;
		for (int i = 0; i < 10; i++) {
			zv = new ZValue();
			zv.setEmValueZ(234 + i);
			zv.setSubmitAt(new Date());
			zValues.add(zv);
		}
		Message msg = new Message();
		msg.obj = zValues;
		msg.what = HandlerMessageCodes.DYNAMIC_FETCH_SENSOR_ZVALUES_SUCCEED;
		handler.sendMessage(msg);
	}

	@Override
	public void onSensorCheckFragmentBelongLaneArrayBeClicked() {
		Toast.makeText(DynamicSensorInfoActivity.this, "查看所属阵列",
				Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(DynamicSensorInfoActivity.this,
				DynamicLaneArrayInfoActivity.class);
		intent.putExtra("laneArray", belongLaneArray);
		startActivity(intent);
	}

	@Override
	public void onSensorCheckFragmentBelongBasestationBeClicked() {
		Toast.makeText(DynamicSensorInfoActivity.this, "查看所属基站",
				Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(DynamicSensorInfoActivity.this,
				NewBaseStationInfoActivity.class);
		intent.putExtra("baseStation", belongBaseStation);
		intent.putExtra("isDynamic", true);
		startActivity(intent);

	}

	@Override
	public void onSensorAddFragmentSubmitBeClicked(ArrayList<Sensor> sensors) {
		// TODO 提交数据
		Toast.makeText(DynamicSensorInfoActivity.this, "提交中",
				Toast.LENGTH_SHORT).show();
		if (sensors != null && sensors.size() > 0 && belongBaseStation != null
				&& supportBaseStation != null) {
			startProgress();
			SensorAddResultFlag = true;
			AppContext
					.getInstance()
					.getVolleyTools()
					.dynamicSubmitSensors(handler, belongBaseStation,
							supportBaseStation, belongLaneArray, sensors);
		} else {
			Toast.makeText(DynamicSensorInfoActivity.this, "有必要信息未填充",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onSensorAddFragmentChangeBasestationBeClicked() {
		// // TODO 更改或添加正式基站
		Toast.makeText(DynamicSensorInfoActivity.this, "请选择更改或添加正式基站",
				Toast.LENGTH_SHORT).show();
		openBaseStationPickFragment(0);
	}

	@Override
	public void onSensorAddFragmentChangeSupportBasestationBeClicked() {
		// // TODO 更改或添加部署基站
		Toast.makeText(DynamicSensorInfoActivity.this, "请选择更改或添加部署基站",
				Toast.LENGTH_SHORT).show();
		openBaseStationPickFragment(1);
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
		fragment.show(getFragmentManager(),
				"DynamicPickBasestationDialogFragment");
	}

	@Override
	public void onSensorAddFragmentCheckLaneArrayBeClicked() {
		// TODO 此处代码重复
		Toast.makeText(DynamicSensorInfoActivity.this, "查看所属阵列",
				Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(DynamicSensorInfoActivity.this,
				DynamicLaneArrayInfoActivity.class);
		intent.putExtra("laneArray", belongLaneArray);
		startActivity(intent);
	}

	@Override
	public void onSensorEditFragmentDeleteBeClicked(Sensor sensor) {
		startProgress();
		AppContext.getInstance().getVolleyTools()
				.dynamicDeleteSensor(handler, sensor);
	}
	@Override
	public void onSensorNeedReset() {
		// TODO Auto-generated method stub
		if(currentSensor == null)
			return;
		if(belongBaseStation == null)
			return;
		//请求网络重置该节点
		AppContext.getInstance().getVolleyTools().submitSensorReset(handler, currentSensor,belongBaseStation);
	}

	@Override
	public void OnBasestationPicked(LocBaseStation baseStation, int actionFlag) {
		switch (actionFlag) {
		case 0:
			this.belongBaseStation = new BaseStation(
					baseStation.getBaseStationId(), baseStation.getCode(),
					baseStation.getCode(), -1, 1, 0.0, 0.0,
					baseStation.getDescription(), baseStation.getDescription(),
					new Date());
			if (dsAddFrag != null) {
				dsAddFrag.refreshBaseStation(belongBaseStation);
			}
			if (daManager != null) {
				daManager.saveOriLocBasestation(baseStation);
			}
			break;
		case 1:
			this.supportBaseStation = new BaseStation(
					baseStation.getBaseStationId(), baseStation.getCode(),
					baseStation.getCode(), -1, 1, 0.0, 0.0,
					baseStation.getDescription(), baseStation.getDescription(),
					new Date());
			if (dsAddFrag != null) {
				dsAddFrag.refreshSupportBaseStation(supportBaseStation);
			}
			if (daManager != null) {
				daManager.saveOriLocSupportBasestation(baseStation);
			}
			break;

		default:
			break;
		}
		// 判断两个基站是否都已存在，存在则提交基站关系列表
		if (belongBaseStation != null && supportBaseStation != null) {
			// TODO 提交提交基站关系列表
//			AppContext
//					.getInstance()
//					.getVolleyTools()
//					.dynamicSubmitBasestationsMap(handler, belongBaseStation,
//							supportBaseStation);

		}
	}

	private int actionFlag = -1;

	@Override
	public void addBasestation(int actionFlag) {
		this.actionFlag = actionFlag;
		// 打开二维码扫描请求
		Intent i = new Intent(DynamicSensorInfoActivity.this,
				ZxingCaptureActivity.class);
		i.putExtra("useDescribe", "请扫描基站二维码");
		startActivityForResult(i,
				HandlerMessageCodes.INTENT_ACTION_CODE_ZXING_REQUEST_CODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case HandlerMessageCodes.INTENT_ACTION_CODE_ZXING_REQUEST_CODE:
				String code = (String) data.getBundleExtra("Zxing").get(
						"result");
				switch (this.actionFlag) {
				case 0:
					// 通过Code请求对应基站
					if (code != null) {
						if (AppConfig.USING_NETWORK) {
							startProgress();
							AppContext.getInstance().getVolleyTools()
									.fetchBasestationInfo(handler, code);
						} else {
							Message msg = new Message();
							msg.obj = new BaseStation(231, code, code, 23, 1,
									0.0, 0.0, "无网络时，添加的数据测试用基站", "", new Date());
							msg.what = HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATION_INFO_SUCCEED;
							handler.sendMessage(msg);
						}
					}
					break;
				case 1:
					LocBaseStation baseStation = new LocBaseStation(-1, code,
							"部署基站");
					OnBasestationPicked(baseStation, this.actionFlag);
					break;

				default:
					break;
				}
				break;

			default:
				break;
			}
		}
	}

	@Override
	protected void readedCardNeededData(String NFC_CODE) {
		switch (currentActionFlag) {
		case 0:

			break;
		case 1:

			break;
		case 2:

			if (dsAddFrag != null) {
				dsAddFrag.addNfcCode(NFC_CODE);
			}
			break;

		default:
			break;
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

	@Override
	public void onSensorNodeChecked() {
		// TODO Auto-generated method stub
		DialogFragmentForDynamicNodeHeartBeats fragment = new DialogFragmentForDynamicNodeHeartBeats();
	Bundle args = new Bundle();
	args.putString("sensorCode",currentSensor.getNfcCode());
		fragment.setArguments(args);
		fragment.setStyle(DialogFragment.STYLE_NO_TITLE,
				android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth);
		// android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth);
		fragment.show(fragmentmanager, "DialogFragmentForDynamicNodeHeartBeats");
	}


}
