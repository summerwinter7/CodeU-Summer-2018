package codeu.controller;

import java.io.IOException;
import java.util.UUID;
import java.time.Instant;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import codeu.controller.AdminServlet;
import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;

public class AdminServletTest {
	private AdminServlet adminServlet;
	private HttpServletRequest mockRequest;
	private HttpServletResponse mockResponse;
	private RequestDispatcher mockRequestDispatcher;
	private UserStore mockUserStore;
	private ConversationStore mockConvoStore;
	private MessageStore mockMessageStore;

	@Before
	public void setup() {
		adminServlet = new AdminServlet();

		mockRequest = Mockito.mock(HttpServletRequest.class);
		mockResponse = Mockito.mock(HttpServletResponse.class);
		mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);
		Mockito.when(
				mockRequest.getRequestDispatcher("/WEB-INF/view/admin.jsp"))
				.thenReturn(mockRequestDispatcher);

		mockUserStore = Mockito.mock(UserStore.class);
		adminServlet.setUserStore(UserStore.getInstance());

		mockConvoStore = Mockito.mock(ConversationStore.class);
		adminServlet.setConversationStore(ConversationStore.getInstance());

		mockMessageStore = Mockito.mock(MessageStore.class);
		adminServlet.setMessageStore(MessageStore.getInstance());
	}

	@Test
	public void testDoGet() throws IOException, ServletException {
		// test users = 1, convos = 2, messages = 3
		User user1 = new User(UUID.randomUUID(), "test_name", "password",
				Instant.EPOCH, "about me");
		mockUserStore.addUser(user1);

		Conversation conversation1 = new Conversation(UUID.randomUUID(),
				user1.getId(), "test_name", Instant.EPOCH.plusSeconds(1));
		Conversation conversation2 = new Conversation(UUID.randomUUID(),
				user1.getId(), "test_name2", Instant.EPOCH.plusSeconds(4));
		mockConvoStore.addConversation(conversation1);
		mockConvoStore.addConversation(conversation2);

		Message message1 = new Message(UUID.randomUUID(),
				conversation1.getId(), user1.getId(), "test_message",
				Instant.EPOCH.plusSeconds(2));
		Message message2 = new Message(UUID.randomUUID(),
				conversation2.getId(), user1.getId(), "test_message2",
				Instant.EPOCH.plusSeconds(5));
		Message message3 = new Message(UUID.randomUUID(),
				conversation2.getId(), user1.getId(), "test_message3",
				Instant.EPOCH.plusSeconds(5));
		mockMessageStore.addMessage(message1);
		mockMessageStore.addMessage(message2);
		mockMessageStore.addMessage(message3);

		int mockTotalUsers = mockUserStore.totalUsers();
		int mockTotalConvos = mockConvoStore.totalConvos();
		int mockTotalMessages = mockMessageStore.totalMessages();

		Mockito.when(mockUserStore.totalUsers()).thenReturn(mockTotalUsers);
		Mockito.when(mockConvoStore.totalConvos()).thenReturn(mockTotalConvos);
		Mockito.when(mockMessageStore.totalMessages()).thenReturn(
				mockTotalMessages);

		adminServlet.doGet(mockRequest, mockResponse);

		Mockito.verify(mockRequest).setAttribute("totalUsers", mockTotalUsers);
		Mockito.verify(mockRequest)
				.setAttribute("totalConvos", mockTotalConvos);
		Mockito.verify(mockRequest).setAttribute("totalMessages",
				mockTotalMessages);
		Mockito.verify(mockRequestDispatcher)
				.forward(mockRequest, mockResponse);
	}

}
