package codeu.controller;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

public class ProfileServlet extends HttpServlet {
  // public void doGet(HttpServletRequest request, HttpServletResponse response)
  //     throws IOException, ServletException {
  //       response.getOutputStream().println("Hello");
  // }
      @Override
      public void init() throws ServletException {
          super.init();
      }


      @Override
      public void doGet(HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {
            request.getRequestDispatcher("/WEB-INF/view/profile.jsp").forward(request, response);
      }



      @Override
      public void doPost(HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {
            String userValue = request.getParameter("AboutMe");
            System.out.println(userValue);
            response.sendRedirect("/profile/" + request.getSession().getAttribute("user"));
      }
}
