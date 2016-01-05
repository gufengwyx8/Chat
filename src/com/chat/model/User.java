package com.chat.model;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

	private int id;
	private String name;
	private String password;
	private List<User> firends;
	private int sex, age;
	private String birthday;
	private String imgPath;
	private boolean login;
	private String sign;

	public User() {

	}

	public User(User user) {
		this.setUser(user);
	}

	public void setUser(User user) {
		this.id = user.id;
		this.name = user.name;
		this.password = user.password;
		this.sex = user.sex;
		this.age = user.age;
		this.birthday = user.birthday;
		this.imgPath = user.imgPath;
		this.sign = user.sign;
		this.login = user.login;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean equals(Object o) {
		if (!(o instanceof User)) {
			return false;
		}
		if (o == null) {
			return false;
		}
		User u = (User) o;
		return u.id == id;
	}

	public List<User> getFirends() {
		return firends;
	}

	public void setFirends(List<User> firends) {
		this.firends = firends;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public boolean isLogin() {
		return login;
	}

	public void setLogin(boolean login) {
		this.login = login;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
}
