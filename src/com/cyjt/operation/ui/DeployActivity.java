package com.cyjt.operation.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.cyjt.operation.R;
import com.cyjt.operation.uitools.ToolsActivity;

/**
 * 部署界面
 * 
 * @author LTP
 *
 */
public class DeployActivity extends Activity {
//	/** 部署 */jhsdalhasdlf,sdads
	private Button button_deploy_static;
	/** 部署工具 */
	private Button button_deploy_tool;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deploy);
		initView();
		viewEvent();
	}

	/**
	 * 控件初始化
	 */
	private void initView() {
		button_deploy_static = (Button) findViewById(R.id.button_deploy);
		button_deploy_tool = (Button) findViewById(R.id.button_deploy_tool);
	}

	/**
	 * 控件事件监听
	 */
	private void viewEvent() {
		button_deploy_static.setOnClickListener(new OnClickListener() {
			// 当按下部署按键就跳转到路段界面
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DeployActivity.this,
						NewPickRoadAreasActivity.class);
				startActivity(intent);
			}
		});

		button_deploy_tool.setOnClickListener(new OnClickListener() {
			// 当按下部署工具按键就跳转到部署工具界面
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DeployActivity.this,
						ToolsActivity.class);
				startActivity(intent);
			}
		});
	}
}
