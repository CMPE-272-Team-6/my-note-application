/*package com.example.mynoteapp;

import java.util.List;
import java.util.Objects;

import android.content.Context;
import android.widget.ArrayAdapter;

public class SpinnerAdapter extends ArrayAdapter<Objects> {

	public SpinnerAdapter(Context theContext, List<Object> objects) {
	   super(theContext, R.id.category, R.id.category, objects);
	}
	
	public SpinnerAdapter(Context theContext, List<Object> objects, int theLayoutResId) {
	  super(theContext, theLayoutResId, objects);
	}
	
	@Override
	public int getCount() {
	   // don't display last item. It is used as hint.
	   int count = super.getCount();
	   return count > 0 ? count - 1 : count;
	}
}*/