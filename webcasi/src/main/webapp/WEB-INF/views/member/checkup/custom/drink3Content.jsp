<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script>
	
	
</script>

<div id="tab${loopCount}" class="small-tab-content">
	<div class="sub-tab-title"><span class="sub-title-span">${tabItem.itemTitle}</span></div>	
	
	
	<c:if test="${!empty tabItem.frequencyItems}">
		<c:set var="frequencyResult" value="${frequencyResultMap[tabItem.questionId]}"/>
		<div class="nutrition-second-question">
			<div class="sub-question-title">
				<img src="${pageContext.request.contextPath}/resources/images/depth_icon4.png" class="text-middle"/>
				<span>최근 1년간 섭취한 평균 음주 빈도는?</span>
			</div>
			<c:forEach var="sitem" items="${tabItem.frequencyItems}" varStatus="status">
				<c:choose>
				<c:when test="${sitem.id eq frequencyResult}">
					<div class="nutrition-radio">
						<input type="radio" id="${sitem.id}" itemId="${sitem.id}" questionId="${tabItem.questionId}" 
							name="frequency_${loopCount}" itemGroup="FREQUENCY" itemSortOrder="${status.index}" 
							value="${sitem.id}" 
							itemTotalCount="${tabItem.frequencyItemCount}" checked/><a href="javascript:clickRadioLink('${sitem.id}')">${sitem.title}</a>
					</div>
				</c:when>
				<c:otherwise>
					<div class="nutrition-radio">
						<input type="radio" id="${sitem.id}" itemId="${sitem.id}" questionId="${tabItem.questionId}" 
							name="frequency_${loopCount}" itemGroup="FREQUENCY" itemSortOrder="${status.index}" 
							value="${sitem.id}" 
							itemTotalCount="${tabItem.frequencyItemCount}"/><a href="javascript:clickRadioLink('${sitem.id}')">${sitem.title}</a>
					</div>
					
				</c:otherwise>
				</c:choose>
			</c:forEach>
		</div>
		<c:if test="${!empty tabItem.quantityItems}">
		<c:set var="quantityResult" value="${quantityResultMap[tabItem.questionId]}"/>
		<div class="nutrition-second-question">
			<div class="sub-question-title">
				<img src="${pageContext.request.contextPath}/resources/images/depth_icon4.png"  class="text-middle"/><span>음주 1회당 알코올 섭취량은?</span>
				<c:forEach var="sitem" items="${tabItem.quantityItems}" varStatus="status">
				<span>
					<input type=text id="${sitem.id}" questionId="${tabItem.questionId}"  name="${sitem.id}" size=10  questionType="${sitem.type}" 
						validator="${sitem.validator}" minRange="${sitem.minRange}" maxRange="${sitem.maxRange}" value="${quantityResult}" itemGroup="QUANTITY" 
						loopCount="${loopCount}" />  잔
				</span>
				</c:forEach>
			</div>
		</div>
		<c:choose>
			<c:when test="${empty quantityResult}">
				<input type=hidden id="complete_flag_${tabItem.questionId}_QUANTITY" questionId="${tabItem.questionId}" value="false"/>
			</c:when>
			<c:otherwise>
				<input type=hidden id="complete_flag_${tabItem.questionId}_QUANTITY" questionId="${tabItem.questionId}" value="true"/>
			</c:otherwise>
		</c:choose>
	</c:if>
		<c:choose>
			<c:when test="${empty frequencyResult}">
				<input type=hidden id="complete_flag_${tabItem.questionId}_FREQUENCY" questionId="${tabItem.questionId}" value="false"/>
			</c:when>
			<c:otherwise>
				<input type=hidden id="complete_flag_${tabItem.questionId}_FREQUENCY" questionId="${tabItem.questionId}" value="true"/>
			</c:otherwise>
		</c:choose>
		
		
	</c:if>
	
	
</div>