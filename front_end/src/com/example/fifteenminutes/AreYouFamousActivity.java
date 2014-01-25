package com.example.fifteenminutes;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class AreYouFamousActivity extends Activity
{
	private ProgressDialog pDialog;
	private String userId;
	private String username;
	private String profilePictureURL;
	private String accessToken;
	
	JSONParser json_parser;
	
	public static final String KEY_WHICH_USER = "userId";
	
	// constants from http://instagram.com/developer/endpoints/users/
	public static final String KEY_JSON_NODE = "data";
	public static final String KEY_USER_ID = "id";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_NAME = "full_name";
	public static final String KEY_PICTURE = "profile_picture";
	
	public static final String KEY_ACCESS_TOKEN = "access_token";
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_are_you_famous);

        Intent previousActivity = getIntent();
        // Get JSON values from previous intent
        accessToken = previousActivity.getStringExtra(KEY_ACCESS_TOKEN);
        
        new GetUserId().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.are_you_famous, menu);
        return true;
    }
    
    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetUserData extends AsyncTask<String, Void, Void> {
  
        @Override
        protected Void doInBackground(String ... args) {
            // Creating service handler class instance
            json_parser = new JSONParser();
 
            // Making a request to url and getting response
            Log.v("Testing", "accessToken: " + args[0] + " | userId: " + args[1]);
            String jsonStr = json_parser.getUserInfo(args[0], args[1]);
            JSONObject jsonObj = null;
            
            if (jsonStr != null) 
    		{
    			try
                {
                    jsonObj = new JSONObject(jsonStr);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
    			try {
    				JSONObject userInfo = jsonObj.getJSONObject(KEY_JSON_NODE); // get "data" note
    				username = userInfo.getString(KEY_USERNAME);
    				profilePictureURL = userInfo.getString(KEY_PICTURE);
    				
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
            }
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result)
        {
            /**
             * Updating parsed JSON data into ImageView and TextView
             * */
//          SessionStore sessionStore = new SessionStore(getApplicationContext());
        	// set image: http://stackoverflow.com/questions/3118691/android-make-an-image-at-a-url-equal-to-imageviews-image
        	try {
        		  ImageView i = (ImageView)findViewById(R.id.profile_picture_view);
        		  Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(profilePictureURL).getContent());
        		  i.setImageBitmap(bitmap); 
        		} catch (MalformedURLException e) {
        		  e.printStackTrace();
        		} catch (IOException e) {
        		  e.printStackTrace();
        		}
        	// set text
            TextView usernameView = (TextView) findViewById(R.id.username);
            usernameView.setText(username);
        }
    }
    
    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetUserId extends AsyncTask<Void, Void, Void> {
 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(AreYouFamousActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        @Override
        protected Void doInBackground(Void ... args) {
            // Creating service handler class instance
            json_parser = new JSONParser();
 
            // Making a request to url and getting response
            String jsonStr = json_parser.getUserId();
            JSONObject jsonObj = null;
            
            if (jsonStr != null) 
    		{
    			try
                {
                    jsonObj = new JSONObject(jsonStr);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
    			try {
    				userId = jsonObj.getString(KEY_WHICH_USER);
    				
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
            }
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            
            // before closing the dialog, get all the values
            new GetUserData().execute(accessToken, userId);
            
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
 
    }
}
