package edu.brown.hackathon.fifteenminutes;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Entity;
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
    List<Entity> results = new LinkedList<Entity>();
    Random rand = new Random();
    while (results.size() == 0) {
      Filter randomFilter = new FilterPredicate("rand_number",
          FilterOperator.GREATER_THAN_OR_EQUAL, rand.nextFloat());
      Query query = new Query("User");
      query.addSort("rand_number");
      query.setFilter(randomFilter);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      results = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(1));
    }
    return UserResource.parseUserIdFromEntity(results.get(0));
  }
}
