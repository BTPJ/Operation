package com.cyjt.operation.uitools;

import java.util.ArrayList;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cyjt.operation.R;
import com.cyjt.operation.adapter.CustomBaseAdapter;
import com.cyjt.operation.base.BaseActivityWithNFC;

public class ToolsNFCActivity extends BaseActivityWithNFC {
	private LayoutInflater inflater;
	private ListView listView;
	private CustomBaseAdapter<RelativeLayout> adapter;
	private ArrayList<RelativeLayout> views = new ArrayList<RelativeLayout>();
	private EditText editView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inflater = getLayoutInflater();
		setContentView(R.layout.activity_tools_nfc);
		initActionBar();
		initView();
		viewEvent();
	}

	private void initActionBar() {
		findViewById(R.id.actionbar_imageView_back_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						onBackPressed();

					}
				});
		((TextView) findViewById(R.id.actionbar_refresh_button)).setText("清除");
		findViewById(R.id.actionbar_refresh_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 清除listView中的数据
						clearListData();
					}
				});
		((TextView) findViewById(R.id.actionbar_textView_title))
				.setText("NFC生产工具");
		findViewById(R.id.actionbar_progressBar).setVisibility(View.GONE);
	}

	private void initView() {
		listView = (ListView) findViewById(R.id.listView_main_nfc_info);
		editView = (EditText) findViewById(R.id.editView_NFC_write_view);

	}

	private void viewEvent() {
		((TextView) findViewById(R.id.textView_write_botton))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String s = editView.getText().toString();
						if (s.length() == 16) {
							writeNFCTag(s);
						} else if (s.length() == 6) {
							s += "0000000000";
							writeNFCTag(s);
						} else {
							// 长度不合要求
							Toast.makeText(ToolsNFCActivity.this, "长度不合要求",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
		editView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO 每两个字符间添加一个空格，模拟16进制，但这段代码并不完美
				// if (count == 1) {
				// if ((s.length() + 1) % 3 == 0) {
				// editView.setText(s + " ");
				// editView.setSelection(s.length() + 1);
				// }
				// } else if (count == 0) {
				// if (s.length() > 0 && s.length() % 3 == 0) {
				// editView.setText(s.subSequence(0, s.length() - 1));
				// editView.setSelection(s.length() - 1);
				// }
				// }
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	private void initOrUpListData() {

		if (adapter == null) {
			adapter = new CustomBaseAdapter<RelativeLayout>(views);
			listView.setAdapter(adapter);
		} else {
			adapter.upData(views);
			adapter.notifyDataSetChanged();
		}
		if (views.size() > 0) {
			listView.setSelection(views.size() - 1);
		}
	}

	RelativeLayout rootView = null;

	private RelativeLayout getRelativeData(String message, int type) {
		switch (type) {
		case 0:
			// 表示是读出来的
			rootView = (RelativeLayout) inflater.inflate(
					R.layout.list_view_item_nfc_readed, null);
			((TextView) rootView.findViewById(R.id.textView_message))
					.setText("读取数据：\n\n" + message);

			break;
		case 1:
			// 表示是写入成功
			rootView = (RelativeLayout) inflater.inflate(
					R.layout.list_view_item_nfc_writed_succed, null);
			((TextView) rootView.findViewById(R.id.textView_message))
					.setText("写入成功:\n\n" + message);

			break;
		case 2:
			// 表示是写入失败
			rootView = (RelativeLayout) inflater.inflate(
					R.layout.list_view_item_nfc_writed_failed, null);
			((TextView) rootView.findViewById(R.id.textView_message))
					.setText("写入失败:\n\n" + message);

			break;
		default:
			break;
		}
		return rootView;
	}

	/**
	 * 将信息添加到界面上
	 * 
	 * @param message要展现的数据
	 * @param type
	 *            0表示要添加的数据是从NFC卡片上读出来的，1表示要添加的数据是要往NFC卡片上写入的,2表示写入失败
	 */
	private void addDataToList(String message, int type) {
		views.add(getRelativeData(message, type));
		initOrUpListData();
	}

	/**
	 * 清掉界面中的数据
	 */
	private void clearListData() {
		views.clear();
		initOrUpListData();
	}

	// =============================NFC==============================

	/** 读取到了标签中指定是数据 */
	protected void readedCardNeededData(String neededData) {
		addDataToList(neededData, 0);
	}

	/** 读取到了标签的Id标号 */
	protected void readedCardId(String nfcTagId) {

	}

	protected void writeTagFailed() {
		addDataToList(editView.getText().toString(), 2);
	}

	protected void writeTagSucceed() {
		addDataToList(editView.getText().toString(), 1);
		// 清掉输入框中的内容
		editView.setText("");
	}
}
