<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://myhealth.kbsmc.co.kr/taglibs" prefix="webcasi" %>


<div class="question subjective">
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
	<div class="question-item-container subjective-row" >
		<c:set var="resultValue" value="${resultSubjectiveMap[question.id]}"/>
			
		<c:if test="${!empty question.preText}">
			<span class="pretext">${question.preText}</span>
		</c:if>
		
		<c:set var="hourValue" scope="request">
			<c:out value="${webcasi:parseToAmPm(resultValue.strValue)}"/>
		</c:set>
		
		<span>
			<select id="${question.id}_ampm" name="ampm_${question.id}" questionId="${question.id}">
			<c:choose>
				<c:when test="${webcasi:isAm(resultValue.strValue)}">
					<option value="am" selected>오전</option>
					<option value="pm">오후</option>
				</c:when>
				<c:otherwise>
					<option value="am">오전</option>
					<option value="pm" selected>오후</option>
				</c:otherwise>
			</c:choose>
			</select>
		</span>
		<span>
			<input type=text id="${question.id}_hour_str1" questionId="${question.id}" name="${question.id}_str1" subj1="true" size=5 questionType="${question.type}" 
					validator="HOUR_AM_PM" value="${hourValue}"></span>
		<span>시</span>
		<c:set var="minuteValue" scope="request">
			<c:out value="${resultValue.strValue2}" default="00"/>
		</c:set>
		<span><input type=text questionId="${question.id}" name="${question.id}_str2" subj2="true" size=5 questionType="${question.type}" 
			validator="MINUTE" value="${minuteValue}"></span>
		<span>분</span>
		<c:if test="${!empty question.postText}">
			<span class="posttext">${question.postText}</span>
		</c:if>
		
		<c:choose>
			<c:when test="${empty resultValue && question.required}">
				<span class="question-submitted-flag"><a href="javascript:clickCompleted('${question.id}','false');" id="${question.id}_submitted"></a></span>
				<input type=hidden id="complete_flag_${question.id}" questionId="${question.id}" value="false"/>
			</c:when>
			<c:otherwise>
				<span class="question-submitted-flag"><a href="javascript:clickCompleted('${question.id}','false');" id="${question.id}_submitted">
					<c:if test="${!empty resultValue}"></c:if>
				</a></span>
				
				<input type=hidden id="complete_flag_${question.id}" questionId="${question.id}" value="true"/>
			</c:otherwise>
		</c:choose>
	</div>
	<div class="question-item-container-bottom">
		<div class="question-item-container-bottom-left"></div>
		<div class="question-item-container-bottom-center"></div>
		<div class="question-item-container-bottom-right"></div>
	</div>
	
</div>