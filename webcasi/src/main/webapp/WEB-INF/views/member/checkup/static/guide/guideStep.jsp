<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/page" prefix="page" %>
<%@ taglib uri="http://myhealth.kbsmc.co.kr/taglibs" prefix="webcasi" %>

<div id="question-step">
	<div class="question-step-top">
		<img src="${pageContext.request.contextPath}/resources/images/page_right/rightmenu_top.png"/>
	</div>
	<div class="question-category-name">건진안내문</div>
	<div class="question-step_body">
		<c:forEach var="guideRow" items="${guideSteps}" varStatus="status">
			<c:choose>
				<c:when test="${guideRow.path eq _currentPath}">
					<c:set var="questionGroupRowClass" value="step-row-active"/>
				</c:when>
				<c:when test="${guideRow.path > _currentPath}">
					<c:set var="questionGroupRowClass" value="step-row-black"/>
				</c:when>
				<c:otherwise>
					<c:set var="questionGroupRowClass" value=""/>
				</c:otherwise>		
			</c:choose>
			<div class="question-group-row ${questionGroupRowClass}">
				<span>
					<a href="javascript:goGuide('${guideRow.url}');"><c:out value="${webcasi:substring(guideRow.title,13)}"/></a>
				</span>
			</div>
		</c:forEach>
	</div>
	<div class="question-step-bottom">
		<img src="${pageContext.request.contextPath}/resources/images/page_right/rightmenu_bottom.png"/>
	</div>
</div>