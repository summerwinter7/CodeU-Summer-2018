package codeu.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** This class was mostly modeled using the ConversationServlet as a guide */
public class ActivityfeedServlet extends HttpServlet{
	
	/**
	 * Set up state for handling the activity feed. This method is only called when
	 * running in a server, not when running in a test.
	 */
	@Override
	public void init() throws ServletException {
		super.init();
	}
	
	/**
	 * This function fires when a user navigates to the activity feed page. It gets all of the
	 * conversations and users from the model and forwards to activityfeed.jsp for rendering the list.
	 * It also forwards the userStore so that activityfeed.jsp can find the owners of conversations.
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException{
	    request.getRequestDispatcher("/WEB-INF/view/activityfeed.jsp").forward(request, response);
	}
	
}