package com.cyjt.operation.bean;

import java.util.Date;

import com.cyjt.operation.base.Tools;

public class ZValue {
	/** 地磁值 */
	private int emValueZ = -1;
	/** 电压 */
	private int voltage = 0;
	/** 信号量 */
	private String signal = "";
	/** 地磁值对应的上报时间 */
	private Date submitAt;

	public String getzValueTimeString() {
		if (getSubmitAt() == null) {
			return Tools.getDF(new Date()).toString();
		}
		return Tools.getDF(getSubmitAt()).toString();
	}

	public String getzValueTimeFromNow() {
		if (getSubmitAt() != null) {
			String s = Tools.diffTime(getSubmitAt(), new Date());
			if (s.equals("")) {
				return "刚刚";
			}
			return Tools.diffTime(getSubmitAt(), new Date()) + "前";
		}
		return "无提交时间";
	}

	public int getEmValueZ() {
		return emValueZ;
	}

	public void setEmValueZ(int emValueZ) {
		this.emValueZ = emValueZ;
	}

	public Date getSubmitAt() {
		return submitAt;
	}

	public void setSubmitAt(Date submitAt) {
		this.submitAt = submitAt;
	}

	public int getVoltage() {
		return voltage;
	}

	public String getVoltagePercentage() {
		if (getVoltage() < 3000) {
			return "0%";
		}
		return (getVoltage() - 3000) * 100 / 670 + "%";
	}

	public void setVoltage(int voltage) {
		this.voltage = voltage;
	}

	public String getSignal() {
		return signal;
	}

	public void setSignal(String signal) {
		this.signal = signal;
	}

}
