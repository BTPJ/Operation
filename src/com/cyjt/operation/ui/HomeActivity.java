package com.cyjt.operation.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.cyjt.operation.R;

/**
 * 功能界面
 * 
 * @author LTP
 *
 */
public class HomeActivity extends Activity implements OnClickListener {
	/** 设备状态按键 */
	private Button button_device_state;
	/** 工单管理按键 */
	private Button button_workOrder_management;
	/** 消息管理按键 */
	private Button button_message_management;
	/** 工作量统计按键 */
	private Button button_workLoad_account;
	/** 部署按键 */
	private Button button_deploy;
	/** 问题反馈按键 */
	private Button button_feedBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		initView();
		viewEvent();
	}

	/**
	 * 控件初始化
	 */
	private void initView() {
		button_device_state = (Button) findViewById(R.id.button_device_state);
		button_workOrder_management = (Button) findViewById(R.id.button_workOrder_management);
		button_message_management = (Button) findViewById(R.id.button_message_management);
		button_workLoad_account = (Button) findViewById(R.id.button_workLoad_account);
		button_deploy = (Button) findViewById(R.id.button_deploy);
		button_feedBack = (Button) findViewById(R.id.button_feedBack);
	}

	/**
	 * 控件事件监听
	 */
	private void viewEvent() {
		button_device_state.setOnClickListener(this);
		button_workOrder_management.setOnClickListener(this);
		button_message_management.setOnClickListener(this);
		button_workLoad_account.setOnClickListener(this);
		button_deploy.setOnClickListener(this);
		button_feedBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_device_state:

			break;
		case R.id.button_workOrder_management:

			break;
		case R.id.button_message_management:

			break;
		case R.id.button_workLoad_account:

			break;
		case R.id.button_deploy:
			// 部署按键被点击后，跳转到部署界面（携带标题）
			Intent intent = new Intent(HomeActivity.this, DeployActivity.class);
			intent.putExtra("title", "部署");
			startActivity(intent);
			break;
		case R.id.button_feedBack:

			break;
		default:
			break;
		}

	}
}
