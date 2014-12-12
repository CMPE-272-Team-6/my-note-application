package com.example.mynoteapp.models;


public class User {
	
	private String id;
	private String userName;
	private String password;
	private String emailId;
	
	public User(String id, String userName, String password, String emailId) {
		super();
		this.id = id;
		this.userName = userName;
		this.password = password;
		this.emailId = emailId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	@Override
	public String toString() {
		return getUserName();
	}
	
	
}
