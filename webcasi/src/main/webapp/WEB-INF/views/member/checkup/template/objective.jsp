<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="question radio">
	<c:if test="${!empty question.description}">
		<div class="question-short-desc">
			${question.help.contents}
		</div>
	</c:if>
	<c:set var="cssPostfix" scope="request">
		<c:choose>
			<c:when test="${questionIndex eq 0 || questionIndex % 2 eq 0}">
				<c:out value="even"/>
			</c:when>
			<c:otherwise>
				<c:out value="odd"/>
			</c:otherwise>
		</c:choose>
	</c:set>
	<div class="question-title-top ${cssPostfix}">
		<div class="question-title-left"></div>
		<div class="question-title-center"></div>
		<div class="question-title-right"></div>
	</div>		
	<div class="question-title ${cssPostfix}" id="question-${question.id}">
		<c:choose>
			<c:when test="${empty questionNo}">
				${question.questionNo}.  ${question.title}
			</c:when>
			<c:otherwise>
				${questionNo}.  ${question.title}
			</c:otherwise>
		</c:choose>	
	</div>
	<div class="question-item-container" >
		<c:set var="resultItems" value="${resultMap[question.id]}"/>
		<c:forEach var="item" items="${question.childItems}">
			<div class="question-item-row obj-row">
				<span class="obj-title">${item.title}(</span>
				<c:set var="innerItem" value="${resultItems[item.id]}"/>
				<c:set var="embededItemResult" value="${innerItem.objectiveValue2}"/>
				<c:forEach var="embededItem" items="${item.childItems}">
					<c:choose>
						<c:when test="${embededItem.key eq embededItemResult}">
							<c:set var="checkedFlag" value="checked"/>
						</c:when>
						<c:otherwise>
							<c:set var="checkedFlag" value=""/>
						</c:otherwise>
					</c:choose>
					<span class="embeded-item">
						<input id="embeded_${item.id}_${embededItem.key}" embededItemId="${embededItem.key}" itemId="${item.id}" questionId="${question.id}" type="radio" name="${item.id}" 
								value="${embededItem.key}" itemType="${item.type}" ${checkedFlag}>
						<a href="javascript:clickRadioLink('embeded_${item.id}_${embededItem.key}');">${embededItem.value}</a>
					</span>
				</c:forEach>
				<span>)</span>
				<!-- 질문에 답변을 했는지 여부를 체크해놓는다. -->
				<c:choose>
					<c:when test="${empty innerItem && question.required}">
						<input type=hidden id="complete_flag_${item.id}" questionId="${question.id}" value="false"/>
						<span class="question-submitted-flag"><a href="javascript:clickCompleted('${item.id}','false');" id="${item.id}_submitted" questionId="${question.id}" existChild="${item.existChildQuestion}"></a></span>
						<c:set var="subjectiveValue" value=""/>
					</c:when>
					<c:otherwise>
						<input type=hidden id="complete_flag_${item.id}" questionId="${question.id}" value="true"/>
						<span class="question-submitted-flag"><a href="javascript:clickCompleted('${item.id}','false');" id="${item.id}_submitted" questionId="${question.id}" existChild="${item.existChildQuestion}">
							<c:if test="${!empty innerItem && item.existChildQuestion}"><img src="${pageContext.request.contextPath}/resources/images/bt_reply.png"/></c:if>
						</a></span>
						<c:set var="subjectiveValue" value="${innerItem.strValue}"/>
					</c:otherwise>
				</c:choose>
				<c:if test="${item.type eq 'OBJ_RADIO_SUBJ'}">
					<div class="question-item-row obj-row sub-row">
						<c:if test="${!empty item.preText}">
							<span class="pretext">${item.preText}</span>
						</c:if>
						<span>
							<input type=text itemId="${item.id}" questionId="${question.id}" itemType="${item.type}" value="${subjectiveValue}" size=40/>
						</span>
						<c:if test="${!empty item.postText}">
							<span class="posttext">${item.postText}</span>
						</c:if>
					</div>
				</c:if>
			</div>
		</c:forEach>
	</div>	
	<div class="question-item-container-bottom">
		<div class="question-item-container-bottom-left"></div>
		<div class="question-item-container-bottom-center"></div>
		<div class="question-item-container-bottom-right"></div>
	</div>
	
	
</div>