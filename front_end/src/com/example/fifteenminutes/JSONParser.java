package com.example.fifteenminutes;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;
 

//import android.util.Log;

/**
 * Deals with getting and storing data on the database.
 * @author Ravi Tamada, from androidhive.info
 */

public class JSONParser 
{ 
	// location of the API for the whole program
	public static final String API_URL = "http://fifteen-minutes.appspot.com/is_famous";
	public static final String API_USER_INFO_1 = "https://api.instagram.com/v1/users/";
	public static final String API_USER_INFO_2 = "/?access_token=";
	
	static String response = null;
	public final static int GET = 1;
	public final static int POST = 2;
	
	public JSONParser() {}
	 
	/**
	 * Making service call
	 * @url - url to make request
	 * @method - http request method
	 * */
	public String getUserInfo(String accessToken, String userId)
	{
		String user_info_url = API_USER_INFO_1 + userId + API_USER_INFO_2 + accessToken;
		Log.v("Testing", "User info url: " + user_info_url);
		return this.makeServiceCall(user_info_url, GET, null);
	}
	
	public String getUserId()
	{
		return this.makeServiceCall(API_URL, GET, null);
	}
	 
	/**
	 * Making service call
	 * @url - url to make request
	 * @method - http request method
	 * @params - http request params
	 * */
	public String makeServiceCall(String url, int method,
	         List<NameValuePair> params) {
		try {
	        // http client
	        DefaultHttpClient httpClient = new DefaultHttpClient();
	        HttpEntity httpEntity = null;
	        HttpResponse httpResponse = null;
	             
	        // Checking http request method type
	        if (method == POST)
	        {
	            HttpPost httpPost = new HttpPost(url);
	            // adding post params
	            if (params != null)
	            {
	                httpPost.setEntity(new UrlEncodedFormEntity(params));
	            }
	 
	        httpResponse = httpClient.execute(httpPost);
	 
	        } 
	        else if (method == GET)
	        {
	            // appending params (if any) to url
	            if (params != null)
	            {
	                String paramString = URLEncodedUtils
	                       .format(params, "utf-8");
	                url += "?" + paramString;
	            }
	            HttpGet httpGet = new HttpGet(url);
	            httpResponse = httpClient.execute(httpGet);
	        }
	        httpEntity = httpResponse.getEntity();
	        response = EntityUtils.toString(httpEntity);
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        } catch (ClientProtocolException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
			Log.v("Testing", response);
	        return response;
	    }
}
