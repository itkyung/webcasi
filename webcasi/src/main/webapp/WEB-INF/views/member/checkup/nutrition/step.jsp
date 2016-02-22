<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/page" prefix="page" %>

<div id="question-step">
	<div class="question-step-top">
		<img src="${pageContext.request.contextPath}/resources/images/page_right/rightmenu_top.png"/>
	</div>
	<div class="question-category-name">
		<!--  <span><img src="${pageContext.request.contextPath}/resources/images/thumbnail/rightmenu_icon_01.png" class="text-middle"/></span> -->
		<span>${questionGroup.category.title}</span></div>
	<div class="question-step_body">
		<c:forEach var="questionGroupRow" items="${questionGroups}">
			<c:choose>
				<c:when test="${_currentQuestionGroupId eq questionGroupRow.id}">
					<c:set var="questionGroupRowClass" value="step-row-active"/>
				</c:when>
				<c:otherwise>
					<c:set var="questionGroupRowClass" value=""/>
				</c:otherwise>		
			</c:choose>
			<div class="question-group-row ${questionGroupRowClass}">
				<c:if test="${!empty questionGroupRow.thumbnailImage}">
					<span>
						<img src="${pageContext.request.contextPath}/resources/images/thumbnail/${questionGroupRow.thumbnailImage}" height="15" class="text-middle"/>
					</span>
				</c:if>
				<c:choose>
					<c:when test="${_lastCategoryId eq questionGroupRow.category.id}">
						<c:if test="${_lastQuestionGroupSortOrder >= questionGroupRow.sortOrder}">
							<span class="step-line-through"><a href="javascript:goQuestionGroup('${questionGroupRow.id}',${questionGroupRow.sortOrder})">
						</c:if>
						<c:out value="${questionGroupRow.title}"/>
						<c:if test="${_lastQuestionGroupSortOrder >= questionGroupRow.sortOrder}">
							</a></span>
						</c:if>					
					</c:when>
					<c:when test="${_lastSortOrder < questionGroupRow.category.sortOrder}">
						<c:out value="${questionGroupRow.title}"/>
					</c:when>
					<c:otherwise>
						<span class="step-line-through"><a href="javascript:goQuestionGroup('${questionGroupRow.id}',${questionGroupRow.sortOrder})"><c:out value="${questionGroupRow.title}"/></a></span>
					</c:otherwise>
				</c:choose>
				
			</div>
		</c:forEach>
	</div>
	<div class="question-step-bottom">
		<img src="${pageContext.request.contextPath}/resources/images/page_right/rightmenu_bottom.png"/>
	</div>
</div>