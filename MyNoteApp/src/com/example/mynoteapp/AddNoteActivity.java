package com.example.mynoteapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.mynoteapp.models.Category;
import com.example.mynoteapp.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class AddNoteActivity extends Activity {
	
	private EditText titleEditText;
	private EditText contentEditText;
	private Button saveNoteButton;
	private Spinner categoryCombo;
	private Spinner assignedUserCombo;
	private List<Category> categories;
	private ArrayAdapter<Category> categoryAdapter;
	private List<User> userList;
	private ArrayAdapter<User> userAdapter;
	
	private String postTitle;
	private String postContent;
	private String categoryId;
	private String assignedUser;
	private String groupId;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_add_note);
		
		Intent intent = this.getIntent();
		
		titleEditText = (EditText) findViewById(R.id.noteTitle);
		contentEditText = (EditText) findViewById(R.id.noteContent);
		
		if (intent.getExtras() != null) {
			groupId = intent.getStringExtra("groupId");
		}
		
		categories = new ArrayList<Category>();
		userList = new ArrayList<User>();
		populateCategories();
		fetchCategories();
		populateAssignedUsers();
		fetchAssignedUsers();
		
		saveNoteButton = (Button)findViewById(R.id.createNote);
		saveNoteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveNote();
			}
		});
	}

	private void fetchCategories() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Category");
	    query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> categoryList, ParseException e) {
				if (e == null) {
	                categories.clear();
	                for (ParseObject cat : categoryList) {
	                    Category category = new Category(cat.getObjectId(), cat.getString("Name"));
	                	categories.add(category);
	                	categoryAdapter.notifyDataSetChanged();
	                }
	            } else {
	                Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
	            }
			}

		});
	}
	
	private void populateCategories() {
		categoryCombo = (Spinner) findViewById(R.id.category);
		categoryAdapter = new ArrayAdapter<Category>(this,
				android.R.layout.simple_spinner_item, categories);
		categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		categoryCombo.setAdapter(categoryAdapter);
	}
	
	private void fetchAssignedUsers() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupDetails").whereEqualTo("GroupId", groupId);
	    query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> groupDetails, ParseException e) {
				if (e == null) {
					userList.clear();
	                for (ParseObject group : groupDetails) {
	                	getUserDetails(group.getString("UserId"));
	                }
	            } else {
	                Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
	            }
			}
		});
	}
	
	private void populateAssignedUsers() {
		assignedUserCombo = (Spinner) findViewById(R.id.assignedUser);
		userAdapter = new ArrayAdapter<User>(this,
			android.R.layout.simple_spinner_item, userList);
		userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		assignedUserCombo.setAdapter(userAdapter);
	}
	
	private void getUserDetails(String userId) {
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.whereEqualTo("objectId", userId);
		query.findInBackground(new FindCallback<ParseUser>() {
		public void done(List<ParseUser> userObjects, ParseException e) {
				if (e == null) {
					if(userObjects.size() > 0){
						ParseUser userObj = userObjects.get(0);
	                	User user = new User(userObj.getObjectId(), userObj.getString("username"), 
	                			userObj.getString("password"), userObj.getString("email"));
	                	userList.add(user);
	                	userAdapter.notifyDataSetChanged();
		            }
				}else {
	                Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
	            }
		  }
		});
	}

	private void saveNote() {
		
		postTitle = titleEditText.getText().toString().trim();
		postContent = contentEditText.getText().toString().trim();
		
		
		categoryId = ((Category) categoryCombo.getSelectedItem()).getCategoryId().trim();
		
		assignedUser = ((User) assignedUserCombo.getSelectedItem()).getId().trim();
		
		ParseUser currentuser = ParseUser.getCurrentUser();
		
		Log.d(getClass().getSimpleName(), "Selectecd Cat:"+((Category)categoryCombo.getSelectedItem()).getCategoryId());
		
		if (currentuser != null && !postTitle.isEmpty() && !categoryId.isEmpty() && !groupId.isEmpty()) {
			final ParseObject post = new ParseObject("Notes");
			post.put("title", postTitle);
			post.put("content", postContent);
			post.put("categoryId", categoryId);
			post.put("assignedUser", assignedUser);
			post.put("groupId", groupId);
			post.put("creatorId", currentuser.getObjectId());
			post.put("isComplete", false);
			setProgressBarIndeterminateVisibility(true);
			post.saveInBackground(new SaveCallback() {
	            public void done(ParseException e) {
	            	setProgressBarIndeterminateVisibility(false);
	                if (e == null) {
	                	saveNoteButton.setEnabled(false);
	                	Utility.displayMessagesShortDuration(getApplicationContext(), "Note Created");
	                	setResult(Utility.ADD_NOTE_RESULTCODE);
	                	finish();
	                } else {
	                	e.printStackTrace();
	                	Utility.displayMessagesShortDuration(getApplicationContext(), "Some problem occur in note creation.");
	                    Log.d(getClass().getSimpleName(), "User update error: " + e);
	                }
	            }
	        });
		} 
		else {
			String errorMsg = "";
			if(postTitle.isEmpty()){
				errorMsg = errorMsg + "Please Enter : Title";
			}
			
			if(categoryId.isEmpty()){
				if(errorMsg.length() == 0){
					errorMsg = errorMsg + "Please Enter : Category";
				}else{
					errorMsg = errorMsg + ", Category";
				}
			}
			
			if(assignedUser.isEmpty()){
				if(errorMsg.length() == 0){
					errorMsg = errorMsg + "Please Enter : Assigned User";
				}else{
					errorMsg = errorMsg + ", Assigned User";
				}
			}
			Utility.displayMessagesLongDuration(getApplicationContext(), errorMsg);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_note, menu);
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
