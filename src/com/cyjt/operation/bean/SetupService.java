package com.cyjt.operation.bean;

public class SetupService {
	private String serviceIp = "0.0.0.0";
	private String port = "0";
	private int type = -1;
	private String equipmentId = "0";
	private int timing = -1;
	private String panId = "";
	private int frequency = -1;
	private int rate = -1;
	private int superNode = -1;

	public SetupService(String ip, String port, int type, String code, int src,
			String panid, int frequency_point, int percentage,int superNode) {
		this.serviceIp = ip;
		this.port = port;
		this.type = type;
		this.equipmentId = code;
		this.timing = src;
		this.panId = panid;
		this.frequency = frequency_point;
		this.rate = percentage;
		this.setSuperNode(superNode);
	}

	public String getServiceIp() {
		return serviceIp;
	}

	public void setServiceIp(String serviceIp) {
		this.serviceIp = serviceIp;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getEquipmentId() {
		return equipmentId;
	}

	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}

	public int getTiming() {
		return timing;
	}

	public void setTiming(int timing) {
		this.timing = timing;
	}

	public String getPanId() {
		return panId;
	}

	public void setPanId(String panId) {
		this.panId = panId;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public int getSuperNode() {
		return superNode;
	}

	public void setSuperNode(int superNode) {
		this.superNode = superNode;
	}
	
}
