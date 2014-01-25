package edu.brown.hackathon.fifteenminutes;

import java.io.IOException;
import java.util.List;
import java.util.Random;

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
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    
    long oldFamousUser = IsFamousServlet.getUserIdOfFamousUser();
    long newFamousUser = selectRandomUserId();
    
    // Make the Instagram API calls.
    // TODO(atm): this won't work until we actually have access tokens and real user IDs in the datastore.
    // makeFamous(newFamousUser, oldFamousUser);
    
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
  
  private static long selectRandomUserId() {
    Query query = new Query("User");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(1000));
    Entity result = results.get(new Random().nextInt(results.size()));
    return UserResource.parseUserIdFromEntity(result);
  }
  
  private static void makeFamous(long newFamousUser, long oldFamousUser) throws IOException {
    Query query = new Query("User");
    query.setFilter(new Query.FilterPredicate("user_id", FilterOperator.NOT_EQUAL, newFamousUser));
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(1000));
    for (Entity result : results) {
      String accessToken = (String) result.getProperty("access_token");
      String unfollowRequestString = "http://api.instagram.com/v1/users/" + oldFamousUser
          + "/relationship?access_token=" + accessToken
          + "&ACTION=unfollow";
      String followRequestString = "http://api.instagram.com/v1/users/" + newFamousUser
          + "/relationship?access_token=" + accessToken
          + "&ACTION=follow";
      
      HttpUtil.doHttpRequest(unfollowRequestString);
      HttpUtil.doHttpRequest(followRequestString);
    }
  }
}
