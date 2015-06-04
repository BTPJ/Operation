package com.cyjt.operation.base;

import java.io.File;

import com.google.gson.Gson;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;

public abstract class BaseActivity extends Activity {
	/** 文件保存路径 */
	public String FOLDER_PATH = "/headerits/construct";
	public Gson gson = Tools.getGson(Constants.ISO8601DateFormatShort);

	/**
	 * 获得SD卡的路径
	 * 
	 * @return
	 */
	public String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
			File file = new File(sdDir.getPath() + FOLDER_PATH);
			if (!file.exists())
				file.mkdirs();
			return file.getPath();
		}
		return null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
