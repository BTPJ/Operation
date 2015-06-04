package com.cyjt.operation.bean;

import java.io.Serializable;
import java.util.Date;

import com.cyjt.operation.base.Tools;

/**
 * 本地基站对象
 * 
 * @author kullo<BR>
 *         2014-9-11 下午3:13:49<BR>
 */
public class BaseStation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 数据库编号 */
	private long id = -1;
	/** 编号 */
	private String code = "";
	/** NFC编号 */
	private String nfcCode = "";
	/** 所处区域编号 */
	private long areaId = -1;
	/** 基站状态 */
	private int status = -1;
	/** 基站所处地理坐标 */
	private double lon = -0.0;
	private double lat = -0.0;
	/** 基站描述 */
	private String description = "";
	/** 基站型号 */
	private String model = "";
	/** 创建时间 */
	private Date createAt;
	/** 剩余流量的更新时间 */
	private Date remainingAt;
	/** 状态变化时间 */
	private Date lastDate;
	/**基站类型，1表示静态部署基站，2表示动态部署基站*/
	private int type = -1;

	/**
	 * 
	 * @param id
	 *            数据库编号
	 * @param code
	 *            编号
	 * @param nfcCode
	 *            NFC编号
	 * @param areaId
	 *            所处区域编号
	 * @param status
	 *            基站状态
	 * @param lon
	 *            基站所处地理坐标
	 * @param lat
	 *            基站所处地理坐标
	 * @param description
	 *            基站描述
	 * @param model
	 *            基站描述==型号
	 * @param createAt
	 *            创建时间
	 */
	public BaseStation(long id, String code, String nfcCode, long areaId,
			int status, double lon, double lat, String description,
			String model, Date createAt) {
		super();
		this.id = id;
		this.code = code;
		this.nfcCode = nfcCode;
		this.areaId = areaId;
		this.status = status;
		this.lon = lon;
		this.lat = lat;
		this.description = description;
		this.createAt = createAt;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public BaseStation() {
		super();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getCreateTime() {
		return getCreateAt();
	}

	public String getCreateTimeString() {
		return getCreateAtString();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNfcCode() {
		return nfcCode;
	}

	public void setNfcCode(String nfcCode) {
		this.nfcCode = nfcCode;
	}

	public long getAreaId() {
		return areaId;
	}

	public void setAreaId(long areaId) {
		this.areaId = areaId;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public String getCreateAtString() {
		if (getCreateAt() == null) {
			return Tools.getDF(new Date()).toString();
		}
		return Tools.getDF(getCreateAt()).toString();
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Date getRemainingAt() {
		return remainingAt;
	}

	public String getRemainingAtString() {
		if (getRemainingAt() == null) {
			return "暂无时间";
		}
		return Tools.getDF(getRemainingAt()).toString();
	}

	public void setRemainingAt(Date remainingAt) {
		this.remainingAt = remainingAt;
	}

	public String getLastAtString() {
		if (getLastDate() == null) {
			return "暂无时间";
		}
		return Tools.getDF(getLastDate()).toString();
	}

	public Date getLastDate() {
		return lastDate;
	}

	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
