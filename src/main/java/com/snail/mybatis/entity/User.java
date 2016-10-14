package com.snail.mybatis.entity;

import java.io.Serializable;
import java.util.Date;

public class User extends PageParams implements Serializable{
	private static final long serialVersionUID = 1L;

	private int id;// ` int(11) NOT NULL AUTO_INCREMENT,
	private String userName;// ` varchar(200) DEFAULT NULL,
	private String realName;// ` varchar(200) DEFAULT NULL,
	private Date birthday;// ` datetime DEFAULT NULL,
	private String password;// ` varchar(64) DEFAULT NULL,
	private String note;// ` varchar(500) DEFAULT NULL,
	private String photo;// 用户头像-->图片在服务地址

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

}
