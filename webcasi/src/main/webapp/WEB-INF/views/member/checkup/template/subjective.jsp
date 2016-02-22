<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

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
	<c:if test="${empty noTitle}">
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
	</c:if>
	<div class="question-item-container subjective-row" >
		<c:set var="resultValue" value="${resultSubjectiveMap[question.id]}"/>
		<c:choose>
			<c:when test="${question.type eq 'TEXT_AREA'}">
				<span><textarea questionId="${question.id}" id="${question.id}" name="${question.id}" rows="5" cols="60" questionType="${question.type}">${resultValue.strValue}</textarea></span>
			</c:when>
			<c:when test="${question.type eq 'SUBJECTIVE_HOUR_MINUTE' || question.type eq 'SUBJECTIVE_HOUR_MINUTE_RANGE'}">
				<c:if test="${!empty question.preText}">
					<span class="pretext">${question.preText}</span>
				</c:if>
				<c:choose>
					<c:when test="${question.type eq 'SUBJECTIVE_HOUR_MINUTE'}">
						<c:set var="hourValue" scope="request">
							<c:out value="${resultValue.strValue}"/>
						</c:set>
						<span>
							<input type=text questionId="${question.id}" name="${question.id}_str1" subj1="true" size=5 questionType="${question.type}" 
									validator="HOUR" value="${hourValue}"></span>
						<span>시</span>
					</c:when>
					<c:otherwise>
						<span><input type=text questionId="${question.id}" name="${question.id}_str1" subj1="true" size=5 questionType="${question.type}" 
							validator="NUMBER" value="${resultValue.strValue}" minRange="${question.minRange}" maxRange="${question.maxRange}"></span>
						<span>시간</span>					
					</c:otherwise>
				</c:choose>
				
				<c:set var="minuteValue" scope="request">
					<c:out value="${resultValue.strValue2}" default="00"/>
				</c:set>
				<span><input type=text questionId="${question.id}" name="${question.id}_str2" subj2="true" size=5 questionType="${question.type}" 
					validator="MINUTE" value="${minuteValue}"></span>
				<span>분</span>
					
				
				<c:if test="${!empty question.postText}">
					<span class="posttext">${question.postText}</span>
				</c:if>
			</c:when>
			<c:when test="${question.type eq 'SUBJECTIVE_YEAR_MONTH_RANGE'}">
				<c:if test="${!empty question.preText}">
					<span class="pretext">${question.preText}</span>
				</c:if>
				<span><input type=text questionId="${question.id}"  name="${question.id}_str1" subj1="true" size=5 
					questionType="${question.type}" validator="YEAR" value="${resultValue.strValue}"></span>
				<span>년</span>
				<span><input type=text questionId="${question.id}"  name="${question.id}_str2" subj2="true" size=5 
					questionType="${question.type}" validator="MONTH" value="${resultValue.strValue2}"></span>
				<span>개월</span>
				<c:if test="${!empty question.postText}">
					<span class="posttext">${question.postText}</span>
				</c:if>
			</c:when>
			<c:when test="${question.type eq 'SUBJECTIVE_MONTH_DATE_RANGE'}">
				<c:if test="${!empty question.preText}">
					<span class="pretext">${question.preText}</span>
				</c:if>
				<span><input type=text questionId="${question.id}"  name="${question.id}_str1" subj1="true" size=5 
					questionType="${question.type}" validator="NUMBER" value="${resultValue.strValue}"></span>
				<span>개월</span>
				<span><input type=text questionId="${question.id}"  name="${question.id}_str2" subj2="true" size=5 
					questionType="${question.type}" validator="NUMBER" value="${resultValue.strValue2}"></span>
				<span>일</span>
				<c:if test="${!empty question.postText}">
					<span class="posttext">${question.postText}</span>
				</c:if>
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${question.validator eq 'NONE'}">
						<c:set var="inputSize" value="50"/>
					</c:when>
					<c:otherwise>
						<c:set var="inputSize" value="8"/>
					</c:otherwise>
				</c:choose>
				<c:if test="${!empty question.preText}">
					<span class="pretext">${question.preText}</span>
				</c:if>
				
				<span><input type=text id="${question.id}" questionId="${question.id}"  name="${question.id}" 
					size=${inputSize} questionType="${question.type}" validator="${question.validator}"  minRange="${question.minRange}" maxRange="${question.maxRange}" 
					value="${resultValue.strValue}"/></span>
		
				<c:if test="${!empty question.postText}">
					<span class="posttext">${question.postText}</span>
				</c:if>
			</c:otherwise>
		</c:choose>
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