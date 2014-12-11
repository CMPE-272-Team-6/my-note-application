package com.example.mynoteapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
 
public class LoginActivity extends Activity {

		protected EditText usernameEditText;
		protected EditText passwordEditText;
		protected ImageButton loginButton;
				
		protected ImageButton signUpTextView;
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
			setContentView(R.layout.login);
			
			signUpTextView = (ImageButton)findViewById(R.id.signUpText);
			usernameEditText = (EditText)findViewById(R.id.usernameField);
			passwordEditText = (EditText)findViewById(R.id.passwordField);
			loginButton = (ImageButton)findViewById(R.id.loginButton);
			
			signUpTextView.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
					startActivity(intent);
				}
			}
			);
			
			loginButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String username = usernameEditText.getText().toString();
					String password = passwordEditText.getText().toString();
	
					username = username.trim();
					password = password.trim();
	
					if (username.isEmpty() || password.isEmpty()) {
						Utility.displayMessagesLongDuration(getApplicationContext(), "Please make sure you enter a \nusername and password!");
					}
					else {
						setProgressBarIndeterminateVisibility(true);
						ParseUser.logInInBackground(username, password, new LogInCallback() {
							@Override
							public void done(ParseUser user, ParseException e) {
								setProgressBarIndeterminateVisibility(false);
	
								if (e == null) {
									if(user.getBoolean("emailVerified")){
										Intent intent = new Intent(LoginActivity.this, MainActivity.class);
										intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
										startActivity(intent);
									}else{
										Utility.displayMessagesShortDuration(getApplicationContext(), "Your account is not verified. \nCheck your mails for email verification.");
									}
								} else {
									Utility.displayMessagesShortDuration(getApplicationContext(), e.getMessage());
								}
							}
						}
						);
					}
				}
			});
		}
		
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			return true;
		}
	
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			return super.onOptionsItemSelected(item);
		}
}