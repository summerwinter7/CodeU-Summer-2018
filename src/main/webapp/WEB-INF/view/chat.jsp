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
<%@ page import="codeu.model.data.Message" %>
<%@ page import="codeu.model.data.User" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%
Conversation conversation = (Conversation) request.getAttribute("conversation");
List<Message> messages = (List<Message>) request.getAttribute("messages");
List<User> users = (List<User>) request.getAttribute("users");
List<String> members = (List<String>)request.getAttribute("member");
%>

<!DOCTYPE html>
<html>
<head>
  <title><%= conversation.getTitle() %></title>
  <link rel="stylesheet" href="/css/main.css" type="text/css">

  <style>
    #chat {
      background-color: white;
      height: 500px;
      overflow-y: scroll
    }
  </style>

  <script>
    // scroll the chat div to the bottom
    function scrollChat() {
      var chatDiv = document.getElementById('chat');
      chatDiv.scrollTop = chatDiv.scrollHeight;
    };
  </script>
</head>
<body onload="scrollChat()">

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
    <a href="/admin.jsp">Admin</a>
    <a href="/activityfeed">Activity Feed</a>
  </nav>


  <div id="container">

    <h1><%= conversation.getTitle() %>
      <a href="" style="float: right">&#8635;</a></h1>
      
    <% if(conversation.getIsPublic() == false) { %>
    <div id="members">
    	<h4>Members:  
    	<%for (int i=0; i<members.size(); i++) {
    		String member = members.get(i);
    		if (i<members.size()-1) { %>
    			<a href="/profile/<%= member %>"><%= member %></a>,
    		<%} else { %>
    			<a href="/profile/<%= member %>"><%= member %></a>
    		<%}
    	}%>
    	</h4>
  	</div>
	<% } %>
      
    <% if((request.getSession().getAttribute("user") != null)&& 
    		!conversation.getIsPublic()){ %>
       <form action="/chat/<%= conversation.getTitle() %>" method="POST">
      <%List<User> convoUsers =
	  		(List<User>) request.getAttribute("ConvoUsers");%>
          <div class="form-group">
            <label for="userLabel">Add Member</label>
            <select name="userLabel" style="width:300px;">
              <option disabled selected value> -- select a user -- </option>
              <% for (User user: convoUsers) { %>
            	 <option value="<%=user.getId()%>"><%=user.getName()%> </option>
              <% } %>
            </select>
          </div>
        <button type="submit">Add</button>
      </form>
    <%} %>

    <hr/>

    <div id="chat">
      <ul>
    <%
      for (Message message : messages) {
        String author = UserStore.getInstance()
          .getUser(message.getAuthorId()).getName();
    %>
      <li><strong><a href="/profile/<%= author %>">
        <%= author %></a>:</strong> <%= message.getContent() %></li>
    <%
      }
    %>
      </ul>
    </div>

    <hr/>

    <% if (request.getSession().getAttribute("user") != null) { %>
    <form action="/chat/<%= conversation.getTitle() %>" method="POST">
        <input  type="text" name="message">
        <br/>
        <button type="submit">Send</button>
    </form>
    <% } else { %>
      <p><a href="/login">Login</a> to send a message.</p>
    <% } %>

    <hr/>

  </div>
</body>
</html>
