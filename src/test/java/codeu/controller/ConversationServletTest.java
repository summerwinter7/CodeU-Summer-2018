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
import codeu.model.data.User;
import codeu.model.store.basic.ConversationStore;
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

public class ConversationServletTest {

  private ConversationServlet conversationServlet;
  private HttpServletRequest mockRequest;
  private HttpSession mockSession;
  private HttpServletResponse mockResponse;
  private RequestDispatcher mockRequestDispatcher;
  private ConversationStore mockConversationStore;
  private UserStore mockUserStore;

  @Before
  public void setup() {
    conversationServlet = new ConversationServlet();

    mockRequest = Mockito.mock(HttpServletRequest.class);
    mockSession = Mockito.mock(HttpSession.class);
    Mockito.when(mockRequest.getSession()).thenReturn(mockSession);

    mockResponse = Mockito.mock(HttpServletResponse.class);
    mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);
    Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/conversations.jsp"))
        .thenReturn(mockRequestDispatcher);

    mockConversationStore = Mockito.mock(ConversationStore.class);
    conversationServlet.setConversationStore(mockConversationStore);

    mockUserStore = Mockito.mock(UserStore.class);
    conversationServlet.setUserStore(mockUserStore);
  }

  @Test
  public void testDoGet() throws IOException, ServletException {


    List<User> fakeUserList = new ArrayList<>();
    fakeUserList.add(
      new User(UUID.randomUUID(),"test_username", "$2a$10$eDhncK/4cNH2KE.Y51AWpeL8/5znNBQLuAFlyJpSYNODR/SJQ/Fg6",
      Instant.now(), "test_aboutMe"));
      Mockito.when(mockUserStore.getAllUsers()).thenReturn(fakeUserList);

    List<Conversation> fakeConversationListPublic = new ArrayList<>();
    fakeConversationListPublic.add(
        new Conversation(UUID.randomUUID(), UUID.randomUUID(), "test_conversation_public", Instant.now(), true));
    Mockito.when(mockConversationStore.getAllPublicConversations()).thenReturn(fakeConversationListPublic);

    //tests the private conversation portion
    List<Conversation> fakeConversationListPrivate = new ArrayList<Conversation>();
    List<UUID> fakeConvoListIds = new ArrayList<UUID>();
    Conversation convo = new Conversation(UUID.randomUUID(), UUID.randomUUID(), "test_conversation_private", Instant.now(), false);
    fakeConversationListPrivate.add(convo);
    fakeConvoListIds.add(convo.getId());
    Mockito.when(mockConversationStore.getConversationWithID(convo.getId())).thenReturn(convo);
    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_username");
    User fakeUser =
        new User(
            UUID.randomUUID(),
            "test_username",
            "$2a$10$eDhncK/4cNH2KE.Y51AWpeL8/5znNBQLuAFlyJpSYNODR/SJQ/Fg6",
            Instant.now(), "test_aboutMe");
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(fakeUser);
    fakeUser.setConversations(fakeConvoListIds);
    conversationServlet.doGet(mockRequest, mockResponse);

    Mockito.verify(mockRequest).setAttribute("publicConversations", fakeConversationListPublic);
    Mockito.verify(mockRequest).setAttribute("privateConversations", fakeConversationListPrivate);
    Mockito.verify(mockRequest).setAttribute("ConvoUsers", fakeUserList);

    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);

  }

  @Test
  public void testDoPost_UserNotLoggedIn() throws IOException, ServletException {
    Mockito.when(mockSession.getAttribute("user")).thenReturn(null);

    conversationServlet.doPost(mockRequest, mockResponse);

    Mockito.verify(mockConversationStore, Mockito.never())
        .addConversation(Mockito.any(Conversation.class));
    Mockito.verify(mockResponse).sendRedirect("/conversations");
  }

  @Test
  public void testDoPost_InvalidUser() throws IOException, ServletException {
    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_username");
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(null);

    conversationServlet.doPost(mockRequest, mockResponse);

    Mockito.verify(mockConversationStore, Mockito.never())
        .addConversation(Mockito.any(Conversation.class));
    Mockito.verify(mockResponse).sendRedirect("/conversations");
  }

  @Test
  public void testDoPost_BadConversationName() throws IOException, ServletException {
    Mockito.when(mockRequest.getParameter("conversationTitle")).thenReturn("bad !@#$% name");
    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_username");

    User fakeUser =
        new User(
            UUID.randomUUID(),
            "test_username",
            "$2a$10$eDhncK/4cNH2KE.Y51AWpeL8/5znNBQLuAFlyJpSYNODR/SJQ/Fg6",
            Instant.now(), "test_aboutMe");
    List<User> users = new ArrayList<User>();
    users.add(fakeUser);
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(fakeUser);
    Mockito.when(mockUserStore.getAllUsers()).thenReturn(users);

    conversationServlet.doPost(mockRequest, mockResponse);

    Mockito.verify(mockConversationStore, Mockito.never())
        .addConversation(Mockito.any(Conversation.class));
    Mockito.verify(mockRequest).setAttribute("error", "Please enter only letters and numbers.");
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
    Mockito.verify(mockRequest).setAttribute("ConvoUsers", users);
  }

  @Test
  public void testDoPost_NullConversationName() throws IOException, ServletException {
    Mockito.when(mockRequest.getParameter("conversationTitle")).thenReturn("");
    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_username");

    User fakeUser =
        new User(
            UUID.randomUUID(),
            "test_username",
            "$2a$10$eDhncK/4cNH2KE.Y51AWpeL8/5znNBQLuAFlyJpSYNODR/SJQ/Fg6",
            Instant.now(), "test_aboutMe");
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(fakeUser);
    List<User> users = new ArrayList<User>();
    users.add(fakeUser);
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(fakeUser);
    Mockito.when(mockUserStore.getAllUsers()).thenReturn(users);

    conversationServlet.doPost(mockRequest, mockResponse);

    Mockito.verify(mockConversationStore, Mockito.never())
        .addConversation(Mockito.any(Conversation.class));
    Mockito.verify(mockRequest).setAttribute("error", "Conversation name cannot be empty");
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
    Mockito.verify(mockRequest).setAttribute("ConvoUsers", users);
  }
  
  @Test
  public void testDoPost_NoAccessControl() throws IOException, ServletException {
    Mockito.when(mockRequest.getParameter("conversationTitle")).thenReturn("test_title");
    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_username");
    Mockito.when(mockRequest.getParameter("accessControl")).thenReturn(null);

    User fakeUser =
        new User(
            UUID.randomUUID(),
            "test_username",
            "$2a$10$eDhncK/4cNH2KE.Y51AWpeL8/5znNBQLuAFlyJpSYNODR/SJQ/Fg6",
            Instant.now(), "test_aboutMe");
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(fakeUser);
    List<User> users = new ArrayList<User>();
    users.add(fakeUser);
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(fakeUser);
    Mockito.when(mockUserStore.getAllUsers()).thenReturn(users);

    conversationServlet.doPost(mockRequest, mockResponse);

    Mockito.verify(mockConversationStore, Mockito.never())
        .addConversation(Mockito.any(Conversation.class));
    Mockito.verify(mockRequest).setAttribute("error", "You must select an access control");
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
    Mockito.verify(mockRequest).setAttribute("ConvoUsers", users);
  }

  @Test
  public void testDoPost_ConversationNameTaken() throws IOException, ServletException {
    Mockito.when(mockRequest.getParameter("conversationTitle")).thenReturn("test_conversation");
    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_username");
    Mockito.when(mockRequest.getParameter("accessControl")).thenReturn("Public");

    User fakeUser =
        new User(
            UUID.randomUUID(),
            "test_username",
            "$2a$10$eDhncK/4cNH2KE.Y51AWpeL8/5znNBQLuAFlyJpSYNODR/SJQ/Fg6",
            Instant.now(), "test_aboutMe");
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(fakeUser);
    
    Mockito.when(mockConversationStore.isTitleTaken("test_conversation")).thenReturn(true);

    conversationServlet.doPost(mockRequest, mockResponse);

    Mockito.verify(mockConversationStore, Mockito.never())
        .addConversation(Mockito.any(Conversation.class));
    Mockito.verify(mockResponse).sendRedirect("/chat/test_conversation");
  }

  @Test
  public void testDoPost_NewConversationPublic() throws IOException, ServletException {
    Mockito.when(mockRequest.getParameter("conversationTitle")).thenReturn("test_conversation");
    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_username");

    User fakeUser =
        new User(
            UUID.randomUUID(),
            "test_username",
            "$2a$10$eDhncK/4cNH2KE.Y51AWpeL8/5znNBQLuAFlyJpSYNODR/SJQ/Fg6",
            Instant.now(), "test_aboutMe");
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(fakeUser);

    Mockito.when(mockConversationStore.isTitleTaken("test_conversation")).thenReturn(false);
    Mockito.when(mockRequest.getParameter("accessControl")).thenReturn("Public");
    Mockito.when(mockUserStore.getUser(fakeUser.getId())).thenReturn(fakeUser);
    Mockito.when(mockRequest.getParameter("userLabel")).thenReturn(fakeUser.getId().toString());


    conversationServlet.doPost(mockRequest, mockResponse);

    ArgumentCaptor<Conversation> conversationArgumentCaptor =
        ArgumentCaptor.forClass(Conversation.class);
    Mockito.verify(mockConversationStore).addConversation(conversationArgumentCaptor.capture());
    Assert.assertEquals(conversationArgumentCaptor.getValue().getTitle(), "test_conversation");
    Assert.assertEquals(conversationArgumentCaptor.getValue().getIsPublic(), true);

    Mockito.verify(mockResponse).sendRedirect("/chat/test_conversation");
  }
  
  @Test
  public void testDoPost_NewConversationPrivate() throws IOException, ServletException {
    Mockito.when(mockRequest.getParameter("conversationTitle")).thenReturn("test_conversation");
    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_username");

    User fakeUser =
        new User(
            UUID.randomUUID(),
            "test_username",
            "$2a$10$eDhncK/4cNH2KE.Y51AWpeL8/5znNBQLuAFlyJpSYNODR/SJQ/Fg6",
            Instant.now(), "test_aboutMe");
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(fakeUser);
    
    User fakeUser2 =
            new User(
                UUID.randomUUID(),
                "test_username2",
                "$2a$10$eDhncK/4cNH2KE.Y51AWpeL8/5znNBQLuAFlyJpSYNODR/SJQ/Fg6",
                Instant.now(), "test_aboutMe2");
    Mockito.when(mockUserStore.getUser(fakeUser2.getId())).thenReturn(fakeUser2);

    Mockito.when(mockConversationStore.isTitleTaken("test_conversation")).thenReturn(false);
    Mockito.when(mockRequest.getParameter("accessControl")).thenReturn("Private");
    Mockito.when(mockUserStore.getUser(fakeUser.getId())).thenReturn(fakeUser);
    Mockito.when(mockRequest.getParameter("userLabel")).thenReturn(fakeUser2.getId().toString());


    conversationServlet.doPost(mockRequest, mockResponse);

    ArgumentCaptor<Conversation> conversationArgumentCaptor =
        ArgumentCaptor.forClass(Conversation.class);
    Mockito.verify(mockConversationStore).addConversation(conversationArgumentCaptor.capture());
    Assert.assertEquals(conversationArgumentCaptor.getValue().getTitle(), "test_conversation");
    Assert.assertEquals(conversationArgumentCaptor.getValue().getIsPublic(), false);
    Assert.assertTrue((conversationArgumentCaptor.getValue().getMembers()).contains(fakeUser.getId()));
    Assert.assertTrue((conversationArgumentCaptor.getValue().getMembers()).contains(fakeUser2.getId()));

    Mockito.verify(mockResponse).sendRedirect("/chat/test_conversation");
  }
  
  @Test
  public void testDoPost_NewConversation_NoUsers() throws IOException, ServletException {
    Mockito.when(mockRequest.getParameter("conversationTitle")).thenReturn("test_conversation");
    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_username");

    User fakeUser =
        new User(
            UUID.randomUUID(),
            "test_username",
            "$2a$10$eDhncK/4cNH2KE.Y51AWpeL8/5znNBQLuAFlyJpSYNODR/SJQ/Fg6",
            Instant.now(), "test_aboutMe");
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(fakeUser);

    Mockito.when(mockConversationStore.isTitleTaken("test_conversation")).thenReturn(false);
    Mockito.when(mockRequest.getParameter("accessControl")).thenReturn("Private");
    Mockito.when(mockUserStore.getUser(fakeUser.getId())).thenReturn(fakeUser);
    Mockito.when(mockRequest.getParameter("userLabel")).thenReturn(null);

    conversationServlet.doPost(mockRequest, mockResponse);

    ArgumentCaptor<Conversation> conversationArgumentCaptor =
        ArgumentCaptor.forClass(Conversation.class);
    Mockito.verify(mockConversationStore).addConversation(conversationArgumentCaptor.capture());
    Assert.assertEquals(conversationArgumentCaptor.getValue().getTitle(), "test_conversation");
    Assert.assertEquals(conversationArgumentCaptor.getValue().getIsPublic(), false);
    Assert.assertTrue((conversationArgumentCaptor.getValue().getMembers()).contains(fakeUser.getId()));

    Mockito.verify(mockResponse).sendRedirect("/chat/test_conversation");
  }
}
