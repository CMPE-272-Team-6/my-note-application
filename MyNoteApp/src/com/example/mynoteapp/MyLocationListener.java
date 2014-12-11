package com.example.mynoteapp;

import java.util.List;

import com.example.mynoteapp.models.Note;
import com.example.mynoteapp.models.SearchResults;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class MyLocationListener implements LocationListener {

	Activity app;
	Note userAssignedNote;

	public MyLocationListener(Activity app) {
		this.app = app;  
	}

	@Override
	public void onLocationChanged(Location location) {
		try {
			if(location != null) {
				Log.d("MyLocationListener" , "in MyLocationListener onLocationChanged");
				userAssignedNote = getUserAssignedNote(ParseUser.getCurrentUser().getObjectId());
				boolean b = userAssignedNote.getIsComplete().booleanValue();
				if(userAssignedNote != null && !b) {
					String results = queryGoogleApi(location, userAssignedNote.getCategoryId());
					List<SearchResults> searchResultsList = SearchResultsParser.parseResults(results);
					if(searchResultsList != null && searchResultsList.size()>0)
						PushNotificationUtil.sendNotification(this.app, userAssignedNote.getId(), userAssignedNote.getTitle(),
								userAssignedNote.getContent(),  
								String.valueOf(location.getLatitude()),
								String.valueOf(location.getLongitude())
								);
				}
			}
		} catch (Exception e) {
			Log.d("mylocationlistener error : ", e.getLocalizedMessage());
		}
	}

	private Note getUserAssignedNote(String assignedUser) {
		Note priorityNote = null;
		try {
			Log.d("MyLocationListener" , "in MyLocationListener getUserAssignedNote");
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Notes").whereEqualTo("isComplete", Boolean.FALSE); 
			query.whereEqualTo("assignedUser", assignedUser);
			query.addAscendingOrder("createdAt");
			List<ParseObject> notesList = query.find();
			if (notesList != null && notesList.size()> 0) {
				ParseObject p = notesList.get(0);
				priorityNote = new Note();
				priorityNote.setId(p.getObjectId());
				priorityNote.setTitle(p.getString("title"));
				priorityNote.setContent(p.getString("content"));
				priorityNote.setCategoryId(getCategoryName(p.getString("categoryId")));
				priorityNote.setAssignedUser(assignedUser);
				priorityNote.setIsComplete(p.getBoolean("isComplete"));
			}
			return priorityNote;
		} catch(Exception e) {
			Log.d("mylocationlistener error : ", e.getLocalizedMessage());
			return null;
		}

	}

	protected String getCategoryName(String categoryId) throws Exception {
		Log.d("MyLocationListener" , "in MyLocationListener getCategoryName");
		String categoryName = "";
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Category"); 
		query.whereEqualTo("objectId", categoryId);
		List<ParseObject> categoryList = query.find();
		for(ParseObject p: categoryList)
			categoryName = p.getString("Name");

		return categoryName;
	}

	private String queryGoogleApi(Location location, String category) {
		Log.d("MyLocationListener" , "in MyLocationListener queryGoogleApi");
		SearchResultServiceClient searchClient = new SearchResultServiceClient();
		String searchResults = "";
		try {
			String lat = String.valueOf(location.getLatitude());
			String lng = String.valueOf(location.getLongitude());
			searchResults = searchClient.execute(lat, lng, category).get();
			return searchResults;
		} catch (Exception e) {
			Log.d("mylocationlistener error", e.getMessage());
			return "{'result':'No results found nearby'}";
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String provider) {
		//Toast.makeText(this, "Enabled new provider " + provider,Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderDisabled(String provider) {
		//Toast.makeText(this, "Disabled provider " + provider,Toast.LENGTH_SHORT).show();
	}
}