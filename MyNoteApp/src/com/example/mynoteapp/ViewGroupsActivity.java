package com.example.mynoteapp;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.mynoteapp.models.Groups;
import com.example.mynoteapp.models.Note;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ViewGroupsActivity extends ListActivity {
	
	private List<Groups> groupList;
	private List<String> gropIds;
	private ParseUser currentUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_view_groups);
		
		groupList = new ArrayList<Groups>();
		gropIds = new ArrayList<String>();
	    ArrayAdapter<Groups> adapter = new ArrayAdapter<Groups>(this, R.layout.plain_list, groupList);
	    setListAdapter(adapter);
	    
	    currentUser = ParseUser.getCurrentUser();
		if (currentUser == null) {
			finish();
		}else{
			refreshGroupsList();
		}
	}
	
	private void refreshGroupsList() {
		if(currentUser != null){
			ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupDetails");
			query.whereEqualTo("UserId", currentUser.getObjectId());
			query.whereEqualTo("isDefault", false);
		    query.findInBackground(new FindCallback<ParseObject>() {
				
				@Override
				public void done(List<ParseObject> groupDetailList, ParseException e) {
					if (e == null) {
						gropIds.clear();
		                for (ParseObject groupObj : groupDetailList) {
		                	gropIds.add(groupObj.getString("GroupId"));
		                }
		                getGroupDetails();
		            } else {
		                Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
		            }
				}
			});
		}
	}
	
	private void getGroupDetails() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Groups").whereContainedIn("objectId", gropIds);
	    setProgressBarIndeterminateVisibility(true);
	    query.findInBackground(new FindCallback<ParseObject>() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void done(List<ParseObject> parseGroupList, ParseException e) {
				setProgressBarIndeterminateVisibility(false);
				if (e == null) {
					groupList.clear();
	                for (ParseObject groupObj : parseGroupList) {
	                	Groups group = new Groups(groupObj.getObjectId(), groupObj.getString("Name"));
	                    groupList.add(group);
	                }
	                ((ArrayAdapter<Note>)getListAdapter()).notifyDataSetChanged();
	            } else {
	                Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
	            }
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.view_groups, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.add_new_Group) {
			Intent intent = new Intent(this, CreateGroupActivity.class);
			startActivityForResult(intent, Utility.CREATE_GROUP_RESULTCODE);
		}else if(id == R.id.action_logout){
			ParseUser.logOut();
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			setResult(Utility.VIEW_GROUP_RESULTCODE);
			finish();
		}else if(id == R.id.action_refresh){
			refreshGroupsList();
		}else if(id == android.R.id.home){
        	NavUtils.navigateUpFromSameTask(this);
        }
		return super.onOptionsItemSelected(item);
	}
	
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data){  
              super.onActivityResult(requestCode, resultCode, data);  
              if(requestCode == Utility.CREATE_GROUP_RESULTCODE || resultCode == Utility.EDIT_GROUP_RESULTCODE){  
            	  if (ParseUser.getCurrentUser() == null) {
            		  finish();
            	  }else{
            		  refreshGroupsList();
            	  }
              }
    } 
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
	    Groups group = groupList.get(position);
	    Intent intent = new Intent(this, ViewNotesActivity.class);
	    intent.putExtra("groupId", group.getGroupId());
	    intent.putExtra("viewFromGroup", true);
	    startActivityForResult(intent, Utility.EDIT_GROUP_RESULTCODE);
	}
}
