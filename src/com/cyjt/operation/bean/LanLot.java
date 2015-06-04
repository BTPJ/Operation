package com.cyjt.operation.bean;

import java.io.Serializable;

public class LanLot implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double lat = 0.0;
	private double lon = 0.0;
	private String address = "";
	private String code = "";
	private String description = "";

	public LanLot(double lat, double lon, String address, String code,
			String description) {
		super();
		this.lat = lat;
		this.lon = lon;
		this.address = address;
		this.code = code;
		this.description = description;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
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
}
