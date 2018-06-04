package codeu.controller;

import codeu.model.data.User;
import codeu.model.store.basic.UserStore;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ProfileServletTest {

      private ProfileServlet profileServlet;
      private HttpServletRequest mockRequest;
      private HttpSession mockSession;
      private HttpServletResponse mockResponse;
      private RequestDispatcher mockRequestDispatcher;
      private UserStore mockUserStore;
       @Before
       public void setup() {
          profileServlet = new ProfileServlet();

          mockRequest = Mockito.mock(HttpServletRequest.class);
          mockSession = Mockito.mock(HttpSession.class);
          Mockito.when(mockRequest.getSession()).thenReturn(mockSession);

          mockResponse = Mockito.mock(HttpServletResponse.class);
          mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);
          Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/profileServlet.jsp"))
              .thenReturn(mockRequestDispatcher);

          mockUserStore = Mockito.mock(UserStore.class);
          chatServlet.setUserStore(mockUserStore);
       }

       @Test
       public void testDoGet() throws IOException, ServletException {


}