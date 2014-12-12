package com.example.mynoteapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.mynoteapp.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
 
public class RegisterActivity extends Activity {
 
    protected EditText usernameEditText;
    protected EditText passwordEditText;
    protected EditText repasswordEditText;
    protected EditText emailEditText;
    protected ImageButton signUpButton;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.register);
 
        usernameEditText = (EditText)findViewById(R.id.usernameField);
        passwordEditText = (EditText)findViewById(R.id.passwordField);
        repasswordEditText = (EditText)findViewById(R.id.repasswordField);
        emailEditText = (EditText)findViewById(R.id.emailField);
        signUpButton = (ImageButton)findViewById(R.id.btnRegister);
 
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String rePassword = repasswordEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
 
                if (username.isEmpty() || password.isEmpty() || email.isEmpty() || rePassword.isEmpty()) {
                	Utility.displayMessagesLongDuration(getApplicationContext(), "Please make sure you enter \nall the fields above!");
                }else if(!(Utility.isValidEmailAddress(email)) || !(password.equalsIgnoreCase(rePassword))){
                	String msg = "";
                	if(!(Utility.isValidEmailAddress(email))){
                		msg = "E-mail is not valid.";
                	}
                	
                	if(!(password.equalsIgnoreCase(rePassword))){
                		if(msg.length() == 0){
                			msg = "Your passwords are not matching.";
                		}else{
                			msg = "E-mail is not valid and \nyour passwords are not matching.";
                		}
                	}
                	Utility.displayMessagesLongDuration(getApplicationContext(), msg);
                }
                else {
                    setProgressBarIndeterminateVisibility(true);
                    final ParseUser newUser = new ParseUser();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmail(email);
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            setProgressBarIndeterminateVisibility(false);
 
                            if (e == null) {
                                // Success!
                            	createDefaultGroup(newUser);
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else {
                            	Utility.displayMessagesShortDuration(getApplicationContext(), e.getMessage());
                            }
                        }
                    });
                }
            }
        });
    }
    
    private void createDefaultGroup(ParseUser userObj) {
    	List<User> userList = new ArrayList<User>();
    	if(userObj != null){
    		User user = new User(userObj.getObjectId(), userObj.getString("username"), 
        			userObj.getString("password"), userObj.getString("email"));
        	userList.add(user);
        	String groupName = "DEFAULT_"+userObj.getString("email");
        	CreateGroupActivity newGroup= new CreateGroupActivity();
    		newGroup.saveGroup(groupName, userList, true);
    	}
	}
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.sign_up, menu);
        return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
        	NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}