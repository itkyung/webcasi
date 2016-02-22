<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.springframework.context.*" %>
<%@ page import="org.springframework.web.context.support.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/page" prefix="page" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="Content-Script-Type" content="text/javascript" />
	<meta http-equiv="Content-Style-Type" content="text/css" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<title>Web CASI-Admin</title>
	<script>
		var _requestPath = "${pageContext.request.contextPath}";
		function goMasterList() {
			location.replace(_requestPath+"/admin/goMasterList");
		};
		function goNurseAccountList() {
			location.replace(_requestPath+"/admin/goNurseAccountList");
		};
		function goPatientList() {
			location.replace(_requestPath+"/admin/goPatientAccountList");
		};
	</script>	
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/jquery/smoothness/jquery-ui-1.9.1.custom.min.css"/>	
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/script/jquery/fancybox/jquery.fancybox.css"/>	
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/common.css"/>	
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/admin.css"/>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery-1.8.2.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery-ui-1.9.1.custom.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/fancybox/jquery.fancybox.pack.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/common.js"></script>
	<decorator:head />
</head>
<c:url value="/admin/nurseManagement" var="nurseManagement"/>
<c:url value="/logout" var="logoutUrl"/>
<body>
	<div id="main-container">
		<div id="main-header">
			<div class="width-1024">
				<div id="header-logo"></div>
				<h3>
				<div id="header-login">
					<span>
	                    <a href="#" onclick="goMasterList();">문진관리</a>&nbsp;&nbsp;&nbsp;
	                </span>            
	                <!--span>
	                    <a href="#" onclick="goNurseAccountList();">간호사계정관리</a>&nbsp;&nbsp;&nbsp;
	                </span-->
	                <!--span>
	                    <a href="${pageContext.request.contextPath}/admin/questionGroupList?masterId=1&categoryType=" >질문그룹</a>&nbsp;&nbsp;&nbsp;
	                </span-->             
	                <span>
	                    <a href="#" onclick="goPatientList();">환자계정관리</a>&nbsp;&nbsp;&nbsp;
	                </span>
	                <span>
	                    <a href="${logoutUrl}">로그아웃</a>
	                </span>
				</div>
				</h3>
			</div>
		</div>
		<div id="main-body-wrapper">
			<div class="width-1024">
				<div id="main-contents-admin">
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
</body>
</html>