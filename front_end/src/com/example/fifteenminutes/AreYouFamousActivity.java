package com.example.fifteenminutes;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class AreYouFamousActivity extends Activity
{
	private ProgressDialog pDialog;
	private String userId;
	
	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_PICTURE = "profile_picture";
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_are_you_famous);

        new GetData().execute();
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
    private class GetData extends AsyncTask<Void, Void, Void> {
 
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
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            JSONParser json_parser = new JSONParser();
 
            // Making a request to url and getting response
            String jsonStr = json_parser.getUserId();

            if (jsonStr != null) 
    		{
    			JSONObject jsonObj = null;
    			try
                {
                    jsonObj = new JSONObject(jsonStr);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
    			try {
    				userId = jsonObj.getString("userId");
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
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            
            /**
             * Updating parsed JSON data into TextView
             * */
//          SessionStore sessionStore = new SessionStore(getApplicationContext());
            TextView accessToken = (TextView) findViewById(R.id.text);
            accessToken.setText("UserId: " + userId);
        }
 
    }
}
