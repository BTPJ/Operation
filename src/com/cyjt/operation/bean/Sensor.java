package com.cyjt.operation.bean;

import java.io.Serializable;
import java.util.Date;

import com.cyjt.operation.base.Tools;

/**
 * 节点对象
 * 
 * @author kullo<BR>
 *         2014-10-30 上午9:10:54<BR>
 */
public class Sensor implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 节点的Id */
	private int id = -1;
	/** 所属基站Id */
	private int basestationId = -1;
	/** 所属阵列Id */
	private int laneArrayId = -1;
	/** 所属车位Id */
	private int parkingLotId = -1;
	/** 节点编号 */
	private String code = "";
	/** 节点编号（特指其二维码编码） */
	private String tdcCode = "";
	/** 节点编号（特指其NFC编码） */
	private String nfcCode = "";
	/** 节点顺序*/
	private int icq = -1;
	/** 创建时间*/
	private Date createAt;
	
private String crossing; //路口名称(方向-名称)例如：南北向-金银湖路
private int driveway; //车道

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

	public Sensor() {
		super();
	}

	public Sensor(int id, String code, String tdcCode, String nfcCode,
			int zValue,int icq, Date zValueTime,String crossing,int driveway) {
		super();
		this.id = id;
		this.code = code;
		this.tdcCode = tdcCode;
		this.nfcCode = nfcCode;
		this.icq = icq;
		this.crossing = crossing;
		this.driveway = driveway;
	}
	
	public Sensor(int id, String code, String tdcCode, String nfcCode,
			int zValue,int icq, Date zValueTime) {
		super();
		this.id = id;
		this.code = code;
		this.tdcCode = tdcCode;
		this.nfcCode = nfcCode;
		this.icq = icq;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.nfcCode = code;
		this.code = code;
	}

	public String getNfcCode() {
		return nfcCode;
	}

	public void setNfcCode(String nfcCode) {
		this.code = nfcCode;
		this.nfcCode = nfcCode;
	}

	public String getTdcCode() {
		return tdcCode;
	}

	public void setTdcCode(String tdcCode) {
		this.tdcCode = tdcCode;
	}

	public String getCreatAtString() {
		if (getCreateAt() == null) {
			return "";
		}
		return Tools.getDF(getCreateAt()).toString();
	}

	public int getBasestationId() {
		return basestationId;
	}

	public void setBasestationId(int basestationId) {
		this.basestationId = basestationId;
	}

	public int getLaneArrayId() {
		return laneArrayId;
	}

	public void setLaneArrayId(int laneArrayId) {
		this.laneArrayId = laneArrayId;
	}

	public int getParkingLotId() {
		return parkingLotId;
	}

	public void setParkingLotId(int parkingLotId) {
		this.parkingLotId = parkingLotId;
	}

	public int getIcq() {
		return icq;
	}

	public void setIcq(int icq) {
		this.icq = icq;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
}
