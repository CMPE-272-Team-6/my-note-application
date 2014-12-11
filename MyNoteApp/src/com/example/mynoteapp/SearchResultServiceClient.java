package com.example.mynoteapp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

@SuppressLint("DefaultLocale")
public class SearchResultServiceClient extends AsyncTask<String, Void, String>{

	protected String doInBackground(String... params) {
		try {
			double lat = Double.parseDouble(params[0]);
			double lng = Double.parseDouble(params[1]);
			String types = "";
			if(params[2] != null)
				types = checkCategory(params[2]);
			
			String searchResults = readSearchResults(lat, lng, types);
			return searchResults;
		} catch (Exception e) {
			Log.d("Search result client" , e.getMessage());
			return null;
		}
	}

	private String checkCategory(String key) {
		String keyType = key.toLowerCase();
		String queryTypeParam  = "";
		switch(keyType) {
			case "grocery": queryTypeParam = "grocery_or_supermarket";
							break;
			case "restaurant": queryTypeParam = "food";
							break;
			case "medical": queryTypeParam = "pharmacy";
							break;
			case "drinks": queryTypeParam = "cafe";
							break;
			default: queryTypeParam = "store";
					break;
		}	
		return queryTypeParam;
	}

	protected void onPostExecute(String feed) {
		// TODO: check this.exception 
		// TODO: do something with the feed
	}

	public String readSearchResults(double latitude, double longitude, String types) {
		StringBuilder stringBuilder = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();
		String URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+latitude+","+longitude+"&radius="+1500+"&types="+types+"&key="+"AIzaSyDi1kvoNOJ4-_zrMHnZVtUWocs9d6WSwzk";
		HttpGet httpGet = new HttpGet(URL);
		try {
			HttpResponse response = httpClient.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream inputStream = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream));
				String line;
				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line);
				}
				inputStream.close();
			} else {
				Log.d("search results json", "Failed to download file");
			}
		} catch (Exception e) {
			Log.d("search results json", e.getLocalizedMessage());
		}        
		return stringBuilder.toString();
	}
}
