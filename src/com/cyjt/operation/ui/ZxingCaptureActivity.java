package com.cyjt.operation.ui;

import java.io.IOException;
import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cyjt.operation.R;
import com.cyjt.operation.zxing.camera.CameraManager;
import com.cyjt.operation.zxing.decoding.CaptureActivityHandler;
import com.cyjt.operation.zxing.decoding.InactivityTimer;
import com.cyjt.operation.zxing.view.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

/**
 * 相机Activity
 * 
 * @author kullo
 * 
 */
public class ZxingCaptureActivity extends Activity implements Callback {

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private Button cancelScanButton;
	private String useDescribe = "请扫描二维码";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.z_xing_camera);
		// ViewUtil.addTopView(getApplicationContext(), this,
		// R.string.scan_card);
		if (getIntent().getStringExtra("useDescribe") != null) {
			useDescribe = getIntent().getStringExtra("useDescribe");
		}
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		cancelScanButton = (Button) this.findViewById(R.id.btn_cancel_scan);
		((TextView) findViewById(R.id.textView_useDescribe)).setText(""
				+ useDescribe);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		// 用户有可能会直接返回
		setResult(Activity.RESULT_CANCELED);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		// 先在此处配置surfaceView的显示像素
		// surfaceView.setLayoutParams(new LinearLayout.LayoutParams(
		// (int) (600), 800));
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			// 初始化相机
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
		}
		decodeFormats = null;
		characterSet = null;
		// quit the scan view
		cancelScanButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ZxingCaptureActivity.this.finish();
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * 解码二维码并将获得的数据result
	 * 
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();

		String resultString = result.getText();
		// FIXME
		if (resultString.equals("")) {
			Toast.makeText(ZxingCaptureActivity.this, "Scan failed!",
					Toast.LENGTH_SHORT).show();
		} else {
			Intent resultIntent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("result", resultString);
			resultIntent.putExtra("Zxing", bundle);
			setResult(RESULT_OK, resultIntent);
		}
		ZxingCaptureActivity.this.finish();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			// 获得一个manager对象
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);

		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}
}