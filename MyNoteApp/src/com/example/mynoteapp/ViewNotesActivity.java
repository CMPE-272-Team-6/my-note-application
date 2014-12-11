package com.example.mynoteapp;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mynoteapp.models.Note;
import com.example.mynoteapp.models.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ViewNotesActivity extends ListActivity {
	private ParseUser currentUser;
	private List<Note> notes;
	String groupId;
	boolean viewedFromGroup = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_view_notes);

		currentUser = ParseUser.getCurrentUser();
		Intent intent = this.getIntent();
		if(intent.getExtras() != null){
			groupId = intent.getStringExtra("groupId");
			viewedFromGroup = intent.getBooleanExtra("viewFromGroup", false);
		}

		notes = new ArrayList<Note>();
		ArrayAdapter<Note> adapter = new ArrayAdapter<Note>(this, R.layout.list_item_layout, notes);
		setListAdapter(adapter);

		refreshPostList();
	}

	public void refreshPostList() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Notes").whereEqualTo("groupId", groupId);

		setProgressBarIndeterminateVisibility(true);
		query.findInBackground(new FindCallback<ParseObject>() {

			@SuppressWarnings("unchecked")
			@Override
			public void done(List<ParseObject> notesList, ParseException e) {
				setProgressBarIndeterminateVisibility(false);
				if (e == null) {
					notes.clear();
					for (ParseObject post : notesList) {
						Note note = new Note(post.getObjectId(), post.getString("title"), post.getString("content"), 
								post.getString("categoryId"), post.getString("assignedUser"), post.getString("groupId"),
								post.getString("creatorId"), post.getBoolean("isComplete"));
						notes.add(note);
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
		if(viewedFromGroup)
			getMenuInflater().inflate(R.menu.view_notes, menu);
		else
			getMenuInflater().inflate(R.menu.view_notes_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {

		case R.id.action_refresh: {
			refreshPostList();
			break;
		}

		case R.id.add_new_note: {
			Intent intent = new Intent(this, AddNoteActivity.class);
			intent.putExtra("groupId", groupId);
			startActivityForResult(intent, Utility.ADD_NOTE_RESULTCODE);
			break;
		}

		case R.id.edit_group: {
			if(viewedFromGroup && !groupId.isEmpty()){
				changeGroupName();
			}
			break;
		}

		case R.id.add_New_Member: {
			if(viewedFromGroup && !groupId.isEmpty()){
				addNewMember();
			}
			break;
		}

		case R.id.exit_group: {
			if(viewedFromGroup){
				showConfirmation();
			}
			break;
		}

		case  R.id.note_action_logout: {
			ParseUser.logOut();
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			if(viewedFromGroup){
				setResult(Utility.EDIT_GROUP_RESULTCODE);
			}else{
				setResult(Utility.VIEW_NOTE_RESULTCODE);
			}
			finish();
			break;
		}

		case android.R.id.home: {
			NavUtils.navigateUpFromSameTask(this);
		}
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("InflateParams")
	private void addNewMember() {
		LayoutInflater li = LayoutInflater.from(ViewNotesActivity.this);
		View promptsView = li.inflate(R.layout.prompts, null);

		TextView defaultMsg = (TextView) promptsView.findViewById(R.id.promptTextView);
		defaultMsg.setText("Enter New Email");
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewNotesActivity.this);

		alertDialogBuilder.setView(promptsView);

		final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

		alertDialogBuilder
		.setCancelable(false)
		.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				String userMail  = userInput.getText().toString().trim();
				if(Utility.isValidEmailAddress(userMail)){
					checkUserExistance(userMail);
				}else{
					Utility.displayMessagesShortDuration(getApplicationContext(), "Invalid Email");
				}
			}
		})
		.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	private void checkUserExistance(String userMail) {
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.whereEqualTo("email", userMail);
		query.findInBackground(new FindCallback<ParseUser>() {
			public void done(List<ParseUser> userObjects, ParseException e) {
				if (e == null && userObjects.size() > 0) {
					ParseUser userObj = userObjects.get(0);
					User user = new User(userObj.getObjectId(), userObj.getString("username"), 
							userObj.getString("password"), userObj.getString("email"));
					isAlreadyInGroup(user);
				}else if(e == null && userObjects.size() == 0){
					String msg = "Sorry,Entered user is not \nusing NoteApp!";
					Utility.displayMessagesLongDuration(getApplicationContext(), msg);
				}else {
					Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
				}
			}
		});
	}

	private void isAlreadyInGroup(User user) {
		final List<User> userList = new ArrayList<User>();
		userList.add(user);
		ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupDetails");
		query.whereEqualTo("UserId", user.getId());
		query.whereEqualTo("GroupId", groupId);
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> groupDetailList, ParseException e) {
				if (e == null) {
					if(groupDetailList.size() > 0){
						Utility.displayMessagesShortDuration(getApplicationContext(), "User is already there in group");
					}else{
						CreateGroupActivity cgActivity = new CreateGroupActivity();
						cgActivity.saveGroupDetails(groupId, userList, false);
						Utility.displayMessagesLongDuration(getApplicationContext(), "User added in the group");
						setResult(Utility.EDIT_GROUP_RESULTCODE);
						finish();
					}
				} else {
					Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
				}
			}
		});
	}

	@SuppressLint("InflateParams")
	private void changeGroupName() {

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Groups");
		query.getInBackground(groupId, new GetCallback<ParseObject>() {
			public void done(ParseObject group, ParseException e) {
				if (e == null) {
					final ParseObject groupData = group;
					LayoutInflater li = LayoutInflater.from(ViewNotesActivity.this);
					View promptsView = li.inflate(R.layout.prompts, null);

					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewNotesActivity.this);

					alertDialogBuilder.setView(promptsView);

					final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
					userInput.setText(groupData.getString("Name"));

					alertDialogBuilder
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							String newName  = userInput.getText().toString().trim();
							if(newName.equalsIgnoreCase(groupData.getString("Name"))){
								Utility.displayMessagesShortDuration(getApplicationContext(), "Please enter the different name");
							}else{
								groupData.put("Name", newName);
								groupData.saveInBackground(new SaveCallback() {
									public void done(ParseException e) {
										if (e == null) {
											Utility.displayMessagesLongDuration(getApplicationContext(), "Group Name Updated");
											setResult(Utility.EDIT_GROUP_RESULTCODE);
											finish();
										} else {
											Utility.displayMessagesShortDuration(getApplicationContext(), "Failed to Update the Group");
											Log.d(getClass().getSimpleName(), "User update error: " + e);
										}
									}
								});
							}
						}
					})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							dialog.cancel();
						}
					});

					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				}else{
					Utility.displayMessagesShortDuration(getApplicationContext(), "Failed to Update the Group");
					Log.d(getClass().getSimpleName(), "User update error: " + e);
				}
			}
		});
	}

	private void showConfirmation() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		alertDialogBuilder.setTitle("Delete Confirmation");

		alertDialogBuilder
		.setMessage("Are you sure to exit the group?")
		.setCancelable(false)
		.setNegativeButton("No",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			}
		})
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				removeUserFromGroup();
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.show();
	}

	private void removeUserFromGroup(){
		if(currentUser != null && !groupId.isEmpty()){
			ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupDetails");
			query.whereEqualTo("UserId", currentUser.getObjectId());
			query.whereEqualTo("GroupId", groupId);
			query.findInBackground(new FindCallback<ParseObject>() {
				@Override
				public void done(List<ParseObject> groupDetailList, ParseException e) {
					if (e == null) {
						if(groupDetailList.size() > 0){
							ParseObject obj = groupDetailList.get(0);
							obj.deleteInBackground();
							setResult(Utility.EDIT_GROUP_RESULTCODE);
							finish();
						}
					} else {
						Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
						Utility.displayMessagesShortDuration(getApplicationContext(), "Some problem occur in deletion.");
					}
				}
			});
		}else{
			Utility.displayMessagesShortDuration(getApplicationContext(), "Some problem occur in deletion.");
		}
	}

	@Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data){  
		super.onActivityResult(requestCode, resultCode, data);  
		if(requestCode == Utility.ADD_NOTE_RESULTCODE || requestCode == Utility.EDIT_NOTE_RESULTCODE){  
			if(ParseUser.getCurrentUser() != null){
				refreshPostList();
			}else{
				finish();
			}
		}  
	}  

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Note note = notes.get(position);
		startViewNote(note);
	}

	private void startViewNote(Note note) {
		LocationManager locationManager;
		String provider;
		Location location;

		locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		location = locationManager.getLastKnownLocation(provider);

		boolean b = note.getIsComplete().booleanValue();
		if(note != null && !b) {
			Intent resultIntent = new Intent(getApplicationContext(), ViewNoteDetails.class);
			resultIntent.putExtra("noteId", note.getId());
			if(location != null) {
				resultIntent.putExtra("lat", String.valueOf(location.getLatitude()));
				resultIntent.putExtra("lng", String.valueOf(location.getLongitude()));
			}
			resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

			startActivityForResult(resultIntent, Utility.EDIT_NOTE_RESULTCODE);

		}
	}

}