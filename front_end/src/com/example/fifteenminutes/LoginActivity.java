package com.example.fifteenminutes;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * 
 * @author http://b2cloud.com.au/uncategorized/instagram-integration-in-an-android-app
 *
 */

public class LoginActivity extends Activity implements OnClickListener
{	
	private Button mButton;
	private InstaImpl mInstaImpl;
	private Context mContext;
	private ResponseListener mResponseListener;
	
	private String name;
	private String accessToken;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_login);
		mButton = (Button)findViewById(R.id.button_login);
		mButton.setOnClickListener(this);
		mResponseListener = new ResponseListener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_insta, menu);
		return true;
	}

	@Override
	public void onClick(View arg0)
	{
		mInstaImpl = new InstaImpl(this);
		mInstaImpl.setAuthAuthenticationListener(new AuthListener());
	}
	
	@Override
	protected void onResume() // TODO wtf
	{
		super.onResume();
		IntentFilter filter = new IntentFilter();
        filter.addAction("com.varundroid.instademo.responselistener");
        filter.addCategory("com.varundroid.instademo");
        registerReceiver(mResponseListener, filter);
	}
	
	@Override
	protected void onPause() {
		unregisterReceiver(mResponseListener);
		super.onPause();
	}
	
	/** AuthListener class */
	public class AuthListener implements com.example.fifteenminutes.InstaImpl.AuthAuthenticationListener
	{
		@Override
		public void onSuccess()
		{
			Toast.makeText(LoginActivity.this, "Instagram authorization successful", Toast.LENGTH_SHORT).show();
			
			accessToken = mInstaImpl.getAccessToken();
			Log.v("Testing", "Access Token again? " + accessToken);
			
			Intent toFame = new Intent(getApplicationContext(), AreYouFamousActivity.class);
			toFame.putExtra(AreYouFamousActivity.KEY_ACCESS_TOKEN, accessToken);
	        startActivity(toFame);
	        finish();
		}

		@Override
		public void onFail(String error) {
			Toast.makeText(LoginActivity.this, "Authorization Failed", Toast.LENGTH_SHORT).show();
		}
	}	
	
	/** ResponseListener class" */
	public class ResponseListener extends BroadcastReceiver
	{
//		public static final String ACTION_RESPONSE = "com.varundroid.instademo.responselistener";
		public static final String EXTRA_NAME = "90293d69-2eae-4ccd-b36c-a8d0c4c1bec6";
		public static final String EXTRA_ACCESS_TOKEN = "bed6838a-65b0-44c9-ab91-ea404aa9eefc";

		@Override
		public void onReceive(Context context, Intent intent) {
			mInstaImpl.dismissDialog();
//			Bundle extras = intent.getExtras();
//			name = extras.getString(EXTRA_NAME);
//			accessToken = extras.getString(EXTRA_ACCESS_TOKEN);
//			final AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
//			alertDialog.setTitle("Your Details");
//			alertDialog.setMessage("Name - " + name + ", Access Token - " + accessToken);
//			alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//				
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					
//				}
//			});
//			alertDialog.show();
		}
	}
}
