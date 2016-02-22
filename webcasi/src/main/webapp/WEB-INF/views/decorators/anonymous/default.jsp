<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.springframework.context.*" %>
<%@ page import="org.springframework.web.context.support.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/page" prefix="page" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Content-Script-Type" content="text/javascript" />
	<meta http-equiv="Content-Style-Type" content="text/css" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<title>Web CASI</title>
	<script>
		var _requestPath = "${pageContext.request.contextPath}";
		
	</script>
	
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/jquery/smoothness/jquery-ui-1.9.1.custom.min.css"/>	
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/script/jquery/fancybox/jquery.fancybox.css"/>	
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/common.css"/>	
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery-1.8.2.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery-ui-1.9.1.custom.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/fancybox/jquery.fancybox.pack.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/anonymous.js"></script>
	<decorator:head />
</head>
<body>
	<div id="main-container">
		<div id="main-header">
			<div class="width-1024">
				<div id="header-logo">로고영역</div>
				<div id="header-login">
				<c:url value="/loginAction" var="loginUrl"/>
					<form id="loginForm" action="${loginUrl}" method="POST">
						<input type=hidden id="j_username" name="j_username"/>
						<span>
							<label>주민번호 : </label>
							<input type=text id="resno1" name="resno1" maxlength=6 size=6/> - 
							<input type=text id="resno2" name="resno2" maxlength=7 size=7/>
						</span>
						<span>
							<label>비밀번호 :</label>
							<input type=password id="j_password" name="j_password" size="13" readonly/>
							<input type=button value="로그인" onclick="javascript:_login();"/>
						</span>
					</form>
				</div>
			</div>
		</div>
		<div id="main-body-wrapper">
			<div class="width-1024">
				<div id="main-left">
					<%@ include file="/WEB-INF/views/decorators/leftMenu.jspf" %>
				</div>		
				<div id="main-contents">
					<decorator:body />
				</div>

			</div>
		</div>
		<div id="main-footer">
			<div class="width-1024">
				<%@ include file="/WEB-INF/views/decorators/footer.jspf" %>
			</div>
		</div>
	</div>
	<div id="password-popup">
		<h2>비밀번호 설정</h2>
		<form id="passwordSetting" method="POST">
		<div>
			<label>비밀번호 : </label>
			<input type="password" id="nPassword" name="nPassword"/>
		</div>
		<div>
			<label>비밀번호 확인 : </label>
			<input type="password" id="rPassword" name="rPassword"/>
		</div>		
		<div>
			<input type=button value="설정하기" onclick="javascript:submitPassword();"/>
		</div>
		</form>
	</div>
</body>
</html>