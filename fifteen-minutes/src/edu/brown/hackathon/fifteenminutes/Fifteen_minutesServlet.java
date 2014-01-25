package edu.brown.hackathon.fifteenminutes;

import java.io.IOException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class Fifteen_minutesServlet extends HttpServlet {
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/plain");
    resp.getWriter().println("Hello, world");
  }
}
