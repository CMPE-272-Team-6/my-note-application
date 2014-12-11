package com.example.mynoteapp.models;

public class Groups {
	
	private String groupId;
	private String groupName;
	
	public Groups(String groupId, String groupName) {
		super();
		this.groupId = groupId;
		this.groupName = groupName;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Override
	public String toString() {
		return getGroupName();
	}
}
