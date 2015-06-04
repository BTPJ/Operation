package com.cyjt.operation.bean;

import java.util.Date;

import com.cyjt.operation.base.Tools;

public class BaseStationHeartBeat {
	private Integer id; // 标识号
	private String gatewayId; // 基站网关 - basestationCode
	private Date receiveAt; // 上传时间

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}

	public Date getReceiveAt() {
		return receiveAt;
	}

	public String getReceiveAtString() {
		if (getReceiveAt() != null) {
			return Tools.getDF(getReceiveAt()).toString();
		} else {
			return Tools.getDF(new Date()).toString();
		}
	}

	public void setReceiveAt(Date receiveAt) {
		this.receiveAt = receiveAt;
	}

	public String getReceiveAtFromNow() {
		if (getReceiveAt() != null) {
			String s = Tools.diffTime(getReceiveAt(), new Date());
			if (s.equals("")) {
				return "刚刚";
			}
			return Tools.diffTime(getReceiveAt(), new Date()) + "前";
		}
		return "无提交时间";
	}
}
