package com.cyjt.operation.bean;

import java.io.Serializable;
import java.util.Date;

import com.cyjt.operation.base.Tools;

/**
 * 
 * @author wsw<BR>
 *         2015-5-9 下午1:27:40<BR>
 */
public class NewNodeHeartBeat implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id; // 标识号
	private String gatewayId; // 基站网关地址
	private String nodeId; // 编码
	private String retain; // 保留
	private int voltage; // 电压
	private int signal; // 信号量
	private int seq; // 心跳序列
	private int nodeSeq; // 节点心跳序列
	private int commandReq; // 命令请求
	private int commandCode; // 命令码
	private Date submitAt; // 上传时间
	private Date receiveAt; // 接收时间

	public NewNodeHeartBeat(Integer id, String gatewayId, String nodeId,
			String retain, int voltage, int signal, int seq, int nodeSeq,
			int commandReq, int commandCode, Date submitAt, Date receiveAt) {
		super();
		this.id = id;
		this.gatewayId = gatewayId;
		this.nodeId = nodeId;
		this.retain = retain;
		this.voltage = voltage;
		this.signal = signal;
		this.seq = seq;
		this.nodeSeq = nodeSeq;
		this.commandReq = commandReq;
		this.commandCode = commandCode;
		this.submitAt = submitAt;
		this.receiveAt = receiveAt;
	}

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

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getRetain() {
		return retain;
	}

	public void setRetain(String retain) {
		this.retain = retain;
	}

	public int getVoltage() {
		return voltage;
	}

	public void setVoltage(int voltage) {
		this.voltage = voltage;
	}

	public int getSignal() {
		return signal;
	}

	public void setSignal(int signal) {
		this.signal = signal;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public int getNodeSeq() {
		return nodeSeq;
	}

	public void setNodeSeq(int nodeSeq) {
		this.nodeSeq = nodeSeq;
	}

	public int getCommandReq() {
		return commandReq;
	}

	public void setCommandReq(int commandReq) {
		this.commandReq = commandReq;
	}

	public int getCommandCode() {
		return commandCode;
	}

	public void setCommandCode(int commandCode) {
		this.commandCode = commandCode;
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

}
