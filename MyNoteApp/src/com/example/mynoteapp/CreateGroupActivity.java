package com.example.mynoteapp;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.mynoteapp.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class CreateGroupActivity extends ListActivity {
	
	ImageButton addUserBtn;
	EditText userField;
	List<User> userList;
	Button createGroupBtn;
	EditText groupNameField;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_group);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		groupNameField = (EditText) findViewById(R.id.groupName);
		userField = (EditText) findViewById(R.id.userName);
		addUserBtn = (ImageButton) findViewById(R.id.createUserBtn);
		createGroupBtn = (Button) findViewById(R.id.createGroup);
		
		ParseUser currentUser = ParseUser.getCurrentUser();
		User user = new User(currentUser.getObjectId(), currentUser.getUsername(), "", currentUser.getEmail());
	 	userList = new ArrayList<User>();
	 	userList.add(user);
	    ArrayAdapter<User> adapter = new ArrayAdapter<User>(this, R.layout.group_users_add, userList);
	    setListAdapter(adapter);
		
	    addUserBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				addNewUserToGroup();
			}
		});
	    
	    createGroupBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				createNewGroup();
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void addNewUserToGroup() {
		String userMail = userField.getText().toString().trim();
		Log.d(getClass().getSimpleName(), userMail);
		if(userMail != null && !userMail.equalsIgnoreCase("")){
			if(!checkUserExist(userMail)){
				ParseQuery<ParseUser> query = ParseUser.getQuery();
				query.whereEqualTo("email", userMail);
				query.findInBackground(new FindCallback<ParseUser>() {
				  public void done(List<ParseUser> userObjects, ParseException e) {
					  Log.d(getClass().getSimpleName(), "List Size From the Web:"+userObjects.size());
						if (e == null && userObjects.size() > 0) {
							ParseUser userObj = userObjects.get(0);
		                	User user = new User(userObj.getObjectId(), userObj.getString("username"), 
		                			userObj.getString("password"), userObj.getString("email"));
		                	userList.add(user);
		                	((ArrayAdapter<String>)getListAdapter()).notifyDataSetChanged();
		    				userField.setText("");
			            }else if(e == null && userObjects.size() == 0){
			            	String msg = "Sorry,Entered user is not \nusing NoteApp!";
			            	Utility.displayMessagesLongDuration(getApplicationContext(), msg);
			            	userField.setText("");
			            }else {
			                Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
			            }
				  }
				});
			}else{
				userField.setText("");
				Utility.displayMessagesLongDuration(getApplicationContext(), "Entered user has already been selected.");
			}
		}else{
			Utility.displayMessagesShortDuration(getApplicationContext(), "Please enter the user first.");
		}
	}
	
	private boolean checkUserExist(String userMail) {
		boolean isExist = false;
		for(User user : userList){
			if(user.getEmailId().trim().equalsIgnoreCase(userMail)){
				isExist = true;
			}
		}
		return isExist;
	}

	private void createNewGroup() {
		String groupName = groupNameField.getText().toString().trim();
		if(!groupName.isEmpty() && userList.size() > 0){
			saveGroup(groupName.trim(), userList, false);
		}else{
			String errorMsg = "Please enter a group name.";
			Utility.displayMessagesLongDuration(getApplicationContext(), errorMsg);
		}
	}
	
	public void saveGroup(String groupTitle, final List<User> users, final boolean isDefault ){
		final ParseObject group = new ParseObject("Groups");
		group.put("Name", groupTitle);
		group.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                	saveGroupDetails(group.getObjectId(), users, isDefault);
                	if(!isDefault){
                		Utility.displayMessagesShortDuration(getApplicationContext(), "Group Created");
                    	setResult(Utility.CREATE_GROUP_RESULTCODE);
                    	finish();
                	}
                } else {
                	e.printStackTrace();
                	if(!isDefault){
                		Utility.displayMessagesShortDuration(getApplicationContext(), "Failed to create Group");
                	}
                    Log.d(getClass().getSimpleName(), "User update error: " + e);
                }
            }
        });
	}
	
	public void saveGroupDetails(String groupId, List<User> users, boolean isDefault) {
		for(User user : users){
			final ParseObject groupDetail = new ParseObject("GroupDetails");
			groupDetail.put("GroupId", groupId);
			groupDetail.put("UserId", user.getId());
			groupDetail.put("isDefault", isDefault);
			groupDetail.saveInBackground();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.create_group, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == android.R.id.home){
        	NavUtils.navigateUpFromSameTask(this);
        }
		return super.onOptionsItemSelected(item);
	}
}
