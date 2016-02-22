<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/page" prefix="page" %>
<%@ taglib uri="http://myhealth.kbsmc.co.kr/taglibs" prefix="webcasi" %>

<div id="question-desc">
	<div class="question-desc-body-top">
		<img src="${pageContext.request.contextPath}/resources/images/page_right/help_01.png"/>	
	</div>
	<div class="question-desc-body">
		<a href="javascript:viewHelp('${questionGroup.id}')">${webcasi:substring(questionGroup.description,74)}</a>
	</div>
	<div class="question-desc-img">
		<img src="${pageContext.request.contextPath}/resources/images/page_right/help_${_currentCategoryType}.png"/>
	</div>
</div>