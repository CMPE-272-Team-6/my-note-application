package com.example.mynoteapp.models;
public class GroupDetails {

	private String id;
	private String groupId;
	private String userId;
	private boolean isDefault;
	
	public GroupDetails(String id, String groupId, String userId,
			boolean isDefault) {
		super();
		this.id = id;
		this.groupId = groupId;
		this.userId = userId;
		this.isDefault = isDefault;
	}
	
	public boolean isDefault() {
		return isDefault;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
}