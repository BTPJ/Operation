package com.cyjt.operation.bean;

import java.io.Serializable;

/**
 * @author kullo<BR>
 * 2015-4-30 上午11:16:17<BR>
 */
public class RoadsAreas implements Serializable {

	
	private static final long serialVersionUID = 1L;
	/**
	 * 数据标识号
	 */
	private Integer id; 
	/**
	 * 标题
	 */
	private String title; 
	/**
	 * 类型
	 */
	private Integer type; 
	/**
	 * 子编码
	 */
	private String code;
	/**
	 * 父编码
	 */
	private String parentCode;
	/**
	 * 经度
	 */
	private Double lon;
	/**
	 * 纬度
	 */
	private Double lat;

	public RoadsAreas(Integer id, String title, Integer type, String code,
			String parentCode, Double lon, Double lat) {
		super();
		this.id = id;
		this.title = title;
		this.type = type;
		this.code = code;
		this.parentCode = parentCode;
		this.lon = lon;
		this.lat = lat;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

}
