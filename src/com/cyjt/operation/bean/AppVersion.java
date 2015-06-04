package com.cyjt.operation.bean;

public class AppVersion {
	private Integer mid;

	private String osName;
	/** App的版本号 */
	private Integer appVersionCode;
	/** App名称 */
	private String appVersionName;
	/** 下载地址 */
	private String downloadUrl;
	/** 应用大小 */
	private double appVersionSize;
	/** 更新信息 */
	private String updateLog;
	/** 终端类型，ios、android、wp等 */
	private Integer terminalType;

	public Integer getMid() {
		return mid;
	}

	public void setMid(Integer mid) {
		this.mid = mid;
	}

	public String getOsName() {
		return osName;
	}

	public void setOsName(String osName) {
		this.osName = osName == null ? null : osName.trim();
	}

	public Integer getAppVersionCode() {
		return appVersionCode;
	}

	public void setAppVersionCode(Integer appVersionCode) {
		this.appVersionCode = appVersionCode;
	}

	public String getAppVersionName() {
		return appVersionName;
	}

	public void setAppVersionName(String appVersionName) {
		this.appVersionName = appVersionName == null ? null : appVersionName
				.trim();
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl == null ? null : downloadUrl.trim();
	}

	public String getUpdateLog() {
		return updateLog;
	}

	public void setUpdateLog(String updateLog) {
		this.updateLog = updateLog == null ? null : updateLog.trim();
	}

	public Integer getTerminalType() {
		return terminalType;
	}

	public void setTerminalType(Integer terminalType) {
		this.terminalType = terminalType;
	}

	public double getAppVersionSize() {
		return appVersionSize;
	}

	public void setAppVersionSize(double appVersionSize) {
		this.appVersionSize = appVersionSize;
	}
}