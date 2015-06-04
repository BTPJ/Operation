package com.cyjt.operation.uitools;

import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cyjt.operation.R;
import com.cyjt.operation.base.AppContext;
import com.cyjt.operation.base.BaseActivityWithNFC;
import com.cyjt.operation.base.Constants;
import com.cyjt.operation.base.HandlerMessageCodes;
import com.cyjt.operation.bean.BaseStation;
import com.cyjt.operation.bean.Sensor;
import com.cyjt.operation.bean.SensorSyncService;
import com.cyjt.operation.fragment.YesOrNoDialogFragment;
import com.cyjt.operation.fragment.YesOrNoDialogFragment.YesOrNoFragmentActionListener;

public class ToolsSynSensorActivity extends BaseActivityWithNFC implements
		YesOrNoFragmentActionListener {
	private BaseStation currentBaseStation;
	private SensorSyncService sensorSyncService;
	private ProgressBar actionbar_progressBar;
	private ScrollView scrollView_for_basestation_info;
	private TextView textView_dync_sensor_submit_button;
	private TextView textView_tips;
	private TextView textView_dync_reset_edit;
	private Spinner spinner_dync_phase_edit;
	private Spinner spinner_dync_heartBeat_edit;
	private Spinner spinner_dync_phase_level_edit;
	private RadioGroup radioGroup_dync_force_trans;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerMessageCodes.DYNAMIC_FETCH_SENSOR_BOUND_BASESTATION_SUCCEED:
				scrollView_for_basestation_info.setVisibility(View.VISIBLE);
				textView_tips.setVisibility(View.GONE);
				currentBaseStation = (BaseStation) msg.obj;
				((TextView) findViewById(R.id.textView_dync_basestation_new_code_edit))
						.setText("" + currentBaseStation.getCode());
				break;
			case HandlerMessageCodes.DYNAMIC_FETCH_SENSOR_BOUND_BASESTATION_FAILED:
				Toast.makeText(ToolsSynSensorActivity.this, "未找到该节点",
						Toast.LENGTH_SHORT).show();
				break;
			case HandlerMessageCodes.SUBMIT_SENSOR_SYN_INFO_SUCCEED:
				Toast.makeText(ToolsSynSensorActivity.this, "提交成功",
						Toast.LENGTH_SHORT).show();
				break;

			default:
				super.handleMessage(msg);
				break;
			}
			stopProgress();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tools_syn_sensor);
		initActionBar();
		initView();
		viewEvent();
	}

	private void initActionBar() {
		actionbar_progressBar = (ProgressBar) findViewById(R.id.actionbar_progressBar);
		findViewById(R.id.actionbar_imageView_back_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						onBackPressed();

					}
				});
		if (canUseNFC) {

			((TextView) findViewById(R.id.actionbar_refresh_button))
					.setVisibility(View.GONE);
		} else {
			((TextView) findViewById(R.id.actionbar_refresh_button))
					.setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.actionbar_refresh_button))
					.setText("输入");
			((TextView) findViewById(R.id.actionbar_refresh_button))
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// 打开输入框
							getSensor("180037");
						}
					});
		}

		((TextView) findViewById(R.id.actionbar_textView_title))
				.setText("节点同步工具");
		stopProgress();
	}

	private void initView() {
		scrollView_for_basestation_info = (ScrollView) findViewById(R.id.scrollView_for_basestation_info);
		textView_tips = (TextView) findViewById(R.id.textView_tips);
		textView_dync_sensor_submit_button = (TextView) findViewById(R.id.textView_dync_sensor_submit_button);
		textView_dync_reset_edit = (TextView) findViewById(R.id.textView_dync_reset_edit);
		spinner_dync_phase_edit = (Spinner) findViewById(R.id.spinner_dync_phase_edit);
		spinner_dync_phase_level_edit = (Spinner) findViewById(R.id.spinner_dync_phase_level_edit);
		spinner_dync_heartBeat_edit = (Spinner) findViewById(R.id.spinner_dync_heartBeat_edit);
		radioGroup_dync_force_trans = (RadioGroup) findViewById(R.id.radioGroup_dync_force_trans);
		initPhaseAdapter();
		initPhaseLevelAdapter();
		initHeartCommAdapter();
	}

	private int positon = 0;;

	private void viewEvent() {
		scrollView_for_basestation_info.setVisibility(View.GONE);
		// textView_tips.setVisibility(View.GONE);
		textView_dync_sensor_submit_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						submitSensorSyncInfo(false);
					}
				});
		textView_dync_reset_edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 提示用户复位操作将使设置的数据都无效
				// 弹出提示，是否要执行复位操作
				YesOrNoDialogFragment yesOrNoFrag = new YesOrNoDialogFragment();
				Bundle b = new Bundle();
				b.putStringArray("stringArray", new String[] {
						"确定要复位该节点吗？\n\n复位操作将使当前设置的参数都无效", "取消", "复位" });
				yesOrNoFrag.setArguments(b);
				yesOrNoFrag.setStyle(DialogFragment.STYLE_NO_TITLE,
						android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth);
				yesOrNoFrag.show(getFragmentManager(), "YesOrNoDialogFragment");
			}
		});
		radioGroup_dync_force_trans
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup radioGroup, int id) {
						for (int i = 0; i < radioGroup.getChildCount(); i++) {
							if (radioGroup.getChildAt(i).getId() == id) {
								positon = i;
							}
						}
					}
				});
	}

	private ArrayAdapter<String> spinnerAdapter_phase = null;
	private ArrayAdapter<String> spinnerAdapter_phaselevel = null;
	private ArrayAdapter<String> spinnerAdapter_Heartcomm = null;

	private void initPhaseAdapter() {
		if (spinnerAdapter_phase == null) {
			spinnerAdapter_phase = new ArrayAdapter<String>(
					ToolsSynSensorActivity.this,
					android.R.layout.simple_spinner_item,
					Constants.SENSOR_SHPEAR);
			spinnerAdapter_phase
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner_dync_phase_edit.setAdapter(spinnerAdapter_phase);
		}
	}

	private void initPhaseLevelAdapter() {
		if (spinnerAdapter_phaselevel == null) {
			spinnerAdapter_phaselevel = new ArrayAdapter<String>(
					ToolsSynSensorActivity.this,
					android.R.layout.simple_spinner_item,
					Constants.SENSOR_SHPEAR_LEVEL);
			spinnerAdapter_phaselevel
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner_dync_phase_level_edit.setAdapter(spinnerAdapter_phaselevel);
		}
	}

	private void initHeartCommAdapter() {
		if (spinnerAdapter_Heartcomm == null) {
			spinnerAdapter_Heartcomm = new ArrayAdapter<String>(
					ToolsSynSensorActivity.this,
					android.R.layout.simple_spinner_item,
					Constants.SENSOR_TIME_BEATCOMMAND);
			spinnerAdapter_Heartcomm
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner_dync_heartBeat_edit.setAdapter(spinnerAdapter_Heartcomm);
		}
	}

	private void getSensorbelongBasestation(String NFC_CODE) {
		startProgress();
		Sensor s = new Sensor();
		s.setNfcCode(NFC_CODE);
		AppContext.getInstance().getVolleyTools()
				.dynamicFetchSensorBoundBasestation(handler, s);
	}

	private void getSensor(String NFC_CODE) {
		((TextView) findViewById(R.id.textView_dync_sensor_id_edit))
				.setText(NFC_CODE);
		getSensorbelongBasestation(NFC_CODE);
	}

	private void submitSensorSyncInfo(boolean reset) {
		getSensorSyncService(reset);
		if (currentBaseStation == null) {
			Toast.makeText(ToolsSynSensorActivity.this, "出错了，请重新获取要配置的节点",
					Toast.LENGTH_LONG).show();
			return;
		}
		startProgress();
		AppContext
				.getInstance()
				.getVolleyTools()
				.submitSensorSyncinfo(handler, sensorSyncService,
						currentBaseStation);
	}

	private void getSensorSyncService(boolean reset) {
		if (sensorSyncService == null) {
			sensorSyncService = new SensorSyncService();
		}
		sensorSyncService
				.setNodeId(((TextView) findViewById(R.id.textView_dync_sensor_id_edit))
						.getText().toString());
		String benchmarkX = ((EditText) findViewById(R.id.textView_dync_benchmarkX_edit))
				.getText().toString();
		benchmarkX = benchmarkX.equals("") ? 0 + "" : benchmarkX;
		String benchmarkY = ((EditText) findViewById(R.id.textView_dync_benchmarkY_edit))
				.getText().toString();
		benchmarkY = benchmarkY.equals("") ? 0 + "" : benchmarkY;
		sensorSyncService.setBenchmarkX(Integer.parseInt(benchmarkX));
		sensorSyncService.setBenchmarkY(Integer.parseInt(benchmarkY));
		sensorSyncService.setForceTrans(positon);
		sensorSyncService.setLevel(spinner_dync_phase_level_edit
				.getSelectedItemPosition());
		sensorSyncService.setNodeHeartBeatCommand(spinner_dync_heartBeat_edit
				.getSelectedItemPosition());
		if (reset) {
			sensorSyncService.setNodeReset(0);
		} else {
			sensorSyncService.setNodeReset(1);
		}
		sensorSyncService.setPhase(spinner_dync_phase_edit
				.getSelectedItemPosition());
		sensorSyncService.setReserved(0);
	}

	@Override
	protected void readedCardNeededData(String NFC_CODE) {
		getSensor(NFC_CODE);
	}

	private void startProgress() {
		actionbar_progressBar.setIndeterminate(false);
		actionbar_progressBar.setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.actionbar_refresh_button))
				.setVisibility(View.GONE);
	}

	private void stopProgress() {
		actionbar_progressBar.setIndeterminate(true);
		actionbar_progressBar.setVisibility(View.GONE);
		((TextView) findViewById(R.id.actionbar_refresh_button))
				.setVisibility(View.VISIBLE);
	}

	@Override
	public void onSureClicked() {
		// 请求网络执行复位操作
		submitSensorSyncInfo(true);
	}
}
