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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.mynoteapp.models.Category;
import com.example.mynoteapp.models.Note;
import com.example.mynoteapp.models.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class EditNoteActivity extends Activity {
	
	private Note note;
	private EditText titleEditText;
	private EditText contentEditText;
	private Button saveNoteButton;
	private Spinner categoryCombo;
	private Spinner assignedUserCombo;
	private List<Category> categories;
	private ArrayAdapter<Category> categoryAdapter;
	private List<User> userList;
	private ArrayAdapter<User> userAdapter;
	private CheckBox isCompleteCb;
	private ParseUser currentUser;
	
	private String postTitle;
	private String postContent;
	private String categoryId;
	private String assignedUser;
	private String groupId;
	private String creatorId;
	private boolean isCompleted = false;
	
	private String oldCategory = null;
	private String oldUser = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_edit_note);
		
		currentUser = ParseUser.getCurrentUser();
		
		Intent intent = this.getIntent();
		titleEditText = (EditText) findViewById(R.id.noteTitle);
		contentEditText = (EditText) findViewById(R.id.noteContent);
		categoryCombo = (Spinner) findViewById(R.id.category);
		assignedUserCombo = (Spinner) findViewById(R.id.assignedUser);
		saveNoteButton = (Button)findViewById(R.id.updateNote);
		isCompleteCb = (CheckBox) findViewById(R.id.checkComplete);
		
		if (intent.getExtras() != null) {
			oldCategory = intent.getStringExtra("category");
			oldUser = intent.getStringExtra("assignedUser");
			groupId = intent.getStringExtra("groupId");
			creatorId = intent.getStringExtra("creatorId");
			isCompleted = intent.getBooleanExtra("isComplete", false);
			
			note = new Note(intent.getStringExtra("noteId"), intent.getStringExtra("noteTitle"), 
					intent.getStringExtra("noteContent"), oldCategory, oldUser, groupId, 
					creatorId, isCompleted);
			titleEditText.setText(note.getTitle());
			contentEditText.setText(note.getContent());
		}
		
		categories = new ArrayList<Category>();
		userList = new ArrayList<User>();
		populateCategories();
		fetchCategories();
		populateAssignedUsers();
		fetchAssignedUsers();
		
		configureCheckbox();
		configureAssignedUser();
		
		saveNoteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateNote();
			}
		});
		
	}

	private void configureAssignedUser() {
		assignedUserCombo.setEnabled(false);
		if(creatorId != null && currentUser.getObjectId().trim().equalsIgnoreCase(creatorId.trim())){
			assignedUserCombo.setEnabled(true);
		}
	}

	private void configureCheckbox() {
		isCompleteCb.setEnabled(false);
		if(isCompleted){
			isCompleteCb.setText("Completed");
			isCompleteCb.setChecked(true);
			saveNoteButton.setEnabled(false);
		}else{
			if(oldUser != null && !oldUser.isEmpty() && oldUser.equalsIgnoreCase(currentUser.getObjectId())){
				isCompleteCb.setEnabled(true);
			}
		}
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
	                }
	                setCategory();
	            } else {
	                Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
	            }
			}

		});
	}
	
	private void populateCategories() {
		categoryAdapter = new ArrayAdapter<Category>(this,
				android.R.layout.simple_spinner_item, categories);
		categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		categoryCombo.setAdapter(categoryAdapter);
	}
	
	private void setCategory(){
		categoryAdapter.notifyDataSetChanged();
		if(oldCategory != null){
			String catName = "";
			for (Category cat : categories) {
				if(cat.getCategoryId().equalsIgnoreCase(oldCategory)){
					catName = cat.getCategoryName();
					break;
				}
			}
			categoryCombo.setSelection(getIndex(categoryCombo, catName));
		}
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
	                	setAssinedUser();
		            }
				}else {
	                Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
	            }
		  }
		});
	}
	
	private void setAssinedUser() {
		userAdapter.notifyDataSetChanged();
		if(oldUser != null){
			String userName = "";
			for(User user : userList){
				if(user.getId().equalsIgnoreCase(oldUser)){
					userName = user.getUserName();
					break;
				}
			}
			assignedUserCombo.setSelection(getIndex(assignedUserCombo, userName));
		}
	}
	
	private void updateNote() {
		
		postTitle = titleEditText.getText().toString().trim();
		postContent = contentEditText.getText().toString().trim();
		
		categoryId = ((Category) categoryCombo.getSelectedItem()).getCategoryId().trim();
		
		assignedUser = ((User) assignedUserCombo.getSelectedItem()).getId().trim();
		
		
		if (!postTitle.isEmpty() && !categoryId.isEmpty() && !groupId.isEmpty()) {
			
				ParseQuery<ParseObject> query = ParseQuery.getQuery("Notes");
				 
				query.getInBackground(note.getId(), new GetCallback<ParseObject>() {
				  public void done(ParseObject post, ParseException e) {
				    if (e == null) {
				    	post.put("title", postTitle);
						post.put("content", postContent);
						post.put("categoryId", categoryId);
						post.put("assignedUser", assignedUser);
						if(isCompleteCb.isEnabled()){
							post.put("isComplete", isCompleteCb.isChecked());
						}
						setProgressBarIndeterminateVisibility(true);
						post.saveInBackground(new SaveCallback() {
				            public void done(ParseException e) {
				            	setProgressBarIndeterminateVisibility(false);
				                if (e == null) {
				                	Utility.displayMessagesLongDuration(getApplicationContext(), "Note Updated");
				                	setResult(Utility.EDIT_NOTE_RESULTCODE);
				                	finish();
				                } else {
				                	Utility.displayMessagesShortDuration(getApplicationContext(), "Failed to Update the note");
				                    Log.d(getClass().getSimpleName(), "User update error: " + e);
				                }
				            }
				        });
				    }
				  }
				});
				
		}else {
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

	private int getIndex(Spinner spinner, String myString){
		 int index = 0;
		 for (int i=0;i<spinner.getCount();i++){
		  if (spinner.getItemAtPosition(i).toString().trim().equalsIgnoreCase(myString.trim())){
		   index = i;
		   i=spinner.getCount();//will stop the loop, kind of break, by making condition false
		  }
		 }
		 Log.d(getClass().getSimpleName(), "index:"+index+"Val:"+myString);
		 return index;
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
