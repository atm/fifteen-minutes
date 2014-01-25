package edu.brown.hackathon.fifteenminutes;

import java.io.IOException;
import java.util.Random;

import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class UserServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String id = req.getParameter("id");
    String accessToken = req.getParameter("access_token");
    Key userKey = KeyFactory.createKey("User", id);
    Entity user = new Entity(userKey);
    user.setProperty("access_token", accessToken);
    user.setProperty("user_id", id);
    user.setProperty("rand_number", new Random().nextFloat());

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(user);
    Gson gson = new Gson();
    resp.setContentType("application/json");
    resp.getWriter().println(gson.toJson(new OkMessageResource()));
  }
}