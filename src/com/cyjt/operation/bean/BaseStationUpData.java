package com.cyjt.operation.bean;

import java.util.Date;

import com.cyjt.operation.base.Tools;

public class BaseStationUpData {
private String gatewayId="";
private String func="";
private Date receiveAt;
private String content="";
private int seq=0;
public String getGatewayId() {
	return gatewayId;
}
public void setGatewayId(String gatewayId) {
	this.gatewayId = gatewayId;
}
public String getFunc() {
	return func;
}
public void setFunc(String func) {
	this.func = func;
}
public Date getReceiveAt() {
	return receiveAt;
}
public void setReceiveAt(Date receiveAt) {
	this.receiveAt = receiveAt;
}
public String getReceiveAtString() {
	if (getReceiveAt() == null) {
		return Tools.getDF(new Date()).toString();
	}
	return Tools.getDF(getReceiveAt()).toString();
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

public String getContent() {
	return content;
}
public void setContent(String content) {
	this.content = content;
}
public int getSeq() {
	return seq;
}
public void setSeq(int seq) {
	this.seq = seq;
}

}
