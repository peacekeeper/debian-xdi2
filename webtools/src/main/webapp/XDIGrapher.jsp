<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="xdi2.core.properties.XDI2Properties" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>XDI Grapher</title>
<link rel="stylesheet" target="_blank" href="style.css" TYPE="text/css" MEDIA="screen">
</head>
<body>
	<div id="imgtop"><img id="imgtopleft" src="images/xdi2-topleft.png"><a href="http://projectdanube.org/"><img id="imgtopright" src="images/xdi2-topright.png"></a></div>
	<div id="main">
	<div class="header">
	<span id="appname"><img src="images/app20b.png"> XDI Grapher</span>
	&nbsp;&nbsp;&nbsp;&nbsp;Examples: 
	<% for (int i=0; i<((Integer) request.getAttribute("sampleInputs")).intValue(); i++) { %>
		<a href="XDIGrapher?sample=<%= i+1 %>"><%= i+1 %></a>&nbsp;&nbsp;
	<% } %>
	<a href="index.jsp">&gt;&gt;&gt; Other Apps...</a><br>
	This is version <%= XDI2Properties.properties.getProperty("project.version") %> <%= XDI2Properties.properties.getProperty("project.build.timestamp") %>, Git commit <%= XDI2Properties.properties.getProperty("git.commit.id").substring(0,6) %> <%= XDI2Properties.properties.getProperty("git.commit.time") %>.
	</div>

	<% if (request.getAttribute("error") != null) { %>
			
		<p style="font-family: monospace; white-space: pre; color: red;"><%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %></p>

	<% } %>

	<p>Note: For a much more advanced XDI graphing tool, see <a href="http://neustar.github.io/xdi-grapheditor/xdi-grapheditor/public_html/index.html">here</a>.

	<form action="XDIGrapher" method="post" accept-charset="UTF-8">

		<textarea class="input" name="input" style="width: 100%" rows="12"><%= request.getAttribute("input") != null ? request.getAttribute("input") : "" %></textarea><br>

		<% String type = (String) request.getAttribute("type"); if (type == null) type = ""; %>

		<p>
		<select name="type">
		<option value="d1" <%= type.equals("d1") ? "selected" : "" %>>D3 Tree</option>
		<option value="d2" <%= type.equals("d2") ? "selected" : "" %>>JUNG KKLayout</option>
		<option value="d3" <%= type.equals("d3") ? "selected" : "" %>>JUNG FRLayout2</option>
		<option value="d4" <%= type.equals("d4") ? "selected" : "" %>>JUNG ISOMLayout</option>
		</select>
		<input type="submit" value="Draw!">
		&nbsp;&nbsp;&nbsp;&nbsp;<a href="XDIGrapherHelp.jsp">What can I do here?</a>
		</p>

	</form>

	<div class="line"></div>

	<% if (request.getAttribute("stats") != null) { %>
		<p>
		<%= request.getAttribute("stats") %>
		</p>
	<% } %>

	<% if (type.equals("d2") || type.equals("d3") || type.equals("d4")) { %>

		<div>
			<img src="/XDIGrapherImage?graphId=<%= request.getAttribute("graphId") %>">
		</div><br>

	<% } else if (type.equals("d1")) { %>
	
		<script type="text/javascript" src="jquery-1.6.4.min.js"></script>
		<script type="text/javascript" src="jquery.iframe-auto-height.plugin.1.5.0.min.js"></script>
	
	 	<iframe src="d3tree.html?graphId=<%= request.getAttribute("graphId") %>" frameborder="2" scrolling="no" width="100%"></iframe>
	
		<script type="text/javascript">jQuery('iframe').iframeAutoHeight({ minHeight: 300 });</script>

	<% } %>

	</div>
</body>
</html>
