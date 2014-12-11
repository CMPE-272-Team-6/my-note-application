package com.example.mynoteapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.widget.Toast;

import com.parse.ParseObject;

public class Utility {
	public static final int ADD_NOTE_RESULTCODE = 2;
	public static final int EDIT_NOTE_RESULTCODE = 3;
	public static final int VIEW_NOTE_RESULTCODE = 7;
	public static final int CREATE_GROUP_RESULTCODE = 4;
	public static final int VIEW_GROUP_RESULTCODE = 5;
	public static final int EDIT_GROUP_RESULTCODE = 6;
	public static final int RANDOM_RESULTCODE = 25;
	
	public void showConfirmation(String title, String msg, final String action, Context context, final ParseObject obj){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
 
			alertDialogBuilder.setTitle(title);
 
			alertDialogBuilder
				.setMessage(msg)
				.setCancelable(false)
				.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						if(action.equalsIgnoreCase("Delete")){
							obj.deleteInBackground();
						}
					}
				  })
				.setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				});
 
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				alertDialog.show();
	}
	
	public static boolean isValidEmailAddress(String email) {
         String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
         java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
         java.util.regex.Matcher m = p.matcher(email);
         return m.matches();
	}
	
	public static void displayMessagesShortDuration(Context context, String msg){
		Toast toast= Toast.makeText(context, msg, Toast.LENGTH_SHORT);  
		toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 180);
		toast.show();
	}
	
	public static void displayMessagesLongDuration(Context context, String msg){
		Toast toast= Toast.makeText(context, msg, Toast.LENGTH_LONG);  
		toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 180);
		toast.show();
	}
	
	/*Intent browserIntent = 
    new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
	startActivity(browserIntent);*/
	
/*	public void showConfirmation( final ParseObject obj){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
 
			alertDialogBuilder.setTitle("Delete Confirmation");
 
			alertDialogBuilder
				.setMessage("Are you sure to delete?")
				.setCancelable(false)
				.setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				})
				.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
							obj.deleteInBackground();
					}
				});
 
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				alertDialog.show();
	}
	
	@SuppressLint("InflateParams")
	public void showPromptDialogue(){
		
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.prompts, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);

		alertDialogBuilder.setView(promptsView);

		final EditText userInput = (EditText) promptsView
				.findViewById(R.id.editTextDialogUserInput);

		// set dialog message
		alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("OK",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {

			    }
			  })
			.setNegativeButton("Cancel",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			    }
			  });

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}*/
	
	/*//To use the AsyncTask, it must be subclassed  
	private ProgressDialog progressDialog;
	new LoadViewTask().execute();
    private class LoadViewTask extends AsyncTask<Void, Integer, Void>  
    {  
        //Before running code in separate thread  
        @Override  
        protected void onPreExecute()  
        {  
           /* //Create a new progress dialog  
            progressDialog = new ProgressDialog(ViewNotesActivity.this);  
            //Set the progress dialog to display a horizontal progress bar  
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  
            //Set the dialog title to 'Loading...'  
            progressDialog.setTitle("Loading...");  
            //Set the dialog message to 'Loading application View, please wait...'  
            progressDialog.setMessage("Loading application View, please wait...");  
            //This dialog can't be canceled by pressing the back key  
            progressDialog.setCancelable(false);  
            //This dialog isn't indeterminate  
            progressDialog.setIndeterminate(false);  
            //The maximum number of items is 100  
            progressDialog.setMax(100);  
            //Set the current progress to zero  
            progressDialog.setProgress(0);  
            //Display the progress dialog  
            progressDialog.show(); /
        	progressDialog = ProgressDialog.show(ViewNotesActivity.this,"Loading...",  
        		    "Loading application View, please wait...", false, false);  
        }  
  
        //The code to be executed in a background thread.  
        @Override  
        protected Void doInBackground(Void... params)  
        {  
        	ParseQuery<ParseObject> query = ParseQuery.getQuery("Notes").whereEqualTo("groupId", groupId);
    	    
    	    setProgressBarIndeterminateVisibility(true);
    	    query.findInBackground(new FindCallback<ParseObject>() {
    			
    			@SuppressWarnings("unchecked")
    			@Override
    			public void done(List<ParseObject> notesList, ParseException e) {
    				setProgressBarIndeterminateVisibility(false);
    				if (e == null) {
    	                notes.clear();
    	                for (ParseObject post : notesList) {
    	                	Note note = new Note(post.getObjectId(), post.getString("title"), post.getString("content"), 
    	                			post.getString("categoryId"), post.getString("assignedUser"), post.getString("groupId"));
    	                    notes.add(note);
    	                }
    	                
    	                ((ArrayAdapter<Note>)getListAdapter()).notifyDataSetChanged();
    	            } else {
    	                Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
    	            }
    				
    			}
    		});
            
        	/* This is just a code that delays the thread execution 4 times, 
             * during 850 milliseconds and updates the current progress. This 
             * is where the code that is going to be executed on a background 
             * thread must be placed. 
             */  
            /*try  
            {  
                //Get the current thread's token  
                synchronized (this)  
                {  
                    //Initialize an integer (that will act as a counter) to zero  
                    int counter = 0;  
                    //While the counter is smaller than four  
                    while(counter <= 4)  
                    {  
                        //Wait 850 milliseconds  
                        this.wait(850);  
                        //Increment the counter  
                        counter++;  
                        //Set the current progress.  
                        //This value is going to be passed to the onProgressUpdate() method.  
                        publishProgress(counter*25);  
                    }  
                }  
            }  
            catch (InterruptedException e)  
            {  
                e.printStackTrace();  
            } 
            return null;  
        }  
  
        //Update the progress  
        @Override  
        protected void onProgressUpdate(Integer... values)  
        {  
            //set the current progress of the progress dialog  
            progressDialog.setProgress(values[0]);  
        }  
  
        //after executing the code in the thread  
        @Override  
        protected void onPostExecute(Void result)  
        {  
            //close the progress dialog  
            progressDialog.dismiss();  
            //initialize the View  
            //setContentView(R.layout.main);  
        }  
    }  */

}
