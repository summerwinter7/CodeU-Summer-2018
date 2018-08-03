// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.controller;

import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;

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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class ChatServletTest {

  private ChatServlet chatServlet;
  private HttpServletRequest mockRequest;
  private HttpSession mockSession;
  private HttpServletResponse mockResponse;
  private RequestDispatcher mockRequestDispatcher;
  private ConversationStore mockConversationStore;
  private MessageStore mockMessageStore;
  private UserStore mockUserStore;

  @Before
  public void setup() {
    chatServlet = new ChatServlet();

    mockRequest = Mockito.mock(HttpServletRequest.class);
    mockSession = Mockito.mock(HttpSession.class);
    Mockito.when(mockRequest.getSession()).thenReturn(mockSession);

    mockResponse = Mockito.mock(HttpServletResponse.class);
    mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);
    Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/chat.jsp"))
        .thenReturn(mockRequestDispatcher);

    mockConversationStore = Mockito.mock(ConversationStore.class);
    chatServlet.setConversationStore(mockConversationStore);

    mockMessageStore = Mockito.mock(MessageStore.class);
    chatServlet.setMessageStore(mockMessageStore);

    mockUserStore = Mockito.mock(UserStore.class);
    chatServlet.setUserStore(mockUserStore);
  }

  @Test
  public void testDoGet_publicConvo() throws IOException, ServletException {
    Mockito.when(mockRequest.getRequestURI()).thenReturn("/chat/test_conversation");

    UUID fakeConversationId = UUID.randomUUID();
    Conversation fakeConversation =
        new Conversation(fakeConversationId, UUID.randomUUID(), "test_conversation", Instant.now(), true);
    Mockito.when(mockConversationStore.getConversationWithTitle("test_conversation"))
        .thenReturn(fakeConversation);
    
    UUID fakeUserId = UUID.randomUUID();
    User fakeUser1 = new User(fakeUserId, "fake_user", "test", Instant.now(), "testing");
    fakeUser1.addConversation(fakeConversationId);
    List<UUID> fakeMembers = fakeConversation.getMembers();
    List<String> fakeUserList = new ArrayList<String>();
    for(UUID fakeMember : fakeMembers){
    	fakeUserList.add(mockUserStore.getUser(fakeMember).getName());
    }
    
    List<Message> fakeMessageList = new ArrayList<>();
    fakeMessageList.add(
        new Message(
            UUID.randomUUID(),
            fakeConversationId,
            UUID.randomUUID(),
            "test message",
            Instant.now()));
    Mockito.when(mockMessageStore.getMessagesInConversation(fakeConversationId))
        .thenReturn(fakeMessageList);

    chatServlet.doGet(mockRequest, mockResponse);

    Mockito.verify(mockRequest).setAttribute("conversation", fakeConversation);
    Mockito.verify(mockRequest).setAttribute("messages", fakeMessageList);
    Mockito.verify(mockRequest).setAttribute("member", fakeUserList);
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }
  
  @Test
  public void testDoGet_privateConvo_NoUser() throws IOException, ServletException {
	Mockito.when(mockRequest.getRequestURI()).thenReturn("/chat/private_conversation");
    Mockito.when(mockSession.getAttribute("user")).thenReturn(null);
    
    UUID privateConversationId = UUID.randomUUID();
    Conversation privateConversation =
        new Conversation(privateConversationId, UUID.randomUUID(), "private_conversation", Instant.now(), false);
    Mockito.when(mockConversationStore.getConversationWithTitle("private_conversation"))
        .thenReturn(privateConversation);

    chatServlet.doGet(mockRequest, mockResponse);
    Mockito.verify(mockResponse).sendRedirect("/conversations");
  }
  
  @Test
  public void testDoGet_privateConvo_UserIsMember() throws IOException, ServletException {
	Mockito.when(mockRequest.getRequestURI()).thenReturn("/chat/private_conversation");
    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_username");
    
    User test_user =
            new User(
                UUID.randomUUID(),
                "test username",
                "$2a$10$.e.4EEfngEXmxAO085XnYOmDntkqod0C384jOR9oagwxMnPNHaGLa",
                Instant.now(), "test_aboutMe");
    List<UUID> members = new ArrayList<>();
    List<String> memberNames = new ArrayList<>();
    members.add(test_user.getId());
    memberNames.add(test_user.getName());
    
    UUID privateConversationId = UUID.randomUUID();
    Conversation privateConversation =
        new Conversation(privateConversationId, UUID.randomUUID(), "private_conversation", Instant.now(), false);
    privateConversation.setMembers(members);
    test_user.addConversation(privateConversation.getId());
    Mockito.when(mockConversationStore.getConversationWithTitle("private_conversation"))
        .thenReturn(privateConversation);
    Mockito.when(mockUserStore.getUser(test_user.getId())).thenReturn(test_user);
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(test_user);
    
    List<Message> fakeMessageList = new ArrayList<>();
    fakeMessageList.add(
        new Message(
            UUID.randomUUID(),
            privateConversationId,
            UUID.randomUUID(),
            "test message",
            Instant.now()));
    Mockito.when(mockMessageStore.getMessagesInConversation(privateConversationId))
        .thenReturn(fakeMessageList);


    chatServlet.doGet(mockRequest, mockResponse);
    Mockito.verify(mockRequest).setAttribute("conversation", privateConversation);
    Mockito.verify(mockRequest).setAttribute("messages", fakeMessageList);
    Mockito.verify(mockRequest).setAttribute("member", memberNames);
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }

  @Test
  public void testDoGet_badConversation() throws IOException, ServletException {
    Mockito.when(mockRequest.getRequestURI()).thenReturn("/chat/bad_conversation");
    Mockito.when(mockConversationStore.getConversationWithTitle("bad_conversation"))
        .thenReturn(null);

    chatServlet.doGet(mockRequest, mockResponse);

    Mockito.verify(mockResponse).sendRedirect("/conversations");
  }

  @Test
  public void testDoPost_UserNotLoggedIn() throws IOException, ServletException {
    Mockito.when(mockSession.getAttribute("user")).thenReturn(null);

    chatServlet.doPost(mockRequest, mockResponse);

    Mockito.verify(mockMessageStore, Mockito.never()).addMessage(Mockito.any(Message.class));
    Mockito.verify(mockResponse).sendRedirect("/login");
  }

  @Test
  public void testDoPost_InvalidUser() throws IOException, ServletException {
    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_username");
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(null);

    chatServlet.doPost(mockRequest, mockResponse);

    Mockito.verify(mockMessageStore, Mockito.never()).addMessage(Mockito.any(Message.class));
    Mockito.verify(mockResponse).sendRedirect("/login");
  }

  @Test
  public void testDoPost_ConversationNotFound() throws IOException, ServletException {
    Mockito.when(mockRequest.getRequestURI()).thenReturn("/chat/test_conversation");
    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_username");

    User fakeUser =
        new User(
            UUID.randomUUID(),
            "test_username",
            "$2a$10$bBiLUAVmUFK6Iwg5rmpBUOIBW6rIMhU1eKfi3KR60V9UXaYTwPfHy",
            Instant.now(),"test_aboutMe");
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(fakeUser);

    Mockito.when(mockConversationStore.getConversationWithTitle("test_conversation"))
        .thenReturn(null);

    chatServlet.doPost(mockRequest, mockResponse);

    Mockito.verify(mockMessageStore, Mockito.never()).addMessage(Mockito.any(Message.class));
    Mockito.verify(mockResponse).sendRedirect("/conversations");
  }

  @Test
  public void testDoPost_StoresMessage() throws IOException, ServletException {
    Mockito.when(mockRequest.getRequestURI()).thenReturn("/chat/test_conversation");
    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_username");

    User fakeUser =
        new User(
            UUID.randomUUID(),
            "test_username",
            "$2a$10$bBiLUAVmUFK6Iwg5rmpBUOIBW6rIMhU1eKfi3KR60V9UXaYTwPfHy",
            Instant.now(), "test_aboutMe");
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(fakeUser);

    Conversation fakeConversation =
        new Conversation(UUID.randomUUID(), UUID.randomUUID(), "test_conversation", Instant.now(), true);
    Mockito.when(mockConversationStore.getConversationWithTitle("test_conversation"))
        .thenReturn(fakeConversation);

    Mockito.when(mockRequest.getParameter("message")).thenReturn("Test message.");

    chatServlet.doPost(mockRequest, mockResponse);

    ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);
    Mockito.verify(mockMessageStore).addMessage(messageArgumentCaptor.capture());
    Assert.assertEquals("Test message.", messageArgumentCaptor.getValue().getContent());

    Mockito.verify(mockResponse).sendRedirect("/chat/test_conversation");
  }

  @Test
  public void testDoPost_CleansHtmlContent() throws IOException, ServletException {
    Mockito.when(mockRequest.getRequestURI()).thenReturn("/chat/test_conversation");
    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_username");

    User fakeUser =
        new User(
            UUID.randomUUID(),
            "test_username",
            "$2a$10$eDhncK/4cNH2KE.Y51AWpeL8/5znNBQLuAFlyJpSYNODR/SJQ/Fg6",
            Instant.now(), "test_aboutMe");
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(fakeUser);

    Conversation fakeConversation =
        new Conversation(UUID.randomUUID(), UUID.randomUUID(), "test_conversation", Instant.now(), true);
    Mockito.when(mockConversationStore.getConversationWithTitle("test_conversation"))
        .thenReturn(fakeConversation);

    Mockito.when(mockRequest.getParameter("message"))
        .thenReturn("Contains <b>html</b> and <script>JavaScript</script> content.");

    chatServlet.doPost(mockRequest, mockResponse);

    ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);
    Mockito.verify(mockMessageStore).addMessage(messageArgumentCaptor.capture());
    Assert.assertEquals(
        "Contains html and  content.", messageArgumentCaptor.getValue().getContent());

    Mockito.verify(mockResponse).sendRedirect("/chat/test_conversation");
  }
  
  @Test
  public void testDoPost_AddUser() throws IOException, ServletException {
    Mockito.when(mockRequest.getRequestURI()).thenReturn("/chat/test_conversation");
    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_username");

    User fakeUser =
        new User(
            UUID.randomUUID(),
            "test_username",
            "$2a$10$bBiLUAVmUFK6Iwg5rmpBUOIBW6rIMhU1eKfi3KR60V9UXaYTwPfHy",
            Instant.now(), "test_aboutMe");
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(fakeUser);
    
    User fakeUser2 =
        new User(
            UUID.randomUUID(),
            "test_username2",
            "$2a$10$eDhncK/4cNH2KE.Y51AWpeL8/5znNBQLuAFlyJpSYNODR/SJQ/Fg6",
            Instant.now(), "test_aboutMe2");
    Mockito.when(mockUserStore.getUser(fakeUser2.getId())).thenReturn(fakeUser2);

    Conversation fakeConversation =
        new Conversation(UUID.randomUUID(), UUID.randomUUID(), "test_conversation", Instant.now(), true);
    Mockito.when(mockConversationStore.getConversationWithTitle("test_conversation"))
        .thenReturn(fakeConversation);

    Mockito.when(mockRequest.getParameter("message")).thenReturn(null);
    Mockito.when(mockRequest.getParameter("userLabel")).thenReturn(fakeUser2.getId().toString());

    chatServlet.doPost(mockRequest, mockResponse);

    Mockito.verify(mockConversationStore).updateConversation(fakeConversation);
    Assert.assertTrue(fakeConversation.getMembers().contains(fakeUser2.getId()));
    Assert.assertTrue(fakeUser2.getConversations().contains(fakeConversation.getId()));

    Mockito.verify(mockResponse).sendRedirect("/chat/test_conversation");
  }
}
