package com.example.mynoteapp.models;

public class Note {
	private String id;
    private String title;
    private String content;
    private String categoryId;
    private String assignedUser;
    private String groupId;
    private String creatorId;
    private Boolean isComplete;
	
    public Note() {
		super();
	}
	
	public Note(String id, String title, String content, String categoryId,
			String assignedUser, String groupId, String creatorId,
			Boolean isComplete) {
		super();
		this.id = id;
		this.title = title;
		this.content = content;
		this.categoryId = categoryId;
		this.assignedUser = assignedUser;
		this.groupId = groupId;
		this.creatorId = creatorId;
		this.isComplete = isComplete;
	}
	
	public String getCategoryId() {
		return categoryId;
	}


	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getAssignedUser() {
		return assignedUser;
	}


	public void setAssignedUser(String assignedUser) {
		this.assignedUser = assignedUser;
	}


	public String getGroupId() {
		return groupId;
	}


	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}


	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public Boolean getIsComplete() {
		return isComplete;
	}

	public void setIsComplete(Boolean isComplete) {
		this.isComplete = isComplete;
	}

	@Override
	public String toString() {
	    return this.getTitle();
	}
}
