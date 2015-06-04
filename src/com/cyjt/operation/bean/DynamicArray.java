package com.cyjt.operation.bean;

import java.io.Serializable;
import java.util.Date;

import com.cyjt.operation.base.Tools;

/**
 * 阵列信息 Created by kullo on 2014-11-24.
 */
public class DynamicArray implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id = -1;
	private String arrayCode = "";
	private String nodePurpose = "";
	private int series = -1;
	private int status = -1;
	private int emValue = -1;
	private Date submitAt;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getSubmitAt() {
		return submitAt;
	}

	public String getSubmitAtString() {
		if (getSubmitAt() != null) {
			return Tools.getDF(getSubmitAt()).toString();
		}
		return "";
	}

	public String getSubmitAtFromNow() {
		if (getSubmitAt() != null) {
			String s = Tools.diffTime(getSubmitAt(), new Date());
			if (s.equals("")) {
				return "刚刚";
			}
			return Tools.diffTime(getSubmitAt(), new Date()) + "前";
		}
		return "无提交时间";
	}

	public void setSubmitAt(Date submitAt) {
		this.submitAt = submitAt;
	}

	public String getArrayCode() {
		return arrayCode;
	}

	public void setArrayCode(String arrayCode) {
		this.arrayCode = arrayCode;
	}

	public String getNodePurpose() {
		return nodePurpose;
	}

	public void setNodePurpose(String nodePurpose) {
		this.nodePurpose = nodePurpose;
	}

	public int getSeries() {
		return series;
	}

	public void setSeries(int series) {
		this.series = series;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getEmValue() {
		return emValue;
	}

	public void setEmValue(int emValue) {
		this.emValue = emValue;
	}
}
