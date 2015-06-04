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
 * ��¼����
 * 
 * @author jacarri at 2014��5��1��
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
				// ��½�ɹ�����ȡ��½����Ϣ
				AppContext.getInstance().getVolleyTools()
						.fetchBuilderInfo(handler);
				// �����½ʱʹ�õ�Id���Է����´�����
				AppContext
						.getInstance()
						.getSharedPreferencesTools()
						.writeStringPreferences(
								SharedPreferencesTools.LOGIN_SUCCEED_USER,
								"LOGIN_SUCCEED_USER", user.getCode());
				break;
			case HandlerMessageCodes.BUILDER_LOGIN_FAILED:
				// ��¼ʧ��
				Toast.makeText(LoginActivity.this, "��½����Ч", Toast.LENGTH_LONG)
						.show();
				break;
			case HandlerMessageCodes.FETCH_BUILDER_INFO_SUCCEED:
				// ����¼��Ա���ݱ������ڴ���
				AppContext.getInstance().setUser((User) msg.obj);
				// ��ʾ����Ϣ
				Toast.makeText(LoginActivity.this,
						AppContext.getInstance().getUser().getName() + ",���!",
						Toast.LENGTH_LONG).show();
				// ʩ����Ա
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
						// ��ȡ���°汾����ʾ����
						// ������ʾ���Ƿ�Ҫɾ���ó�λ
						YesOrNoDialogFragment yesOrNoFrag = new YesOrNoDialogFragment();
						Bundle b = new Bundle();
						b.putStringArray(
								"stringArray",
								new String[] {
										"�����°汾��Ҫ������"
												+ "\n\n"
												+ appVersion.getUpdateLog()
												+ "\n��С��"
												+ appVersion
														.getAppVersionSize()
												+ "MB", "�Ժ�", "��������" });
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
				Toast.makeText(LoginActivity.this, "���س���", Toast.LENGTH_SHORT)
						.show();
				break;
			case HandlerMessageCodes.HTTP_VOLLEY_ERROR:
				Toast.makeText(LoginActivity.this, "Intent���ʳ���",
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
		// ������
		if (AppConfig.OPEN_UPDATE_VERSION) {
			checkUpdateVersion();
		}
		if (!canUseNFC) {
			Toast.makeText(LoginActivity.this, "�豸��֧��NFC,���ֶ�¼���Ҫ��Ϣ!",
					Toast.LENGTH_LONG).show();
			userLodgingDialog();
		}
		initView();
		viewEvent();
		startAnim();
		// ����������
		// onSureClicked();
	}

	/**
	 * �ؼ���ʼ��
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
	 * �ؼ��ĵ���¼�
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

	/** imagine_logo������Ĵ��� */
	private int stemp = 0;
	private ForDeveloperFragmentPickIp fdF;

	/**
	 * 2�����������5�ξʹ�IPѡ�񴰿�
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
		Log.d(TAG, "�������" + stemp);
		if (stemp > 4) {
			stemp = 0;
			Toast.makeText(LoginActivity.this, "��ѡ��Ҫ���Ե�IP��ַ",
					Toast.LENGTH_SHORT).show();
			// ����IP��ַѡ��
			fdF = new ForDeveloperFragmentPickIp();
			fdF.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
			fdF.show(getFragmentManager(), "ForDeveloperFragmentPickIp");
		}
	}

	/**
	 * ��������
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
	 * ����豸��֧��NFC���򵯳���¼�˻��������
	 */
	private void userLodgingDialog() {
		if (!canUseNFC) {
			// ������¼�˻��������
			/* ��bundle��������� */
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
	 * post�ύ��֤��¼��Ϣ
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
	 * ����Ƿ����°汾������
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
		// ������dialog
		downloadFragment = new DialogFragmentUpdateVersion();
		downloadFragment
				.setStyle(
						DialogFragment.STYLE_NO_TITLE,
						android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth);
		downloadFragment.show(getFragmentManager(),
				"DialogFragmentUpdateVersion");
	}

	/**
	 * ����apk
	 * 
	 * @param url
	 */
	private void downloadApk() {
		if (!beforeDownloadFile()) {
			handler.sendEmptyMessage(HandlerMessageCodes.HANDLER_CODE_UP_APP_DOWN_ERROR);
			return;
		}
		// �ļ����ز���
		// apkUrl = "http://down11.zol.com.cn/suyan/u17.2.0.2.apk";
		interceptFlag = true;
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}

	/**
	 * ��װapk
	 * 
	 * @param apkFilePath
	 *            ��װ·��
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
	/** Ŀ��Ӧ�����ص�ַ */
	private String apkUrl;
	/** �����߳� */
	private Thread downLoadThread;
	/** ��ֹ��� */
	private boolean interceptFlag = false;
	/** ��ʱ�����ļ�·�� */
	private String tmpFilePath = "";
	/** apk��������·�� */
	private String apkFilePath = "";
	private File ApkFile = null;

	private boolean beforeDownloadFile() {
		// �ж��Ƿ������SD��
		if (getSDPath() == null) {
			Toast.makeText(LoginActivity.this, "����SD��", Toast.LENGTH_SHORT)
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
		// �ж��ļ����Ƿ����
		File file = new File(savePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		apkFilePath = savePath + apkName;
		tmpFilePath = savePath + tmpApk;
		ApkFile = new File(apkFilePath);
		// �ж��ļ��Ƿ����
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
				// �����ʱ�����ļ�
				File tmpFile = new File(tmpFilePath);
				FileOutputStream fos = new FileOutputStream(tmpFile);
				Log.v("demo", "APK�ļ�������·��apkUrl:" + apkUrl);
				URL url = new URL(apkUrl);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();
				// ��ʾ�ļ���С��ʽ��2��С������ʾ
				DecimalFormat df = new DecimalFormat("0.00");
				// ������������ʾ�����ļ���С
				String apkFileSize = df.format((float) length / 1024 / 1024)
						+ "MB";
				int count = 0;
				byte buf[] = new byte[1024];

				do {
					int numread = is.read(buf);

					count += numread;
					// ������������ʾ�ĵ�ǰ�����ļ���С
					// ��ǰ����ֵ
					String tmpFileSize = df.format((float) count / 1024 / 1024)
							+ "MB/" + apkFileSize;
					// ���½���
					handler.sendMessage(handler
							.obtainMessage(
									HandlerMessageCodes.HANDLER_CODE_UP_APP_DOWN_UPDATE,
									count * 100 / length, 0, tmpFileSize));
					if (numread <= 0) {
						// ������� - ����ʱ�����ļ�ת��APK�ļ�
						if (ApkFile != null && tmpFile.renameTo(ApkFile)) {
							// ֪ͨ��װ
							handler.sendMessage(handler
									.obtainMessage(
											HandlerMessageCodes.HANDLER_CODE_UP_APP_DOWN_DOWN_OVER,
											apkFilePath));
						}
						break;
					}
					fos.write(buf, 0, numread);
				} while (interceptFlag);// ���ȡ����ֹͣ����
				Log.v("demo", "ֹͣ����");
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
		// ģ������
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
		// // ֪ͨ��װ
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
