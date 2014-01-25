package com.example.fifteenminutes;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionStore
{	
	private static final String FILE_NAME = "InstaSessionStore";
	private static final String ACCESS_TOKEN_KEY = "Access_Token";
	private Context mContext;
	
	private static final String KEY_API_ID = "Api_Id";
	private static final String KEY_API_NAME = "Api_name";
	private static final String KEY_API_ACCESS = "Api_access_token";
	private static final String KEY_API_USERNAME = "Api_user_name";
	
	public SessionStore(Context context)
	{
		mContext = context;
	}
	
	public SharedPreferences getSharedPreferences()
	{
		return mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
	}
	
	public void saveInstaAccessToken(String accessToken)
	{
		Editor editor = getSharedPreferences().edit();
		editor.putString(ACCESS_TOKEN_KEY, accessToken);
		editor.commit();
	}
	
	public String getInstaAccessToken()
	{
		String accessToken = getSharedPreferences().getString(ACCESS_TOKEN_KEY, null);
		return accessToken;
	}
	
	public String getUserId()
	{
		return getSharedPreferences().getString(KEY_API_ID, null);
	}
	
	public String getUsername()
	{
		return getSharedPreferences().getString(KEY_API_NAME, null);
	}
	
	public void saveInstagramSession(String token, String id, String username, String name)
	{
		Editor editor = getSharedPreferences().edit();
		editor.putString(KEY_API_ID, id);
		editor.putString(KEY_API_NAME, name);
		editor.putString(KEY_API_ACCESS, token);
		editor.putString(KEY_API_USERNAME, username);
		editor.commit();
	}
	
	//If you have other things saved in SharedPreference, clear things like this
	public void resetInstagram()
	{
		Editor editor = getSharedPreferences().edit();
		editor.remove(KEY_API_ID);
		editor.remove(KEY_API_NAME);
		editor.remove(KEY_API_ACCESS);
		editor.remove(KEY_API_USERNAME);
		editor.remove("Api_email");
		editor.commit();
	}
	
	//If you have only Instagram value saved in SharedPreference, you can reset using this function as well.
	public void resetInstagream()
	{
		Editor editor = getSharedPreferences().edit();
		editor.clear();
		editor.commit();
	}

}
