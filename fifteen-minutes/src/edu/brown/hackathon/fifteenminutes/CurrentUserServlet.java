package edu.brown.hackathon.fifteenminutes;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

@SuppressWarnings("serial")
public class CurrentUserServlet extends HttpServlet {
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String accessToken = req.getParameter("access_token");
    
    URL url = new URL("https://api.instagram.com/v1/users/self/?access_token=" + accessToken);
    
    URLFetchService fetcher = URLFetchServiceFactory.getURLFetchService();
    HTTPResponse authResp = fetcher.fetch(url);
    
    String authRespBody = new String(authResp.getContent());
    resp.setContentType("application/json");
    resp.getWriter().println(authRespBody);
  }
}