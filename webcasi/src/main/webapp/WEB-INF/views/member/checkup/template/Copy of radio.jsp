<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="question radio">
	<div class="question-title">
		${question.questionNo}. ${question.title}
	</div>
	<div class="question-item-container">
		<c:set var="resultItems" value="${resultMap[question.id]}"/>
		<c:forEach var="item" items="${question.childItems}">
			<div class="question-item-row radio-row">
			<c:choose>
				<c:when test="${empty resultItems}">
					<span><input id="${item.id}" questionId="${question.id}" type="radio" name="${question.id}" value="${item.id}" existChild="${item.existChildQuestion}" itemType="${item.type}">${item.title}</span>
					<span class="question-submitted-flag"><a href="javascript:clickCompleted('${item.id}','${item.existChildQuestion}');" id="${item.id}_submitted" questionId="${question.id}"></a></span>
				</c:when>
				<c:otherwise>
					<c:set var="innerItem" value="${resultItems[item.id]}"/>
					<c:choose>
						<c:when test="${empty innerItem}">
							<span><input id="${item.id}" questionId="${question.id}" type="radio" name="${question.id}" value="${item.id}" existChild="${item.existChildQuestion}" itemType="${item.type}">${item.title}</span>
							<span class="question-submitted-flag"><a href="javascript:clickCompleted('${item.id}','${item.existChildQuestion}');" id="${item.id}_submitted" questionId="${question.id}"></a></span>
						</c:when>
						<c:otherwise>
						<!-- 이미 답변을 선택한 경우임 -->
							<span><input id="${item.id}" questionId="${question.id}" type="radio" name="${question.id}" value="${item.id}" existChild="${item.existChildQuestion}" itemType="${item.type}" checked>${item.title}</span>
							<span class="question-submitted-flag"><a href="javascript:clickCompleted('${item.id}','${item.existChildQuestion}');" id="${item.id}_submitted" questionId="${question.id}">답변완료</a></span>
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
			</div>
		</c:forEach>
		<!-- 질문에 답변을 했는지 여부를 체크해놓는다. -->
		<c:choose>
			<c:when test="${empty resultItems}">
				<input type=hidden id="complete_flag_${question.id}" value="false"/>
			</c:when>
			<c:otherwise>
				<input type=hidden id="complete_flag_${question.id}" value="true"/>
			</c:otherwise>
		</c:choose>
	</div>	
	
	
</div>