<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="nurse-question">
	<span class="nurse-question-title">
		간호사 확인여부 :
	</span>
	<span class="nurse-question-item-container">
		<c:set var="nurseResult" value="${nurseResultMap[question.id]}"/>
		<c:forEach var="item" items="${question.childNurseItems}">
			<c:choose>
				<c:when test="${!empty nurseResult && nurseResult.objectiveValue eq item.key}">
					<input type="radio" name="nurse_${question.id}" questionId="${question.id}" nurseCheck="true" embededItemId="${item.key}" checked readOnly/>
					${item.value}
				</c:when>
				<c:otherwise>
					<input type="radio" name="nurse_${question.id}" questionId="${question.id}" nurseCheck="true" embededItemId="${item.key}"/>
					${item.value}
				</c:otherwise>
				
			</c:choose>
			
			
		</c:forEach>
	</span>
</div>