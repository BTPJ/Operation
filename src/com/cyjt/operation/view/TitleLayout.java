package com.cyjt.operation.view;

import com.cyjt.operation.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 自定义标题栏控件
 * 
 * @author LTP
 *
 */
public class TitleLayout extends RelativeLayout {
	private ImageView imageView_back;
	private TextView textView_title;

	public TitleLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.action_bar_title, this);
		imageView_back = (ImageView) findViewById(R.id.imageView_back);
		textView_title = (TextView) findViewById(R.id.textView_title);
		imageView_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				((Activity) getContext()).onBackPressed();
			}
		});
		if (!isInEditMode()) {
			Intent intent = ((Activity) getContext()).getIntent();
			String title = intent.getStringExtra("title");
			textView_title.setText(title);
		}
	}

}
