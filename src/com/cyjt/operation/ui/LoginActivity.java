package com.cyjt.operation.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.cyjt.operation.R;
import com.cyjt.operation.base.AppConfig;
import com.cyjt.operation.base.AppContext;
import com.cyjt.operation.base.BaseActivityWithNFC;
import com.cyjt.operation.base.HandlerMessageCodes;
import com.cyjt.operation.base.IpAddress;
import com.cyjt.operation.base.SharedPreferencesTools;
import com.cyjt.operation.base.Tools;
import com.cyjt.operation.bean.AppVersion;
import com.cyjt.operation.bean.User;
import com.cyjt.operation.fragment.DialogFragmentUpdateVersion;
import com.cyjt.operation.fragment.DialogFragmentUpdateVersion.DialogFragmentListener;
import com.cyjt.operation.fragment.ForDeveloperFragmentAddIp.OnAddIpAddressListener;
import com.cyjt.operation.fragment.ForDeveloperFragmentPickIp;
import com.cyjt.operation.fragment.UserLodgingDialogFragment;
import com.cyjt.operation.fragment.UserLodgingDialogFragment.OnUserLodgingListener;
import com.cyjt.operation.fragment.YesOrNoDialogFragment;
import com.cyjt.operation.fragment.YesOrNoDialogFragment.YesOrNoFragmentActionListener;
import com.cyjt.operation.view.ShimmerTextView;

/**
 * 登录界面
 * 
 * @author jacarri at 2014年5月1日
 */
