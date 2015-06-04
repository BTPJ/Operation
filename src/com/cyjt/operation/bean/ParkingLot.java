package com.cyjt.operation.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import android.hardware.Sensor;

import com.cyjt.operation.base.Tools;

/**
 * 本地的ParkingLot车位对象 <BR>
 * getParkingLotForService()方法将返回提交给服务器的车位对象（用于提交、修改车位）
 * 
 * @author kullo<BR>
 *         2014-9-11 下午3:46:40<BR>
 */
public class ParkingLot implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 车位的数据库Id */
	private long id = -1;
	/** 车位编码（车位二维码） */
	private String code = "";
	/** 车位NFC编码 */
	private String nfcCode = "";
	/** 车位二维码编码 */
	private String tdcCode = "";
	/** 车位描述字段 */
	private String describe = "";
	/** 是否已激活 */
	private boolean hasActivited = true;
	/** 车位所属的传感器集合 */
	private ArrayList<Sensor> sensors;
	private String sensorCode = "";
	private String sensorCode1 = "";
	/** 车位的创建时间 */
	private Date createTime;
	/** 车位状态，有无车 */
	private int parkingLotStatus = -1;
	/** 提交时间 */
	private Date lastTime;
	/** 车位所属基站NFCCode */
	private String basestationNfcCode = "";
	private Date submitAt = null;
	private int status;
	private int spaceStatus;// 节点状态(1,无节点 2,有节点 心跳有问题 3,心跳正常)

	public int getSpaceStatus() {
		return spaceStatus;
	}

	public void setSpaceStatus(int spaceStatus) {
		this.spaceStatus = spaceStatus;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * 车位对象的构造函数
	 * 
	 * @param id
	 *            车位的数据库Id
	 * @param code
	 *            车位编码
	 * @param nfcCode
	 *            车位编码（NFC编码）
	 * @param tdcCode
	 *            车位编码（车位二维码）（车位铭牌二维码）
	 * @param describe
	 *            车位描述字段
	 * @param hasActivited
	 *            是否已激活
	 * @param sensors
	 *            车位所属的传感器集合
	 * @param createTime
	 *            车位的创建时间
	 * @param parkingLotStatus
	 *            车位状态，有无车
	 * @param lastTime
	 *            提交时间
	 */
	public ParkingLot(long id, String code, String nfcCode, String tdcCode,
			String describe, boolean hasActivited, ArrayList<Sensor> sensors,
			Date createTime, int parkingLotStatus, Date lastTime) {
		super();
		this.id = id;
		this.code = code;
		this.nfcCode = nfcCode;
		this.tdcCode = tdcCode;
		this.describe = describe;
		this.hasActivited = hasActivited;
		this.sensors = sensors;
		this.createTime = createTime;
		this.parkingLotStatus = parkingLotStatus;
		this.lastTime = lastTime;
	}

	public ParkingLot(long id, String code, String nfcCode, String tdcCode,
			String describe, boolean hasActivited, String sensorCode,
			String sensor1Code, Date createTime, int parkingLotStatus,
			Date lastTime, int status, int spaceStatus) {
		super();
		this.id = id;
		this.code = code;
		this.nfcCode = nfcCode;
		this.tdcCode = tdcCode;
		this.describe = describe;
		this.hasActivited = hasActivited;
		this.sensorCode = sensorCode;
		this.sensorCode1 = sensor1Code;
		this.createTime = createTime;
		this.parkingLotStatus = parkingLotStatus;
		this.lastTime = lastTime;
		this.status = status;
		this.spaceStatus = spaceStatus;
	}

	public ParkingLot() {

	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public boolean isHasActivited() {
		return hasActivited;
	}

	public void setHasActivited(boolean hasActivited) {
		this.hasActivited = hasActivited;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getLastTime() {
		return lastTime;
	}

	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}

	public int getParkingLotStatus() {
		return parkingLotStatus;
	}

	public void setParkingLotStatus(int parkingLotStatus) {
		this.parkingLotStatus = parkingLotStatus;
	}

	public String getNfcCode() {
		return nfcCode;
	}

	public void setNfcCode(String nfcCode) {
		this.nfcCode = nfcCode;
	}

	public ArrayList<Sensor> getSensors() {
		return sensors;
	}

	public void setSensors(ArrayList<Sensor> sensors) {
		this.sensors = sensors;
	}

	public String getTdcCode() {
		return tdcCode;
	}

	public void setTdcCode(String tdcCode) {
		this.tdcCode = tdcCode;
	}

	public ArrayList<String> getCodes() {
		ArrayList<String> codes = new ArrayList<String>();
		codes.add(getSensorCode());
		codes.add(getSensorCode1());
		return codes;
	}

	public String getSensorCode() {
		return sensorCode;
	}

	public void setSensorCode(String sensorCode) {
		this.sensorCode = sensorCode;
	}

	public String getSensorCode1() {
		return sensorCode1;
	}

	public void setSensorCode1(String sensorCode1) {
		this.sensorCode1 = sensorCode1;
	}

	public String getBasestationNfcCode() {
		return basestationNfcCode;
	}

	public void setBasestationNfcCode(String basestationNfcCode) {
		this.basestationNfcCode = basestationNfcCode;
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
		} else {
			return Tools.getDF(getSubmitAt()).toString();

		}
	}
}
