<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<div id="body-${loopCount}" class="body-question" questionNo="${question.questionNo}">
	<div class="body-sub-title">${question.title}</div>
	<div class="body-sub-question-content">
		<c:set var="resultItems" value="${resultMap[question.id]}"/>
		<c:forEach var="item" items="${question.childItems}">
			<c:set var="innerItem" value="${resultItems[item.id]}"/>
			<c:if test="${item.active eq true}">	
				<div class="body-check-row">
					<c:choose>
					<c:when test="${empty innerItem}">
						<input questionId="${question.id}" id="${item.id}" type="checkbox" 
								value="${item.id}" existChild="false" itemType="${item.type}" 
								noneFlag="${item.noneFlag}" tabQuestion="false"><a href="javascript:clickCheckboxLink('${item.id}')">${item.title}</a>
						
						<c:if test="${item.type eq 'CHECK_SUBJ_1'}">
							<span>
								<c:if test="${!empty item.preText}">
									${item.preText}
								</c:if>
								<input type=text questionId="${question.id}" itemId="${item.id}" itemType="${item.type}" 
									size=10 validator="${item.validator}" existChild="${item.existChildQuestion}" tabQuestion="${tabQuestion}"
										 minRange="${item.minRange}" maxRange="${item.maxRange}">
								<c:if test="${!empty item.postText}">
									${item.postText}
								</c:if>
							</span>
						</c:if>
					</c:when>
					<c:otherwise>
						<input questionId="${question.id}" id="${item.id}" type="checkbox" 
								value="${item.id}" existChild="false" itemType="${item.type}" 
								noneFlag="${item.noneFlag}" tabQuestion="false" checked><a href="javascript:clickCheckboxLink('${item.id}')">${item.title}</a>
						
						<c:if test="${item.type eq 'CHECK_SUBJ_1'}">
							<span>
								<c:if test="${!empty item.preText}">
									${item.preText}
								</c:if>
								<input type=text questionId="${question.id}" itemId="${item.id}" itemType="${item.type}" 
									size=10 validator="${item.validator}" existChild="${item.existChildQuestion}" tabQuestion="${tabQuestion}"
										 minRange="${item.minRange}" maxRange="${item.maxRange}" value="${innerItem.strValue}">
								<c:if test="${!empty item.postText}">
									${item.postText}
								</c:if>
							</span>
						</c:if>
					</c:otherwise>
					</c:choose>
				</div>
			</c:if>
		</c:forEach>
	</div>
</div>