<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="nurse-question">
	<span class="nurse-question-title">
		간호사 확인여부 :
	</span>
	<span class="nurse-question-item-container">
		<c:set var="nurseResult" value="${nurseResultMap[question.id]}"/>
		<c:choose>
			<c:when test="${!empty nurseResult && nurseResult.checked}">
				<input type=checkbox questionId="${question.id}" nurseCheck="true" checked readOnly>
			</c:when>
			<c:otherwise>
				<input type=checkbox questionId="${question.id}" nurseCheck="true" >
			</c:otherwise>
		</c:choose>
		
	</span>
</div>