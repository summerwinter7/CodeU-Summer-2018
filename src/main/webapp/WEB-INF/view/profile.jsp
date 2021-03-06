<%@ page import="codeu.model.store.basic.UserStore"%>
<%@ page import="codeu.model.data.User"%>

<!DOCTYPE html>
<html>
<head>
<style>
  p {margin:2px; padding: 2px; margin-top:-25px;}
  h3{margin-bottom:0px;}}
</style>

    <title>Profile</title>
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
      <% User user = (User)request.getAttribute("usersProfile");
      if( user != null) { %>
        <h1><%=user.getName()%>'s Profile Page</h1>
        <hr/>
        <form action="/profile" method="POST">
          <h2> About Me </h2>
            <p style="width:800px"><%=user.getAboutMe() %></p>
          <% if (user.getName().equals(request.getSession().getAttribute("user"))) {%>
          <h3> Edit your About Me(only you can see this)</h2>
          <textarea name="aboutMe" rows="5" cols="112"></textarea>
          <button type="submit">Submit</button>
          <% } %>
        </form>
      <%} %>
    </div>
</body>

</html>
