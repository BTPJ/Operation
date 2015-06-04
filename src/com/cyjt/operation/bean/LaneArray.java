package com.cyjt.operation.bean;

import java.io.Serializable;
import java.util.Date;

import com.cyjt.operation.base.Tools;

public class LaneArray implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 数据库ID */
	private int id = -1;
	/** 编号 */
	private String code = "";
	/** 创建时间 */
	private Date createAt;
	/** 描述 */
	private String description = "";
	/** 纬度 */
	private double lon = -0.0;
	/** 经度 */
	private double lat = -0.0;
	/** 用于描述阵列在项目中的标的点 */
	private String location = "";

	/**
	 * 
	 * @param id
	 *            数据库id
	 * @param code
	 *            编码（二维码）
	 * @param creatAt
	 *            创建时间
	 * @param describe
	 *            描述
	 */
	public LaneArray(int id, String code, Date creatAt, String describe) {
		super();
		this.id = id;
		this.code = code;
		this.createAt = creatAt;
		this.description = describe;
	}

	public LaneArray() {
		super();
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
		this.code = code;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public String getCreatAtString() {
		if (getCreateAt() == null) {
			return Tools.getDF(new Date()).toString();
		}
		return Tools.getDF(getCreateAt()).toString();
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public String getLocation() {
		return location == null ? "" : location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
