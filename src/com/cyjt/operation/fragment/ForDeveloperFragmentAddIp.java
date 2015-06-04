package com.cyjt.operation.fragment;


import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.cyjt.operation.R;
import com.cyjt.operation.base.IpAddress;

public class ForDeveloperFragmentAddIp extends DialogFragment {
private EditText editText1;
private TextView textView1;
private TextView textView2;
private String ipString="";
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_for_developer_add_ip,
				container, false);
		textView1 = (TextView) rootView.findViewById(R.id.textView1);
		textView2 = (TextView) rootView.findViewById(R.id.textView2);
		editText1 = (EditText) rootView.findViewById(R.id.editText1);
		
		textView2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		textView1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!ipString.equals("")){
					//调用接口
					IpAddress ip = new IpAddress();
					ip.setIp(""+ipString);
					listener.OnAddSucceed(ip);
					dismiss();
				}else{
					
				}
			}
		});
		editText1.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				ipString = editText1.getText().toString();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		return rootView;
	}
	private OnAddIpAddressListener listener;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		listener = (OnAddIpAddressListener) activity;// 这句就是赋初值了。
	}

	// Container Activity must implement this interface
	public interface OnAddIpAddressListener {
		public void OnAddSucceed(IpAddress ip);
	}
}
