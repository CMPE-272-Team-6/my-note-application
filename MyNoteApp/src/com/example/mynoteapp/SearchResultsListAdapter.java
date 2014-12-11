package com.example.mynoteapp;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mynoteapp.models.SearchResults;

@SuppressLint("ViewHolder")
public class SearchResultsListAdapter extends ArrayAdapter<SearchResults> {
	private final Context context;
	private final List<SearchResults> searchResults;

	public SearchResultsListAdapter(Context context, List<SearchResults> values) {
		super(context, R.layout.results_row_layout, values);
		this.context = context;
		this.searchResults = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		try {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.results_row_layout, parent, false);
			TextView name = (TextView) rowView.findViewById(R.id.label);
			ImageView icon = (ImageView) rowView.findViewById(R.id.icon);
			TextView address = (TextView) rowView.findViewById(R.id.address);
			name.setText(searchResults.get(position).getName());
			address.setText(searchResults.get(position).getVicinity());
			icon.setImageResource(R.drawable.cart);
			return rowView;
		} catch (Exception e) {
			Log.d("SearchResultsListAdapter error : ", e.getLocalizedMessage());
			return null;
		}
	}
} 