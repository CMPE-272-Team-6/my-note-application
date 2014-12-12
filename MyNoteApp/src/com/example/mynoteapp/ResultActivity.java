package com.example.mynoteapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


//import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mynoteapp.models.SearchResults;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ResultActivity extends ListActivity{

	private TextView noteTitle;
	private TextView noteContent;
	private TextView noteCategory;
	private TextView assignedUser;
	private List<SearchResults> searchResultsList;
	SearchResultsListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_results);
		onNewIntent(getIntent());
	}

	@Override
	public void onNewIntent(Intent intent){
		String noteId = "";
		String lat = "0";
		String lng = "0";
		List<ParseObject> notesList = null;

		Bundle extras = intent.getExtras();
		try {
			if(extras.containsKey("noteId"))
				noteId = extras.getString("noteId");

			if (noteId != null && !noteId.equalsIgnoreCase("")) {
				ParseQuery<ParseObject> query = ParseQuery.getQuery("Notes"); 
				query.whereEqualTo("objectId", noteId); 
				notesList = query.find();
				if (notesList != null && notesList.size()> 0)
					showOnScreen(notesList);
				//Toast.makeText(getApplicationContext(), "notes got .. ", Toast.LENGTH_LONG).show();
				
			}

			searchResultsList = new ArrayList<SearchResults>();
			adapter = new SearchResultsListAdapter(this, searchResultsList);
			setListAdapter(adapter);

			if(extras != null){

				ParseObject p = notesList.get(0);
				String categoryName = getCategoryName(p.getString("categoryId"));
				
				if(extras.containsKey("lat") && extras.containsKey("lng") ) {
					lat = extras.getString("lat");
					lng = extras.getString("lng");
					
					String results = queryGoogleApi(lat, lng, categoryName);
					List<SearchResults>  tempSearchResultsList = SearchResultsParser.parseResults(results);
					if(tempSearchResultsList != null && tempSearchResultsList.size() > 0) {
						for(SearchResults s : tempSearchResultsList)
							searchResultsList.add(s);
				}

//					Toast.makeText(getApplicationContext(), "finally .. ", Toast.LENGTH_LONG).show();
				}
				adapter.notifyDataSetChanged();
			}

		} catch(Exception e) {
			Log.d("ResultActivity error : ", e.getMessage());
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		SearchResults item = (SearchResults) getListAdapter().getItem(position);
		Double latitude = Double.parseDouble(item.getLatitude());
		Double longitude = Double.parseDouble(item.getLongitude());
		int zoom = 20;
		String uri = String.format(Locale.US, "geo:%f,%f?z=%d", latitude, longitude, zoom);
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getApplicationContext().startActivity(intent);
	}

	private String queryGoogleApi(String lat, String lng, String categoryName) {
		SearchResultServiceClient searchClient = new SearchResultServiceClient();
		String searchResults = "";
		try {
			searchResults = searchClient.execute(lat, lng, categoryName).get();
			return searchResults;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d("ResultActivity error", e.getMessage());
			return "{'result':'No results found nearby'}";
		}
	}

	@SuppressWarnings("static-access")
	protected void showOnScreen(List<ParseObject> notesList) throws Exception {
		noteTitle = (TextView) findViewById(R.id.noteTitle);
		noteContent = (TextView) findViewById(R.id.noteContent);
		noteCategory = (TextView) findViewById(R.id.noteCategory);
		assignedUser = (TextView) findViewById(R.id.assignedUser);
		
		assignedUser.setVisibility(assignedUser.GONE);
		for (int i = 0; i < notesList.size(); i++) {
			ParseObject p = notesList.get(i);                       
			noteTitle.setText("Title: "+p.getString("title"));
			noteContent.setText("Desc: "+p.getString("content"));
			noteCategory.setText("Category: "+getCategoryName(p.getString("categoryId")));
			assignedUser.setText("");
		} 
	}

	protected String getAssignedUser(String userId) throws Exception {
		String assignedUserName = "";
		ParseQuery<ParseObject> query = ParseQuery.getQuery("User"); 
		query.whereEqualTo("objectId", userId);
		List<ParseObject> userList = query.find();
		for(ParseObject p: userList)
			assignedUserName = p.getString("username");

		return assignedUserName;
	}

	protected String getCategoryName(String categoryId) throws Exception {
		String categoryName = "";
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Category"); 
		query.whereEqualTo("objectId", categoryId);
		List<ParseObject> categoryList = query.find();
		for(ParseObject p: categoryList)
			categoryName = p.getString("Name");

		return categoryName;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.view_notes, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		/*int id = item.getItemId();
		if (id == R.id.edit_note) {
			Log.d("action performed","edit note");
			return true;
		}

		if (id == R.id.delete_note) {
			Log.d("action performed","delete note");
			return true;
		}*/
		return super.onOptionsItemSelected(item);
	}
}
