<%--
  Copyright 2017 Google Inc.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
--%>
<%-- Imports needed for Admin Page --%>

<%-- Number of users --%>
<%@ page import="codeu.model.data.User" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<% 
	UserStore us = UserStore.getInstance();
	Integer totalUsers = us.totalNumberOfUsers();
%>

<%-- Number of Convos --%>
<%@ page import="codeu.model.store.basic.ConversationStore" %>
<%@ page import="codeu.model.data.Conversation" %>
<% 
	ConversationStore cs = ConversationStore.getInstance();
	Integer totalConvos = cs.totalNumberOfConversations();
%>

<%-- Number of Messages --%>
<%@ page import="codeu.model.data.Message" %>
<%@ page import="codeu.model.store.basic.MessageStore"%>
<% 
	MessageStore ms = MessageStore.getInstance();
	Integer totalConvos = ms.totalNumberOfMessages();
%>



<!DOCTYPE html>
<html>
<head>
  <title>Admin Page</title>
  <link rel="stylesheet" href="/css/main.css">
</head>
<body>

  <nav>
    <a id="navTitle" href="/">CodeU Chat App</a>
    <a href="/conversations">Conversations</a>
    <% if(request.getSession().getAttribute("user") != null){ %>
      <a>Hello <%= request.getSession().getAttribute("user") %>!</a>
    <% } else{ %>
      <a href="/login">Login</a>
    <% } %>
    <a href="/about.jsp">About</a>
    <a href="/admin.jsp">Admin</a>
  </nav>

  <div id="container">
    <div
      style="width:75%; margin-left:auto; margin-right:auto; margin-top: 50px;">

      <h1>Administration</h1>
      <h2>Site Statistics</h2>
      <p>
   		Here are some site stats:
      </p>

      <ul>
        <li>Users: <%=totalUsers %> </li>
        <li>Conversations: <%=totalConvos %></li>
        <li>Messages: <%=totalMessages %> </li>
      </ul>
    </div>
  </div>
</body>
</html>
