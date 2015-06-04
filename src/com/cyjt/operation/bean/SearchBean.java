package com.cyjt.operation.bean;

import java.util.Date;

import com.cyjt.operation.base.Tools;

public class SearchBean {
	/** 数据库ID */
	private int _id;
	/** 对象编号 ,用于唯一标识 */
	private String code = "";
	/** 插入时间 */
	private long time;
	/** 数据 */
	private String gsonString;
	/**
	 * 数据类型 <BR>
	 * 0表示基站，1表示车位,2表示分割线,3表示阵列
	 */
	private int type;

	public SearchBean(String code,long time, int type, String gsonString) {
		super();
		this.time = time;
		this.type = type;
		this.code = code;
		this.gsonString = gsonString;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public long getTime() {
		return time;
	}

	public String getTimeByFormted() {
		return Tools.getDF(new Date(getTime())).toString();
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getGsonString() {
		return gsonString;
	}

	public void setGsonString(String gsonString) {
		this.gsonString = gsonString;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
