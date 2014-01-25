package edu.brown.hackathon.fifteenminutes;

import java.io.IOException;
import javax.servlet.http.*;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class IsFamousServlet extends HttpServlet {
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    UserResource ur = new UserResource(123);
    Gson gson = new Gson();
    resp.setContentType("application/json");
    resp.getWriter().println(gson.toJson(ur));
  }
}