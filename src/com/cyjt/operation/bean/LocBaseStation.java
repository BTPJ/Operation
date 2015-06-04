package com.cyjt.operation.bean;

import java.io.Serializable;

/**
 * 本地基站对象
 * 
 * @author kullo<BR>
 *         2014-9-11 下午3:13:49<BR>
 */
public class LocBaseStation  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 数据库编号 */
	private long baseStationId = -1;
	/** 编号 */
	private String code = "";
	/** 基站描述 */
	private String description = "";
	
	/**
	 * 
	 * @param id 数据库id、
	 * @param code
	 * @param description
	 */
	public LocBaseStation(long baseStationId, String code, String description) {
		super();
		this.setBaseStationId(baseStationId);
		this.code = code;
		this.description = description;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public long getBaseStationId() {
		return baseStationId;
	}

	public void setBaseStationId(long baseStationId) {
		this.baseStationId = baseStationId;
	}

}
