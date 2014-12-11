package com.example.mynoteapp;
import android.app.Application;

import com.parse.Parse;


public class NoteAppApplication extends Application {
	
	@Override
	public void onCreate() {
	    super.onCreate();
	 
	    Parse.initialize(this, "8wXzChBDzePezmriMMXqoXHqz5KxTdIaDzO1jwXm", "Xj25FUsg0cuRYWHEWdd4v7KCFKCOB7TZqpp5cGHX");
	}
	
}
