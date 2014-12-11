package com.example.mynoteapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class MainActivity extends Activity{

	ArrayList<String> listItems=new ArrayList<String>();
	ArrayAdapter<String> adapter;
	private ParseUser currentUser;
	private ImageButton viewGroupBtn;
	private ImageButton createGroupBtn;
	private ImageButton viewNoteBtn;
	private ImageButton createNoteBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			
			//Parse.initialize(this, "V7pdKBadqlxkhKfR6yNdtUf3NmMSISOAwgtffQ6t", "V5BDslHfiGexd2Teq7bYbMrktTDHc25rU1J0X3Ca");
			Parse.initialize(this, "8wXzChBDzePezmriMMXqoXHqz5KxTdIaDzO1jwXm", "Xj25FUsg0cuRYWHEWdd4v7KCFKCOB7TZqpp5cGHX");
			currentUser = ParseUser.getCurrentUser();
			if (currentUser == null) {
				Intent intent = new Intent(this, LoginActivity.class);
			    startActivity(intent);
			    finish();
			}else{
				setContentView(R.layout.activity_main);
				initiateButtons();
				LocationTimerTask timer = new LocationTimerTask(this);
				timer.startTimer();
			} 
		} catch(Exception e) {
			Log.d("User details error : ", e.getMessage());
		}
	}
	
	private void initiateButtons() {
		viewGroupBtn = (ImageButton) findViewById(R.id.viewGpBtn);
		createGroupBtn = (ImageButton) findViewById(R.id.createGpBtn);
		viewNoteBtn = (ImageButton) findViewById(R.id.viewNoteBtn);
		createNoteBtn = (ImageButton) findViewById(R.id.createNoteBtn);
		
		viewGroupBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, ViewGroupsActivity.class);
			    startActivityForResult(intent, Utility.VIEW_GROUP_RESULTCODE);
			}
		});
	
		createGroupBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, CreateGroupActivity.class);
				startActivityForResult(intent, Utility.CREATE_GROUP_RESULTCODE);
			}
		});
	
		viewNoteBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadViewNoteScreen();
			}
		});
		
		createNoteBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					loadAddNoteScreen();
				}
		});
	}
	
	private void loadAddNoteScreen() {
		if(currentUser != null){
			ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupDetails");
			query.whereEqualTo("UserId", currentUser.getObjectId());
			query.whereEqualTo("isDefault", true);
		    query.findInBackground(new FindCallback<ParseObject>() {
				
				@Override
				public void done(List<ParseObject> groupDetailList, ParseException e) {
					if (e == null) {
						if(groupDetailList.size() > 0){
							Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
					        intent.putExtra("groupId", groupDetailList.get(0).getString("GroupId").trim());
					        startActivityForResult(intent, Utility.ADD_NOTE_RESULTCODE);
			            }
					} else {
		                Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
		                Utility.displayMessagesShortDuration(getApplicationContext(), "Problem in note creation.");
		            }
				}
			});
		}else{
			Utility.displayMessagesShortDuration(getApplicationContext(), "Problem in note creation.");
		}
	}
	
	private void loadViewNoteScreen() {
		if(currentUser != null){
			ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupDetails");
			query.whereEqualTo("UserId", currentUser.getObjectId());
			query.whereEqualTo("isDefault", true);
		    query.findInBackground(new FindCallback<ParseObject>() {
				
				@Override
				public void done(List<ParseObject> groupDetailList, ParseException e) {
					if (e == null) {
						if(groupDetailList.size() > 0){
							Intent intent = new Intent(MainActivity.this, ViewNotesActivity.class);
							intent.putExtra("groupId", groupDetailList.get(0).getString("GroupId").trim());
							intent.putExtra("viewFromGroup", false);
						    startActivityForResult(intent, Utility.VIEW_NOTE_RESULTCODE);;
						}
		            } else {
		                Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
		                Utility.displayMessagesShortDuration(getApplicationContext(), "Problem in fetching notes.");
		            }
				}
			});
		}else{
			Utility.displayMessagesShortDuration(getApplicationContext(), "Problem in fetching notes.");
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_logout) {
			ParseUser.logOut();
			Intent intent = new Intent(this, LoginActivity.class);
		    startActivity(intent);
		    finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data){  
              super.onActivityResult(requestCode, resultCode, data);  
              if(requestCode == Utility.VIEW_GROUP_RESULTCODE || requestCode == Utility.CREATE_GROUP_RESULTCODE
            		  || requestCode == Utility.ADD_NOTE_RESULTCODE || resultCode == Utility.VIEW_NOTE_RESULTCODE){  
            	  if(ParseUser.getCurrentUser() == null){
            		  finish();
            	  }
              }
    } 
}
