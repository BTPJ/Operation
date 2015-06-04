package com.cyjt.operation.bean;

public class SensorData {

	private String nfcCode;
	private int seq;
	private String crossing;
	private int driveway;
	
	
	public SensorData(String nfcCode, int seq, String crossing, int driveway) {
		super();
		this.nfcCode = nfcCode;
		this.seq = seq;
		this.crossing = crossing;
		this.driveway = driveway;
	}
	public String getNfcCode() {
		return nfcCode;
	}
	public void setNfcCode(String nfcCode) {
		this.nfcCode = nfcCode;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getCrossing() {
		return crossing;
	}
	public void setCrossing(String crossing) {
		this.crossing = crossing;
	}
	public int getDriveway() {
		return driveway;
	}
	public void setDriveway(int driveway) {
		this.driveway = driveway;
	}
	
	
}
