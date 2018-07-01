package codeu.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeu.model.data.Activity;
import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;

/** This class was mostly modeled using the ConversationServlet as a guide */
public class ActivityfeedServlet extends HttpServlet{
	
	/** Store class that gives access to Users. */
	private UserStore userStore;
	
	private ConversationStore conversationStore;
	
	private MessageStore messageStore;
	
	/**
	 * Set up state for handling the activity feed. This method is only called when
	 * running in a server, not when running in a test.
	 */
	@Override
	public void init() throws ServletException {
		super.init();
	    setUserStore(UserStore.getInstance());
	    setConversationStore(ConversationStore.getInstance());
	    setMessageStore(MessageStore.getInstance());
	}
	
	public void setUserStore(UserStore userStore) {
		this.userStore = userStore;
	}
	
	public void setConversationStore(ConversationStore conversationStore) {
		this.conversationStore = conversationStore;
	}
	
	public void setMessageStore(MessageStore messageStore) {
		this.messageStore = messageStore;
	}
	
	/** This helper method builds the list by going through the lists of all of the
	 * users, conversations, and messages and adding them to the list of Activities
	 * to avoid casting issues. It then sorts the list and reverses the order so that
	 * it will display correctly. */
	private List<Activity> buildList() {
		List<Activity> activity = new ArrayList<>();
		List<User> users = userStore.getAllUsers();
		List<Conversation> conversations = conversationStore.getAllConversations();
		List<Message> messages = messageStore.getAllMessages();
		// adds users individually to avoid casting with generics issue
		for (User user : users) {
			activity.add(user);
		}	
		for (Conversation convo : conversations) {
			User owner = userStore.getUser(convo.getOwnerId());
			convo.setDisplayText(owner.getName() + " created conversation: " + convo.getTitle());
			activity.add(convo);
		}
		for (Message message : messages) {
			User author = userStore.getUser(message.getAuthorId());
			Conversation conversation = conversationStore.getConversationWithID(message.getConversationId());
			message.setDisplayText(author.getName() + " sent message: \"" + message.getContent() + "\"" + " to conversation: " + conversation.getTitle());
			activity.add(message);
		}
		Collections.sort(activity, Collections.reverseOrder());
		return activity;
	}
	
	
	/**
	 * This function fires when a user navigates to the activity feed page. It calls buildList
	 * to get all of the activity in a sorted list and forwards to activityfeed.jsp for rendering the list.
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException{
		List<Activity> activity = buildList();
		request.setAttribute("activity", activity);
	    request.getRequestDispatcher("/WEB-INF/view/activityfeed.jsp").forward(request, response);
	}
	
}