package edu.brown.hackathon.fifteenminutes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class HttpUtil {
  
  public static JsonObject doHttpRequest(String urlAsString) throws IOException {
    URL url = new URL(urlAsString);
    BufferedReader reader = new BufferedReader(new InputStreamReader(
        url.openStream()));
    StringBuilder sb = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      sb.append(line);
    }
    reader.close();
    
    JsonParser parser = new JsonParser();
    return parser.parse(sb.toString()).getAsJsonObject();
  }
  
  public static void main(String[] args) throws IOException {
    JsonObject obj = doHttpRequest("http://fifteen-minutes.appspot.com/is_famous");
    System.out.println("====> " + obj.get("userId"));
  }

}
