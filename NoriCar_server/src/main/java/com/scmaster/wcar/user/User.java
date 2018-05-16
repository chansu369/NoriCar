package com.scmaster.wcar.user;

public class User {

	private int usernum;
	private String name;
	private String phone;
	private String password;
	private String email;
	private String token;
	
	public User() {
		super();
	}
	
	public User(int usernum, String name, String phone, String password, String email, String token) {
		super();
		this.usernum = usernum;
		this.name = name;
		this.phone = phone;
		this.password = password;
		this.email = email;
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getUsernum() {
		return usernum;
	}
	public void setUsernum(int usernum) {
		this.usernum = usernum;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Override
	public String toString() {
		return "User [usernum=" + usernum + ", name=" + name + ", phone=" + phone + ", password=" + password
				+ ", email=" + email + ", token=" + token + "]";
	}
	
	
	
	
	
}
