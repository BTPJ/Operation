package com.cyjt.operation.bean;

import java.io.Serializable;

public class Area implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 区域名 */
	private String title = "";
	/** 区域描述 */
	private String describe = "";
	private String code = "";
	/** 区域的数据库Id */
	private long id = -1;

	public Area(String code, String title, String describe) {
		super();
		this.title = title;
		this.describe = describe;
	}

	public Area() {
		super();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	@Override
	public String toString() {
		return "FirstBean ["+ ", title=" + title
				+ ", describe=" + describe + "]";
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
