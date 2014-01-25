package edu.brown.hackathon.fifteenminutes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

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
    StringBuilder body = new StringBuilder();
    for (Entry<String, String> entry : params.entrySet()) {
      body.append(entry.getKey());
      body.append("=");
      body.append(entry.getValue());
      body.append("&");
    }
  
    URL url = new URL(urlAsString);
    
    URLFetchService fetcher = URLFetchServiceFactory.getURLFetchService();
    HTTPRequest req = new HTTPRequest(url, HTTPMethod.POST);
    req.setPayload(body.toString().getBytes());
    HTTPResponse resp = fetcher.fetch(req);
    
    return new String(resp.getContent());
  }
  
  public static void main(String[] args) throws IOException {
    String obj = doHttpRequest("http://fifteen-minutes.appspot.com/is_famous");
    System.out.println("====> " + obj);
  }

}
