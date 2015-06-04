package com.cyjt.operation.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cyjt.operation.R;

/**
 * 版本更新对话框
 * 
 * @author kullo<BR>
 *         2014-7-21 下午3:06:43<BR>
 */
public class DialogFragmentUpdateVersion extends DialogFragment {
	private TextView textview_download_message;
	private TextView textview_download_percent;
	private ProgressBar progressBar_download;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_download, container,
				false);
		textview_download_message = (TextView) rootView
				.findViewById(R.id.textview_download_message);
		textview_download_percent = (TextView) rootView
				.findViewById(R.id.textview_download_percent);
		progressBar_download = (ProgressBar) rootView
				.findViewById(R.id.progressBar_download);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setDialogCancelable(false);
		viewEvent();
		listener.onDialogFragmentCreated();
	}

	private void viewEvent() {

	}

	public void setDialogCancelable(boolean b) {
		setCancelable(b);
	}

	public void dialogDismiss() {
		setDialogCancelable(true);
		dismiss();
	}

	public void refeashProgress(int progress, String message) {
		textview_download_message.setText("" + message);
		textview_download_percent.setText("" + progress + "%");
		progressBar_download.setProgress(progress);
	}

	private DialogFragmentListener listener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (DialogFragmentListener) activity;
	}

	public interface DialogFragmentListener {
		/** 当Dialog被创建 */
		public void onDialogFragmentCreated();
	}
}
