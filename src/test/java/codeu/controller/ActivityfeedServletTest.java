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
import javax.servlet.http.HttpSession;

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
	  private HttpSession mockSession;
	  
	  @Before
	  public void setup() {
	    activityfeedServlet = new ActivityfeedServlet();
	    
	    mockRequest = Mockito.mock(HttpServletRequest.class);
	    mockSession = Mockito.mock(HttpSession.class);
	    Mockito.when(mockRequest.getSession()).thenReturn(mockSession);
	    
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
	  public void testDoGet_NoUser() throws IOException, ServletException {
		List<User> fakeUserList = new ArrayList<User>();
		List<Conversation> fakeConversationList = new ArrayList<Conversation>();
		List<Message> fakeMessageList = new ArrayList<Message>();

		User user1 = new User(UUID.randomUUID(), "test_name", "password", Instant.EPOCH, "about me");
		Conversation conversation1 = new Conversation(UUID.randomUUID(), user1.getId(), "test_name", Instant.EPOCH.plusSeconds(1), false);
		Message message1 = new Message(UUID.randomUUID(), conversation1.getId(), user1.getId(), "test_message", Instant.EPOCH.plusSeconds(2));
		User user2 = new User(UUID.randomUUID(), "test_name2", "password2", Instant.EPOCH.plusSeconds(3), "about me 2");
		Conversation conversation2 = new Conversation(UUID.randomUUID(), user2.getId(), "test_name2", Instant.EPOCH.plusSeconds(4), true);
		Message message2 = new Message(UUID.randomUUID(), conversation2.getId(), user2.getId(), "test_message2", Instant.EPOCH.plusSeconds(5));
	

		fakeUserList.add(user1);
		fakeUserList.add(user2);
		fakeConversationList.add(conversation1);
		fakeConversationList.add(conversation2);
		fakeMessageList.add(message1);
		fakeMessageList.add(message2);
		
		List<UUID> memberList = new ArrayList<UUID>();
		memberList.add(user1.getId());
		conversation1.setMembers(memberList);
		List<UUID> conversationList = new ArrayList<UUID>();
		conversationList.add(conversation1.getId());
		user1.setConversations(conversationList);
		
		Mockito.when(mockSession.getAttribute("user")).thenReturn(null);
	    Mockito.when(mockUserStore.getAllUsers()).thenReturn(fakeUserList);
	    Mockito.when(mockConversationStore.getAllConversations()).thenReturn(fakeConversationList);
	    Mockito.when(mockMessageStore.getAllMessages()).thenReturn(fakeMessageList);
	    
	    Mockito.when(mockUserStore.getUser(user1.getId())).thenReturn(user1);
	    Mockito.when(mockUserStore.getUser(user2.getId())).thenReturn(user2);
	    Mockito.when(mockConversationStore.getConversationWithID(conversation1.getId())).thenReturn(conversation1);
	    Mockito.when(mockConversationStore.getConversationWithID(conversation2.getId())).thenReturn(conversation2);
	    
	    activityfeedServlet.doGet(mockRequest, mockResponse);
	    
	    // this creates the list in reverse order, which is how it should be sent by the servlet
	    List<Activity> orderedList = new ArrayList<>();
	    orderedList.add(message2);
	    orderedList.add(conversation2);
	    orderedList.add(user2);
	    orderedList.add(user1);

	    Mockito.verify(mockRequest).setAttribute("activity", orderedList);
	    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
	  }
	  
	  @Test 
	  public void testDoGet_withUser() throws IOException, ServletException {
		  	List<User> fakeUserList = new ArrayList<>();
			List<Conversation> fakeConversationList = new ArrayList<>();
			List<Message> fakeMessageList = new ArrayList<>();

			User user1 = new User(UUID.randomUUID(), "test_name", "password", Instant.EPOCH, "about me");
			Conversation conversation1 = new Conversation(UUID.randomUUID(), user1.getId(), "test_name", Instant.EPOCH.plusSeconds(1), false);
			Message message1 = new Message(UUID.randomUUID(), conversation1.getId(), user1.getId(), "test_message", Instant.EPOCH.plusSeconds(2));
			User user2 = new User(UUID.randomUUID(), "test_name2", "password2", Instant.EPOCH.plusSeconds(3), "about me 2");
			Conversation conversation2 = new Conversation(UUID.randomUUID(), user2.getId(), "test_name2", Instant.EPOCH.plusSeconds(4), true);
			Message message2 = new Message(UUID.randomUUID(), conversation2.getId(), user2.getId(), "test_message2", Instant.EPOCH.plusSeconds(5));

			fakeUserList.add(user1);
			fakeUserList.add(user2);
			fakeConversationList.add(conversation1);
			fakeConversationList.add(conversation2);
			fakeMessageList.add(message1);
			fakeMessageList.add(message2);
			
			List<UUID> memberList = new ArrayList<UUID>();
			memberList.add(user1.getId());
			conversation1.setMembers(memberList);
			List<UUID> conversationList = new ArrayList<UUID>();
			conversationList.add(conversation1.getId());
			user1.setConversations(conversationList);
			
			Mockito.when(mockSession.getAttribute("user")).thenReturn("test_name");
		    Mockito.when(mockUserStore.getUser("test_name")).thenReturn(user1);
			
		    Mockito.when(mockUserStore.getAllUsers()).thenReturn(fakeUserList);
		    Mockito.when(mockConversationStore.getAllConversations()).thenReturn(fakeConversationList);
		    Mockito.when(mockMessageStore.getAllMessages()).thenReturn(fakeMessageList);
		    
		    Mockito.when(mockUserStore.getUser(user1.getId())).thenReturn(user1);
		    Mockito.when(mockUserStore.getUser(user2.getId())).thenReturn(user2);
		    Mockito.when(mockConversationStore.getConversationWithID(conversation1.getId())).thenReturn(conversation1);
		    Mockito.when(mockConversationStore.getConversationWithID(conversation2.getId())).thenReturn(conversation2);
		    
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