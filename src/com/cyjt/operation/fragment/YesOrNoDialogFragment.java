package com.cyjt.operation.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cyjt.operation.R;

/**
 * 确认是否退出对话框
 * 
 * @author LTP
 *
 */
public class YesOrNoDialogFragment extends DialogFragment {
	private String[] stringarray;
	private TextView title;
	private TextView enter_button;
	private TextView cancle_button;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_yes_or_no,
				container, false);
		title = (TextView) rootView
				.findViewById(R.id.textView_supportBasestationCode);
		cancle_button = (TextView) rootView
				.findViewById(R.id.textView_cancle_button);
		enter_button = (TextView) rootView
				.findViewById(R.id.textView_enter_button);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		stringarray = getArguments().getStringArray("stringArray");
		viewEvent();
	}

	private void viewEvent() {
		title.setText("" + stringarray[0]);
		cancle_button.setText("" + stringarray[1]);
		enter_button.setText("" + stringarray[2]);
		cancle_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		enter_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				listener.onSureClicked();
			}
		});
	}

	private YesOrNoFragmentActionListener listener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (YesOrNoFragmentActionListener) activity;
	}

	public interface YesOrNoFragmentActionListener {
		/** 确定按钮被点击 */
		public void onSureClicked();
	}
}
