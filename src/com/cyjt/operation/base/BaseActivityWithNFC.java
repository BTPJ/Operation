package com.cyjt.operation.base;

import java.io.IOException;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public abstract class BaseActivityWithNFC extends BaseActivity {
	// ==================================NFC部分=======================
	private NfcAdapter mAdapter;
	private String[][] techList;
	private IntentFilter[] intentFilters;
	private PendingIntent pendingIntent;
	private Tag tag;
	public boolean canUseNFC = true;
	private final int HANDLER_MWSSAGE_FOR_NFC_WRITE_SUCCEED = 0;
	private final int HANDLER_MWSSAGE_FOR_NFC_WRITE_FAILED = 1;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_MWSSAGE_FOR_NFC_WRITE_SUCCEED:
				writeTagSucceed();
				break;
			case HANDLER_MWSSAGE_FOR_NFC_WRITE_FAILED:
				writeTagFailed();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initNFC();
	}

	/**
	 * 准备NFC
	 */
	private void initNFC() {
		// 获取默认的NFC控制器
		mAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mAdapter == null) {
			Toast.makeText(BaseActivityWithNFC.this, "设备不支持NFC！",
					Toast.LENGTH_SHORT).show();
			canUseNFC = false;
			return;
		}
		if (!mAdapter.isEnabled()) {
			Toast.makeText(BaseActivityWithNFC.this, "请在系统设置中先启用NFC功能！",
					Toast.LENGTH_SHORT).show();
			startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
		}
		techList = new String[][] { new String[] { NfcV.class.getName() },
				new String[] { NfcA.class.getName() },
				new String[] { NfcB.class.getName() },
				new String[] { NfcF.class.getName() } };
		intentFilters = new IntentFilter[] {
				new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
				new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
				new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED) };
		// 创建一个 PendingIntent 对象, 这样Android系统就能在一个tag被检测到时定位到这个对象
		pendingIntent = PendingIntent.getActivity(BaseActivityWithNFC.this, 0,
				new Intent(BaseActivityWithNFC.this, getClass())
						.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
	}

	/**
	 * 读到技术类型为TAG的卡片
	 * 
	 * @param tag
	 */
	private void readedTAGCard(Tag tag) {
		Toast.makeText(BaseActivityWithNFC.this, "暂不支持该类型", Toast.LENGTH_SHORT)
				.show();
	}

	/**
	 * 读到技术类型为NDEF的卡片
	 * 
	 * @param tag
	 */
	private void readedNDEFCard(Tag tag) {
		Toast.makeText(BaseActivityWithNFC.this, "暂不支持该类型", Toast.LENGTH_SHORT)
				.show();
	}

	/**
	 * 读到技术类型为TECH的卡片
	 * 
	 * @param tag
	 */
	private void readedTECHCard(Tag tag) {
		final MifareUltralight mifareU = MifareUltralight.get(tag);
		if (mifareU == null){
			readedCardNull();
			return;
		}
		switch (mifareU.getType()) {
		case MifareUltralight.TYPE_ULTRALIGHT:
			Log.v("demo", "类型：TYPE_ULTRALIGHT");
			break;
		case MifareUltralight.TYPE_ULTRALIGHT_C:
			Log.v("demo", "类型：TYPE_ULTRALIGHT_C");
			readerUltralightC(mifareU);
			break;
		case MifareUltralight.TYPE_UNKNOWN:
			Log.v("demo", "类型：TYPE_UNKNOWN");
			break;
		default:
			break;
		}

	}

	/** 解析TYPE_ULTRALIGHT_C类型的卡片 */
	private void readerUltralightC(MifareUltralight mifareU) {
		String nfcTagId = "";
		String neededData = "";
		try {
			mifareU.connect();
			// 读取卡的Id
			nfcTagId = bytesToHexString(mifareU.getTag().getId());
			// 取指定的从第16块区块开始的连续4个区块数据
			neededData = bytesToHexString(mifareU.readPages(16))
					.substring(0, 6);// 仅保留数据前6位
			mifareU.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		readedCardId(nfcTagId);
		readedCardNeededData(neededData);
	}
	/**读到空卡片*/
	protected void readedCardNull() {

	}
	/** 读取到了标签中指定是数据 */
	protected void readedCardNeededData(String neededData) {

	}

	/** 读取到了标签的Id标号 */
	protected void readedCardId(String nfcTagId) {

	}

	protected void writeTagFailed() {

	}

	protected void writeTagSucceed() {

	}

	private byte[] bs;

	/***
	 * 往指定标签的默认区块中中写入指定数据
	 * 
	 * @param data
	 */
	public void writeNFCTag(String data) {
		if (tag == null) {
			Toast.makeText(BaseActivityWithNFC.this, "请重新靠近要写的标签",
					Toast.LENGTH_SHORT).show();
			return;
		}
		bs = HexString2Bytes(data);
		final MifareUltralight mifareU = MifareUltralight.get(tag);
		new Thread() {
			@Override
			public void run() {
				super.run();
				try {
					mifareU.connect();
					// mifareU.writePage(16, HexString2Bytes("11111111"));
					// mifareU.writePage(17, HexString2Bytes("23245252"));
					// mifareU.writePage(18, HexString2Bytes("036acccc"));
					// mifareU.writePage(19, HexString2Bytes("aaaaaaaf"));
					mifareU.writePage(16, new byte[] { bs[0], bs[1], bs[2],
							bs[3] });
					mifareU.writePage(17, new byte[] { bs[4], bs[5], bs[6],
							bs[7] });
					mifareU.writePage(18, HexString2Bytes("00000000"));
					mifareU.writePage(19, HexString2Bytes("00000000"));
					mifareU.close();
					 handler.sendEmptyMessage(HANDLER_MWSSAGE_FOR_NFC_WRITE_SUCCEED);
				} catch (IOException e1) {
					e1.printStackTrace();
					Log.v("demo", "写入出错!");
					 handler.sendEmptyMessage(HANDLER_MWSSAGE_FOR_NFC_WRITE_FAILED);
				}
			}
		}.start();
		return;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mAdapter == null) {
			// Toast.makeText(BaseActivityWithNFC.this, "设备不支持NFC！",
			// Toast.LENGTH_SHORT).show();
			return;
		}
		mAdapter.enableForegroundDispatch(BaseActivityWithNFC.this,
				pendingIntent, intentFilters, techList);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if(intent.getAction()==null){
			return;
		}
		if (intent.getAction().equals(
				"com.palmelf.ipms.construct.intent.action.exitApp")) {
			finish();
		}
		if (intent.getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED)) {
			tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			if (tag != null) {
				readedTECHCard(tag);
			}
			return;
		}
		if (intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
			tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			if (tag != null) {
				readedNDEFCard(tag);
			}
			return;
		}
		if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
			tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			if (tag != null) {
				readedTAGCard(tag);
			}
			return;
		}
	}

	// =========================以下为一些字符转换工具============================
	/**
	 * 字符序列转换为16进制字符串
	 * 
	 * @param src
	 * @return
	 */
	public String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		char[] buffer = new char[2];
		for (int i = 0; i < src.length; i++) {
			buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
			buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
			stringBuilder.append(buffer);
		}
		return stringBuilder.toString();
	}

	/**
	 * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF,
	 * 0xD9}
	 * 
	 * @param src
	 *            String
	 * @return byte[]
	 */
	public static byte[] HexString2Bytes(String src) {
		byte[] ret = new byte[src.length() / 2];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < src.length() / 2; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	/**
	 * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF
	 * 
	 * @param src0
	 *            byte
	 * @param src1
	 *            byte
	 * @return byte
	 */
	public static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
				.byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
				.byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}
}
