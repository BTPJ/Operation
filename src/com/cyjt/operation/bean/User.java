package com.cyjt.operation.bean;

public class User {
	/** 用户数据Id */
	private int id = -1;
	/** 用户编号 */
	private String code = "";
	/** 用户名 */
	private String name = "";
	/** 用户类型 */
	private int type = -1;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
