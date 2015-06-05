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
 * �������
 * 
 * @author LTP
 *
 */
public class DeployActivity extends Activity {
	/** ��̬���� */
	private Button button_deploy_static;
	/** ��̬���� */
	private Button button_deploy_dynamic;
	/** ���𹤾� */
	private Button button_deploy_tool;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deploy);
		initView();
		viewEvent();
	}

	/**
	 * �ؼ���ʼ��
	 */
	private void initView() {
		button_deploy_static = (Button) findViewById(R.id.button_deploy_static);
		button_deploy_dynamic = (Button) findViewById(R.id.button_deploy_dynamic);
		button_deploy_tool = (Button) findViewById(R.id.button_deploy_tool);
	}

	/**
	 * �ؼ��¼�����
	 */
	private void viewEvent() {
		button_deploy_static.setOnClickListener(new OnClickListener() {
			// �����¾�̬���𰴼�����ת��·�ν���
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DeployActivity.this,
						NewPickRoadAreasActivity.class);
				startActivity(intent);
			}
		});
		button_deploy_dynamic.setOnClickListener(new OnClickListener() {
			// �����¶�̬���𰴼�����ת��·�ν���
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DeployActivity.this,
						DynamicMapAndListActivity.class);
				startActivity(intent);
			}
		});
		button_deploy_tool.setOnClickListener(new OnClickListener() {
			// �����²��𹤾߰�������ת�����𹤾߽���
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DeployActivity.this,
						ToolsActivity.class);
				startActivity(intent);
			}
		});
	}
}
