package edu.brown.hackathon.fifteenminutes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@SuppressWarnings("serial")
public class ListAllUsersServlet extends HttpServlet {
  
  private static final Logger log = Logger.getLogger(ListAllUsersServlet.class.getName());
  
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Query query = new Query("User");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(1000));
    List<String> allUsers = new ArrayList<String>();
    for (Entity result : results) {
      allUsers.add((String) result.getProperty("access_token"));
    }
    
    resp.setContentType("application/json");
    resp.getWriter().println(new Gson().toJson(allUsers.toArray()));
  }
  
  private static void makeFamous(String newFamousUser, String oldFamousUser, List<String> allAccessTokens) throws IOException {
    log.info("====> going to make " + oldFamousUser + " unfamous");
    log.info("====> going to make " + newFamousUser + " hella famous");
    for (String accessToken : allAccessTokens) {
      log.info("====> going to make requests using access token: " + accessToken);
      String unfollowRequestString = "https://api.instagram.com/v1/users/" + oldFamousUser + "/relationship";
      String followRequestString = "https://api.instagram.com/v1/users/" + newFamousUser + "/relationship";
      Map<String, String> postParams = new HashMap<String, String>();
      postParams.put("access_token", accessToken);
      postParams.put("action", "unfollow");
      log.info("====> unfollow result: " + HttpUtil.doHttpPost(unfollowRequestString, postParams));
      postParams.put("action", "follow");
      log.info("====> follow result: " + HttpUtil.doHttpPost(followRequestString, postParams));
    }
  }
  
  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.out.println("Usage: ListAllusersServlet <base URL>");
      System.exit(1);
    }
    String baseUrl = args[0];
    String result = HttpUtil.doHttpRequest(baseUrl + "/new-famous-user");
    
    JsonParser jsonParser = new JsonParser();
    JsonObject parsedResult = jsonParser.parse(result).getAsJsonObject();
    String newUserId = parsedResult.get("userId").getAsString();
    String oldUserId = parsedResult.get("oldUserId").getAsString();
    System.out.println("====> " + newUserId + " : " + oldUserId);
    
    result = HttpUtil.doHttpRequest(baseUrl + "/list-all-users");
    JsonArray parsedUsers = jsonParser.parse(result).getAsJsonArray();
    List<String> allAccessTokens = new ArrayList<String>();
    for (JsonElement elem : parsedUsers) {
      allAccessTokens.add(elem.getAsString());
    }
    
    makeFamous(newUserId, oldUserId, allAccessTokens);
  }
}
