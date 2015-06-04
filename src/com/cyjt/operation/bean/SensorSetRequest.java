package com.cyjt.operation.bean;

import java.util.Date;

import com.cyjt.operation.base.Tools;

/**
 * 节点配置请求
 * 
 * @author kullo<BR>
 *         2015-1-16 下午5:42:01<BR>
 */
public class SensorSetRequest {
	private String gatewayId;
	private String nodeId;
	private String func;
	private String content;
	private Date submitAt;
	private Date receiveAt;

	public String getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getFunc() {
		return func;
	}

	public void setFunc(String func) {
		this.func = func;
	}

	public Date getSubmitAt() {
		return submitAt;
	}

	public String getSubmitAtStringFromNow() {
		if (getSubmitAt() != null) {
			String s = Tools.diffTime(getSubmitAt(), new Date());
			if (s.equals("")) {
				return "刚刚";
			}
			return Tools.diffTime(getSubmitAt(), new Date()) + "前";
		}
		return "无提交时间";
	}

	public String getSubmitAtString() {
		if (getSubmitAt() == null) {
			return Tools.getDF(new Date()).toString();
		}
		return Tools.getDF(getSubmitAt()).toString();
	}

	public void setSubmitAt(Date submitAt) {
		this.submitAt = submitAt;
	}

	public Date getReceiveAt() {
		return receiveAt;
	}

	public void setReceiveAt(Date receiveAt) {
		this.receiveAt = receiveAt;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
