package com.cyjt.operation.bean;

public class SensorSyncService {
	// 节点Id
	private String nodeId = "";
	// 服务器下发X基准值
	private int benchmarkX = 0;
	// 强制状态转换
	private int forceTrans = 0;
	// 节点复位命令
	private int nodeReset = 0;
	// 节点心跳时间命令
	private int nodeHeartBeatCommand = 0;
	// 服务器下发Y基准值
	private int benchmarkY = 0;
	// 动态交通所属相位
	private int phase = 0;
	// 动态交通相位级数
	private int level = 0;
	// 预留值
	private int reserved = 0;

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public int getBenchmarkX() {
		return benchmarkX;
	}

	public void setBenchmarkX(int benchmarkX) {
		this.benchmarkX = benchmarkX;
	}

	public int getForceTrans() {
		return forceTrans;
	}

	public void setForceTrans(int forceTrans) {
		this.forceTrans = forceTrans;
	}

	public int getNodeReset() {
		return nodeReset;
	}

	public void setNodeReset(int nodeReset) {
		this.nodeReset = nodeReset;
	}

	public int getNodeHeartBeatCommand() {
		return nodeHeartBeatCommand;
	}

	public void setNodeHeartBeatCommand(int nodeHeartBeatCommand) {
		this.nodeHeartBeatCommand = nodeHeartBeatCommand;
	}

	public int getBenchmarkY() {
		return benchmarkY;
	}

	public void setBenchmarkY(int benchmarkY) {
		this.benchmarkY = benchmarkY;
	}

	public int getPhase() {
		return phase;
	}

	public void setPhase(int phase) {
		this.phase = phase;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getReserved() {
		return reserved;
	}

	public void setReserved(int reserved) {
		this.reserved = reserved;
	}

}
