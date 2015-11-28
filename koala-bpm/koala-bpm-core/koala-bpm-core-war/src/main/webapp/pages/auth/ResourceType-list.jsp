<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>资源类型管理</title>
<%@ include file="/pages/common/header.jsp" %>
</head>
<body style="padding:10px;height:100%; text-align:center;">
  <div id="maingrid"></div> 
  
  <div id="editUrl" style="display: none;">
	  <form id="form">
	  	<table class="form2column">
	  		<tr><td colspan="2">&nbsp;</td></tr>
	  		<tr>
	  			<td class="label">资源类型名称</td>
	  			<td class="content"><input type="text" id="name" name="name" class="input-common" dataType="Require"  /></td>
	  		</tr>
	  	</table>
	  </form>
  </div>
  
  <script src="${pageContext.request.contextPath}/js/auth/resourceType.js" type="text/javascript"></script>
</body>
</html>
