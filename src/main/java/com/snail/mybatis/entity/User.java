package com.snail.mybatis.entity;

import java.io.Serializable;

public class User extends PageParams implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;// bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
	private String user_name;// varchar(60) NOT NULL COMMENT '用户名称',
	private String cnname;// varchar(60) NOT NULL COMMENT '姓名',
	private char sex;// tinyint(3) NOT NULL COMMENT '性别',
	private String mobile;// varchar(20) NOT NULL COMMENT '手机号码',
	private String email;// varchar(60) NOT NULL COMMENT '电子邮件',
	private String note;// varchar(1024) DEFAULT NULL COMMENT '备注',

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getCnname() {
		return cnname;
	}

	public void setCnname(String cnname) {
		this.cnname = cnname;
	}

	public char getSex() {
		return sex;
	}

	public void setSex(char sex) {
		this.sex = sex;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
