package edu.brown.hackathon.fifteenminutes;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class NewFamousUserServlet extends HttpServlet {
  
  private static final Logger log = Logger.getLogger(NewFamousUserServlet.class.getName());
  
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    
    long oldFamousUser = IsFamousServlet.getUserIdOfFamousUser();
    long newFamousUser = selectRandomUserId(oldFamousUser);
    
    // Make the Instagram API calls.
    makeFamous(newFamousUser, oldFamousUser);
    
    // Set the new famous user in the DB.
    Entity famousUser = new Entity("FamousUser");
    famousUser.setProperty("user_id", newFamousUser);
    famousUser.setProperty("current_time", System.currentTimeMillis());

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(famousUser);
    
    resp.setContentType("application/json");
    UserResource ur = new UserResource(newFamousUser);
    resp.getWriter().println(new Gson().toJson(ur));
  }
  
  private static long selectRandomUserId(long oldFamousUser) {
    Query query = new Query("User");
    query.setFilter(new Query.FilterPredicate("user_id", FilterOperator.NOT_EQUAL, oldFamousUser));
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(1000));
    Entity result = results.get(new Random().nextInt(results.size()));
    return UserResource.parseUserIdFromEntity(result);
  }
  
  private static void makeFamous(long newFamousUser, long oldFamousUser) throws IOException {
    Query query = new Query("User");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(1000));
    log.info("====> going to make " + oldFamousUser + " unfamous");
    log.info("====> going to make " + newFamousUser + " hella famous");
    for (Entity result : results) {
      String accessToken = (String) result.getProperty("access_token");
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
}
