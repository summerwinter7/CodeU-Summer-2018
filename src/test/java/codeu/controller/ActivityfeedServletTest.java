package codeu.controller;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import codeu.model.data.Activity;
import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;


public class ActivityfeedServletTest {
	
	  private ActivityfeedServlet activityfeedServlet;
	  private HttpServletRequest mockRequest;
	  private HttpServletResponse mockResponse;
	  private RequestDispatcher mockRequestDispatcher;
	  private UserStore mockUserStore;
	  private ConversationStore mockConversationStore;
	  private MessageStore mockMessageStore;
	  
	  @Before
	  public void setup() {
	    activityfeedServlet = new ActivityfeedServlet();
	    
	    mockRequest = Mockito.mock(HttpServletRequest.class);
	    mockResponse = Mockito.mock(HttpServletResponse.class);
	    mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);
	    Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/activityfeed.jsp"))
	        .thenReturn(mockRequestDispatcher);
	    
	    mockUserStore = Mockito.mock(UserStore.class);
	    activityfeedServlet.setUserStore(mockUserStore);
	    
	    mockConversationStore = Mockito.mock(ConversationStore.class);
	    activityfeedServlet.setConversationStore(mockConversationStore);
	    
	    mockMessageStore = Mockito.mock(MessageStore.class);
	    activityfeedServlet.setMessageStore(mockMessageStore);

	  }

	  @Test
	  public void testDoGet() throws IOException, ServletException {
		// Later will add a fake Conversation and fake Messages 
		List<User> fakeUserList = new ArrayList<>();
		List<Conversation> fakeConversationList = new ArrayList<>();
		List<Message> fakeMessageList = new ArrayList<>();
		User user1 = new User(UUID.randomUUID(), "test_name", "password", Instant.EPOCH, "about me");
		Conversation conversation1 = new Conversation(UUID.randomUUID(), UUID.randomUUID(), "test_name", Instant.EPOCH.plusSeconds(1), true);
		Message message1 = new Message(UUID.randomUUID(), UUID.randomUUID(),UUID.randomUUID(), "test_message", Instant.EPOCH.plusSeconds(2));
		User user2 = new User(UUID.randomUUID(), "test_name2", "password2", Instant.EPOCH.plusSeconds(3), "about me 2");
		Conversation conversation2 = new Conversation(UUID.randomUUID(), UUID.randomUUID(), "test_name2", Instant.EPOCH.plusSeconds(4), true);
		Message message2 = new Message(UUID.randomUUID(), UUID.randomUUID(),UUID.randomUUID(), "test_message2", Instant.EPOCH.plusSeconds(5));

		fakeUserList.add(user1);
		fakeUserList.add(user2);
		fakeConversationList.add(conversation1);
		fakeConversationList.add(conversation2);
		fakeMessageList.add(message1);
		fakeMessageList.add(message2);
		
	    Mockito.when(mockUserStore.getAllUsers()).thenReturn(fakeUserList);
	    Mockito.when(mockConversationStore.getAllConversations()).thenReturn(fakeConversationList);
	    Mockito.when(mockMessageStore.getAllMessages()).thenReturn(fakeMessageList);

	    
	    activityfeedServlet.doGet(mockRequest, mockResponse);
	    
	    // this creates the list in reverse order, which is how it should be sent by the servlet
	    List<Activity> orderedList = new ArrayList<>();
	    orderedList.add(message2);
	    orderedList.add(conversation2);
	    orderedList.add(user2);
	    orderedList.add(message1);
	    orderedList.add(conversation1);
	    orderedList.add(user1);

	    Mockito.verify(mockRequest).setAttribute("activity", orderedList);
	    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
	  }
	  
}