package com.cyjt.operation.uitools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cyjt.operation.R;
import com.cyjt.operation.base.AppContext;
import com.cyjt.operation.base.BaseActivity;
import com.cyjt.operation.base.Constants;
import com.cyjt.operation.base.HandlerMessageCodes;
import com.cyjt.operation.bean.BaseStation;
import com.cyjt.operation.bean.SetupService;
import com.cyjt.operation.ui.ZxingCaptureActivity;

public class ToolsSetupDynamicBasestationActivity extends BaseActivity {
	private ProgressBar actionbar_progressBar;
	private ScrollView scrollView_for_basestation_info;
	private TextView textView_dynamic_basestation_new_code_edit;
	private EditText textView_dynamic_basestation_equipmentId_edit;
	private EditText textView_dynamic_basestation_panid_edit;
	private Spinner spinner_dynamic_type_edit;
	private Spinner spinner_dynamic_time_src_edit;
	private EditText textView_dynamic_service_ip_edit;
	private EditText textView_dynamic_service_port_edit;
	private TextView textView_dynamic_basestation_frequency_point_value;
	private SeekBar seekBar_dynamic_basestation_frequency_point_edit;
	private TextView textView_dynamic_basestation_percentage_value;
	private TextView textView_dynamic_basestation_submit_button;
	private SeekBar seekBar_dynamic_basestation_frequency_percentage_edit;
	private TextView textView_dynamic_basestation_time_period_value;
	private SeekBar seekBar_dynamic_basestation_time_period_edit;
	private ArrayAdapter<String> spinnerAdapter_type = null;
	private ArrayAdapter<String> spinnerAdapter_src = null;
	// 以下值全部默认表示"不修改"
	private int frequency_point = 0xff;
	private int percentage = 0;
	// 设备类型
	private int type = 0x00;
	// 配时源
	private int src = 0;
	private int superNode = 0;
	private String ip = "0.0.0.0";
	private String port = "0";
	// PANid
	private String panid = "0";
	// 设备id
	private String code = "0";
	private BaseStation bs;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATION_INFO_SUCCEED:
				scrollView_for_basestation_info.setVisibility(View.VISIBLE);
				// 获取基站信息成功
				bs = (BaseStation) msg.obj;
				upUI(bs);
				break;
			case HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATION_INFO_FAILED:
				Toast.makeText(ToolsSetupDynamicBasestationActivity.this,
						"暂无该基站相关信息", Toast.LENGTH_SHORT).show();
				break;
			case HandlerMessageCodes.DYNAMIC_SUBMIT_BASESTATION_DATA_SYNC_SUCCEED:
				Toast.makeText(ToolsSetupDynamicBasestationActivity.this,
						"修改成功", Toast.LENGTH_SHORT).show();
				break;
			case HandlerMessageCodes.DYNAMIC_SUBMIT_BASESTATION_DATA_SYNC_FAILED:
				Toast.makeText(ToolsSetupDynamicBasestationActivity.this,
						"提交失败", Toast.LENGTH_SHORT).show();
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
		setContentView(R.layout.activity_tools_set_up_dynamic_basestation);
		initActionBar();
		initView();
		viewEvent();
		if (getIntent().getSerializableExtra("baseStation") != null) {
			Message msg = handler
					.obtainMessage(
							HandlerMessageCodes.HTTP_BUILDER_FETCH_BASESTATION_INFO_SUCCEED,
							getIntent().getSerializableExtra("baseStation"));
			handler.sendMessage(msg);
			return;
		}
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
		((TextView) findViewById(R.id.actionbar_refresh_button)).setText("扫描");
		findViewById(R.id.actionbar_refresh_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent i = new Intent();
						i.setClass(ToolsSetupDynamicBasestationActivity.this,
								ZxingCaptureActivity.class);
						i.putExtra("useDescribe", "请扫描要配置的基站");
						startActivityForResult(
								i,
								HandlerMessageCodes.INTENT_ACTION_CODE_ZXING_REQUEST_CODE);
					}
				});

		((TextView) findViewById(R.id.actionbar_textView_title))
				.setText("基站数据同步工具");
		stopProgress();
	}

	private void initView() {
		scrollView_for_basestation_info = (ScrollView) findViewById(R.id.scrollView_for_basestation_info);
		textView_dynamic_basestation_new_code_edit = (TextView) findViewById(R.id.textView_dynamic_basestation_new_code_edit);
		textView_dynamic_basestation_panid_edit = (EditText) findViewById(R.id.textView_dynamic_basestation_panid_edit);
		textView_dynamic_basestation_equipmentId_edit = (EditText) findViewById(R.id.textView_dynamic_basestation_equipmentId_edit);
		textView_dynamic_service_ip_edit = (EditText) findViewById(R.id.textView_dynamic_service_ip_edit);
		textView_dynamic_service_port_edit = (EditText) findViewById(R.id.textView_dynamic_service_port_edit);
		spinner_dynamic_type_edit = (Spinner) findViewById(R.id.spinner_dynamic_type_edit);
		spinner_dynamic_time_src_edit = (Spinner) findViewById(R.id.spinner_dynamic_time_src_edit);
		textView_dynamic_basestation_frequency_point_value = (TextView) findViewById(R.id.textView_dynamic_basestation_frequency_point_value);
		textView_dynamic_basestation_percentage_value = (TextView) findViewById(R.id.textView_dynamic_basestation_percentage_value);
		seekBar_dynamic_basestation_frequency_point_edit = (SeekBar) findViewById(R.id.seekBar_dynamic_basestation_frequency_point_edit);
		seekBar_dynamic_basestation_frequency_percentage_edit = (SeekBar) findViewById(R.id.seekBar_dynamic_basestation_frequency_percentage_edit);
		textView_dynamic_basestation_submit_button = (TextView) findViewById(R.id.textView_dynamic_basestation_submit_button);
		seekBar_dynamic_basestation_time_period_edit = (SeekBar) findViewById(R.id.seekBar_dynamic_basestation_time_period_edit);
		textView_dynamic_basestation_time_period_value = (TextView) findViewById(R.id.textView_dynamic_basestation_time_period_value);
		initTypeAdapter();
		initSrcAdapter();
		initProgress();
	}

	private void viewEvent() {
		scrollView_for_basestation_info.setVisibility(View.GONE);
		seekBar_dynamic_basestation_frequency_point_edit
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {

					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						if (progress == 0) {
							frequency_point = 0xff;
							textView_dynamic_basestation_frequency_point_value
									.setText("不修改");
							return;
						}
						frequency_point = progress - 1;
						textView_dynamic_basestation_frequency_point_value
								.setText(frequency_point * 0.625 + 410
										+ "  -->  " + frequency_point + "级");
					}
				});
		seekBar_dynamic_basestation_frequency_percentage_edit
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {

					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						percentage = progress;
						textView_dynamic_basestation_percentage_value
								.setText(progress * 100 / 63 + "%" + "  -->  "
										+ progress + "级");
					}
				});
		seekBar_dynamic_basestation_time_period_edit
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {

					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						superNode = progress / 3;
						textView_dynamic_basestation_time_period_value
								.setText(progress + " 秒");
					}
				});
		textView_dynamic_basestation_submit_button
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						submitData(getDataInfo());
					}
				});
		textView_dynamic_service_port_edit
				.addTextChangedListener(new TextWatcher() {
					String string = "";

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						if (!s.toString().equals("")) {
							if (Integer.parseInt(s.toString()) > 65535) {
								textView_dynamic_service_port_edit
										.setText(string);
								textView_dynamic_service_port_edit
										.setSelection(string.length() - 1);
							} else if (Integer.parseInt(s.toString()) < 0) {
								textView_dynamic_service_port_edit.setText("0");
							}
						}
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
						string = textView_dynamic_service_port_edit.getText()
								.toString();
						if (!s.toString().equals("")
								&& Integer.parseInt(s.toString()) > 65535) {
							Toast.makeText(
									ToolsSetupDynamicBasestationActivity.this,
									"端口值不应大于65535", Toast.LENGTH_LONG).show();
						}
					}

					@Override
					public void afterTextChanged(Editable s) {

					}
				});
	}

	private void initTypeAdapter() {
		if (spinnerAdapter_type == null) {
			spinnerAdapter_type = new ArrayAdapter<String>(
					ToolsSetupDynamicBasestationActivity.this,
					android.R.layout.simple_spinner_item, Constants.TYPE_ARRAY);
			spinnerAdapter_type
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner_dynamic_type_edit.setAdapter(spinnerAdapter_type);
		}
	}

	private void initSrcAdapter() {
		if (spinnerAdapter_src == null) {
			spinnerAdapter_src = new ArrayAdapter<String>(
					ToolsSetupDynamicBasestationActivity.this,
					android.R.layout.simple_spinner_item, Constants.SRC_ARRAY);
			spinnerAdapter_src
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner_dynamic_time_src_edit.setAdapter(spinnerAdapter_src);
		}
	}

	private void initProgress() {
		// seekBar_dynamic_basestation_frequency_point_edit.setProgress(1);
		// seekBar_dynamic_basestation_frequency_percentage_edit.setProgress(1);
	}

	private void upUI(BaseStation bs) {
		textView_dynamic_basestation_new_code_edit.setText("" + bs.getCode());
	}

	private SetupService getDataInfo() {
		code = textView_dynamic_basestation_equipmentId_edit.getText()
				.toString();
		ip = textView_dynamic_service_ip_edit.getText().toString();
		port = textView_dynamic_service_port_edit.getText().toString();
		panid = textView_dynamic_basestation_panid_edit.getText().toString();
		type = spinner_dynamic_type_edit.getSelectedItemPosition();
		if (type == 0) {
			type = 0x00;
		} else {
			type = type - 1;
		}
		src = spinner_dynamic_time_src_edit.getSelectedItemPosition();
		// frequency_point
		// percentage
		return new SetupService((ip.equals("")) ? "0.0.0.0" : ip,
				(port.equals("")) ? "0" : port, type, (code.equals("")) ? "0"
						: code, src, (panid.equals("")) ? "0" : panid,
				frequency_point, percentage, superNode);
	}

	private void submitData(SetupService ss) {
		startProgress();
		AppContext.getInstance().getVolleyTools()
				.dynamicSubmitBasestationDataSync(handler, ss, bs);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case HandlerMessageCodes.INTENT_ACTION_CODE_ZXING_REQUEST_CODE:
				String s = (String) data.getBundleExtra("Zxing").get("result");
				startProgress();
				// 请求网络查询该基站
				AppContext.getInstance().getVolleyTools()
						.fetchBasestationInfo(handler, s);
			default:
				// Toast.makeText(ToolsSetupDynamicBasestationActivity.this,
				// "请扫描符合协议的二维码", Toast.LENGTH_SHORT).show();
				break;
			}
		}
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
}
