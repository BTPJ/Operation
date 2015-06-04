package com.cyjt.operation.uitools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cyjt.operation.R;

public class ToolsActivity extends Activity {
	private GridLayout rootView;
	private ProgressBar actionbar_progressBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tools);
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
		findViewById(R.id.actionbar_refresh_button).setVisibility(View.GONE);
		((TextView) findViewById(R.id.actionbar_textView_title))
				.setText("部署工具");
		stopProgress();
	}

	private void initView() {
		rootView = (GridLayout) findViewById(R.id.rootView);
		bandViewListener(findViewById(R.id.textView1));
		bandViewListener(findViewById(R.id.textView2));
		bandViewListener(findViewById(R.id.textView3));
		bandViewListener(findViewById(R.id.textView_sensor_sync));
	}

	private void viewEvent() {
		findViewById(R.id.textView1).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ToolsActivity.this,
						ToolsNFCActivity.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.textView2).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ToolsActivity.this,
						ToolsSetupDynamicBasestationActivity.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.textView3).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ToolsActivity.this,
						ToolsChangeArrayStatusActivity.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.textView_sensor_sync).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ToolsActivity.this,
						ToolsSynSensorActivity.class);
				startActivity(intent);
			}
		});
	}

	private View beingPressedView = null;

	private void bandViewListener(final View view) {
		view.setOnDragListener(new OnDragListener() {

			@Override
			public boolean onDrag(View v, DragEvent event) {
				int index = 0;
				for (int i = 0; i < rootView.getChildCount(); i++) {
					if (rootView.getChildAt(i).getId() == v.getId()) {
						index = i;
						break;
					}
				}
				// 处理所有需要的事件
				switch (event.getAction()) {
				case DragEvent.ACTION_DRAG_STARTED:
					if (v.getId() == beingPressedView.getId()) {
						v.setVisibility(View.GONE);
					}
					break;

				case DragEvent.ACTION_DRAG_ENTERED:
					if (v.getId() == beingPressedView.getId()) {
					} else {
						rootView.removeView(beingPressedView);
						rootView.addView(beingPressedView, index);
					}
					break;

				case DragEvent.ACTION_DRAG_EXITED:

					break;

				case DragEvent.ACTION_DRAG_LOCATION:

					break;

				case DragEvent.ACTION_DROP:
					rootView.removeView(beingPressedView);
					rootView.addView(beingPressedView, index);
					break;

				case DragEvent.ACTION_DRAG_ENDED:
					v.setVisibility(View.VISIBLE);
					break;
				}
				return true;
			}

		});
		view.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				DragShadowBuilder shadowBuilder = new DragShadowBuilder(v);
				v.startDrag(null, shadowBuilder, v, 0);
				beingPressedView = v;
				return false;
			}
		});
	}
	private void startProgress() {
		actionbar_progressBar.setIndeterminate(false);
		actionbar_progressBar.setVisibility(View.VISIBLE);
	}

	private void stopProgress() {
		actionbar_progressBar.setIndeterminate(true);
		actionbar_progressBar.setVisibility(View.GONE);
	}
}
