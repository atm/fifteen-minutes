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
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class NewFamousUserServlet extends HttpServlet {
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    long userId = selectRandomUserId();
    
    Entity famousUser = new Entity("FamousUser");
    famousUser.setProperty("user_id", userId);
    famousUser.setProperty("current_time", System.currentTimeMillis());

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(famousUser);
    
    resp.setContentType("application/json");
    UserResource ur = new UserResource(userId);
    resp.getWriter().println(new Gson().toJson(ur));
  }
  
  private static long selectRandomUserId() {
    Query query = new Query("User");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(1000));
    Entity result = results.get(new Random().nextInt(results.size()));
    return UserResource.parseUserIdFromEntity(result);
  }
}
