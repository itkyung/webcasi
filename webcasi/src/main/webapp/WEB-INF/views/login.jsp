<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>



<html>
<head>
	<title>Login Form</title>
</head>
<body>
	<c:url value="/loginAction" var="loginUrl"/>
	<h2>삼성병원 문진시스템 간호사,어드민 로그인 페이지</h2>
	<form action="${loginUrl}" method="POST">
		아이디 : <input type="text" name="j_username" value="admin" size="20" maxlength="50"/>
		비밀번호 : <input type="password" name="j_password" value="admin1234" size="20"/>
		<input type="submit" value="login"/>
	</form>
	<c:if test="${not empty SPRING_SECURITY_LAST_EXCEPTION.message}">
		<ul>
			<li>Error : ${SPRING_SECURITY_LAST_EXCEPTION.message}</li>
		</ul>
		<c:remove var = "SPRING_SECURITY_LAST_EXCEPTION" scope = "session" />
	</c:if>
</body>
</html>