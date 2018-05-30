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


public class ActivityfeedServlet extends HttpServlet{
	
	/** Store class that gives access to Users. */
	private UserStore userStore;
	
	/** Store class that gives access to Conversations. */
	private ConversationStore conversationStore;
	
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
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException{
	    List<Conversation> conversations = conversationStore.getAllConversations();
	    List<User> users = userStore.getAllUsers();
	    //add all users and messages as attributes
	    request.setAttribute("conversations", conversations);
	    request.setAttribute("userStore", userStore);
	    request.setAttribute("users", users);
	    request.getRequestDispatcher("/WEB-INF/view/activityfeed.jsp").forward(request, response);
	}
	
}