package edu.brown.hackathon.fifteenminutes;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.utils.SystemProperty;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class ClientInfoServlet extends HttpServlet {
  
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    bootstrapClientInfo();
    Entity result = getClientInfo();
    
    Gson gson = new Gson();
    String resultString = gson.toJson(new ClientResource((String)result.getProperty("client_id"),
        (String)result.getProperty("redirect_url")));
    resp.getWriter().println(resultString);
  }
  
  public static Entity getClientInfo() {
    String environment = "development";
    if (SystemProperty.environment.value() ==
        SystemProperty.Environment.Value.Production) {
      environment = "production";
    }
    Query query = new Query("ClientInfo");
    query.setFilter(new Query.FilterPredicate("environment", FilterOperator.EQUAL, environment));
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    return datastore.prepare(query).asSingleEntity();
  }
  
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
  
  private static class ClientResource {
    private final String client_id;
    private final String redirect_url;
    
    public ClientResource(String clientId, String redirectUrl) {
      this.client_id = clientId;
      this.redirect_url = redirectUrl;
    }
  }

}
