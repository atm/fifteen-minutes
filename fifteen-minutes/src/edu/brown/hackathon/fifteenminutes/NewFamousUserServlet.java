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
  
  private static void bootstrapClientInfo() {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    
    Query query = new Query("ClientInfo");
    if (datastore.prepare(query).countEntities(FetchOptions.Builder.withLimit(1000)) == 0) {
      Entity bootstrapClientInfo = new Entity("ClientInfo");
      bootstrapClientInfo.setProperty("environment", "development");
      bootstrapClientInfo.setProperty("client_id", "de9f9c9633ed45cd8f7f123054b8bb62");
      bootstrapClientInfo.setProperty("redirect_url", "http://localhost:8888/login");
      bootstrapClientInfo.setProperty("client_secret", "a9fc7a5e2244421b804c970bb26903a4");
      datastore.put(bootstrapClientInfo);
    }
  }
  
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    
    bootstrapClientInfo();
    
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
  
  // TODO(atm): this won't work until we actually have access tokens and real user IDs in the datastore.
  private static void makeFamous(long newFamousUser, long oldFamousUser) throws IOException {
    Query query = new Query("User");
    query.setFilter(new Query.FilterPredicate("user_id", FilterOperator.NOT_EQUAL, newFamousUser));
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(1000));
    System.out.println("====> going to make " + oldFamousUser + " unfamous");
    System.out.println("====> going to make " + newFamousUser + " hella famous");
    for (Entity result : results) {
      String accessToken = (String) result.getProperty("access_token");
      String unfollowRequestString = "http://api.instagram.com/v1/users/" + oldFamousUser
          + "/relationship?access_token=" + accessToken
          + "&ACTION=unfollow";
      String followRequestString = "http://api.instagram.com/v1/users/" + newFamousUser
          + "/relationship?access_token=" + accessToken
          + "&ACTION=follow";
      System.out.println("====> calling: " + unfollowRequestString);
      System.out.println("====> calling: " + followRequestString);
      //HttpUtil.doHttpRequest(unfollowRequestString);
      //HttpUtil.doHttpRequest(followRequestString);
    }
  }
}
