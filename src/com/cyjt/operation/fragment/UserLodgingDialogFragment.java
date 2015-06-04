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
import com.cyjt.operation.base.AppContext;
import com.cyjt.operation.base.SharedPreferencesTools;
import com.cyjt.operation.bean.User;

public class UserLodgingDialogFragment extends DialogFragment {

	private OnUserLodgingListener listener;
	private TextView textView_user_lodging_button;
	private EditText editText_user_nfc_code;
	private User user;
	private String code;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_user_loging,
				container, false);
		textView_user_lodging_button = (TextView) rootView
				.findViewById(R.id.textView_user_lodging_button);
		editText_user_nfc_code = (EditText) rootView
				.findViewById(R.id.editText_user_nfc_code);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		user = new User();
		code = AppContext
				.getInstance()
				.getSharedPreferencesTools()
				.readStringPreferences(
						SharedPreferencesTools.LOGIN_SUCCEED_USER,
						"LOGIN_SUCCEED_USER");
		viewEvent();
		editText_user_nfc_code.setText(code);
	}

	private void viewEvent() {
		textView_user_lodging_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.OnUserLodging(user);
				dismiss();
			}
		});
		editText_user_nfc_code.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if ((s + "").length() < 14) {
					textView_user_lodging_button.setClickable(false);
					textView_user_lodging_button
							.setBackgroundResource(R.drawable.fillet_shape_button_with_alph_orange_pressed_1);
				} else if ((s + "").length() == 14) {
					textView_user_lodging_button.setClickable(true);
					textView_user_lodging_button
							.setBackgroundResource(R.drawable.fillet_shape_button_with_alph_orange_1);
				}
				user.setCode(s + "");
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

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		listener = (OnUserLodgingListener) activity;
	}

	public interface OnUserLodgingListener {
		public void OnUserLodging(User user);
	}
}
