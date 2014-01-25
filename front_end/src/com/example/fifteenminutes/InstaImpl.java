package com.example.fifteenminutes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class InstaImpl
{
	private static final String TOKENURL = "https://api.instagram.com/oauth/access_token";
	private static final String AUTHURL = "https://api.instagram.com/oauth/authorize/";
	public static final String APIURL = "https://api.instagram.com/v1";
	public static String CALLBACKURL;
	
	private SessionStore mSessionStore;
	
	private String mAuthURLString;
	private String mTokenURLString;
	private String mAccessTokenString;
	public String mClient_id;
	public String mClient_secret;
	private String mToken;
	
	private AuthAuthenticationListener mAuthAuthenticationListener;
	
	private ProgressDialog mProgressDialog;
	private Context mContext;
	
	public InstaImpl(Context context)
	{
		mContext = context;
		mSessionStore = new SessionStore(context);
		mClient_id = context.getResources().getString(R.string.instagram_id); // Recommended: Put your Instagram ID in string class
		mClient_secret = context.getResources().getString(R.string.instagram_secret); // Recommended: Put your Instagram Secret in string class
		CALLBACKURL = context.getResources().getString(R.string.callbackurl);
		
		mAuthURLString = AUTHURL + "?client_id=" + mClient_id + "&redirect_uri=" + CALLBACKURL + "&response_type=code&display=touch&scope=likes+comments+relationships";
		mTokenURLString = TOKENURL + "?client_id=" + mClient_id + "&client_secret=" + mClient_secret + "&redirect_uri=" + CALLBACKURL + "&grant_type=authorization_code";
		
		InstaAuthDialogListener instaAuthDialogListener = new InstaAuthDialogListener();
		mAccessTokenString = mSessionStore.getInstaAccessToken();
		InstaLoginDialog instaLoginDialog = new InstaLoginDialog(context, mAuthURLString, instaAuthDialogListener);
		instaLoginDialog.show();
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setTitle("Please Wait");
		mProgressDialog.setCancelable(false);
	}
	
	public String getAccessToken()
	{
		return mAccessTokenString;
	}
	
	public class InstaAuthDialogListener implements com.example.fifteenminutes.InstaLoginDialog.AuthDialogListener
	{
		@Override
		public void onComplete(String token) {
			getAccessToken(token);
		}

		@Override
		public void onError(String error) {
			
		}
	}
	
	private void getAccessToken(String token)
	{
		this.mToken = token;
		new GetInstagramTokenAsyncTask().execute();
	}
	
	public class GetInstagramTokenAsyncTask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected Void doInBackground(Void... params) {
			try 
			{
				URL url = new URL(mTokenURLString);
				HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
				httpsURLConnection.setRequestMethod("POST");
				httpsURLConnection.setDoInput(true);
				httpsURLConnection.setDoOutput(true);
				
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
				outputStreamWriter.write("client_id="+mClient_id+
						"&client_secret="+ mClient_secret +
						"&grant_type=authorization_code" +
						"&redirect_uri="+CALLBACKURL+
						"&code=" + mToken);
				outputStreamWriter.flush();
				
				//Response would be a JSON response sent by instagram
				String response = streamToString(httpsURLConnection.getInputStream());
				//Log.e("USER Response", response);
				JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
				
				//Your access token that you can use to make future request
				mAccessTokenString = jsonObject.getString("access_token");
				Log.v("Testing", "Access token: " + mAccessTokenString);
				
				//User details like, name, id, tagline, profile pic etc.
				JSONObject userJsonObject = jsonObject.getJSONObject("user");
				//Log.e("USER DETAIL", userJsonObject.toString());
				
				//User ID
				String id = userJsonObject.getString("id");
				//Log.e(TAG, id);
				
				//Username
				String username = userJsonObject.getString("username");
				//Log.e(TAG, username);
				
				//User full name
				String name = userJsonObject.getString("full_name");
				//Log.e(TAG, name);
				mSessionStore.saveInstagramSession(mAccessTokenString, id, username, name);
//				showResponseDialog(name, mAccessTokenString);
			}
			catch (Exception e) 
			{
				mAuthAuthenticationListener.onFail("Failed to get access token");
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			dismissDialog();
			mAuthAuthenticationListener.onSuccess();
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			showDialog("Getting Access Token..");
			super.onPreExecute();
		}
	}
	
	public void setAuthAuthenticationListener(AuthAuthenticationListener authAuthenticationListener)
	{
		this.mAuthAuthenticationListener = authAuthenticationListener;
	}
	
	public interface AuthAuthenticationListener
	{
		public abstract void onSuccess();
		public abstract void onFail(String error);
	}
	
	public String streamToString(InputStream is) throws IOException
	{
		String string = "";

		if (is != null)
		{
			StringBuilder stringBuilder = new StringBuilder();
			String line;

			try
			{
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));

				while ((line = reader.readLine()) != null)
				{
					stringBuilder.append(line);
				}

				reader.close();
			}
			finally
			{
				is.close();
			}

			string = stringBuilder.toString();
		}

		return string;
	}
	
	public void showDialog(String message)
	{
		mProgressDialog.setMessage(message);
		mProgressDialog.show();
	}
	
	public void dismissDialog() {
		if (mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}	
}
