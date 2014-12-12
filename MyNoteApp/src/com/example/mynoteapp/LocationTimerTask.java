package com.example.mynoteapp;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;

public class LocationTimerTask {
	
	Activity app;
	Timer timer;
	TimerTask timerTask;
	final Handler handler = new Handler();
	
	private LocationManager locationManager;
	private String provider;
	private LocationListener locationListener;
	Location location;

	
	public LocationTimerTask(Activity a) {
		this.app = a;
	}
	
	public void startTimer() {
		//set a new Timer
		Log.d("LocationTimerTask error" , "timer task started");
		try {
			timer = new Timer();
			
			locationManager = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
			Criteria criteria = new Criteria();
			provider = locationManager.getBestProvider(criteria, false);
			location = locationManager.getLastKnownLocation(provider);
			locationListener = new MyLocationListener(app);
			if (location != null) {
				Log.d("Provider log: ","Provider " + provider + " has been selected.");
				locationListener.onLocationChanged(location);
			}

			//initialize the TimerTask's job
			initializeTimerTask();
			
			//schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
			timer.schedule(timerTask, 10, 18000000); //
		} catch (Exception e) {
			Log.d("LocationTimerTask error" , e.getMessage());
		}
	}
	
	public void stoptimertask() {
		//stop the timer, if it's not already null
		if (timer != null) {
			locationManager.removeUpdates(locationListener);
			timer.cancel();
			timer = null;
		}
	}

	public void initializeTimerTask() {

		timerTask = new TimerTask() {
			public void run() {
				//use a handler to run a toast that shows the current timestamp
				handler.post(new Runnable() {
					public void run() {
						Log.d("LocationTimerTask error" , "timer task running");
						locationManager.requestLocationUpdates(provider, 400, 1, locationListener);
					}
				});
			}
		};
	}
}
