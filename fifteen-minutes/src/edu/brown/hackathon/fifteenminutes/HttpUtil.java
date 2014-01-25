package edu.brown.hackathon.fifteenminutes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

@SuppressWarnings("deprecation")
public class HttpUtil {
  
  public static String doHttpRequest(String urlAsString) throws IOException {
    URL url = new URL(urlAsString);
    BufferedReader reader = new BufferedReader(new InputStreamReader(
        url.openStream()));
    StringBuilder sb = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      sb.append(line);
    }
    reader.close();
    
    return sb.toString();
  }
  
  public static String doHttpPost(String urlAsString, Map<String, String> params) throws IOException {
    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httppost = new HttpPost(urlAsString);

    // Request parameters and other properties.
    List<NameValuePair> postParams = new ArrayList<NameValuePair>(2);
    for (Entry<String, String> param : params.entrySet()) {
      postParams.add(new BasicNameValuePair(param.getKey(), param.getValue()));
    }
    httppost.setEntity(new UrlEncodedFormEntity(postParams, "UTF-8"));

    //Execute and get the response.
    HttpResponse response = httpclient.execute(httppost);
    HttpEntity entity = response.getEntity();

    StringBuilder sb = new StringBuilder();
    if (entity != null) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
      try {
        String line;
        while ((line = reader.readLine()) != null) {
          sb.append(line);
        }
      } finally {
          reader.close();
      }
    }
    return sb.toString();
  }
  
  public static void main(String[] args) throws IOException {
    String obj = doHttpRequest("http://fifteen-minutes.appspot.com/is_famous");
    System.out.println("====> " + obj);
  }

}