public class LoginActivity extends BaseActivityWithNFC implements
		OnUserLodgingListener, OnAddIpAddressListener,
		YesOrNoFragmentActionListener, DialogFragmentListener {

	private static final String TAG = "MainActivity";
	private ImageView imageView_logo;
	private ImageView imageView_loding_anim_item1;
	private ImageView imageView_loding_anim_item2;
	private ImageView imageView_loding_anim_item3;
	private ImageView imageView_loding_anim_item4;
	private ShimmerTextView shimmerTextView;
	private User user;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerMessageCodes.BUILDER_LOGIN_SUCCEED:
				// 登陆成功，获取登陆者信息
				AppContext.getInstance().getVolleyTools()
						.fetchBuilderInfo(handler);
				// 保存登陆时使用的Id，以方便下次输入
				AppContext
						.getInstance()
						.getSharedPreferencesTools()
						.writeStringPreferences(
								SharedPreferencesTools.LOGIN_SUCCEED_USER,
								"LOGIN_SUCCEED_USER", user.getCode());
				break;
			case HandlerMessageCodes.BUILDER_LOGIN_FAILED:
				// 登录失败
				Toast.makeText(LoginActivity.this, "登陆卡无效", Toast.LENGTH_LONG)
						.show();
				break;
			case HandlerMessageCodes.FETCH_BUILDER_INFO_SUCCEED:
				// 将登录人员数据保存在内存中
				AppContext.getInstance().setUser((User) msg.obj);
				// 提示绑定信息
				Toast.makeText(LoginActivity.this,
						AppContext.getInstance().getUser().getName() + ",你好!",
						Toast.LENGTH_LONG).show();
				// 施工人员
				Intent i3 = new Intent(LoginActivity.this, HomeActivity.class);
				startActivity(i3);
				// finish();
				break;

			case HandlerMessageCodes.FETCH_NEW_APP_VERSION_SUCCEED:
				final int curVersionCode = Tools.getCurrentVersion();
				appVersion = (AppVersion) msg.obj;
				if (appVersion != null) {
					if (curVersionCode != 0
							&& curVersionCode < appVersion.getAppVersionCode()) {
						// 获取到新版本，提示更新
						// 弹出提示，是否要删除该车位
						YesOrNoDialogFragment yesOrNoFrag = new YesOrNoDialogFragment();
						Bundle b = new Bundle();
						b.putStringArray(
								"stringArray",
								new String[] {
										"发现新版本！要更新吗？"
												+ "\n\n"
												+ appVersion.getUpdateLog()
												+ "\n大小："
												+ appVersion
														.getAppVersionSize()
												+ "MB", "稍后", "立即更新" });
						yesOrNoFrag.setArguments(b);
						yesOrNoFrag
								.setStyle(
										DialogFragment.STYLE_NO_TITLE,
										android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth);
						yesOrNoFrag.show(getFragmentManager(),
								"YesOrNoDialogFragment");
					}
				}
				break;
			case HandlerMessageCodes.HANDLER_CODE_UP_APP_DOWN_UPDATE:
				if (downloadFragment != null && !downloadFragment.isHidden()) {
					downloadFragment
							.refeashProgress(msg.arg1, (String) msg.obj);
				}
				break;
			case HandlerMessageCodes.HANDLER_CODE_UP_APP_DOWN_DOWN_OVER:
				if (downloadFragment != null && !downloadFragment.isHidden()) {
					downloadFragment.dialogDismiss();
				}
				installApk((String) msg.obj);
				break;
			case HandlerMessageCodes.HANDLER_CODE_UP_APP_DOWN_ERROR:
				if (downloadFragment != null && !downloadFragment.isHidden()) {
					downloadFragment.dialogDismiss();
				}
				Toast.makeText(LoginActivity.this, "下载出错", Toast.LENGTH_SHORT)
						.show();
				break;
			case HandlerMessageCodes.HTTP_VOLLEY_ERROR:
				Toast.makeText(LoginActivity.this, "Intent访问出错",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loging);
		// 检查更新
		if (AppConfig.OPEN_UPDATE_VERSION) {
			checkUpdateVersion();
		}
		if (!canUseNFC) {
			Toast.makeText(LoginActivity.this, "设备不支持NFC,请手动录入必要信息!",
					Toast.LENGTH_LONG).show();
			userLodgingDialog();
		}
		initView();
		viewEvent();
		startAnim();
		// 进度条测试
		// onSureClicked();
	}

	/**
	 * 控件初始化
	 */
	private void initView() {
		shimmerTextView = (ShimmerTextView) findViewById(R.id.shimmerTextView);
		imageView_logo = (ImageView) findViewById(R.id.imageView_logo);
		imageView_loding_anim_item1 = (ImageView) findViewById(R.id.imageView_loding_anim_item1);
		imageView_loding_anim_item2 = (ImageView) findViewById(R.id.imageView_loding_anim_item2);
		imageView_loding_anim_item3 = (ImageView) findViewById(R.id.imageView_loding_anim_item3);
		imageView_loding_anim_item4 = (ImageView) findViewById(R.id.imageView_loding_anim_item4);
	}

	private UserLodgingDialogFragment fragment;

	/**
	 * 控件的点击事件
	 */
	private void viewEvent() {
		shimmerTextView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				userLodgingDialog();
				return false;
			}
		});
		imageView_logo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openIpSelectFragment();
			}
		});
	}

	/** imagine_logo被点击的次数 */
	private int stemp = 0;
	private ForDeveloperFragmentPickIp fdF;

	/**
	 * 2秒内连续点击5次就打开IP选择窗口
	 */
	private void openIpSelectFragment() {
		if (stemp == 0) {
			new Thread() {
				@Override
				public void run() {
					try {
						sleep(2 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						stemp = 0;
					}
				}
			}.start();
		}
		stemp++;
		Log.d(TAG, "点击次数" + stemp);
		if (stemp > 4) {
			stemp = 0;
			Toast.makeText(LoginActivity.this, "请选择要调试的IP地址",
					Toast.LENGTH_SHORT).show();
			// 弹出IP地址选择
			fdF = new ForDeveloperFragmentPickIp();
			fdF.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
			fdF.show(getFragmentManager(), "ForDeveloperFragmentPickIp");
		}
	}

	/**
	 * 开启动画
	 */
	private void startAnim() {
		imageView_loding_anim_item1.setAnimation(AnimationUtils.loadAnimation(
				LoginActivity.this, R.anim.rotate_item1));
		imageView_loding_anim_item2.setAnimation(AnimationUtils.loadAnimation(
				LoginActivity.this, R.anim.rotate_item2));
		imageView_loding_anim_item3.setAnimation(AnimationUtils.loadAnimation(
				LoginActivity.this, R.anim.rotate_item3));
		imageView_loding_anim_item4.setAnimation(AnimationUtils.loadAnimation(
				LoginActivity.this, R.anim.rotate_item4));
	}

	/**
	 * 如果设备不支持NFC，则弹出登录账户输入界面
	 */
	private void userLodgingDialog() {
		if (!canUseNFC) {
			// 弹出登录账户输入界面
			/* 往bundle中添加数据 */
			fragment = new UserLodgingDialogFragment();
			fragment.setStyle(
					DialogFragment.STYLE_NO_TITLE,
					android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth);
			fragment.show(getFragmentManager(), "UserLodgingDialogFragment");
		}
	}

	@Override
	protected void readedCardId(String NFC_CODE) {
		login(NFC_CODE);
	}

	/**
	 * post提交验证登录信息
	 * 
	 * @param string
	 * @throws JSONException
	 */
	private void login(final String nfcCode) {
		user = new User();
		user.setCode(nfcCode);
		AppContext.getInstance().getVolleyTools()
				.builderLogin(handler, nfcCode);

	}

	/**
	 * 检查是否有新版本并更新
	 */
	private void checkUpdateVersion() {
		AppContext.getInstance().getVolleyTools().getNewAppVersion(handler);
	}

	@Override
	public void OnUserLodging(User user) {
		login(user.getCode());
	}

	@Override
	public void OnAddSucceed(IpAddress ip) {
		if (fdF != null) {
			fdF.OnAddSucceed(ip);
		}
	}

	// @Override
	// protected void readedCardNull() {
	// super.readedCardNull();
	// login("04d4dd12853280");
	// }

	private DialogFragmentUpdateVersion downloadFragment;

	@Override
	public void onSureClicked() {
		if (fragment != null && !fragment.isHidden()) {
			fragment.dismiss();
		}
		// 打开下载dialog
		downloadFragment = new DialogFragmentUpdateVersion();
		downloadFragment
				.setStyle(
						DialogFragment.STYLE_NO_TITLE,
						android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth);
		downloadFragment.show(getFragmentManager(),
				"DialogFragmentUpdateVersion");
	}

	/**
	 * 下载apk
	 * 
	 * @param url
	 */
	private void downloadApk() {
		if (!beforeDownloadFile()) {
			handler.sendEmptyMessage(HandlerMessageCodes.HANDLER_CODE_UP_APP_DOWN_ERROR);
			return;
		}
		// 文件下载测试
		// apkUrl = "http://down11.zol.com.cn/suyan/u17.2.0.2.apk";
		interceptFlag = true;
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}

	/**
	 * 安装apk
	 * 
	 * @param apkFilePath
	 *            安装路径
	 */
	private void installApk(String apkFilePath) {
		File apkfile = new File(apkFilePath);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		startActivity(i);
		finish();
	}

	private AppVersion appVersion;
	/** 目标应用下载地址 */
	private String apkUrl;
	/** 下载线程 */
	private Thread downLoadThread;
	/** 终止标记 */
	private boolean interceptFlag = false;
	/** 临时下载文件路径 */
	private String tmpFilePath = "";
	/** apk保存完整路径 */
	private String apkFilePath = "";
	private File ApkFile = null;

	private boolean beforeDownloadFile() {
		// 判断是否挂载了SD卡
		if (getSDPath() == null) {
			Toast.makeText(LoginActivity.this, "请检查SD卡", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		if (appVersion == null) {
			return false;
		}
		apkUrl = appVersion.getDownloadUrl();
		if (apkUrl == null || apkUrl.equals("")) {
			return false;
		}
		String apkName = "construct_" + appVersion.getAppVersionName() + ".apk";
		String tmpApk = "construct_" + appVersion.getAppVersionName() + ".tmp";
		String savePath = getSDPath() + "/Update/";
		// 判断文件夹是否存在
		File file = new File(savePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		apkFilePath = savePath + apkName;
		tmpFilePath = savePath + tmpApk;
		ApkFile = new File(apkFilePath);
		// 判断文件是否存在
		if (ApkFile.exists()) {
			handler.sendMessage(handler.obtainMessage(
					HandlerMessageCodes.HANDLER_CODE_UP_APP_DOWN_DOWN_OVER,
					apkFilePath));
			return false;
		}
		return true;
	}

	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				// 输出临时下载文件
				File tmpFile = new File(tmpFilePath);
				FileOutputStream fos = new FileOutputStream(tmpFile);
				Log.v("demo", "APK文件的下载路径apkUrl:" + apkUrl);
				URL url = new URL(apkUrl);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();
				// 显示文件大小格式：2个小数点显示
				DecimalFormat df = new DecimalFormat("0.00");
				// 进度条下面显示的总文件大小
				String apkFileSize = df.format((float) length / 1024 / 1024)
						+ "MB";
				int count = 0;
				byte buf[] = new byte[1024];

				do {
					int numread = is.read(buf);

					count += numread;
					// 进度条下面显示的当前下载文件大小
					// 当前进度值
					String tmpFileSize = df.format((float) count / 1024 / 1024)
							+ "MB/" + apkFileSize;
					// 更新进度
					handler.sendMessage(handler
							.obtainMessage(
									HandlerMessageCodes.HANDLER_CODE_UP_APP_DOWN_UPDATE,
									count * 100 / length, 0, tmpFileSize));
					if (numread <= 0) {
						// 下载完成 - 将临时下载文件转成APK文件
						if (ApkFile != null && tmpFile.renameTo(ApkFile)) {
							// 通知安装
							handler.sendMessage(handler
									.obtainMessage(
											HandlerMessageCodes.HANDLER_CODE_UP_APP_DOWN_DOWN_OVER,
											apkFilePath));
						}
						break;
					}
					fos.write(buf, 0, numread);
				} while (interceptFlag);// 点击取消就停止下载
				Log.v("demo", "停止下载");
				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	};

	@Override
	public void onDialogFragmentCreated() {
		downloadApk();
		// 模拟下载
		// new Thread() {
		// int count = 0;
		// int length = 262;
		//
		// @Override
		// public void run() {
		// try {
		// while (true) {
		// count++;
		// sleep(40);
		// handler.sendMessage(handler
		// .obtainMessage(
		// HandlerMessageCodes.HANDLER_CODE_UP_APP_DOWN_UPDATE,
		// count * 100 / length, 0, count + "/"
		// + length));
		// if (count == length) {
		// // 通知安装
		// break;
		// }
		// }
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// }.start();
	}
}
