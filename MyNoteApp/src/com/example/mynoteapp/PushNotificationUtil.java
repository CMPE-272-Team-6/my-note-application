package com.example.mynoteapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class PushNotificationUtil {
	@SuppressLint("NewApi")
	public static void sendNotification(Activity a, String noteId, String noteTitle, String noteContent, String lat, String lng) {
		try {
			Log.d("PushNotificationUtil", "in PushNotificationUtil sendNotification");
			NotificationCompat.Builder mBuilder =
					new NotificationCompat.Builder(a)
			.setSmallIcon(R.drawable.notification_icon)
			.setContentTitle(noteTitle.trim())
			.setContentText(noteContent);

			Intent resultIntent = new Intent(a.getApplicationContext(), ResultActivity.class);
			resultIntent.putExtra("noteId", noteId);
			resultIntent.putExtra("lat", lat);
			resultIntent.putExtra("lng", lng);
			resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
			PendingIntent resultPendingIntent = PendingIntent.getActivity(a.getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(resultPendingIntent).setAutoCancel(true);

			NotificationManager mNotificationManager = (NotificationManager) a.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(0, mBuilder.build());
		} catch (Exception e) {
			Log.d("PushNotificationUtil error", e.getMessage());
		}
	}
}
