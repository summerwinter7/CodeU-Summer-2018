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
<%@ page import="java.util.List" %>
<%@ page import="codeu.model.data.Conversation" %>
<%@ page import="codeu.model.data.User" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<!DOCTYPE html>
<html>
<head>
  <title>Conversations</title>
  <link rel="stylesheet" href="/css/main.css">
</head>
<body>

  <nav>
    <a id="navTitle" href="/">["hip", "hip"] Chat App</a>
    <a href="/conversations">Conversations</a>
    <% if(request.getSession().getAttribute("user") != null){ %>
      <a>Hello <%= request.getSession().getAttribute("user") %>!</a>
      <a href="/profile/<%=request.getSession().getAttribute("user")%>">Profile</a>
    <% } else{ %>
      <a href="/login">Login</a>
    <% } %>
    <a href="/about.jsp">About</a>
    <a href="/admin">Admin</a>
    <a href="/activityfeed">Activity Feed</a>
  </nav>

  <div id="container">

    <% if(request.getAttribute("error") != null){ %>
        <h2 style="color:red"><%= request.getAttribute("error") %></h2>
    <% } %>

    <% if(request.getSession().getAttribute("user") != null){ %>
      <h1>Create Group Conversation</h1>
      <form action="/conversations" method="POST">
          <div class="form-group">
            <label class="form-control-label">Group Name:</label>
          <input type="text" name="conversationTitle">

          </div>
          <div class="form-group">
            <label for="accessControl">Set Access</label>
            <select name="accessControl" style="width:300px;">
              <option disabled selected value> -- select an access -- </option>
              <option value="Public">Public</option>
              <option value="Private">Private</option>
            </select>
          </div>
           <%List<User> users =
	  		(List<User>) request.getAttribute("ConvoUsers");%>
          <div class="form-group">
            <label for="userLabel">Add Users</label>
            <select name="userLabel" style="width:300px;">
              <option disabled selected value> -- select a user -- </option>
              <% for (User user: users) { %>
            	 <option value="<%=user.getId()%>"><%=user.getName()%> </option>
              <% } %>
            </select>
          </div>
        <button type="submit">Create</button>
      </form>

      <hr/>
    <% } %>

    <h1>Conversations</h1>

    <%
    List<Conversation> privateConversations =
	  (List<Conversation>) request.getAttribute("privateConversations");
    if(request.getSession().getAttribute("user") != null && !privateConversations.isEmpty()) {  %>
    <h4>Private Conversations (Only group members can view):</h4>
    	<ul class="mdl-list">
    	<%
     	  for(Conversation conversation : privateConversations){
   		%>
      		<li><a href="/chat/<%= conversation.getTitle() %>">
        	<%= conversation.getTitle() %></a></li>
    	<%
      	  }
    	%>
      </ul>
    <%} %>

	<h4>Public Conversations (Anyone can view):</h4>
    <%
    List<Conversation> publicConversations =
      (List<Conversation>) request.getAttribute("publicConversations");
    if(publicConversations == null || publicConversations.isEmpty()){
    %>
      <p>Create a conversation to get started.</p>
    <%
    }
    else{
    %>
      <ul class="mdl-list">
    <%
      for(Conversation conversation : publicConversations){
    %>
      <li><a href="/chat/<%= conversation.getTitle() %>">
        <%= conversation.getTitle() %></a></li>
    <%
      }
    %>
      </ul>
    <%
    }
    %>
    <hr/>
  </div>
</body>
</html>
