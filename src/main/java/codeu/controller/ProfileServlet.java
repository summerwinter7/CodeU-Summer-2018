package codeu.controller;

import codeu.model.data.User;
import codeu.model.store.basic.UserStore;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

public class ProfileServlet extends HttpServlet {
  private UserStore userStore;
  private User user;

  public void init() throws ServletException {
    super.init();
    setUserStore(UserStore.getInstance());
  }

  void setUserStore(UserStore userStore) {
    this.userStore = userStore;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
        request.getRequestDispatcher("/WEB-INF/view/profile.jsp").forward(request, response);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

    String username = (String) request.getSession().getAttribute("user");
    user = UserStore.getInstance().getUser(username);

    if (request.getSession().getAttribute("user") != null) {
      String aboutMe = request.getParameter("aboutMe");
      user.setAboutMe(aboutMe);
      UserStore.getInstance().updateUser(user);
    }
    response.sendRedirect("/profile");
  }
}
