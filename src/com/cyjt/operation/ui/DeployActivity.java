package com.cyjt.operation.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.cyjt.operation.R;
import com.cyjt.operation.uidynamic.DynamicMapAndListActivity;
import com.cyjt.operation.uitools.ToolsActivity;

/**
 * 部署界面
 * 
 * @author LTP
 *
 */
public class DeployActivity extends Activity {
	/** 静态部署 */
	private Button button_deploy_static;
	/** 动态部署 */
	private Button button_deploy_dynamic;
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
		button_deploy_static = (Button) findViewById(R.id.button_deploy_static);
		button_deploy_dynamic = (Button) findViewById(R.id.button_deploy_dynamic);
		button_deploy_tool = (Button) findViewById(R.id.button_deploy_tool);
	}

	/**
	 * 控件事件监听
	 */
	private void viewEvent() {
		button_deploy_static.setOnClickListener(new OnClickListener() {
			// 当按下静态部署按键就跳转到路段界面
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DeployActivity.this,
						NewPickRoadAreasActivity.class);
				startActivity(intent);
			}
		});
		button_deploy_dynamic.setOnClickListener(new OnClickListener() {
			// 当按下动态部署按键就跳转到路段界面
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DeployActivity.this,
						DynamicMapAndListActivity.class);
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
