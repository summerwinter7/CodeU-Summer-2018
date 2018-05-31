package codeu.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeu.model.data.Conversation;
import codeu.model.data.User;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.UserStore;

/** This class was mostly modeled using the ConversationServlet as a guide */
public class ActivityfeedServlet extends HttpServlet{
	
	/** Store class that gives access to Users. */
	private UserStore userStore;
	
	/** Store class that gives access to Conversations. */
	private ConversationStore conversationStore;
	
	/**
	 * Set up state for handling the activity feed. This method is only called when
	 * running in a server, not when running in a test.
	 */
	@Override
	public void init() throws ServletException {
		super.init();
	    setUserStore(UserStore.getInstance());
	    setConversationStore(ConversationStore.getInstance());
	}
	
	/**
	 * Sets the UserStore used by this servlet. This function provides a common setup method for use
	 * by the test framework or the servlet's init() function.
	 */
	void setUserStore(UserStore userStore) {
	  this.userStore = userStore;
	}
	
	/**
	 * Sets the ConversationStore used by this servlet. This function provides a common setup method
	 * for use by the test framework or the servlet's init() function.
	 */
	void setConversationStore(ConversationStore conversationStore) {
	  this.conversationStore = conversationStore;
	}
	
	/**
	 * This function fires when a user navigates to the activity feed page. It gets all of the
	 * conversations and users from the model and forwards to activityfeed.jsp for rendering the list.
	 * It also forwards the userStore so that activityfeed.jsp can find the owners of conversations.
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException{
	    List<Conversation> conversations = conversationStore.getAllConversations();
	    List<User> users = userStore.getAllUsers();
	    request.setAttribute("conversations", conversations);
	    request.setAttribute("userStore", userStore);
	    request.setAttribute("users", users);
	    request.getRequestDispatcher("/WEB-INF/view/activityfeed.jsp").forward(request, response);
	}
	
}