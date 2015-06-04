package com.cyjt.operation.bean;

import java.util.Date;

import com.cyjt.operation.base.Constants;
import com.cyjt.operation.base.Tools;

public class LaneArrayStatus {
	private int status;
	private Date submitAt;

	public int getStatus() {
		return status;
	}

	public String getStatusString() {
		String status = "";
		switch (getStatus()) {
		case Constants.LANEARRAY_STATUS_GREEN:
			status = "畅通";
			break;
		case Constants.LANEARRAY_STATUS_YELLOW:
			status = "缓行";
			break;
		case Constants.LANEARRAY_STATUS_RED:
			status = "阻塞";
			break;
		default:
			break;
		}
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getSubmitAt() {
		return submitAt;
	}

	public void setSubmitAt(Date submitAt) {
		this.submitAt = submitAt;
	}

	public String getSubmitAtString() {
		if (getSubmitAt() == null) {
			return Tools.getDF(new Date()).toString();
		}
		return Tools.getDF(getSubmitAt()).toString();
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
}
