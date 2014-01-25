package edu.brown.hackathon.fifteenminutes;

import java.io.IOException;
import java.net.URL;
import java.util.Random;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String code = req.getParameter("code");
    Entity clientInfo = ClientInfoServlet.getClientInfo();
    
    String body = new StringBuilder()
      .append("client_id=")
      .append(clientInfo.getProperty("client_id"))
      .append("&client_secret=")
      .append(clientInfo.getProperty("client_secret"))
      .append("&grant_type=authorization_code")
      .append("&redirect_uri=")
      .append(clientInfo.getProperty("redirect_url"))
      .append("&code=")
      .append(code)
      .toString();
    
    URL url = new URL("https://api.instagram.com/oauth/access_token");
    
    URLFetchService fetcher = URLFetchServiceFactory.getURLFetchService();
    HTTPRequest authReq = new HTTPRequest(url, HTTPMethod.POST);
    authReq.setPayload(body.getBytes());
    HTTPResponse authResp = fetcher.fetch(authReq);
    
    // XXX: hacky as shit *shameface*
    String authRespBody = new String(authResp.getContent());
    String tokenPrefix = "\"access_token\":\"";
    int tokenStart = authRespBody.indexOf(tokenPrefix) + tokenPrefix.length();
    int tokenEnd = authRespBody.indexOf("\"", tokenStart);
    String token = authRespBody.substring(tokenStart, tokenEnd);
    String idPrefix = "\"id\":\"";
    int idStart = authRespBody.indexOf(idPrefix) + idPrefix.length();
    int idEnd = authRespBody.indexOf("\"", idStart);
    int id = Integer.parseInt(authRespBody.substring(idStart, idEnd));
    
    Key userKey = KeyFactory.createKey("User", id);
    Entity user = new Entity("User", userKey);
    user.setProperty("access_token", token);
    user.setProperty("user_id", id);
    user.setProperty("rand_number", new Random().nextFloat());

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(user);
    
    resp.setContentType("application/json");
    UserResource ur = new UserResource(id);
    resp.getWriter().println(new Gson().toJson(ur));
  }
}