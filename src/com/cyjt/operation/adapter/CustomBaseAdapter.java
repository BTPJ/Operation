package com.cyjt.operation.adapter;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * �Զ����ListViewʹ�õ�adapter
 * 
 * @author LTP 2014-7-24 ����5:03:37<BR>
 * @param <T>
 */
public class CustomBaseAdapter<T> extends BaseAdapter {
	/** pagerAdapter ����õ� */
	private List<T> mListViews;
	/** ���ݽ����ڲ��� */
	private int viewCount = 0;

	public CustomBaseAdapter(List<T> allViews) {
		mListViews = allViews;
		viewCount = mListViews.size();
	}

	@Override
	public int getCount() {
		return viewCount;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return (View) mListViews.get(position);
	}

	public void upData(List<T> views) {
		this.mListViews = views;
		viewCount = this.mListViews.size();
	}
}
