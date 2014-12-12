package com.example.mynoteapp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.example.mynoteapp.models.SearchResults;

public class SearchResultsParser {
	public static List<SearchResults> parseResults(String jsonString) {
		List<SearchResults> listItems = new ArrayList<SearchResults>();
		try {
			JSONObject jObject = new JSONObject(jsonString);
			JSONArray jArray = jObject.getJSONArray("results");

			for (int i = 0; i < jArray.length(); i++) {
				JSONObject jObj = jArray.getJSONObject(i);
				SearchResults a = new SearchResults();

				JSONObject geometry = jObj.getJSONObject("geometry");
				JSONObject location = geometry.getJSONObject("location");
				a.setLatitude(""+location.getDouble("lat"));
				a.setLongitude(""+location.getDouble("lng"));

				a.setIcon(jObj.getString("icon"));
				a.setName(jObj.getString("name"));
				a.setPlace_id(jObj.getString("place_id"));

				if(jObj.has("rating"))
					a.setRating(""+jObj.getInt("rating"));

				a.setVicinity(jObj.getString("vicinity"));
				listItems.add(a);
			}
			return listItems;
		} catch (Exception e) {
			Log.d("SearchResultsParser error in json", e.getMessage());
			return listItems;
		} 
	}
}
